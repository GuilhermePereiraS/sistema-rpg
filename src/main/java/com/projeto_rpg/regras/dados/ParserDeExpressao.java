package com.projeto_rpg.regras.dados;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte texto em notação de dados ("2d6", "d8") para {@link ExpressaoDeDado}.
 */
public class ParserDeExpressao {

    private static final Pattern TERMO = Pattern.compile("(\\d*)d(\\d+)");

    public ExpressaoDeDado interpretar(String texto) {
        Matcher matcher = TERMO.matcher(texto);
        matcher.matches();
        int quantidade = matcher.group(1).isEmpty() ? 1 : Integer.parseInt(matcher.group(1));
        int faces = Integer.parseInt(matcher.group(2));
        return new ExpressaoDeDado(List.of(TermoDeDado.simples(quantidade, faces)), 0);
    }
}
