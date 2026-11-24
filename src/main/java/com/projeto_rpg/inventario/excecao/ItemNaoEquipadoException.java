package com.projeto_rpg.inventario.excecao;

/**
 * O item não está equipado no inventário.
 */
public class ItemNaoEquipadoException extends IllegalStateException {

    public ItemNaoEquipadoException(String nome) {
        super("Item '" + nome + "' não está equipado");
    }
}
