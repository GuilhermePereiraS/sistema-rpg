package com.projeto_rpg.ficha.regra;

import com.projeto_rpg.ficha.excecao.CompraDePontosInvalidaException;
import com.projeto_rpg.ficha.modelo.Atributo;
import com.projeto_rpg.ficha.modelo.Atributos;

/**
 * Regras da compra de pontos de atributo.
 */
public final class CompraDePontos {

    public static final int VALOR_MINIMO = 8;
    public static final int VALOR_MAXIMO = 15;
    public static final int PONTOS_DISPONIVEIS = 27;

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

    /**
     * Soma do custo dos seis atributos.
     *
     * @throws CompraDePontosInvalidaException se algum valor estiver fora de 8 a 15
     */
    public static int custoTotal(Atributos atributos) {
        int total = 0;
        for (Atributo atributo : Atributo.values()) {
            total += custo(atributos.valor(atributo));
        }
        return total;
    }

    /**
     * Pontos que ainda sobram dos 27 disponíveis.
     */
    public static int pontosRestantes(Atributos atributos) {
        return PONTOS_DISPONIVEIS - custoTotal(atributos);
    }

    /**
     * Confere se a distribuição respeita a compra de pontos: cada valor entre 8 e 15
     * e no máximo 27 pontos gastos.
     *
     * @throws CompraDePontosInvalidaException se a distribuição for nula ou inválida
     */
    public static void validar(Atributos atributos) {
        if (atributos == null) {
            throw new CompraDePontosInvalidaException("Atributos não podem ser nulos");
        }
        int gasto = custoTotal(atributos);
        if (gasto > PONTOS_DISPONIVEIS) {
            throw new CompraDePontosInvalidaException("Distribuição gasta " + gasto
                    + " pontos, mas só há " + PONTOS_DISPONIVEIS + " disponíveis");
        }
    }
}
