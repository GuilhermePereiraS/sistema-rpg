package com.projeto_rpg.regras.perfil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CriticoPorFaixaTest {

    private final RegraDeCritico dnd = new CriticoPorFaixa(20, 20, 1, 1);
    private final RegraDeCritico percentual = new CriticoPorFaixa(1, 5, 96, 100);

    @Test
    @DisplayName("deve reconhecer o 20 natural como crítico e o 1 como falha crítica no padrão D&D")
    void deveReconhecerCriticoEFalhaQuandoPadraoForDnd() {
        assertTrue(dnd.eCritico(20));
        assertFalse(dnd.eCritico(19));
        assertTrue(dnd.eFalhaCritica(1));
        assertFalse(dnd.eFalhaCritica(2));
    }

    @ParameterizedTest
    @ValueSource(ints = {19, 20})
    @DisplayName("deve permitir faixa ampliada de crítico (ex.: 19-20)")
    void deveReconhecerCriticoQuandoFaixaForAmpliada(int dado) {
        assertTrue(new CriticoPorFaixa(19, 20, 1, 1).eCritico(dado));
    }

    @Test
    @DisplayName("deve suportar faixas invertidas de sistemas percentuais (crítico baixo, falha alta)")
    void deveReconhecerFaixasQuandoSistemaForPercentual() {
        assertTrue(percentual.eCritico(3));
        assertFalse(percentual.eCritico(6));
        assertTrue(percentual.eFalhaCritica(97));
        assertFalse(percentual.eFalhaCritica(95));
    }

    @ParameterizedTest(name = "crítico [{0},{1}] falha [{2},{3}]")
    @CsvSource({"20, 19, 1, 1", "20, 20, 2, 1", "0, 20, 1, 1", "10, 20, 1, 15"})
    @DisplayName("deve lançar exceção quando as faixas forem invertidas, não positivas ou sobrepostas")
    void deveLancarExcecaoQuandoFaixasForemInvalidas(int critMin, int critMax, int falhaMin, int falhaMax) {
        assertThrows(IllegalArgumentException.class,
                () -> new CriticoPorFaixa(critMin, critMax, falhaMin, falhaMax));
    }
}
