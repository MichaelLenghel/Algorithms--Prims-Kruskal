// Kruskal's Minimum Spanning Tree Algorithm
// Union-find implemented using disjoint set trees without compression

import java.io.*;    
 
class Edge 
{
    public int u, v, wgt;

    public Edge() 
    {
        u = 0;
        v = 0;
        wgt = 0;
    }

    public Edge(int x, int y, int w)
     {
        u = x;
        v = y;
        wgt = w;
    }
    
    public void show() 
    {
        System.out.print("Edge " + toChar(u) + "--" + wgt + "--" + toChar(v) + "\n") ;
    }

    public void showWeight() 
    {
        System.out.println(wgt) ;
    }

    //We need this when printing out the tree structure of the heap
    public void showWeightWOSpace() 
    {
         System.out.print(wgt + "  ");
    }
    
    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }

    //Accessor methods
    public int getOrigin()
    {
        return u;
    }
    public int getDest()
    {
        return v;
    }
    public int getWeight()
    {
        return wgt;
    }

    //Set the weight
    public static Edge setValues(int _u, int _v, int weight)
    {
        Edge e = new Edge(_u, _v, weight);
        return e;
    }
}


class Heap
{
	private Edge[] h;
    int N, Nmax;
    Edge[] edge;


    // Bottom up heap construc
    public Heap(int _N, Edge[] _edge) 
    {
        int i;
        Nmax = _N;
        N = 0;
        h = new Edge[Nmax+1];
        edge = _edge;

        //Insert each edge into the heap
        while(N != Nmax)
        {
           N++;
           //The array in the heap will hold the weight
           h[N] = edge[N];
           siftUp(N);
        }
    }

    public void siftUp( int k) 
    {
       Edge v = edge[k];
       h[0] = Edge.setValues(0, 0, Integer.MIN_VALUE);

       while( v.getWeight() <= h[k / 2].getWeight() ) 
       {
          h[k] = h[k / 2];
          k = k / 2;
       }
       //Compelte the swap
       h[k] = v;
    }

    private void siftDown( int k) {
        Edge v, j;

        //Assign the value we will be sifting down
        v = h[k];

        //If N <=2, then we have no need to check h[(k * 2) + 1], which from the getgo will check a value at N == 3
        if (N <= 2) 
        {
            if( v.getWeight() >= h[k * 2].getWeight() )
            {
                h[k] = h[k * 2];
                k = k * 2;
            }
            h[k] = v;
            return;
        }

        //While loop iterates through both sides of the heap rather than just the child to the left
        while( v.getWeight() >= h[k * 2].getWeight() || v.getWeight() >= h[(k * 2) + 1].getWeight() ) 
        {
            if (h[k * 2].getWeight() <=  h[k * 2 + 1].getWeight())
            {
                h[k] = h[k * 2];
                k = k * 2;
            }

            else
            {
                h[k] = h[(k * 2) + 1];
                k = (k * 2) + 1;
            }

            //Don't go out of bounds on (k * 2 + 1), but still swap with k * 2 if it's not out of bounds
            if ((k * 2 + 1) > N && k * 2 <= N) 
            {
                h[k] = h[k * 2];
                k = k * 2;
                //Last swap has been done -> Break to not try to check h[k * 2 + 1], as it will break
                break;
            }//end if

            //Need this, as if we don't have it at the bottom, when we re-enter the while it will break.
            if (k * 2 > N) 
            {
                break;
            }
        }//end while
        //Assign the final element
        h[k] = v;
    }//end while


    public Edge remove() {
        Edge temp = h[1];
        h[1] = h[N--];
        siftDown(1);
        return temp;
    }

    public void display() 
    {
       System.out.println("\n\nThe tree structure of the heaps is:");
       h[1].showWeight();
       for(int i = 1; i<= N/2; i = i * 2) 
       {
          for(int j = 2*i; j < 4*i && j <= N; ++j)
             h[j].showWeightWOSpace(); 
           System.out.println();
       }
    }

    public boolean isEmpty()
    {
      //Gets a big number at the end as a[0] is the biggest no.
        return (N == 0);
    }
}

/****************************************************
*
*       UnionFind partition to support union-find operations
*       Implemented simply using Discrete Set Trees
*
*****************************************************/

class UnionFindSets
{
    private int[] treeParent;
    private int N;
    
    public UnionFindSets( int V)
    {
        N = V;
        treeParent = new int[V+1];
        //Sooo, make sets for each vertex. ( ie {A}, {B}, {C}, {D}, {E}, {F}, {G}) and have them point to themselves )
                            //Represented as 1 is A, 2 is B... etc
        for(int i = 1; i < treeParent.length - 1; i++)
        {
            treeParent[i] = i;
        }
    }

    public int findSet( int vertex)
    {   
        //Initally each element points to itself, then will point to each other, until the minimum spanning tree is created.
        if (treeParent[vertex] == vertex) 
        {
            return vertex;
        }
        return findSet(treeParent[vertex]);
    }
    
