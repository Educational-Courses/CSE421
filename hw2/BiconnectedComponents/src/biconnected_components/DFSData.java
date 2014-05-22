package biconnected_components;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class implements depth-first search for IntGraph.
 * 
 * @author Chun-Wei Chen
 * @version 07/11/13
 */
public class DFSData {
	private IntGraph graph;  // the graph that is used to do depth-first search
	private int[] dfsNums;  // DFS# of each node
	private List<Integer>[] backEdges;  // back edges of each node
	private List<Integer>[] children;  // children of each node
	private int[] low;  // LOW value of each node
	private Set<Integer> articPts;  // indicate whether a node is articulation point
	private int root;  // root node of the recently finished depth-first search
	private int dfsCount;  // counter for depth-first search to keep track of DFS#

	/**
	 * Creates an object to store the data that depth-first search needs and 
	 * to hold the data/information getting back from depth-first search.
	 * 
	 * @param g the graph that is going to be used to do depth-first search
	 * @throws IllegalArgumentException if g == null
	 */
	@SuppressWarnings("unchecked")
	public DFSData(IntGraph g) {
		if (g == null)
			throw new IllegalArgumentException("Graph passed in cannot be null.");
		
		graph = new IntGraph(g);
		dfsNums = new int[g.numberOfVertices()];
		for (int i = 0; i < dfsNums.length; i++)
			dfsNums[i] = -1;
		
		backEdges = (LinkedList<Integer>[]) new LinkedList[g.numberOfVertices()];
		for (int i = 0; i < backEdges.length; i++)
			backEdges[i] = new LinkedList<Integer>();
		
		children = (LinkedList<Integer>[]) new LinkedList[g.numberOfVertices()];
		for (int i = 0; i < children.length; i++)
			children[i] = new LinkedList<Integer>();
		
		low = new int[g.numberOfVertices()];
		articPts = new HashSet<Integer>();
		root = -1;
		dfsCount = 0;
	}
	
	/**
	 * Does depth-first search from node v, updating dfsNums and low along the way. 
	 * Also stores children and back edges of node v.
	 * 
	 * @param v a node
	 * @param vParent a parent node of v, use -1 as parent if node v is the root
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in graph and 
	 * if vParent < -1 or vParent >= total number of vertices in graph
	 */
	public void depthFirstSearch(int v, int vParent) {
		if (doneWithDFS(v))
			return;
		
		if (v < 0 || v >= graph.numberOfVertices())
			throw new IllegalArgumentException("v must be in the range of 0 and " + 
											   (graph.numberOfVertices() - 1));
		
		if (vParent < -1 || vParent >= graph.numberOfVertices())
			throw new IllegalArgumentException("vParent must be in the range of -1 and " + 
					   						   (graph.numberOfVertices() - 1));
		
		// reset the data stored in DFSData if the user does depth first search starting at different node
		if (dfsCount == graph.numberOfVertices())
			resetDFSData();
		
		dfsCount++;
		
		// set the root node of current depth first search
		if (dfsCount == 1)
			root = v;
		
		dfsNums[v] = dfsCount;
		low[v] = dfsNums[v];
		
		List<Integer> neighbors = graph.neighborsOf(v);
		for (int x : neighbors) {
			if (dfsNums[x] == -1) {
				children[v].add(x);
				depthFirstSearch(x, v);
				low[v] = Math.min(low[v], low[x]);
				
				if (dfsNums[v] == 1) {
					// root node is articulation point if it has more than 1 child
					if (children[v].size() > 1)
						articPts.add(v);
				} else if (low[x] >= dfsNums[v]) {
					articPts.add(v);
				}
			} else if (x != vParent) {
				if (dfsNums[x] < dfsNums[v])
					backEdges[v].add(x);
				low[v] = Math.min(low[v], dfsNums[x]);
			}
		}
	}
	
	/**
	 * Resets the DFSData when the user calls depthFirstSearch on different starting node.
	 */
	private void resetDFSData() {
		for (int i = 0; i < dfsNums.length; i++)
			dfsNums[i] = -1;
		
		for (int i = 0; i < backEdges.length; i++)
			backEdges[i] = new LinkedList<Integer>();
		
		for (int i = 0; i < children.length; i++)
			children[i] = new LinkedList<Integer>();
		
		low = new int[graph.numberOfVertices()];
		articPts = new HashSet<Integer>();
		root = -1;
		dfsCount = 0;
	}
	
	/**
	 * Returns the root node of the most recently finished depth-first search. 
	 * 
	 * @return the root node of the most recently finished depth-first search
	 */
	public int getRootOfDFS() {
		return root;
	}
	
	/**
	 * Return a list of back edges of the node passed in.
	 * 
	 * @param v a node
	 * @return a list of back edges of v
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in the graph
	 */
	public List<Integer> backEdgesOf(int v) {
		if (v < 0 || v >= graph.numberOfVertices())
			throw new IllegalArgumentException("v must be in the range of 0 and " + 
											   (graph.numberOfVertices() - 1));
		
		return new LinkedList<Integer>(backEdges[v]);
	}
	
