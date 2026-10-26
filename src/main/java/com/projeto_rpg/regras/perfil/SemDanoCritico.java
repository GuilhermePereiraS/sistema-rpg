package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;

/**
 * Sistemas sem dano crítico: o dano é rolado normalmente.
 */
public record SemDanoCritico() implements RegraDeDanoCritico {

    @Override
    public ResultadoRolagem rolarDanoCritico(ExpressaoDeDado dano, RoladorDeDados rolador) {
        return rolador.rolar(dano);
    }
}
