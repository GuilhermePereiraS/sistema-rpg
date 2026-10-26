package com.projeto_rpg.regras.combate;

import com.projeto_rpg.regras.excecao.TesteContraMorteNaoPermitidoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MorteAoZerarTest {

    private final RegraDeMorte regra = new MorteAoZerar();

    @Test
    @DisplayName("deve morrer assim que o PV chegar a zero, sem morte instantânea por excedente")
    void deveMorrerQuandoPvChegarAZero() {
        EstadoDeCombate estado = new EstadoDeCombate(1L, 12);

        estado.sofrerDano(12, false, regra);

        assertEquals(Condicao.MORTO, estado.getCondicao());
        assertEquals(0, estado.getPvAtual());
    }

    @Test
    @DisplayName("deve continuar consciente enquanto o PV for positivo")
    void deveContinuarConscienteQuandoPvForPositivo() {
        EstadoDeCombate estado = new EstadoDeCombate(1L, 12);

        estado.sofrerDano(11, false, regra);

        assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
    }

    @Test
    @DisplayName("deve recusar teste contra a morte porque o sistema não o possui")
    void deveRecusarTesteContraMorteQuandoSistemaNaoPossuir() {
        EstadoDeCombate estado = new EstadoDeCombate(1L, 12);

        assertThrows(TesteContraMorteNaoPermitidoException.class,
                () -> estado.realizarTesteContraMorte(10, regra));
    }
}
