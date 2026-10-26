package com.projeto_rpg.regras.combate;

/**
 * Resultado de um teste contra a morte.
 *
 * @param dadoNatural valor natural rolado
 * @param sucesso     se o teste foi bem-sucedido
 * @param sucessos    sucessos acumulados após o teste
 * @param falhas      falhas acumuladas após o teste
 * @param condicao    condição do personagem após o teste
 */
public record ResultadoTesteContraMorte(int dadoNatural, boolean sucesso, int sucessos, int falhas,
                                        Condicao condicao) {
}
