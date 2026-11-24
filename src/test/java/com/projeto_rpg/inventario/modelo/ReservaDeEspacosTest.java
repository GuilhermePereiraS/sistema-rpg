package com.projeto_rpg.inventario.modelo;

import com.projeto_rpg.inventario.excecao.EspacoDeHabilidadeInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaDeEspacosTest {

    private static final long PERSONAGEM = 1L;

    private ReservaDeEspacos reserva;

    @BeforeEach
    void prepararDados() {
        reserva = new ReservaDeEspacos(PERSONAGEM, List.of(2, 1, 0, 0, 0, 0, 0, 0, 0));
    }

    @Test
    @DisplayName("deve criar a reserva com o máximo informado para cada nível")
    void deveCriarReservaComMaximosPorNivel() {
        assertAll(
                () -> assertEquals(2, reserva.maximo(1)),
                () -> assertEquals(1, reserva.maximo(2)),
                () -> assertEquals(0, reserva.maximo(3)),
                () -> assertEquals(2, reserva.disponiveis(1)));
    }

    @Test
    @DisplayName("deve lançar exceção quando a lista de máximos não tiver nove níveis")
    void deveLancarExcecaoQuandoMaximosTiverTamanhoInvalido() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ReservaDeEspacos(PERSONAGEM, List.of(1, 1, 1))),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new ReservaDeEspacos(PERSONAGEM, null)));
    }

    @Test
    @DisplayName("deve lançar exceção quando algum máximo for negativo")
    void deveLancarExcecaoQuandoMaximoForNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ReservaDeEspacos(PERSONAGEM, List.of(-1, 0, 0, 0, 0, 0, 0, 0, 0)));
    }

    @Test
    @DisplayName("deve gastar espaço e reduzir os disponíveis")
    void deveGastarEspacoQuandoHouverDisponivel() {
        reserva.gastar(1);

        assertAll(
                () -> assertEquals(1, reserva.disponiveis(1)),
                () -> assertEquals(1, reserva.gastos(1)),
                () -> assertTrue(reserva.temEspaco(1)));
    }

    @Test
    @DisplayName("deve lançar exceção ao gastar espaço sem disponibilidade")
    void deveLancarExcecaoAoGastarSemEspaco() {
        reserva.gastar(1);
        reserva.gastar(1);

        assertAll(
                () -> assertFalse(reserva.temEspaco(1)),
                () -> assertThrows(EspacoDeHabilidadeInsuficienteException.class, () -> reserva.gastar(1)),
                () -> assertThrows(EspacoDeHabilidadeInsuficienteException.class, () -> reserva.gastar(3)));
    }

    @Test
    @DisplayName("deve lançar exceção ao usar nível fora de 1 a 9")
    void deveLancarExcecaoQuandoNivelForForaDoIntervalo() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> reserva.gastar(0)),
                () -> assertThrows(IllegalArgumentException.class, () -> reserva.gastar(10)),
                () -> assertThrows(IllegalArgumentException.class, () -> reserva.disponiveis(10)));
    }

    @Test
    @DisplayName("deve recuperar uma quantidade de espaços gastos")
    void deveRecuperarEspacosQuandoHouverGastos() {
        reserva.gastar(1);
        reserva.gastar(1);

        reserva.recuperar(1, 1);

        assertEquals(1, reserva.disponiveis(1));
    }

    @Test
    @DisplayName("não deve recuperar além do máximo")
    void naoDeveRecuperarAlemDoMaximo() {
        reserva.gastar(1);

        reserva.recuperar(1, 5);

        assertEquals(2, reserva.disponiveis(1));
    }

    @Test
    @DisplayName("deve lançar exceção ao recuperar quantidade não positiva")
    void deveLancarExcecaoAoRecuperarQuantidadeNaoPositiva() {
        assertThrows(IllegalArgumentException.class, () -> reserva.recuperar(1, 0));
    }

    @Test
    @DisplayName("deve recuperar todos os espaços no descanso longo")
    void deveRecuperarTodosOsEspacosNoDescansoLongo() {
        reserva.gastar(1);
        reserva.gastar(1);
        reserva.gastar(2);

        reserva.descansoLongo();

        assertAll(
                () -> assertEquals(2, reserva.disponiveis(1)),
                () -> assertEquals(1, reserva.disponiveis(2)),
                () -> assertEquals(0, reserva.gastos(1)));
    }

    @Test
    @DisplayName("deve ajustar o máximo e limitar os gastos ao novo valor")
    void deveAjustarMaximoELimitarGastos() {
        reserva.gastar(1);
        reserva.gastar(1);

        reserva.ajustarMaximo(1, 1);

        assertAll(
                () -> assertEquals(1, reserva.maximo(1)),
                () -> assertEquals(1, reserva.gastos(1)),
                () -> assertEquals(0, reserva.disponiveis(1)));
    }

    @Test
    @DisplayName("deve lançar exceção ao ajustar máximo negativo")
    void deveLancarExcecaoAoAjustarMaximoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> reserva.ajustarMaximo(1, -1));
    }
}
