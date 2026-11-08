package com.projeto_rpg.ficha.excecao;

/**
 * Já existe um registro com o mesmo nome (a comparação ignora maiúsculas e minúsculas).
 */
public class NomeJaCadastradoException extends IllegalStateException {

    public NomeJaCadastradoException(String tipo, String nome) {
        super(tipo + " '" + nome + "' já cadastrada");
    }
}
