package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.combate.MorteAoZerar;
import com.projeto_rpg.regras.combate.TestesContraMorte;

import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Perfis de regras prontos. Novos sistemas podem ser adicionados aqui
 * ou montados diretamente com {@link PerfilDeRegras}.
 */
public final class PerfisDeRegras {

    private static final Map<String, Supplier<PerfilDeRegras>> PERFIS = Map.of(
            "dnd5e", PerfisDeRegras::dnd5e,
            "percentual", PerfisDeRegras::percentual);

    private PerfisDeRegras() {
    }

    /**
     * D&D 5e: d20, rolar acima, crítico no 20, falha no 1, dano crítico dobra os dados,
     * testes contra a morte.
     */
    public static PerfilDeRegras dnd5e() {
        return new PerfilDeRegras("dnd5e", 20, new RolarAcimaOuIgual(), new CriticoPorFaixa(20, 20, 1, 1),
                new DobrarDados(), TestesContraMorte.dnd5e());
    }

    /**
     * Sistemas percentuais (estilo Call of Cthulhu): d100, rolar abaixo, crítico de 1 a 5,
     * falha de 96 a 100, dano crítico maximizado, morte ao zerar o PV.
     */
    public static PerfilDeRegras percentual() {
        return new PerfilDeRegras("percentual", 100, new RolarAbaixoOuIgual(), new CriticoPorFaixa(1, 5, 96, 100),
                new MaximizarDados(), new MorteAoZerar());
    }

    /**
     * @throws IllegalArgumentException se o nome for vazio ou não corresponder a um perfil
     */
    public static PerfilDeRegras porNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do perfil não pode ser vazio");
        }
        Supplier<PerfilDeRegras> perfil = PERFIS.get(nome.trim().toLowerCase(Locale.ROOT));
        if (perfil == null) {
            throw new IllegalArgumentException(
                    "Perfil de regras desconhecido: " + nome + ". Disponíveis: " + PERFIS.keySet());
        }
        return perfil.get();
    }
}
