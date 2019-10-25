import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class SimpleDataGenerator {


    public static void main(String[] args) {
        int x = 1;
        int numVertices = 0, numEdges;
        int minWeight = 0;
        int maxWeight = 0;
        Random random = new Random();
        Scanner sc = new Scanner(System.in);
        boolean ok = false;
        while (!ok) {
            try {

                System.out.println("PLEASE ENTER NUMBER OF[VERTICES] [MIN WEIGHT] [MAX WEIGHT] [DENSITY DIVIDER]:");

                numVertices = sc.nextInt();
                minWeight = sc.nextInt();
                maxWeight = sc.nextInt();
                x = sc.nextInt();
                x = 1 / x;
                ok = (maxWeight - 1 > minWeight);
            } catch (Exception e) {
                System.out.println("Error input.");
            }
        }


        numEdges = numVertices * (numVertices - 1);


        double density = (double) numEdges / (numVertices * numVertices);

        numEdges = numEdges * x;
        ArrayList<int[]> highD = new ArrayList<>();
        int[] temp = new int[3];
        int[] v1 = vertices(numVertices);
        int[] v2 = Arrays.copyOf(v1, v1.length);

        System.out.println("Possible highest Density=" + density);


        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (v1[i] != v2[j]) {
                    temp[0] = v1[i];
                    temp[1] = v2[j];
                    temp[2] = minWeight + random.nextInt(maxWeight - minWeight + 1);
                    highD.add(temp);
                    temp = new int[3];
                }
            }
        }

        int mD = numEdges  / 10; // medium density edges.
        int lD = numEdges / 100; // low density edges.

        ArrayList<int[]> mediumD = pick(highD, mD, random);


        ArrayList<int[]> lowD = pick(highD, lD, random);

        ArrayList<int[]> hd2 = pick(highD, numEdges, random);

        String h = "H_density";
        String m = "M_density";
        String l = "L_density";

        writeCSV(h, h, hd2, v1);
        writeCSV(m, m, mediumD, v1);
        writeCSV(l, l, lowD, v1);

        System.out.println("High density:"+ density(numVertices, numEdges) + " , Medium density: " + density(numVertices, mD) + ", Low Density:" + density(numVertices, lD));



    }

    private static double density(int Vs, int Es) {
        return (double) Es / (double) (Vs * Vs);
    }

    private static void writeCSV(String outputFilename, String outputScriptName, ArrayList<int[]> arrayList, int[] vertices) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(outputFilename + ".csv"));
            PrintWriter pw2 = new PrintWriter(new FileWriter(outputScriptName + "_RV.in"));
            PrintWriter pw3 = new PrintWriter(new FileWriter(outputScriptName + "_N.in"));
            PrintWriter pw4 = new PrintWriter(new FileWriter(outputScriptName + "_U.in"));


            int i = 0;
            for (int k : vertices) {
                String temp = "AV " + k + "\n";

                pw2.write(temp);
                pw3.write(temp);
                pw4.write(temp);
            }


            for (int[] ints : arrayList) {

                String temp = "AE " + ints[0] + " " + ints[1] + " " + ints[2] + "\n";

                pw2.write(temp);
                pw3.write(temp);
                pw4.write(temp);
                pw.write(ints[0] + "," + ints[1] + "," + ints[2]);
                pw.write("");
                i++;

            }
            
            int size = arrayList.size();
            ArrayList<int[]> sub = new ArrayList<>();
            for (int k=0;k<size;k++){
                if (k%2==0){
                    sub.add(arrayList.get(k));
                }
            }



            for (int[] v1 : sub){


                pw2.write("RV "+v1[0]+"\n");
                pw3.write( "ON 5 "+v1[0]+"\n"+
                                   "IN 5 "+v1[0]+"\n");

                pw4.write("U "+v1[0]+" "+v1[1]+" 3\n"
                        +"U "+v1[0]+" "+v1[1]+" 0\n");


            }
            
            

            
            



            pw.close();
            System.out.println("File "+outputFilename + ".csv created.");
            pw2.close();
            System.out.println("File "+outputScriptName + "_RV.in created.");
            pw3.close();
            System.out.println("File "+outputScriptName + "_N.in created.");
            pw4.close();
            System.out.println("File "+outputScriptName + "_U.in created.");
            System.out.println(i + " items created.");
            System.out.println();

        } catch (FileNotFoundException ex) {
            //	System.err.println("File " + args[1] + " not found.");
        } catch (IOException ex) {
            //	System.err.println("Cannot open file " + args[1]);
        }
    }

    @SuppressWarnings({"unuse", "unchecked"})
    private static ArrayList<int[]> pick(ArrayList<int[]> list, int edges, Random random) {

        ArrayList<int[]> afterList = new ArrayList<>();
        ArrayList<int[]> tempo;
        tempo = (ArrayList<int[]>) list.clone();
        for (int i = 0; i < edges; i++) {
            int k = random.nextInt(tempo.size());
            afterList.add(tempo.get(k));
            tempo.remove(k);

        }
        return afterList;
    }

    private static int[] vertices(int num) {
        int[] ints = new int[num];
        for (int i = 0; i < num; i++) {
            ints[i] = i + 1;
        }
        return ints;
    }

    private static void print(ArrayList<int[]> ints) {
        for (int[] ints1 : ints) {
            System.out.println(ints1[0] + "," + ints1[1] + "," + ints1[2]);
        }
        System.out.println("Total " + ints.size() + " items.");
    }

    private static void print(int[] ints) {
        for (int i : ints) {
            System.out.println(i);
        }
        System.out.println("Total " + ints.length + " items.");
    }

    private static void printTotalNum(ArrayList<int[]> arrayList) {
        System.out.println("Total " + arrayList.size() + " items.");
    }


}
