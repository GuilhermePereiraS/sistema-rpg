package com.projeto_rpg.inventario.modelo;

import com.projeto_rpg.inventario.excecao.ItemNaoEquipadoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEquipavelException;
import com.projeto_rpg.inventario.excecao.ItemNaoPertenceAoInventarioException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventarioTest {

    private static final long PERSONAGEM = 1L;

    private Inventario inventario;
    private Item espada;
    private Item cota;
    private Item escudo;
    private Item pocao;

    @BeforeEach
    void prepararDados() {
        inventario = new Inventario(PERSONAGEM);
        espada = Item.arma("Espada longa", 3.0);
        cota = Item.armadura("Cota de malha", 30.0, 14, TipoDeArmadura.MEDIA);
        escudo = Item.escudo("Escudo", 6.0);
        pocao = Item.consumivel("Poção de cura", 0.5);
    }

    @Test
    @DisplayName("deve lançar exceção quando o personagem for nulo")
    void deveLancarExcecaoQuandoPersonagemForNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Inventario(null));
    }

    @Test
    @DisplayName("deve guardar o item sem duplicar o mesmo item")
    void deveGuardarItemSemDuplicar() {
        inventario.adicionarItem(espada);
        inventario.adicionarItem(espada);

        assertEquals(1, inventario.getItens().size());
    }

    @Test
    @DisplayName("deve lançar exceção ao guardar item nulo")
    void deveLancarExcecaoAoGuardarItemNulo() {
        assertThrows(IllegalArgumentException.class, () -> inventario.adicionarItem(null));
    }

    @Test
    @DisplayName("deve remover o item guardado")
    void deveRemoverItemQuandoEstiverGuardado() {
        inventario.adicionarItem(espada);

        inventario.removerItem(espada);

        assertTrue(inventario.getItens().isEmpty());
    }

    @Test
    @DisplayName("deve lançar exceção ao remover item que não está guardado")
    void deveLancarExcecaoAoRemoverItemAusente() {
        assertThrows(ItemNaoPertenceAoInventarioException.class, () -> inventario.removerItem(espada));
    }

    @Test
    @DisplayName("deve lançar exceção ao remover item nulo")
    void deveLancarExcecaoAoRemoverItemNulo() {
        assertThrows(ItemNaoPertenceAoInventarioException.class, () -> inventario.removerItem(null));
    }

    @Test
    @DisplayName("deve desequipar o item que mudou de tipo e não cabe mais no espaço, mantendo-o guardado")
    void deveDesequiparItemQuandoTipoMudarENaoCaberMaisNoEspaco() {
        inventario.adicionarItem(cota);
        inventario.adicionarItem(espada);
        inventario.equipar(cota);
        inventario.equipar(espada);

        cota.atualizar("Cota de malha", TipoDeItem.GERAL, 30.0, 0, null);
        inventario.revalidarEquipamento();

        assertAll(
                () -> assertNull(inventario.getArmaduraEquipada()),
                () -> assertEquals(espada, inventario.getArmaEquipada()),
                () -> assertEquals(2, inventario.getItens().size()),
                () -> assertEquals(13, inventario.caFinal(3)));
    }

    @Test
    @DisplayName("deve manter equipado o que continua do mesmo tipo ao revalidar o equipamento")
    void deveManterEquipadoQuandoTipoNaoMudar() {
        inventario.adicionarItem(cota);
        inventario.equipar(cota);

        inventario.revalidarEquipamento();

        assertEquals(cota, inventario.getArmaduraEquipada());
    }

    @Test
    @DisplayName("deve equipar arma, armadura e escudo nos espaços correspondentes")
    void deveEquiparItensNosEspacosCorrespondentes() {
        inventario.adicionarItem(espada);
        inventario.adicionarItem(cota);
        inventario.adicionarItem(escudo);

        inventario.equipar(espada);
        inventario.equipar(cota);
        inventario.equipar(escudo);

        assertAll(
                () -> assertEquals(espada, inventario.getArmaEquipada()),
                () -> assertEquals(cota, inventario.getArmaduraEquipada()),
                () -> assertEquals(escudo, inventario.getEscudoEquipado()));
    }

    @Test
    @DisplayName("deve lançar exceção ao equipar item que não está guardado")
    void deveLancarExcecaoAoEquiparItemAusente() {
        assertThrows(ItemNaoPertenceAoInventarioException.class, () -> inventario.equipar(espada));
    }

    @Test
    @DisplayName("deve lançar exceção ao equipar item que não é equipável")
    void deveLancarExcecaoAoEquiparItemNaoEquipavel() {
        inventario.adicionarItem(pocao);

        assertThrows(ItemNaoEquipavelException.class, () -> inventario.equipar(pocao));
    }

    @Test
    @DisplayName("deve desequipar o item equipado")
    void deveDesequiparItemQuandoEstiverEquipado() {
        inventario.adicionarItem(cota);
        inventario.equipar(cota);

        inventario.desequipar(cota);

        assertNull(inventario.getArmaduraEquipada());
    }

    @Test
    @DisplayName("deve lançar exceção ao desequipar item que não está equipado")
    void deveLancarExcecaoAoDesequiparItemNaoEquipado() {
        inventario.adicionarItem(cota);

        assertThrows(ItemNaoEquipadoException.class, () -> inventario.desequipar(cota));
    }

    @Test
    @DisplayName("deve desequipar o item ao removê-lo do inventário")
    void deveDesequiparItemAoRemover() {
        inventario.adicionarItem(cota);
        inventario.equipar(cota);

        inventario.removerItem(cota);

        assertAll(
                () -> assertNull(inventario.getArmaduraEquipada()),
                () -> assertTrue(inventario.getItens().isEmpty()));
    }

    @Test
    @DisplayName("deve somar o peso de todos os itens guardados")
    void deveSomarPesoTotalQuandoHouverItens() {
        inventario.adicionarItem(espada);
        inventario.adicionarItem(cota);
        inventario.adicionarItem(escudo);

        assertEquals(39.0, inventario.pesoTotal());
    }

    @Test
    @DisplayName("deve calcular a capacidade de carga a partir da Força")
    void deveCalcularCapacidadeDeCargaAPartirDaForca() {
        assertEquals(240, inventario.capacidadeDeCarga(16));
    }

    @Test
    @DisplayName("deve indicar sobrecarga quando o peso passar da capacidade")
    void deveIndicarSobrecargaQuandoPesoPassarDaCapacidade() {
        inventario.adicionarItem(cota);

        assertAll(
                () -> assertFalse(inventario.estaSobrecarregado(16)),
                () -> assertTrue(inventario.estaSobrecarregado(1)));
    }

    @Test
    @DisplayName("deve calcular a CA final com a armadura e o escudo equipados")
    void deveCalcularCaFinalComEquipamento() {
        inventario.adicionarItem(cota);
        inventario.adicionarItem(escudo);
        inventario.equipar(cota);
        inventario.equipar(escudo);

        assertEquals(18, inventario.caFinal(3));
    }
}
