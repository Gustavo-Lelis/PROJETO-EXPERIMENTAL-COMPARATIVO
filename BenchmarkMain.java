import java.util.*;

/**
 * Benchmark comparativo formal para os dois problemas do projeto:
 * 1) Particao de Conjuntos
 * 2) Arvore Geradora Minima (AGM)
 *
 * Tecnicas avaliadas:
 * - Backtracking
 * - Branch and Bound
 * - Programacao Dinamica
 * - Estrategia Gulosa
 */
public class BenchmarkMain {
    private static final int[] PARTITION_SIZES = {8, 12, 16, 20, 24};
    private static final int[] MST_VERTICES = {6, 8, 10, 12};

    private static final int PARTITION_BT_LIMIT = 22;
    private static final int MST_BT_VERT_LIMIT = 8;
    private static final int MST_BNB_VERT_LIMIT = 10;

    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println("ANALISE COMPARATIVA FORMAL - PARTICAO E AGM");
        System.out.println("==============================================================");
        System.out.println("Tecnicas: Backtracking | Branch and Bound | DP | Gulosa\n");

        benchmarkPartition();
        benchmarkMST();
    }

    private static void benchmarkPartition() {
        System.out.println("\n" + "=".repeat(78));
        System.out.println("PROBLEMA 1: PARTICAO DE CONJUNTOS");
        System.out.println("=".repeat(78));

        System.out.println("\nTabela A - Tempo (ms), memoria (KB), podas e qualidade (diff)");
        System.out.println(String.format(
                "%-4s | %-12s | %-10s | %-10s | %-11s | %-10s | %-6s",
                "n", "Tecnica", "Tempo(ms)", "Mem(KB)", "Nos visit.", "Nos pod.", "Diff"));
        System.out.println("-".repeat(78));

        for (int n : PARTITION_SIZES) {
            int[] elements = generatePartitionInstance(n, 2026 + n);
            PartitionSolver solver = new PartitionSolver(elements);

            PartitionSolver.PartitionResult bt = null;
            if (n <= PARTITION_BT_LIMIT) {
                bt = solver.backtracking();
                printPartitionRow(n, "Backtracking", bt);
            } else {
                printSkippedRow(n, "Backtracking", "limite pratico");
            }

            PartitionSolver.PartitionResult bnb = solver.branchAndBound();
            printPartitionRow(n, "BranchBound", bnb);

            PartitionSolver.PartitionResult dp = solver.dynamic();
            printPartitionRow(n, "ProgDinamica", dp);

            PartitionSolver.PartitionResult greedy = solver.greedy();
            printPartitionRow(n, "Gulosa", greedy);

            int diffExact = bestPartitionDiff(bt, bnb, dp);
            int diffGreedy = partitionDiff(greedy);
            String ratio = diffExact == 0 ? String.format("%d", diffGreedy)
                    : String.format("%.2f", (double) diffGreedy / diffExact);

            System.out.println(String.format(
                    "%-4s | %-12s | %-10s | %-10s | %-11s | %-10s | %-6s",
                    "", "Qualidade", "-", "-", "-", "-", "G/OPT=" + ratio));
            System.out.println("-".repeat(78));
        }

        System.out.println("\nLeitura rapida:");
        System.out.println("1. BT/BnB/DP sao tecnicas exatas para particao quando encontram diff=0.");
        System.out.println("2. Gulosa minimiza diferenca rapidamente, mas pode nao atingir a particao exata.");
        System.out.println("3. Nos podados quantificam otimizacao de busca em BT/BnB.");
    }

    private static void benchmarkMST() {
        System.out.println("\n" + "=".repeat(78));
        System.out.println("PROBLEMA 2: ARVORE GERADORA MINIMA (AGM)");
        System.out.println("=".repeat(78));

        System.out.println("\nTabela B - Tempo (ms), memoria (KB), podas e qualidade (peso)");
        System.out.println(String.format(
                "%-4s | %-12s | %-10s | %-10s | %-11s | %-10s | %-8s",
                "V", "Tecnica", "Tempo(ms)", "Mem(KB)", "Nos visit.", "Nos pod.", "Peso"));
        System.out.println("-".repeat(78));

        for (int v : MST_VERTICES) {
            List<MSTSolver.Edge> edges = generateMSTInstance(v, 3030 + v);
            MSTSolver solver = new MSTSolver(v, edges);

            MSTSolver.MSTResult kruskal = solver.kruskal();
            int optWeight = kruskal.weight;
            printMSTRow(v, "Kruskal", kruskal);

            MSTSolver.MSTResult prim = solver.prim();
            printMSTRow(v, "Prim", prim);

            MSTSolver.MSTResult dynamic = solver.dynamic();
            printMSTRow(v, "ProgDinamica", dynamic);

            if (v <= MST_BT_VERT_LIMIT) {
                MSTSolver.MSTResult bt = solver.backtracking();
                printMSTRow(v, "Backtracking", bt);
            } else {
                printSkippedRow(v, "Backtracking", "limite pratico");
            }

            if (v <= MST_BNB_VERT_LIMIT) {
                MSTSolver.MSTResult bnb = solver.branchAndBound();
                printMSTRow(v, "BranchBound", bnb);
            } else {
                printSkippedRow(v, "BranchBound", "limite pratico");
            }

            System.out.println(String.format(
                    "%-4s | %-12s | %-10s | %-10s | %-11s | %-10s | %-8s",
                    "", "Qualidade", "-", "-", "-", "-", "OPT=" + optWeight));
            System.out.println("-".repeat(78));
        }

        System.out.println("\nLeitura rapida:");
        System.out.println("1. Em AGM, Kruskal e Prim sao gulosos com garantia de otimalidade.");
        System.out.println("2. BT/BnB sao exatos, mas escalam pior com crescimento de vertices/arestas.");
        System.out.println("3. Podas em BnB mostram reducao do espaco de busca quando o bound e efetivo.");
    }

    private static void printPartitionRow(int n, String technique, PartitionSolver.PartitionResult result) {
        System.out.println(String.format(
                "%-4d | %-12s | %10.4f | %10.2f | %11d | %10d | %6d",
                n,
                technique,
                result.timeMs,
                result.memoryKb,
                result.nodesVisited,
                result.nodesPruned,
                partitionDiff(result)));
    }

    private static void printMSTRow(int vertices, String technique, MSTSolver.MSTResult result) {
        System.out.println(String.format(
                "%-4d | %-12s | %10.4f | %10.2f | %11d | %10d | %8d",
                vertices,
                technique,
                result.timeMs,
                result.memoryKb,
                result.nodesVisited,
                result.nodesPruned,
                result.weight));
    }

    private static void printSkippedRow(int size, String technique, String reason) {
        System.out.println(String.format(
                "%-4d | %-12s | %-10s | %-10s | %-11s | %-10s | %-6s",
                size,
                technique,
                "-",
                "-",
                "-",
                "-",
                reason));
    }

    private static int partitionDiff(PartitionSolver.PartitionResult result) {
        if (result == null || result.subset1 == null || result.subset2 == null) {
            return Integer.MAX_VALUE;
        }
        int sum1 = result.subset1.stream().mapToInt(Integer::intValue).sum();
        int sum2 = result.subset2.stream().mapToInt(Integer::intValue).sum();
        return Math.abs(sum1 - sum2);
    }

    private static int bestPartitionDiff(PartitionSolver.PartitionResult... results) {
        int best = Integer.MAX_VALUE;
        for (PartitionSolver.PartitionResult r : results) {
            best = Math.min(best, partitionDiff(r));
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    private static int[] generatePartitionInstance(int n, int seed) {
        Random random = new Random(seed);
        int[] elements = new int[n];
        for (int i = 0; i < n; i += 2) {
            int value = 5 + random.nextInt(45);
            elements[i] = value;
            if (i + 1 < n) {
                elements[i + 1] = value;
            }
        }
        return elements;
    }

    private static List<MSTSolver.Edge> generateMSTInstance(int vertices, int seed) {
        Random random = new Random(seed);
        List<MSTSolver.Edge> edges = new ArrayList<>();
        Set<String> used = new HashSet<>();

        for (int v = 1; v < vertices; v++) {
            int u = random.nextInt(v);
            int w = 1 + random.nextInt(50);
            addEdge(edges, used, u, v, w);
        }

        int targetEdges = Math.max(vertices - 1, (int) Math.ceil(vertices * 2.2));
        while (edges.size() < targetEdges) {
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);
            if (u == v) {
                continue;
            }
            int w = 1 + random.nextInt(50);
            addEdge(edges, used, u, v, w);
        }

        return edges;
    }

    private static void addEdge(List<MSTSolver.Edge> edges, Set<String> used, int u, int v, int w) {
        int a = Math.min(u, v);
        int b = Math.max(u, v);
        String key = a + "-" + b;
        if (used.add(key)) {
            edges.add(new MSTSolver.Edge(a, b, w));
        }
    }
}
