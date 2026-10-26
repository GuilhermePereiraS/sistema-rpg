package com.projeto_rpg.regras.dados;

/**
 * Fonte de aleatoriedade do motor. Isolada em uma interface para que os testes
 * possam usar valores previsíveis.
 */
@FunctionalInterface
public interface GeradorAleatorio {

    /**
     * @return um número inteiro entre 1 e {@code faces}, inclusive
     */
    int rolar(int faces);
}
