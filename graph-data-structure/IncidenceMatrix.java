import java.io.PrintWriter;
import java.util.*;


/**
 * Incident matrix implementation for the AssociationGraph interface.
 * <p>
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2019.
 * @author Haoqian Yang, s3681278, 2019.
 */
@SuppressWarnings("ALL")
public class IncidenceMatrix extends AbstractAssocGraph {

    /**
     * Contructs empty graph.
     */


    // dictionary of vertice labels and index numbers of them.
    Map<String, Integer> vertices = new HashMap<>();
    // dictionary of edge labels and index numbers of them.
    Map<String, Integer> edges = new HashMap<>();
    int[][] weights = new int[0][0];


    public IncidenceMatrix() {
        // Implement me!
    } // end of IncidentMatrix()


    /**
     * This method first check if the vertex has existed
     * in the map.
     * If not, put it into the map as well as
     * extend the 2D array by row.
     *
     * @param vertLabel Vertex to add.
     */


    public void addVertex(String vertLabel) {

        // Implement me!
        if (indexOf(vertLabel, vertices) != -1) {
            return;
        }
        vertices.put(vertLabel, vertices.size());
        if (weights.length == 0) {
            weights = new int[1][0];
        } else {
            weights = addOneRow(weights);
        }


    } // end of addVertex()

    /**
     *
     * This method firstly check if the edge has existed.
     * This step avoided more than one directed edge being
     * added between same pair of vertices.
     * Then it checks if both vertices exist.
     * If yes, then add new Edge to the map as well
     * as give weight value to the correct position.
     *
     * @param srcLabel Source vertex of edge to add.
     * @param tarLabel Target vertex of edge to add.
     * @param weight Integer weight to add between edges.
     */


    public void addEdge(String srcLabel, String tarLabel, int weight) {
        // Implement me!

        String e = srcLabel + tarLabel;
        if (indexOf(e, edges) > -1) {
            return;
        }
        int sInt = indexOf(srcLabel, vertices);
        int tInt = indexOf(tarLabel, vertices);
        if (sInt < 0 ||tInt < 0) {
            return;
        }

        int eInt = edges.size();
        edges.put(e, eInt);
        weights = addOneCol(weights);
        weights[sInt][eInt] = weight;
        weights[tInt][eInt] = weight * (-1);


    } // end of addEdge()

    /**
     * This method firstly checks if the required edge
     * exists. If yes, transfer the string value into
     * integer to get the coordinate of the needed value.
     *
     * @param srcLabel Source vertex of edge to check.
     * @param tarLabel Target vertex of edge to check.
     *
     * @return weight value if edge exists. Otherwise -1;
     */


    public int getEdgeWeight(String srcLabel, String tarLabel) {
        // Implement me!
        int eInt = indexOf(srcLabel + tarLabel, edges);
        if (eInt != -1) {
            return weights[indexOf(srcLabel, vertices)][eInt];
        }

        // update return value
        return EDGE_NOT_EXIST;
    } // end of existEdge()

    /**
     * This method firstly checks if the required edge exists.
     * If exist, modify the reqiured positon value to weight.
     * If the weight is zero, delete it from the map.
     *
     * @param srcLabel Source vertex of edge to update weight of.
     * @param tarLabel Target vertex of edge to update weight of.
     * @param weight Weight to update edge to.  If weight = 0, delete the edge.
     */


    public void updateWeightEdge(String srcLabel, String tarLabel, int weight) {
        // Implement me!

        String e = srcLabel + tarLabel;
        if (indexOf(e, edges) < 0) {
            return;
        }


        int srcInt = indexOf(srcLabel, vertices);
        int tarInt = indexOf(tarLabel, vertices);
        weights[srcInt][edges.get(e)] = weight;
        weights[tarInt][edges.get(e)] = weight * (-1);

        if (weight == 0) {
            edges.remove(e);
            return;
        }



    } // end of updateWeightEdge()

