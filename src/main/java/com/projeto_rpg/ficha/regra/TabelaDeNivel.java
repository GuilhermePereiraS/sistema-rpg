package com.projeto_rpg.ficha.regra;

/**
 * Tabela de progressão: XP necessário para cada nível.
 */
public final class TabelaDeNivel {

    public static final int NIVEL_MINIMO = 1;
    public static final int NIVEL_MAXIMO = 20;

    private static final int[] XP_MINIMO = {
            0, 300, 900, 2_700, 6_500, 14_000, 23_000, 34_000, 48_000, 64_000,
            85_000, 100_000, 120_000, 140_000, 165_000, 195_000, 225_000, 265_000, 305_000, 355_000
    };

    private TabelaDeNivel() {
    }

    /**
     * Nível correspondente à quantidade de XP (no máximo 20).
     *
     * @throws IllegalArgumentException se o XP for negativo
     */
    public static int nivelPara(int xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("XP não pode ser negativo");
        }
        int nivel = NIVEL_MINIMO;
        while (nivel < NIVEL_MAXIMO && xp >= XP_MINIMO[nivel]) {
            nivel++;
        }
        return nivel;
    }

    /**
     * XP mínimo para alcançar o nível informado.
     *
     * @throws IllegalArgumentException se o nível estiver fora de 1 a 20
     */
    public static int xpMinimoDoNivel(int nivel) {
        validarNivel(nivel);
        return XP_MINIMO[nivel - 1];
    }

    private static void validarNivel(int nivel) {
        if (nivel < NIVEL_MINIMO || nivel > NIVEL_MAXIMO) {
            throw new IllegalArgumentException("Nível deve estar entre " + NIVEL_MINIMO + " e "
                    + NIVEL_MAXIMO + ", mas foi " + nivel);
        }
    }
}
