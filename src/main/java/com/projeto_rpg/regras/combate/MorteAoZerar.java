package com.projeto_rpg.regras.combate;

import com.projeto_rpg.regras.excecao.TesteContraMorteNaoPermitidoException;

/**
 * Regra de morte para sistemas sem testes contra a morte: 0 PV = morto.
 */
public record MorteAoZerar() implements RegraDeMorte {

    @Override
    public void aoZerarPv(EstadoDeCombate estado, int danoExcedente) {
        estado.morrer();
    }

    @Override
    public void aoSofrerDanoSemPv(EstadoDeCombate estado, int dano, boolean critico) {
        estado.morrer();
    }

    @Override
    public ResultadoTesteContraMorte realizarTesteContraMorte(EstadoDeCombate estado, int dadoNatural) {
        throw new TesteContraMorteNaoPermitidoException("o sistema em uso não possui testes contra a morte");
    }
}
