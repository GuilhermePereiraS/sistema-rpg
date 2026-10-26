package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.mecanica.ModoDeRolagem;

/**
 * Textos padronizados das rolagens registradas no histórico.
 */
final class DescricaoDeRolagem {

    private DescricaoDeRolagem() {
    }

    /** Ex.: "d20+5 contra 15 (VANTAGEM)". */
    static String teste(int facesDoDado, int modificador, int alvo, ModoDeRolagem modo) {
        return "d%d%+d contra %d (%s)".formatted(facesDoDado, modificador, alvo, modo);
    }

    /** Ex.: "1d8+3" ou "1d8+3 (crítico)". */
    static String dano(String expressao, boolean critico) {
        return expressao.trim() + (critico ? " (crítico)" : "");
    }

    /** Ex.: "d20 teste contra a morte". */
    static String testeContraMorte(int facesDoDado) {
        return "d%d teste contra a morte".formatted(facesDoDado);
    }
}
