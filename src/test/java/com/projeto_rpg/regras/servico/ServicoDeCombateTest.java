package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.combate.Condicao;
import com.projeto_rpg.regras.combate.EstadoDeCombate;
import com.projeto_rpg.regras.combate.EstadoDeCombateRepository;
import com.projeto_rpg.regras.combate.ResultadoTesteContraMorte;
import com.projeto_rpg.regras.dados.GeradorFixo;
import com.projeto_rpg.regras.excecao.CombateJaIniciadoException;
import com.projeto_rpg.regras.excecao.EstadoDeCombateNaoEncontradoException;
import com.projeto_rpg.regras.historico.RegistroDeRolagem;
import com.projeto_rpg.regras.historico.TipoRolagem;
import com.projeto_rpg.regras.mecanica.ModoDeRolagem;
import com.projeto_rpg.regras.mecanica.ResultadoAtaque;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(MotorDeTeste.class)
class ServicoDeCombateTest {

    private static final long ATACANTE = 1L;
    private static final long ALVO = 2L;
    private static final int PV_MAXIMO = 20;

    @Autowired
    private ServicoDeCombate servico;

    @Autowired
    private ServicoDeHistorico historico;

    @Autowired
    private EstadoDeCombateRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GeradorFixo gerador;

    @BeforeEach
    void prepararCombate() {
        gerador.reiniciar();
        servico.iniciarCombate(ALVO, PV_MAXIMO);
    }

    /** Simula uma nova chamada: grava no banco e descarta o que está em memória. */
    private void novaChamada() {
        entityManager.flush();
        entityManager.clear();
    }

    private void derrubarAlvo() {
        servico.aplicarDano(ALVO, PV_MAXIMO, false);
        novaChamada();
    }

    @Nested
    @DisplayName("Início e consulta")
    class InicioEConsulta {

        @Test
        @DisplayName("deve iniciar o combate com PV cheio e consciente")
        void deveIniciarCombateComPvCheio() {
            EstadoDeCombate estado = servico.consultarEstado(ALVO);

            assertEquals(PV_MAXIMO, estado.getPvAtual());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
        }

        @Test
        @DisplayName("deve impedir iniciar dois combates para o mesmo personagem")
        void deveImpedirIniciarCombateDuasVezes() {
            assertThrows(CombateJaIniciadoException.class, () -> servico.iniciarCombate(ALVO, 30));
        }

        @Test
        @DisplayName("deve lançar exceção quando o personagem não estiver em combate")
        void deveLancarExcecaoQuandoPersonagemNaoEstiverEmCombate() {
            assertThrows(EstadoDeCombateNaoEncontradoException.class, () -> servico.consultarEstado(99L));
            assertThrows(EstadoDeCombateNaoEncontradoException.class, () -> servico.aplicarDano(99L, 5, false));
        }
    }

    @Nested
    @DisplayName("Ataque")
    class Ataque {

        @Test
        @DisplayName("deve aplicar o dano no alvo e registrar ataque e dano no histórico do atacante")
        void deveAplicarDanoNoAlvoQuandoAtaqueAcertar() {
            gerador.enfileirar(12, 6);

            ResultadoAtaque resultado = servico.atacar(ATACANTE, ALVO, 5, 15, "1d8+3", ModoDeRolagem.NORMAL);

            assertTrue(resultado.acertou());
            assertEquals(11, servico.consultarEstado(ALVO).getPvAtual());
            List<RegistroDeRolagem> registros = historico.consultarPorPersonagem(ATACANTE);
            assertAll(
                    () -> assertEquals(List.of(TipoRolagem.DANO, TipoRolagem.ATAQUE),
                            registros.stream().map(RegistroDeRolagem::getTipo).toList()),
                    () -> assertEquals("1d8+3", registros.get(0).getDescricao()),
                    () -> assertEquals(9, registros.get(0).getTotal()),
                    () -> assertEquals("d20+5 contra 15 (NORMAL)", registros.get(1).getDescricao()));
        }

        @Test
        @DisplayName("não deve alterar o alvo nem registrar dano quando o ataque errar")
        void naoDeveAlterarAlvoQuandoAtaqueErrar() {
            gerador.enfileirar(3);

            ResultadoAtaque resultado = servico.atacar(ATACANTE, ALVO, 5, 15, "1d8+3", ModoDeRolagem.NORMAL);

            assertFalse(resultado.acertou());
            assertEquals(PV_MAXIMO, servico.consultarEstado(ALVO).getPvAtual());
            assertEquals(List.of(TipoRolagem.ATAQUE), historico.consultarPorPersonagem(ATACANTE).stream()
                    .map(RegistroDeRolagem::getTipo).toList());
        }

