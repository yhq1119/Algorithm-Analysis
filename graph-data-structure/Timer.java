import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Timer {


    public static String timer(String density, String scenario, String algorithm,long start) {

        String s1 = "Density "; String s2 =""; String s3 =""; String s="";

        switch (density.toUpperCase()) {
            case "L": s=" Low ";
                break;
            case "M": s=" Medium";
                break;
            case "H": s=" High";
                break;
        }

        switch (scenario.toUpperCase()){
            case "1": s2 =" Shrinking graph ";
                break;
            case "2": s2=" Nearest Neighbours ";
                break;
            case "3": s2=" Change Association ";
                break;
        }

        switch ((algorithm).toUpperCase()){
            case "A":s3=" Adjacency List ";
                break;
            case "I":s3=" Incidence Matrix ";
                break;
        }

        String tt = s2;

        String fin= tt+" cost time "+(System.nanoTime()-start)+" ns.";
        String sss= String.valueOf(System.nanoTime()-start);
        System.out.println(sss);


        try {
            FileWriter fw = new FileWriter(new File("time_cost_record.txt"),true);


            fw.write(sss+"\n");
            fw.close();


        }catch (IOException e){
            System.out.println("Cannot record time.");
        }

        return fin;
    }

}

