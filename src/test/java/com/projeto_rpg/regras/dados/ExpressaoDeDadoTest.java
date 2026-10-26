package com.projeto_rpg.regras.dados;

import com.projeto_rpg.regras.excecao.ExpressaoInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExpressaoDeDadoTest {

    @Test
    @DisplayName("deve lançar exceção quando não houver nenhum termo de dado")
    void deveLancarExcecaoQuandoNaoHouverTermos() {
        assertThrows(ExpressaoInvalidaException.class, () -> new ExpressaoDeDado(List.of(), 3));
        assertThrows(ExpressaoInvalidaException.class, () -> new ExpressaoDeDado(null, 3));
    }

    @Test
    @DisplayName("deve proteger a lista de termos contra alterações externas")
    void deveProtegerTermosQuandoListaOriginalForAlterada() {
        List<TermoDeDado> termos = new ArrayList<>(List.of(TermoDeDado.simples(1, 6)));
        ExpressaoDeDado expressao = new ExpressaoDeDado(termos, 0);

        termos.add(TermoDeDado.simples(1, 4));

        assertEquals(1, expressao.termos().size());
    }

    @Test
    @DisplayName("deve dobrar quantidade e dados mantidos de cada termo, preservando o modificador")
    void deveDobrarDadosPreservandoModificador() {
        ExpressaoDeDado expressao = new ExpressaoDeDado(
                List.of(TermoDeDado.simples(2, 6), TermoDeDado.mantendoMaiores(2, 20, 1)), 3);

        ExpressaoDeDado dobrada = expressao.comDadosDobrados();

        assertEquals(List.of(TermoDeDado.simples(4, 6), TermoDeDado.mantendoMaiores(4, 20, 2)), dobrada.termos());
        assertEquals(3, dobrada.modificador());
    }
}
