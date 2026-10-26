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
}
