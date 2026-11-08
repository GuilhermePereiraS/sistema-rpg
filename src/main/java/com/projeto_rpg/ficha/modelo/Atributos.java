package com.projeto_rpg.ficha.modelo;

import jakarta.persistence.Embeddable;

/**
 * Valores dos seis atributos de um personagem.
 */
@Embeddable
public class Atributos {

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
        this.forca = forca;
        this.destreza = destreza;
        this.constituicao = constituicao;
        this.inteligencia = inteligencia;
        this.sabedoria = sabedoria;
        this.carisma = carisma;
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
}
