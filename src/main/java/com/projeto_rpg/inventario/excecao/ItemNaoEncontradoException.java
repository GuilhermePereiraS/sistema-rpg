package com.projeto_rpg.inventario.excecao;

/**
 * Não existe item com o id informado.
 */
public class ItemNaoEncontradoException extends RuntimeException {

    public ItemNaoEncontradoException(Long id) {
        super("Item " + id + " não encontrado");
    }
}
