import java.util.*;

/**
 * Classe principal para testar as implementações das quatro técnicas:
 * Backtracking, Branch and Bound, Programação Dinâmica e Estratégia Gulosa
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== TESTE DAS QUATRO TÉCNICAS DE PROJETO DE ALGORITMOS ===\n");
        testPartition();
        testMST();
    }

    // ================================================================
    // TESTE — PARTIÇÃO DE CONJUNTOS
    // ================================================================

    private static void testPartition() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     PROBLEMA DE PARTIÇÃO DE CONJUNTOS           ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

        int[] elements = {1, 5, 11, 5};
        System.out.println("Instância: elements = " + Arrays.toString(elements));
        System.out.println("Soma total = " + Arrays.stream(elements).sum() +
                           " | Alvo = " + Arrays.stream(elements).sum() / 2 + "\n");

        PartitionSolver solver = new PartitionSolver(elements);

        if (!solver.isPossible()) {
            System.out.println("Partição exata impossível (soma total ímpar).\n");
            return;
        }

        // Backtracking
        PartitionSolver.PartitionResult bt = solver.backtracking();
        printPartitionResult("Backtracking", bt);

        // Branch and Bound
        PartitionSolver.PartitionResult bnb = solver.branchAndBound();
        printPartitionResult("Branch and Bound", bnb);

        // Programação Dinâmica
        PartitionSolver.PartitionResult dp = solver.dynamic();
        printPartitionResult("Programação Dinâmica", dp);

        // Estratégia Gulosa
        PartitionSolver.PartitionResult greedy = solver.greedy();
        printPartitionResult("Estratégia Gulosa", greedy);

        // Sumário
        System.out.println("── Sumário ──────────────────────────────────────────");
        System.out.printf("%-22s | %10s | %8s | %10s | %8s%n",
                "Técnica", "Tempo (ms)", "Mem (KB)", "Nós visit.", "Nós pod.");
        System.out.println("─".repeat(70));
        printSummaryPartition("Backtracking",       bt);
        printSummaryPartition("Branch and Bound",   bnb);
        printSummaryPartition("Prog. Dinâmica",     dp);
        printSummaryPartition("Gulosa",             greedy);
        System.out.println();
    }

    private static void printPartitionResult(String name,
            PartitionSolver.PartitionResult r) {
        System.out.println("--- " + name + " ---");
        if (r.found) {
            int s1 = r.subset1.stream().mapToInt(Integer::intValue).sum();
            int s2 = r.subset2.stream().mapToInt(Integer::intValue).sum();
            System.out.println("  Encontrou partição: SIM");
            System.out.println("  subset1 = " + r.subset1 + "  (soma = " + s1 + ")");
            System.out.println("  subset2 = " + r.subset2 + "  (soma = " + s2 + ")");
            System.out.println("  Diff = " + Math.abs(s1 - s2));
        } else {
            System.out.println("  Encontrou partição: NÃO");
        }
        System.out.printf("  Tempo: %.4f ms | Memória: %.2f KB | " +
                          "Nós visitados: %d | Nós podados: %d%n%n",
                r.timeMs, r.memoryKb, r.nodesVisited, r.nodesPruned);
    }

    private static void printSummaryPartition(String name,
            PartitionSolver.PartitionResult r) {
        System.out.printf("%-22s | %10.4f | %8.2f | %10d | %8d%n",
                name, r.timeMs, r.memoryKb, r.nodesVisited, r.nodesPruned);
    }

    // ================================================================
    // TESTE — ÁRVORE GERADORA MÍNIMA
    // ================================================================

    private static void testMST() {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║     PROBLEMA DA ÁRVORE GERADORA MÍNIMA (AGM)    ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");

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

        System.out.println("Instância: " + nVertices + " vértices, " +
                           edges.size() + " arestas\n");

        // Kruskal
        MSTSolver.MSTResult kruskal = solver.kruskal();
        printMSTResult("Kruskal (Guloso)", kruskal);

        // Prim
        MSTSolver.MSTResult prim = solver.prim();
        printMSTResult("Prim (Guloso)", prim);

        // Programação Dinâmica (bitmask DP)
        MSTSolver.MSTResult dp = solver.dynamic();
        printMSTResult("Programação Dinâmica (bitmask DP)", dp);

        // Backtracking
        MSTSolver.MSTResult bt = solver.backtracking();
        printMSTResult("Backtracking", bt);

        // Branch and Bound
        MSTSolver.MSTResult bnb = solver.branchAndBound();
        printMSTResult("Branch and Bound", bnb);

        // Verificação de corretude
        int opt = kruskal.weight;
        System.out.println("── Verificação de corretude (peso ótimo = " + opt + ") ──");
        checkMST("Kruskal",          kruskal, opt);
        checkMST("Prim",             prim,    opt);
        checkMST("Prog. Dinâmica",   dp,      opt);
        checkMST("Backtracking",     bt,      opt);
        checkMST("Branch and Bound", bnb,     opt);
        System.out.println();

        // Sumário
        System.out.println("── Sumário ──────────────────────────────────────────");
        System.out.printf("%-34s | %10s | %8s | %10s | %8s%n",
                "Técnica", "Tempo (ms)", "Mem (KB)", "Nós visit.", "Nós pod.");
        System.out.println("─".repeat(80));
        printSummaryMST("Kruskal",          kruskal);
        printSummaryMST("Prim",             prim);
        printSummaryMST("Prog. Dinâmica",   dp);
        printSummaryMST("Backtracking",     bt);
        printSummaryMST("Branch and Bound", bnb);
        System.out.println();
    }

    private static void printMSTResult(String name, MSTSolver.MSTResult r) {
        System.out.println("--- " + name + " ---");
        System.out.println("  Peso da AGM: " + r.weight);
        System.out.println("  Arestas: " + r.edges);
        System.out.printf("  Tempo: %.4f ms | Memória: %.2f KB | " +
                          "Nós visitados: %d | Nós podados: %d%n%n",
                r.timeMs, r.memoryKb, r.nodesVisited, r.nodesPruned);
    }

    private static void checkMST(String name, MSTSolver.MSTResult r, int opt) {
        boolean ok = r.weight == opt;
        System.out.printf("  %-22s → peso = %3d  %s%n",
                name, r.weight, ok ? "✓ ótimo" : "✗ subótimo");
    }

    private static void printSummaryMST(String name, MSTSolver.MSTResult r) {
        System.out.printf("%-34s | %10.4f | %8.2f | %10d | %8d%n",
                name, r.timeMs, r.memoryKb, r.nodesVisited, r.nodesPruned);
    }
}