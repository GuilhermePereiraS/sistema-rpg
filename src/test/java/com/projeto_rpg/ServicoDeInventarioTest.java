package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.InventarioJaExistenteException;
import com.projeto_rpg.inventario.excecao.InventarioNaoEncontradoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEncontradoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEquipadoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEquipavelException;
import com.projeto_rpg.inventario.excecao.ItemNaoPertenceAoInventarioException;
import com.projeto_rpg.inventario.modelo.Inventario;
import com.projeto_rpg.inventario.modelo.Item;
import com.projeto_rpg.inventario.modelo.TipoDeArmadura;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({ServicoDeInventario.class, ServicoDeItem.class})
class ServicoDeInventarioTest {

    /** O inventário guarda apenas a referência ao personagem, então basta um id. */
    private static final Long PERSONAGEM_ID = 1L;

    /** Força 16 -> capacidade de carga 240 lb. */
    private static final int FORCA = 16;

    /** Destreza 14 -> modificador +2. */
    private static final int MODIFICADOR_DE_DESTREZA = 2;

    @Autowired
    private ServicoDeInventario servico;

    @Autowired
    private ServicoDeItem itens;

    @Autowired
    private TestEntityManager entityManager;

    private void novaChamada() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("deve criar inventário para o personagem")
    void deveCriarInventarioQuandoNaoExistirInventario() {
        Inventario inventario = servico.criar(PERSONAGEM_ID);

        assertAll(
                () -> assertNotNull(inventario.getId()),
                () -> assertEquals(PERSONAGEM_ID, inventario.getPersonagemId()),
                () -> assertTrue(inventario.getItens().isEmpty()));
    }

    @Test
    @DisplayName("deve lançar exceção ao criar dois inventários para o mesmo personagem")
    void deveLancarExcecaoQuandoInventarioJaExistir() {
        servico.criar(PERSONAGEM_ID);

        assertThrows(InventarioJaExistenteException.class, () -> servico.criar(PERSONAGEM_ID));
    }

    @Test
    @DisplayName("deve lançar exceção ao buscar inventário inexistente")
    void deveLancarExcecaoQuandoInventarioNaoExistir() {
        assertThrows(InventarioNaoEncontradoException.class, () -> servico.buscar(999L));
    }

    @Test
    @DisplayName("deve listar os inventários cadastrados")
    void deveListarInventariosQuandoExistirem() {
        servico.criar(PERSONAGEM_ID);
        servico.criar(2L);

        assertEquals(2, servico.listar().size());
    }

    @Test
    @DisplayName("deve guardar o item no inventário")
    void deveGuardarItemQuandoInventarioExistir() {
        Item espada = itens.criarArma("Espada longa", 3.0);
        servico.criar(PERSONAGEM_ID);

        Inventario inventario = servico.adicionarItem(PERSONAGEM_ID, espada.getId());

        assertAll(
                () -> assertEquals(1, inventario.getItens().size()),
                () -> assertEquals("Espada longa", inventario.getItens().getFirst().getNome()));
    }

    @Test
    @DisplayName("deve lançar exceção ao guardar item inexistente")
    void deveLancarExcecaoAoGuardarItemInexistente() {
        servico.criar(PERSONAGEM_ID);

        assertThrows(ItemNaoEncontradoException.class, () -> servico.adicionarItem(PERSONAGEM_ID, 999L));
    }

    @Test
    @DisplayName("deve equipar armadura e escudo e somar tudo na CA final")
    void deveCalcularCaFinalComArmaduraEEscudo() {
        Item cota = itens.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        Item escudo = itens.criarEscudo("Escudo", 6.0);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, cota.getId());
        servico.adicionarItem(PERSONAGEM_ID, escudo.getId());

        servico.equipar(PERSONAGEM_ID, cota.getId());
        servico.equipar(PERSONAGEM_ID, escudo.getId());

