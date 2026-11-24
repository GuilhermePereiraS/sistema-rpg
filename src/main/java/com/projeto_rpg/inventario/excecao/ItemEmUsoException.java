package com.projeto_rpg.inventario.excecao;

/**
 * O item não pode ser removido porque está guardado em algum inventário.
 */
public class ItemEmUsoException extends IllegalStateException {

    public ItemEmUsoException(Long id) {
        super("Item " + id + " está em uso por um inventário e não pode ser removido");
    }
}
