package com.projeto_rpg.ficha.excecao;

/**
 * A classe não pode ser removida porque há personagens que a utilizam.
 */
public class ClasseEmUsoException extends IllegalStateException {

    public ClasseEmUsoException(Long id) {
        super("Classe " + id + " está em uso por personagens e não pode ser removida");
    }
}
