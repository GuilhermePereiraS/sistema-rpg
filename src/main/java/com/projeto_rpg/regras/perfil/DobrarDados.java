package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;

/**
 * Crítico rola o dobro de dados; o modificador entra uma vez (D&D 5e).
 */
public record DobrarDados() implements RegraDeDanoCritico {

    @Override
    public ResultadoRolagem rolarDanoCritico(ExpressaoDeDado dano, RoladorDeDados rolador) {
        return rolador.rolar(dano.comDadosDobrados());
    }
}
