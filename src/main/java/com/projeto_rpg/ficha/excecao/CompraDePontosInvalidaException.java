package com.projeto_rpg.ficha.excecao;

/**
 * A distribuição de atributos não respeita as regras da compra de pontos.
 */
public class CompraDePontosInvalidaException extends IllegalArgumentException {

    public CompraDePontosInvalidaException(String mensagem) {
        super(mensagem);
    }
}
