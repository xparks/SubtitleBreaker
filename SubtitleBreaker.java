import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SubtitleBreaker {


    public static void main(String args[]) {
        try {
            //scan the first line.
            Scanner in = new Scanner(System.in); //first input is a new line.
            String inputFileName = in.nextLine();//
            System.out.println(inputFileName); //log this because you'll want to know what it is.

            //create new subtitle util object ot handle all of the different operations.
            SubtitleUtil subtitleUtil = new SubtitleUtil(inputFileName);

            //determine if we want to clean up the file.
            if ("Y".equalsIgnoreCase(in.nextLine())) {
                subtitleUtil.cleanupInputFile();
            }

            //split on lines and rebalance lines.
            subtitleUtil.splitAndRebalanceFile();
        } catch (Exception ex) {
            System.out.println(ex.toString()); //log this because you'll want to know what it is.
        }
    }
}
