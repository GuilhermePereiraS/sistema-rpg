package com.projeto_rpg.regras.config;

import com.projeto_rpg.regras.dados.GeradorAleatorio;
import com.projeto_rpg.regras.dados.GeradorAleatorioPadrao;
import com.projeto_rpg.regras.dados.ParserDeExpressao;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.mecanica.ResolvedorDeAtaque;
import com.projeto_rpg.regras.mecanica.RoladorDeTeste;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;
import com.projeto_rpg.regras.perfil.PerfisDeRegras;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Monta o motor de regras no Spring. As classes de domínio não dependem do framework.
 *
 * <p>O sistema de RPG em uso é escolhido pela propriedade {@code rpg.regras.perfil}
 * (padrão: {@code dnd5e}).
 */
@Configuration
public class ConfiguracaoDeRegras {

    @Bean
    public PerfilDeRegras perfilDeRegras(@Value("${rpg.regras.perfil:dnd5e}") String nomeDoPerfil) {
        return PerfisDeRegras.porNome(nomeDoPerfil);
    }

    @Bean
    public GeradorAleatorio geradorAleatorio() {
        return new GeradorAleatorioPadrao();
    }

    @Bean
    public ParserDeExpressao parserDeExpressao() {
        return new ParserDeExpressao();
    }

    @Bean
    public RoladorDeDados roladorDeDados(GeradorAleatorio gerador) {
        return new RoladorDeDados(gerador);
    }

    @Bean
    public RoladorDeTeste roladorDeTeste(GeradorAleatorio gerador, PerfilDeRegras perfil) {
        return new RoladorDeTeste(gerador, perfil);
    }

    @Bean
    public ResolvedorDeAtaque resolvedorDeAtaque(RoladorDeTeste roladorDeTeste, RoladorDeDados roladorDeDados,
                                                 PerfilDeRegras perfil) {
        return new ResolvedorDeAtaque(roladorDeTeste, roladorDeDados, perfil);
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
