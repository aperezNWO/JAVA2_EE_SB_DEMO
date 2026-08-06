package com.example.demo;
import java.util.*;

public class GFG {
    // The output array. dist[i] will hold the shortest distance from src to i
    public int[] dist;
    private int vertexSize;
    public List<String> path;

    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    private int minDistance(int[] dist, boolean[] sptSet) {
        // Initialize min value
        int min = Integer.MAX_VALUE, minIndex = -1;

        for (int v = 0; v < vertexSize; v++)
            if (!sptSet[v] && dist[v] <= min) {
                min = dist[v];
                minIndex = v;
            }

        return minIndex;
    }

    // Function that implements Dijkstra's single source shortest path algorithm
    // for a graph represented using adjacency matrix representation
    public void dijkstra(int[][] graph, int src, int vertexSize) {
        this.vertexSize = vertexSize;
        dist = new int[vertexSize]; // The output array. dist[i] will hold the shortest distance from src to i
        path = new ArrayList<>(vertexSize);

        // sptSet[i] will be true if vertex i is included in shortest path tree
        // or shortest distance from src to i is finalized
        boolean[] sptSet = new boolean[vertexSize];

        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < vertexSize; i++) {
            path.add("");
        }
        
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(sptSet, false);

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // Find shortest path for all vertices
        for (int count = 0; count < vertexSize - 1; count++) {
            // Pick the minimum distance vertex from the set of vertices not yet processed.
            // u is always equal to src in first iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the picked vertex.
            for (int v = 0; v < vertexSize; v++) {

                // Update dist[v] only if is not in 
                // sptSet, there is an edge from u 
                // to v, and total weight of path 
                // from src to v through u is smaller 
                // than current value of dist[v] 
                if (!sptSet[v] 
                    && 
                    graph[u][v] != 0 
                    &&
                    dist[u] != Integer.MAX_VALUE 
                    && 
                    dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                    path.set(v, path.get(u) + String.format("[%d,%d]≡", u, v));
                }
            }
        }
    }
}

