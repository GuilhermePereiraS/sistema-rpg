package com.projeto_rpg.regras.combate;

import com.projeto_rpg.regras.excecao.TesteContraMorteNaoPermitidoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestesContraMorteTest {

    private static final int PV_MAXIMO = 20;

    private final RegraDeMorte dnd = TestesContraMorte.dnd5e();
    private EstadoDeCombate estado;

    @BeforeEach
    void derrubarPersonagem() {
        estado = new EstadoDeCombate(1L, PV_MAXIMO);
        estado.sofrerDano(PV_MAXIMO, false, dnd);
    }

    private ResultadoTesteContraMorte testar(int dadoNatural) {
        return estado.realizarTesteContraMorte(dadoNatural, dnd);
    }

    @Test
    @DisplayName("deve registrar sucesso quando o dado for igual à CD (10)")
    void deveRegistrarSucessoQuandoDadoForIgualACd() {
        ResultadoTesteContraMorte resultado = testar(10);

        assertAll(
                () -> assertTrue(resultado.sucesso()),
                () -> assertEquals(10, resultado.dadoNatural()),
                () -> assertEquals(1, resultado.sucessos()),
                () -> assertEquals(0, resultado.falhas()),
                () -> assertEquals(Condicao.INCONSCIENTE, resultado.condicao()));
    }

    @Test
    @DisplayName("deve registrar falha quando o dado for menor que a CD")
    void deveRegistrarFalhaQuandoDadoForMenorQueCd() {
        ResultadoTesteContraMorte resultado = testar(9);

        assertFalse(resultado.sucesso());
        assertEquals(1, resultado.falhas());
    }

    @Test
    @DisplayName("deve registrar duas falhas quando o dado natural for 1")
    void deveRegistrarDuasFalhasQuandoDadoNaturalForUm() {
        ResultadoTesteContraMorte resultado = testar(1);

        assertEquals(2, resultado.falhas());
    }

    @Test
    @DisplayName("deve recuperar 1 PV e a consciência quando o dado natural for 20")
    void deveReanimarComUmPvQuandoDadoNaturalForVinte() {
        testar(5);

        ResultadoTesteContraMorte resultado = testar(20);

        assertAll(
                () -> assertTrue(resultado.sucesso()),
                () -> assertEquals(Condicao.CONSCIENTE, resultado.condicao()),
                () -> assertEquals(0, resultado.falhas()),
                () -> assertEquals(0, resultado.sucessos()),
                () -> assertEquals(1, estado.getPvAtual()));
    }

    @Test
    @DisplayName("deve estabilizar e zerar os contadores após três sucessos")
    void deveEstabilizarQuandoAcumularTresSucessos() {
        testar(12);
        testar(4);
        testar(15);

        ResultadoTesteContraMorte resultado = testar(11);

        assertEquals(Condicao.ESTAVEL, resultado.condicao());
        assertEquals(0, estado.getSucessos());
        assertEquals(0, estado.getFalhas());
    }

    @Test
    @DisplayName("deve morrer após três falhas")
    void deveMorrerQuandoAcumularTresFalhas() {
        testar(2);
        testar(9);

        ResultadoTesteContraMorte resultado = testar(5);

        assertEquals(Condicao.MORTO, resultado.condicao());
        assertEquals(3, resultado.falhas());
    }

    @Test
    @DisplayName("deve voltar a fazer testes com uma falha quando um personagem estável sofrer dano")
    void deveDesestabilizarQuandoEstavelSofrerDano() {
        testar(10);
        testar(10);
        testar(10);

        estado.sofrerDano(2, false, dnd);

        assertEquals(Condicao.INCONSCIENTE, estado.getCondicao());
        assertEquals(1, estado.getFalhas());
    }

    @Test
    @DisplayName("deve recusar teste contra a morte quando o personagem estiver consciente")
    void deveRecusarTesteQuandoPersonagemEstiverConsciente() {
        EstadoDeCombate consciente = new EstadoDeCombate(2L, PV_MAXIMO);

        assertThrows(TesteContraMorteNaoPermitidoException.class,
                () -> consciente.realizarTesteContraMorte(10, dnd));
    }

    @Test
    @DisplayName("deve recusar teste contra a morte quando o personagem estiver estável")
    void deveRecusarTesteQuandoPersonagemEstiverEstavel() {
        testar(10);
        testar(10);
        testar(10);

        assertThrows(TesteContraMorteNaoPermitidoException.class, () -> testar(10));
    }

    @Test
    @DisplayName("deve recusar teste contra a morte quando o personagem estiver morto")
    void deveRecusarTesteQuandoPersonagemEstiverMorto() {
        testar(1);
        testar(1);

        assertThrows(TesteContraMorteNaoPermitidoException.class, () -> testar(10));
    }

    @ParameterizedTest(name = "cd={0}, sucessos={1}, falhas={2}")
    @CsvSource({"0, 3, 3", "10, 0, 3", "10, 3, 0"})
    @DisplayName("deve lançar exceção quando a configuração da regra não for positiva")
    void deveLancarExcecaoQuandoConfiguracaoForInvalida(int cd, int sucessos, int falhas) {
        assertThrows(IllegalArgumentException.class, () -> new TestesContraMorte(cd, sucessos, falhas, 20, 1));
    }
}
