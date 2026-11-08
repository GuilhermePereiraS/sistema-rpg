package com.projeto_rpg.ficha.regra;

import com.projeto_rpg.ficha.excecao.CompraDePontosInvalidaException;
import com.projeto_rpg.ficha.modelo.Atributos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CompraDePontosTest {

    @Test
    @DisplayName("deve custar 1 ponto por valor acima de 8 até 13")
    void deveCobrarUmPontoPorValorQuandoValorEstiverAteTreze() {
        assertAll(
                () -> assertEquals(0, CompraDePontos.custo(8)),
                () -> assertEquals(1, CompraDePontos.custo(9)),
                () -> assertEquals(2, CompraDePontos.custo(10)),
                () -> assertEquals(3, CompraDePontos.custo(11)),
                () -> assertEquals(4, CompraDePontos.custo(12)),
                () -> assertEquals(5, CompraDePontos.custo(13)));
    }

    @Test
    @DisplayName("deve custar 2 pontos por valor de 14 e 15")
    void deveCobrarDoisPontosPorValorQuandoValorForQuatorzeOuQuinze() {
        assertAll(
                () -> assertEquals(7, CompraDePontos.custo(14)),
                () -> assertEquals(9, CompraDePontos.custo(15)));
    }

    @Test
    @DisplayName("deve lançar exceção ao consultar custo de valor fora de 8 a 15")
    void deveLancarExcecaoQuandoValorEstiverForaDaFaixaDeCompra() {
        assertThrows(CompraDePontosInvalidaException.class, () -> CompraDePontos.custo(7));
        assertThrows(CompraDePontosInvalidaException.class, () -> CompraDePontos.custo(16));
    }
}
