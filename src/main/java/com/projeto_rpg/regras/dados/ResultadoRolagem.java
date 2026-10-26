package com.projeto_rpg.regras.dados;

import java.util.List;

/**
 * Resultado de uma rolagem.
 *
 * @param dados todos os dados rolados, na ordem, incluindo os descartados por kh/kl
 * @param total soma dos dados mantidos mais o modificador
 */
public record ResultadoRolagem(List<Integer> dados, int total) {

    public ResultadoRolagem {
        dados = List.copyOf(dados);
    }
}
