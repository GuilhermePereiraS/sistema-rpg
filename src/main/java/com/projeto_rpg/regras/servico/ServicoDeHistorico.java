package com.projeto_rpg.regras.servico;

import com.projeto_rpg.regras.historico.RegistroDeRolagem;
import com.projeto_rpg.regras.historico.RegistroDeRolagemRepository;
import com.projeto_rpg.regras.historico.TipoRolagem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.List;

/**
 * Registra e consulta o histórico de rolagens dos personagens.
 */
@Service
@Transactional
public class ServicoDeHistorico {

    private final RegistroDeRolagemRepository repository;
    private final Clock clock;

    public ServicoDeHistorico(RegistroDeRolagemRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public RegistroDeRolagem registrar(Long personagemId, TipoRolagem tipo, String descricao, List<Integer> dados,
                                       int total) {
        return repository.save(new RegistroDeRolagem(personagemId, tipo, descricao, dados, total, clock.instant()));
    }

    @Transactional(readOnly = true)
    public List<RegistroDeRolagem> consultarPorPersonagem(Long personagemId) {
        exigirPersonagem(personagemId);
        return repository.findByPersonagemIdOrderByDataHoraDescIdDesc(personagemId);
    }

    @Transactional(readOnly = true)
    public List<RegistroDeRolagem> consultarPorPersonagemETipo(Long personagemId, TipoRolagem tipo) {
        exigirPersonagem(personagemId);
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo da rolagem não pode ser nulo");
        }
        return repository.findByPersonagemIdAndTipoOrderByDataHoraDescIdDesc(personagemId, tipo);
    }

    private static void exigirPersonagem(Long personagemId) {
        if (personagemId == null) {
            throw new IllegalArgumentException("Personagem não pode ser nulo");
        }
    }
}
