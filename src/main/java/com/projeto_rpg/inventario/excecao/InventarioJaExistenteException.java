package com.projeto_rpg.inventario.excecao;

/**
 * O personagem já possui um inventário.
 */
public class InventarioJaExistenteException extends IllegalStateException {

    public InventarioJaExistenteException(Long personagemId) {
        super("Personagem " + personagemId + " já possui um inventário");
    }
}
