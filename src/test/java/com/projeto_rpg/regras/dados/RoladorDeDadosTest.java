package com.projeto_rpg.regras.dados;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoladorDeDadosTest {

    private final ParserDeExpressao parser = new ParserDeExpressao();

    private ResultadoRolagem rolar(String expressao, Integer... valores) {
        return new RoladorDeDados(new GeradorFixo(valores)).rolar(parser.interpretar(expressao));
    }

    @Test
    @DisplayName("deve somar os dados e o modificador")
    void deveSomarDadosEModificadorQuandoRolarExpressaoSimples() {
        ResultadoRolagem resultado = rolar("2d6+3", 4, 5);

        assertEquals(List.of(4, 5), resultado.dados());
        assertEquals(12, resultado.total());
    }

    @Test
    @DisplayName("deve somar todos os termos de uma expressão composta")
    void deveSomarTodosOsTermosQuandoExpressaoForComposta() {
        ResultadoRolagem resultado = rolar("1d8+1d6-1", 8, 6);

        assertEquals(List.of(8, 6), resultado.dados());
        assertEquals(13, resultado.total());
    }

    @Test
    @DisplayName("deve manter apenas os maiores dados quando a seleção for 'kh'")
    void deveSomarApenasMaioresQuandoTermoMantiverMaiores() {
        ResultadoRolagem resultado = rolar("4d6kh3", 1, 6, 3, 5);

        assertEquals(List.of(1, 6, 3, 5), resultado.dados());
        assertEquals(14, resultado.total());
    }

    @Test
    @DisplayName("deve manter apenas os menores dados quando a seleção for 'kl'")
    void deveSomarApenasMenoresQuandoTermoMantiverMenores() {
        ResultadoRolagem resultado = rolar("2d20kl1", 15, 7);

        assertEquals(7, resultado.total());
    }

    @Test
    @DisplayName("deve pedir ao gerador o número de faces de cada termo")
    void devePedirFacesCorretasAoGeradorQuandoRolar() {
        GeradorFixo gerador = new GeradorFixo(1, 2, 3);

        new RoladorDeDados(gerador).rolar(parser.interpretar("2d4+1d12"));

        assertEquals(List.of(4, 4, 12), gerador.facesSolicitadas());
    }

    @Test
    @DisplayName("deve permitir total negativo quando o modificador for maior que os dados")
    void devePermitirTotalNegativoQuandoModificadorSuperarDados() {
        ResultadoRolagem resultado = rolar("1d4-5", 1);

        assertEquals(-4, resultado.total());
    }
}
