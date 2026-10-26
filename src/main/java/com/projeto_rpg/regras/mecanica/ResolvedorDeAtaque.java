package com.projeto_rpg.regras.mecanica;

import com.projeto_rpg.regras.dados.ExpressaoDeDado;
import com.projeto_rpg.regras.dados.ResultadoRolagem;
import com.projeto_rpg.regras.dados.RoladorDeDados;
import com.projeto_rpg.regras.perfil.PerfilDeRegras;

import java.util.List;
import java.util.Objects;

/**
 * Resolve um ataque: teste contra a defesa e, se acertar, rolagem de dano.
 * Crítico sempre acerta; falha crítica sempre erra.
 */
public class ResolvedorDeAtaque {

    private final RoladorDeTeste roladorDeTeste;
    private final RoladorDeDados roladorDeDados;
    private final PerfilDeRegras perfil;

    public ResolvedorDeAtaque(RoladorDeTeste roladorDeTeste, RoladorDeDados roladorDeDados, PerfilDeRegras perfil) {
        this.roladorDeTeste = Objects.requireNonNull(roladorDeTeste, "Rolador de teste não pode ser nulo");
        this.roladorDeDados = Objects.requireNonNull(roladorDeDados, "Rolador de dados não pode ser nulo");
        this.perfil = Objects.requireNonNull(perfil, "Perfil de regras não pode ser nulo");
    }

    public ResultadoAtaque atacar(int bonusDeAtaque, int defesaDoAlvo, ExpressaoDeDado dano, ModoDeRolagem modo) {
        Objects.requireNonNull(dano, "Expressão de dano não pode ser nula");
        ResultadoTeste teste = roladorDeTeste.testar(bonusDeAtaque, defesaDoAlvo, modo);
        if (!acertou(teste)) {
            return new ResultadoAtaque(teste, false, false, List.of(), 0);
        }
        ResultadoRolagem rolagemDeDano = teste.critico()
                ? perfil.regraDeDanoCritico().rolarDanoCritico(dano, roladorDeDados)
                : roladorDeDados.rolar(dano);
        return new ResultadoAtaque(teste, true, teste.critico(), rolagemDeDano.dados(),
                Math.max(0, rolagemDeDano.total()));
    }

    private static boolean acertou(ResultadoTeste teste) {
        if (teste.critico()) {
            return true;
        }
        if (teste.falhaCritica()) {
            return false;
        }
        return teste.sucesso();
    }
}
