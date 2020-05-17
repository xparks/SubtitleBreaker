import java.io.*;
import java.util.*;

public class SubtitleBreaker {

public static final Integer max = 40;
public static final Integer min = 25;

public static void main(String args[]) {        
    try {
        //inputs
        System.out.println("Reading input file from : " + new File("input.txt").getAbsolutePath());
        File input = new File("input.txt");
        System.out.println("Writing output file to : " + new File("input.txt").getAbsolutePath()); //don't mind that this is input.

        //establish outputs.
        File output = new File("output.txt");
        Scanner sc = new Scanner(input);
        PrintWriter printer = new PrintWriter(output);
        //iterate through
        while(sc.hasNextLine()) {
            String s = sc.nextLine();
            List<String> splitLine = splitLine(s);
            //rebalance lines
            splitLine = rebalanceLines(splitLine);
            for (String substring : splitLine) {
                System.out.println("substring to write = " + substring);
                printer.write(substring+"\n");
            }
        }
        printer.flush();
    }
    catch(FileNotFoundException e) {
        System.err.println("File not found. Please scan in new file.");
    }
}

/**
As a user, I want to split a given rawLine from a file into n number of roughly equal lines.
*/
public static List<String> splitLine(String rawLine) {
    //figure out what the content of the line is and split it based upon spaces.
    List<String> words = Arrays.asList(rawLine.split(" ")); //want every line to be broken into individual "words"
    List<String> lines = new ArrayList<String>(); //want to save all lines and then return the lines for the main method to append to the output file.

    //iterate through the words and figure out whether or not they comfortably fit within the tolerance of the words.
    StringBuilder currentLineCandidate = new StringBuilder();
    StringBuilder previousLineCandidate = new StringBuilder();

    System.out.println("words in line = " + words.size());
    for (String word : words) {
        System.out.println("CurrentCandidate = " + currentLineCandidate.toString());
        
        //rollover check.
        if (currentLineCandidate.length() + word.length() > max) { //won't be fussy about spaces.
            if (previousLineCandidate.length() > 0) {
                lines.add(previousLineCandidate.toString());
            }
            previousLineCandidate = currentLineCandidate;
            currentLineCandidate = new StringBuilder();
        }

        //word needs a space before it if it's not the first word in the line
        word = (currentLineCandidate.length() == 0) ? word : " " + word;
        currentLineCandidate.append(word);            
    }
    lines.add(previousLineCandidate.toString());
    lines.add(currentLineCandidate.toString());
    return lines;
}

//greedy and just steals the last item.
public static List<String> rebalanceLines(List<String> splitLines) {
    //fail fast.  This is stupid if I've only got one line.
    String lastSubtitle = splitLines.get(splitLines.size() - 1);
    if (splitLines.size() == 1) {
        System.out.println("No worries rebalancing the lines.  There is only one line.");
        return splitLines;
    } else if (lastSubtitle.length() >= min) {
        System.out.println("No worries rebalancing the lines.  The last subtitle looks great.");
        return splitLines;
    }

    System.out.println("Looks like I've got to rebalance this string: " + lastSubtitle);
    String penultimateSubtitle = splitLines.get(splitLines.size() -2);
    //keep rebalancing until the last subtitle's size is greater than the minimum or the penultimate subtitle is less than the minimum and I haven't run out of characters.
    while (lastSubtitle.length() < min && penultimateSubtitle.length() > min) {
        //get the penultimate subtitle, which should exist because the splitLines array has more than one element.
        //find the index of the last psace of the penultimate string.
        Integer lastIndexOfSpace = penultimateSubtitle.lastIndexOf(" ") + 1;
        System.out.println("found the characters after the last mention of a space at the index " + lastIndexOfSpace + " in the last splitline: " + penultimateSubtitle.substring(lastIndexOfSpace));
        lastSubtitle = penultimateSubtitle.substring(lastIndexOfSpace) + " " + lastSubtitle;
        penultimateSubtitle = penultimateSubtitle.substring(0, lastIndexOfSpace - 1);
        System.out.println("penultimateSubtitle = " + penultimateSubtitle + " and the lastSubtitle = " + lastSubtitle);
    }
    splitLines.set(splitLines.size() -2, penultimateSubtitle);
    splitLines.set(splitLines.size() -1, lastSubtitle);
    return splitLines;
}

}
