package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.HabilidadeJaCadastradaException;
import com.projeto_rpg.inventario.excecao.HabilidadeNaoEncontradaException;
import com.projeto_rpg.inventario.modelo.Habilidade;
import com.projeto_rpg.inventario.modelo.HabilidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD do catálogo de habilidades.
 */
@Service
@Transactional
public class ServicoDeHabilidade {

    private final HabilidadeRepository repository;

    public ServicoDeHabilidade(HabilidadeRepository repository) {
        this.repository = repository;
    }

    public Habilidade criar(String nome, int nivel, String descricao) {
        Habilidade habilidade = new Habilidade(nome, nivel, descricao);
        if (repository.existsByNomeIgnoreCase(habilidade.getNome())) {
            throw new HabilidadeJaCadastradaException(habilidade.getNome());
        }
        return repository.save(habilidade);
    }

    @Transactional(readOnly = true)
    public Habilidade buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new HabilidadeNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Habilidade> listar() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Habilidade> listarPorNivel(int nivel) {
        return repository.findByNivel(nivel);
    }

    public Habilidade atualizar(Long id, String nome, int nivel, String descricao) {
        Habilidade habilidade = buscar(id);
        // checagem antes de alterar a entidade (ver ServicoDeItem.atualizar)
        if (nome != null && repository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id)) {
            throw new HabilidadeJaCadastradaException(nome.trim());
        }
        habilidade.atualizar(nome, nivel, descricao);
        return repository.save(habilidade);
    }

    public void remover(Long id) {
        Habilidade habilidade = buscar(id);
        repository.delete(habilidade);
    }
}