	/**
	 * Return a list of children of the node passed in.
	 * 
	 * @param v a node
	 * @return a list of back edges of v
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in the graph
	 */
	public List<Integer> childrenOf(int v) {
		if (v < 0 || v >= graph.numberOfVertices())
			throw new IllegalArgumentException("v must be in the range of 0 and " + 
											   (graph.numberOfVertices() - 1));
		
		return new LinkedList<Integer>(children[v]);
	}
	
	/**
	 * Returns value of LOW of the node passed in.
	 * 
	 * @param v a node
	 * @return value of LOW of node v
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in the graph
	 */
	public int lowOf(int v) {
		if (v < 0 || v >= graph.numberOfVertices())
			throw new IllegalArgumentException("v must be in the range of 0 and " + 
											   (graph.numberOfVertices() - 1));
		
		return low[v];
	}
	
	/**
	 * Returns true if the node passed in is an articulation point of the graph.
	 * 
	 * @param v a node
	 * @return true if the node passed in is an articulation point of the graph
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in the graph
	 */
	public boolean isArticulationPoint(int v) {
		if (v < 0 || v >= graph.numberOfVertices())
			throw new IllegalArgumentException("v must be in the range of 0 and " + 
											   (graph.numberOfVertices() - 1));
		
		return articPts.contains(v);
	}
	
	/**
	 * Returns a list of articulation points of the graph.
	 * 
	 * @return a list of articulation points of the graph
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in the graph
	 */
	public Set<Integer> getArticulationPoints() {
		return new HashSet<Integer>(articPts);
	}
	
	/**
	 * Returns true if this DFSData stores the date/info of depth-first search starting at node v.
	 * 
	 * @return true if this DFSData stores the date/info of depth-first search starting at node v
	 * @throws IllegalArgumentException if v < 0 or v >= total number of vertices in the graph
	 */
	public boolean doneWithDFS(int v) {
		if (v < 0 || v >= graph.numberOfVertices())
			throw new IllegalArgumentException("v must be in the range of 0 and " + 
											   (graph.numberOfVertices() - 1));
		
		return root == v && dfsCount == graph.numberOfVertices();
	}
	
	/**
	 * Returns a list of biconnected components of the graph.
	 * 
	 * @return a list of biconnected components of the graph
	 * @throws IllegalStateException if depthFirstSearch is not called before calling this function
	 */
	public List<LinkedList<IntNodesEdge>> getBiconnectedComponenets() {
		if (root == -1)
			throw new IllegalStateException("Must call depthFirstSearch before calling this function.");
		
		List<LinkedList<IntNodesEdge>> biComps = new LinkedList<LinkedList<IntNodesEdge>>();
		for (int v = 0; v < graph.numberOfVertices(); v++) {
			for (int x : children[v]) {
				if (low[x] >= dfsNums[v]) {
					LinkedList<IntNodesEdge> comp = new LinkedList<IntNodesEdge>();
					comp.add(new IntNodesEdge(v, x));
					biconnectedComponenetsHelper(comp, x);
					biComps.add(comp);
				}
			}
		}
		return biComps;
	}
	
	/**
	 * A recursive helper function to find a biconnected component.
	 * 
	 * @param c current component
	 * @param v a node
	 */
	private void biconnectedComponenetsHelper(LinkedList<IntNodesEdge> c, int v) {
		for (int w : backEdges[v])
			c.add(new IntNodesEdge(v, w));
		
		for (int x : children[v]) {
			if (low[x] < dfsNums[v]) {
				c.add(new IntNodesEdge(v, x));
				biconnectedComponenetsHelper(c, x);
			}
		}
	}
	
	/**
	 * This inner class represents an undirected edge of an IntGraph.
	 */
	class IntNodesEdge {
		int v;  // one end of the edge
		int u;  // the other end of the edge
		
		/**
		 * Constructs an undirected edge of an IntGraph.
		 * 
		 * @param v one end of the edge
		 * @param u the other end of the edge
		 * @throws IllegalArgumentException if either one of the argument is < 0 
		 * or >= total number of vertices in the graph
		 */
		IntNodesEdge (int v, int u) {
			if (v < 0 || v >= graph.numberOfVertices())
				throw new IllegalArgumentException("v must be in the range of 0 and " + 
												   (graph.numberOfVertices() - 1));
			
			if (u < 0 || u >= graph.numberOfVertices())
				throw new IllegalArgumentException("u must be in the range of 0 and " + 
												   (graph.numberOfVertices() - 1));
			
			this.v = v;
			this.u = u;
		}
		
		public String toString() {
			return v + "-" + u; 
		}
	}
	
