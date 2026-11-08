package com.projeto_rpg.ficha.servico;

import com.projeto_rpg.ficha.excecao.NomeJaCadastradoException;
import com.projeto_rpg.ficha.excecao.RacaNaoEncontradaException;
import com.projeto_rpg.ficha.modelo.Raca;
import com.projeto_rpg.ficha.modelo.RacaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD de raças de personagem.
 */
@Service
@Transactional
public class ServicoDeRaca {

    private final RacaRepository repository;

    public ServicoDeRaca(RacaRepository repository) {
        this.repository = repository;
    }

    public Raca criar(String nome, int deslocamento) {
        Raca raca = new Raca(nome, deslocamento);
        if (repository.existsByNomeIgnoreCase(raca.getNome())) {
            throw new NomeJaCadastradoException("Raça", raca.getNome());
        }
        return repository.save(raca);
    }

    @Transactional(readOnly = true)
    public Raca buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new RacaNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Raca> listar() {
        return repository.findAll();
    }

    public Raca atualizar(Long id, String nome, int deslocamento) {
        Raca raca = buscar(id);
        // checagem antes de alterar a entidade (ver ServicoDeClasse.atualizar)
        if (nome != null && repository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id)) {
            throw new NomeJaCadastradoException("Raça", nome.trim());
        }
        raca.atualizar(nome, deslocamento);
        return repository.save(raca);
    }

    public void remover(Long id) {
        repository.delete(buscar(id));
    }
}
