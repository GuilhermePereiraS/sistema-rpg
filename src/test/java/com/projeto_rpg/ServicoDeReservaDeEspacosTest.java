package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.EspacoDeHabilidadeInsuficienteException;
import com.projeto_rpg.inventario.excecao.HabilidadeNaoEncontradaException;
import com.projeto_rpg.inventario.excecao.ReservaDeEspacosJaExistenteException;
import com.projeto_rpg.inventario.excecao.ReservaDeEspacosNaoEncontradaException;
import com.projeto_rpg.inventario.modelo.Habilidade;
import com.projeto_rpg.inventario.modelo.ReservaDeEspacos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ServicoDeReservaDeEspacos.class, ServicoDeHabilidade.class})
class ServicoDeReservaDeEspacosTest {

    private static final long PERSONAGEM = 1L;
    private static final List<Integer> MAXIMOS = List.of(2, 1, 0, 0, 0, 0, 0, 0, 0);

    @Autowired
    private ServicoDeReservaDeEspacos servico;

    @Autowired
    private ServicoDeHabilidade habilidades;

    @Autowired
    private TestEntityManager entityManager;

    private void novaChamada() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("deve criar e persistir a reserva de espaços")
    void deveCriarReservaQuandoDadosForemValidos() {
        ReservaDeEspacos reserva = servico.criar(PERSONAGEM, MAXIMOS);

        assertAll(
                () -> assertNotNull(reserva.getId()),
                () -> assertEquals(PERSONAGEM, reserva.getPersonagemId()),
                () -> assertEquals(2, reserva.maximo(1)),
                () -> assertEquals(1, reserva.maximo(2)));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar duas reservas para o mesmo personagem")
    void deveLancarExcecaoQuandoReservaJaExistir() {
        servico.criar(PERSONAGEM, MAXIMOS);

        assertThrows(ReservaDeEspacosJaExistenteException.class, () -> servico.criar(PERSONAGEM, MAXIMOS));
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar reserva inexistente")
    void deveLancarExcecaoQuandoReservaNaoExistir() {
        assertThrows(ReservaDeEspacosNaoEncontradaException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve gastar espaço e reduzir os disponíveis")
    void deveGastarEspacoQuandoHouverDisponivel() {
        servico.criar(PERSONAGEM, MAXIMOS);

        servico.gastar(PERSONAGEM, 1);

        assertEquals(1, servico.disponiveis(PERSONAGEM, 1));
    }

    @Test
    @DisplayName("deve lançar exceção ao gastar espaço sem disponibilidade")
    void deveLancarExcecaoAoGastarSemEspaco() {
        servico.criar(PERSONAGEM, MAXIMOS);
        servico.gastar(PERSONAGEM, 1);
        servico.gastar(PERSONAGEM, 1);

        assertThrows(EspacoDeHabilidadeInsuficienteException.class, () -> servico.gastar(PERSONAGEM, 1));
    }

    @Test
    @DisplayName("deve persistir os espaços gastos entre chamadas")
    void devePersistirEspacosGastosEntreChamadas() {
        servico.criar(PERSONAGEM, MAXIMOS);

        servico.gastar(PERSONAGEM, 1);
        novaChamada();

        assertEquals(1, servico.disponiveis(PERSONAGEM, 1));
    }

    @Test
    @DisplayName("deve consumir um espaço ao usar uma habilidade do próprio nível")
    void deveConsumirEspacoAoUsarHabilidade() {
        Habilidade bolaDeFogo = habilidades.criar("Bola de fogo", 3, null);
        servico.criar(PERSONAGEM, List.of(2, 1, 1, 0, 0, 0, 0, 0, 0));

        servico.usarHabilidade(PERSONAGEM, bolaDeFogo.getId());

        assertAll(
                () -> assertEquals(0, servico.disponiveis(PERSONAGEM, 3)),
                () -> assertEquals(2, servico.disponiveis(PERSONAGEM, 1)));
    }

    @Test
    @DisplayName("não deve consumir espaço ao usar um truque")
    void naoDeveConsumirEspacoAoUsarTruque() {
        Habilidade luz = habilidades.criar("Luz", 0, null);
        servico.criar(PERSONAGEM, MAXIMOS);

        servico.usarHabilidade(PERSONAGEM, luz.getId());

        assertEquals(2, servico.disponiveis(PERSONAGEM, 1));
    }

    @Test
    @DisplayName("deve lançar exceção ao usar habilidade inexistente")
    void deveLancarExcecaoAoUsarHabilidadeInexistente() {
        servico.criar(PERSONAGEM, MAXIMOS);

        assertThrows(HabilidadeNaoEncontradaException.class, () -> servico.usarHabilidade(PERSONAGEM, 999L));
    }

    @Test
    @DisplayName("deve lançar exceção ao usar habilidade sem espaço disponível")
    void deveLancarExcecaoAoUsarHabilidadeSemEspaco() {
        Habilidade bolaDeFogo = habilidades.criar("Bola de fogo", 3, null);
        servico.criar(PERSONAGEM, List.of(2, 1, 1, 0, 0, 0, 0, 0, 0));
        servico.gastar(PERSONAGEM, 3);

        assertThrows(EspacoDeHabilidadeInsuficienteException.class,
                () -> servico.usarHabilidade(PERSONAGEM, bolaDeFogo.getId()));
    }

    @Test
    @DisplayName("deve recuperar todos os espaços no descanso longo")
    void deveRecuperarTodosOsEspacosNoDescansoLongo() {
        servico.criar(PERSONAGEM, MAXIMOS);
        servico.gastar(PERSONAGEM, 1);
        servico.gastar(PERSONAGEM, 1);
        servico.gastar(PERSONAGEM, 2);
        novaChamada();

        servico.descansoLongo(PERSONAGEM);

        assertAll(
                () -> assertEquals(2, servico.disponiveis(PERSONAGEM, 1)),
                () -> assertEquals(1, servico.disponiveis(PERSONAGEM, 2)));
    }

    @Test
    @DisplayName("deve recuperar uma quantidade de espaços gastos")
    void deveRecuperarEspacosQuandoHouverGastos() {
        servico.criar(PERSONAGEM, MAXIMOS);
        servico.gastar(PERSONAGEM, 1);
        servico.gastar(PERSONAGEM, 1);

        servico.recuperar(PERSONAGEM, 1, 1);

        assertEquals(1, servico.disponiveis(PERSONAGEM, 1));
    }

    @Test
    @DisplayName("deve ajustar o máximo de espaços de um nível")
    void deveAjustarMaximoDeEspacos() {
        servico.criar(PERSONAGEM, MAXIMOS);

        servico.ajustarMaximo(PERSONAGEM, 1, 3);

        assertEquals(3, servico.buscar(PERSONAGEM).maximo(1));
    }
}
