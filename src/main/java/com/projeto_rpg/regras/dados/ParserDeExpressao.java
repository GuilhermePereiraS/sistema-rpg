package com.projeto_rpg.regras.dados;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte texto em notação de dados ("2d6", "d8", "2d6+1d4-1") para {@link ExpressaoDeDado}.
 */
public class ParserDeExpressao {

    private static final Pattern TERMO = Pattern.compile("\\G([+-]?)(?:(\\d*)d(\\d+)|(\\d+))");

    public ExpressaoDeDado interpretar(String texto) {
        String normalizado = texto.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        Matcher matcher = TERMO.matcher(normalizado);
        List<TermoDeDado> termos = new ArrayList<>();
        int modificador = 0;
        while (matcher.find()) {
            if (matcher.group(4) != null) {
                int valor = Integer.parseInt(matcher.group(4));
                modificador += "-".equals(matcher.group(1)) ? -valor : valor;
            } else {
                int quantidade = matcher.group(2).isEmpty() ? 1 : Integer.parseInt(matcher.group(2));
                int faces = Integer.parseInt(matcher.group(3));
                termos.add(TermoDeDado.simples(quantidade, faces));
            }
        }
        return new ExpressaoDeDado(termos, modificador);
    }
}
