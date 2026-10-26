package com.projeto_rpg.regras.excecao;

/**
 * Operação impossível porque o personagem está morto.
 */
public class PersonagemMortoException extends IllegalStateException {

    public PersonagemMortoException(Long personagemId) {
        super("Personagem " + personagemId + " está morto");
    }
}
