import java.util.*;

/**
 * Classe principal para testar as implementações de Backtracking e Branch and Bound
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== TESTE DE BACKTRACKING E BRANCH AND BOUND ===\n");

        // Teste 1: Problema de Partição
        testPartition();

        // Teste 2: Problema da Árvore Geradora Mínima
        testMST();
    }

    private static void testPartition() {
        System.out.println("### PROBLEMA DE PARTIÇÃO DE CONJUNTOS ###\n");

        int[] elements = {1, 5, 11, 5};

        PartitionSolver solver = new PartitionSolver(elements);

        System.out.println("Instância: elements=" + Arrays.toString(elements) + "\n");

        if (!solver.isPossible()) {
            System.out.println("Não é possível particionar este conjunto (soma ímpar).\n");
            return;
        }

        // Teste Backtracking
        System.out.println("--- Executando Backtracking ---");
        long startBT = System.currentTimeMillis();
        PartitionSolver.PartitionResult btResult = solver.backtracking();
        long endBT = System.currentTimeMillis();
        System.out.println("Resultado: " + btResult);
        System.out.println("Tempo de execução total: " + (endBT - startBT) + "ms\n");

        // Teste Branch and Bound
        System.out.println("--- Executando Branch and Bound ---");
        long startBnB = System.currentTimeMillis();
        PartitionSolver.PartitionResult bnbResult = solver.branchAndBound();
        long endBnB = System.currentTimeMillis();
        System.out.println("Resultado: " + bnbResult);
        System.out.println("Tempo de execução total: " + (endBnB - startBnB) + "ms\n");

        System.out.println("---");
        if (btResult.found) {
            System.out.println("Backtracking achou partição: subset1=" + btResult.subset1 + 
                             ", subset2=" + btResult.subset2);
            System.out.println("Somas: " + 
                             btResult.subset1.stream().mapToInt(Integer::intValue).sum() + " = " +
                             btResult.subset2.stream().mapToInt(Integer::intValue).sum());
        }
        System.out.println("Backtracking visitou " + btResult.nodesVisited + " nós, " +
                         "Branch and Bound visitou " + bnbResult.nodesVisited + " nós\n");
    }

    private static void testMST() {
        System.out.println("### PROBLEMA DA ÁRVORE GERADORA MÍNIMA ###\n");

        List<MSTSolver.Edge> edges = new ArrayList<>();
        edges.add(new MSTSolver.Edge(0, 1, 4));
        edges.add(new MSTSolver.Edge(0, 2, 2));
        edges.add(new MSTSolver.Edge(1, 2, 1));
        edges.add(new MSTSolver.Edge(1, 3, 5));
        edges.add(new MSTSolver.Edge(2, 3, 8));
        edges.add(new MSTSolver.Edge(2, 4, 10));
        edges.add(new MSTSolver.Edge(3, 4, 2));

        int nVertices = 5;
        MSTSolver solver = new MSTSolver(nVertices, edges);

        System.out.println("Instância: " + nVertices + " vértices, " + edges.size() + " arestas\n");

        // Teste Backtracking
        System.out.println("--- Executando Backtracking ---");
        long startBT = System.currentTimeMillis();
        MSTSolver.MSTResult btResult = solver.backtracking();
        long endBT = System.currentTimeMillis();
        System.out.println("Resultado: " + btResult);
        System.out.println("Tempo de execução total: " + (endBT - startBT) + "ms\n");

        // Teste Branch and Bound
        System.out.println("--- Executando Branch and Bound ---");
        long startBnB = System.currentTimeMillis();
        MSTSolver.MSTResult bnbResult = solver.branchAndBound();
        long endBnB = System.currentTimeMillis();
        System.out.println("Resultado: " + bnbResult);
        System.out.println("Tempo de execução total: " + (endBnB - startBnB) + "ms\n");

        System.out.println("---");
        System.out.println("Ambas encontraram AGM com peso: " + 
                         (btResult.weight == bnbResult.weight ? "✓ Correto (" + btResult.weight + ")" : "✗ Diferente"));
        System.out.println("Backtracking visitou " + btResult.nodesVisited + " nós, " +
                         "Branch and Bound visitou " + bnbResult.nodesVisited + " nós\n");
    }
}
