package com.projeto_rpg.inventario.excecao;

/**
 * Não há espaços de habilidade disponíveis no nível pedido.
 */
public class EspacoDeHabilidadeInsuficienteException extends IllegalStateException {

    public EspacoDeHabilidadeInsuficienteException(int nivel) {
        super("Não há espaços de habilidade disponíveis no nível " + nivel);
    }
}
