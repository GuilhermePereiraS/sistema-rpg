package com.projeto_rpg.regras.perfil;

/**
 * Sucesso quando o total é maior ou igual ao alvo (D&D, Pathfinder). Dado maior é melhor.
 */
public record RolarAcimaOuIgual() implements CriterioDeSucesso {

    @Override
    public boolean foiBemSucedido(int total, int alvo) {
        return total >= alvo;
    }

    @Override
    public int melhorDado(int primeiro, int segundo) {
        return Math.max(primeiro, segundo);
    }
}
