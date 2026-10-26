package com.projeto_rpg.regras.dados;

/**
 * Um grupo de dados iguais dentro de uma expressão, como "2d6" ou "4d6kh3".
 *
 * @param quantidade quantos dados são rolados
 * @param faces      número de faces de cada dado
 * @param selecao    quais dados entram na soma
 * @param mantidos   quantos dados entram na soma
 */
public record TermoDeDado(int quantidade, int faces, Selecao selecao, int mantidos) {

    public static TermoDeDado simples(int quantidade, int faces) {
        return new TermoDeDado(quantidade, faces, Selecao.TODOS, quantidade);
    }

    public static TermoDeDado mantendoMaiores(int quantidade, int faces, int mantidos) {
        return new TermoDeDado(quantidade, faces, Selecao.MAIORES, mantidos);
    }

    public static TermoDeDado mantendoMenores(int quantidade, int faces, int mantidos) {
        return new TermoDeDado(quantidade, faces, Selecao.MENORES, mantidos);
    }
}
