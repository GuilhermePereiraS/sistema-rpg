package com.projeto_rpg.regras.dados;

import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converte texto em notação de dados para {@link ExpressaoDeDado}.
 *
 * <p>Notação aceita (sem diferenciar maiúsculas e ignorando espaços):
 * <ul>
 *   <li>{@code NdF}: N dados de F faces; N pode ser omitido ({@code d8} = {@code 1d8}).</li>
 *   <li>{@code NdFkhM} / {@code NdFklM}: mantém os M maiores/menores ({@code 4d6kh3}, {@code 2d20kl1}).</li>
 *   <li>Termos e constantes ligados por {@code +} ou {@code -} ({@code 2d6+1d4-1}). Dados só podem ser somados.</li>
 * </ul>
 * Limites de entrada: até {@value #QUANTIDADE_MAXIMA} dados por termo e até {@value #FACES_MAXIMAS} faces.
 */
public class ParserDeExpressao {

    static final int QUANTIDADE_MAXIMA = 100;
    static final int FACES_MAXIMAS = 1000;

    private static final Pattern TERMO =
            Pattern.compile("\\G(?<sinal>[+-]?)(?:(?<quantidade>\\d*)d(?<faces>\\d+)"
                    + "(?:k(?<selecao>[hl])(?<mantidos>\\d+))?|(?<constante>\\d+))");

    public ExpressaoDeDado interpretar(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new ExpressaoInvalidaException("Expressão de dados não pode ser vazia");
        }
        String normalizado = normalizar(texto);
        Matcher matcher = TERMO.matcher(normalizado);
        List<TermoDeDado> termos = new ArrayList<>();
        int modificador = 0;
        int fimDoUltimoTermo = 0;
        while (matcher.find()) {
            boolean semSinal = matcher.group("sinal").isEmpty();
            boolean negativo = "-".equals(matcher.group("sinal"));
            if (fimDoUltimoTermo > 0 && semSinal) {
                throw malformada(texto);
            }
            if (matcher.group("constante") != null) {
                int valor = paraInteiro(matcher.group("constante"), texto);
                modificador += negativo ? -valor : valor;
            } else if (negativo) {
                throw new ExpressaoInvalidaException("Dados não podem ser subtraídos: " + texto);
            } else {
                termos.add(interpretarTermo(matcher, texto));
            }
            fimDoUltimoTermo = matcher.end();
        }
        if (fimDoUltimoTermo != normalizado.length()) {
            throw malformada(texto);
        }
        if (termos.isEmpty()) {
            throw new ExpressaoInvalidaException("Expressão precisa ter ao menos um dado: " + texto);
        }
        return new ExpressaoDeDado(termos, modificador);
    }

    private String normalizar(String texto) {
        return texto.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
    }

    private TermoDeDado interpretarTermo(Matcher matcher, String texto) {
        String quantidadeInformada = matcher.group("quantidade");
        int quantidade = quantidadeInformada.isEmpty() ? 1 : paraInteiro(quantidadeInformada, texto);
        int faces = paraInteiro(matcher.group("faces"), texto);
        validarLimitesDeEntrada(quantidade, faces, texto);

        String selecao = matcher.group("selecao");
        if (selecao == null) {
            return TermoDeDado.simples(quantidade, faces);
        }
        int mantidos = paraInteiro(matcher.group("mantidos"), texto);
        return "h".equals(selecao)
                ? TermoDeDado.mantendoMaiores(quantidade, faces, mantidos)
                : TermoDeDado.mantendoMenores(quantidade, faces, mantidos);
    }

    private void validarLimitesDeEntrada(int quantidade, int faces, String texto) {
        if (quantidade > QUANTIDADE_MAXIMA) {
            throw new ExpressaoInvalidaException(
                    "Quantidade de dados não pode passar de " + QUANTIDADE_MAXIMA + ": " + texto);
        }
        if (faces > FACES_MAXIMAS) {
            throw new ExpressaoInvalidaException(
                    "Faces do dado não podem passar de " + FACES_MAXIMAS + ": " + texto);
        }
    }

    private int paraInteiro(String numero, String texto) {
        try {
            return Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            throw new ExpressaoInvalidaException("Número muito grande na expressão: " + texto);
        }
    }

    private ExpressaoInvalidaException malformada(String texto) {
        return new ExpressaoInvalidaException("Expressão malformada: " + texto);
    }
}
