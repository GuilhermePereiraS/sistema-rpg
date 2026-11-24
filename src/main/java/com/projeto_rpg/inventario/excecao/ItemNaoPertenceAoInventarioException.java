package com.projeto_rpg.inventario.excecao;

/**
 * O item informado não está guardado no inventário.
 */
public class ItemNaoPertenceAoInventarioException extends IllegalStateException {

    public ItemNaoPertenceAoInventarioException(String nome) {
        super("Item '" + nome + "' não pertence ao inventário");
    }
}
