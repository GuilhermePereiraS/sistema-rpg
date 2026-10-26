package com.projeto_rpg.regras.excecao;

/**
 * A expressão de dados informada não segue a notação aceita ou viola os limites.
 */
public class ExpressaoInvalidaException extends IllegalArgumentException {

    public ExpressaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
