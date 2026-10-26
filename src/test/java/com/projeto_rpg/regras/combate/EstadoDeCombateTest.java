package com.projeto_rpg.regras.combate;

import com.projeto_rpg.regras.excecao.PersonagemMortoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstadoDeCombateTest {

    private static final long PERSONAGEM_ID = 1L;
    private static final int PV_MAXIMO = 20;

    private final RegraDeMorte dnd = TestesContraMorte.dnd5e();

    private EstadoDeCombate novoEstado() {
        return new EstadoDeCombate(PERSONAGEM_ID, PV_MAXIMO);
    }

    private EstadoDeCombate estadoCaido() {
        EstadoDeCombate estado = novoEstado();
        estado.sofrerDano(PV_MAXIMO, false, dnd);
        return estado;
    }

    @Nested
    @DisplayName("Criação")
    class Criacao {

        @Test
        @DisplayName("deve iniciar com PV cheio, consciente e sem testes contra a morte")
        void deveIniciarComPvCheioQuandoCriado() {
            EstadoDeCombate estado = novoEstado();

            assertEquals(PERSONAGEM_ID, estado.getPersonagemId());
            assertEquals(PV_MAXIMO, estado.getPvAtual());
            assertEquals(PV_MAXIMO, estado.getPvMaximo());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
            assertEquals(0, estado.getSucessos());
            assertEquals(0, estado.getFalhas());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -5})
        @DisplayName("deve lançar exceção quando o PV máximo não for positivo")
        void deveLancarExcecaoQuandoPvMaximoNaoForPositivo(int pvMaximo) {
            assertThrows(IllegalArgumentException.class, () -> new EstadoDeCombate(PERSONAGEM_ID, pvMaximo));
        }

        @Test
        @DisplayName("deve lançar exceção quando o personagem for nulo")
        void deveLancarExcecaoQuandoPersonagemForNulo() {
            assertThrows(IllegalArgumentException.class, () -> new EstadoDeCombate(null, PV_MAXIMO));
        }
    }

    @Nested
    @DisplayName("Dano")
    class Dano {

        @Test
        @DisplayName("deve reduzir o PV quando o dano for menor que o PV atual")
        void deveReduzirPvQuandoDanoForMenorQueAtual() {
            EstadoDeCombate estado = novoEstado();

            estado.sofrerDano(7, false, dnd);

            assertEquals(13, estado.getPvAtual());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
        }

        @Test
        @DisplayName("deve ignorar dano zero")
        void deveIgnorarDanoQuandoValorForZero() {
            EstadoDeCombate estado = estadoCaido();

            estado.sofrerDano(0, true, dnd);

            assertEquals(0, estado.getFalhas());
        }

        @Test
        @DisplayName("deve lançar exceção quando o dano for negativo")
        void deveLancarExcecaoQuandoDanoForNegativo() {
            assertThrows(IllegalArgumentException.class, () -> novoEstado().sofrerDano(-1, false, dnd));
        }

        @Test
        @DisplayName("deve ficar inconsciente com 0 PV quando o dano zerar o PV sem excedente letal")
        void deveFicarInconscienteQuandoDanoZerarPv() {
            EstadoDeCombate estado = novoEstado();

            estado.sofrerDano(39, false, dnd);

            assertEquals(0, estado.getPvAtual());
            assertEquals(Condicao.INCONSCIENTE, estado.getCondicao());
        }

        @Test
        @DisplayName("deve morrer instantaneamente quando o excedente alcançar o PV máximo")
        void deveMorrerInstantaneamenteQuandoExcedenteAlcancarPvMaximo() {
            EstadoDeCombate estado = novoEstado();

            estado.sofrerDano(40, false, dnd);

            assertEquals(0, estado.getPvAtual());
            assertEquals(Condicao.MORTO, estado.getCondicao());
        }

        @Test
        @DisplayName("deve registrar uma falha quando sofrer dano com 0 PV")
        void deveRegistrarFalhaQuandoSofrerDanoComZeroPv() {
            EstadoDeCombate estado = estadoCaido();

            estado.sofrerDano(3, false, dnd);

            assertEquals(1, estado.getFalhas());
            assertEquals(Condicao.INCONSCIENTE, estado.getCondicao());
        }

        @Test
        @DisplayName("deve registrar duas falhas quando sofrer crítico com 0 PV")
        void deveRegistrarDuasFalhasQuandoSofrerCriticoComZeroPv() {
            EstadoDeCombate estado = estadoCaido();

            estado.sofrerDano(3, true, dnd);

            assertEquals(2, estado.getFalhas());
        }

        @Test
        @DisplayName("deve morrer quando acumular três falhas por dano")
        void deveMorrerQuandoAcumularFalhasPorDano() {
            EstadoDeCombate estado = estadoCaido();

            estado.sofrerDano(1, true, dnd);
            estado.sofrerDano(1, false, dnd);

            assertEquals(Condicao.MORTO, estado.getCondicao());
        }

        @Test
        @DisplayName("deve morrer quando o dano com 0 PV alcançar o PV máximo")
        void deveMorrerQuandoDanoComZeroPvAlcancarPvMaximo() {
            EstadoDeCombate estado = estadoCaido();

            estado.sofrerDano(PV_MAXIMO, false, dnd);

            assertEquals(Condicao.MORTO, estado.getCondicao());
        }

        @Test
        @DisplayName("deve lançar exceção quando um personagem morto sofrer dano")
        void deveLancarExcecaoQuandoMortoSofrerDano() {
            EstadoDeCombate estado = novoEstado();
            estado.sofrerDano(40, false, dnd);

            assertThrows(PersonagemMortoException.class, () -> estado.sofrerDano(1, false, dnd));
        }
    }

    @Nested
    @DisplayName("Cura e recuperação")
    class Cura {

        @Test
        @DisplayName("deve recuperar PV sem ultrapassar o máximo")
        void deveLimitarAoMaximoQuandoReceberCura() {
            EstadoDeCombate estado = novoEstado();
            estado.sofrerDano(10, false, dnd);

            estado.receberCura(50);

            assertEquals(PV_MAXIMO, estado.getPvAtual());
        }

        @Test
        @DisplayName("deve recuperar a consciência e zerar os testes quando curado com 0 PV")
        void deveRecuperarConscienciaQuandoCuradoComZeroPv() {
            EstadoDeCombate estado = estadoCaido();
            estado.sofrerDano(1, false, dnd);

            estado.receberCura(4);

            assertEquals(4, estado.getPvAtual());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
            assertEquals(0, estado.getFalhas());
            assertEquals(0, estado.getSucessos());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -3})
        @DisplayName("deve lançar exceção quando a cura não for positiva")
        void deveLancarExcecaoQuandoCuraNaoForPositiva(int cura) {
            assertThrows(IllegalArgumentException.class, () -> novoEstado().receberCura(cura));
        }

        @Test
        @DisplayName("deve lançar exceção quando tentar curar um personagem morto")
        void deveLancarExcecaoQuandoCurarMorto() {
            EstadoDeCombate estado = novoEstado();
            estado.sofrerDano(40, false, dnd);

            assertThrows(PersonagemMortoException.class, () -> estado.receberCura(5));
        }

        @Test
        @DisplayName("deve restaurar PV máximo, consciência e zerar testes na recuperação total")
        void deveRestaurarTudoQuandoRecuperarTotalmente() {
            EstadoDeCombate estado = estadoCaido();
            estado.sofrerDano(1, false, dnd);

            estado.recuperarTotalmente();

            assertEquals(PV_MAXIMO, estado.getPvAtual());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
            assertEquals(0, estado.getFalhas());
        }

        @Test
        @DisplayName("deve lançar exceção quando tentar recuperar totalmente um personagem morto")
        void deveLancarExcecaoQuandoRecuperarMorto() {
            EstadoDeCombate estado = novoEstado();
            estado.sofrerDano(40, false, dnd);

            assertThrows(PersonagemMortoException.class, estado::recuperarTotalmente);
        }
    }
}
