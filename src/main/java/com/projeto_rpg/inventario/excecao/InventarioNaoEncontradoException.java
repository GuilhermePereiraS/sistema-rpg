package com.projeto_rpg.inventario.excecao;

/**
 * Não existe inventário para o personagem informado.
 */
public class InventarioNaoEncontradoException extends RuntimeException {

    public InventarioNaoEncontradoException(Long personagemId) {
        super("Inventário do personagem " + personagemId + " não encontrado");
    }
}
