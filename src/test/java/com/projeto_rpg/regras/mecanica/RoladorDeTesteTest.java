package com.projeto_rpg.regras.mecanica;

import com.projeto_rpg.regras.dados.GeradorFixo;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;
import com.projeto_rpg.regras.perfil.PerfisDeRegras;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoladorDeTesteTest {

    private static ResultadoTeste testar(PerfilDeRegras perfil, int modificador, int alvo, ModoDeRolagem modo,
                                         Integer... dados) {
        return new RoladorDeTeste(new GeradorFixo(dados), perfil).testar(modificador, alvo, modo);
    }

    @Nested
    @DisplayName("Perfil D&D 5e (d20, rolar acima)")
    class Dnd {

        private final PerfilDeRegras dnd = PerfisDeRegras.dnd5e();

        @Test
        @DisplayName("deve ter sucesso quando dado + modificador alcançar a CD")
        void deveTerSucessoQuandoTotalAlcancarCd() {
            ResultadoTeste resultado = testar(dnd, 3, 15, ModoDeRolagem.NORMAL, 12);

            assertAll(
                    () -> assertEquals(List.of(12), resultado.dados()),
                    () -> assertEquals(12, resultado.dadoNatural()),
                    () -> assertEquals(3, resultado.modificador()),
                    () -> assertEquals(15, resultado.total()),
                    () -> assertEquals(15, resultado.alvo()),
                    () -> assertTrue(resultado.sucesso()),
                    () -> assertFalse(resultado.critico()),
                    () -> assertFalse(resultado.falhaCritica()));
        }

        @Test
        @DisplayName("deve falhar quando o total ficar abaixo da CD")
        void deveFalharQuandoTotalFicarAbaixoDaCd() {
            ResultadoTeste resultado = testar(dnd, 3, 15, ModoDeRolagem.NORMAL, 11);

            assertFalse(resultado.sucesso());
        }

        @Test
        @DisplayName("deve usar o maior de dois d20 quando houver vantagem")
        void deveUsarMaiorDadoQuandoHouverVantagem() {
            ResultadoTeste resultado = testar(dnd, 0, 15, ModoDeRolagem.VANTAGEM, 7, 18);

            assertEquals(List.of(7, 18), resultado.dados());
            assertEquals(18, resultado.dadoNatural());
            assertTrue(resultado.sucesso());
        }

        @Test
        @DisplayName("deve usar o menor de dois d20 quando houver desvantagem")
        void deveUsarMenorDadoQuandoHouverDesvantagem() {
            ResultadoTeste resultado = testar(dnd, 0, 15, ModoDeRolagem.DESVANTAGEM, 7, 18);

            assertEquals(7, resultado.dadoNatural());
            assertFalse(resultado.sucesso());
        }

        @Test
        @DisplayName("deve indicar crítico no 20 natural e falha crítica no 1 natural")
        void deveIndicarCriticoEFalhaCriticaQuandoDadoForExtremo() {
            assertTrue(testar(dnd, 0, 30, ModoDeRolagem.NORMAL, 20).critico());
            assertTrue(testar(dnd, 0, 2, ModoDeRolagem.NORMAL, 1).falhaCritica());
        }

        @Test
        @DisplayName("deve rolar o dado de teste do perfil")
        void deveRolarDadoDoPerfil() {
            GeradorFixo gerador = new GeradorFixo(10, 10);

            new RoladorDeTeste(gerador, dnd).testar(0, 10, ModoDeRolagem.VANTAGEM);

            assertEquals(List.of(20, 20), gerador.facesSolicitadas());
        }

        @Test
        @DisplayName("deve lançar exceção quando o modo de rolagem for nulo")
        void deveLancarExcecaoQuandoModoForNulo() {
            assertThrows(NullPointerException.class, () -> testar(dnd, 0, 10, null, 10));
        }
    }

    @Nested
    @DisplayName("Perfil percentual (d100, rolar abaixo)")
    class Percentual {

        private final PerfilDeRegras percentual = PerfisDeRegras.percentual();

        @Test
        @DisplayName("deve ter sucesso quando o d100 ficar abaixo ou igual à perícia")
        void deveTerSucessoQuandoDadoFicarAbaixoDoAlvo() {
            ResultadoTeste resultado = testar(percentual, 0, 60, ModoDeRolagem.NORMAL, 45);

            assertTrue(resultado.sucesso());
        }

        @Test
        @DisplayName("deve falhar quando o d100 passar da perícia")
        void deveFalharQuandoDadoPassarDoAlvo() {
            assertFalse(testar(percentual, 0, 60, ModoDeRolagem.NORMAL, 61).sucesso());
        }

        @Test
        @DisplayName("deve usar o MENOR dado na vantagem, pois em rolar abaixo o menor é melhor")
        void deveUsarMenorDadoQuandoHouverVantagemEmRolarAbaixo() {
            ResultadoTeste resultado = testar(percentual, 0, 60, ModoDeRolagem.VANTAGEM, 80, 30);

            assertEquals(30, resultado.dadoNatural());
            assertTrue(resultado.sucesso());
        }

        @Test
        @DisplayName("deve usar o MAIOR dado na desvantagem em rolar abaixo")
        void deveUsarMaiorDadoQuandoHouverDesvantagemEmRolarAbaixo() {
            assertEquals(80, testar(percentual, 0, 60, ModoDeRolagem.DESVANTAGEM, 80, 30).dadoNatural());
        }

        @Test
        @DisplayName("deve indicar crítico de 1 a 5 e falha crítica de 96 a 100")
        void deveIndicarFaixasDeCriticoDoPerfil() {
            assertTrue(testar(percentual, 0, 60, ModoDeRolagem.NORMAL, 3).critico());
            assertTrue(testar(percentual, 0, 60, ModoDeRolagem.NORMAL, 97).falhaCritica());
        }
    }
}
