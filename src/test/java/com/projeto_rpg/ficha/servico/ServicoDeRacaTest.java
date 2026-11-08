package com.projeto_rpg.ficha.servico;

import com.projeto_rpg.ficha.excecao.NomeJaCadastradoException;
import com.projeto_rpg.ficha.excecao.RacaNaoEncontradaException;
import com.projeto_rpg.ficha.modelo.Raca;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(ServicoDeRaca.class)
class ServicoDeRacaTest {

    @Autowired
    private ServicoDeRaca servico;

    @Test
    @DisplayName("deve criar e persistir raça com id gerado")
    void deveCriarRacaQuandoDadosForemValidos() {
        Raca criada = servico.criar("Anão", 25);

        assertAll(
                () -> assertNotNull(criada.getId()),
                () -> assertEquals("Anão", criada.getNome()),
                () -> assertEquals(25, criada.getDeslocamento()));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar raça com nome já cadastrado, ignorando caixa")
    void deveLancarExcecaoQuandoNomeDaRacaJaExistir() {
        servico.criar("Elfo", 30);

        assertThrows(NomeJaCadastradoException.class, () -> servico.criar("ELFO", 35));
    }

    @Test
    @DisplayName("deve buscar raça pelo id")
    void deveBuscarRacaQuandoIdExistir() {
        Raca criada = servico.criar("Humano", 30);

        assertEquals("Humano", servico.buscar(criada.getId()).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar raça inexistente")
    void deveLancarExcecaoQuandoRacaNaoExistir() {
        assertThrows(RacaNaoEncontradaException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve listar todas as raças cadastradas")
    void deveListarRacasQuandoHouverCadastradas() {
        servico.criar("Humano", 30);
        servico.criar("Elfo", 30);

        List<Raca> racas = servico.listar();

        assertEquals(2, racas.size());
    }

    @Test
    @DisplayName("deve atualizar nome e deslocamento da raça")
    void deveAtualizarRacaQuandoDadosForemValidos() {
        Raca criada = servico.criar("Humano", 30);

        Raca atualizada = servico.atualizar(criada.getId(), "Meio-elfo", 35);

        assertAll(
                () -> assertEquals("Meio-elfo", atualizada.getNome()),
                () -> assertEquals(35, servico.buscar(criada.getId()).getDeslocamento()));
    }

    @Test
    @DisplayName("deve permitir atualizar mantendo o próprio nome")
    void devePermitirAtualizarQuandoNomeDaRacaNaoMudar() {
        Raca criada = servico.criar("Humano", 30);

        Raca atualizada = servico.atualizar(criada.getId(), "Humano", 35);

        assertEquals(35, atualizada.getDeslocamento());
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar para o nome de outra raça")
    void deveLancarExcecaoQuandoAtualizarParaNomeDeOutraRaca() {
        servico.criar("Humano", 30);
        Raca elfo = servico.criar("Elfo", 30);

        assertThrows(NomeJaCadastradoException.class, () -> servico.atualizar(elfo.getId(), "Humano", 30));
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar raça inexistente")
    void deveLancarExcecaoQuandoAtualizarRacaInexistente() {
        assertThrows(RacaNaoEncontradaException.class, () -> servico.atualizar(999L, "Elfo", 30));
    }

    @Test
    @DisplayName("deve remover a raça")
    void deveRemoverRacaQuandoIdExistir() {
        Raca criada = servico.criar("Humano", 30);

        servico.remover(criada.getId());

        assertThrows(RacaNaoEncontradaException.class, () -> servico.buscar(criada.getId()));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover raça inexistente")
    void deveLancarExcecaoQuandoRemoverRacaInexistente() {
        assertThrows(RacaNaoEncontradaException.class, () -> servico.remover(999L));
    }
}
