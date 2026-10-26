package com.projeto_rpg.regras.perfil;

/**
 * Crítico e falha crítica definidos por faixas fechadas de valores naturais.
 * Ex.: D&D = crítico [20,20], falha [1,1]; percentual = crítico [1,5], falha [96,100].
 */
public record CriticoPorFaixa(int criticoMinimo, int criticoMaximo, int falhaMinima, int falhaMaxima)
        implements RegraDeCritico {

    public CriticoPorFaixa {
        validarFaixa(criticoMinimo, criticoMaximo, "crítico");
        validarFaixa(falhaMinima, falhaMaxima, "falha crítica");
        boolean sobrepostas = criticoMinimo <= falhaMaxima && falhaMinima <= criticoMaximo;
        if (sobrepostas) {
            throw new IllegalArgumentException("Faixas de crítico e falha crítica não podem se sobrepor");
        }
    }

    private static void validarFaixa(int minimo, int maximo, String nome) {
        if (minimo < 1 || minimo > maximo) {
            throw new IllegalArgumentException(
                    "Faixa de " + nome + " inválida: [" + minimo + ", " + maximo + "]");
        }
    }

    @Override
    public boolean eCritico(int dadoNatural) {
        return dadoNatural >= criticoMinimo && dadoNatural <= criticoMaximo;
    }

    @Override
    public boolean eFalhaCritica(int dadoNatural) {
        return dadoNatural >= falhaMinima && dadoNatural <= falhaMaxima;
    }
}
