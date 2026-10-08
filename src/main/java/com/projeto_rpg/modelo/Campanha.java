package com.projeto_rpg.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Campanha de RPG, vinculada a um sistema e a um conjunto de regras ativas.
 *
 * <p>Regras de negócio:
 * <ul>
 *   <li>Só é possível ativar regra que pertença ao sistema da campanha
 *       ou que tenha sido criada como regra da casa.</li>
 *   <li>Não é possível ativar regra já ativa nem desativar regra inativa.</li>
 *   <li>Não é possível adicionar duas regras da casa com o mesmo título.</li>
 *   <li>Uma campanha encerrada não aceita mais alterações (ativar, desativar,
 *       adicionar regra da casa) e não pode ser encerrada duas vezes.</li>
 * </ul>
 */
public class Campanha {

    private final String nome;
    private final SistemaRpg sistema;
    private final List<Regra> regrasDaCasa;
    private final List<Regra> regrasAtivas;
    private boolean encerrada;

    public Campanha(String nome, SistemaRpg sistema) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da campanha não pode ser vazio");
        }
        if (sistema == null) {
            throw new IllegalArgumentException("Sistema da campanha não pode ser nulo");
        }
        this.nome = nome;
        this.sistema = sistema;
        this.regrasDaCasa = new ArrayList<>();
        this.regrasAtivas = new ArrayList<>();
        this.encerrada = false;
    }

    /**
     * Ativa uma regra na campanha.
     *
     * @throws IllegalArgumentException se a regra for nula ou não pertencer à campanha
     * @throws IllegalStateException    se a campanha estiver encerrada ou a regra já estiver ativa
     */
    public void ativarRegra(Regra regra) {
        if (regra == null) {
            throw new IllegalArgumentException("Regra não pode ser nula");
        }
        verificarCampanhaAberta();
        if (possuiRegraAtiva(regra)) {
            throw new IllegalStateException(
                    "Regra '" + regra.getTitulo() + "' já está ativa na campanha");
        }
        if (!pertenceACampanha(regra)) {
            throw new IllegalArgumentException(
                    "Regra '" + regra.getTitulo()
                            + "' não pertence ao sistema da campanha nem às regras da casa");
        }
        regrasAtivas.add(regra);
    }

    /**
     * Desativa uma regra ativa da campanha.
     *
     * @throws IllegalArgumentException se a regra for nula
     * @throws IllegalStateException    se a campanha estiver encerrada ou a regra não estiver ativa
     */
    public void desativarRegra(Regra regra) {
        if (regra == null) {
            throw new IllegalArgumentException("Regra não pode ser nula");
        }
        verificarCampanhaAberta();
        if (!possuiRegraAtiva(regra)) {
            throw new IllegalStateException(
                    "Regra '" + regra.getTitulo() + "' não está ativa na campanha");
        }
        regrasAtivas.removeIf(ativa -> ativa.getTitulo().equals(regra.getTitulo()));
    }

    /**
     * Adiciona uma regra criada pela própria campanha (regra da casa).
     *
     * @throws IllegalArgumentException se a regra for nula ou já existir
     *                                  regra da casa com o mesmo título
     * @throws IllegalStateException    se a campanha estiver encerrada
     */
    public void adicionarRegraDaCasa(Regra regra) {
        if (regra == null) {
            throw new IllegalArgumentException("Regra não pode ser nula");
        }
        verificarCampanhaAberta();
        boolean tituloDuplicado = regrasDaCasa.stream()
                .anyMatch(casa -> casa.getTitulo().equals(regra.getTitulo()));
        if (tituloDuplicado) {
            throw new IllegalArgumentException(
                    "Já existe regra da casa com o título '" + regra.getTitulo() + "'");
        }
        regrasDaCasa.add(regra);
    }

    /**
     * Lista as regras ativas da campanha (lista imutável).
     */
    public List<Regra> listarRegrasAtivas() {
        return List.copyOf(regrasAtivas);
    }

    /**
     * Encerra a campanha, bloqueando novas alterações.
     *
     * @throws IllegalStateException se a campanha já estiver encerrada
     */
    public void encerrar() {
        if (encerrada) {
            throw new IllegalStateException("Campanha já está encerrada");
        }
        this.encerrada = true;
    }

    private void verificarCampanhaAberta() {
        if (encerrada) {
            throw new IllegalStateException("Campanha encerrada não aceita alterações");
        }
    }

    private boolean possuiRegraAtiva(Regra regra) {
        return regrasAtivas.stream()
                .anyMatch(ativa -> ativa.getTitulo().equals(regra.getTitulo()));
    }

    /**
     * A regra pertence à campanha se existir no sistema (mesmo título)
     * ou tiver sido adicionada como regra da casa.
     */
    private boolean pertenceACampanha(Regra regra) {
        boolean existeNoSistema = sistema.buscarRegraPorTitulo(regra.getTitulo()).isPresent();
        boolean eRegraDaCasa = regrasDaCasa.stream()
                .anyMatch(casa -> casa.getTitulo().equals(regra.getTitulo()));
        return existeNoSistema || eRegraDaCasa;
    }

    public String getNome() {
        return nome;
    }

    public SistemaRpg getSistema() {
        return sistema;
    }

    public List<Regra> getRegrasDaCasa() {
        return List.copyOf(regrasDaCasa);
    }

    public boolean isEncerrada() {
        return encerrada;
    }
}
