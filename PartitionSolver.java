import java.util.*;

/**
 * Implementação das técnicas de Backtracking e Branch and Bound
 * para o Problema de Partição de Conjuntos
 */
public class PartitionSolver {
    private int[] elements;
    private int n;
    private int totalSum;
    private int targetSum;
    private long nodesVisited;
    private long nodesPruned;

    public PartitionSolver(int[] elements) {
        this.elements = elements;
        this.n = elements.length;
        this.totalSum = Arrays.stream(elements).sum();
        this.targetSum = totalSum / 2;
    }

    /**
     * Verifica se uma partição com somas iguais é possível
     */
    public boolean isPossible() {
        return totalSum % 2 == 0;
    }

    /**
     * Resolve o problema de Partição usando Backtracking
     */
    public PartitionResult backtracking() {
        if (!isPossible()) {
            return new PartitionResult(false, null, null, 0, 0, 0, 0);
        }

        this.nodesVisited = 0;
        this.nodesPruned = 0;

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        PartitionData result = new PartitionData();
        List<Integer> currentIndices = new ArrayList<>();

        btRecursive(0, 0, currentIndices, result);

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        if (result.found) {
            List<Integer> subset1 = new ArrayList<>(result.subset1);
            List<Integer> subset2 = new ArrayList<>(result.subset2);
            return new PartitionResult(true, subset1, subset2, timeMs, memoryKb, nodesVisited, nodesPruned);
        } else {
            return new PartitionResult(false, null, null, timeMs, memoryKb, nodesVisited, nodesPruned);
        }
    }

    private void btRecursive(int idx, int currentSum, List<Integer> currentIndices, PartitionData result) {
        nodesVisited++;

        if (result.found) {
            return; // Para quando encontra uma solução
        }

        if (idx == n) {
            if (currentSum == targetSum) {
                result.found = true;
                result.subset1 = new ArrayList<>(currentIndices);
                result.subset2 = new ArrayList<>();

                for (int i = 0; i < n; i++) {
                    if (!currentIndices.contains(i)) {
                        result.subset2.add(elements[i]);
                    }
                }

                for (int i : currentIndices) {
                    result.subset1.set(result.subset1.indexOf(i), elements[i]);
                }

                // Corrigir: criar subset1 com valores, não índices
                result.subset1.clear();
                for (int i : currentIndices) {
                    result.subset1.add(elements[i]);
                }
            }
            return;
        }

        // Pruning: se a soma atual excede o alvo, não continua
        if (currentSum > targetSum) {
            nodesPruned++;
            return;
        }

        // Tenta incluir o elemento atual
        if (currentSum + elements[idx] <= targetSum) {
            currentIndices.add(idx);
            btRecursive(idx + 1, currentSum + elements[idx], currentIndices, result);
            currentIndices.remove(currentIndices.size() - 1);
        }

        // Tenta não incluir o elemento atual
        btRecursive(idx + 1, currentSum, currentIndices, result);
    }

    /**
     * Resolve o problema de Partição usando Programação Dinâmica
     */
    public PartitionResult dynamic() {
        if (!isPossible()) {
            return new PartitionResult(false, null, null, 0, 0, 0, 0);
        }

        this.nodesVisited = 0;
        this.nodesPruned = 0;

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // dp[i][s] = true se é possível atingir soma s usando elementos 0..i-1
        boolean[][] dp = new boolean[n + 1][targetSum + 1];
        dp[0][0] = true;

        // Preenche a tabela
        for (int i = 0; i < n; i++) {
            for (int s = 0; s <= targetSum; s++) {
                nodesVisited++;

                if (dp[i][s]) {
                    dp[i + 1][s] = true; // Não inclui elemento i
                    if (s + elements[i] <= targetSum) {
                        dp[i + 1][s + elements[i]] = true; // Inclui elemento i
                    }
                }
            }
        }

        if (!dp[n][targetSum]) {
            long endTime = System.nanoTime();
            long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
            double timeMs = (endTime - startTime) / 1_000_000.0;
            double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);
            return new PartitionResult(false, null, null, timeMs, memoryKb, nodesVisited, nodesPruned);
        }

        // Reconstrói a solução
        List<Integer> subset1 = new ArrayList<>();
        List<Integer> subset2 = new ArrayList<>();
        int currentSum = targetSum;

