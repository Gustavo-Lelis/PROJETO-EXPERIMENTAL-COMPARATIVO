import java.util.*;

/**
 * Implementação das técnicas de Backtracking, Branch and Bound, Programação Dinâmica e Estratégia Gulosa
 * para o Problema da Árvore Geradora Mínima (AGM)
 */
public class MSTSolver {
    private int nVertices;
    private List<Edge> edges;
    private long nodesVisited;
    private long nodesPruned;
    private int bestWeight;
    private List<Edge> bestEdges;

    public MSTSolver(int nVertices, List<Edge> edges) {
        this.nVertices = nVertices;
        this.edges = new ArrayList<>(edges);
        this.edges.sort(Comparator.comparingInt(e -> e.weight));
        this.bestWeight = Integer.MAX_VALUE;
        this.bestEdges = new ArrayList<>();
    }

    /**
     * Resolve o problema da AGM usando Backtracking
     */
    public MSTResult backtracking() {
        this.nodesVisited = 0;
        this.nodesPruned = 0;
        this.bestWeight = Integer.MAX_VALUE;
        this.bestEdges = new ArrayList<>();

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        List<Edge> currentEdges = new ArrayList<>();
        btRecursive(0, currentEdges, new UnionFind(nVertices));

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new MSTResult(bestWeight, new ArrayList<>(bestEdges), timeMs, memoryKb, nodesVisited,
                nodesPruned);
    }

    private void btRecursive(int edgeIdx, List<Edge> currentEdges, UnionFind uf) {
        nodesVisited++;

        if (currentEdges.size() == nVertices - 1) {
            int currentWeight = currentEdges.stream().mapToInt(e -> e.weight).sum();
            if (currentWeight < bestWeight) {
                bestWeight = currentWeight;
                bestEdges = new ArrayList<>(currentEdges);
            }
            return;
        }

        if (edgeIdx >= edges.size()) {
            return;
        }

        int currentWeight = currentEdges.stream().mapToInt(e -> e.weight).sum();
        if (currentWeight >= bestWeight) {
            nodesPruned++;
            return;
        }

        Edge edge = edges.get(edgeIdx);

        if (uf.find(edge.u) != uf.find(edge.v)) {
            currentEdges.add(edge);
            UnionFind newUf = new UnionFind(nVertices);
            copyUnionFind(uf, newUf);
            newUf.union(edge.u, edge.v);
            btRecursive(edgeIdx + 1, currentEdges, newUf);
            currentEdges.remove(currentEdges.size() - 1);
        }

        btRecursive(edgeIdx + 1, currentEdges, uf);
    }

