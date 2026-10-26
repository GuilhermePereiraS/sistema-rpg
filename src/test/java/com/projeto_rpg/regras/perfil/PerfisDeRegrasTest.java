package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.combate.MorteAoZerar;
import com.projeto_rpg.regras.combate.TestesContraMorte;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PerfisDeRegrasTest {

    @Test
    @DisplayName("deve montar o perfil D&D 5e: d20, rolar acima, crítico no 20, dobrar dados e testes contra a morte")
    void deveMontarPerfilDnd5e() {
        PerfilDeRegras perfil = PerfisDeRegras.dnd5e();

        assertAll(
                () -> assertEquals("dnd5e", perfil.nome()),
                () -> assertEquals(20, perfil.facesDoDadoDeTeste()),
                () -> assertEquals(new RolarAcimaOuIgual(), perfil.criterioDeSucesso()),
                () -> assertEquals(new CriticoPorFaixa(20, 20, 1, 1), perfil.regraDeCritico()),
                () -> assertEquals(new DobrarDados(), perfil.regraDeDanoCritico()),
                () -> assertEquals(TestesContraMorte.dnd5e(), perfil.regraDeMorte()));
    }

    @Test
    @DisplayName("deve montar o perfil percentual: d100, rolar abaixo, crítico 1-5, falha 96-100 e morte ao zerar")
    void deveMontarPerfilPercentual() {
        PerfilDeRegras perfil = PerfisDeRegras.percentual();

        assertAll(
                () -> assertEquals("percentual", perfil.nome()),
                () -> assertEquals(100, perfil.facesDoDadoDeTeste()),
                () -> assertEquals(new RolarAbaixoOuIgual(), perfil.criterioDeSucesso()),
                () -> assertEquals(new CriticoPorFaixa(1, 5, 96, 100), perfil.regraDeCritico()),
                () -> assertEquals(new MaximizarDados(), perfil.regraDeDanoCritico()),
                () -> assertEquals(new MorteAoZerar(), perfil.regraDeMorte()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"dnd5e", "DND5E", " percentual "})
    @DisplayName("deve encontrar o perfil pelo nome, ignorando maiúsculas e espaços")
    void deveEncontrarPerfilQuandoNomeExistir(String nome) {
        PerfilDeRegras perfil = PerfisDeRegras.porNome(nome);

        assertEquals(nome.trim().toLowerCase(), perfil.nome());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"gurps-inexistente"})
    @DisplayName("deve lançar exceção quando o nome do perfil for vazio ou desconhecido")
    void deveLancarExcecaoQuandoPerfilNaoExistir(String nome) {
        assertThrows(IllegalArgumentException.class, () -> PerfisDeRegras.porNome(nome));
    }

    @Test
    @DisplayName("deve lançar exceção ao montar perfil com dado de teste de menos de 2 faces")
    void deveLancarExcecaoQuandoDadoDeTesteForInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new PerfilDeRegras("x", 1, new RolarAcimaOuIgual(),
                new CriticoPorFaixa(20, 20, 1, 1), new DobrarDados(), new MorteAoZerar()));
    }

    @Test
    @DisplayName("deve lançar exceção ao montar perfil sem alguma das regras")
    void deveLancarExcecaoQuandoAlgumaRegraForNula() {
        assertThrows(NullPointerException.class, () -> new PerfilDeRegras("x", 20, null,
                new CriticoPorFaixa(20, 20, 1, 1), new DobrarDados(), new MorteAoZerar()));
    }

    @Test
    @DisplayName("deve lançar exceção ao montar perfil sem nome")
    void deveLancarExcecaoQuandoNomeForVazio() {
        assertThrows(IllegalArgumentException.class, () -> new PerfilDeRegras(" ", 20, new RolarAcimaOuIgual(),
                new CriticoPorFaixa(20, 20, 1, 1), new DobrarDados(), new MorteAoZerar()));
    }
}
