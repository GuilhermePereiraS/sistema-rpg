package com.projeto_rpg.modelo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Representa uma regra de um sistema de RPG.
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>Título e descrição não podem ser vazios.</li>
 *   <li>Uma regra arquivada não pode ser editada nem arquivada novamente.</li>
 *   <li>Tags são únicas: não é possível adicionar a mesma tag duas vezes.</li>
 *   <li>Só é possível remover tag que exista na regra.</li>
 * </ul>
 */
public class Regra {

    private final String titulo;
    private final Categoria categoria;
    private String descricao;
    private final Set<String> tags;
    private boolean arquivada;

    public Regra(String titulo, String descricao, Categoria categoria) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título da regra não pode ser vazio");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição da regra não pode ser vazia");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria da regra não pode ser nula");
        }
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.tags = new LinkedHashSet<>();
        this.arquivada = false;
    }

    /**
     * Atualiza a descrição da regra.
     *
     * @throws IllegalArgumentException se a nova descrição for nula ou vazia
     * @throws IllegalStateException    se a regra estiver arquivada
     */
    public void editarDescricao(String novaDescricao) {
        if (novaDescricao == null || novaDescricao.isBlank()) {
            throw new IllegalArgumentException("Nova descrição não pode ser vazia");
        }
        if (arquivada) {
            throw new IllegalStateException("Regra arquivada não pode ser editada");
        }
        this.descricao = novaDescricao;
    }

    /**
     * Adiciona uma tag à regra.
     *
     * @throws IllegalArgumentException se a tag for nula ou vazia
     * @throws IllegalStateException    se a tag já existir na regra
     */
    public void adicionarTag(String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag não pode ser vazia");
        }
        if (!tags.add(tag.trim())) {
            throw new IllegalStateException("Tag '" + tag + "' já existe nesta regra");
        }
    }

    /**
     * Remove uma tag da regra.
     *
     * @throws IllegalArgumentException se a tag for nula, vazia ou não existir na regra
     */
    public void removerTag(String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag não pode ser vazia");
        }
        if (!tags.remove(tag)) {
            throw new IllegalArgumentException("Tag '" + tag + "' não encontrada na regra");
        }
    }

    /**
     * Verifica se a regra possui a tag informada.
     */
    public boolean possuiTag(String tag) {
        return tag != null && tags.contains(tag);
    }

    /**
     * Arquiva a regra, impedindo novas edições.
     *
     * @throws IllegalStateException se a regra já estiver arquivada
     */
    public void arquivar() {
        if (arquivada) {
            throw new IllegalStateException("Regra já está arquivada");
        }
        this.arquivada = true;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public boolean isArquivada() {
        return arquivada;
    }
}
