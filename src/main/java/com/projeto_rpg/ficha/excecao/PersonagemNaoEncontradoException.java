package com.projeto_rpg.ficha.excecao;

/**
 * Não existe personagem com o id informado.
 */
public class PersonagemNaoEncontradoException extends RuntimeException {

    public PersonagemNaoEncontradoException(Long id) {
        super("Personagem " + id + " não encontrado");
    }
}
