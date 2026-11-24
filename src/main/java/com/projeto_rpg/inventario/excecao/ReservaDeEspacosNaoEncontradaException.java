package com.projeto_rpg.inventario.excecao;

/**
 * Não existe reserva de espaços para o personagem informado.
 */
public class ReservaDeEspacosNaoEncontradaException extends RuntimeException {

    public ReservaDeEspacosNaoEncontradaException(Long personagemId) {
        super("Reserva de espaços do personagem " + personagemId + " não encontrada");
    }
}
