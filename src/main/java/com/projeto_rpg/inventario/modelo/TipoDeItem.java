package com.projeto_rpg.inventario.modelo;

/**
 * Natureza de um item do catálogo.
 *
 * <p>Determina em qual espaço o item é equipado e se ele participa do cálculo de CA.
 */
public enum TipoDeItem {

    ARMA,
    ARMADURA,
    ESCUDO,
    CONSUMIVEL,
    GERAL;

    /**
     * Um item equipável ocupa um dos espaços de equipamento do inventário.
     */
    public boolean equipavel() {
        return this == ARMA || this == ARMADURA || this == ESCUDO;
    }
}
