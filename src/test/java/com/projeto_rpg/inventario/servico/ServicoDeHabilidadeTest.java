package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.HabilidadeJaCadastradaException;
import com.projeto_rpg.inventario.excecao.HabilidadeNaoEncontradaException;
import com.projeto_rpg.inventario.modelo.Habilidade;
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
@Import(ServicoDeHabilidade.class)
class ServicoDeHabilidadeTest {

    @Autowired
    private ServicoDeHabilidade servico;

    @Test
    @DisplayName("deve criar e persistir habilidade com id gerado")
    void deveCriarHabilidadeQuandoDadosForemValidos() {
        Habilidade criada = servico.criar("Bola de fogo", 3, "Explosão de 8d6");

        assertAll(
                () -> assertNotNull(criada.getId()),
                () -> assertEquals("Bola de fogo", criada.getNome()),
                () -> assertEquals(3, criada.getNivel()),
                () -> assertEquals("Explosão de 8d6", criada.getDescricao()));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar habilidade com nome já cadastrado, ignorando caixa")
    void deveLancarExcecaoQuandoNomeDaHabilidadeJaExistir() {
        servico.criar("Luz", 0, null);

        assertThrows(HabilidadeJaCadastradaException.class, () -> servico.criar("LUZ", 0, null));
    }

    @Test
    @DisplayName("deve buscar habilidade pelo id")
    void deveBuscarHabilidadeQuandoIdExistir() {
        Habilidade criada = servico.criar("Mísseis mágicos", 1, null);

        assertEquals("Mísseis mágicos", servico.buscar(criada.getId()).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar habilidade inexistente")
    void deveLancarExcecaoQuandoHabilidadeNaoExistir() {
        assertThrows(HabilidadeNaoEncontradaException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve listar todas as habilidades cadastradas")
    void deveListarHabilidadesQuandoHouverCadastradas() {
        servico.criar("Luz", 0, null);
        servico.criar("Mísseis mágicos", 1, null);

        List<Habilidade> habilidades = servico.listar();

        assertEquals(2, habilidades.size());
    }

    @Test
    @DisplayName("deve listar as habilidades de um nível")
    void deveListarHabilidadesPorNivel() {
        servico.criar("Luz", 0, null);
        servico.criar("Raio de fogo", 1, null);
        servico.criar("Escudo arcano", 1, null);

        assertEquals(2, servico.listarPorNivel(1).size());
    }

    @Test
    @DisplayName("deve atualizar os dados da habilidade")
    void deveAtualizarHabilidadeQuandoDadosForemValidos() {
        Habilidade criada = servico.criar("Raio de fogo", 1, null);

        Habilidade atualizada = servico.atualizar(criada.getId(), "Mísseis mágicos", 2, "Três dardos");

        assertAll(
                () -> assertEquals("Mísseis mágicos", atualizada.getNome()),
                () -> assertEquals(2, servico.buscar(criada.getId()).getNivel()));
    }

    @Test
    @DisplayName("deve permitir atualizar mantendo o próprio nome")
    void devePermitirAtualizarQuandoNomeNaoMudar() {
        Habilidade criada = servico.criar("Raio de fogo", 1, null);

        Habilidade atualizada = servico.atualizar(criada.getId(), "Raio de fogo", 2, null);

        assertEquals(2, atualizada.getNivel());
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar para o nome de outra habilidade")
    void deveLancarExcecaoQuandoAtualizarParaNomeDeOutraHabilidade() {
        servico.criar("Luz", 0, null);
        Habilidade raio = servico.criar("Raio de fogo", 1, null);

        assertThrows(HabilidadeJaCadastradaException.class,
                () -> servico.atualizar(raio.getId(), "Luz", 1, null));
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar habilidade inexistente")
    void deveLancarExcecaoQuandoAtualizarHabilidadeInexistente() {
        assertThrows(HabilidadeNaoEncontradaException.class, () -> servico.atualizar(999L, "Luz", 0, null));
    }

    @Test
    @DisplayName("deve remover a habilidade")
    void deveRemoverHabilidadeQuandoIdExistir() {
        Habilidade criada = servico.criar("Luz", 0, null);

        servico.remover(criada.getId());

        assertThrows(HabilidadeNaoEncontradaException.class, () -> servico.buscar(criada.getId()));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover habilidade inexistente")
    void deveLancarExcecaoQuandoRemoverHabilidadeInexistente() {
        assertThrows(HabilidadeNaoEncontradaException.class, () -> servico.remover(999L));
    }
}
