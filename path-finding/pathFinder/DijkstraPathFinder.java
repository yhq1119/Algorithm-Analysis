package pathFinder;

import map.Coordinate;
import map.PathMap;

import java.util.*;

/**
 * This class uses Dijkstra Algorithm
 * to find out the shortest path between grid coordinates.
 *
 * @author Haoqian Yang
 */

public class DijkstraPathFinder implements PathFinder {

    // List of origin cells/coordinates
    private List<Coordinate> originCells;
    // list of destination cells/coordinates
    private List<Coordinate> destCells;
    // list of waypoint cells/coordinates
    private List<Coordinate> waypointCells;
    // the entire map
    private PathMap map;
    // count the explored coordinates;
    private static List<Coordinate> explored;
    // record if the path finding succeeds
    private static boolean success;
    // record all possible sequences of index
    private List<int[]> allOrderSorts = new ArrayList<>();

    //constructor
    public DijkstraPathFinder(PathMap map) {

        this.map = map;
        this.originCells = map.originCells;
        this.destCells = map.destCells;
        this.waypointCells = map.waypointCells;
        explored = new ArrayList<>();


    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        //print brief information of the given map
        printInfo();
        List<Coordinate> path = new ArrayList<>();
        if (waypointCells.size() < 1) {
            findPath1(path);
        } else {
            findPath2(path);
        }
        if (!success) {
            // if the result is not correct, return an empty list.
            path = new ArrayList<>();
        }
        return trim(path);
    } // end of findPath()


    @Override
    public int coordinatesExplored() {
        // TODO: Implement (optional)

        // placeholder
        return 0;
    } // end of cellsExplored()


    /*
     ************************************
     *   below are modified contents    *
     ************************************
     */


    /**
     * For display information about the list.
     */

    private void printInfo() {
        for (Coordinate c1 : originCells) {
            System.out.println("Origin: " + c1.toString());
        }
        for (Coordinate c2 : destCells) {
            System.out.println("destination: " + c2.toString());
        }
        for (Coordinate c3 : waypointCells) {
            System.out.println("way point: " + c3.toString());
        }
    }

    /**
     * This method is used for task A, B, C.
     *
     * It calculates all the mixture of given
     * origin coordinates and destination coordinates.
     * Finds out all mixture's shortest path and record the total cost.
     * Finds out the minimum cost of all paths and add its shortest path
     * to the path list.
     *
     * @param path the passing in List of Coordinates.
     *             The path coordinates would be added to it.
     */

    private void findPath1(List<Coordinate> path) {

        // records the start coordinate in a specific sequence.
        ArrayList<Coordinate> starts = new ArrayList<>();
        // records the end coordinate in a specific sequence.
        ArrayList<Coordinate> ends = new ArrayList<>();
        // records the total cost of the path in a specific sequence.
        ArrayList<Integer> cost = new ArrayList<>();

        for (Coordinate o1 : originCells) {
            for (Coordinate d1 : destCells) {
                Graph graph = new Graph(getEdge(map));
                graph.dijkstra(o1);
                graph.printPath(d1);
                starts.add(o1);
                ends.add(d1);
                cost.add(graph.getPathCost(d1));
            }
        }
        int index = getMinIndex(cost);

        Graph graph = new Graph(getEdge(map));
        graph.dijkstra(starts.get(index));
        graph.printPath(ends.get(index));
        for (Graph.Node node : graph.getPathReference()) {
            path.add(node.coordinate);
        }
        setSuccess(path);
    }

    /**
     * Trim down the none-movement coordinates
     * (e.g. two coordinates in the path
     * which next to each other but they are
     * same coordinate)
     * in the shortest path.
     *
     * @param list the list that needs to be trimmed.
     * @return trimmed list.
     */

