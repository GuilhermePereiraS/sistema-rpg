package com.projeto_rpg.regras.dados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Rola uma {@link ExpressaoDeDado} usando um {@link GeradorAleatorio}.
 */
public class RoladorDeDados {

    private final GeradorAleatorio gerador;

    public RoladorDeDados(GeradorAleatorio gerador) {
        this.gerador = Objects.requireNonNull(gerador, "Gerador aleatório não pode ser nulo");
    }

    public ResultadoRolagem rolar(ExpressaoDeDado expressao) {
        List<Integer> todosOsDados = new ArrayList<>();
        int total = expressao.modificador();
        for (TermoDeDado termo : expressao.termos()) {
            List<Integer> dadosDoTermo = rolarTermo(termo);
            todosOsDados.addAll(dadosDoTermo);
            total += somarMantidos(termo, dadosDoTermo);
        }
        return new ResultadoRolagem(todosOsDados, total);
    }

    private List<Integer> rolarTermo(TermoDeDado termo) {
        return IntStream.range(0, termo.quantidade())
                .mapToObj(i -> gerador.rolar(termo.faces()))
                .toList();
    }

    private int somarMantidos(TermoDeDado termo, List<Integer> dados) {
        Stream<Integer> emOrdemDePrioridade = switch (termo.selecao()) {
            case TODOS -> dados.stream();
            case MAIORES -> dados.stream().sorted(Comparator.reverseOrder());
            case MENORES -> dados.stream().sorted();
        };
        return emOrdemDePrioridade.limit(termo.mantidos()).mapToInt(Integer::intValue).sum();
    }
}
