package biconnected_components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * This class represents a graph with nodes that contain non-negative integers.
 * 
 * @author Chun-Wei Chen
 * @version 07/11/13
 */
public class IntGraph {
	private LinkedList<Integer>[] adjList;  // adjacency list of each node
	private final int vertices;  // number of vertices
	private int edges;  // number of edges
	
	/**
	 * Creates a new instance of IntGraph with total number of v vertices.
	 * 
	 * @param v total number of vertices in this graph
	 * @throws IllegalArgumentException if v < 0
	 */
	@SuppressWarnings("unchecked")
	public IntGraph(int v) {
		if (v < 0)
			throw new IllegalArgumentException("v passed in must be " + 
					                           ">= 0.");
		
		adjList = (LinkedList<Integer>[]) new LinkedList[v];
		for (int i = 0; i < v; i++)
			adjList[i] = new LinkedList<Integer>();
		
		vertices = v;
		edges = 0;
	}
	
	/**
	 * Creates a copy of an existing IntGraph.
	 * 
	 * @param g an IntGraph to be copied
	 */
	public IntGraph(IntGraph g) {
		this(g.vertices);
		this.edges = g.edges;
		
		for (int i = 0; i < g.vertices; i++)
			adjList[i] = new LinkedList<Integer>(g.adjList[i]);
	}
	
	/**
	 * Adds edges from <var>v</var> to <var>u</var> and 
	 * from <var>u</var> to <var>v</var>.
	 * 
	 * @param v one end of the edge
	 * @param u the other end of the edge
	 * @throws IllegalArgumentException if either v or u is greater than 
	 * or equal to total number of vertices, or less than 0
	 */
	public void addEdge(int v, int u) {
		if (v < 0 || v >= vertices)
			throw new IllegalArgumentException("v must be in the range of 0 " +
					                           "to (total number of vertices - 1).");
		
		if (u < 0 || u >= vertices)
			throw new IllegalArgumentException("u must be in the range of 0 " +
                                               "to (total number of vertices - 1).");
		
		adjList[v].add(u);
		adjList[u].add(v);
		
		// increase the number of edges by 1 since it's undirected graph
		edges++;
	}
	
	/**
	 * Returns total number of vertices in this graph.
	 * 
	 * @return total number of vertices in this graph
	 */
	public int numberOfVertices() {
		return vertices;
	}
	
	/**
	 * Returns total number of vertices in this graph.
	 * 
	 * @return total number of vertices in this graph
	 */
	public int numberOfEdges() {
		return edges;
	}
	
	/**
	 * Returns true if u is adjacent to v. 
	 * 
	 * @param u a node
	 * @param v another node
	 * @return true if u is adjacent to v
	 * @throws IllegalArgumentException if either v or u is greater than 
	 * or equal to total number of vertices, or less than 0
	 */
	public boolean isAdjacentTo(int u, int v) {
		if (u < 0 || u >= vertices)
			throw new IllegalArgumentException("u must be in the range of 0 " +
                                               "to (total number of vertices - 1).");
		
		if (v < 0 || v >= vertices)
			throw new IllegalArgumentException("v must be in the range of 0 " +
					                           "to (total number of vertices - 1).");
		
		return adjList[v].contains(u);
	}
	
	/**
	 * Returns a list of nodes that are neighbors of the node passed in.
	 * 
	 * @param v a node
	 * @return a list of nodes that are neighbors of v
	 * @throws IllegalArgumentException if v is greater than 
	 * or equal to total number of vertices, or less than 0
	 */
	public List<Integer> neighborsOf(int v) {
		if (v < 0 || v >= vertices)
			throw new IllegalArgumentException("v must be in the range of 0 " +
					                           "to (total number of vertices - 1).");
		
		return new LinkedList<Integer>(adjList[v]);
	}
	
	/**
	 * Builds the graph using the file passed in. The file needs to be 
	 * well-formatted. The file should contain a total of odd number of 
	 * non-negative integers. The first integer N must be a positive integer 
	 * which indicates number of nodes in the graph, and followed by some 
	 * pairs of non-negative integers in the range of 0 to N-1, which 
	 * represent the edges of the graph.</p>
	 * 
	 * Assume the input file obeyed the format described above.</p>
	 * 
	 * <var>filename</var> passed in should be an absolute pathname.
	 * 
	 * @param filename the absolute pathname of the file that is used to built the graph
	 * @return a graph built from the file passed in
	 * @throws FileNotFoundException if file passed in is not found
	 * @throws IllegalArgumentException if filename == null and if file 
	 * passed in is not well-formatted 
	 */
	public static IntGraph buildIntGraph(String filename) throws FileNotFoundException {
		if (filename == null)
			throw new IllegalArgumentException("File name cannot be null.");

		Scanner sc = new Scanner(new File(filename));
		
		// check if the file has at least one integer
		if (!sc.hasNextInt()) {
			sc.close();
			throw new IllegalArgumentException("File is not well-formmated.");
		}
		
		int vertices = sc.nextInt();
		
		// check if the first integer in the file is greater than 0
		if (vertices <= 0) {
			sc.close();
			throw new IllegalArgumentException("First integer in the file " +
											   "must be a positive integer.");
		}
		
		IntGraph g = new IntGraph(vertices);
		
		while (sc.hasNextInt()) {
			int v = sc.nextInt();
			
			// check if v is in the range of 0 to vertices - 1
			if (v < 0 || v >= vertices) {
				sc.close();
				throw new IllegalArgumentException("node must be non-negative integer " +
						   						   "in the range of 0 to total number " + 
						   						   "of vertices - 1");
			}
			
			// check if the file contain pair of integers after the first integer
			if (!sc.hasNextInt()) {
				sc.close();
				throw new IllegalArgumentException("File is not well-formatted.");
			}
			
			int u = sc.nextInt();
			
			// check if u is in the range of 0 to vertices - 1
			if (u < 0 || u >= vertices) {
				sc.close();
				throw new IllegalArgumentException("node must be non-negative integer " +
												   "in the range of 0 to total number " + 
												   "of vertices - 1");
			}
			
			g.addEdge(v, u);
		}
		
		sc.close();
		return g;
	}
	
	/**
	 * Simple main method to test the graph built from the test file.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
		IntGraph g = buildIntGraph("src/biconnected_components/data/n16d3s1.txt");
		System.out.println("Number of vertices: " + g.numberOfVertices());
		System.out.println("Number of edges: " + g.numberOfEdges());
		for (int i = 0; i < 16; i++)
			System.out.println("Neighbor of " + i + ": " + g.neighborsOf(i));
		System.out.println("Is 1 adjacent to 11? " + g.isAdjacentTo(1, 11));
		System.out.println("Is 0 adjacent to 9? " + g.isAdjacentTo(0, 9));
	}
}
