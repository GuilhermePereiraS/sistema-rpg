package com.projeto_rpg.regras.mecanica;

import java.util.List;

/**
 * Resultado de um ataque.
 *
 * @param teste       rolagem de ataque contra a defesa
 * @param acertou     se o ataque acertou
 * @param critico     se o acerto foi crítico
 * @param dadosDeDano dados de dano rolados (vazio quando erra)
 * @param dano        dano causado, nunca negativo (0 quando erra)
 */
public record ResultadoAtaque(ResultadoTeste teste, boolean acertou, boolean critico, List<Integer> dadosDeDano,
                              int dano) {

    public ResultadoAtaque {
        dadosDeDano = List.copyOf(dadosDeDano);
    }
}
