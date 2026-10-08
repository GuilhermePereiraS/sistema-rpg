package com.projeto_rpg.modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Biblioteca que reúne os sistemas de RPG cadastrados.
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>Não é possível cadastrar dois sistemas com o mesmo nome.</li>
 *   <li>Remover sistema inexistente gera exceção.</li>
 *   <li>A busca por tag varia as regras de todos os sistemas cadastrados.</li>
 * </ul>
 */
public class Biblioteca {

    private final List<SistemaRpg> sistemas;

    public Biblioteca() {
        this.sistemas = new ArrayList<>();
    }

    /**
     * Cadastra um sistema na biblioteca.
     *
     * @throws IllegalArgumentException se o sistema for nulo ou já cadastrado (mesmo nome, ignorando caixa)
     */
    public void cadastrarSistema(SistemaRpg sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("Sistema não pode ser nulo");
        }
        if (buscarSistemaPorNome(sistema.getNome()).isPresent()) {
            throw new IllegalArgumentException(
                    "Já existe um sistema cadastrado com o nome '" + sistema.getNome() + "'");
        }
        sistemas.add(sistema);
    }

    /**
     * Remove o sistema com o nome informado (ignora caixa).
     *
     * @return o sistema removido
     * @throws IllegalArgumentException se o nome for vazio ou o sistema não existir
     */
    public SistemaRpg removerSistema(String nome) {
        SistemaRpg sistema = buscarSistemaPorNome(nome)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sistema com nome '" + nome + "' não encontrado"));
        sistemas.remove(sistema);
        return sistema;
    }

    /**
     * Busca um sistema pelo nome, ignorando maiúsculas/minúsculas.
     *
     * @throws IllegalArgumentException se o nome for nulo ou vazio
     */
    public Optional<SistemaRpg> buscarSistemaPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do sistema não pode ser vazio");
        }
        return sistemas.stream()
                .filter(sistema -> sistema.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    /**
     * Busca, em todos os sistemas cadastrados, as regras que possuem a tag informada
     * (lista imutável).
     *
     * @throws IllegalArgumentException se a tag for nula ou vazia
     */
    public List<Regra> buscarRegrasPorTag(String tag) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag não pode ser vazia");
        }
        return sistemas.stream()
                .flatMap(sistema -> sistema.getRegras().stream())
                .filter(regra -> regra.possuiTag(tag))
                .toList();
    }

    /**
     * Lista todos os sistemas cadastrados (lista imutável).
     */
    public List<SistemaRpg> listarSistemas() {
        return List.copyOf(sistemas);
    }
}
