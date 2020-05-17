Welcome to my SubtitleBreaker.  It's a much more intense name than maybe the code deserves.

Idea: 
I have a script that is broken out into sentences and questions.
I want to have a set of lines that I can feed to YouTube so that it can auto-generate my subtitles with appropriate line breaks.
I could do this manually by hand, but that is time-consuming and horrible.
Computers should be able to do this for me quickly and easily.

Implementation Strategy:
There exists a max and min number of characters per line, but I need to add whole words to lines as opposed to arbitrary character counts.
Break each sentence into a list of words.
Keep appending the words together until they go over the max character limit.
If adding a word would cause it to go over the max, then that's a complete subtitle line and I'll add it.
Keep doing this for all of the words in a sentence.

After all of the lines have been generated, attempt to do a rebalance of the line.
Rebalancing occurs if there is more than one subtitle generated and the last subtitle for a given line has a length smaller than the min.
In that case, the rebalancer steals words from the prior line until either the length of the last subtitle is above the min or the penultimate subtitle would dip below the min.
There is likely a more elegant way of doing this, but it's probably sufficient for an initial pass.

How to use it:
1) Create a new text file in the same directory as the SubtitleBreaker with the name: input.txt
2) Populate the file with the text that you want to have it break up into subtitle-sized lines with each sentence and question on it's own line.
3) Run the following command within the folder that SubtitleBreaker lives:
```javac SubtitleBreaker.java```
which compiles the application.
4) Run the application by running the following command:
```java SubtitleBreaker.java```
*Note that the terminal that you are running this from will output a lot of log statements.  This is potentially helpful for figuring out how the application actually works and what it's doing.
5) Check the output file (output.txt) to ensure that the result is as expected.