        for (int i = n - 1; i >= 0; i--) {
            if (currentSum >= elements[i] && dp[i][currentSum - elements[i]]) {
                subset1.add(elements[i]);
                currentSum -= elements[i];
            } else {
                subset2.add(elements[i]);
            }
        }

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new PartitionResult(true, subset1, subset2, timeMs, memoryKb, nodesVisited, nodesPruned);
    }

    /**
     * Resolve o problema de Partição usando Estratégia Gulosa
     * Nota: Esta é uma aproximação e pode não encontrar a partição ótima
     */
    public PartitionResult greedy() {
        this.nodesVisited = n;
        this.nodesPruned = 0;

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Ordena elementos em ordem decrescente
        Integer[] sorted = new Integer[n];
        for (int i = 0; i < n; i++) {
            sorted[i] = i;
        }
        Arrays.sort(sorted, (a, b) -> Integer.compare(elements[b], elements[a]));

        List<Integer> subset1 = new ArrayList<>();
        List<Integer> subset2 = new ArrayList<>();
        int sum1 = 0, sum2 = 0;

        for (int idx : sorted) {
            if (sum1 <= sum2) {
                subset1.add(elements[idx]);
                sum1 += elements[idx];
            } else {
                subset2.add(elements[idx]);
                sum2 += elements[idx];
            }
        }

        boolean found = sum1 == sum2;

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new PartitionResult(found, subset1, subset2, timeMs, memoryKb, nodesVisited, nodesPruned);
    }

    /**
     * Resolve o problema de Partição usando Backtracking
     */
    public PartitionResult branchAndBound() {
        if (!isPossible()) {
            return new PartitionResult(false, null, null, 0, 0, 0, 0);
        }

        this.nodesVisited = 0;
        this.nodesPruned = 0;

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> Integer.compare(
                Math.abs(b.sum1 - b.sum2), Math.abs(a.sum1 - a.sum2)
        ));

        Node root = new Node(0, 0, 0, new ArrayList<>(), new ArrayList<>());
        queue.offer(root);

        PartitionData result = new PartitionData();

        while (!queue.isEmpty() && !result.found) {
            Node node = queue.poll();
            nodesVisited++;

            if (node.index == n) {
                if (node.sum1 == node.sum2) {
                    result.found = true;
                    result.subset1 = new ArrayList<>(node.subset1);
                    result.subset2 = new ArrayList<>(node.subset2);
                }
                continue;
            }

            // Pruning: se a diferença entre as somas é maior que a soma dos elementos restantes
            int remainingSum = totalSum - node.sum1 - node.sum2;
            if (Math.abs(node.sum1 - node.sum2) > remainingSum) {
                nodesPruned++;
                continue;
            }

            // Tenta adicionar ao subset1
            List<Integer> newSubset1 = new ArrayList<>(node.subset1);
            newSubset1.add(elements[node.index]);
            Node leftChild = new Node(node.index + 1, node.sum1 + elements[node.index], node.sum2, newSubset1,
                    new ArrayList<>(node.subset2));
            queue.offer(leftChild);

            // Tenta adicionar ao subset2
            List<Integer> newSubset2 = new ArrayList<>(node.subset2);
            newSubset2.add(elements[node.index]);
            Node rightChild = new Node(node.index + 1, node.sum1, node.sum2 + elements[node.index],
                    new ArrayList<>(node.subset1), newSubset2);
            queue.offer(rightChild);
        }

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        if (result.found) {
            return new PartitionResult(true, result.subset1, result.subset2, timeMs, memoryKb, nodesVisited,
                    nodesPruned);
        } else {
            return new PartitionResult(false, null, null, timeMs, memoryKb, nodesVisited, nodesPruned);
        }
    }

    /**
     * Classe interna para armazenar dados da partição
     */
    private static class PartitionData {
        boolean found = false;
        List<Integer> subset1;
        List<Integer> subset2;
    }

    /**
     * Classe interna para representar um nó na árvore de busca (Branch and Bound)
     */
    private static class Node {
        int index;
        int sum1;
        int sum2;
        List<Integer> subset1;
        List<Integer> subset2;

        Node(int index, int sum1, int sum2, List<Integer> subset1, List<Integer> subset2) {
            this.index = index;
            this.sum1 = sum1;
            this.sum2 = sum2;
            this.subset1 = subset1;
            this.subset2 = subset2;
        }
    }

    /**
     * Classe para encapsular resultados do solver
     */
    public static class PartitionResult {
        public boolean found;
        public List<Integer> subset1;
        public List<Integer> subset2;
        public double timeMs;
        public double memoryKb;
        public long nodesVisited;
        public long nodesPruned;

        public PartitionResult(boolean found, List<Integer> subset1, List<Integer> subset2, double timeMs,
                double memoryKb, long nodesVisited, long nodesPruned) {
            this.found = found;
            this.subset1 = subset1;
            this.subset2 = subset2;
            this.timeMs = timeMs;
            this.memoryKb = memoryKb;
            this.nodesVisited = nodesVisited;
            this.nodesPruned = nodesPruned;
        }

        @Override
        public String toString() {
            return String.format("PartitionResult{found=%s, subset1=%s, subset2=%s, time=%.2fms, " +
                    "memory=%.2fkb, nodes_visited=%d, nodes_pruned=%d}",
                    found, subset1, subset2, timeMs, memoryKb, nodesVisited, nodesPruned);
        }
    }
}
