package com.projeto_rpg.regras.mecanica;

import java.util.List;

/**
 * Resultado de um teste (perícia, resistência, ataque...).
 *
 * @param dados        dados rolados (dois na vantagem/desvantagem)
 * @param dadoNatural  dado considerado
 * @param modificador  modificador aplicado
 * @param total        dado natural + modificador
 * @param alvo         CD, perícia ou defesa contra a qual o teste foi feito
 * @param sucesso      se o total venceu o alvo pelo critério do perfil
 * @param critico      se o dado natural está na faixa de crítico
 * @param falhaCritica se o dado natural está na faixa de falha crítica
 */
public record ResultadoTeste(List<Integer> dados, int dadoNatural, int modificador, int total, int alvo,
                             boolean sucesso, boolean critico, boolean falhaCritica) {

    public ResultadoTeste {
        dados = List.copyOf(dados);
    }
}
