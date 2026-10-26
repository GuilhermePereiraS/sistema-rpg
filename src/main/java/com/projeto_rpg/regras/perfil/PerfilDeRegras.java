package com.projeto_rpg.regras.perfil;

import com.projeto_rpg.regras.combate.RegraDeMorte;

import java.util.Objects;

/**
 * Conjunto de regras de um sistema de RPG. Trocar o perfil troca o comportamento
 * do motor sem alterar seu código.
 *
 * @param nome               identificador do perfil
 * @param facesDoDadoDeTeste dado usado em testes e ataques (20 no D&D, 100 em sistemas percentuais)
 * @param criterioDeSucesso  como comparar o total com o alvo
 * @param regraDeCritico     quando há crítico ou falha crítica
 * @param regraDeDanoCritico como calcular dano crítico
 * @param regraDeMorte       o que acontece com 0 PV
 */
public record PerfilDeRegras(String nome,
                             int facesDoDadoDeTeste,
                             CriterioDeSucesso criterioDeSucesso,
                             RegraDeCritico regraDeCritico,
                             RegraDeDanoCritico regraDeDanoCritico,
                             RegraDeMorte regraDeMorte) {

    public PerfilDeRegras {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do perfil não pode ser vazio");
        }
        if (facesDoDadoDeTeste < 2) {
            throw new IllegalArgumentException("Dado de teste deve ter ao menos 2 faces");
        }
        Objects.requireNonNull(criterioDeSucesso, "Critério de sucesso é obrigatório");
        Objects.requireNonNull(regraDeCritico, "Regra de crítico é obrigatória");
        Objects.requireNonNull(regraDeDanoCritico, "Regra de dano crítico é obrigatória");
        Objects.requireNonNull(regraDeMorte, "Regra de morte é obrigatória");
    }
}
