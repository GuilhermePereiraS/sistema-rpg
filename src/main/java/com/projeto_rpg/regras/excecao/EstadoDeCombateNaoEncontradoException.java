package com.projeto_rpg.regras.excecao;

/**
 * O personagem não possui estado de combate (combate não iniciado).
 */
public class EstadoDeCombateNaoEncontradoException extends RuntimeException {

    public EstadoDeCombateNaoEncontradoException(Long personagemId) {
        super("Personagem " + personagemId + " não está em combate");
    }
}
