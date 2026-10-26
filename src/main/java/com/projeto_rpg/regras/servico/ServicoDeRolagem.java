package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.dados.ParserDeExpressao;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.historico.TipoRolagem;
import com.projeto_rpg.regras.mecanica.ModoDeRolagem;
import com.projeto_rpg.regras.mecanica.ResultadoTeste;
import com.projeto_rpg.regras.mecanica.RoladorDeTeste;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Rolagens livres e testes de um personagem, sempre registrados no histórico.
 */
@Service
@Transactional
public class ServicoDeRolagem {

    private final ParserDeExpressao parser;
    private final RoladorDeDados roladorDeDados;
    private final RoladorDeTeste roladorDeTeste;
    private final PerfilDeRegras perfil;
    private final ServicoDeHistorico historico;

    public ServicoDeRolagem(ParserDeExpressao parser, RoladorDeDados roladorDeDados, RoladorDeTeste roladorDeTeste,
                            PerfilDeRegras perfil, ServicoDeHistorico historico) {
        this.parser = parser;
        this.roladorDeDados = roladorDeDados;
        this.roladorDeTeste = roladorDeTeste;
        this.perfil = perfil;
        this.historico = historico;
    }

    public ResultadoRolagem rolar(Long personagemId, String expressao) {
        exigirPersonagem(personagemId);
        ResultadoRolagem resultado = roladorDeDados.rolar(parser.interpretar(expressao));
        historico.registrar(personagemId, TipoRolagem.LIVRE, expressao.trim(), resultado.dados(), resultado.total());
        return resultado;
    }

    public ResultadoTeste testar(Long personagemId, int modificador, int alvo, ModoDeRolagem modo) {
        exigirPersonagem(personagemId);
        ResultadoTeste resultado = roladorDeTeste.testar(modificador, alvo, modo);
        String descricao = DescricaoDeRolagem.teste(perfil.facesDoDadoDeTeste(), modificador, alvo, modo);
        historico.registrar(personagemId, TipoRolagem.TESTE, descricao, resultado.dados(), resultado.total());
        return resultado;
    }

    private static void exigirPersonagem(Long personagemId) {
        if (personagemId == null) {
            throw new IllegalArgumentException("Personagem não pode ser nulo");
        }
    }
}
