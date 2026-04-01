# Projeto Taal - Particao de Conjuntos e AGM

## Objetivo

Este projeto compara tecnicas de projeto de algoritmos em **dois problemas**:

1. Problema de Particao de Conjuntos
2. Problema da Arvore Geradora Minima (AGM)

## Estrutura

```text
/home/gustavo/Documents/Projeto_taal/
├── PartitionSolver.java
├── MSTSolver.java
├── Main.java
├── BenchmarkMain.java
├── partition.py
├── mst.py
├── benchmark_experiments.py
├── benchmark_results.json
├── benchmark_results.md
├── README.md
├── README_JAVA.md
└── Projeto Experimental Comparativo de Tecnicas de Projeto de Algoritmos.md
```

## Tecnicas Implementadas

- Backtracking
- Branch and Bound
- Programacao Dinamica
- Estrategias Gulosas

## Compilacao e Execucao (Java)

```bash
cd ~/Documents/Projeto_taal
javac PartitionSolver.java MSTSolver.java Main.java BenchmarkMain.java
java Main
java BenchmarkMain
```

## Benchmarks (Python)

```bash
cd ~/Documents/Projeto_taal
python3 benchmark_experiments.py
```

Arquivos gerados:

- benchmark_results.json
- benchmark_results.md

## Escopo Atual

Este repositorio foi ajustado para manter **somente** os problemas de:

- Particao de Conjuntos
- Arvore Geradora Minima (AGM)
