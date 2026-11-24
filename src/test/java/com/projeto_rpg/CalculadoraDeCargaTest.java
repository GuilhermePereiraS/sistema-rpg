package com.projeto_rpg.inventario.regra;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraDeCargaTest {

    @Test
    @DisplayName("deve calcular 15 libras por ponto de Força")
    void deveCalcularQuinzePorPontoDeForca() {
        assertAll(
                () -> assertEquals(150, CalculadoraDeCarga.capacidadeDeCarga(10)),
                () -> assertEquals(240, CalculadoraDeCarga.capacidadeDeCarga(16)),
                () -> assertEquals(450, CalculadoraDeCarga.capacidadeDeCarga(30)));
    }

    @Test
    @DisplayName("deve retornar zero quando a Força for zero")
    void deveRetornarZeroQuandoForcaForZero() {
        assertEquals(0, CalculadoraDeCarga.capacidadeDeCarga(0));
    }

    @Test
    @DisplayName("deve lançar exceção quando a Força for negativa")
    void deveLancarExcecaoQuandoForcaForNegativa() {
        assertThrows(IllegalArgumentException.class, () -> CalculadoraDeCarga.capacidadeDeCarga(-1));
    }
}