    /**
     * Resolve o problema da AGM usando Branch and Bound
     */
    public MSTResult branchAndBound() {
        this.nodesVisited = 0;
        this.nodesPruned = 0;
        this.bestWeight = Integer.MAX_VALUE;
        this.bestEdges = new ArrayList<>();

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        // Usa Kruskal como heurística para upper bound
        MSTResult kruskalResult = kruskal();
        bestWeight = kruskalResult.weight;
        bestEdges = new ArrayList<>(kruskalResult.edges);
        // Não mistura métricas do Kruskal com as métricas do BnB.
        this.nodesVisited = 0;
        this.nodesPruned = 0;

        PriorityQueue<Node> queue = new PriorityQueue<>((a, b) -> Integer.compare(a.weight, b.weight));
        Node root = new Node(0, 0, new ArrayList<>(), new UnionFind(nVertices));
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            nodesVisited++;

            if (node.edges.size() == nVertices - 1) {
                if (node.weight < bestWeight) {
                    bestWeight = node.weight;
                    bestEdges = new ArrayList<>(node.edges);
                }
                continue;
            }

            if (node.weight >= bestWeight) {
                nodesPruned++;
                continue;
            }

            if (node.edgeIdx >= edges.size()) {
                continue;
            }

            Edge edge = edges.get(node.edgeIdx);

            if (node.uf.find(edge.u) != node.uf.find(edge.v)) {
                List<Edge> newEdges = new ArrayList<>(node.edges);
                newEdges.add(edge);
                UnionFind newUf = new UnionFind(nVertices);
                copyUnionFind(node.uf, newUf);
                newUf.union(edge.u, edge.v);
                queue.offer(new Node(node.edgeIdx + 1, node.weight + edge.weight, newEdges, newUf));
            }

                UnionFind skipUf = new UnionFind(nVertices);
                copyUnionFind(node.uf, skipUf);
                queue.offer(new Node(node.edgeIdx + 1, node.weight, new ArrayList<>(node.edges), skipUf));
        }

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new MSTResult(bestWeight, new ArrayList<>(bestEdges), timeMs, memoryKb, nodesVisited,
                nodesPruned);
    }

    /**
     * Resolve o problema da AGM usando Algoritmo de Kruskal (Estratégia Gulosa)
     */
    public MSTResult kruskal() {
        this.nodesVisited = edges.size();
        this.nodesPruned = 0;
        this.bestWeight = 0;
        this.bestEdges = new ArrayList<>();

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        UnionFind uf = new UnionFind(nVertices);
        List<Edge> mstEdges = new ArrayList<>();
        int totalWeight = 0;

        for (Edge edge : edges) {
            if (uf.find(edge.u) != uf.find(edge.v)) {
                uf.union(edge.u, edge.v);
                mstEdges.add(edge);
                totalWeight += edge.weight;

                if (mstEdges.size() == nVertices - 1) {
                    break;
                }
            } else {
                nodesPruned++;
            }
        }

        bestWeight = totalWeight;
        bestEdges = mstEdges;

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new MSTResult(bestWeight, new ArrayList<>(bestEdges), timeMs, memoryKb, nodesVisited,
                nodesPruned);
    }

    /**
     * Resolve o problema da AGM usando Algoritmo de Prim (Estratégia Gulosa)
     */
    public MSTResult prim() {
        this.nodesVisited = 0;
        this.nodesPruned = 0;
        this.bestWeight = 0;
        this.bestEdges = new ArrayList<>();

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        boolean[] visited = new boolean[nVertices];
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> e.weight));
        List<Edge> mstEdges = new ArrayList<>();
        int totalWeight = 0;

        visited[0] = true;
        nodesVisited++;

        for (Edge edge : edges) {
            if (edge.u == 0 || edge.v == 0) {
                pq.offer(edge);
            }
        }

        while (!pq.isEmpty() && mstEdges.size() < nVertices - 1) {
            Edge edge = pq.poll();

            int u = edge.u;
            int v = edge.v;

            if (visited[u] && visited[v]) {
                nodesPruned++;
                continue;
            }

            int newVertex = visited[u] ? v : u;
            visited[newVertex] = true;
            nodesVisited++;

            mstEdges.add(edge);
            totalWeight += edge.weight;

            for (Edge e : edges) {
                if ((e.u == newVertex && !visited[e.v]) || (e.v == newVertex && !visited[e.u])) {
                    pq.offer(e);
                }
            }
        }

        bestWeight = totalWeight;
        bestEdges = mstEdges;

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new MSTResult(bestWeight, new ArrayList<>(bestEdges), timeMs, memoryKb, nodesVisited,
                nodesPruned);
    }

    /**
     * Resolve o problema da AGM usando Programação Dinâmica (Similar a Prim)
     */
    public MSTResult dynamic() {
        this.nodesVisited = 0;
        this.nodesPruned = 0;
        this.bestWeight = 0;
        this.bestEdges = new ArrayList<>();

        long startTime = System.nanoTime();
        long startMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        boolean[] visited = new boolean[nVertices];
        int[] key = new int[nVertices];
        int[] parent = new int[nVertices];

        for (int i = 0; i < nVertices; i++) {
            key[i] = Integer.MAX_VALUE;
            parent[i] = -1;
        }

        key[0] = 0;
        List<Edge> mstEdges = new ArrayList<>();
        int totalWeight = 0;

        for (int count = 0; count < nVertices - 1; count++) {
            nodesVisited++;

            int u = -1;
            int min = Integer.MAX_VALUE;

            for (int v = 0; v < nVertices; v++) {
                if (!visited[v] && key[v] < min) {
                    min = key[v];
                    u = v;
                }
            }

            if (u == -1) break;

            visited[u] = true;

            for (Edge edge : edges) {
                int v = -1;
                if (edge.u == u && !visited[edge.v]) {
                    v = edge.v;
                } else if (edge.v == u && !visited[edge.u]) {
                    v = edge.u;
                }

                if (v != -1 && edge.weight < key[v]) {
                    key[v] = edge.weight;
                    parent[v] = u;
                }
            }
        }

        for (int v = 1; v < nVertices; v++) {
            if (parent[v] != -1) {
                for (Edge edge : edges) {
                    if ((edge.u == parent[v] && edge.v == v) || (edge.v == parent[v] && edge.u == v)) {
                        mstEdges.add(edge);
                        totalWeight += edge.weight;
                        break;
                    }
                }
            }
        }

        bestWeight = totalWeight;
        bestEdges = mstEdges;

        long endTime = System.nanoTime();
        long endMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        double timeMs = (endTime - startTime) / 1_000_000.0;
        double memoryKb = Math.max(0.0, (endMemory - startMemory) / 1024.0);

        return new MSTResult(bestWeight, new ArrayList<>(bestEdges), timeMs, memoryKb, nodesVisited,
                nodesPruned);
    }

    private void copyUnionFind(UnionFind source, UnionFind dest) {
        for (int i = 0; i < nVertices; i++) {
            dest.parent[i] = source.parent[i];
            dest.rank[i] = source.rank[i];
        }
    }

    // ==================== CLASSES INTERNAS ====================

    /**
     * Classe para representar uma aresta
     */
    public static class Edge {
        public int u, v, weight;

        public Edge(int u, int v, int weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("(%d-%d:%d)", u, v, weight);
        }
    }

    /**
     * Estrutura de dados Union-Find
     */
    public static class UnionFind {
        int[] parent;
        int[] rank;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return false;
            }

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
            return true;
        }
    }

    /**
     * Classe interna para representar um nó na árvore de busca (Branch and Bound)
     */
    private static class Node {
        int edgeIdx;
        int weight;
        List<Edge> edges;
        UnionFind uf;

        Node(int edgeIdx, int weight, List<Edge> edges, UnionFind uf) {
            this.edgeIdx = edgeIdx;
            this.weight = weight;
            this.edges = edges;
            this.uf = uf;
        }
    }

    /**
     * Classe para encapsular resultados do solver
     */
    public static class MSTResult {
        public int weight;
        public List<Edge> edges;
        public double timeMs;
        public double memoryKb;
        public long nodesVisited;
        public long nodesPruned;

        public MSTResult(int weight, List<Edge> edges, double timeMs, double memoryKb,
                long nodesVisited, long nodesPruned) {
            this.weight = weight;
            this.edges = edges;
            this.timeMs = timeMs;
            this.memoryKb = memoryKb;
            this.nodesVisited = nodesVisited;
            this.nodesPruned = nodesPruned;
        }

        @Override
        public String toString() {
            return String.format("MSTResult{weight=%d, edges=%s, time=%.2fms, memory=%.2fkb, " +
                    "nodes_visited=%d, nodes_pruned=%d}",
                    weight, edges, timeMs, memoryKb, nodesVisited, nodesPruned);
        }
    }
}
