package com.projeto_rpg.ficha.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClasseTest {

    @Test
    @DisplayName("deve criar classe com nome e dado de vida")
    void deveCriarClasseQuandoDadosForemValidos() {
        Classe classe = new Classe("Guerreiro", 10);

        assertAll(
                () -> assertEquals("Guerreiro", classe.getNome()),
                () -> assertEquals(10, classe.getDadoDeVida()));
    }

    @Test
    @DisplayName("deve remover espaços nas pontas do nome")
    void deveRemoverEspacosDoNomeQuandoHouverEspacos() {
        assertEquals("Mago", new Classe("  Mago ", 6).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção quando o nome for vazio ou nulo")
    void deveLancarExcecaoQuandoNomeDaClasseForVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Classe("  ", 8));
        assertThrows(IllegalArgumentException.class, () -> new Classe(null, 8));
    }

    @Test
    @DisplayName("deve aceitar apenas d6, d8, d10 e d12 como dado de vida")
    void deveLancarExcecaoQuandoDadoDeVidaForInvalido() {
        assertAll(
                () -> assertEquals(6, new Classe("A", 6).getDadoDeVida()),
                () -> assertEquals(8, new Classe("B", 8).getDadoDeVida()),
                () -> assertEquals(10, new Classe("C", 10).getDadoDeVida()),
                () -> assertEquals(12, new Classe("D", 12).getDadoDeVida()),
                () -> assertThrows(IllegalArgumentException.class, () -> new Classe("E", 4)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Classe("F", 20)));
    }

    @Test
    @DisplayName("deve atualizar nome e dado de vida")
    void deveAtualizarClasseQuandoDadosForemValidos() {
        Classe classe = new Classe("Guerreiro", 10);

        classe.atualizar("Bárbaro", 12);

        assertAll(
                () -> assertEquals("Bárbaro", classe.getNome()),
                () -> assertEquals(12, classe.getDadoDeVida()));
    }

    @Test
    @DisplayName("deve manter os dados anteriores quando a atualização for inválida")
    void deveManterDadosQuandoAtualizacaoForInvalida() {
        Classe classe = new Classe("Guerreiro", 10);

        assertThrows(IllegalArgumentException.class, () -> classe.atualizar("Bárbaro", 7));

        assertAll(
                () -> assertEquals("Guerreiro", classe.getNome()),
                () -> assertEquals(10, classe.getDadoDeVida()));
    }
}
