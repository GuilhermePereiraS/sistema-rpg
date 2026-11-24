package com.projeto_rpg.inventario.excecao;

/**
 * Já existe um item com o mesmo nome (a comparação ignora maiúsculas e minúsculas).
 */
public class ItemJaCadastradoException extends IllegalStateException {

    public ItemJaCadastradoException(String nome) {
        super("Item '" + nome + "' já cadastrado");
    }
}