    //Combines two sets
    public void union( int set1, int set2)
    {
        int sx = findSet(set1);
        int sy = findSet(set2);
        //Let the element from origin point to the destination
        treeParent[sx] = sy;
    }
    
    public void showTrees()
    {
        int i;
        for(i=1; i<=N; ++i)
            System.out.print(toChar(i) + "->" + toChar(treeParent[i]) + "  " );
        System.out.print("\n");
    }
    
    public void showSets()
    {
        int u, root;
        int[] shown = new int[N+1];
        for (u=1; u<=N; ++u)
        {   
            root = findSet(u);
            if(shown[root] != 1) {
                showSet(root);
                shown[root] = 1;
            }            
        }   
        System.out.print("\n");
    }

    private void showSet(int root)
    {
        int v;
        System.out.print("Set{");
        for(v=1; v<=N; ++v)
            if(findSet(v) == root)
                System.out.print(toChar(v) + " ");
        System.out.print("}  ");
    
    }
    
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }
}

class Graph 
{ 
    //Number of vertices and edges
    private int V, E;
    private Edge[] edge;
    private Edge[] mst;        

    public Graph(String graphFile) throws IOException
    {
        int u, v;
        int w, e;

        FileReader fr = new FileReader(graphFile);
		BufferedReader reader = new BufferedReader(fr);
	           
        String splits = " +";  // multiple whitespace as delimiter
		String line = reader.readLine(); 
        //split is a handy way of spliting substrings into components using a delimter       
        String[] parts = line.split(splits);
        System.out.println("Parts[] = " + parts[0] + " " + parts[1]);
        
        //We read it from the file, so with parseInt we convert it to an integer
        V = Integer.parseInt(parts[0]);
        E = Integer.parseInt(parts[1]);
        
        // create edge array
        edge = new Edge[E+1];   
        
        // read the edges
        System.out.println("Reading edges from text file");
        for(e = 1; e <= E; ++e)
        {
            line = reader.readLine();
            parts = line.split(splits);
            u = Integer.parseInt(parts[0]);
            v = Integer.parseInt(parts[1]); 
            w = Integer.parseInt(parts[2]);
            
            System.out.println("Edge " + toChar(u) + "--(" + w + ")--" + toChar(v));                         
             
            // create Edge object, for each edge we are storing its origin, "destination" and weight
            edge[e] = new Edge(u, v, w);
            //reference with edge[e].show();
        }        
    }


    /**********************************************************
    *
    *       Kruskal's minimum spanning tree algorithm
    *
    **********************************************************/
    public void MST_Kruskal() 
    {
        int mst_weight = 0, ei = 0, edge_counter = 0;
        Edge e, uSet, vSet;
        
        // create edge array to store MST
        // Initially it has no edges.
        mst = new Edge[V-1];

        // priority queue for indices of array of edges
        Heap h = new Heap(E, edge);
        h.display();

        //Test code to make sure that the heap returns the minimum values

        //System.out.println("\n" + "Displaying the elements in order from smallest weight: ");
        // while(!h.isEmpty())
        // {
        //     e = h.remove();
        //     //h.display(); //(displays heap as each element is removed)
        //     e.show();
        // }


        //create partition of singleton sets for the vertices 
        UnionFindSets partition = new UnionFindSets(V);

        //Over here we return the minimum edge, check to see in which set x exists ( find_Set(x) ) ? and if it does we apply a union, then add it to the tree

        //Break loop once reach no. of vertices or if we pass the number of edges
        while(mst.length < V - 1 || edge_counter < E - 1)
        {
            //Gets the edge
            e = h.remove();

            //If this set is not equal to that set, then we can join the sets and add the to the minimum spanning tree
            if(partition.findSet(e.getOrigin()) != partition.findSet(e.getDest()))
            {
                //Each time called - 1 set created, 2 sets destroyed
                partition.union(e.getOrigin(), e.getDest());
                mst_weight += e.getWeight();
                mst[ei] = e;
                ei++;
            }
            //Increase the index if the edge
            edge_counter++;
        }
        System.out.println("Minimum weight of the spanning tree is: " + mst_weight);
    }


    // convert vertex into char for pretty printing
    private char toChar(int u)
    {  
        return (char)(u + 64);
    }

    public void showMST()
    {
        System.out.print("\nMinimum spanning tree build from following edges:\n");
        for(int e = 0; e < V-1; ++e) {
            mst[e].show(); 
        }
        System.out.println();
    }

} // end of Graph class
    
    // test code
class KruskalTrees {
    public static void main(String[] args) throws IOException
    {
        String fname = "wGraph3.txt";
        //System.out.print("\nInput name of file with graph definition: ");
        //fname = Console.ReadLine();

        Graph g = new Graph(fname);

        g.MST_Kruskal();

        g.showMST();
        
    }
}    


