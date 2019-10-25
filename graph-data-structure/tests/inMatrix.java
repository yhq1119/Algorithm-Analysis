package tests;

import java.util.HashMap;
import java.util.Map;

public class inMatrix {

    int vertexLenth;
    int edges;

    Vertex[] graph;

    Map<String,Integer> vertices = new HashMap<>();

    public inMatrix() {


        graph = new Vertex[0];


    }


    void addVertex(String vLabel){
        if (vertices.containsKey(vLabel)){
            return;
        }
        vertices.put(vLabel,vertexLenth);
        vertexLenth++;
    }




        public class Vertex {

            int index;
            Vertex prev;
            Vertex next;
            int weight;

            public Vertex() {

                index = 0;
                prev = null;
                next = null;
            }

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public Vertex getPrev() {
                return prev;
            }

            public void setPrev(Vertex prev) {
                this.prev = prev;
            }

            public Vertex getNext() {
                return next;
            }

            public void setNext(Vertex next) {
                this.next = next;
            }

            public int getWeight() {
                return weight;
            }

            public void setWeight(int weight) {
                this.weight = weight;
            }

            public void add(Vertex vertex){

                next = vertex;
                next.setIndex(index+1);
            }

        }



}
