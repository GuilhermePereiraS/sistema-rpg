package com.projeto_rpg.inventario.modelo;

import com.projeto_rpg.inventario.excecao.ItemNaoEquipadoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEquipavelException;
import com.projeto_rpg.inventario.excecao.ItemNaoPertenceAoInventarioException;
import com.projeto_rpg.inventario.regra.CalculadoraDeCa;
import com.projeto_rpg.inventario.regra.CalculadoraDeCarga;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventário de um personagem: os itens carregados e o que está equipado.
 *
 * <p>O personagem é referenciado apenas pelo id; o módulo não depende de nenhum outro.
 * A capacidade de carga é calculada a partir da Força e a CA final a partir do
 * modificador de Destreza e da armadura/escudo equipados.
 */
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long personagemId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "inventario_item",
            joinColumns = @JoinColumn(name = "inventario_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private List<Item> itens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "armadura_equipada_id")
    private Item armaduraEquipada;

    @ManyToOne
    @JoinColumn(name = "escudo_equipado_id")
    private Item escudoEquipado;

    @ManyToOne
    @JoinColumn(name = "arma_equipada_id")
    private Item armaEquipada;

    protected Inventario() {
        // exigido pelo JPA
    }

    public Inventario(Long personagemId) {
        if (personagemId == null) {
            throw new IllegalArgumentException("Personagem não pode ser nulo");
        }
        this.personagemId = personagemId;
    }

    /**
     * Guarda o item no inventário. Se o item já estiver guardado, nada acontece.
     */
    public void adicionarItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item não pode ser nulo");
        }
        if (!contem(item)) {
            itens.add(item);
        }
    }

    /**
     * Remove o item do inventário e o desequipa, se estiver equipado.
     *
     * @throws ItemNaoPertenceAoInventarioException se o item não estiver guardado
     */
    public void removerItem(Item item) {
        if (item == null || !removerDaLista(item)) {
            throw new ItemNaoPertenceAoInventarioException(item == null ? "desconhecido" : item.getNome());
        }
        desequiparSeFor(item);
    }

    /**
     * Desequipa o que não cabe mais no espaço onde está, o que acontece quando o tipo de um
     * item equipado muda no catálogo (por exemplo, de armadura para item geral). O item
     * continua guardado no inventário; só deixa de estar equipado.
     */
    public void revalidarEquipamento() {
        if (armaduraEquipada != null && armaduraEquipada.getTipo() != TipoDeItem.ARMADURA) {
            armaduraEquipada = null;
        }
        if (escudoEquipado != null && escudoEquipado.getTipo() != TipoDeItem.ESCUDO) {
            escudoEquipado = null;
        }
        if (armaEquipada != null && armaEquipada.getTipo() != TipoDeItem.ARMA) {
            armaEquipada = null;
        }
    }

    /**
     * Equipa o item no espaço correspondente ao seu tipo.
     *
     * @throws ItemNaoPertenceAoInventarioException se o item não estiver guardado
     * @throws ItemNaoEquipavelException se o item não for arma, armadura ou escudo
     */
    public void equipar(Item item) {
        if (item == null || !contem(item)) {
            throw new ItemNaoPertenceAoInventarioException(item == null ? "desconhecido" : item.getNome());
        }
        switch (item.getTipo()) {
            case ARMADURA -> armaduraEquipada = item;
            case ESCUDO -> escudoEquipado = item;
            case ARMA -> armaEquipada = item;
            default -> throw new ItemNaoEquipavelException(item.getNome());
        }
    }

    /**
     * Desequipa o item do espaço que ele ocupa.
     *
     * @throws ItemNaoEquipadoException se o item não estiver equipado
     */
    public void desequipar(Item item) {
        if (!desequiparSeFor(item)) {
            throw new ItemNaoEquipadoException(item == null ? "desconhecido" : item.getNome());
        }
    }

    /**
     * Peso total carregado, em libras.
     */
    public double pesoTotal() {
        return itens.stream().mapToDouble(Item::getPeso).sum();
    }

    /**
     * Capacidade de carga a partir da Força do personagem.
     */
    public int capacidadeDeCarga(int forca) {
        return CalculadoraDeCarga.capacidadeDeCarga(forca);
    }

    public boolean estaSobrecarregado(int forca) {
        return pesoTotal() > capacidadeDeCarga(forca);
    }

    /**
     * CA final a partir do modificador de Destreza e da armadura/escudo equipados.
     */
    public int caFinal(int modificadorDeDestreza) {
        return CalculadoraDeCa.caFinal(modificadorDeDestreza, armaduraEquipada, escudoEquipado);
    }

    private boolean contem(Item item) {
        return itens.stream().anyMatch(guardado -> mesmoItem(guardado, item));
    }

    private boolean removerDaLista(Item item) {
        for (int i = 0; i < itens.size(); i++) {
            if (mesmoItem(itens.get(i), item)) {
                itens.remove(i);
                return true;
            }
        }
        return false;
    }

    private boolean desequiparSeFor(Item item) {
        boolean removeu = false;
        if (mesmoItem(armaduraEquipada, item)) {
            armaduraEquipada = null;
            removeu = true;
        }
        if (mesmoItem(escudoEquipado, item)) {
            escudoEquipado = null;
            removeu = true;
        }
        if (mesmoItem(armaEquipada, item)) {
            armaEquipada = null;
            removeu = true;
        }
        return removeu;
    }

    private static boolean mesmoItem(Item um, Item outro) {
        if (um == null || outro == null) {
            return false;
        }
        return um == outro || (um.getId() != null && um.getId().equals(outro.getId()));
    }

    public Long getId() {
        return id;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public List<Item> getItens() {
        return List.copyOf(itens);
    }

    public Item getArmaduraEquipada() {
        return armaduraEquipada;
    }

    public Item getEscudoEquipado() {
        return escudoEquipado;
    }

    public Item getArmaEquipada() {
        return armaEquipada;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Inventario inventario)) {
            return false;
        }
        return id != null && id.equals(inventario.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hash(id);
    }
}
