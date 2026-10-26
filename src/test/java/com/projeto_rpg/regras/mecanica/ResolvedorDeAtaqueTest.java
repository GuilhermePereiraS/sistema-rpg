package com.projeto_rpg.regras.mecanica;

import com.projeto_rpg.regras.dados.GeradorFixo;
import com.projeto_rpg.regras.dados.ParserDeExpressao;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;
import com.projeto_rpg.regras.perfil.PerfisDeRegras;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolvedorDeAtaqueTest {

    private final ParserDeExpressao parser = new ParserDeExpressao();

    private ResultadoAtaque atacar(PerfilDeRegras perfil, int bonus, int defesa, String dano, ModoDeRolagem modo,
                                   Integer... valores) {
        GeradorFixo gerador = new GeradorFixo(valores);
        ResolvedorDeAtaque resolvedor = new ResolvedorDeAtaque(
                new RoladorDeTeste(gerador, perfil), new RoladorDeDados(gerador), perfil);
        return resolvedor.atacar(bonus, defesa, parser.interpretar(dano), modo);
    }

    private ResultadoAtaque atacarDnd(int bonus, int defesa, String dano, Integer... valores) {
        return atacar(PerfisDeRegras.dnd5e(), bonus, defesa, dano, ModoDeRolagem.NORMAL, valores);
    }

    @Test
    @DisplayName("deve acertar e rolar o dano quando o ataque alcançar a defesa")
    void deveAcertarERolarDanoQuandoAtaqueAlcancarDefesa() {
        ResultadoAtaque resultado = atacarDnd(3, 15, "1d8+2", 12, 5);

        assertAll(
                () -> assertTrue(resultado.acertou()),
                () -> assertFalse(resultado.critico()),
                () -> assertEquals(15, resultado.teste().total()),
                () -> assertEquals(List.of(5), resultado.dadosDeDano()),
                () -> assertEquals(7, resultado.dano()));
    }

    @Test
    @DisplayName("deve errar sem rolar dano quando o ataque ficar abaixo da defesa")
    void deveErrarSemRolarDanoQuandoAtaqueFicarAbaixoDaDefesa() {
        ResultadoAtaque resultado = atacarDnd(3, 15, "1d8+2", 11);

        assertFalse(resultado.acertou());
        assertEquals(List.of(), resultado.dadosDeDano());
        assertEquals(0, resultado.dano());
    }

    @Test
    @DisplayName("deve acertar sempre no crítico, mesmo contra defesa inalcançável, e dobrar os dados")
    void deveAcertarEDobrarDadosQuandoForCritico() {
        ResultadoAtaque resultado = atacarDnd(0, 30, "1d8+2", 20, 3, 4);

        assertTrue(resultado.acertou());
        assertTrue(resultado.critico());
        assertEquals(List.of(3, 4), resultado.dadosDeDano());
        assertEquals(9, resultado.dano());
    }

    @Test
    @DisplayName("deve errar sempre na falha crítica, mesmo com bônus altíssimo")
    void deveErrarQuandoForFalhaCritica() {
        ResultadoAtaque resultado = atacarDnd(50, 10, "1d8", 1);

        assertFalse(resultado.acertou());
    }

    @Test
    @DisplayName("deve limitar o dano a zero quando o modificador negativo superar os dados")
    void deveLimitarDanoAZeroQuandoModificadorForMuitoNegativo() {
        ResultadoAtaque resultado = atacarDnd(3, 10, "1d4-5", 15, 1);

        assertTrue(resultado.acertou());
        assertEquals(0, resultado.dano());
    }

    @Test
    @DisplayName("deve aplicar vantagem no ataque")
    void deveAplicarVantagemQuandoModoForVantagem() {
        ResultadoAtaque resultado = atacar(PerfisDeRegras.dnd5e(), 0, 15, "1d6", ModoDeRolagem.VANTAGEM, 5, 17, 2);

        assertTrue(resultado.acertou());
        assertEquals(2, resultado.dano());
    }

    @Test
    @DisplayName("deve usar a regra de dano crítico do perfil percentual (maximizar)")
    void deveUsarRegraDeDanoCriticoDoPerfilQuandoSistemaForPercentual() {
        ResultadoAtaque resultado = atacar(PerfisDeRegras.percentual(), 0, 60, "1d6+1", ModoDeRolagem.NORMAL, 3);

        assertTrue(resultado.critico());
        assertEquals(7, resultado.dano());
    }
}
