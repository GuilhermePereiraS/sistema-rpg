package com.projeto_rpg.inventario.regra;

import com.projeto_rpg.inventario.modelo.Item;
import com.projeto_rpg.inventario.modelo.TipoDeArmadura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculadoraDeCaTest {

    @Test
    @DisplayName("sem armadura deve ser 10 mais o modificador de Destreza")
    void deveSomarDestrezaQuandoNaoHouverArmadura() {
        assertAll(
                () -> assertEquals(13, CalculadoraDeCa.caFinal(3, null, null)),
                () -> assertEquals(10, CalculadoraDeCa.caFinal(0, null, null)),
                () -> assertEquals(8, CalculadoraDeCa.caFinal(-2, null, null)));
    }

    @Test
    @DisplayName("armadura leve deve somar a Destreza integralmente")
    void deveSomarDestrezaIntegralQuandoArmaduraForLeve() {
        Item couro = Item.armadura("Armadura de couro", 10.0, 11, TipoDeArmadura.LEVE);

        assertAll(
                () -> assertEquals(14, CalculadoraDeCa.caFinal(3, couro, null)),
                () -> assertEquals(9, CalculadoraDeCa.caFinal(-2, couro, null)));
    }

    @Test
    @DisplayName("armadura média deve somar a Destreza limitada a +2")
    void deveLimitarDestrezaDoisQuandoArmaduraForMedia() {
        Item cota = Item.armadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);

        assertAll(
                () -> assertEquals(16, CalculadoraDeCa.caFinal(3, cota, null)),
                () -> assertEquals(16, CalculadoraDeCa.caFinal(2, cota, null)),
                () -> assertEquals(14, CalculadoraDeCa.caFinal(0, cota, null)),
                () -> assertEquals(13, CalculadoraDeCa.caFinal(-1, cota, null)));
    }

    @Test
    @DisplayName("armadura pesada deve usar apenas a CA base")
    void deveIgnorarDestrezaQuandoArmaduraForPesada() {
        Item placas = Item.armadura("Armadura de placas", 65.0, 16, TipoDeArmadura.PESADA);

        assertAll(
                () -> assertEquals(16, CalculadoraDeCa.caFinal(3, placas, null)),
                () -> assertEquals(16, CalculadoraDeCa.caFinal(0, placas, null)),
                () -> assertEquals(16, CalculadoraDeCa.caFinal(-3, placas, null)));
    }

    @Test
    @DisplayName("escudo deve somar +2 em qualquer caso")
    void deveSomarDoisQuandoHouverEscudo() {
        Item couro = Item.armadura("Armadura de couro", 10.0, 11, TipoDeArmadura.LEVE);
        Item cota = Item.armadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        Item placas = Item.armadura("Armadura de placas", 65.0, 16, TipoDeArmadura.PESADA);
        Item escudo = Item.escudo("Escudo", 6.0);

        assertAll(
                () -> assertEquals(15, CalculadoraDeCa.caFinal(3, null, escudo)),
                () -> assertEquals(16, CalculadoraDeCa.caFinal(3, couro, escudo)),
                () -> assertEquals(18, CalculadoraDeCa.caFinal(3, cota, escudo)),
                () -> assertEquals(18, CalculadoraDeCa.caFinal(3, placas, escudo)));
    }

    @Test
    @DisplayName("deve lançar exceção quando a armadura não for do tipo ARMADURA")
    void deveLancarExcecaoQuandoArmaduraNaoForDoTipo() {
        Item espada = Item.arma("Espada longa", 3.0);

        assertThrows(IllegalArgumentException.class, () -> CalculadoraDeCa.caFinal(2, espada, null));
    }

    @Test
    @DisplayName("deve lançar exceção quando o escudo não for do tipo ESCUDO")
    void deveLancarExcecaoQuandoEscudoNaoForDoTipo() {
        Item corda = Item.geral("Corda", 10.0);

        assertThrows(IllegalArgumentException.class, () -> CalculadoraDeCa.caFinal(2, null, corda));
    }
}
