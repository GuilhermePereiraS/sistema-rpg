package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.combate.Condicao;
import com.projeto_rpg.regras.dados.GeradorFixo;
import com.projeto_rpg.regras.excecao.TesteContraMorteNaoPermitidoException;
import com.projeto_rpg.regras.historico.TipoRolagem;
import com.projeto_rpg.regras.mecanica.ModoDeRolagem;
import com.projeto_rpg.regras.mecanica.ResultadoAtaque;
import com.projeto_rpg.regras.mecanica.ResultadoTeste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Os mesmos serviços, com o perfil percentual (d100, rolar abaixo, morte ao zerar):
 * prova que o motor não depende de D&D.
 */
@DataJpaTest
@Import(MotorDeTeste.class)
@TestPropertySource(properties = "rpg.regras.perfil=percentual")
class ServicoComPerfilPercentualTest {

    private static final long INVESTIGADOR = 1L;
    private static final long CULTISTA = 2L;

    @Autowired
    private ServicoDeCombate combate;

    @Autowired
    private ServicoDeRolagem rolagem;

    @Autowired
    private ServicoDeHistorico historico;

    @Autowired
    private GeradorFixo gerador;

    @BeforeEach
    void preparar() {
        gerador.reiniciar();
        combate.iniciarCombate(CULTISTA, 12);
    }

    @Test
    @DisplayName("deve fazer teste de perícia em d100 rolando abaixo, com dado bônus ficando com o menor")
    void deveTestarPericiaRolandoAbaixoComDadoBonus() {
        gerador.enfileirar(80, 30);

        ResultadoTeste resultado = rolagem.testar(INVESTIGADOR, 0, 60, ModoDeRolagem.VANTAGEM);

        assertEquals(30, resultado.dadoNatural());
        assertTrue(resultado.sucesso());
        assertEquals("d100+0 contra 60 (VANTAGEM)",
                historico.consultarPorPersonagemETipo(INVESTIGADOR, TipoRolagem.TESTE).getFirst().getDescricao());
    }

    @Test
    @DisplayName("deve maximizar o dano no crítico (1 a 5 no d100)")
    void deveMaximizarDanoQuandoAtaqueForCritico() {
        gerador.enfileirar(3);

        ResultadoAtaque resultado = combate.atacar(INVESTIGADOR, CULTISTA, 0, 50, "1d6+1", ModoDeRolagem.NORMAL);

        assertTrue(resultado.critico());
        assertEquals(7, resultado.dano());
        assertEquals(5, combate.consultarEstado(CULTISTA).getPvAtual());
    }

    @Test
    @DisplayName("deve morrer ao chegar a 0 PV, sem testes contra a morte")
    void deveMorrerAoZerarPvSemTestesContraMorte() {
        combate.aplicarDano(CULTISTA, 12, false);

        assertEquals(Condicao.MORTO, combate.consultarEstado(CULTISTA).getCondicao());
        gerador.enfileirar(50);
        assertThrows(TesteContraMorteNaoPermitidoException.class, () -> combate.realizarTesteContraMorte(CULTISTA));
    }
}
