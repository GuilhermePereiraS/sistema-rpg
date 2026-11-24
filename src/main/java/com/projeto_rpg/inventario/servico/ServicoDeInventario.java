package com.projeto_rpg.inventario.servico;

import com.projeto_rpg.inventario.excecao.InventarioJaExistenteException;
import com.projeto_rpg.inventario.excecao.InventarioNaoEncontradoException;
import com.projeto_rpg.inventario.excecao.ItemNaoEncontradoException;
import com.projeto_rpg.inventario.modelo.Inventario;
import com.projeto_rpg.inventario.modelo.InventarioRepository;
import com.projeto_rpg.inventario.modelo.Item;
import com.projeto_rpg.inventario.modelo.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Casos de uso do inventário: guardar e equipar itens, capacidade de carga e CA final.
 *
 * <p>Este módulo é independente da ficha do personagem. Os dois valores que vêm dela —
 * a Força (para a capacidade de carga) e o modificador de Destreza (para a CA final) —
 * são recebidos como números nos métodos que precisam deles. O inventário guarda apenas
 * o id do personagem como referência.
 */
@Service
@Transactional
public class ServicoDeInventario {

    private final InventarioRepository repository;
    private final ItemRepository itens;

    public ServicoDeInventario(InventarioRepository repository, ItemRepository itens) {
        this.repository = repository;
        this.itens = itens;
    }

    public Inventario criar(Long personagemId) {
        if (repository.existsByPersonagemId(personagemId)) {
            throw new InventarioJaExistenteException(personagemId);
        }
        return repository.save(new Inventario(personagemId));
    }

    @Transactional(readOnly = true)
    public Inventario buscar(Long personagemId) {
        return repository.findByPersonagemId(personagemId)
                .orElseThrow(() -> new InventarioNaoEncontradoException(personagemId));
    }

    @Transactional(readOnly = true)
    public List<Inventario> listar() {
        return repository.findAll();
    }

    public Inventario adicionarItem(Long personagemId, Long itemId) {
        Inventario inventario = buscar(personagemId);
        inventario.adicionarItem(buscarItem(itemId));
        return repository.save(inventario);
    }

    public Inventario removerItem(Long personagemId, Long itemId) {
        Inventario inventario = buscar(personagemId);
        inventario.removerItem(buscarItem(itemId));
        return repository.save(inventario);
    }

    public Inventario equipar(Long personagemId, Long itemId) {
        Inventario inventario = buscar(personagemId);
        inventario.equipar(buscarItem(itemId));
        return repository.save(inventario);
    }

    public Inventario desequipar(Long personagemId, Long itemId) {
        Inventario inventario = buscar(personagemId);
        inventario.desequipar(buscarItem(itemId));
        return repository.save(inventario);
    }

    /**
     * Capacidade de carga do personagem, a partir da Força informada.
     */
    @Transactional(readOnly = true)
    public int capacidadeDeCarga(Long personagemId, int forca) {
        return buscar(personagemId).capacidadeDeCarga(forca);
    }

    @Transactional(readOnly = true)
    public double pesoTotal(Long personagemId) {
        return buscar(personagemId).pesoTotal();
    }

    @Transactional(readOnly = true)
    public boolean estaSobrecarregado(Long personagemId, int forca) {
        return buscar(personagemId).estaSobrecarregado(forca);
    }

    /**
     * CA final do personagem, a partir do modificador de Destreza informado e do equipamento.
     */
    @Transactional(readOnly = true)
    public int caFinal(Long personagemId, int modificadorDeDestreza) {
        return buscar(personagemId).caFinal(modificadorDeDestreza);
    }

    private Item buscarItem(Long itemId) {
        return itens.findById(itemId).orElseThrow(() -> new ItemNaoEncontradoException(itemId));
    }
}
