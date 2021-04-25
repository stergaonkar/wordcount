/*
 * Author       : Shivani Sanjay Tergaonkar
 * Email ID     : stergaonkar@scu.edu
 * File Name    : WordThread.java
 * Date Created : 01/24/2021
 * Version      : v1.0
 * Description  : Implement a program to count the frequency
 *                of words in a text file. The text file is
 *                partitioned into N segments. Each segment is
 *                processed by a separate thread that outputs
 *                the intermediate frequency count for its segment.
 *                The main process waits until all the threads complete;
 *                then it computes the consolidated word-frequency
 *                data based on the individual threads' output
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WordThread implements  Callable {
    private int startIndex;
    private int endIndex;
    private String[] wordArray;

    public WordThread() { }

    public WordThread(int startIndex, int endIndex, String[] wordArray){
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.wordArray = wordArray;
    }

    // this function reads from an input file named "input.txt", and parses the file contents for words
    // i.e. excludes any special characters, and then stores these words in an array named "wordArray"
    // for further processing
    // This function also handles any file exception, throws an error, and aborts the program immediately
    private String[] ParseFile(){
        String[] wordArray={};
        try {
            BufferedReader fileRead = new BufferedReader(new FileReader("input.txt")); //
            StringBuilder stringBuilder = new StringBuilder();
            String currLine = null;
            String ls = System.getProperty("line.separator");
            while ((currLine = fileRead.readLine()) != null) {
                stringBuilder.append(currLine);
                stringBuilder.append(ls);
            }
            // delete the last new line separator
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            fileRead.close();

            String content = stringBuilder.toString();
            wordArray = content.toLowerCase().split(("\\W+")); //wordArray holds all the words from the input file in lower case.
            return wordArray;

        }
        catch (IOException e){
            System.out.println(e);
            return wordArray;
        }
    }

    // this function creates an hash map to store the word count. It then iterates over the word array, from
    // a given start index to end index, and updates the word count hash map. Every time a new word is encountered,
    // a new entry is created in the hash map with the initial count of one. If the word already exists in the hash
    // map, then the count is incremented by 1
    private  Map<String,Integer> Count() {
        HashMap<String, Integer> wordCountMap = new HashMap();

        for (int i = startIndex; i <= endIndex; i++) {
            // Get if the element is present
            Integer count = wordCountMap.get(wordArray[i]);

            // Check if its the first occurrence of element in the hash map and insert in the hash map
            if (wordCountMap.get(wordArray[i]) == null) {
                wordCountMap.put(wordArray[i], 1);
            }
            // If elements already exists in hash map; Increment the count of element by 1
            else {
                wordCountMap.put(wordArray[i], ++count);
            }


        }
            System.out.println("=========================== Thread" + Thread.currentThread().getId()+ " Summary [Start Index: " + startIndex + ", End Index: " + endIndex + "] ====================== \n" + " Word count for this thread : " + wordCountMap + "\n========================================================================================================\n\n" );
            return wordCountMap;

    }
    @Override
    public Map<String,Integer> call(){
        //Calls the processing logic and return the results
        return Count();
    }

    // This is the main function. It performs the following operations.
    // 1. First, a wordthread object is created, and the input file is processed
    //    and the words are stored in the array. Any special characters are ignored, 
    //    and all the characters are converted to lower case.
    // 2. Next, an user prompt is displayed in the output screen requesting to input
    //    the number of segments or threads. If the value entered is less than zero,
    //    or any character, then the program terminates with an error.
    // 3. After that, the value entered is valid, so the function then runs a for loop
    //    to spawn a thread. The parameters required by the thread is calculated every
    //    iteration of the loop. This to ensure that the cases where the segment size
    //    calculated is not an exact multiple of N, the workload per thread is nearly
    //    fair. The segment size calculation is therefore dynamic i.e. to say that it
    //    is calculated every iteration based on the words remaining to be assigned, 
    //    and pending threads to be spawned. 
    //    Example: Lets say the number of words (W) in the file are 425, whereas the number
    //    of threads (N) input by the user is 100. In such case, the segment size (S) will be
    //    (425/100 = 4.25). Since the segment size calculated is not a whole number, an
    //    equal segment cannot be created and assigned. This is where the dynamic 
    //    segment size calculation comes handy. For the first 25 threads, the segment size
    //    calculated will be 5 using ceil(W/N). From the 26th thread onwards, the number of words 
    //    pending to be assigned will be 300, whereas pending threads will be 75. Now, the 
    //    segment size will be 4 as 300 is completely divisible by 75. Another thing to 
    //    highlight here is that the workload for the first few threads will have bigger
    //    segment size compared to the rest. This ensures that those threads capitalize 
    //    on the thread creation overhead for the latter ones thereby ensuring minimal
    //    performance impact.
    // 4. In the penultimate step, the threads complete their tasks of calculating the 
    //    word count for the segment assigned to it. All the hashmaps returned by each
    //    of the threads are merged by the main function
    // 5. Finally, the contents of overall hash map that holds the word count for the
    //    entire file is displayed and the program completes successfully.
    public static void main(String[] args){
        try{
            int sIndex;
            int eIndex;

            WordThread wt = new WordThread();
            String[] wordArray = wt.ParseFile();

            Scanner scanner=new Scanner(System.in);
            System.out.println("Please enter the no of segments: ");
            int noOfSegments=scanner.nextInt();
            scanner.close();
            if(noOfSegments < 1){
               System.out.println(" !!! Error !!! Invalid value entered for no of segments. Please enter a value greater than 0.");

            } else{

               System.out.println("=============== Configuration =========================");
               System.out.printf(" Number of Segments or Threads (N)             : %d \n", noOfSegments);
               System.out.printf(" Total Number of words in the file (W)         : %d \n", wordArray.length);
               System.out.println("=======================================================\n\n");

               ExecutorService executorService = Executors.newFixedThreadPool(noOfSegments);

               List<Callable<Map<String, Integer>>> wordsList = new ArrayList();

               // Assign each segment as tasks to threads
               int segmentSize;
               int threadCount=noOfSegments;
               int wordsRemaining=wordArray.length;
               int endIndex=-1;
               int startIndex;
               for (int i = 0; i < noOfSegments; i++) {
                  segmentSize=(int) Math.ceil((double) wordsRemaining / threadCount);
                  startIndex=endIndex+1;
                  endIndex = startIndex + segmentSize - 1;
                  wordsRemaining -= segmentSize;
                  threadCount -= 1;
                  wordsList.add(new WordThread(startIndex, endIndex, wordArray));
               }

               // Invoke threads and waits till all threads complete execution
               List<Future<Map<String, Integer>>> allMaps = executorService.invokeAll(wordsList);//Program execution resumes beyond this point only when all threads are finished.

               executorService.shutdown();//shut down the threadpool

               // Calculate total frequency of each word by combining the word frequencies from different thread outputs
               Map<String, Integer> combinedWordMap = new HashMap<String, Integer>();

               for (Future<Map<String, Integer>> eachMap : allMaps) {
                  for (Map.Entry<String, Integer> entry : eachMap.get().entrySet()) {
                      String key = entry.getKey();
                      Integer current = combinedWordMap.get(key);
                      combinedWordMap.put(key, current == null ? entry.getValue() : entry.getValue() + current);
                  }
               }

               // Print the combined word frequency count
               int i = 0;
               System.out.println("========================= Overall Summary - Word frequency count =========================");
               for (Map.Entry<String, Integer> entry : combinedWordMap.entrySet()){
                  i++;
                  System.out.printf("%-3d. %-20s - %d \n", i, entry.getKey(), entry.getValue());
               }
               System.out.println("==========================================================================================");
           }
        }
        catch (Exception ex){
         System.out.println(ex);
        }
    }
}
