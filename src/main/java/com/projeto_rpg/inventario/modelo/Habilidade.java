package com.projeto_rpg.inventario.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Habilidade do catálogo (truque ou magia).
 *
 * <p>O nível vai de 0 (truque, que não consome espaço) a 9. Habilidades de nível 1 a 9
 * consomem um espaço do nível correspondente na {@link ReservaDeEspacos}.
 */
@Entity
@Table(name = "habilidade")
public class Habilidade {

    public static final int NIVEL_MINIMO = 0;
    public static final int NIVEL_MAXIMO = 9;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(nullable = false)
    private int nivel;

    private String descricao;

    protected Habilidade() {
        // exigido pelo JPA
    }

    public Habilidade(String nome, int nivel, String descricao) {
        aplicar(nome, nivel, descricao);
    }

    public Habilidade(String nome, int nivel) {
        this(nome, nivel, null);
    }

    /**
     * Atualiza todos os dados da habilidade. Se algum valor for inválido, nada é alterado.
     */
    public void atualizar(String novoNome, int novoNivel, String novaDescricao) {
        aplicar(novoNome, novoNivel, novaDescricao);
    }

    private void aplicar(String nome, int nivel, String descricao) {
        String nomeValidado = validarNome(nome);
        int nivelValidado = validarNivel(nivel);
        this.nome = nomeValidado;
        this.nivel = nivelValidado;
        this.descricao = normalizarDescricao(descricao);
    }

    private static String validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da habilidade não pode ser vazio");
        }
        return nome.trim();
    }

    private static int validarNivel(int nivel) {
        if (nivel < NIVEL_MINIMO || nivel > NIVEL_MAXIMO) {
            throw new IllegalArgumentException("Nível da habilidade deve estar entre " + NIVEL_MINIMO
                    + " e " + NIVEL_MAXIMO + ", mas foi " + nivel);
        }
        return nivel;
    }

    private static String normalizarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return null;
        }
        return descricao.trim();
    }

    /**
     * Truques (nível 0) podem ser usados sem consumir espaço de habilidade.
     */
    public boolean isTruque() {
        return nivel == NIVEL_MINIMO;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getNivel() {
        return nivel;
    }

    public String getDescricao() {
        return descricao;
    }
}
