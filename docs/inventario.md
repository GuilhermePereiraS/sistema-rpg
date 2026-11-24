# Módulo `inventario` (Pessoa 3)

Pacote: `com.projeto_rpg.inventario` — catálogo de itens e habilidades, inventário do
personagem, capacidade de carga, CA final e espaços de habilidade.

## Estrutura

| Pacote | Conteúdo |
|---|---|
| `inventario.modelo` | `Item` (e `TipoDeItem`/`TipoDeArmadura`), `Habilidade`, `Inventario`, `ReservaDeEspacos` (e `EspacosDeNivel`) e seus repositórios |
| `inventario.regra` | `CalculadoraDeCa`, `CalculadoraDeCarga` (regras puras, sem Spring) |
| `inventario.servico` | `ServicoDeItem` e `ServicoDeHabilidade` (CRUD) e `ServicoDeInventario`/`ServicoDeReservaDeEspacos` (casos de uso) |
| `inventario.excecao` | exceções de domínio do módulo |

## Entidades

- **`Item`** — item do catálogo: nome (único), tipo (`ARMA`, `ARMADURA`, `ESCUDO`,
  `CONSUMIVEL`, `GERAL`) e peso. Armaduras guardam a CA base e a categoria (`LEVE`,
  `MEDIA`, `PESADA`).
- **`Habilidade`** — habilidade do catálogo: nome (único), nível (0 a 9) e descrição.
  Nível 0 é um truque, que não consome espaço.
- **`Inventario`** — itens carregados e os espaços de equipamento (arma, armadura e
  escudo). Referencia o personagem só pelo id.
- **`ReservaDeEspacos`** — espaços de habilidade dos níveis 1 a 9 (`EspacosDeNivel` é um
  `@Embeddable` com máximo, gastos e disponíveis).

## Regras de negócio validadas pelos testes

- Item: nome não vazio e único (sem diferenciar maiúsculas); peso não negativo; armadura
  exige CA base positiva e categoria, e os demais tipos não podem tê-las.
- Habilidade: nível entre 0 e 9; nome único; truque é o nível 0.
- Inventário: guardar o item não duplica; só é possível equipar item guardado e
  equipável; ao remover um item equipado ele é desequipado; item em uso não pode ser
  removido do catálogo; se o tipo de um item equipado mudar no catálogo (ex.: armadura
  virar item geral), ele deixa de estar equipado, mas continua guardado.
- **Capacidade de carga** (recebe a Força): `Força × 15` libras. Sobrecarga quando o peso
  total passa da capacidade.
- **CA final** (recebe o modificador de Destreza e usa o equipamento):
  - sem armadura: `10 + Destreza`;
  - armadura leve: base + Destreza integral;
  - armadura média: base + Destreza (no máximo +2);
  - armadura pesada: apenas a base;
  - escudo: `+2` em qualquer caso.
- **Espaços de habilidade**: usar uma habilidade de nível 1 a 9 gasta um espaço do próprio
  nível; sem espaço disponível lança `EspacoDeHabilidadeInsuficienteException`; o descanso
  longo recupera todos os espaços.

## Independência em relação aos outros módulos

Este módulo não depende de nenhum outro. O inventário guarda apenas o id do personagem
(`personagemId`) e os dois valores que vêm da ficha são recebidos como números:

```java
// Força (capacidade de carga) e modificador de Destreza (CA) são passados pelo chamador
int carga = servicoDeInventario.capacidadeDeCarga(personagemId, forca);
int ca = servicoDeInventario.caFinal(personagemId, modificadorDeDestreza);
```

## Como rodar

```bash
./mvnw test
```
