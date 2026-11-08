package com.projeto_rpg.ficha.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Set;

/**
 * Classe de personagem (Guerreiro, Mago...). Define o dado de vida usado no cálculo de PV.
 */
@Entity
@Table(name = "classe")
public class Classe {

    private static final Set<Integer> DADOS_DE_VIDA_VALIDOS = Set.of(6, 8, 10, 12);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private int dadoDeVida;

    protected Classe() {
        // exigido pelo JPA
    }

    public Classe(String nome, int dadoDeVida) {
        this.nome = validarNome(nome);
        this.dadoDeVida = validarDadoDeVida(dadoDeVida);
    }

    /**
     * Atualiza nome e dado de vida. Se algum valor for inválido, nada é alterado.
     */
    public void atualizar(String novoNome, int novoDadoDeVida) {
        String nomeValidado = validarNome(novoNome);
        int dadoValidado = validarDadoDeVida(novoDadoDeVida);
        this.nome = nomeValidado;
        this.dadoDeVida = dadoValidado;
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da classe não pode ser vazio");
        }
        return nome.trim();
    }

    private static int validarDadoDeVida(int dadoDeVida) {
        if (!DADOS_DE_VIDA_VALIDOS.contains(dadoDeVida)) {
            throw new IllegalArgumentException(
                    "Dado de vida deve ser d6, d8, d10 ou d12, mas foi d" + dadoDeVida);
        }
        return dadoDeVida;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getDadoDeVida() {
        return dadoDeVida;
    }
}
