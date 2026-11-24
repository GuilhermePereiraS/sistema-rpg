package com.projeto_rpg.inventario.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

/**
 * Item do catálogo do sistema (arma, armadura, escudo, consumível ou item geral).
 *
 * <p>Armaduras carregam a CA base e a categoria ({@link TipoDeArmadura}); os demais
 * itens usam apenas nome, tipo e peso. O peso alimenta a capacidade de carga do inventário.
 */
@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeItem tipo;

    /** Peso em libras, usado no cálculo da capacidade de carga. */
    @Column(nullable = false)
    private double peso;

    /** CA base da armadura; zero para os itens que não são armadura. */
    @Column(nullable = false)
    private int caBase;

    @Enumerated(EnumType.STRING)
    private TipoDeArmadura tipoDeArmadura;

    protected Item() {
        // exigido pelo JPA
    }

    private Item(String nome, TipoDeItem tipo, double peso, int caBase, TipoDeArmadura tipoDeArmadura) {
        aplicar(nome, tipo, peso, caBase, tipoDeArmadura);
    }

    public static Item arma(String nome, double peso) {
        return new Item(nome, TipoDeItem.ARMA, peso, 0, null);
    }

    public static Item armadura(String nome, double peso, int caBase, TipoDeArmadura tipoDeArmadura) {
        return new Item(nome, TipoDeItem.ARMADURA, peso, caBase, tipoDeArmadura);
    }

    public static Item escudo(String nome, double peso) {
        return new Item(nome, TipoDeItem.ESCUDO, peso, 0, null);
    }

    public static Item consumivel(String nome, double peso) {
        return new Item(nome, TipoDeItem.CONSUMIVEL, peso, 0, null);
    }

    public static Item geral(String nome, double peso) {
        return new Item(nome, TipoDeItem.GERAL, peso, 0, null);
    }

    /**
     * Atualiza todos os dados do item. Se algum valor for inválido, nada é alterado.
     */
    public void atualizar(String novoNome, TipoDeItem novoTipo, double novoPeso, int novaCaBase,
                          TipoDeArmadura novoTipoDeArmadura) {
        aplicar(novoNome, novoTipo, novoPeso, novaCaBase, novoTipoDeArmadura);
    }

    private void aplicar(String nome, TipoDeItem tipo, double peso, int caBase, TipoDeArmadura tipoDeArmadura) {
        String nomeValidado = validarNome(nome);
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo do item não pode ser nulo");
        }
        double pesoValidado = validarPeso(peso);
        validarArmadura(tipo, caBase, tipoDeArmadura);
        this.nome = nomeValidado;
        this.tipo = tipo;
        this.peso = pesoValidado;
        this.caBase = caBase;
        this.tipoDeArmadura = tipoDeArmadura;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do item não pode ser vazio");
        }
        return nome.trim();
    }

    private static double validarPeso(double peso) {
        if (peso < 0) {
            throw new IllegalArgumentException("Peso do item não pode ser negativo");
        }
        return peso;
    }

    private static void validarArmadura(TipoDeItem tipo, int caBase, TipoDeArmadura tipoDeArmadura) {
        if (tipo == TipoDeItem.ARMADURA) {
            if (caBase <= 0) {
                throw new IllegalArgumentException("Armadura precisa de CA base positiva");
            }
            if (tipoDeArmadura == null) {
                throw new IllegalArgumentException("Armadura precisa de uma categoria (leve, média ou pesada)");
            }
        } else {
            if (caBase != 0) {
                throw new IllegalArgumentException("Apenas armaduras possuem CA base");
            }
            if (tipoDeArmadura != null) {
                throw new IllegalArgumentException("Apenas armaduras possuem categoria de armadura");
            }
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoDeItem getTipo() {
        return tipo;
    }

    public double getPeso() {
        return peso;
    }

    public int getCaBase() {
        return caBase;
    }

    public TipoDeArmadura getTipoDeArmadura() {
        return tipoDeArmadura;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Item item)) {
            return false;
        }
        return id != null && id.equals(item.id);
    }

    @Override
    public int hashCode() {
        return id == null ? System.identityHashCode(this) : Objects.hash(id);
    }
}
