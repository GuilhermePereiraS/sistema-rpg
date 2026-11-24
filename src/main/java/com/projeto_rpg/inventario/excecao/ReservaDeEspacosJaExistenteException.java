package com.projeto_rpg.inventario.excecao;

/**
 * O personagem já possui uma reserva de espaços.
 */
public class ReservaDeEspacosJaExistenteException extends IllegalStateException {

    public ReservaDeEspacosJaExistenteException(Long personagemId) {
        super("Personagem " + personagemId + " já possui uma reserva de espaços");
    }
}
