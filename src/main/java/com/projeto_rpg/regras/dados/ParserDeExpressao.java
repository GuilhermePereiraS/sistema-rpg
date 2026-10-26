package com.projeto_rpg.regras.dados;

import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte texto em notação de dados ("2d6", "d8", "2d6+1d4-1", "4d6kh3") para {@link ExpressaoDeDado}.
 */
public class ParserDeExpressao {

    private static final Pattern TERMO =
            Pattern.compile("\\G([+-]?)(?:(\\d*)d(\\d+)(?:k([hl])(\\d+))?|(\\d+))");

    public ExpressaoDeDado interpretar(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ExpressaoInvalidaException("Expressão de dados não pode ser vazia");
        }
        String normalizado = texto.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
        Matcher matcher = TERMO.matcher(normalizado);
        List<TermoDeDado> termos = new ArrayList<>();
        int modificador = 0;
        int fimDoUltimoTermo = 0;
        while (matcher.find()) {
            if (fimDoUltimoTermo > 0 && matcher.group(1).isEmpty()) {
                throw new ExpressaoInvalidaException("Expressão malformada: " + texto);
            }
            if (matcher.group(6) != null) {
                int valor = paraInteiro(matcher.group(6), texto);
                modificador += "-".equals(matcher.group(1)) ? -valor : valor;
            } else {
                if ("-".equals(matcher.group(1))) {
                    throw new ExpressaoInvalidaException("Dados não podem ser subtraídos: " + texto);
                }
                int quantidade = matcher.group(2).isEmpty() ? 1 : paraInteiro(matcher.group(2), texto);
                int faces = paraInteiro(matcher.group(3), texto);
                if (quantidade < 1 || quantidade > 100) {
                    throw new ExpressaoInvalidaException("Quantidade de dados deve estar entre 1 e 100: " + texto);
                }
                if (faces < 2 || faces > 1000) {
                    throw new ExpressaoInvalidaException("Faces do dado devem estar entre 2 e 1000: " + texto);
                }
                if (matcher.group(4) != null) {
                    int mantidos = paraInteiro(matcher.group(5), texto);
                    if (mantidos < 1 || mantidos > quantidade) {
                        throw new ExpressaoInvalidaException(
                                "Dados mantidos devem estar entre 1 e a quantidade rolada: " + texto);
                    }
                    termos.add("h".equals(matcher.group(4))
                            ? TermoDeDado.mantendoMaiores(quantidade, faces, mantidos)
                            : TermoDeDado.mantendoMenores(quantidade, faces, mantidos));
                } else {
                    termos.add(TermoDeDado.simples(quantidade, faces));
                }
            }
            fimDoUltimoTermo = matcher.end();
        }
        if (fimDoUltimoTermo != normalizado.length()) {
            throw new ExpressaoInvalidaException("Expressão malformada: " + texto);
        }
        if (termos.isEmpty()) {
            throw new ExpressaoInvalidaException("Expressão precisa ter ao menos um dado: " + texto);
        }
        return new ExpressaoDeDado(termos, modificador);
    }

    private int paraInteiro(String numero, String texto) {
        try {
            return Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            throw new ExpressaoInvalidaException("Número muito grande na expressão: " + texto);
        }
    }
}