    /**
     * This method first check if the vertex exists.
     * Then it delete the vertex from the map.
     * Then remove every edge envolved with it.
     *
     * @param vertLabel Vertex to remove.
     */

    public void removeVertex(String vertLabel) {

        // Implement me!
        if (indexOf(vertLabel, vertices) <0) {
            return;
        }
        vertices.remove(vertLabel);
        Iterator<String> iterator = edges.keySet().iterator();
        while (iterator.hasNext()) {
            String k = iterator.next();
            if (k.contains(vertLabel)) {
                iterator.remove();
                edges.remove(k);
            }
        }
    }


    /**
     * This method firstly checks if the vertex exists in the map.
     * If yes, look up its index in the map and look up the weight
     * of edges that point to it in int[][] weights. Because
     * the weight value of in-nearest neighbours are negative,
     * here needs to turn it to positive. Finally, use sortAndTrim()
     * method to make it match the number of k and in a sorted order.
     *
     * @param k The number of nearest neighbours that requires.
     * @param vertLabel Vertex to find the in-neighbourhood for.
     *
     * @return a list fo MyPair. If total number of it <= k, return
     * all MyPairs.
     */

    public List<MyPair> inNearestNeighbours(int k, String vertLabel) {
        /* Return in nearest neighbours, search for the rowLength that contains vertLabel.
        And search for any weights that smaller than 0 */


        List<MyPair> neighbours = new ArrayList<MyPair>();
        if (indexOf(vertLabel, vertices) < 0) {
            return neighbours;
        }

        int index = indexOf(vertLabel, vertices);

        for (int i = 0; i < weights[index].length; i++) {
            if (weights[index][i] < 0) {
                String label = "";
                for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                    int value = entry.getValue();
                    if (value == i) {
                        label = entry.getKey().substring(0, 1);
                        break;
                    }
                }
                if (!label.isEmpty())

                    neighbours.add(new MyPair(label, -weights[index][i]));
            }
        }

