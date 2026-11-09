package com.projeto_rpg.ficha.regra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraDePvTest {

    @Test
    @DisplayName("deve usar o dado de vida cheio mais Constituição no nível 1")
    void deveUsarDadoCheioQuandoNivelForUm() {
        assertAll(
                () -> assertEquals(10, CalculadoraDePv.pvMaximo(10, 1, 0)),
                () -> assertEquals(13, CalculadoraDePv.pvMaximo(10, 1, 3)),
                () -> assertEquals(8, CalculadoraDePv.pvMaximo(10, 1, -2)));
    }

    @Test
    @DisplayName("deve usar a média fixa do dado (dado / 2 + 1) a partir do nível 2")
    void deveUsarMediaFixaQuandoNivelForMaiorQueUm() {
        assertAll(
                () -> assertEquals(6 + 4, CalculadoraDePv.pvMaximo(6, 2, 0)),
                () -> assertEquals(8 + 5 * 2, CalculadoraDePv.pvMaximo(8, 3, 0)),
                () -> assertEquals(12 + 7 * 19, CalculadoraDePv.pvMaximo(12, 20, 0)));
    }

    @Test
    @DisplayName("deve somar o modificador de Constituição em todos os níveis")
    void deveSomarConstituicaoQuandoHouverVariosNiveis() {
        assertEquals((10 + 2) + 3 * (6 + 2), CalculadoraDePv.pvMaximo(10, 4, 2));
    }

    @Test
    @DisplayName("deve garantir no mínimo 1 PV por nível")
    void deveGarantirMinimoDeUmPvQuandoConstituicaoForMuitoNegativa() {
        assertAll(
                () -> assertEquals(1, CalculadoraDePv.pvMaximo(6, 1, -5)),
                () -> assertEquals(3, CalculadoraDePv.pvMaximo(6, 3, -5)));
    }
}
