import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SubtitleUtil {
    public static final Integer max = 40;
    public static final Integer min = 25;
    public static final String DEFAULT_FILE_NAME = "input.txt";
    private String inputFriendlyFileName;
    private String inputFileName;
    private String outputFileName;
    private String cleanInputFileName;

    public String getInputFriendlyFileName() {
        return inputFriendlyFileName;
    }

    public void setInputFriendlyFileName(String inputFriendlyFileName) {
        this.inputFriendlyFileName = inputFriendlyFileName;
    }

    public String getInputFileName() {
        return inputFileName;
    }

    public void setInputFileName(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public String getCleanInputFileName() {
        return cleanInputFileName;
    }

    public void setCleanInputFileName(String cleanInputFileName) {
        this.cleanInputFileName = cleanInputFileName;
    }

    /**
     * Does some basic string parsing and fiddling to get the different file names.
     * @param fileName
     */
    public SubtitleUtil(String fileName) {
        //need to be safe.
        if (fileName == null) {
            fileName = DEFAULT_FILE_NAME;
        }
        this.setInputFileName(fileName);
        List<String> fileNameComponents = Arrays.asList(fileName.split("\\.", 0));
        if (fileNameComponents != null && !fileNameComponents.isEmpty()) {
            //always get the first value.
            //never null
            this.setInputFriendlyFileName(fileNameComponents.get(0));
            this.setOutputFileName(this.getInputFriendlyFileName() + "_output.txt");
        }
    }

    public boolean fvaFileNames() {
        if (this.getInputFriendlyFileName() == null) {
            System.out.println("InputFriendlyFileName cannot be null.");
            return false;
        } else if (this.getInputFileName() == null) {
            System.out.println("InputFileName cannot be null.");
            return false;
        } else if (this.getOutputFileName() == null) {
            System.out.println("OutputFileName cannot be null.");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Reads the input file and then splits each of the lines based upon punctuation and outputs it to the clean file.
     * @return
     */
    public void cleanupInputFile() throws Exception{
        if (!fvaFileNames()) {
            throw new Exception("Cannot clean up input file due to missing names.");
        }
        this.setCleanInputFileName(this.getInputFriendlyFileName() + "_clean.txt");
        //open up the file.
        try {
            File input = new File(this.getInputFileName());
            File output = new File(this.getCleanInputFileName());
            Scanner sc = new Scanner(input);
            PrintWriter printer = new PrintWriter(output);
            System.out.println("Writing output file to : " + new File(this.getCleanInputFileName()).getAbsolutePath());

            //iterate through
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                //regex: [\\.,\\!;?:\"]
                //need to scrub the line and then split each of the components.
                List<String> paragraph = Arrays.asList(s.split("(?<=\\.)|(?<=\\!)|(?<=\\?)"));
                for (String sentence : paragraph) {
                    System.out.println("sentence to write = " + sentence.trim());
                    printer.write(sentence.trim() + "\n");

                }
            }
            printer.flush();
        } catch(FileNotFoundException e) {
                System.err.println("File not found. Please scan in new file.");
        }
    }

    /**
     * Opens the input file
     * @return
     */
    public void splitAndRebalanceFile() throws Exception{
        if (!fvaFileNames()) {
            throw new Exception("Cannot split and rebalance input file due to missing names.");
        }

        //setup files
        //use the clean file if you have one.
        String inputFileName = (this.getCleanInputFileName() != null) ? this.getCleanInputFileName() : this.getInputFileName();
        File input = new File(inputFileName);
        File output = new File(this.getOutputFileName());
        System.out.println("Reading input file from : " + new File(inputFileName).getAbsolutePath());
        System.out.println("Outputting output file from : " + new File(this.getOutputFileName()).getAbsolutePath());

        try {
            Scanner sc = new Scanner(input);
            PrintWriter printer = new PrintWriter(output);
            //iterate through the lines in the clean file and give it a go.
            while(sc.hasNextLine()) {
                String s = sc.nextLine();
                //need to scrub the line and then split each of the components.
                List<String> splitLine = this.splitLine(s);
                //rebalance after splitting the line.
                splitLine = this.rebalanceLines(splitLine);
                //write
                for (String substring : splitLine) {
                    System.out.println("substring to write = " + substring);
                    printer.write(substring+"\n");
                }
            }

            printer.flush();
        } catch(FileNotFoundException e) {
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
            //if the current line's length plus the new word would put it over the max, don't add it.
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
