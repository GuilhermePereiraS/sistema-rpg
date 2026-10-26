package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;

/**
 * Crítico dobra o total do dano, incluindo o modificador (ex.: Pathfinder 2e).
 */
public record DobrarTotal() implements RegraDeDanoCritico {

    @Override
    public ResultadoRolagem rolarDanoCritico(ExpressaoDeDado dano, RoladorDeDados rolador) {
        ResultadoRolagem normal = rolador.rolar(dano);
        return new ResultadoRolagem(normal.dados(), normal.total() * 2);
    }
}
