import java.util.*;

/**
 * Classe principal — lê entrada padrão e executa os solvers.
 *
 * Partição de Conjuntos:
 *   Linha 1: n
 *   Linha 2: a1 a2 ... an
 *   Saída:
 *     YES
 *     subset1 (valores separados por espaço)
 *     subset2 (valores separados por espaço)
 *   ou: NO
 *
 * Árvore Geradora Mínima:
 *   Linha 1: n m
 *   Próximas m linhas: u v w  (vértices numerados de 1 a n)
 *   Saída:
 *     peso_total
 *     u1 v1 w1
 *     ...  (vértices na saída também de 1 a n)
 */
public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Escolha o problema:");
        System.out.println("1 - Particao de Conjuntos");
        System.out.println("2 - Arvore Geradora Minima");
        System.out.print("Opcao: ");
        int opcao = sc.nextInt();

        if (opcao == 1) {
            resolverParticao(sc);
        } else if (opcao == 2) {
            resolverAGM(sc);
        } else {
            System.out.println("Opcao invalida.");
        }

        sc.close();
    }

    // ================================================================
    // PARTIÇÃO DE CONJUNTOS
    // ================================================================

    private static void resolverParticao(Scanner sc) {
        System.out.print("n: ");
        int n = sc.nextInt();

        int[] elements = new int[n];
        System.out.print("Elementos: ");
        for (int i = 0; i < n; i++) {
            elements[i] = sc.nextInt();
        }

        System.out.println("\nEscolha a tecnica:");
        System.out.println("1 - Backtracking");
        System.out.println("2 - Branch and Bound");
        System.out.println("3 - Programacao Dinamica");
        System.out.println("4 - Gulosa");
        System.out.print("Opcao: ");
        int tecnica = sc.nextInt();

        PartitionSolver solver = new PartitionSolver(elements);

        if (!solver.isPossible()) {
            System.out.println("NO");
            return;
        }

        PartitionSolver.PartitionResult result;
        switch (tecnica) {
            case 1: result = solver.backtracking(); break;
            case 2: result = solver.branchAndBound(); break;
            case 3: result = solver.dynamic(); break;
            case 4: result = solver.greedy(); break;
            default:
                System.out.println("Tecnica invalida.");
                return;
        }

        imprimirResultadoParticao(result);
    }

    private static void imprimirResultadoParticao(PartitionSolver.PartitionResult result) {
        System.out.println();
        if (result.found) {
            System.out.println("YES");
            // subset1
            StringBuilder sb1 = new StringBuilder();
            for (int i = 0; i < result.subset1.size(); i++) {
                if (i > 0) sb1.append(" ");
                sb1.append(result.subset1.get(i));
            }
            System.out.println(sb1);
            // subset2
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < result.subset2.size(); i++) {
                if (i > 0) sb2.append(" ");
                sb2.append(result.subset2.get(i));
            }
            System.out.println(sb2);
        } else {
            System.out.println("NO");
        }
    }

    // ================================================================
    // ÁRVORE GERADORA MÍNIMA
    // ================================================================

    private static void resolverAGM(Scanner sc) {
        System.out.print("n m: ");
        int n = sc.nextInt();
        int m = sc.nextInt();

        List<MSTSolver.Edge> edges = new ArrayList<>();
        System.out.println("Arestas (u v w), vertices de 1 a " + n + ":");
        for (int i = 0; i < m; i++) {
            int u = sc.nextInt() - 1;   // converte para base 0 internamente
            int v = sc.nextInt() - 1;
            int w = sc.nextInt();
            edges.add(new MSTSolver.Edge(u, v, w));
        }

        System.out.println("\nEscolha a tecnica:");
        System.out.println("1 - Kruskal (Guloso)");
        System.out.println("2 - Prim (Guloso)");
        System.out.println("3 - Programacao Dinamica (bitmask DP)");
        System.out.println("4 - Backtracking");
        System.out.println("5 - Branch and Bound");
        System.out.print("Opcao: ");
        int tecnica = sc.nextInt();

        MSTSolver solver = new MSTSolver(n, edges);
        MSTSolver.MSTResult result;

        switch (tecnica) {
            case 1: result = solver.kruskal(); break;
            case 2: result = solver.prim(); break;
            case 3: result = solver.dynamic(); break;
            case 4: result = solver.backtracking(); break;
            case 5: result = solver.branchAndBound(); break;
            default:
                System.out.println("Tecnica invalida.");
                return;
        }

        imprimirResultadoAGM(result);
    }

    private static void imprimirResultadoAGM(MSTSolver.MSTResult result) {
        System.out.println();
        if (result.edges == null || result.edges.isEmpty()) {
            System.out.println("Nao foi possivel encontrar uma AGM.");
            return;
        }
        // Saída: peso total e arestas com vértices de 1 a n (base 1)
        System.out.println(result.weight);
        for (MSTSolver.Edge e : result.edges) {
            System.out.println((e.u + 1) + " " + (e.v + 1) + " " + e.weight);
        }
    }
}