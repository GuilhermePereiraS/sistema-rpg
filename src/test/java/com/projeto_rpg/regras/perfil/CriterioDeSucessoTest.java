package com.projeto_rpg.regras.perfil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CriterioDeSucessoTest {

    @Nested
    @DisplayName("Rolar acima ou igual (D&D, Pathfinder)")
    class RolarAcima {

        private final CriterioDeSucesso criterio = new RolarAcimaOuIgual();

        @ParameterizedTest(name = "total {0} contra alvo {1} -> {2}")
        @CsvSource({"15, 15, true", "16, 15, true", "14, 15, false"})
        @DisplayName("deve ter sucesso quando o total for maior ou igual ao alvo")
        void deveTerSucessoQuandoTotalForMaiorOuIgualAoAlvo(int total, int alvo, boolean esperado) {
            assertEquals(esperado, criterio.foiBemSucedido(total, alvo));
        }

        @Test
        @DisplayName("deve considerar o maior dado como o melhor")
        void deveConsiderarMaiorDadoComoMelhor() {
            assertEquals(18, criterio.melhorDado(7, 18));
            assertEquals(7, criterio.piorDado(7, 18));
        }
    }

    @Nested
    @DisplayName("Rolar abaixo ou igual (Call of Cthulhu, GURPS)")
    class RolarAbaixo {

        private final CriterioDeSucesso criterio = new RolarAbaixoOuIgual();

        @ParameterizedTest(name = "total {0} contra alvo {1} -> {2}")
        @CsvSource({"60, 60, true", "12, 60, true", "61, 60, false"})
        @DisplayName("deve ter sucesso quando o total for menor ou igual ao alvo")
        void deveTerSucessoQuandoTotalForMenorOuIgualAoAlvo(int total, int alvo, boolean esperado) {
            assertEquals(esperado, criterio.foiBemSucedido(total, alvo));
        }

        @Test
        @DisplayName("deve considerar o menor dado como o melhor")
        void deveConsiderarMenorDadoComoMelhor() {
            assertEquals(7, criterio.melhorDado(7, 18));
            assertEquals(18, criterio.piorDado(7, 18));
        }
    }
}
