package com.projeto_rpg.ficha.modelo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AtributosTest {

    @Test
    @DisplayName("deve calcular o modificador pela fórmula (valor - 10) / 2 arredondado para baixo")
    void deveCalcularModificadorQuandoValorForInformado() {
        assertAll(
                () -> assertEquals(-5, Atributos.modificadorDe(1)),
                () -> assertEquals(-1, Atributos.modificadorDe(8)),
                () -> assertEquals(-1, Atributos.modificadorDe(9)),
                () -> assertEquals(0, Atributos.modificadorDe(10)),
                () -> assertEquals(0, Atributos.modificadorDe(11)),
                () -> assertEquals(1, Atributos.modificadorDe(12)),
                () -> assertEquals(2, Atributos.modificadorDe(15)),
                () -> assertEquals(5, Atributos.modificadorDe(20)),
                () -> assertEquals(10, Atributos.modificadorDe(30)));
    }

    @Test
    @DisplayName("deve expor valor e modificador de cada atributo")
    void deveExporValorEModificadorQuandoConsultarAtributo() {
        Atributos atributos = new Atributos(15, 14, 13, 12, 10, 8);

        assertAll(
                () -> assertEquals(15, atributos.valor(Atributo.FORCA)),
                () -> assertEquals(14, atributos.valor(Atributo.DESTREZA)),
                () -> assertEquals(13, atributos.valor(Atributo.CONSTITUICAO)),
                () -> assertEquals(12, atributos.valor(Atributo.INTELIGENCIA)),
                () -> assertEquals(10, atributos.valor(Atributo.SABEDORIA)),
                () -> assertEquals(8, atributos.valor(Atributo.CARISMA)),
                () -> assertEquals(2, atributos.modificador(Atributo.FORCA)),
                () -> assertEquals(2, atributos.modificador(Atributo.DESTREZA)),
                () -> assertEquals(1, atributos.modificador(Atributo.CONSTITUICAO)),
                () -> assertEquals(1, atributos.modificador(Atributo.INTELIGENCIA)),
                () -> assertEquals(0, atributos.modificador(Atributo.SABEDORIA)),
                () -> assertEquals(-1, atributos.modificador(Atributo.CARISMA)));
    }
}