        @Test
        @DisplayName("deve registrar o dano como crítico e somar duas falhas quando o alvo caído sofrer crítico")
        void deveSomarDuasFalhasQuandoAlvoCaidoSofrerCritico() {
            derrubarAlvo();
            gerador.enfileirar(20, 1, 1);

            servico.atacar(ATACANTE, ALVO, 5, 15, "1d4", ModoDeRolagem.NORMAL);

            assertEquals(2, servico.consultarEstado(ALVO).getFalhas());
            assertEquals("1d4 (crítico)", historico.consultarPorPersonagemETipo(ATACANTE, TipoRolagem.DANO)
                    .getFirst().getDescricao());
        }

        @Test
        @DisplayName("deve lançar exceção sem rolar quando o alvo não estiver em combate")
        void deveLancarExcecaoQuandoAlvoNaoEstiverEmCombate() {
            gerador.enfileirar(15);

            assertThrows(EstadoDeCombateNaoEncontradoException.class,
                    () -> servico.atacar(ATACANTE, 99L, 5, 15, "1d8", ModoDeRolagem.NORMAL));
            assertFalse(gerador.consumiuTudo());
        }
    }

    @Nested
    @DisplayName("Dano, cura e recuperação")
    class DanoECura {

        @Test
        @DisplayName("deve matar instantaneamente quando o dano excedente alcançar o PV máximo")
        void deveMatarInstantaneamenteQuandoExcedenteAlcancarPvMaximo() {
            EstadoDeCombate estado = servico.aplicarDano(ALVO, 2 * PV_MAXIMO, false);

            assertEquals(Condicao.MORTO, estado.getCondicao());
        }

        @Test
        @DisplayName("deve curar um valor fixo")
        void deveCurarValorFixo() {
            servico.aplicarDano(ALVO, 10, false);

            assertEquals(15, servico.curar(ALVO, 5).getPvAtual());
        }

        @Test
        @DisplayName("deve curar com rolagem, reanimar o personagem caído e registrar a cura no histórico do alvo")
        void deveCurarComRolagemERegistrar() {
            derrubarAlvo();
            gerador.enfileirar(3, 2);

            EstadoDeCombate estado = servico.curarComRolagem(ALVO, "2d4+2");

            assertEquals(7, estado.getPvAtual());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
            RegistroDeRolagem registro = historico.consultarPorPersonagemETipo(ALVO, TipoRolagem.CURA).getFirst();
            assertEquals(List.of(3, 2), registro.getDados());
        }

        @Test
        @DisplayName("deve recuperar totalmente o personagem")
        void deveRecuperarTotalmente() {
            derrubarAlvo();

            EstadoDeCombate estado = servico.recuperarTotalmente(ALVO);

            assertEquals(PV_MAXIMO, estado.getPvAtual());
            assertEquals(Condicao.CONSCIENTE, estado.getCondicao());
        }
    }

    @Nested
    @DisplayName("Testes contra a morte persistidos")
    class TestesContraMortePersistidos {

        @Test
        @DisplayName("deve acumular falhas entre chamadas e morrer na terceira")
        void deveAcumularFalhasEntreChamadasEMorrerNaTerceira() {
            derrubarAlvo();

            gerador.enfileirar(5);
            servico.realizarTesteContraMorte(ALVO);
            novaChamada();
            gerador.enfileirar(8);
            servico.realizarTesteContraMorte(ALVO);
            novaChamada();
            assertEquals(2, repository.findByPersonagemId(ALVO).orElseThrow().getFalhas());

            gerador.enfileirar(9);
            ResultadoTesteContraMorte ultimo = servico.realizarTesteContraMorte(ALVO);
            novaChamada();

            assertEquals(Condicao.MORTO, ultimo.condicao());
            assertEquals(Condicao.MORTO, repository.findByPersonagemId(ALVO).orElseThrow().getCondicao());
        }

        @Test
        @DisplayName("deve acumular sucessos entre chamadas e estabilizar no terceiro")
        void deveAcumularSucessosEntreChamadasEEstabilizar() {
            derrubarAlvo();

            for (int dado : new int[]{12, 15, 10}) {
                gerador.enfileirar(dado);
                servico.realizarTesteContraMorte(ALVO);
                novaChamada();
            }

            assertEquals(Condicao.ESTAVEL, servico.consultarEstado(ALVO).getCondicao());
        }

        @Test
        @DisplayName("deve registrar cada teste contra a morte no histórico do personagem")
        void deveRegistrarTesteContraMorteNoHistorico() {
            derrubarAlvo();
            gerador.enfileirar(14);

            servico.realizarTesteContraMorte(ALVO);

            RegistroDeRolagem registro =
                    historico.consultarPorPersonagemETipo(ALVO, TipoRolagem.TESTE_CONTRA_MORTE).getFirst();
            assertEquals(List.of(14), registro.getDados());
            assertEquals("d20 teste contra a morte", registro.getDescricao());
        }
    }
}
