package com.projeto_rpg.regras.dados;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
