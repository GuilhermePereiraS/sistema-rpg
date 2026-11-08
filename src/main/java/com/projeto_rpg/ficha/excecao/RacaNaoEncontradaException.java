package com.projeto_rpg.ficha.excecao;

/**
 * Não existe raça com o id informado.
 */
public class RacaNaoEncontradaException extends RuntimeException {

    public RacaNaoEncontradaException(Long id) {
        super("Raça " + id + " não encontrada");
    }
}
