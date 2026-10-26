package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.dados.GeradorFixo;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;
import com.projeto_rpg.regras.historico.RegistroDeRolagem;
import com.projeto_rpg.regras.historico.TipoRolagem;
import com.projeto_rpg.regras.mecanica.ModoDeRolagem;
import com.projeto_rpg.regras.mecanica.ResultadoTeste;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import(MotorDeTeste.class)
class ServicoDeRolagemTest {

    private static final long PERSONAGEM = 1L;

    @Autowired
    private ServicoDeRolagem servico;

    @Autowired
    private ServicoDeHistorico historico;

    @Autowired
    private GeradorFixo gerador;

    @Test
    @DisplayName("deve rolar uma expressão livre e registrar no histórico")
    void deveRolarExpressaoERegistrarNoHistorico() {
        gerador.enfileirar(4, 5);

        ResultadoRolagem resultado = servico.rolar(PERSONAGEM, "2d6+3");

        assertEquals(12, resultado.total());
        RegistroDeRolagem registro = historico.consultarPorPersonagem(PERSONAGEM).getFirst();
        assertAll(
                () -> assertEquals(TipoRolagem.LIVRE, registro.getTipo()),
                () -> assertEquals("2d6+3", registro.getDescricao()),
                () -> assertEquals(List.of(4, 5), registro.getDados()),
                () -> assertEquals(12, registro.getTotal()),
                () -> assertEquals(MotorDeTeste.AGORA, registro.getDataHora()));
    }

    @Test
    @DisplayName("não deve registrar nada quando a expressão for inválida")
    void naoDeveRegistrarQuandoExpressaoForInvalida() {
        assertThrows(ExpressaoInvalidaException.class, () -> servico.rolar(PERSONAGEM, "2d"));

        assertTrue(historico.consultarPorPersonagem(PERSONAGEM).isEmpty());
    }

    @Test
    @DisplayName("deve lançar exceção sem rolar quando o personagem for nulo")
    void deveLancarExcecaoSemRolarQuandoPersonagemForNulo() {
        gerador.enfileirar(3);

        assertThrows(IllegalArgumentException.class, () -> servico.rolar(null, "1d6"));
        assertThrows(IllegalArgumentException.class, () -> servico.testar(null, 0, 10, ModoDeRolagem.NORMAL));
        assertEquals(false, gerador.consumiuTudo());
    }

    @Test
    @DisplayName("deve fazer um teste com vantagem e registrá-lo no histórico")
    void deveTestarComVantagemERegistrar() {
        gerador.enfileirar(7, 18);

        ResultadoTeste resultado = servico.testar(PERSONAGEM, 3, 15, ModoDeRolagem.VANTAGEM);

        assertTrue(resultado.sucesso());
        RegistroDeRolagem registro = historico.consultarPorPersonagemETipo(PERSONAGEM, TipoRolagem.TESTE).getFirst();
        assertAll(
                () -> assertEquals("d20+3 contra 15 (VANTAGEM)", registro.getDescricao()),
                () -> assertEquals(List.of(7, 18), registro.getDados()),
                () -> assertEquals(21, registro.getTotal()));
    }
}
