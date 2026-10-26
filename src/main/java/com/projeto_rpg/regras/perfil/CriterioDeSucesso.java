package com.projeto_rpg.regras.perfil;

/**
 * Decide se um total vence o alvo e qual de dois dados é o melhor.
 * Permite sistemas de "rolar acima" (D&D) e "rolar abaixo" (Call of Cthulhu).
 */
public interface CriterioDeSucesso {

    boolean foiBemSucedido(int total, int alvo);

    /**
     * Melhor dado entre dois, usado na vantagem.
     */
    int melhorDado(int primeiro, int segundo);

    /**
     * Pior dado entre dois, usado na desvantagem.
     */
    default int piorDado(int primeiro, int segundo) {
        return melhorDado(primeiro, segundo) == primeiro ? segundo : primeiro;
    }
}
