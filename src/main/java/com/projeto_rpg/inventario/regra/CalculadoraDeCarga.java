package com.projeto_rpg.inventario.regra;

/**
 * Cálculo da capacidade de carga a partir da Força.
 *
 * <p>Adota a regra de 15 libras por ponto de Força.
 */
public final class CalculadoraDeCarga {

    public static final int PESO_POR_PONTO_DE_FORCA = 15;

    private CalculadoraDeCarga() {
    }

    /**
     * @throws IllegalArgumentException se a Força for negativa
     */
    public static int capacidadeDeCarga(int forca) {
        if (forca < 0) {
            throw new IllegalArgumentException("Força não pode ser negativa");
        }
        return forca * PESO_POR_PONTO_DE_FORCA;
    }
}
