package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.HabilidadeNaoEncontradaException;
import com.projeto_rpg.inventario.excecao.ReservaDeEspacosJaExistenteException;
import com.projeto_rpg.inventario.excecao.ReservaDeEspacosNaoEncontradaException;
import com.projeto_rpg.inventario.modelo.Habilidade;
import com.projeto_rpg.inventario.modelo.HabilidadeRepository;
import com.projeto_rpg.inventario.modelo.ReservaDeEspacos;
import com.projeto_rpg.inventario.modelo.ReservaDeEspacosRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso dos espaços de habilidade: criar a reserva, gastar ao usar uma habilidade
 * e recuperar tudo no descanso longo.
 */
@Service
@Transactional
public class ServicoDeReservaDeEspacos {

    private final ReservaDeEspacosRepository repository;
    private final HabilidadeRepository habilidades;

    public ServicoDeReservaDeEspacos(ReservaDeEspacosRepository repository, HabilidadeRepository habilidades) {
        this.repository = repository;
        this.habilidades = habilidades;
    }

    public ReservaDeEspacos criar(Long personagemId, List<Integer> maximos) {
        if (repository.existsByPersonagemId(personagemId)) {
            throw new ReservaDeEspacosJaExistenteException(personagemId);
        }
        return repository.save(new ReservaDeEspacos(personagemId, maximos));
    }

    @Transactional(readOnly = true)
    public ReservaDeEspacos buscar(Long personagemId) {
        return repository.findByPersonagemId(personagemId)
                .orElseThrow(() -> new ReservaDeEspacosNaoEncontradaException(personagemId));
    }

    @Transactional(readOnly = true)
    public List<ReservaDeEspacos> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public int disponiveis(Long personagemId, int nivel) {
        return buscar(personagemId).disponiveis(nivel);
    }

    public ReservaDeEspacos gastar(Long personagemId, int nivel) {
        ReservaDeEspacos reserva = buscar(personagemId);
        reserva.gastar(nivel);
        return repository.save(reserva);
    }

    public ReservaDeEspacos recuperar(Long personagemId, int nivel, int quantidade) {
        ReservaDeEspacos reserva = buscar(personagemId);
        reserva.recuperar(nivel, quantidade);
        return repository.save(reserva);
    }

    /**
     * Usa uma habilidade. Truques (nível 0) não consomem espaço; as demais consomem um
     * espaço do próprio nível.
     */
    public ReservaDeEspacos usarHabilidade(Long personagemId, Long habilidadeId) {
        ReservaDeEspacos reserva = buscar(personagemId);
        Habilidade habilidade = habilidades.findById(habilidadeId)
                .orElseThrow(() -> new HabilidadeNaoEncontradaException(habilidadeId));
        if (!habilidade.isTruque()) {
            reserva.gastar(habilidade.getNivel());
        }
        return repository.save(reserva);
    }

    /**
     * Descanso longo: recupera todos os espaços gastos.
     */
    public ReservaDeEspacos descansoLongo(Long personagemId) {
        ReservaDeEspacos reserva = buscar(personagemId);
        reserva.descansoLongo();
        return repository.save(reserva);
    }

    public ReservaDeEspacos ajustarMaximo(Long personagemId, int nivel, int maximo) {
        ReservaDeEspacos reserva = buscar(personagemId);
        reserva.ajustarMaximo(nivel, maximo);
        return repository.save(reserva);
    }
}
