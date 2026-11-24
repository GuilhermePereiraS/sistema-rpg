package com.projeto_rpg.inventario.excecao;

/**
 * Já existe uma habilidade com o mesmo nome (a comparação ignora maiúsculas e minúsculas).
 */
public class HabilidadeJaCadastradaException extends IllegalStateException {

    public HabilidadeJaCadastradaException(String nome) {
        super("Habilidade '" + nome + "' já cadastrada");
    }
}
