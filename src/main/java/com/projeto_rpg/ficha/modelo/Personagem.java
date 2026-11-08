package com.projeto_rpg.ficha.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Ficha de um personagem.
 *
 * <p>A raça é referenciada apenas pelo id ({@code racaId}), para que o cadastro de raças
 * possa pertencer a outro módulo.
 */
@Entity
@Table(name = "personagem")
public class Personagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @ManyToOne(optional = false)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @Column(nullable = false)
    private Long racaId;

    @Embedded
    private Atributos atributos;

    @Column(nullable = false)
    private int xp;

    protected Personagem() {
        // exigido pelo JPA
    }

    public Personagem(String nome, Classe classe, Long racaId, Atributos atributos) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do personagem não pode ser vazio");
        }
        if (classe == null) {
            throw new IllegalArgumentException("Classe do personagem não pode ser nula");
        }
        if (racaId == null) {
            throw new IllegalArgumentException("Raça do personagem não pode ser nula");
        }
        if (atributos == null) {
            throw new IllegalArgumentException("Atributos do personagem não podem ser nulos");
        }
        this.nome = nome.trim();
        this.classe = classe;
        this.racaId = racaId;
        this.atributos = atributos;
    }

    public int modificador(Atributo atributo) {
        return atributos.modificador(atributo);
    }

    public int getNivel() {
        return 1;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Classe getClasse() {
        return classe;
    }

    public Long getRacaId() {
        return racaId;
    }

    public Atributos getAtributos() {
        return atributos;
    }

    public int getXp() {
        return xp;
    }
}
