package com.projeto_rpg.inventario.regra;

import com.projeto_rpg.inventario.modelo.Item;
import com.projeto_rpg.inventario.modelo.TipoDeItem;

/**
 * Cálculo da CA (Classe de Armadura) final.
 *
 * <p>Sem armadura: {@code 10 + Destreza}. Armadura leve: base + Destreza integral.
 * Armadura média: base + Destreza (no máximo +2). Armadura pesada: apenas a base.
 * Um escudo equipado soma +2 em qualquer caso.
 */
public final class CalculadoraDeCa {

    public static final int CA_SEM_ARMADURA = 10;
    public static final int BONUS_DE_ESCUDO = 2;

    private CalculadoraDeCa() {
    }

    public static int caFinal(int modificadorDeDestreza, Item armadura, Item escudo) {
        validarArmadura(armadura);
        validarEscudo(escudo);
        int base = armadura == null ? CA_SEM_ARMADURA : armadura.getCaBase();
        int destreza = armadura == null
                ? modificadorDeDestreza
                : armadura.getTipoDeArmadura().aplicarDestreza(modificadorDeDestreza);
        int ca = base + destreza;
        if (escudo != null) {
            ca += BONUS_DE_ESCUDO;
        }
        return ca;
    }

    private static void validarArmadura(Item armadura) {
        if (armadura != null && armadura.getTipo() != TipoDeItem.ARMADURA) {
            throw new IllegalArgumentException("O item equipado como armadura deve ser do tipo ARMADURA");
        }
    }

    private static void validarEscudo(Item escudo) {
        if (escudo != null && escudo.getTipo() != TipoDeItem.ESCUDO) {
            throw new IllegalArgumentException("O item equipado como escudo deve ser do tipo ESCUDO");
        }
    }
}
