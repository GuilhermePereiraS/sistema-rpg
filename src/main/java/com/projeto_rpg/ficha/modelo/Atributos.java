package com.projeto_rpg.ficha.modelo;

import jakarta.persistence.Embeddable;

import java.util.Objects;

/**
 * Valores dos seis atributos de um personagem.
 */
@Embeddable
public class Atributos {

    public static final int VALOR_MINIMO = 1;
    public static final int VALOR_MAXIMO = 30;

    private int forca;
    private int destreza;
    private int constituicao;
    private int inteligencia;
    private int sabedoria;
    private int carisma;

    protected Atributos() {
        // exigido pelo JPA
    }

    public Atributos(int forca, int destreza, int constituicao, int inteligencia, int sabedoria, int carisma) {
        this.forca = validar(Atributo.FORCA, forca);
        this.destreza = validar(Atributo.DESTREZA, destreza);
        this.constituicao = validar(Atributo.CONSTITUICAO, constituicao);
        this.inteligencia = validar(Atributo.INTELIGENCIA, inteligencia);
        this.sabedoria = validar(Atributo.SABEDORIA, sabedoria);
        this.carisma = validar(Atributo.CARISMA, carisma);
    }

    private static int validar(Atributo atributo, int valor) {
        if (valor < VALOR_MINIMO || valor > VALOR_MAXIMO) {
            throw new IllegalArgumentException("Valor de " + atributo + " deve estar entre "
                    + VALOR_MINIMO + " e " + VALOR_MAXIMO + ", mas foi " + valor);
        }
        return valor;
    }

    /**
     * Modificador de um valor de atributo: (valor - 10) / 2, arredondado para baixo.
     */
    public static int modificadorDe(int valor) {
        return Math.floorDiv(valor - 10, 2);
    }

    public int valor(Atributo atributo) {
        return switch (atributo) {
            case FORCA -> forca;
            case DESTREZA -> destreza;
            case CONSTITUICAO -> constituicao;
            case INTELIGENCIA -> inteligencia;
            case SABEDORIA -> sabedoria;
            case CARISMA -> carisma;
        };
    }

    public int modificador(Atributo atributo) {
        return modificadorDe(valor(atributo));
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Atributos atributos)) {
            return false;
        }
        return forca == atributos.forca && destreza == atributos.destreza
                && constituicao == atributos.constituicao && inteligencia == atributos.inteligencia
                && sabedoria == atributos.sabedoria && carisma == atributos.carisma;
    }

    @Override
    public int hashCode() {
        return Objects.hash(forca, destreza, constituicao, inteligencia, sabedoria, carisma);
    }
}
