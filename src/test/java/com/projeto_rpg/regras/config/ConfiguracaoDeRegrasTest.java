package com.projeto_rpg.regras.config;

import com.projeto_rpg.regras.dados.GeradorAleatorio;
import com.projeto_rpg.regras.dados.GeradorAleatorioPadrao;
import com.projeto_rpg.regras.dados.ParserDeExpressao;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.mecanica.ResolvedorDeAtaque;
import com.projeto_rpg.regras.mecanica.RoladorDeTeste;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.time.Clock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguracaoDeRegrasTest {

    private final ApplicationContextRunner contexto =
            new ApplicationContextRunner().withUserConfiguration(ConfiguracaoDeRegras.class);

    @Test
    @DisplayName("deve usar o perfil D&D 5e quando nenhum perfil for configurado")
    void deveUsarPerfilDnd5eQuandoNenhumPerfilForConfigurado() {
        contexto.run(ctx -> assertEquals("dnd5e", ctx.getBean(PerfilDeRegras.class).nome()));
    }

    @Test
    @DisplayName("deve selecionar o perfil pela propriedade rpg.regras.perfil")
    void deveSelecionarPerfilQuandoPropriedadeForInformada() {
        contexto.withPropertyValues("rpg.regras.perfil=percentual")
                .run(ctx -> assertEquals("percentual", ctx.getBean(PerfilDeRegras.class).nome()));
    }

    @Test
    @DisplayName("deve falhar ao iniciar quando o perfil configurado não existir")
    void deveFalharAoIniciarQuandoPerfilNaoExistir() {
        contexto.withPropertyValues("rpg.regras.perfil=inexistente")
                .run(ctx -> assertTrue(ctx.getStartupFailure() != null));
    }

    @Test
    @DisplayName("deve disponibilizar todos os componentes do motor com o gerador aleatório padrão")
    void deveDisponibilizarComponentesDoMotor() {
        contexto.run(ctx -> {
            assertInstanceOf(GeradorAleatorioPadrao.class, ctx.getBean(GeradorAleatorio.class));
            assertNotNull(ctx.getBean(ParserDeExpressao.class));
            assertNotNull(ctx.getBean(RoladorDeDados.class));
            assertNotNull(ctx.getBean(RoladorDeTeste.class));
            assertNotNull(ctx.getBean(ResolvedorDeAtaque.class));
            assertNotNull(ctx.getBean(Clock.class));
        });
    }
}
