package com.projeto_rpg.ficha.excecao;

/**
 * Não existe classe com o id informado.
 */
public class ClasseNaoEncontradaException extends RuntimeException {

    public ClasseNaoEncontradaException(Long id) {
        super("Classe " + id + " não encontrada");
    }
}
