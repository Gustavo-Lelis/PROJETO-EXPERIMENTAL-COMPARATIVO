# Projeto Experimental Comparativo de Técnicas de Projeto de Algoritmos

> Disciplina: Técnicas de Análise de Algoritmos (TAAL) — UEPB, 2025.2  
> Autor: Gustavo Azevedo Lelis Farias

Implementação e comparação formal de quatro paradigmas clássicos de projeto de algoritmos aplicados a dois problemas distintos: **Partição de Conjuntos** e **Árvore Geradora Mínima (AGM)**.

---

## Técnicas implementadas

| Técnica | Partição | AGM |
|---|---|---|
| Backtracking | ✅ | ✅ |
| Branch and Bound | ✅ | ✅ |
| Programação Dinâmica | ✅ tabela de alcançabilidade | ✅ bitmask DP |
| Estratégia Gulosa | ✅ heurística de balanceamento | ✅ Kruskal + Prim |

---

## Estrutura do projeto

```
.
├── PartitionSolver.java   # Quatro técnicas para Partição de Conjuntos
├── MSTSolver.java         # Quatro técnicas para AGM (inclui bitmask DP)
├── Main.java              # Testes manuais com instâncias fixas
└── BenchmarkMain.java     # Benchmark automatizado com variação de tamanho
```

---

## Como compilar e executar

**Pré-requisito:** Java 11 ou superior.

```bash
# Compilar todos os arquivos
javac *.java

# Executar os testes manuais (instâncias pequenas, saída detalhada)
java Main

# Executar o benchmark completo (seed fixo, variação de n e V)
java BenchmarkMain
```

---

## Configuração do benchmark

Os parâmetros do benchmark estão no topo de `BenchmarkMain.java`:

```java
private static final int[] PARTITION_SIZES  = {8, 12, 16, 20, 24};
private static final int[] MST_VERTICES     = {6, 8, 10, 12};

private static final int PARTITION_BT_LIMIT  = 22;   // BT desativado acima deste n
private static final int MST_BT_VERT_LIMIT   = 8;    // BT desativado acima deste V
private static final int MST_BNB_VERT_LIMIT  = 10;   // BnB desativado acima deste V
```

As instâncias são geradas deterministicamente com `seed = 2026 + n` (Partição) e `seed = 3030 + V` (AGM), garantindo reprodutibilidade.

---

## Métricas coletadas

Para cada algoritmo e instância, são registrados:

- Tempo de execução (ms) via `System.nanoTime()`
- Uso de memória (KB) via diferença de heap da JVM
- Número de nós visitados
- Número de nós podados
- Qualidade da solução (diff para Partição, peso para AGM)

> **Nota:** valores de memória próximos a zero refletem limitação da estimativa quando o GC da JVM atua entre as medições, e não ausência de alocação.

---

## Complexidades implementadas

### Partição de Conjuntos

| Técnica | Tempo | Espaço |
|---|---|---|
| Backtracking | O(2ⁿ) | O(n) |
| Branch and Bound | O(2ⁿ)† | O(2ⁿ) fronteira |
| Programação Dinâmica | O(n × alvo) | O(n × alvo) |
| Gulosa | O(n log n) | O(n) |

### Árvore Geradora Mínima

| Técnica | Tempo | Espaço |
|---|---|---|
| Backtracking | exponencial em E | O(E) |
| Branch and Bound | exponencial em E† | O(2ⁿ) fronteira |
| Bitmask DP | O(2^V × E) | O(2^V) |
| Kruskal | O(E log E) | O(E) |
| Prim | O(E log V) | O(V) |

† Pior caso teórico, com redução prática dependente da qualidade do bound.

---

## Resultados resumidos

### Partição de Conjuntos (n = 8 a 24)

- **DP** e **Gulosa** foram as mais rápidas em todas as instâncias
- **BnB** preservou exatidão até n = 24, porém com consumo de 37 MB de memória
- **BT** atingiu o limite prático para n > 22
- **Gulosa** obteve diff > 0 em 4 das 5 instâncias — sem garantia de exatidão

### AGM (V = 6 a 12)

- **Kruskal** e **Prim** produziram peso ótimo em todas as instâncias com tempo abaixo de 0,2 ms
- **Bitmask DP** também atingiu peso ótimo em todas as instâncias em menos de 0,2 ms
- **BT** tornou-se inviável a partir de V = 10
- **BnB** completou V = 10 em 184 ms, mas falhou para V = 12

---

## Observações de implementação

**Bitmask DP para AGM:** a implementação em `MSTSolver.dynamic()` é uma DP genuína sobre subconjuntos de vértices — `dp[mask]` armazena o menor custo para conectar exatamente os vértices em `mask` partindo do vértice 0. Não se trata de Prim com outro nome: há sobreposição de subproblemas real, pois o mesmo `mask` pode ser alcançado por múltiplas sequências de expansão.

**Branch and Bound para Partição:** a fila de prioridade expande primeiro os nós mais equilibrados (menor |sum1 − sum2|), acelerando a convergência. O bound descarta nós onde |sum1 − sum2| > soma dos elementos restantes.

**Branch and Bound para AGM:** o incumbente é inicializado com o resultado do Kruskal, o que garante um upper bound ótimo desde o início e aumenta a eficácia das podas.

---

## Referências

- CORMEN, T. H. et al. *Introduction to Algorithms*. 3. ed. MIT Press, 2009.
- HOROWITZ, E.; SAHNI, S.; RAJASEKARAN, S. *Fundamentals of Computer Algorithms*. 2. ed. Silicon Press, 2007.
- SKIENA, S. S. *The Algorithm Design Manual*. 2. ed. Springer, 2008.