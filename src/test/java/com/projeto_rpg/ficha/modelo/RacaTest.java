package com.projeto_rpg.ficha.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RacaTest {

    @Test
    @DisplayName("deve criar raça com nome e deslocamento")
    void deveCriarRacaQuandoDadosForemValidos() {
        Raca raca = new Raca("Anão", 25);

        assertAll(
                () -> assertEquals("Anão", raca.getNome()),
                () -> assertEquals(25, raca.getDeslocamento()));
    }

    @Test
    @DisplayName("deve remover espaços nas pontas do nome")
    void deveRemoverEspacosDoNomeQuandoHouverEspacos() {
        assertEquals("Elfo", new Raca(" Elfo  ", 30).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção quando o nome for vazio ou nulo")
    void deveLancarExcecaoQuandoNomeDaRacaForVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Raca("", 30));
        assertThrows(IllegalArgumentException.class, () -> new Raca(null, 30));
    }

    @Test
    @DisplayName("deve lançar exceção quando o deslocamento não for positivo")
    void deveLancarExcecaoQuandoDeslocamentoNaoForPositivo() {
        assertThrows(IllegalArgumentException.class, () -> new Raca("Humano", 0));
        assertThrows(IllegalArgumentException.class, () -> new Raca("Humano", -5));
    }

    @Test
    @DisplayName("deve atualizar nome e deslocamento")
    void deveAtualizarRacaQuandoDadosForemValidos() {
        Raca raca = new Raca("Humano", 30);

        raca.atualizar("Meio-elfo", 35);

        assertAll(
                () -> assertEquals("Meio-elfo", raca.getNome()),
                () -> assertEquals(35, raca.getDeslocamento()));
    }

    @Test
    @DisplayName("deve manter os dados anteriores quando a atualização for inválida")
    void deveManterDadosQuandoAtualizacaoDaRacaForInvalida() {
        Raca raca = new Raca("Humano", 30);

        assertThrows(IllegalArgumentException.class, () -> raca.atualizar("Elfo", 0));

        assertAll(
                () -> assertEquals("Humano", raca.getNome()),
                () -> assertEquals(30, raca.getDeslocamento()));
    }
}
