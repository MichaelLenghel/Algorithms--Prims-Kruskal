// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

/**Name: Michael Lenghel
   Student Number: c16434974
	*/

import java.io.*;
import java.util.Scanner;

class Heap
{
    private int[] h;	   // heap array
    public int[] hPos;	   // hPos[h[k]] == k
    private int[] dist;    // dist[v] = priority of v

    private int N;         // heap size
   
    // The heap constructor gets passed from the Graph:
    //    1. maximum heap size
    //    2. reference to the dist[] array
    //    3. reference to the hPos[] array
    public Heap(int maxSize, int[] _dist, int[] _hPos) 
    {
        N = 0;
        h = new int[maxSize + 1];
        //Point to the arrays
        dist = _dist;
        hPos = _hPos;
        dist[0] = 0;
    }


    public boolean isEmpty() 
    {
        return N == 0;
    }


    public void siftUp( int k) 
    {
        int v = h[k];
        h[0] = 0;
        // k / 2 is 0 on first iteration
        //While infinity < 0
        while( dist[v] < dist[h[k / 2]]) 
        {
            //System.out.println("a[k / 2] inside loop = " + a[k / 2]);
            h[k] = h[k / 2];
            //Saying hPos[numberDealingWith] = Postion in heap
            hPos[h[k]] = k;
            k = k /  2;
        }

        h[k] = v;
        hPos[v] = k;
    }

    public void siftDown( int k) 
    {
        int v, j;
        //Assign element we will be shifting top v
        v = h[k];

        while(dist[v] > dist[h[k * 2]])
        {
            h[k] = h[k * 2];
            hPos[h[k]] = k;
            k = k * 2;

            if (k * 2 > N) 
            {
                break;
            }
        }//end while
        //Finally assig the node we are sifting to its correct position
        h[k] = v;
        hPos[v] = k;
    }


    public void insert( int x) 
    {
        h[++N] = x;
        siftUp( N);
    }


    public int remove() 
    {   
        int v = h[1];
        hPos[v] = 0; // v is no longer in heap
        h[N+1] = 0;  // put null node into empty spot
        
        h[1] = h[N--];
        siftDown(1);
        
        return v;
    }
}

class Graph {
    class Node {
        /**Note: We don't need dest as adj[u].vert = v;*/
                                        //Vurepresents from where, vert the dest encapsulates v
        public int vert;
        public int wgt;
        public Node next;
    }
    
    // V = number of vertices
    // E = number of edges
    // adj[] is the adjacency lists array
    private int V, E;
    private Node[] adj;
    private Node z;
    private int[] mst;
    
    // used for traversing graph
    private int[] visited;
    private int id;
    
    
    // default constructor
    public Graph(String graphFile)  throws IOException
    {
        int u, v;
        int e, wgt;
        Node node, temp;

        FileReader fr = new FileReader(graphFile);
        BufferedReader reader = new BufferedReader(fr);
               
        String splits = " +";  // multiple whitespace as delimiter
        String line = reader.readLine();        
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // create sentinel node
        z = new Node(); 
        z.next = z;
        
        // create adjacency lists, initialised to sentinel node z
        //It's of type v, since adj[v].vert = u has v to always represent origin and will be on bounds of vertices       
        adj = new Node[V+1];        
        for(v = 1; v <= V; ++v)
            adj[v] = z;               
        
       // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            wgt = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + wgt + ")--" + toChar(v));    
            System.out.println("");

            //Create the adj list here. Since using prims, we set for both origin and destination to the array
            temp = adj[u];
            adj[u] = new Node();
            adj[u].vert = v;
            adj[u].wgt = wgt;
            adj[u].next = temp;

            temp = adj[v];
            adj[v] = new Node();
            adj[v].vert = u;
            adj[v].wgt = wgt;
            adj[v].next = temp;
        } 
    }
   
    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
    
    // method to display the graph representation
    public void display() {
        int v;
        Node n;
        
        for(v=1; v<V; ++v){
            System.out.print("\nadj[" + toChar(v) + "] ->" );
            for(n = adj[v]; n != z; n = n.next) 
                System.out.print(" |" + toChar(n.vert) + " | " + n.wgt + "| ->");    
        }
        System.out.println("");
    }

    
    public void MST_Prim(int s)
    {
        int v;
        int wgt_sum = 0;
        /**Parent stores the parent vertex of u in MST and is MST, dist stores dist of a vertex u to some nearest vertex*/
        int[] dist, parent, hPos;

        /**Java initialises all arrays to 0 on its own*/
        dist = new int[V + 1];
        parent = new int[V + 1];
        hPos = new int[V + 1];

        //Initialise dist
        for (int i = 1; i < V + 1;i++ ) 
            dist[i] = Integer.MAX_VALUE;

        //Sets the starting element to 0, which acts as a nice buffer to stop it from going in an infite loo
        dist[s] = 0;
        parent[s] = 0;
        
        Heap pq =  new Heap(V, dist, hPos);
        pq.insert(s);//s is the root of the mst

        while(! ( pq.isEmpty() ) )
        {
            v = pq.remove();
            dist[v] = -dist[v];
            Node t = adj[v];
            //for (t = adj[u]; t != z; t = t.next) //(Just another method of iterating)
            while(t != z)
            {
                if (t.wgt < dist[t.vert])//dist[t] was original
                {
                    dist[t.vert] = t.wgt;
                    parent[t.vert] = v;//Adds to min span tree
                    
                    //If the graph connects to a new point, add it. Otherwise sift it up to find order
                    if (hPos[t.vert] == 0) 
                        pq.insert(t.vert);  
                    else
                        pq.siftUp(hPos[t.vert]);
                }
                t = t.next;
            }//end inner while
        }//end outer while

        for (int d: dist) 
            wgt_sum += Math.abs(d);

        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        
        mst = parent;   

        showMST(s);                         
    }
    
    public void showMST(int s)
    {
            System.out.println("Starting vertex is: " + toChar(s));
            System.out.print("\n\nMinimum Spanning tree parent array is:\n");
            for(int v = 1; v <= V; ++v)
            {
                if (toChar(mst[v]) > 64 && toChar(mst[v]) < 91) 
                {
                    System.out.println(toChar(v) + " -> " + toChar(mst[v]));  
                }
            }
            System.out.println(""); 
    }

}//end class Graph

public class PrimLists 
{
    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);  
        System.out.println("Please enter the name of the graph"); 
        String  fname = sc.next();//e.g.wGraph3.txt  
        
        System.out.println("Please enter the starting vertex of the graph"); 
        int startVertex = sc.nextInt();  

        Graph g = new Graph(fname);
       
        g.display();
               
        g.MST_Prim(startVertex);
    }
    
    
}
