package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.config.ConfiguracaoDeRegras;
import com.projeto_rpg.regras.dados.GeradorFixo;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

/**
 * Motor de regras real para os testes de serviço, trocando apenas a aleatoriedade
 * (por um {@link GeradorFixo}) e o relógio (por um relógio fixo).
 */
@TestConfiguration
@Import({ConfiguracaoDeRegras.class, ServicoDeHistorico.class, ServicoDeRolagem.class})
public class MotorDeTeste {

    public static final Instant AGORA = Instant.parse("2026-03-01T12:00:00Z");

    @Bean
    @Primary
    public GeradorFixo geradorFixo() {
        return new GeradorFixo();
    }

    @Bean
    @Primary
    public Clock relogioFixo() {
        return Clock.fixed(AGORA, ZoneOffset.UTC);
    }
}
