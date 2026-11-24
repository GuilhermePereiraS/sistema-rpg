package com.projeto_rpg.inventario.modelo;

import com.projeto_rpg.inventario.excecao.EspacoDeHabilidadeInsuficienteException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Espaços de habilidade de um nível: quantos existem e quantos já foram gastos.
 *
 * <p>É embutido em {@link ReservaDeEspacos}; por isso o {@code equals} considera os
 * campos mutáveis, permitindo ao Hibernate detectar alterações na coleção.
 */
@Embeddable
public class EspacosDeNivel {

    @Column(nullable = false)
    private int nivel;

    @Column(nullable = false)
    private int maximo;

    @Column(nullable = false)
    private int gastos;

    protected EspacosDeNivel() {
        // exigido pelo JPA
    }

    public EspacosDeNivel(int nivel, int maximo) {
        validarNivel(nivel);
        validarMaximo(maximo);
        this.nivel = nivel;
        this.maximo = maximo;
    }

    public int disponiveis() {
        return maximo - gastos;
    }

    void gastar() {
        if (disponiveis() <= 0) {
            throw new EspacoDeHabilidadeInsuficienteException(nivel);
        }
        gastos++;
    }

    void recuperar(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade a recuperar deve ser positiva");
        }
        gastos = Math.max(0, gastos - quantidade);
    }

    void recuperarTudo() {
        gastos = 0;
    }

    void ajustarMaximo(int novoMaximo) {
        validarMaximo(novoMaximo);
        maximo = novoMaximo;
        gastos = Math.min(gastos, maximo);
    }

    private static void validarNivel(int nivel) {
        if (nivel < ReservaDeEspacos.NIVEL_MINIMO || nivel > ReservaDeEspacos.NIVEL_MAXIMO) {
            throw new IllegalArgumentException("Nível de espaço deve estar entre " + ReservaDeEspacos.NIVEL_MINIMO
                    + " e " + ReservaDeEspacos.NIVEL_MAXIMO + ", mas foi " + nivel);
        }
    }

    private static void validarMaximo(int maximo) {
        if (maximo < 0) {
            throw new IllegalArgumentException("Quantidade de espaços não pode ser negativa");
        }
    }

    public int getNivel() {
        return nivel;
    }

    public int getMaximo() {
        return maximo;
    }

    public int getGastos() {
        return gastos;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof EspacosDeNivel espacos)) {
            return false;
        }
        return nivel == espacos.nivel && maximo == espacos.maximo && gastos == espacos.gastos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nivel, maximo, gastos);
    }
}
