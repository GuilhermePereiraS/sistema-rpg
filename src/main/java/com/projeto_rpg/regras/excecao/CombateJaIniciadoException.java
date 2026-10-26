package com.projeto_rpg.regras.excecao;

/**
 * O personagem já possui um estado de combate.
 */
public class CombateJaIniciadoException extends IllegalStateException {

    public CombateJaIniciadoException(Long personagemId) {
        super("Personagem " + personagemId + " já está em combate");
    }
}
