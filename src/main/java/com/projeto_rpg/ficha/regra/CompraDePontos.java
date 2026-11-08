package com.projeto_rpg.ficha.regra;

import com.projeto_rpg.ficha.excecao.CompraDePontosInvalidaException;

/**
 * Regras da compra de pontos de atributo.
 */
public final class CompraDePontos {

    public static final int VALOR_MINIMO = 8;
    public static final int VALOR_MAXIMO = 15;

    private CompraDePontos() {
    }

    /**
     * Custo, em pontos, de um valor de atributo.
     *
     * @throws CompraDePontosInvalidaException se o valor estiver fora de 8 a 15
     */
    public static int custo(int valor) {
        if (valor < VALOR_MINIMO || valor > VALOR_MAXIMO) {
            throw new CompraDePontosInvalidaException("Valor " + valor + " fora da faixa de compra ("
                    + VALOR_MINIMO + " a " + VALOR_MAXIMO + ")");
        }
        if (valor <= 13) {
            return valor - VALOR_MINIMO;
        }
        return (valor - VALOR_MINIMO) + (valor - 13);
    }
}
