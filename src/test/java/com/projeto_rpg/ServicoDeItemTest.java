package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.ItemEmUsoException;
import com.projeto_rpg.inventario.excecao.ItemJaCadastradoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEncontradoException;
import com.projeto_rpg.inventario.modelo.Inventario;
import com.projeto_rpg.inventario.modelo.InventarioRepository;
import com.projeto_rpg.inventario.modelo.Item;
import com.projeto_rpg.inventario.modelo.TipoDeArmadura;
import com.projeto_rpg.inventario.modelo.TipoDeItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(ServicoDeItem.class)
class ServicoDeItemTest {

    @Autowired
    private ServicoDeItem servico;

    @Autowired
    private InventarioRepository inventarios;

    @Test
    @DisplayName("deve criar e persistir arma com id gerado")
    void deveCriarArmaQuandoDadosForemValidos() {
        Item criada = servico.criarArma("Espada longa", 3.0);

        assertAll(
                () -> assertNotNull(criada.getId()),
                () -> assertEquals("Espada longa", criada.getNome()),
                () -> assertEquals(TipoDeItem.ARMA, criada.getTipo()),
                () -> assertEquals(3.0, criada.getPeso()));
    }

    @Test
    @DisplayName("deve criar armadura e escudo")
    void deveCriarArmaduraEEscudoQuandoDadosForemValidos() {
        Item armadura = servico.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        Item escudo = servico.criarEscudo("Escudo", 6.0);

        assertAll(
                () -> assertEquals(14, armadura.getCaBase()),
                () -> assertEquals(TipoDeArmadura.MEDIA, armadura.getTipoDeArmadura()),
                () -> assertEquals(TipoDeItem.ESCUDO, escudo.getTipo()));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar item com nome já cadastrado, ignorando caixa")
    void deveLancarExcecaoQuandoNomeDoItemJaExistir() {
        servico.criarArma("Espada longa", 3.0);

        assertThrows(ItemJaCadastradoException.class, () -> servico.criarArma("ESPADA LONGA", 3.0));
    }

    @Test
    @DisplayName("deve buscar item pelo id")
    void deveBuscarItemQuandoIdExistir() {
        Item criada = servico.criarConsumivel("Poção de cura", 0.5);

        assertEquals("Poção de cura", servico.buscar(criada.getId()).getNome());
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar item inexistente")
    void deveLancarExcecaoQuandoItemNaoExistir() {
        assertThrows(ItemNaoEncontradoException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve listar todos os itens cadastrados")
    void deveListarItensQuandoHouverCadastrados() {
        servico.criarArma("Espada longa", 3.0);
        servico.criarEscudo("Escudo", 6.0);

        List<Item> itens = servico.listar();

        assertEquals(2, itens.size());
    }

    @Test
    @DisplayName("deve listar os itens de um tipo")
    void deveListarItensPorTipo() {
        servico.criarArma("Espada longa", 3.0);
        servico.criarArma("Adaga", 1.0);
        servico.criarEscudo("Escudo", 6.0);

        assertEquals(2, servico.listarPorTipo(TipoDeItem.ARMA).size());
    }

    @Test
    @DisplayName("deve atualizar os dados do item")
    void deveAtualizarItemQuandoDadosForemValidos() {
        Item criada = servico.criarArma("Espada longa", 3.0);

        Item atualizada = servico.atualizar(criada.getId(), "Espada curta", TipoDeItem.ARMA, 2.0, 0, null);

        assertAll(
                () -> assertEquals("Espada curta", atualizada.getNome()),
                () -> assertEquals(2.0, servico.buscar(criada.getId()).getPeso()));
    }

    @Test
    @DisplayName("deve permitir atualizar mantendo o próprio nome")
    void devePermitirAtualizarQuandoNomeNaoMudar() {
        Item criada = servico.criarArma("Espada longa", 3.0);

        Item atualizada = servico.atualizar(criada.getId(), "Espada longa", TipoDeItem.ARMA, 4.0, 0, null);

        assertEquals(4.0, atualizada.getPeso());
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar para o nome de outro item")
    void deveLancarExcecaoQuandoAtualizarParaNomeDeOutroItem() {
        servico.criarArma("Espada longa", 3.0);
        Item adaga = servico.criarArma("Adaga", 1.0);

        assertThrows(ItemJaCadastradoException.class,
                () -> servico.atualizar(adaga.getId(), "Espada longa", TipoDeItem.ARMA, 1.0, 0, null));
    }

    @Test
    @DisplayName("deve desequipar o item equipado quando o tipo dele mudar, sem quebrar o cálculo da CA")
    void deveDesequiparItemEquipadoQuandoTipoMudar() {
        Item cota = servico.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        Inventario inventario = new Inventario(1L);
        inventario.adicionarItem(cota);
        inventario.equipar(cota);
        inventarios.save(inventario);

        servico.atualizar(cota.getId(), "Cota de malha", TipoDeItem.GERAL, 30.0, 0, null);

        Inventario atualizado = inventarios.findByPersonagemId(1L).orElseThrow();
        assertAll(
                () -> assertNull(atualizado.getArmaduraEquipada()),
                () -> assertEquals(1, atualizado.getItens().size()),
                () -> assertEquals(12, atualizado.caFinal(2)));
    }

    @Test
    @DisplayName("deve lançar exceção ao atualizar item inexistente")
    void deveLancarExcecaoQuandoAtualizarItemInexistente() {
        assertThrows(ItemNaoEncontradoException.class,
                () -> servico.atualizar(999L, "Adaga", TipoDeItem.ARMA, 1.0, 0, null));
    }

    @Test
    @DisplayName("deve remover o item")
    void deveRemoverItemQuandoIdExistir() {
        Item criada = servico.criarArma("Espada longa", 3.0);

        servico.remover(criada.getId());

        assertThrows(ItemNaoEncontradoException.class, () -> servico.buscar(criada.getId()));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover item inexistente")
    void deveLancarExcecaoQuandoRemoverItemInexistente() {
        assertThrows(ItemNaoEncontradoException.class, () -> servico.remover(999L));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover item guardado em algum inventário")
    void deveLancarExcecaoQuandoRemoverItemEmUso() {
        Item espada = servico.criarArma("Espada longa", 3.0);
        Inventario inventario = new Inventario(1L);
        inventario.adicionarItem(espada);
        inventarios.save(inventario);

        assertAll(
                () -> assertThrows(ItemEmUsoException.class, () -> servico.remover(espada.getId())),
                () -> assertEquals("Espada longa", servico.buscar(espada.getId()).getNome()));
    }
}
