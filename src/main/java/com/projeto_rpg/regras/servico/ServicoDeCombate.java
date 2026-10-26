package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.combate.EstadoDeCombate;
import com.projeto_rpg.regras.combate.EstadoDeCombateRepository;
import com.projeto_rpg.regras.combate.ResultadoTesteContraMorte;
import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.GeradorAleatorio;
import com.projeto_rpg.regras.dados.ParserDeExpressao;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.excecao.CombateJaIniciadoException;
import com.projeto_rpg.regras.excecao.EstadoDeCombateNaoEncontradoException;
import com.projeto_rpg.regras.historico.TipoRolagem;
import com.projeto_rpg.regras.mecanica.ModoDeRolagem;
import com.projeto_rpg.regras.mecanica.ResolvedorDeAtaque;
import com.projeto_rpg.regras.mecanica.ResultadoAtaque;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso de combate: iniciar, atacar, dano, cura, recuperação e testes contra a morte.
 * O estado é carregado e salvo a cada chamada, então os testes contra a morte
 * continuam de onde pararam.
 */
@Service
@Transactional
public class ServicoDeCombate {

    private final EstadoDeCombateRepository repository;
    private final ResolvedorDeAtaque resolvedorDeAtaque;
    private final RoladorDeDados roladorDeDados;
    private final ParserDeExpressao parser;
    private final GeradorAleatorio gerador;
    private final PerfilDeRegras perfil;
    private final ServicoDeHistorico historico;

    public ServicoDeCombate(EstadoDeCombateRepository repository, ResolvedorDeAtaque resolvedorDeAtaque,
                            RoladorDeDados roladorDeDados, ParserDeExpressao parser, GeradorAleatorio gerador,
                            PerfilDeRegras perfil, ServicoDeHistorico historico) {
        this.repository = repository;
        this.resolvedorDeAtaque = resolvedorDeAtaque;
        this.roladorDeDados = roladorDeDados;
        this.parser = parser;
        this.gerador = gerador;
        this.perfil = perfil;
        this.historico = historico;
    }

    public EstadoDeCombate iniciarCombate(Long personagemId, int pvMaximo) {
        if (repository.existsByPersonagemId(personagemId)) {
            throw new CombateJaIniciadoException(personagemId);
        }
        return repository.save(new EstadoDeCombate(personagemId, pvMaximo));
    }

    @Transactional(readOnly = true)
    public EstadoDeCombate consultarEstado(Long personagemId) {
        return buscar(personagemId);
    }

    public ResultadoAtaque atacar(Long atacanteId, Long alvoId, int bonusDeAtaque, int defesaDoAlvo,
                                  String expressaoDeDano, ModoDeRolagem modo) {
        EstadoDeCombate alvo = buscar(alvoId);
        ExpressaoDeDado dano = parser.interpretar(expressaoDeDano);
        ResultadoAtaque resultado = resolvedorDeAtaque.atacar(bonusDeAtaque, defesaDoAlvo, dano, modo);
        registrarAtaque(atacanteId, bonusDeAtaque, defesaDoAlvo, modo, resultado);
        if (resultado.acertou()) {
            registrarDano(atacanteId, expressaoDeDano, resultado);
            alvo.sofrerDano(resultado.dano(), resultado.critico(), perfil.regraDeMorte());
        }
        return resultado;
    }

    private void registrarAtaque(Long atacanteId, int bonusDeAtaque, int defesaDoAlvo, ModoDeRolagem modo,
                                 ResultadoAtaque resultado) {
        String descricao = DescricaoDeRolagem.teste(perfil.facesDoDadoDeTeste(), bonusDeAtaque, defesaDoAlvo, modo);
        historico.registrar(atacanteId, TipoRolagem.ATAQUE, descricao, resultado.teste().dados(),
                resultado.teste().total());
    }

    private void registrarDano(Long atacanteId, String expressaoDeDano, ResultadoAtaque resultado) {
        historico.registrar(atacanteId, TipoRolagem.DANO, DescricaoDeRolagem.dano(expressaoDeDano, resultado.critico()),
                resultado.dadosDeDano(), resultado.dano());
    }

    public EstadoDeCombate aplicarDano(Long personagemId, int dano, boolean critico) {
        EstadoDeCombate estado = buscar(personagemId);
        estado.sofrerDano(dano, critico, perfil.regraDeMorte());
        return estado;
    }

    public EstadoDeCombate curar(Long personagemId, int cura) {
        EstadoDeCombate estado = buscar(personagemId);
        estado.receberCura(cura);
        return estado;
    }

    public EstadoDeCombate curarComRolagem(Long personagemId, String expressaoDeCura) {
        EstadoDeCombate estado = buscar(personagemId);
        ResultadoRolagem cura = roladorDeDados.rolar(parser.interpretar(expressaoDeCura));
        historico.registrar(personagemId, TipoRolagem.CURA, expressaoDeCura.trim(), cura.dados(), cura.total());
        if (cura.total() > 0) {
            estado.receberCura(cura.total());
        }
        return estado;
    }

    public EstadoDeCombate recuperarTotalmente(Long personagemId) {
        EstadoDeCombate estado = buscar(personagemId);
        estado.recuperarTotalmente();
        return estado;
    }

    public ResultadoTesteContraMorte realizarTesteContraMorte(Long personagemId) {
        EstadoDeCombate estado = buscar(personagemId);
        int faces = perfil.facesDoDadoDeTeste();
        int dado = gerador.rolar(faces);
        ResultadoTesteContraMorte resultado = estado.realizarTesteContraMorte(dado, perfil.regraDeMorte());
        historico.registrar(personagemId, TipoRolagem.TESTE_CONTRA_MORTE, DescricaoDeRolagem.testeContraMorte(faces),
                List.of(dado), dado);
        return resultado;
    }

    private EstadoDeCombate buscar(Long personagemId) {
        return repository.findByPersonagemId(personagemId)
                .orElseThrow(() -> new EstadoDeCombateNaoEncontradoException(personagemId));
    }
}
