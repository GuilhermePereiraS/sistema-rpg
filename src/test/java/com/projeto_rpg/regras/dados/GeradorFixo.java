package com.projeto_rpg.regras.dados;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * Dublê de teste: devolve os valores informados, na ordem, e registra as faces pedidas.
 * Falha se um valor não couber no dado pedido ou se os valores acabarem.
 */
public class GeradorFixo implements GeradorAleatorio {

    private final Deque<Integer> valores;
    private final List<Integer> facesSolicitadas = new ArrayList<>();

    public GeradorFixo(Integer... valores) {
        this.valores = new ArrayDeque<>(Arrays.asList(valores));
    }

    /**
     * Acrescenta valores ao fim da fila.
     */
    public void enfileirar(Integer... novosValores) {
        valores.addAll(Arrays.asList(novosValores));
    }

    /**
     * Descarta valores pendentes e o registro de faces (útil quando o gerador é um bean compartilhado).
     */
    public void reiniciar() {
        valores.clear();
        facesSolicitadas.clear();
    }

    /**
     * Indica se todos os valores enfileirados foram consumidos.
     */
    public boolean consumiuTudo() {
        return valores.isEmpty();
    }

    @Override
    public int rolar(int faces) {
        if (valores.isEmpty()) {
            throw new IllegalStateException("GeradorFixo sem valores restantes");
        }
        int valor = valores.poll();
        if (valor < 1 || valor > faces) {
            throw new IllegalStateException("Valor " + valor + " impossível em um d" + faces);
        }
        facesSolicitadas.add(faces);
        return valor;
    }

    public List<Integer> facesSolicitadas() {
        return List.copyOf(facesSolicitadas);
    }
}
