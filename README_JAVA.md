# Projeto Taal - Implementacao Java

## Overview

Implementacao em Java para dois problemas:

1. Particao de Conjuntos
2. Arvore Geradora Minima (AGM)

## Arquivos Java

```text
PartitionSolver.java
MSTSolver.java
Main.java
BenchmarkMain.java
```

## PartitionSolver.java

Tecnicas disponiveis:

- backtracking()
- branchAndBound()
- dynamic()
- greedy()

Saida principal:

- existencia de particao
- subconjuntos encontrados
- metricas (tempo, memoria, nos visitados e podados)

## MSTSolver.java

Tecnicas disponiveis:

- backtracking()
- branchAndBound()
- dynamic()
- kruskal()
- prim()

Saida principal:

- peso total da AGM
- arestas selecionadas
- metricas (tempo, memoria, nos visitados e podados)

## Main.java

Executa testes basicos de:

- Particao de Conjuntos
- AGM

## BenchmarkMain.java

Executa comparacao de desempenho das tecnicas para:

- Particao de Conjuntos
- AGM

## Compilar

```bash
cd ~/Documents/Projeto_taal
javac PartitionSolver.java MSTSolver.java Main.java BenchmarkMain.java
```

## Executar

```bash
java Main
java BenchmarkMain
```
