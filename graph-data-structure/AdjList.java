import java.io.*;
import java.util.*;

/**
 * Adjacency list implementation for the AssociationGraph interface.
 * <p>
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2019.
 * @author Haoqian Yang, s3681278, 2019.
 */
public class AdjList extends AbstractAssocGraph {

    /**
     * Contructs empty graph.
     */

    long start = 0;
    int step = 0;
    long total;

    private void setStart(String s) {
        start = System.nanoTime();
    }

    private void endStart(int s) {
        step++;
        long now = System.nanoTime();
        System.out.println("cost " + (now - start) + " ns, step " + step);
        total = total + now - start;
        start = System.nanoTime();
    }

    // vertices length.
    private int vLength = 0;

    // Array of vertices of the LinkList(Node class). Here applies one additional null position.
    private Node[] vertices = new Node[vLength + 1];

    public AdjList() {
        /* Empty constructor*/
    }

    /**
     * Create a new link and add it to the vertices array.
     * Then extends the length of the vertices array.
     *
     * @param vertLabel the string value to represent vertex.
     */

    public void addVertex(String vertLabel) {
        // check if the vertex exists. Do nothing when vertex has existed.
        if (hasExisted(vertLabel)) {
            return;
        }
        Node head = new Node(vertLabel);
        head.setHeader();
        vertices[vLength] = head;
        vLength++;
        vertices = Arrays.copyOf(vertices, vLength + 1);

    } // end of addVertex()

    /**
     * Mark an edge with the start and the end vertices.
     * It firstly check if the edge already exits.
     * Then check if the labels are all valid.
     * Then local the start vertex to add next node
     * which is considered as the target vertex and
     * record the weight in the target vertex node.
     *
     * @param srcLabel Source vertex of edge to add.
     * @param tarLabel Target vertex of edge to add.
     * @param weight   Integer weight to add about the edge.
     */

    public void addEdge(String srcLabel, String tarLabel, int weight) {

        // if edge already exists, skip the adding procedure.
        if (getEdgeWeight(srcLabel, tarLabel) != -1) {
            return;
        }

        if (hasExisted(tarLabel)) {
            for (int i = 0; i < vertices.length - 1; i++) {
                if (vertices[i].getLabel().equals(srcLabel)) {
                    Node currNode = vertices[i];
                    while (currNode.getNext() != null) {
                        /* Reach to the end of the linklist */
                        currNode = currNode.getNext();
                    }
                    Node newNode = new Node(tarLabel);

                    newNode.setWeight(weight);
                    newNode.setPrev(currNode);
                    currNode.setNext(newNode);
                    break;
                }
            }
        } else {
            System.out.println("Target Vertex does not exist.");
        }

    }
    // end of addEdge()

    /**
     * This method firstly check if the labels are valid.
     * Then it locate the source vertex and the target vertex.
     * Then it read and return the weight value from the target vertex node.
     *
     * @param srcLabel Source vertex of edge to check.
     * @param tarLabel Target vertex of edge to check.
     * @return the weight value that stored in target vertex node.
     */

    public int getEdgeWeight(String srcLabel, String tarLabel) {
        /* Loop through each header. For each header loop through its childs and
         * return the weight of tarLabel child
         */


        for (int i = 0; i < vertices.length - 1; i++) {
            if (vertices[i].getLabel().equals(srcLabel)) {
                Node currNode = vertices[i];
                while (currNode != null) {
                    if (!currNode.getLabel().equals(tarLabel) || currNode.isHeader()) {
                        currNode = currNode.getNext();
                    } else {
                        break;
                    }
                }
                if (currNode != null && currNode.getLabel().equals(tarLabel)) {
                    return currNode.getWeight();
                } else {
                    return EDGE_NOT_EXIST;
                }
            }
        }


        return EDGE_NOT_EXIST;
    } // end of existEdge()

    /**
     * Traverse through the vertices, find the header that matches srcLabel.
     * Search and locate each node in the LinkList and then Update its weight.
     *
     * @param srcLabel Source vertex of edge to update weight of.
     * @param tarLabel Target vertex of edge to update weight of.
     * @param weight   Weight to update edge to.  If weight = 0, delete the edge.
     */

