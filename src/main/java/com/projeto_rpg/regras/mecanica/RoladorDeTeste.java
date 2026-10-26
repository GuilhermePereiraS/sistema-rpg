package com.projeto_rpg.regras.mecanica;

import com.projeto_rpg.regras.dados.GeradorAleatorio;
import com.projeto_rpg.regras.perfil.CriterioDeSucesso;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;

import java.util.List;
import java.util.Objects;

/**
 * Faz testes com o dado do perfil, aplicando vantagem/desvantagem e o critério de sucesso.
 */
public class RoladorDeTeste {

    private final GeradorAleatorio gerador;
    private final PerfilDeRegras perfil;

    public RoladorDeTeste(GeradorAleatorio gerador, PerfilDeRegras perfil) {
        this.gerador = Objects.requireNonNull(gerador, "Gerador aleatório não pode ser nulo");
        this.perfil = Objects.requireNonNull(perfil, "Perfil de regras não pode ser nulo");
    }

    public ResultadoTeste testar(int modificador, int alvo, ModoDeRolagem modo) {
        Objects.requireNonNull(modo, "Modo de rolagem não pode ser nulo");
        List<Integer> dados = rolarDadosDeTeste(modo);
        int dadoNatural = escolherDado(dados, modo);
        int total = dadoNatural + modificador;
        return new ResultadoTeste(dados, dadoNatural, modificador, total, alvo,
                perfil.criterioDeSucesso().foiBemSucedido(total, alvo),
                perfil.regraDeCritico().eCritico(dadoNatural),
                perfil.regraDeCritico().eFalhaCritica(dadoNatural));
    }

    private List<Integer> rolarDadosDeTeste(ModoDeRolagem modo) {
        int faces = perfil.facesDoDadoDeTeste();
        if (modo == ModoDeRolagem.NORMAL) {
            return List.of(gerador.rolar(faces));
        }
        return List.of(gerador.rolar(faces), gerador.rolar(faces));
    }

    private int escolherDado(List<Integer> dados, ModoDeRolagem modo) {
        CriterioDeSucesso criterio = perfil.criterioDeSucesso();
        return switch (modo) {
            case NORMAL -> dados.getFirst();
            case VANTAGEM -> criterio.melhorDado(dados.get(0), dados.get(1));
            case DESVANTAGEM -> criterio.piorDado(dados.get(0), dados.get(1));
        };
    }
}
