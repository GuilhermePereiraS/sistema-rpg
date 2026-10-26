package com.projeto_rpg.regras.historico;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro imutável de uma rolagem feita por um personagem.
 */
@Entity
@Table(name = "registro_de_rolagem")
public class RegistroDeRolagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long personagemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRolagem tipo;

    @Column(nullable = false)
    private String descricao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "registro_rolagem_dado", joinColumns = @JoinColumn(name = "registro_id"))
    @OrderColumn(name = "posicao")
    @Column(name = "valor", nullable = false)
    private List<Integer> dados = new ArrayList<>();

    @Column(nullable = false)
    private int total;

    @Column(nullable = false)
    private Instant dataHora;

    protected RegistroDeRolagem() {
        // exigido pelo JPA
    }

    public RegistroDeRolagem(Long personagemId, TipoRolagem tipo, String descricao, List<Integer> dados, int total,
                             Instant dataHora) {
        exigir(personagemId != null, "Personagem não pode ser nulo");
        exigir(tipo != null, "Tipo da rolagem não pode ser nulo");
        exigir(descricao != null && !descricao.isBlank(), "Descrição da rolagem não pode ser vazia");
        exigir(dados != null, "Dados da rolagem não podem ser nulos");
        exigir(dataHora != null, "Data e hora da rolagem não podem ser nulas");
        this.personagemId = personagemId;
        this.tipo = tipo;
        this.descricao = descricao;
        this.dados = new ArrayList<>(dados);
        this.total = total;
        this.dataHora = dataHora;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPersonagemId() {
        return personagemId;
    }

    public TipoRolagem getTipo() {
        return tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Integer> getDados() {
        return List.copyOf(dados);
    }

    public int getTotal() {
        return total;
    }

    public Instant getDataHora() {
        return dataHora;
    }
}