    public void updateWeightEdge(String srcLabel, String tarLabel, int weight) {


        for (int i = 0; i < vertices.length - 1; i++) {
            if (vertices[i].getLabel().equals(srcLabel)) {
                Node currNode = vertices[i];
                while (currNode != null) {
                    if (!currNode.getLabel().equals(tarLabel)) {
                        currNode = currNode.getNext();
                    } else {
                        break;
                    }
                }
                if (weight == 0) {
                    removeEdgesByVertex(currNode, tarLabel);
                    return;
                }

                if (currNode != null) {
                    currNode.setWeight(weight);
                } else {
                    System.out.println("Can't GET node " + tarLabel + " in " + srcLabel);
                }
                break;
            }
        }
    } // end of updateWeightEdge()

    /**
     * This method is to remove the vertex and the edges that contains it.
     * First it matches and locate the vertex in the graph
     * by matching the string value of label and index of vertices[].
     * Then it just create a size-1 vertices[] and copy the content
     * except the one wanted to be removed.
     * Last set the result to the vertices[].
     * update the length.
     *
     * @param vertLabel the lable of Vertex to remove.
     */

    public void removeVertex(String vertLabel) {
        // initialize the index of vertex to remove to a invalid value.

        int removeIndexOfVertex = -1;
        boolean hasFound = false;
        Node[] clone = vertices.clone();

        // traverse and match the index of vertex that needed to remove.
        for (int i = 0; i < vertices.length - 1; i++) {
            if (vertices[i].getLabel().equals(vertLabel)) {
                removeIndexOfVertex = i;
            }
            removeEdgesByVertex(vertices[i], vertLabel);
        }

        for (int i = 0; i < clone.length - 1; i++) {
            //copy and skip the wanted vertex to do remove action.
            if (i == removeIndexOfVertex) {
                hasFound = true;
            } else {
                if (hasFound) {
                    vertices[i - 1] = clone[i];
                } else {
                    vertices[i] = clone[i];
                }
            }
        }
        if (removeIndexOfVertex != -1) {
            vertices = Arrays.copyOf(vertices, vertices.length - 1);
            vLength--;
        }
    } // end of removeVertex()




    /**
     * Traverse through the vertices to find its neighbours.
     * Here it checks if the vertex is the head and
     * if yes then browse its neighbour vertices
     * Then add the result into a List of MyPair.
     *
     * @param k         the amount of neighbours that needed.
     * @param vertLabel a String of the vertex to find the out-neighbourhood for.
     * @return needed List of MyPair.
     */
    public List<MyPair> inNearestNeighbours(int k, String vertLabel) {


        List<MyPair> neighbours = new ArrayList<MyPair>();
        for (int i = 0; i < vertices.length - 1; i++) {
            if (!vertices[i].getLabel().equals(vertLabel)) {
                Node currNode = vertices[i];
                currNode = currNode.getNext();
                while (currNode != null) {
                    if (currNode.getLabel().equals(vertLabel)) {
                        MyPair nearestNeighbour =
                                new MyPair(vertices[i].getLabel(), currNode.getWeight());
                        neighbours.add(nearestNeighbour);
                    }
                    currNode = currNode.getNext();
                }
            }
        }

     return sortAndTrim(k, neighbours);


    } // end of inNearestNeighbours()


    /**
     * Traverse through the vertices to find its neighbours.
     * Here it checks if the vertex is the head and
     * if yes then browse its neighbour vertices
     * Then add the result into a List of MyPair.
     * Then use sortAndTrim() and trim() methods to get wanted result.
     *
     * @param k         the amount of neighbours that needed.
     * @param vertLabel a String of the vertex to find the out-neighbourhood for.
     * @return needed List of MyPair.
     */

    public List<MyPair> outNearestNeighbours(int k, String vertLabel) {

        List<MyPair> neighbours = new ArrayList<MyPair>();
        for (int i = 0; i < vertices.length - 1; i++) {
            if (vertices[i].getLabel().equals(vertLabel)) {
                Node currNode = vertices[i];
                currNode = currNode.getNext();
                while (currNode != null) {
                    MyPair nearestNeighbour =
                            new MyPair(currNode.getLabel(), currNode.getWeight());
                    neighbours.add(nearestNeighbour);
                    currNode = currNode.getNext();
                }
                break;
            }
        }
        return sortAndTrim(k, neighbours);


    }// end of outNearestNeighbours()

