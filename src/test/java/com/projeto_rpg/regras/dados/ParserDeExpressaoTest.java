package com.projeto_rpg.regras.dados;

import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserDeExpressaoTest {

    private ParserDeExpressao parser;

    @BeforeEach
    void setUp() {
        parser = new ParserDeExpressao();
    }

    @Test
    @DisplayName("deve interpretar um termo simples NdF")
    void deveInterpretarTermoSimplesQuandoExpressaoForNdF() {
        ExpressaoDeDado expressao = parser.interpretar("2d6");

        assertEquals(List.of(TermoDeDado.simples(2, 6)), expressao.termos());
        assertEquals(0, expressao.modificador());
    }

    @Test
    @DisplayName("deve assumir quantidade 1 quando a quantidade for omitida")
    void deveAssumirQuantidadeUmQuandoQuantidadeForOmitida() {
        ExpressaoDeDado expressao = parser.interpretar("d8");

        assertEquals(List.of(TermoDeDado.simples(1, 8)), expressao.termos());
    }

    @Test
    @DisplayName("deve somar o modificador positivo")
    void deveInterpretarModificadorQuandoExpressaoTiverSoma() {
        ExpressaoDeDado expressao = parser.interpretar("2d6+3");

        assertEquals(List.of(TermoDeDado.simples(2, 6)), expressao.termos());
        assertEquals(3, expressao.modificador());
    }

    @Test
    @DisplayName("deve subtrair o modificador negativo")
    void deveInterpretarModificadorNegativoQuandoExpressaoTiverSubtracao() {
        ExpressaoDeDado expressao = parser.interpretar("4d6-1");

        assertEquals(-1, expressao.modificador());
    }

    @Test
    @DisplayName("deve interpretar vários grupos de dados e acumular os modificadores")
    void deveInterpretarVariosTermosQuandoExpressaoForComposta() {
        ExpressaoDeDado expressao = parser.interpretar("2d6+1d4+2-1");

        assertEquals(List.of(TermoDeDado.simples(2, 6), TermoDeDado.simples(1, 4)), expressao.termos());
        assertEquals(1, expressao.modificador());
    }

    @Test
    @DisplayName("deve ignorar espaços e maiúsculas")
    void deveIgnorarEspacosEMaiusculasQuandoExpressaoForDigitadaLivremente() {
        ExpressaoDeDado expressao = parser.interpretar("  1D20 + 5 ");

        assertEquals(List.of(TermoDeDado.simples(1, 20)), expressao.termos());
        assertEquals(5, expressao.modificador());
    }

    @Test
    @DisplayName("deve interpretar 'kh' como manter os maiores dados")
    void deveManterMaioresQuandoExpressaoTiverKh() {
        ExpressaoDeDado expressao = parser.interpretar("4d6kh3");

        assertEquals(List.of(TermoDeDado.mantendoMaiores(4, 6, 3)), expressao.termos());
    }

    @Test
    @DisplayName("deve interpretar 'kl' como manter os menores dados")
    void deveManterMenoresQuandoExpressaoTiverKl() {
        ExpressaoDeDado expressao = parser.interpretar("2d20kl1+4");

        assertEquals(List.of(TermoDeDado.mantendoMenores(2, 20, 1)), expressao.termos());
        assertEquals(4, expressao.modificador());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("deve lançar exceção quando a expressão for nula ou vazia")
    void deveLancarExcecaoQuandoExpressaoForNulaOuVazia(String texto) {
        assertThrows(ExpressaoInvalidaException.class, () -> parser.interpretar(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "2d", "d", "2d6+", "2d6++3", "+", "2d6x", "2d6kh", "5", "-3", "d6d6"})
    @DisplayName("deve lançar exceção quando a expressão estiver malformada ou não tiver dados")
    void deveLancarExcecaoQuandoExpressaoForMalformada(String texto) {
        assertThrows(ExpressaoInvalidaException.class, () -> parser.interpretar(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"99999999999d6", "1d6+99999999999"})
    @DisplayName("deve lançar exceção quando algum número não couber em um inteiro")
    void deveLancarExcecaoQuandoNumeroForGrandeDemais(String texto) {
        assertThrows(ExpressaoInvalidaException.class, () -> parser.interpretar(texto));
    }

    @Test
    @DisplayName("deve lançar exceção quando dados forem subtraídos")
    void deveLancarExcecaoQuandoDadosForemSubtraidos() {
        assertThrows(ExpressaoInvalidaException.class, () -> parser.interpretar("1d20-1d4"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0d6", "101d6", "1d1", "1d1001", "2d20kh0", "2d20kl3"})
    @DisplayName("deve lançar exceção quando quantidade, faces ou dados mantidos estiverem fora dos limites")
    void deveLancarExcecaoQuandoLimitesForemViolados(String texto) {
        assertThrows(ExpressaoInvalidaException.class, () -> parser.interpretar(texto));
    }

    @ParameterizedTest
    @ValueSource(strings = {"100d6", "1d2", "1d1000", "2d20kh2"})
    @DisplayName("deve aceitar valores exatamente nos limites")
    void deveAceitarExpressaoQuandoValoresEstiveremNosLimites(String texto) {
        assertDoesNotThrow(() -> parser.interpretar(texto));
    }
}
