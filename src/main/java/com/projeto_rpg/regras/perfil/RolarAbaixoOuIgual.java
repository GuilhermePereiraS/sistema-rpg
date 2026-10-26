package com.projeto_rpg.regras.perfil;

/**
 * Sucesso quando o total é menor ou igual ao alvo (Call of Cthulhu, GURPS). Dado menor é melhor.
 */
public record RolarAbaixoOuIgual() implements CriterioDeSucesso {

    @Override
    public boolean foiBemSucedido(int total, int alvo) {
        return total <= alvo;
    }

    @Override
    public int melhorDado(int primeiro, int segundo) {
        return Math.min(primeiro, segundo);
    }
}
