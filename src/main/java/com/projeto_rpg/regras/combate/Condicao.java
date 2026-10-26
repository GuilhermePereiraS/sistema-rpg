package com.projeto_rpg.regras.combate;

/**
 * Condição do personagem em combate.
 */
public enum Condicao {
    /** PV acima de zero, age normalmente. */
    CONSCIENTE,
    /** 0 PV, fazendo testes contra a morte. */
    INCONSCIENTE,
    /** 0 PV, mas fora de perigo: não faz mais testes contra a morte. */
    ESTAVEL,
    /** Morto: não recebe dano, cura nem recuperação. */
    MORTO
}
