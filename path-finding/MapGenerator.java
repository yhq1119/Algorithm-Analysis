

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class generates map files
 * with the given parameters.
 */

public class MapGenerator {

    private List<int[]> origins;
    private List<int[]> destinations;
    private List<int[]> impassable;
    private List<int[]> terrains;
    private List<int[]> wayPoints;

    private int sizeR;
    private int sizeC;
    private int NumOfOrigin;
    private int NumOfDestination;
    private int NamOfImpassable;
    private int NumOfTerrains;
    private int NumOfWayPoints;

    private MapGenerator() {

        impassable = new ArrayList<>();
        origins = new ArrayList<>();
        destinations = new ArrayList<>();
        terrains = new ArrayList<>();
        wayPoints = new ArrayList<>();
        sizeR = 10; // size of map rows
        sizeC = 10; // size of map columns
        NumOfOrigin = 1; // number of origins
        NumOfDestination = 1; // number of destinations
        NamOfImpassable = 33; // number of impassable blocks
        NumOfTerrains = 10; // number of coordinates with cost >= 1
        NumOfWayPoints = 7; // number of way points

    }

    public static void main(String[] args) {
        new MapGenerator().run();
    }

    private void run() {

        List<int[]> nodes = new ArrayList<>();

        for (int i = 0; i < sizeR; i++) {
            for (int j = 0; j < sizeC; j++) {
                int[] temp = {i, j};
                nodes.add(temp);
            }
        }

        gene(NumOfOrigin, origins, nodes);
        gene(NumOfDestination, destinations, nodes);
        gene(NamOfImpassable, impassable, nodes);
        gene(NumOfTerrains, terrains, nodes);
        gene(NumOfWayPoints, wayPoints, nodes);

        System.out.println("Origins coordinates");
        print(origins);
        System.out.println("Destinations coordinates");
        print(destinations);
        System.out.println("Impassable coordinates");
        print(impassable);
        System.out.println("Terrains coordinates");
        print(terrains);
        System.out.println("WayPoints coordinates");
        print(wayPoints);
        write();

    }

    private void print(List<int[]> list) {
        StringBuilder temp = new StringBuilder("(");
        for (int[] ints : list) {
            for (int i : ints) {
                temp.append(i).append(",");
            }
            temp.append(")");
            System.out.println(temp);
            temp = new StringBuilder("(");
        }
    }

    private void gene(int k, List<int[]> add, List<int[]> remove) {
        while (k > 0) {
            int s = new Random().nextInt(remove.size());
            add.add(remove.get(s));
            remove.remove(s);
            k--;
        }
    }

    private void write() {

        File example = new File("geneExample3.para");
        File terrain = new File("geneTerrain3.para");
        File wayPoint = new File("geneWayPoints3.para");

        try {

            FileWriter fw1 = new FileWriter(example);
            FileWriter fw2 = new FileWriter(terrain);
            FileWriter fw3 = new FileWriter(wayPoint);

            fw1.write(sizeR + " " + sizeC + "\n");
            toFile1(origins, fw1);
            toFile1(destinations, fw1);
            toFile2(impassable, fw1);
            toFile2(wayPoints, fw3);
            toFile3(terrains, fw2);

            fw1.flush();
            fw2.flush();
            fw3.flush();

        } catch (IOException ignored) {
        }
    }

    private void toFile1(List<int[]> list, FileWriter fw) throws IOException {
        StringBuilder content = new StringBuilder();
        for (int[] ints : list) {
            for (int i : ints) {
                content.append(i).append(" ");
            }
        }
        fw.write(content + "\n");
    }

    private void toFile2(List<int[]> list, FileWriter fw) throws IOException {

        for (int[] ints : list) {
            StringBuilder content = new StringBuilder();
            for (int i : ints) {
                content.append(i).append(" ");
            }
            fw.write(content + "\n");
        }

    }

    private void toFile3(List<int[]> list, FileWriter fw) throws IOException {

        StringBuilder content = new StringBuilder();

        for (int[] ints : list) {
            for (int i : ints) {
                content.append(i).append(" ");
            }
            int weight = 1 + new Random().nextInt(30);
            fw.write(content.toString() + weight + "\n");
            content = new StringBuilder();
        }
    }
}
