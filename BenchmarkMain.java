import java.util.*;

/**
 * Classe principal para testes comparativos da primeira entrega (BT e BnB)
 */
public class BenchmarkMain {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║ ANÁLISE COMPARATIVA: PARTIÇÃO E ÁRVORE GERADORA MÍNIMA         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");

        testPartitionComplete();
        testMSTComplete();
    }

    private static void testPartitionComplete() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("PROBLEMA 1: PARTIÇÃO DE CONJUNTOS");
        System.out.println("═".repeat(70));

        int[] elements = {1, 5, 11, 5};

        PartitionSolver solver = new PartitionSolver(elements);

        System.out.println("\nInstância: elements=" + Arrays.toString(elements) + "\n");

        if (!solver.isPossible()) {
            System.out.println("⚠ Não é possível particionar este conjunto (soma total ímpar).\n");
            return;
        }

        Map<String, PartitionSolver.PartitionResult> results = new LinkedHashMap<>();

        System.out.println("Backtracking");
        results.put("Backtracking", solver.backtracking());

        System.out.println("Branch and Bound");
        results.put("Branch and Bound", solver.branchAndBound());

        System.out.println("\n" + "-".repeat(70));
        System.out.println("COMPARAÇÃO DOS RESULTADOS");
        System.out.println("-".repeat(70));

        printPartitionComparison(results);
    }

    private static void testMSTComplete() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("PROBLEMA 2: ÁRVORE GERADORA MÍNIMA (MST)");
        System.out.println("═".repeat(70));

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

        System.out.println("\nInstância: " + nVertices + " vértices, " + edges.size() + " arestas");
        System.out.println("Arestas: " + edges + "\n");

        Map<String, MSTSolver.MSTResult> results = new LinkedHashMap<>();

        System.out.println("Backtracking");
        results.put("Backtracking", solver.backtracking());

        System.out.println("Branch and Bound");
        results.put("Branch and Bound", solver.branchAndBound());

        System.out.println("\n" + "-".repeat(70));
        System.out.println("COMPARAÇÃO DOS RESULTADOS");
        System.out.println("-".repeat(70));

        printMSTComparison(results);
    }

    private static void printPartitionComparison(Map<String, PartitionSolver.PartitionResult> results) {
        System.out.println(String.format("%-18s | %-10s | %-10s | %-9s | %-11s | %-10s",
                "Técnica", "Encontrou", "Tempo(ms)", "Mem(kb)", "Nós Vis.", "Nós Pod."));
        System.out.println("-".repeat(70));

        for (Map.Entry<String, PartitionSolver.PartitionResult> entry : results.entrySet()) {
            PartitionSolver.PartitionResult r = entry.getValue();
            String found = r.found ? "SIM" : "NÃO";
            System.out.println(String.format("%-18s | %-10s | %10.4f | %9.2f | %11d | %10d",
                    entry.getKey(), found, r.timeMs, r.memoryKb, r.nodesVisited, r.nodesPruned));
        }

        System.out.println("\n" + "-".repeat(70));
        System.out.println("ANÁLISE:");

        PartitionSolver.PartitionResult btResult = results.get("Backtracking");
        PartitionSolver.PartitionResult bnbResult = results.get("Branch and Bound");

        System.out.println("✓ Backtracking (exata): " + (btResult.found ? "Encontrou" : "Não encontrou"));
        System.out.println("✓ Branch and Bound (exata): " + (bnbResult.found ? "Encontrou" : "Não encontrou"));

        if (btResult.found) {
            int sum1 = btResult.subset1.stream().mapToInt(Integer::intValue).sum();
            int sum2 = btResult.subset2.stream().mapToInt(Integer::intValue).sum();
            System.out.println(String.format("\nSubset1 (soma=%d): %s", sum1, btResult.subset1));
            System.out.println(String.format("Subset2 (soma=%d): %s", sum2, btResult.subset2));
        }

        System.out.println(String.format("\n• BnB visitou %d nós e podou %d",
                bnbResult.nodesVisited, bnbResult.nodesPruned));
    }

    private static void printMSTComparison(Map<String, MSTSolver.MSTResult> results) {
        System.out.println(String.format("%-18s | %-8s | %-10s | %-9s | %-11s | %-10s",
                "Técnica", "Peso", "Tempo(ms)", "Mem(kb)", "Nós Vis.", "Nós Pod."));
        System.out.println("-".repeat(70));

        for (Map.Entry<String, MSTSolver.MSTResult> entry : results.entrySet()) {
            MSTSolver.MSTResult r = entry.getValue();
            System.out.println(String.format("%-18s | %8d | %10.4f | %9.2f | %11d | %10d",
                    entry.getKey(), r.weight, r.timeMs, r.memoryKb, r.nodesVisited, r.nodesPruned));
        }

        System.out.println("\n" + "-".repeat(70));
        System.out.println("ANÁLISE:");

        MSTSolver.MSTResult btResult = results.get("Backtracking");
        MSTSolver.MSTResult bnbResult = results.get("Branch and Bound");

        System.out.println("✓ BT e BnB encontraram AGM com peso: " + btResult.weight);
        System.out.println(String.format("✓ Backtracking (BT) visitou %d nós, podou %d",
                btResult.nodesVisited, btResult.nodesPruned));
        System.out.println(String.format("✓ Branch and Bound (BnB) visitou %d nós, podou %d",
            bnbResult.nodesVisited, bnbResult.nodesPruned));

        System.out.println(String.format("\n• BnB é %.2fx mais rápida que Backtracking: %.4f vs %.4f ms",
            btResult.timeMs / bnbResult.timeMs, btResult.timeMs, bnbResult.timeMs));
    }
}
