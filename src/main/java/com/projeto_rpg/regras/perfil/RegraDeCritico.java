package com.projeto_rpg.regras.perfil;

/**
 * Decide, pelo valor natural do dado de teste, se houve crítico ou falha crítica.
 */
public interface RegraDeCritico {

    boolean eCritico(int dadoNatural);

    boolean eFalhaCritica(int dadoNatural);
}
