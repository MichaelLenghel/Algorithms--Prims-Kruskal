// Simple weighted graph representation 
// Uses an Adjacency Linked Lists, suitable for sparse graphs

import java.io.*;

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
        dist = _dist;
        hPos = _hPos;
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

        //dist[v] = Integer.MAX_VALUE;
        //While node at pos k, has a left child node
        while(dist[v] > dist[h[k * 2]] && N >= (k * 2))
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

    public boolean isElementHeap(int u)
    {
        for (int i = 0;i < h.length ;i++ ) 
        {
            if (h[i] == u) 
            {
                  return true;
            }  
        }
        return false;
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
    Node head, tail;
    private int[] mst;
    
    // used for traversing graph
    private int[] visited;
    private int id;
    
    
    // default constructor
    public Graph(String graphFile)  throws IOException
    {
        head = tail = z;
        int u, v, lastV = -1;
        int e, wgt;

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

            //Has to be adj[u] as we are mapping from First column to second
            addEdge(u, v, wgt);
        } 
    }

    public void addEdge(int u, int v, int wgt)
    {
        Node curr, prev, t;
        t = new Node();
        //Initialise t with data
        t.vert = v;
        t.wgt = wgt;
        t.next = z;

        prev = z;
        curr = adj[u];

        while(curr != z)
        {
            System.out.println("Just entered");
            prev = curr;
            curr = curr.next;
        }

        //If prev is null, then the list is empty
        if (prev == z) 
        {
            adj[u] = t;
            //t.next = curr;
        }

        //List is not empty
        else
        {
            prev.next = t;
            t.next = curr;
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

    public int getMaxValue()
    {
        int max = Integer.MIN_VALUE;

        for(int v=1; v<V; ++v)
        {
            for(Node n = adj[v]; n != z; n = n.next)
            {
                if (adj[v].vert > max) 
                {
                    max = adj[v].vert;
                }
            }
        }
        //Need it to be able to hold no. of edges at minimum
        if (max < E) 
        {
            return E + 1;
        }
        else
        {
            return max;
        }
        
    }


    
   /** Prequesites b4 calling this:
    dist[h[k]] references a distance from u to v*/
    
    public void MST_Prim(int s)
    {
        int v, u;
        int wgt, e, wgt_sum = 0;
        /**Parent stores the parent vertex of u in MST and is MST, dist stores dist of a vertex u to some nearest vertex*/
        //
        int[] dist, parent, hPos;
        Node t;

        dist = new int[E + 1];
        parent = new int[E + 1];
        hPos = new int[getMaxValue()];
        //Initialise arrays
        for (e = 0; e < E + 1;e++ ) 
        {
            //Indicates that it's null or not in the heap
            hPos[e] = parent[e] = 0;
            dist[e] = Integer.MAX_VALUE;
        }

        //Sets the bottom element as 0, which acts as a nice buffer to stop it from going in an infite loop in siftUp()
        dist[0] = 0;
        
        Heap pq =  new Heap(V, dist, hPos);

        pq.insert(s);//s is the root of the mst
        
        for (int count = 0; count < V - 1; count++) 
        {
            u = pq.remove();//Add v to mst
            dist[u] -= dist[u]; // Set distance to 0, marks as in the MST

            for (t = adj[u]; t != z; t = t.next) 
            {
                if (adj[u].wgt < dist[u]) 
                {
                    //Assign the weight from one vertex to a vertex near it
                    dist[u] = adj[u].wgt;
                    //Add to the minimum spanning tree
                    parent[u] = adj[u].vert;//adj[u].vert = v (where v is the destination)
                    wgt_sum += adj[u].wgt;
                    //If the graph connects to a new point, add it
                    if (pq.isElementHeap(u)) 
                    {
                        pq.insert(u);      
                    }
                    else
                    {
                        //Note: hPos stores the position on the heap
                        pq.siftUp(pq.hPos[u]);
                    } 
                }
            }//end inner for
        }//end outer for
        System.out.print("\n\nWeight of MST = " + wgt_sum + "\n");
        
        //mst = parent;                            
    }

    // public int wgt(int u, int v)
    // {
    //     int wgtFound = 0;
    //     for (Node t = adj[u]; t != z; t = t.next) 
    //     {
    //         if (t.vert == v) 
    //         {
    //             wgtFound = 1;
    //             break;
    //         }
    //     }

    //     if (wgtFound == 1) 
    //     {
    //         //Will be the correct weight as t.vert points to the correct destination
    //         return adj[u].wgt;
    //     }
    //     else
    //     {
    //         //Weight not found, so return maximum value to put an empty element in the array to signify that
    //         return  Integer.MAX_VALUE;
    //     }
    // }
    
    public void showMST()
    {
            System.out.print("\n\nMinimum Spanning tree parent array is:\n");
            for(int v = 1; v <= V; ++v)
                System.out.println(toChar(v) + " -> " + toChar(mst[v]));
            System.out.println("");
    }

}//end class Graph

public class PrimLists 
{
    public static void main(String[] args) throws IOException
    {
        int s = 2;
        String fname = "wGraph3.txt";               

        Graph g = new Graph(fname);
       
        g.display();
               
        g.MST_Prim(2);
    }
    
    
}