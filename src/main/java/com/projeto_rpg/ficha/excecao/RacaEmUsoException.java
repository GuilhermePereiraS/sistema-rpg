package com.projeto_rpg.ficha.excecao;

/**
 * A raça não pode ser removida porque há personagens que a utilizam.
 */
public class RacaEmUsoException extends IllegalStateException {

    public RacaEmUsoException(Long id) {
        super("Raça " + id + " está em uso por personagens e não pode ser removida");
    }
}
