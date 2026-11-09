package com.projeto_rpg.ficha.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Raça de personagem (Humano, Anão, Elfo...).
 */
@Entity
@Table(name = "raca")
public class Raca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    /** Deslocamento base, em pés por rodada. */
    @Column(nullable = false)
    private int deslocamento;

    protected Raca() {
        // exigido pelo JPA
    }

    public Raca(String nome, int deslocamento) {
        this.nome = validarNome(nome);
        this.deslocamento = validarDeslocamento(deslocamento);
    }

    /**
     * Atualiza nome e deslocamento. Se algum valor for inválido, nada é alterado.
     */
    public void atualizar(String novoNome, int novoDeslocamento) {
        String nomeValidado = validarNome(novoNome);
        int deslocamentoValidado = validarDeslocamento(novoDeslocamento);
        this.nome = nomeValidado;
        this.deslocamento = deslocamentoValidado;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da raça não pode ser vazio");
        }
        return nome.trim();
    }

    private static int validarDeslocamento(int deslocamento) {
        if (deslocamento <= 0) {
            throw new IllegalArgumentException("Deslocamento deve ser positivo");
        }
        return deslocamento;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getDeslocamento() {
        return deslocamento;
    }
}
