package com.projeto_rpg.regras.dados;

import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Implementação de produção do {@link GeradorAleatorio}, baseada em {@link RandomGenerator}.
 */
public class GeradorAleatorioPadrao implements GeradorAleatorio {

    private static final int FACES_MINIMAS = 2;

    private final RandomGenerator random;

    public GeradorAleatorioPadrao() {
        this(RandomGenerator.getDefault());
    }

    public GeradorAleatorioPadrao(RandomGenerator random) {
        this.random = Objects.requireNonNull(random, "RandomGenerator não pode ser nulo");
    }

    @Override
    public int rolar(int faces) {
        if (faces < FACES_MINIMAS) {
            throw new IllegalArgumentException("Dado deve ter ao menos " + FACES_MINIMAS + " faces");
        }
        return random.nextInt(1, faces + 1);
    }
}
