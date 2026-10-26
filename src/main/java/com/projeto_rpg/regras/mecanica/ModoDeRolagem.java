package com.projeto_rpg.regras.mecanica;

/**
 * Como o dado de teste é rolado.
 */
public enum ModoDeRolagem {
    /** Um dado. */
    NORMAL,
    /** Dois dados, fica o melhor segundo o critério de sucesso do perfil. */
    VANTAGEM,
    /** Dois dados, fica o pior segundo o critério de sucesso do perfil. */
    DESVANTAGEM
}
