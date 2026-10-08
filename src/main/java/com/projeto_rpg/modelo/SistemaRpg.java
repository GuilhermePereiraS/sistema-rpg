package com.projeto_rpg.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Representa um sistema de RPG (ex.: D&D, FATE), que agrupa suas regras.
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>O nome do sistema não pode ser vazio.</li>
 *   <li>Não é possível adicionar duas regras com o mesmo título.</li>
 *   <li>Remover regra inexistente gera exceção.</li>
 * </ul>
 */
public class SistemaRpg {

    private final String nome;
    private final List<Regra> regras;

    public SistemaRpg(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do sistema não pode ser vazio");
        }
        this.nome = nome;
        this.regras = new ArrayList<>();
    }

    /**
     * Adiciona uma regra ao sistema.
     *
     * @throws IllegalArgumentException se a regra for nula ou já existir uma regra com o mesmo título
     */
    public void adicionarRegra(Regra regra) {
        if (regra == null) {
            throw new IllegalArgumentException("Regra não pode ser nula");
        }
        if (buscarRegraPorTitulo(regra.getTitulo()).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe uma regra com o título '" + regra.getTitulo() + "' neste sistema");
        }
        regras.add(regra);
    }

    /**
     * Remove a regra com o título informado.
     *
     * @return a regra removida
     * @throws IllegalArgumentException se o título for vazio ou a regra não existir
     */
    public Regra removerRegra(String titulo) {
        Regra regra = buscarRegraPorTitulo(titulo)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Regra com título '" + titulo + "' não encontrada"));
        regras.remove(regra);
        return regra;
    }

    /**
     * Busca uma regra pelo título (correspondência exata).
     */
    public Optional<Regra> buscarRegraPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }
        return regras.stream()
                .filter(regra -> regra.getTitulo().equals(titulo))
                .findFirst();
    }

    /**
     * Lista as regras de uma categoria específica (lista imutável).
     *
     * @throws IllegalArgumentException se a categoria for nula
     */
    public List<Regra> listarRegrasPorCategoria(Categoria categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula");
        }
        return regras.stream()
                .filter(regra -> regra.getCategoria() == categoria)
                .toList();
    }

    /**
     * Conta a quantidade de regras do sistema.
     */
    public int contarRegras() {
        return regras.size();
    }

    public String getNome() {
        return nome;
    }

    public List<Regra> getRegras() {
        return List.copyOf(regras);
    }
}