    private List<Coordinate> trim(List<Coordinate> list) {

        List<Coordinate> temp = new ArrayList<>();
        if (list.size() == 0) {
            return temp;
        }
        temp.add(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i - 1).equals(list.get(i))) {
                continue;
            }
            temp.add(list.get(i));
        }
        return temp;
    }

    /**
     * find out the index of minimum value
     * in a list.
     *
     * @param cost the list that needs to be searched
     * @return the index of the list's minimum value.
     */

    private int getMinIndex(List<Integer> cost) {
        int min = cost.get(0);
        int index = 0;
        for (int i = 1; i < cost.size(); i++) {
            if (cost.get(i) < 0) {
                continue;
            }
            if (cost.get(i) < min) {
                min = cost.get(i);
                index = i;
            }
        }
        return index;
    }


    /**
     * This method is for task D.
     *
     * It first generates all possibilities of
     * the way points order and add the origin coordinate
     * to its head and the end coordinate to its tail.
     * Then it stores the order in a list
     * and calculates the order's cost and stores it in another list.
     * After all possible orders are processed, it finds out
     * the minimum order and add its shortest path to the path list.
     *
     * @param path the passing in List of Coordinates.
     *             The path coordinates would be added to it.
     */

    private void findPath2(List<Coordinate> path) {
        List<Integer> cost = new ArrayList<>(); // store the total cost of each path
        // store all possible sequences of way points
        ArrayList<List<Coordinate>> allSorts = new ArrayList<>();
        int[] index = new int[waypointCells.size()];
        for (int i = 0; i < index.length; i++) {// generate the index reference list
            index[i] = i;
        }
        permutation(index, 0, index.length - 1);
        for (Coordinate o1 : originCells) {
            for (Coordinate d1 : destCells) {
                for (int[] ints1 : allOrderSorts) {
                    List<Coordinate> temp = getOneSort(ints1, waypointCells);
                    temp.add(0, o1);
                    temp.add(d1);
                    int tempCost = 0;
                    for (int i = 0; i < temp.size() - 1; i++) {
                        Graph graph = new Graph(getEdge(map));
                        Coordinate start = temp.get(i);
                        graph.dijkstra(start);
                        Coordinate end = temp.get(i + 1);
                        graph.printPath(end);
                        tempCost = tempCost + graph.getPathCost(end);
                        if (graph.getPathCost(end) == Integer.MAX_VALUE) {
                            tempCost = Integer.MAX_VALUE;
                            break;
                        }
                    }
                    cost.add(tempCost);
                    allSorts.add(temp);
                }
            }
        }
        System.out.println("All sorts now have <" + allSorts.size() + "> items.");
        List<Coordinate> best = allSorts.get(getMinIndex(cost));
        generatePath(best, path);
        setSuccess(path);
    }

    /**
     * This method generates all path coordinates
     * from a given source list and add them to
     * the path list.
     *
     * @param source the list used for generates path coordinates
     * @param path   the list used for record path coordinates
     */

    private void generatePath(List<Coordinate> source, List<Coordinate> path) {
        for (int i = 0; i < source.size() - 1; i++) {
            Graph graph = new Graph(getEdge(map));
            Coordinate start = source.get(i);
            Coordinate end = source.get(i + 1);
            graph.dijkstra(start);
            graph.printPath(end);
            for (Graph.Node node : graph.getPathReference()) {
                path.add(node.coordinate);
            }
        }
    }

    /**
     * This method checks if it finds
     * the final correct result.
     *
     * @param path the result coordinate list.
     */

    private void setSuccess(List<Coordinate> path) {
        success = false;
        boolean hasStart = false;
        boolean hasEnd = false;
        boolean hasWayPoint = true;
        for (Coordinate end : destCells) {
            if (path.contains(end)) {
                hasEnd = true;
                break;
            }
        }
        for (Coordinate start : originCells) {
            if (path.contains(start)) {
                hasStart = true;
                break;
            }
        }
        for (Coordinate way : waypointCells) {
            if (!path.contains(way)) {
                hasWayPoint = false;
            }
        }
        success = hasStart && hasEnd && hasWayPoint;
    }

    /**
     * returns a list of coordinates
     * with the given index sequence.
     *
     * @param ints the index sequence.
     * @param list the list that used as a reference.
     * @return a new list with same elements of reference list but with the ints sequence.
     */

    private List<Coordinate> getOneSort(int[] ints, List<Coordinate> list) {
        List<Coordinate> temp = new ArrayList<>();
        for (int i : ints) {
            temp.add(list.get(i));
        }
        return temp;
    }


    /**
     * generate all possible sequences
     * of a given sequence.
     *
     * @param nums  the given sequence
     * @param start the start digit of nums.
     * @param end   the end digit of nums.
     */
    private void permutation(int[] nums, int start, int end) {
        if (start == end) { //
            int[] newNums = new int[nums.length]; //
            if (end + 1 >= 0) System.arraycopy(
                    nums, 0, newNums, 0, end + 1);
            allOrderSorts.add(newNums); //
        } else {
            for (int i = start; i <= end; i++) {
                int temp = nums[start]; //
                nums[start] = nums[i];
                nums[i] = temp;
                permutation(nums, start + 1, end); //
                nums[i] = nums[start]; //
                nums[start] = temp;
            }
        }
    }

    /**
     * This method is to create all connected edges of
     * the given grid.
     *
     * @param map the PathMap that needs to be transferred
     * @return a list of Edges;
     */

    private List<Graph.Edge> getEdge(PathMap map) {
        // record the visited coordinates
        List<Coordinate> visited = new ArrayList<>();
        // get all coordinates from the map
        List<Coordinate> allCoordinates = map.getCoordinates();
        // for record all generated edges
        List<Graph.Edge> edges = new ArrayList<>();


        while (visited.size() <= allCoordinates.size() - 1) {
            for (Coordinate temp : allCoordinates) {

                if (visited.contains(temp)) {
                    continue;
                }
                visited.add(temp);
                List<Coordinate> neighbors = map.neighbours(temp);
                for (Coordinate tempNeighbour : neighbors) {
                    edges.add(new Graph.Edge(temp, tempNeighbour, tempNeighbour.getTerrainCost()));
                }
            }
        }
        // trim impassable coordinates
        List<Graph.Edge> fEdges = new ArrayList<>();
        for (Graph.Edge dd : edges) {
            if (dd.startNode.getImpassable() || dd.endNode.getImpassable()) {
                continue;
            }
            fEdges.add(dd);
        }
        return fEdges;
    }

    /**
     * This is an inner class to transfer information
     * of the given grid representation into the form
     * that Dijkstra Algorithm can handle.
     */

    static class Graph {
        // mapping of Coordinate to Node, built from a set of Edges
        private final Map<Coordinate, Node> graph = new HashMap<>();
        // record the entire path vertices
        private List<Node> graphPathRef = new ArrayList<>();


        /*
         * Builds a graph from a list of edges
         */
        Graph(List<Edge> edges) {

            //one pass to find all vertices
            // this step avoid add isolated coordinates to the graph
            for (Edge e : edges) {
                if (!graph.containsKey(e.startNode)) {
                    graph.put(e.startNode, new Node(e.startNode));
                }
                if (!graph.containsKey(e.endNode)) {
                    graph.put(e.endNode, new Node(e.endNode));
                }
            }

            //another pass to set neighbouring vertices
            for (Edge e : edges) {
                graph.get(e.startNode).neighbours.put(graph.get(e.endNode), e.weight);
                //graph.get(e.v2).neighbours.put(graph.get(e.v1), e.dist); // also do this for an undirected graph
            }
        }

        /*
         * reverse and return the recorded path
         * of vertices.
         *
         * @return a list of vertex for trace back use.
         */

        List<Node> getPathReference() {
            Collections.reverse(graphPathRef);

            return graphPathRef;
        }

        /*
         * this inner class used for generate Edges of the map.
         */
        static class Edge {
            //the coordinate of the start of the edge.
            Coordinate startNode;
            // the coordinate of the end of the edge.
            Coordinate endNode;
            // the length/cost of this edge.
            int weight;

            Edge(Coordinate temp,
                 Coordinate tempNeighbour,
                 int terrainCost) {
                startNode = temp;
                endNode = tempNeighbour;
                weight = terrainCost;
            }
        }

        /*
         * This inner class is set up for trace back the path.
         * It connects coordinates and the algorithm.
         * It is vital because it has a previous Node
         * so it is easier to trace back.
         */
        public class Node implements Comparable<Node> {

            final Coordinate coordinate;  // the contained coordinates
            int dist = Integer.MAX_VALUE; // MAX_VALUE assumed to be infinity
            Node previous = null; // vital variable for trace back
            // a link to other vertices
            final Map<Node, Integer> neighbours = new HashMap<>();
            // for display and compare use
            final String label;

            // constructor
            Node(Coordinate coordinate) {
                this.coordinate = coordinate;
                this.label = "(" + coordinate.getRow() + "," + coordinate.getColumn() + ")";
            }

            /*
             * This method traces back the path
             * and print information about this progress.
             */

            private void printPath() {
                // generate and print the shortest path.
                if (this == this.previous) {
                    graphPathRef.add(this);
                } else if (this.previous == null) {
                    System.out.print("");
                } else {
                    graphPathRef.add(this);
                    this.previous.printPath();
                }
            }

            /*
             * For compare use. The NavigableSet
             * requires this.
             *
             * @param other anther Node
             * @return the compare result
             */

            public int compareTo(Node other) {
                if (dist == other.dist)
                    return label.compareTo(other.label);
                return Integer.compare(dist, other.dist);
            }

            /*
             * Display this vertex's
             * coordinate's location info.
             *
             * @return the vertex info in string form.
             */

            @Override
            public String toString() {
                return "(" + label + ", " + dist + ")";
            }
        }


        /**
         * Runs dijkstra with the starting coordinate
         */

        void dijkstra(Coordinate startName) {
            if (!graph.containsKey(startName)) {
                //test use print statement
                //System.out.printf("Graph doesn't contain start vertex \"%s\"\n", startName);
                return;
            }
            final Node source = graph.get(startName);
            NavigableSet<Node> queue = new TreeSet<>();

            // set-up vertices
            for (Node node : graph.values()) {
                node.previous = node == source ? source : null;
                node.dist = node == source ? 0 : Integer.MAX_VALUE;
                queue.add(node);
            }
            dijkstra(queue);
        }

        /**
         * Implementation of dijkstra's algorithm using a binary heap.
         */

        private void dijkstra(final NavigableSet<Node> queue) {
            Node firstNode, neighbor;
            while (!queue.isEmpty()) {
                // vertex with shortest distance (first iteration will return source)
                firstNode = queue.pollFirst();
                assert firstNode != null;
                if (!explored.contains(firstNode.coordinate)) {
                    explored.add(firstNode.coordinate);
                }
                if (firstNode.dist == Integer.MAX_VALUE)
                    // we can ignore u (and any other remaining vertices) since they are unreachable
                    break;
                //look at distances to each neighbour
                for (Map.Entry<Node, Integer> temp : firstNode.neighbours.entrySet()) {
                    neighbor = temp.getKey(); //the neighbour in this iteration
                    if (!explored.contains(neighbor.coordinate)) {
                        explored.add(neighbor.coordinate);
                    }
                    final int alternateDist = firstNode.dist + temp.getValue();
                    if (alternateDist < neighbor.dist) { // shorter path to neighbour found
                        queue.remove(neighbor);
                        neighbor.dist = alternateDist;
                        neighbor.previous = firstNode;
                        queue.add(neighbor);
                    }
                }
            }
        }

        /**
         * Prints a path from the source to the specified vertex
         */
        void printPath(Coordinate endName) {
            if (!graph.containsKey(endName)) {
                // test use, display point
                //  System.err.printf("Graph doesn't contain end vertex \"%s\"\n", endName);
                return;
            }
            graph.get(endName).printPath();
        }

        /**
         * This method returns the total
         * cost of the path of the given
         * end coordinate end.
         *
         * @param end the given end coordinate which the path ends up with.
         * @return an integer of the total cost of the path.
         */

        int getPathCost(Coordinate end) {
            if (!graph.containsKey(end)) {
                // test use display point
                // System.err.printf("Graph doesn't contain end vertex \"%s\"\n", end);
                return Integer.MAX_VALUE;
            }
            int weight = Integer.MAX_VALUE;
            for (Coordinate key : graph.keySet()) {
                if (key.equals(end)) {
                    weight = graph.get(key).dist;
                }
            }
            return weight;
        }
    }
} // end of class DijsktraPathFinder
