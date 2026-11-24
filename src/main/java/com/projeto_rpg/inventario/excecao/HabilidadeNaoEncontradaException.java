package com.projeto_rpg.inventario.excecao;

/**
 * Não existe habilidade com o id informado.
 */
public class HabilidadeNaoEncontradaException extends RuntimeException {

    public HabilidadeNaoEncontradaException(Long id) {
        super("Habilidade " + id + " não encontrada");
    }
}
