package tests;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

public class testData {
    @Test
    void test1(){
        int[] vertices1 = {1,2,3,4,5,6,7};
        int[] vertices2 = {1,2,3,4,5,6,7};
        ArrayList<String> edges = new ArrayList<>();
        Random ran = new Random();
        for (int i = 0;i<vertices1.length;i++){
            for (int j = 0;j<vertices2.length;j++){
                if (vertices1[i]!=vertices2[j]){
                    edges.add("("+vertices1[i]+","+vertices2[j]+","+ran.nextInt(100)+")");
                }
            }
        }

        print(edges);
        System.out.println(edges.size());
        System.out.println((double) edges.size()/(double) (vertices1.length*vertices2.length));



    }
    private void print(ArrayList<String> arrayList){
        for (String s:arrayList){
            System.out.println(s);
        }
    }

}
