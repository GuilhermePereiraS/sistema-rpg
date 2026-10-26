package com.projeto_rpg.regras.dados;

/**
 * Um grupo de dados iguais dentro de uma expressão, como "2d6".
 */
public record TermoDeDado(int quantidade, int faces) {

    public static TermoDeDado simples(int quantidade, int faces) {
        return new TermoDeDado(quantidade, faces);
    }
}
