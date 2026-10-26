package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.dados.GeradorFixo;
import com.projeto_rpg.regras.dados.ParserDeExpressao;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegraDeDanoCriticoTest {

    private final ParserDeExpressao parser = new ParserDeExpressao();

    private ResultadoRolagem rolarCritico(RegraDeDanoCritico regra, String expressao, Integer... valores) {
        return regra.rolarDanoCritico(parser.interpretar(expressao), new RoladorDeDados(new GeradorFixo(valores)));
    }

    @Test
    @DisplayName("DobrarDados: deve rolar o dobro de dados e somar o modificador uma única vez")
    void deveDobrarSomenteOsDadosQuandoRegraForDobrarDados() {
        ResultadoRolagem resultado = rolarCritico(new DobrarDados(), "1d8+3", 5, 7);

        assertEquals(List.of(5, 7), resultado.dados());
        assertEquals(15, resultado.total());
    }

    @Test
    @DisplayName("DobrarDados: deve dobrar cada termo de uma expressão composta")
    void deveDobrarCadaTermoQuandoExpressaoForComposta() {
        ResultadoRolagem resultado = rolarCritico(new DobrarDados(), "2d6+1d4+2", 1, 2, 3, 4, 1, 2);

        assertEquals(List.of(1, 2, 3, 4, 1, 2), resultado.dados());
        assertEquals(15, resultado.total());
    }

    @Test
    @DisplayName("DobrarTotal: deve rolar normalmente e dobrar o total, incluindo o modificador")
    void deveDobrarOTotalQuandoRegraForDobrarTotal() {
        ResultadoRolagem resultado = rolarCritico(new DobrarTotal(), "1d8+3", 5);

        assertEquals(List.of(5), resultado.dados());
        assertEquals(16, resultado.total());
    }

    @Test
    @DisplayName("MaximizarDados: deve usar o valor máximo de cada dado mantido sem rolar")
    void deveMaximizarDadosQuandoRegraForMaximizarDados() {
        ResultadoRolagem resultado = rolarCritico(new MaximizarDados(), "2d6+1d4+2");

        assertEquals(List.of(6, 6, 4), resultado.dados());
        assertEquals(18, resultado.total());
    }

    @Test
    @DisplayName("MaximizarDados: deve considerar apenas os dados mantidos em kh/kl")
    void deveMaximizarSomenteMantidosQuandoTermoTiverSelecao() {
        ResultadoRolagem resultado = rolarCritico(new MaximizarDados(), "4d6kh3");

        assertEquals(18, resultado.total());
    }

    @Test
    @DisplayName("SemDanoCritico: deve rolar o dano normalmente")
    void deveRolarNormalmenteQuandoSistemaNaoTiverDanoCritico() {
        ResultadoRolagem resultado = rolarCritico(new SemDanoCritico(), "1d8+3", 5);

        assertEquals(8, resultado.total());
    }
}
