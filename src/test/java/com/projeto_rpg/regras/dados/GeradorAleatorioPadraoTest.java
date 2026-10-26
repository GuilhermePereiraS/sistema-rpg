package com.projeto_rpg.regras.dados;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.Set;
import java.util.SplittableRandom;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeradorAleatorioPadraoTest {

    @Test
    @DisplayName("deve gerar valores sempre entre 1 e o número de faces, cobrindo todas as faces")
    void deveGerarValoresDentroDoIntervaloQuandoRolarMuitasVezes() {
        GeradorAleatorioPadrao gerador = new GeradorAleatorioPadrao();
        Set<Integer> valoresObtidos = new HashSet<>();

        for (int i = 0; i < 2_000; i++) {
            int valor = gerador.rolar(6);
            assertTrue(valor >= 1 && valor <= 6, "valor fora do intervalo: " + valor);
            valoresObtidos.add(valor);
        }

        assertEquals(Set.of(1, 2, 3, 4, 5, 6), valoresObtidos);
    }

    @Test
    @DisplayName("deve ser reproduzível quando receber um gerador com a mesma semente")
    void deveRepetirSequenciaQuandoSementeForIgual() {
        GeradorAleatorioPadrao primeiro = new GeradorAleatorioPadrao(new SplittableRandom(42));
        GeradorAleatorioPadrao segundo = new GeradorAleatorioPadrao(new SplittableRandom(42));

        int[] sequenciaA = IntStream.range(0, 20).map(i -> primeiro.rolar(20)).toArray();
        int[] sequenciaB = IntStream.range(0, 20).map(i -> segundo.rolar(20)).toArray();

        assertEquals(java.util.Arrays.toString(sequenciaA), java.util.Arrays.toString(sequenciaB));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 0, -6})
    @DisplayName("deve lançar exceção quando o dado tiver menos de 2 faces")
    void deveLancarExcecaoQuandoFacesForemMenoresQueDois(int faces) {
        GeradorAleatorioPadrao gerador = new GeradorAleatorioPadrao();

        assertThrows(IllegalArgumentException.class, () -> gerador.rolar(faces));
    }
}
