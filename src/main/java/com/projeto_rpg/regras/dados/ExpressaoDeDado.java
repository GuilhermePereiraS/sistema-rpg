package com.projeto_rpg.regras.dados;

import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;

import java.util.List;

/**
 * Expressão de dados já interpretada, como "2d6+3". Imutável.
 *
 * @param termos      grupos de dados (ao menos um)
 * @param modificador valor fixo somado ao resultado dos dados
 */
public record ExpressaoDeDado(List<TermoDeDado> termos, int modificador) {

    public ExpressaoDeDado {
        if (termos == null || termos.isEmpty()) {
            throw new ExpressaoInvalidaException("Expressão precisa ter ao menos um dado");
        }
        termos = List.copyOf(termos);
    }

    /**
     * Mesma expressão com todos os dados dobrados e o modificador inalterado.
     */
    public ExpressaoDeDado comDadosDobrados() {
        return new ExpressaoDeDado(termos.stream().map(TermoDeDado::dobrado).toList(), modificador);
    }
}
