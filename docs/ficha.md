# Módulo `ficha` (Pessoa 1)

Pacote: `com.projeto_rpg.ficha` — personagem, atributos, classes e raças.

## Estrutura

| Pacote | Conteúdo |
|---|---|
| `ficha.modelo` | `Atributo` (enum), `Atributos` (`@Embeddable`), `Classe`, `Raca`, `Personagem` e seus repositórios |
| `ficha.regra` | `CompraDePontos`, `TabelaDeNivel`, `CalculadoraDePv` (regras puras, sem Spring) |
| `ficha.servico` | `ServicoDeClasse`, `ServicoDeRaca` (CRUD) e `ServicoDePersonagem` |
| `ficha.excecao` | exceções de domínio do módulo |

## Regras de negócio validadas pelos testes

- Atributos entre 1 e 30; modificador = `floor((valor - 10) / 2)`.
- Compra de pontos: 27 pontos, valores de 8 a 15; custo 1 por ponto até 13, 2 por ponto em 14 e 15 (14 = 7, 15 = 9).
- Nível pela tabela de XP (1 a 20, 300 XP para o nível 2 ... 355 000 para o 20); o XP satura em `Integer.MAX_VALUE`.
- Bônus de proficiência: `2 + (nível - 1) / 4`.
- PV máximo: nível 1 = dado de vida cheio + mod. CON; níveis seguintes = `dado/2 + 1` + mod. CON; mínimo de 1 PV por nível.
- Dado de vida da classe: d6, d8, d10 ou d12.
- Nomes de classe e raça são únicos (sem diferenciar maiúsculas); classe ou raça em uso por personagem não pode ser removida.

## Contrato com os outros módulos

```java
Personagem p = servicoDePersonagem.buscar(id);

p.getPvMaximo();                              // Pessoa 2: new EstadoDeCombate(p.getId(), p.getPvMaximo())
p.getAtributos().valor(Atributo.FORCA);       // Pessoa 3: capacidade de carga
p.modificador(Atributo.DESTREZA);             // Pessoa 3: CA final
p.getBonusDeProficiencia();
```

`Personagem` guarda a raça só como `racaId` (`Long`). Se o CRUD de `Raca` passar para a Pessoa 3 (plano B),
basta mover `Raca`, `RacaRepository` e `ServicoDeRaca` e ajustar as chamadas `servicoDeRaca.buscar(...)` /
`personagens.existsByRacaId(...)`; `Personagem` não muda.

## Como rodar

```bash
./mvnw test -Dtest='com.projeto_rpg.ficha.**'
```
