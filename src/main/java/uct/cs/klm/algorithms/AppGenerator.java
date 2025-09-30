package uct.cs.klm.algorithms;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import uct.cs.klm.algorithms.generators.*;
/**
 * The App class is responsible for generating a knowledge base (KB) of defeasible implications based on user-defined parameters.
 */
public class AppGenerator 
{
    private static Connective con = Connective.getInstance();
    private static AtomBuilder gen = AtomBuilder.getInstance();
    private static int filenum = 1;
    private static String choice;

    /**
     * The main method for running the knowledge base (KB) generation program.
     *
     * @param args Command-line arguments.
     */
    public static void main( String[] args ){
        Rules r = new Rules();
        Scanner in = new Scanner(System.in);

        do{
            con.reset();
            System.out.println( "Defeasible Knowledge Base Generator:");
            List<Integer> complexityAntList = new ArrayList<>(); // Number of possible connectives in a defImplications antecedent
            List<Integer> complexityConList = new ArrayList<>(); // Number of possible connectives in a defImplications consequent
            List<Integer> connectiveList = new ArrayList<>(); // Number of different connectives a defImplication can have

            System.out.println("Enter the number of ranks in the KB:");
            System.out.print("> ");
            int numRanks = in.nextInt(); // Number of ranks in the knowledgebase (including rank 0)
            while ((numRanks <= 0)){
                System.out.println("Enter a non-negative number of ranks in the KB:");
                System.out.print("> ");
                numRanks = in.nextInt();
            }

            System.out.println("Enter the defImplication distribution [f (flat), lg (linear-growth), ld (linear-decline), r (random)]:");
            System.out.print("> ");
            String distribution = in.next(); // Distribution of the defImplications in the knowledge base
            while (!validDistribution(distribution)){
                System.out.println("Enter valid defImplication distribution [f (flat), lg (linear-growth), ld (linear-decline), r (random)]:");
                System.out.print("> ");
                distribution = in.next();
            }

            int min = minDefImplications(distribution, numRanks);
            System.out.println("Enter the number of defImplications in the KB (Must be greater than or equal to " + min + "):");
            System.out.print("> ");
            int numDefImplications = in.nextInt(); // Number of defImplications in the knowledge base
            while (!(numDefImplications >= min)){
                System.out.println("Enter a valid number of defImplications in the KB (Must be greater than or equal to " + min + "):");
                System.out.print("> ");
                numDefImplications = in.nextInt();
            }

            int[] defImplicationDistribution = Distribution.distributeDIs(numDefImplications, numRanks, distribution);

            System.out.println("Simple defImplications only? [y, n]:");
            System.out.print("> ");
            String smple = in.next(); // Knowledge base generation using only simple defImplications
            boolean simple = (smple.equalsIgnoreCase("y")) ? true : false;

            System.out.println("Reuse Consequent? [y, n]:");
            System.out.print("> ");
            String reuseAnt = in.next(); // Reuse the rankBaseConsequent to generate ranks in the knowledge base
            boolean reuseConsequent = (reuseAnt.equalsIgnoreCase("y")) ? true : false;

            if(simple == false){
                System.out.println("Antecedent complexity [0, 1, 2]:");
                System.out.println("Enter chosen numbers seperated by commas:");
                System.out.print("> ");
                String antComplexity = in.next();

                String[] antStrings = antComplexity.split(",");
                for (int i = 0; i < antStrings.length; i++){
                    int temp = Integer.parseInt(antStrings[i].trim());
                    if (temp != 0 && temp != 1 && temp != 2){
                        // Skip invalid numbers
                    }
                    else{
                        complexityAntList.add(temp);
                    }
                }

                System.out.println("Consequent complexity [0, 1, 2]:");
                System.out.println("Enter chosen numbers separated by commas:");
                System.out.print("> ");
                String conComplexity = in.next();

                String[] conStrings = conComplexity.split(",");
                for (int i = 0; i < conStrings.length; i++){
                    int temp = Integer.parseInt(conStrings[i].trim());
                    if (temp != 0 && temp != 1 && temp != 2){
                        // Skip invalid numbers
                    }
                    else{
                        complexityConList.add(temp);
                    }
                }

                System.out.println("Connective types [1, 2, 3, 4, 5]:");
                System.out.println("1 = disjuntion, 2 = conjunction, 3 = implication, 4 = bi-implication, 5 = mixture");
                System.out.println("Enter chosen numbers separated by commas:");
                System.out.print("> ");
                String connectiveTypes = in.next();

                String[] connectiveStrings = connectiveTypes.split(",");
                for (int i = 0; i < connectiveStrings.length; i++){
                    int temp = Integer.parseInt(connectiveStrings[i].trim());
                    if (temp != 1 && temp != 2 && temp != 3 && temp != 4 && temp != 5){
                        // Skip invalid numbers
                    }
                    else{
                        connectiveList.add(temp);
                    }
                }
                }

            System.out.println("Would you like to change connective symbols? [y, n]");
            System.out.print("> ");
            String chnge = in.next(); // Change the connective symbols used in the defImplications
            boolean change = (chnge.equalsIgnoreCase("y")) ? true : false;
            if(change == true){
                System.out.println("Default Defeasible Implication symbol: ~> ['s' to skip]");
                System.out.print("> ");
                String defImp = in.next();
                boolean chng = (defImp.equalsIgnoreCase("s")) ? true : false;
                if(chng == false){con.setDISymbol(defImp);} // Sets defeasible implication symbol
                
                if(simple == false){
                    System.out.println("Default Conjunction symbol: & ['s' to skip]");
                    System.out.print("> ");
                    String conj = in.next();
                    chng = (conj.equalsIgnoreCase("s")) ? true : false;
                    if(chng == false){con.setConjunctionSymbol(conj);} // Sets conjunction symbol

                    System.out.println("Default Disjunction symbol: || ['s' to skip]");
                    System.out.print("> ");
                    String disj = in.next();
                    chng = (disj.equalsIgnoreCase("s")) ? true : false;
                    if(chng == false){con.setDisjunctionSymbol(disj);}  // Sets disjunction symbol

                    System.out.println("Default Implication symbol: => ['s' to skip]");
                    System.out.print("> ");
                    String imp = in.next();
                    chng = (imp.equalsIgnoreCase("s")) ? true : false;
                    if(chng == false){con.setImplicationSymbol(imp);}  // Sets implication symbol

                    System.out.println("Default Bi-Implication symbol: <=> ['s' to skip]");
                    System.out.print("> ");
                    String biimp = in.next();
                    chng = (biimp.equalsIgnoreCase("s")) ? true : false;
                    if(chng == false){con.setBiImplicationSymbol(biimp);}  // Sets bi-implication symbol

                    System.out.println("Default Negation symbol: ! ['s' to skip]");
                    System.out.print("> ");
                    String negation = in.next();
                    chng = (negation.equalsIgnoreCase("s")) ? true : false;
                    if(chng == false){con.setNegationSymbol(negation);}  // Sets negation symbol
                }
            }
            
            System.out.println("Enter the character set for the knowledge base [lowerlatin, upperlatin, altlatin, greek]");
            System.out.println("Greek & altlatin character sets require code page 65001");
            System.out.println("Can set this in the terminal using 'chcp 65001'");
            System.out.print("> ");
            String characterSet = "lowerlatin"; // The character set used for the atoms
            while (!validCharacterSet(characterSet)){
                System.out.println("Enter valid character set [lowerlatin, upperlatin, altlatin, greek]:");
                System.out.print("> ");
                characterSet = in.next();
            }
            gen.setCharacters(characterSet);

            do{
                int[] complexityAnt = new int[complexityAntList.size()];
                for (int i = 0; i < complexityAntList.size(); i++){
                        complexityAnt[i] = complexityAntList.get(i);
                }

                int[] complexityCon = new int[complexityConList.size()];
                for (int i = 0; i < complexityConList.size(); i++){
                        complexityCon[i] = complexityConList.get(i);
                }

                int[] connectiveTypes = new int[connectiveList.size()];
                for (int i = 0; i < connectiveList.size(); i++){
                        connectiveTypes[i] = connectiveList.get(i);
                }

                if(simple == false){
                    if((complexityAnt.length == 1 & complexityCon.length == 1) & (complexityAnt[0] == 0 & complexityCon[0] == 0)){
                        simple = true;
                    }
                }

                System.out.println("Generator type? [s (standard), o (optimised)]:");
                System.out.print("> ");
                String type = in.next(); // Knowledge base generation using only simple defImplications
                System.out.println("Generating Knowledge Base:");
                LinkedHashSet<LinkedHashSet<DefImplication>> KB = new LinkedHashSet<>();
                boolean rerun = true;
                if(type.equalsIgnoreCase("s")){
                    KB = KBGenerator.KBGenerate(defImplicationDistribution, simple, reuseConsequent, complexityAnt, complexityCon, connectiveTypes);
                }
                else{
                    boolean s = simple;
                    do{
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        long timeoutDuration = 10000;
                        try{
                            Callable<LinkedHashSet<LinkedHashSet<DefImplication>>> kbGenerationTask = () -> {
                                return KBGeneratorThreaded.KBGenerate(defImplicationDistribution, s, complexityAnt, complexityCon, connectiveTypes);
                            };
                            Future<LinkedHashSet<LinkedHashSet<DefImplication>>> future = executor.submit(kbGenerationTask);
                            KB = future.get(timeoutDuration, TimeUnit.MILLISECONDS);
                            rerun = false;

                        }catch(TimeoutException e){
                            System.out.println("Timeout occurred during KB generation. Retrying...");
                            executor.shutdownNow();
                            gen.reset();
                            rerun = true;
                        }catch(InterruptedException | ExecutionException e){

                        }finally{
                            executor.shutdownNow();
                        }
                    }while(rerun == true);
                }

                System.out.println("Save to text file? [y, n]:");
                System.out.print("> ");
                String save = in.next(); // Save the knowledge base to a text file
                if(save.equalsIgnoreCase("y")){
                kbToFile(KB);
                }

                System.out.println("Print to terminal? [y, n]:");
                System.out.print("> ");
                String print = in.next(); // Print knowledge base to terminal
                if(print.equalsIgnoreCase("y")){
                    System.out.println("Knowledge base:");
                    int i = 0;
                    for (LinkedHashSet<DefImplication> set : KB){
                        System.out.print("Rank " + i + ": ");
                        Iterator<DefImplication> iterator = set.iterator();
                        while (iterator.hasNext()){
                            DefImplication element = iterator.next();
                            System.out.print(element.toString());
                            
                            if(iterator.hasNext()){
                                System.out.print(", ");
                            }
                            else{
                                System.out.println();
                            }
                        }
                        i++;
                    }
                }
                
                gen.reset();
                System.out.println("Regenerate new knowledge base? [r]:");
                System.out.println("Change settings? [c]:");
                System.out.println("Quit? [q]:");
                System.out.print("> ");
                choice = in.next();
                
            }while(choice.equalsIgnoreCase("r"));
        }while(choice.equalsIgnoreCase("c"));
        System.out.println("Quitting");
        in.close();
    }