        return sortAndTrim(k, neighbours);


    } // end of inNearestNeighbours()

    /**
     * similar with the out-Nearest neighbours.First, check
     * if the vertex exists in map. If yes, look up the weight of
     * the edge of it points to in the matrix. Then add it with its
     * pointing vertex label and weight.Finally, use sortAndTrim()
     * method to make it match the number of k and in a sorted order.
     *
     *
     * @param k
     * @param vertLabel Vertex to find the out-neighbourhood for.
     *
     * @return a list fo MyPair. If total number of it <= k, return
     * all MyPairs.
     */

    public List<MyPair> outNearestNeighbours(int k, String vertLabel) {
          /* Return out nearest neighbours, search for the rowLength that contains vertLabel.
        And search for any weights that larger than 0 */


        List<MyPair> neighbours = new ArrayList<MyPair>();
        if (indexOf(vertLabel, vertices) < 0) {
            return neighbours;
        }
        int index = indexOf(vertLabel, vertices);

        for (int i = 0; i < weights[index].length; i++) {
            if (weights[index][i] > 0) {
                String label = "";
                for (Map.Entry<String, Integer> entry : edges.entrySet()) {
                    int value = entry.getValue();
                    if (value == i) {
                        label = entry.getKey().substring(1, 2);
                    }
                }
                if (!label.isEmpty())
                    neighbours.add(new MyPair(label, weights[index][i]));
            }
        }

        return sortAndTrim(k, neighbours);


    } // end of outNearestNeighbours()

    /**
     * Traverse through the vertices map to
     * show every element of it.
     *
     * @param os PrinterWriter to print to.
     */


    public void printVertices(PrintWriter os) {


        // Implement me!
        String s = "";
        for (String k : vertices.keySet()) {
            s = s + k + " ";
        }
        os.println(s);


    } // end of printVertices()

    /**
     * Traverse through the entire edge map
     * to show every element of it.
     *
     * @param os PrinterWriter to print to.
     */


    public void printEdges(PrintWriter os) {


        // Implement me!
        for (String e : edges.keySet()) {
            String src = e.substring(0, 1);
            String tar = e.substring(1, 2);
            int srcInt = indexOf(e.substring(0, 1), vertices);
            int eInt = edges.get(e);
            int w = getEdgeWeight(src, tar);

            os.println(src + " " + tar + " " + w);

        }

    } // end of printEdges()


    /**
     * ********************************************************
     * <p>
     * following starts the added methods.
     * <p>
     * ********************************************************
     */

    /**
     * This method applies bubble algorithm to sort the List of
     * MyPairs, and trim to the first number of k elments.
     *
     * @param k the number of the front elemtns.
     * @param neighbours the List of MyPair that needs sorting
     *                   and triming.
     * @return the sorted and trimed list. If the length of it is
     * less than k, just return it.
     */


    /* Bubble Sort Algorithm sortAndTrim MyPair list*/
    private List<MyPair> sortAndTrim(int k, List<MyPair> neighbours) {

        for (int i = 0; i <= neighbours.size() - 2; i++) {
            for (int j = 0; j <= neighbours.size() - 2 - i; j++) {
                if (neighbours.get(j + 1).getValue() > neighbours.get(j).getValue()) {
                    Collections.swap(neighbours, j, j + 1);
                }
            }
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
     * Get the number of 2D array's rows.
     *
     * @param ints the 2D array to read from.
     * @return the array's length.
     */


    private int getRow(int[][] ints) {
        return ints.length;
    }

    /**
     * Get the number of 2D array's columns.
     *
     * @param ints the 2D array to read from.
     * @return the array's first column's length.
     * If the array's row length is 0, return 0.
     */


    private int getCol(int[][] ints) {
        if (ints.length == 0) {
            return 0;
        }
        return ints[0].length;
    }

    /**
     * This method read the length of the passing in int[][]
     * array's rows and columns then copy it to a new int[][]
     * with an extra row.
     *
     * @param ints the array that needs to be extended.
     * @return the extended array.
     */


    private int[][] addOneCol(int[][] ints) {

        int vertexLength = getRow(ints);
        int edgesLength = getCol(ints);

        int[][] temp = new int[vertexLength][edgesLength + 1];
        for (int i = 0; i < vertexLength; i++) {
            for (int j = 0; j < edgesLength; j++) {
                temp[i][j] = ints[i][j];
            }
        }
        return temp;
    }

    /**
     * This method checks the existance of the passing in string
     * value in the passing in map's keyset. If it exists,
     * read its value in the map.
     *
     * @param s The string that needs to check and read.
     * @param map The map that check string s from.
     * @return The value of map.get(s). If s does not exist, return -1;
     */

    private int indexOf(String s, Map<String, Integer> map) {

        if (map.containsKey(s)) {
            return map.get(s);
        }
        return EDGE_NOT_EXIST;
    }

    /**
     * This method read the length of the passing in int[][]
     * array's rows and columns then copy it to a new int[][]
     * with an extra column.
     *
     * @param ints the array that needs to be extended.
     * @return the extended array.
     */

    private int[][] addOneRow(int[][] ints) {


        int row = getRow(ints);
        int col = getCol(ints);
        int[][] temp = new int[row + 1][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                temp[i][j] = ints[i][j];
            }
        }

        return temp;
    }


    /**
     * This method is to update the index value
     * after deleting specific edge or vertex.
     * It follows how the index changes in arrays
     * to modify(here means minus one) the index stored in maps.
     *
     * @param indexOfDeleted the index of deleted edge or vertex.
     * @param map            the map that need to update.
     */

    public Map<String, Integer> updateIndex(int indexOfDeleted, Map<String, Integer> map) {


        for (String key : map.keySet()) {
            int s = map.get(key);
            if (s >= indexOfDeleted) {
                map.put(key, s - 1);
            }
        }

        return map;
    }




} // end of class IncidenceMatrix
