package com.projeto_rpg.ficha.servico;

import com.projeto_rpg.ficha.excecao.ClasseNaoEncontradaException;
import com.projeto_rpg.ficha.excecao.CompraDePontosInvalidaException;
import com.projeto_rpg.ficha.excecao.PersonagemNaoEncontradoException;
import com.projeto_rpg.ficha.excecao.RacaNaoEncontradaException;
import com.projeto_rpg.ficha.modelo.Atributo;
import com.projeto_rpg.ficha.modelo.Atributos;
import com.projeto_rpg.ficha.modelo.Classe;
import com.projeto_rpg.ficha.modelo.Personagem;
import com.projeto_rpg.ficha.modelo.Raca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import({ServicoDePersonagem.class, ServicoDeClasse.class, ServicoDeRaca.class})
class ServicoDePersonagemTest {

    private static final Atributos COMPRA_VALIDA = new Atributos(15, 14, 13, 12, 10, 8);

    @Autowired
    private ServicoDePersonagem servico;

    @Autowired
    private ServicoDeClasse servicoDeClasse;

    @Autowired
    private ServicoDeRaca servicoDeRaca;

    private Classe guerreiro;
    private Raca humano;

    @BeforeEach
    void prepararCadastros() {
        guerreiro = servicoDeClasse.criar("Guerreiro", 10);
        humano = servicoDeRaca.criar("Humano", 30);
    }

    @Test
    @DisplayName("deve criar personagem no nível 1 a partir de uma compra de pontos válida")
    void deveCriarPersonagemQuandoCompraDePontosForValida() {
        Personagem personagem = servico.criar("Aragorn", guerreiro.getId(), humano.getId(), COMPRA_VALIDA);

        assertAll(
                () -> assertNotNull(personagem.getId()),
                () -> assertEquals("Aragorn", personagem.getNome()),
                () -> assertEquals(guerreiro.getId(), personagem.getClasse().getId()),
                () -> assertEquals(humano.getId(), personagem.getRacaId()),
                () -> assertEquals(1, personagem.getNivel()),
                () -> assertEquals(2, personagem.getBonusDeProficiencia()),
                () -> assertEquals(11, personagem.getPvMaximo()),
                () -> assertEquals(2, personagem.modificador(Atributo.FORCA)));
    }

    @Test
    @DisplayName("deve lançar exceção quando a distribuição gastar mais de 27 pontos")
    void deveLancarExcecaoQuandoCompraDePontosForInvalida() {
        Atributos acimaDoLimite = new Atributos(15, 15, 15, 15, 8, 8);

        assertThrows(CompraDePontosInvalidaException.class,
                () -> servico.criar("Aragorn", guerreiro.getId(), humano.getId(), acimaDoLimite));
    }

    @Test
    @DisplayName("deve lançar exceção quando a classe não existir")
    void deveLancarExcecaoQuandoClasseDoPersonagemNaoExistir() {
        assertThrows(ClasseNaoEncontradaException.class,
                () -> servico.criar("Aragorn", 999L, humano.getId(), COMPRA_VALIDA));
    }

    @Test
    @DisplayName("deve lançar exceção quando a raça não existir")
    void deveLancarExcecaoQuandoRacaDoPersonagemNaoExistir() {
        assertThrows(RacaNaoEncontradaException.class,
                () -> servico.criar("Aragorn", guerreiro.getId(), 999L, COMPRA_VALIDA));
    }

    @Test
    @DisplayName("deve buscar personagem pelo id")
    void deveBuscarPersonagemQuandoIdExistir() {
        Personagem criado = servico.criar("Aragorn", guerreiro.getId(), humano.getId(), COMPRA_VALIDA);

        assertEquals("Aragorn", servico.buscar(criado.getId()).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar personagem inexistente")
    void deveLancarExcecaoQuandoPersonagemNaoExistir() {
        assertThrows(PersonagemNaoEncontradoException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve listar os personagens cadastrados")
    void deveListarPersonagensQuandoHouverCadastrados() {
        servico.criar("Aragorn", guerreiro.getId(), humano.getId(), COMPRA_VALIDA);
        servico.criar("Boromir", guerreiro.getId(), humano.getId(), COMPRA_VALIDA);

        assertEquals(2, servico.listar().size());
    }

    @Test
    @DisplayName("deve registrar o XP ganho e recalcular o nível")
    void deveSubirDeNivelQuandoGanharXpSuficiente() {
        Personagem criado = servico.criar("Aragorn", guerreiro.getId(), humano.getId(), COMPRA_VALIDA);

        Personagem atualizado = servico.ganharXp(criado.getId(), 300);

        assertAll(
                () -> assertEquals(300, atualizado.getXp()),
                () -> assertEquals(2, atualizado.getNivel()),
                () -> assertEquals(18, atualizado.getPvMaximo()),
                () -> assertEquals(300, servico.buscar(criado.getId()).getXp()));
    }

    @Test
    @DisplayName("deve lançar exceção ao dar XP para personagem inexistente")
    void deveLancarExcecaoQuandoGanharXpDePersonagemInexistente() {
        assertThrows(PersonagemNaoEncontradoException.class, () -> servico.ganharXp(999L, 100));
    }
}
