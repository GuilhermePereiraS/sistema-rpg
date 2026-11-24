package com.projeto_rpg.inventario.modelo;

/**
 * Categoria da armadura, que define quanto do modificador de Destreza entra na CA.
 */
public enum TipoDeArmadura {

    /** Soma o modificador de Destreza integralmente. */
    LEVE,
    /** Soma o modificador de Destreza, limitado a +2. */
    MEDIA,
    /** Ignora o modificador de Destreza (usa apenas a CA base). */
    PESADA;

    /**
     * Aplica o modificador de Destreza conforme a categoria da armadura.
     */
    public int aplicarDestreza(int modificadorDeDestreza) {
        return switch (this) {
            case LEVE -> modificadorDeDestreza;
            case MEDIA -> Math.min(modificadorDeDestreza, 2);
            case PESADA -> 0;
        };
    }
}
