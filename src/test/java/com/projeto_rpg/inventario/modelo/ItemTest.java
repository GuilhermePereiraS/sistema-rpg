package com.projeto_rpg.inventario.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemTest {

    @Test
    @DisplayName("deve criar arma com nome, tipo e peso")
    void deveCriarArmaQuandoDadosForemValidos() {
        Item espada = Item.arma("Espada longa", 3.0);

        assertAll(
                () -> assertEquals("Espada longa", espada.getNome()),
                () -> assertEquals(TipoDeItem.ARMA, espada.getTipo()),
                () -> assertEquals(3.0, espada.getPeso()),
                () -> assertEquals(0, espada.getCaBase()),
                () -> assertNull(espada.getTipoDeArmadura()));
    }

    @Test
    @DisplayName("deve criar armadura com CA base e categoria")
    void deveCriarArmaduraQuandoDadosForemValidos() {
        Item cota = Item.armadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);

        assertAll(
                () -> assertEquals(TipoDeItem.ARMADURA, cota.getTipo()),
                () -> assertEquals(14, cota.getCaBase()),
                () -> assertEquals(TipoDeArmadura.MEDIA, cota.getTipoDeArmadura()));
    }

    @Test
    @DisplayName("deve criar escudo, consumível e item geral sem CA base")
    void deveCriarItensSimplesSemCaBase() {
        assertAll(
                () -> assertEquals(TipoDeItem.ESCUDO, Item.escudo("Escudo", 6.0).getTipo()),
                () -> assertEquals(TipoDeItem.CONSUMIVEL, Item.consumivel("Poção", 0.5).getTipo()),
                () -> assertEquals(TipoDeItem.GERAL, Item.geral("Corda", 10.0).getTipo()),
                () -> assertEquals(0, Item.escudo("Escudo", 6.0).getCaBase()),
                () -> assertNull(Item.escudo("Escudo", 6.0).getTipoDeArmadura()));
    }

    @Test
    @DisplayName("deve remover espaços nas pontas do nome")
    void deveRemoverEspacosDoNomeQuandoHouverEspacos() {
        assertEquals("Adaga", Item.arma("  Adaga ", 1.0).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção quando o nome for vazio ou nulo")
    void deveLancarExcecaoQuandoNomeDoItemForVazio() {
        assertThrows(IllegalArgumentException.class, () -> Item.arma("  ", 1.0));
        assertThrows(IllegalArgumentException.class, () -> Item.arma(null, 1.0));
    }

    @Test
    @DisplayName("deve lançar exceção quando o peso for negativo")
    void deveLancarExcecaoQuandoPesoForNegativo() {
        assertThrows(IllegalArgumentException.class, () -> Item.arma("Adaga", -1.0));
    }

    @Test
    @DisplayName("deve lançar exceção quando armadura não tiver CA base ou categoria")
    void deveLancarExcecaoQuandoArmaduraForIncompleta() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Item.armadura("Peitoral", 20.0, 0, TipoDeArmadura.MEDIA)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Item.armadura("Peitoral", 20.0, 14, null)));
    }

    @Test
    @DisplayName("deve lançar exceção quando item que não é armadura receber CA base ou categoria")
    void deveLancarExcecaoQuandoItemComumReceberDadosDeArmadura() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Item.arma("Adaga", 1.0).atualizar("Adaga", TipoDeItem.ARMA, 1.0, 12, null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Item.escudo("Escudo", 6.0).atualizar("Escudo", TipoDeItem.ESCUDO, 6.0, 0,
                                TipoDeArmadura.LEVE)));
    }

    @Test
    @DisplayName("deve lançar exceção quando o tipo for nulo")
    void deveLancarExcecaoQuandoTipoForNulo() {
        Item item = Item.arma("Adaga", 1.0);

        assertThrows(IllegalArgumentException.class,
                () -> item.atualizar("Adaga", null, 1.0, 0, null));
    }

    @Test
    @DisplayName("deve atualizar os dados do item")
    void deveAtualizarItemQuandoDadosForemValidos() {
        Item item = Item.arma("Adaga", 1.0);

        item.atualizar("Cota de malha", TipoDeItem.ARMADURA, 30.0, 14, TipoDeArmadura.MEDIA);

        assertAll(
                () -> assertEquals("Cota de malha", item.getNome()),
                () -> assertEquals(TipoDeItem.ARMADURA, item.getTipo()),
                () -> assertEquals(30.0, item.getPeso()),
                () -> assertEquals(14, item.getCaBase()),
                () -> assertEquals(TipoDeArmadura.MEDIA, item.getTipoDeArmadura()));
    }

    @Test
    @DisplayName("deve manter os dados anteriores quando a atualização for inválida")
    void deveManterDadosQuandoAtualizacaoForInvalida() {
        Item cota = Item.armadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);

        assertThrows(IllegalArgumentException.class,
                () -> cota.atualizar("Cota de malha", TipoDeItem.ARMADURA, 30.0, 0, TipoDeArmadura.MEDIA));

        assertAll(
                () -> assertEquals("Cota de malha", cota.getNome()),
                () -> assertEquals(14, cota.getCaBase()),
                () -> assertEquals(TipoDeArmadura.MEDIA, cota.getTipoDeArmadura()));
    }
}
