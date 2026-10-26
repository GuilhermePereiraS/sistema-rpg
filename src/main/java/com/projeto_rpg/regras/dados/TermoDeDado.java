package com.projeto_rpg.regras.dados;

import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;

import java.util.Objects;

/**
 * Um grupo de dados iguais dentro de uma expressão, como "2d6" ou "4d6kh3".
 *
 * @param quantidade quantos dados são rolados (mínimo 1)
 * @param faces      número de faces de cada dado (mínimo 2)
 * @param selecao    quais dados entram na soma
 * @param mantidos   quantos dados entram na soma (entre 1 e a quantidade)
 */
public record TermoDeDado(int quantidade, int faces, Selecao selecao, int mantidos) {

    public TermoDeDado {
        Objects.requireNonNull(selecao, "Seleção não pode ser nula");
        if (quantidade < 1) {
            throw new ExpressaoInvalidaException("Quantidade de dados deve ser ao menos 1");
        }
        if (faces < 2) {
            throw new ExpressaoInvalidaException("Dado deve ter ao menos 2 faces");
        }
        if (mantidos < 1 || mantidos > quantidade) {
            throw new ExpressaoInvalidaException("Dados mantidos devem estar entre 1 e a quantidade rolada");
        }
    }

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