    // Private helper methods:

    /**
     * Checks if the input string is a valid distribution type.
     *
     * @param input The input string to check.
     * @return True if the input is a valid distribution type, otherwise false.
     */
    private static boolean validDistribution(String input){
        return input.equalsIgnoreCase("f") || input.equalsIgnoreCase("lg") ||
               input.equalsIgnoreCase("ld") || input.equalsIgnoreCase("r");
    }

    /**
     * Checks if the input string is a valid character set.
     *
     * @param input The input string to check.
     * @return True if the input is a valid character set, otherwise false.
     */
    private static boolean validCharacterSet(String input){
        return input.equalsIgnoreCase("lowerlatin") || input.equalsIgnoreCase("upperlatin") ||
               input.equalsIgnoreCase("altlatin") || input.equalsIgnoreCase("greek");
    }


    /**
     * Calculates the minimum number of defImplications required based on the distribution type and number of ranks.
     *
     * @param distribution The distribution type.
     * @param numRanks     The number of ranks in the knowledge base.
     * @return The minimum number of defImplications required.
     */
    private static int minDefImplications(String distribution, int numRanks){
        int min = 0;
        switch(distribution){
            case "f":
                min = (numRanks*2)-1;
                break;
            case "lg":
                min = Distribution.minDIsLinear(numRanks);
                break;
            case "ld":
                min = Distribution.minDIsLinearDecline(numRanks);
                break;
            case "r":
                min = (numRanks*2);
                break;
        }
        return min;
    }

    /**
     * Writes a knowledge base to a text file.
     *
     * @param KB The knowledge base to write to the file.
     */
    private static void kbToFile(LinkedHashSet<LinkedHashSet<DefImplication>> KB){
        String filePath = "output" + filenum + ".txt";
        filenum++;
        try{
            File file = new File(filePath);
            FileWriter fw = new FileWriter(file);

            for(LinkedHashSet<DefImplication> set : KB){
                for (DefImplication element : set){
                    fw.write(element.toString() + "\n");
                }
            }
            fw.close();
        } 
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
