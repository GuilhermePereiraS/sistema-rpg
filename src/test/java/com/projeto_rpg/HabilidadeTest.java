package com.projeto_rpg.inventario.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HabilidadeTest {

    @Test
    @DisplayName("deve criar habilidade com nome, nível e descrição")
    void deveCriarHabilidadeQuandoDadosForemValidos() {
        Habilidade bolaDeFogo = new Habilidade("Bola de fogo", 3, "Explosão de 8d6");

        assertAll(
                () -> assertEquals("Bola de fogo", bolaDeFogo.getNome()),
                () -> assertEquals(3, bolaDeFogo.getNivel()),
                () -> assertEquals("Explosão de 8d6", bolaDeFogo.getDescricao()));
    }

    @Test
    @DisplayName("deve remover espaços nas pontas do nome e da descrição")
    void deveRemoverEspacosQuandoHouverEspacos() {
        Habilidade habilidade = new Habilidade("  Raio de fogo  ", 1, "  Dano de fogo  ");

        assertAll(
                () -> assertEquals("Raio de fogo", habilidade.getNome()),
                () -> assertEquals("Dano de fogo", habilidade.getDescricao()));
    }

    @Test
    @DisplayName("deve tratar descrição vazia como nula")
    void deveTratarDescricaoVaziaComoNula() {
        assertAll(
                () -> assertNull(new Habilidade("Luz", 0, "   ").getDescricao()),
                () -> assertNull(new Habilidade("Luz", 0, null).getDescricao()));
    }

    @Test
    @DisplayName("deve lançar exceção quando o nome for vazio ou nulo")
    void deveLancarExcecaoQuandoNomeDaHabilidadeForVazio() {
        assertThrows(IllegalArgumentException.class, () -> new Habilidade("  ", 1));
        assertThrows(IllegalArgumentException.class, () -> new Habilidade(null, 1));
    }

    @Test
    @DisplayName("deve aceitar apenas níveis de 0 a 9")
    void deveLancarExcecaoQuandoNivelForInvalido() {
        assertAll(
                () -> assertEquals(0, new Habilidade("Luz", 0).getNivel()),
                () -> assertEquals(9, new Habilidade("Desejo", 9).getNivel()),
                () -> assertThrows(IllegalArgumentException.class, () -> new Habilidade("Futuro", -1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new Habilidade("Futuro", 10)));
    }

    @Test
    @DisplayName("deve reconhecer truque quando o nível for zero")
    void deveReconhecerTruqueQuandoNivelForZero() {
        assertAll(
                () -> assertTrue(new Habilidade("Luz", 0).isTruque()),
                () -> assertFalse(new Habilidade("Raio de fogo", 1).isTruque()));
    }

    @Test
    @DisplayName("deve atualizar os dados da habilidade")
    void deveAtualizarHabilidadeQuandoDadosForemValidos() {
        Habilidade habilidade = new Habilidade("Raio de fogo", 1);

        habilidade.atualizar("Mísseis mágicos", 1, "Três dardos de força");

        assertAll(
                () -> assertEquals("Mísseis mágicos", habilidade.getNome()),
                () -> assertEquals(1, habilidade.getNivel()),
                () -> assertEquals("Três dardos de força", habilidade.getDescricao()));
    }

    @Test
    @DisplayName("deve manter os dados anteriores quando a atualização for inválida")
    void deveManterDadosQuandoAtualizacaoForInvalida() {
        Habilidade habilidade = new Habilidade("Raio de fogo", 1);

        assertThrows(IllegalArgumentException.class, () -> habilidade.atualizar("Futuro", 10, "x"));

        assertAll(
                () -> assertEquals("Raio de fogo", habilidade.getNome()),
                () -> assertEquals(1, habilidade.getNivel()));
    }
}