	/**
	 * A method to test if functions work as expected using the graph in problem 1.
	 * 
	 * @throws FileNotFoundException if problem1.txt was not found in 
	 * src/biconnected_components/data/ folder
	 */
	private static void testForProblem1() throws FileNotFoundException {
		// problem1.txt is the file I created using the graph in problem 1 
		// to simply check if the output of my implementation of 
		// articulation point and other things match up with what I've done 
		// by hand in problem 1
		IntGraph g = IntGraph.buildIntGraph("src/biconnected_components/data/problem1.txt");
		
		System.out.println("Number of vertices: " + g.numberOfVertices());
		System.out.println("Number of edges: " + g.numberOfEdges());
		
		DFSData dfsd = new DFSData(g);
		dfsd.depthFirstSearch(0, -1);
		assert(dfsd.doneWithDFS(0));
		
		for (int i = 0; i < g.numberOfVertices(); i++) {
			System.out.println(i + "'s back edges: " + dfsd.backEdges[i]);
			System.out.println(i + "'s children: " + dfsd.children[i]);
		}
		
		Set<Integer> ap = dfsd.getArticulationPoints();
		System.out.println("Number of articulation points: " + ap.size());
		
		System.out.println("List of articulation points:");
		for (int n : ap)
			System.out.print(" " + n);
		System.out.println();
		
		List<LinkedList<IntNodesEdge>> bc = dfsd.getBiconnectedComponenets();
		System.out.println("Number of biconnected components: " + bc.size());
		
		System.out.print("Biconnected components: ");
		for (LinkedList<IntNodesEdge> c : bc) {
			System.out.println();
			for (IntNodesEdge e : c)
				System.out.print(" " + e);
		}
		System.out.println();
	}
	
	/**
	 * This method runs a required test case the professor provided.
	 * It prints out number of nodes, number of edges, number of biconnected components, 
	 * number of articulation points, list the articulation points, 
	 * list the edges in each biconnected component, and the algorithm¡¦s run time. 
	 * 
	 * @throws FileNotFoundException if biconn-g16-2-5-1.txt was not found 
	 * in src/biconnected_components/data/ folder
	 */
	private static void requiredTestCase() throws FileNotFoundException {
		IntGraph g = IntGraph.buildIntGraph("src/biconnected_components/data/biconn-g16-2-5-1.txt");
		DFSData dfsd = new DFSData(g);
		dfsd.depthFirstSearch(0, -1);
		Set<Integer> ap = dfsd.getArticulationPoints();
		
		int loopCount = 500;
		long totalTime = 0;
		List<LinkedList<IntNodesEdge>> bc = null;
		for (int i = 0; i < loopCount; i++) {
			long startTime = System.currentTimeMillis();
			bc = dfsd.getBiconnectedComponenets();
			long endTime = System.currentTimeMillis();
			totalTime += endTime - startTime;
		}
		double averageTime = totalTime * 1.0 / loopCount;
		
		System.out.printf("Number of vertices: %d\n", g.numberOfVertices());
		System.out.printf("Number of edges: %d\n", g.numberOfEdges());
		System.out.printf("Number of articulation points: %d\n", ap.size());
		System.out.printf("Number of biconnected components: %d\n", bc.size());
		System.out.println();
		
		System.out.println("List of articulation points: ");
		for (int n : ap)
			System.out.print(" " + n);
		System.out.println("\n");
		
		System.out.print("Biconnected components: ");
		for (LinkedList<IntNodesEdge> c : bc) {
			System.out.println();
			for (IntNodesEdge e : c)
				System.out.print(" " + e);
		}
		System.out.println("\n");
		
		System.out.printf("Algorithm run time: %.3f milliseconds\n",  averageTime);
	}
	
	private static void runTestCases() throws FileNotFoundException {
		String folderPath = "src/biconnected_components/data/";
		String excluded1 = "problem1.txt";
		String excluded2 = "biconn-g16-2-5-1.txt";
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (!(listOfFiles[i].getName().equals(excluded1) || 
				  listOfFiles[i].getName().equals(excluded2)))
				runIndividualTestCase(folderPath + listOfFiles[i].getName());
		}
	}
	
	private static void runIndividualTestCase(String filename) throws FileNotFoundException {
		IntGraph g = IntGraph.buildIntGraph(filename);
		DFSData dfsd = new DFSData(g);
		dfsd.depthFirstSearch(0, -1);
		
		int loopCount = 500;
		long totalTime = 0;
		for (int i = 0; i < loopCount; i++) {
			long startTime = System.currentTimeMillis();
			dfsd.getBiconnectedComponenets();
			long endTime = System.currentTimeMillis();
			totalTime += endTime - startTime;
		}
		double averageTime = totalTime * 1.0 / loopCount;
		
		System.out.printf("Number of vertices: %d\n", g.numberOfVertices());
		System.out.printf("Number of edges: %d\n", g.numberOfEdges());
		System.out.printf("Algorithm run time: %.3f milliseconds\n",  averageTime);
	}
	
	/**
	 * Simple main method to test if functions work as expected.
	 * 
	 * @param args
	 * @throws FileNotFoundException if files were not found
	 */
	public static void main(String[] args) throws FileNotFoundException {
		testForProblem1();
		requiredTestCase();
		runTestCases();
	}
}
