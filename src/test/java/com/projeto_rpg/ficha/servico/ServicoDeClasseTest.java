package com.projeto_rpg.ficha.servico;

import com.projeto_rpg.ficha.excecao.ClasseEmUsoException;
import com.projeto_rpg.ficha.excecao.ClasseNaoEncontradaException;
import com.projeto_rpg.ficha.excecao.NomeJaCadastradoException;
import com.projeto_rpg.ficha.modelo.Atributos;
import com.projeto_rpg.ficha.modelo.Classe;
import com.projeto_rpg.ficha.modelo.Personagem;
import com.projeto_rpg.ficha.modelo.PersonagemRepository;
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
@Import(ServicoDeClasse.class)
class ServicoDeClasseTest {

    @Autowired
    private ServicoDeClasse servico;

    @Autowired
    private PersonagemRepository personagens;

    @Test
    @DisplayName("deve criar e persistir classe com id gerado")
    void deveCriarClasseQuandoDadosForemValidos() {
        Classe criada = servico.criar("Guerreiro", 10);

        assertAll(
                () -> assertNotNull(criada.getId()),
                () -> assertEquals("Guerreiro", criada.getNome()),
                () -> assertEquals(10, criada.getDadoDeVida()));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar classe com nome já cadastrado, ignorando caixa")
    void deveLancarExcecaoQuandoNomeDaClasseJaExistir() {
        servico.criar("Guerreiro", 10);

        assertThrows(NomeJaCadastradoException.class, () -> servico.criar("guerreiro", 8));
    }

    @Test
    @DisplayName("deve buscar classe pelo id")
    void deveBuscarClasseQuandoIdExistir() {
        Classe criada = servico.criar("Mago", 6);

        assertEquals("Mago", servico.buscar(criada.getId()).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar classe inexistente")
    void deveLancarExcecaoQuandoClasseNaoExistir() {
        assertThrows(ClasseNaoEncontradaException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve listar todas as classes cadastradas")
    void deveListarClassesQuandoHouverCadastradas() {
        servico.criar("Guerreiro", 10);
        servico.criar("Mago", 6);

        List<Classe> classes = servico.listar();

        assertEquals(2, classes.size());
    }

    @Test
    @DisplayName("deve atualizar nome e dado de vida da classe")
    void deveAtualizarClasseQuandoDadosForemValidos() {
        Classe criada = servico.criar("Guerreiro", 10);

        Classe atualizada = servico.atualizar(criada.getId(), "Bárbaro", 12);

        assertAll(
                () -> assertEquals("Bárbaro", atualizada.getNome()),
                () -> assertEquals(12, servico.buscar(criada.getId()).getDadoDeVida()));
    }

    @Test
    @DisplayName("deve permitir atualizar mantendo o próprio nome")
    void devePermitirAtualizarQuandoNomeNaoMudar() {
        Classe criada = servico.criar("Guerreiro", 10);

        Classe atualizada = servico.atualizar(criada.getId(), "Guerreiro", 12);

        assertEquals(12, atualizada.getDadoDeVida());
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar para o nome de outra classe")
    void deveLancarExcecaoQuandoAtualizarParaNomeDeOutraClasse() {
        servico.criar("Guerreiro", 10);
        Classe mago = servico.criar("Mago", 6);

        assertThrows(NomeJaCadastradoException.class,
                () -> servico.atualizar(mago.getId(), "Guerreiro", 6));
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar classe inexistente")
    void deveLancarExcecaoQuandoAtualizarClasseInexistente() {
        assertThrows(ClasseNaoEncontradaException.class, () -> servico.atualizar(999L, "Bárbaro", 12));
    }

    @Test
    @DisplayName("deve remover a classe")
    void deveRemoverClasseQuandoIdExistir() {
        Classe criada = servico.criar("Guerreiro", 10);

        servico.remover(criada.getId());

        assertThrows(ClasseNaoEncontradaException.class, () -> servico.buscar(criada.getId()));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover classe inexistente")
    void deveLancarExcecaoQuandoRemoverClasseInexistente() {
        assertThrows(ClasseNaoEncontradaException.class, () -> servico.remover(999L));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover classe usada por algum personagem")
    void deveLancarExcecaoQuandoRemoverClasseEmUso() {
        Classe guerreiro = servico.criar("Guerreiro", 10);
        personagens.save(new Personagem("Aragorn", guerreiro, 1L, new Atributos(10, 10, 10, 10, 10, 10)));

        assertAll(
                () -> assertThrows(ClasseEmUsoException.class, () -> servico.remover(guerreiro.getId())),
                () -> assertEquals("Guerreiro", servico.buscar(guerreiro.getId()).getNome()));
    }
}
