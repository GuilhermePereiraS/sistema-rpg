package com.projeto_rpg.ficha.regra;

/**
 * Cálculo de PV máximo.
 *
 * <p>No nível 1 vale o dado de vida cheio; nos níveis seguintes, a média fixa do dado
 * (dado / 2 + 1). Em todo nível se soma o modificador de Constituição, com ganho mínimo de 1 PV.
 */
public final class CalculadoraDePv {

    private CalculadoraDePv() {
    }

    public static int pvMaximo(int dadoDeVida, int nivel, int modificadorDeConstituicao) {
        int pvNivelUm = Math.max(1, dadoDeVida + modificadorDeConstituicao);
        int pvPorNivel = Math.max(1, dadoDeVida / 2 + 1 + modificadorDeConstituicao);
        return pvNivelUm + (nivel - 1) * pvPorNivel;
    }
}
