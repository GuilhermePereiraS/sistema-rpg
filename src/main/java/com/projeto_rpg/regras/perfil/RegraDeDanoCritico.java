package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;

/**
 * Define como o dano é calculado quando um ataque é crítico.
 */
public interface RegraDeDanoCritico {

    ResultadoRolagem rolarDanoCritico(ExpressaoDeDado dano, RoladorDeDados rolador);
}
