package com.projeto_rpg.regras.dados;

import java.util.List;

/**
 * Expressão de dados já interpretada, como "2d6+3".
 */
public record ExpressaoDeDado(List<TermoDeDado> termos, int modificador) {
}
