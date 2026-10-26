package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.dados.TermoDeDado;

import java.util.Collections;
import java.util.List;

/**
 * Crítico usa o valor máximo de cada dado mantido, sem rolar.
 */
public record MaximizarDados() implements RegraDeDanoCritico {

    @Override
    public ResultadoRolagem rolarDanoCritico(ExpressaoDeDado dano, RoladorDeDados rolador) {
        List<Integer> dadosMaximos = dano.termos().stream()
                .flatMap(termo -> Collections.nCopies(termo.mantidos(), termo.faces()).stream())
                .toList();
        int total = dano.termos().stream()
                .mapToInt(termo -> termo.mantidos() * termo.faces())
                .sum() + dano.modificador();
        return new ResultadoRolagem(dadosMaximos, total);
    }

    private static int maximoDoTermo(TermoDeDado termo) {
        return termo.mantidos() * termo.faces();
    }
}
