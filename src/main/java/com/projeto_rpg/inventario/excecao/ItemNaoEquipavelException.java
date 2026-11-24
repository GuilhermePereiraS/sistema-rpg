package com.projeto_rpg.inventario.excecao;

/**
 * O item não pode ser equipado (não é arma, armadura nem escudo).
 */
public class ItemNaoEquipavelException extends IllegalStateException {

    public ItemNaoEquipavelException(String nome) {
        super("Item '" + nome + "' não pode ser equipado");
    }
}
