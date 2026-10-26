package com.projeto_rpg.regras.excecao;

/**
 * Teste contra a morte pedido em uma situação que não permite (ex.: personagem consciente,
 * estável, morto ou sistema sem testes contra a morte).
 */
public class TesteContraMorteNaoPermitidoException extends IllegalStateException {

    public TesteContraMorteNaoPermitidoException(String motivo) {
        super("Teste contra a morte não permitido: " + motivo);
    }
}