    /**
     * Traverse through all lists' headers(the vertices)
     * and print.
     *
     * @param os PrinterWriter to print to.
     */

    public void printVertices(PrintWriter os) {

        for (int i = 0; i < vertices.length - 1; i++) {
            os.print(vertices[i].getLabel() + " ");
        }
        os.println();

    } // end of printVertices()


    /**
     * Traverse through all vertices to read and print
     * the edge labels and weight values.
     *
     * @param os PrinterWriter to print to.
     */

    public void printEdges(PrintWriter os) {


        for (int i = 0; i < vertices.length - 1; i++) {
            Node currNode = vertices[i];
            while (currNode != null) {
                if (currNode.getWeight() != 0) {
                    os.println(
                            vertices[i].getLabel() + " "
                                    + currNode.getLabel() + " "
                                    + currNode.getWeight()
                    );
                }
                currNode = currNode.getNext();
            }
        }
    } // end of printEdges()

    /**

     *********************************************************

     following starts the added methods.

     *********************************************************

     */

    /**
     * This method is to break the remove Vertex procedure into small method.
     * Firstly, it traverse and locate the wanted vertex and directly link
     * the its previous and next node.
     *
     * @param header
     * @param vertLabel
     */

    private void removeEdgesByVertex(Node header, String vertLabel) {


        if(header==null){// skip the null node.
            return;
        }
        while (header.getNext() != null) {
            header = header.getNext();
        }
        if (header.getLabel().equals(vertLabel) && !header.isHeader()) {  // locate in header's child nodes.
            Node next = header.getNext();
            Node prev = header.getPrev();
            if (next != null) { // check if the located one is not the last node.
                next.setPrev(prev);
                prev.setNext(next);
            } else { // it is the last.
                prev.setNext(null);
            }
        }
    }

    /**
     * Traverse all vertices to find out if the label of vertex exists in graph.
     *
     * @param label the String label to testify.
     * @return true if the label exists.
     */

    private boolean hasExisted(String label) {


        boolean isValid = false;
        for (int i = 0; i < vertices.length - 1; i++) {
            if (vertices[i].getLabel().equals(label)) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }


    /**
     * Bubble sortAndTrim algorithm to sortAndTrim MyPair List.
     * It sortAndTrim larger elements to the left(beginning) of the List.
     * This method is also to cut possible larger than wanted List of
     * MyPair into wanted size of k. If its size < k, just return it.
     *
     * @param k          the amounts of MyPair that needed.
     * @param neighbours List of MyPair that need to sort and trim.
     * @return the List after sort and trim finished.
     */
    private List<MyPair> sortAndTrim(int k, List<MyPair> neighbours) {


        int i = 0;
        while (i <= neighbours.size() - 2) {
            // traverse the MyPairs
            int j = 0;
            while (j <= neighbours.size() - 2 - i) {
                // compare each pair and swap
                if (neighbours.get(j + 1).getValue() > neighbours.get(j).getValue()) {
                    Collections.swap(neighbours, j, j + 1);
                }
                j++;
            }
            i++;
        }
        if (k != -1) {
                try {
                    neighbours = neighbours.subList(0, k);
                } catch (IndexOutOfBoundsException e) {
                   return neighbours;
                }
            }
            return neighbours;
    }

    /**
     * The Node class is an internal class of AdjList class.
     * It stores data in a linkedList form.
     */


    protected class Node {

        /**
         * Statement of Node Class variables.
         * String label is for storing node(vertex) label.
         * Node prev is to store/link to its previous Node.
         * Node next is to store/link to its next Node.
         */

        private final String label;
        private Node prev;
        private Node next;
        private int weight;

        //constructor initializes some of the variables.

         Node(String value) {
            label = value;
            next = null;
            prev = null;
        }


        //getters and setters of all variables.

        String getLabel() {
            return label;
        }

        int getWeight() {
            return weight;
        }

         Node getPrev() {
            return prev;
        }

         Node getNext() {
            return next;
        }

         void setWeight(int value) {
            weight = value;
        }

         void setNext(Node next) {
            this.next = next;
        }

         void setPrev(Node prev) {
            this.prev = prev;
        }

         void setHeader() {
            this.prev = null;
        }

         boolean isHeader() {
            return prev == null;
        }
    }
} // end of class AdjList