        // 14 (base) + 2 (Destreza limitada) + 2 (escudo)
        assertEquals(18, servico.caFinal(PERSONAGEM_ID, MODIFICADOR_DE_DESTREZA));
    }

    @Test
    @DisplayName("deve recalcular a CA ao desequipar escudo e armadura")
    void deveRecalcularCaAoDesequipar() {
        Item cota = itens.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        Item escudo = itens.criarEscudo("Escudo", 6.0);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, cota.getId());
        servico.adicionarItem(PERSONAGEM_ID, escudo.getId());
        servico.equipar(PERSONAGEM_ID, cota.getId());
        servico.equipar(PERSONAGEM_ID, escudo.getId());

        servico.desequipar(PERSONAGEM_ID, escudo.getId());
        int semEscudo = servico.caFinal(PERSONAGEM_ID, MODIFICADOR_DE_DESTREZA);
        servico.desequipar(PERSONAGEM_ID, cota.getId());
        int semArmadura = servico.caFinal(PERSONAGEM_ID, MODIFICADOR_DE_DESTREZA);

        assertAll(
                () -> assertEquals(16, semEscudo),
                () -> assertEquals(12, semArmadura));
    }

    @Test
    @DisplayName("deve persistir o equipamento entre chamadas")
    void devePersistirEquipamentoEntreChamadas() {
        Item cota = itens.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, cota.getId());
        servico.equipar(PERSONAGEM_ID, cota.getId());

        novaChamada();

        assertEquals(cota.getId(), servico.buscar(PERSONAGEM_ID).getArmaduraEquipada().getId());
    }

    @Test
    @DisplayName("deve lançar exceção ao equipar item que não está guardado")
    void deveLancarExcecaoAoEquiparItemNaoGuardado() {
        Item espada = itens.criarArma("Espada longa", 3.0);
        servico.criar(PERSONAGEM_ID);

        assertThrows(ItemNaoPertenceAoInventarioException.class,
                () -> servico.equipar(PERSONAGEM_ID, espada.getId()));
    }

    @Test
    @DisplayName("deve lançar exceção ao equipar item que não é equipável")
    void deveLancarExcecaoAoEquiparItemNaoEquipavel() {
        Item pocao = itens.criarConsumivel("Poção de cura", 0.5);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, pocao.getId());

        assertThrows(ItemNaoEquipavelException.class, () -> servico.equipar(PERSONAGEM_ID, pocao.getId()));
    }

    @Test
    @DisplayName("deve lançar exceção ao desequipar item que não está equipado")
    void deveLancarExcecaoAoDesequiparItemNaoEquipado() {
        Item cota = itens.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, cota.getId());

        assertThrows(ItemNaoEquipadoException.class, () -> servico.desequipar(PERSONAGEM_ID, cota.getId()));
    }

    @Test
    @DisplayName("deve desequipar o item ao removê-lo do inventário")
    void deveDesequiparItemAoRemover() {
        Item cota = itens.criarArmadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, cota.getId());
        servico.equipar(PERSONAGEM_ID, cota.getId());

        Inventario inventario = servico.removerItem(PERSONAGEM_ID, cota.getId());

        assertAll(
                () -> assertNull(inventario.getArmaduraEquipada()),
                () -> assertTrue(inventario.getItens().isEmpty()));
    }

    @Test
    @DisplayName("deve calcular a capacidade de carga a partir da Força informada")
    void deveCalcularCapacidadeDeCargaAPartirDaForca() {
        servico.criar(PERSONAGEM_ID);

        assertEquals(240, servico.capacidadeDeCarga(PERSONAGEM_ID, FORCA));
    }

    @Test
    @DisplayName("deve indicar sobrecarga quando o peso total passar da capacidade")
    void deveIndicarSobrecargaQuandoPesoPassarDaCapacidade() {
        Item pedra = itens.criarGeral("Pedra gigante", 300.0);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, pedra.getId());

        assertAll(
                () -> assertEquals(300.0, servico.pesoTotal(PERSONAGEM_ID)),
                () -> assertTrue(servico.estaSobrecarregado(PERSONAGEM_ID, FORCA)));
    }

    @Test
    @DisplayName("não deve indicar sobrecarga quando o peso estiver dentro da capacidade")
    void naoDeveIndicarSobrecargaQuandoPesoEstiverDentro() {
        Item espada = itens.criarArma("Espada longa", 3.0);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, espada.getId());

        assertFalse(servico.estaSobrecarregado(PERSONAGEM_ID, FORCA));
    }

    @Test
    @DisplayName("deve usar o equipamento persistido no cálculo da CA entre chamadas")
    void deveUsarEquipamentoPersistidoNoCalculoDaCa() {
        Item placas = itens.criarArmadura("Armadura de placas", 65.0, 16, TipoDeArmadura.PESADA);
        servico.criar(PERSONAGEM_ID);
        servico.adicionarItem(PERSONAGEM_ID, placas.getId());
        servico.equipar(PERSONAGEM_ID, placas.getId());

        novaChamada();

        // pesada ignora a Destreza: apenas a base
        assertEquals(16, servico.caFinal(PERSONAGEM_ID, MODIFICADOR_DE_DESTREZA));
    }
}
