package uct.cs.klm.algorithms.generators;

import java.util.HashMap;

/**
 * The Rules class defines and manages the rules controlling the generation of complex defeasible implications.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class Rules{
    
    private static HashMap<String, String> keyMap; // Hashmap of complex statements and their required available currRankAtoms

    /**
     * Constructor for the Rules class. Initializes the keyMap with rules for generating complex statements.
     */
    public Rules(){
        // Key = "connectionType,complexityAnt,complexityCon"
        // Value = "currRankAtoms.length()"
        keyMap = new HashMap<>();
        
        // connectionType = disjunction
        keyMap.put("1,0,1", "2");
        keyMap.put("1,0,2", "3");
        keyMap.put("1,1,0", "1");
        keyMap.put("1,1,1", "2");
        keyMap.put("1,1,2", "3");
        keyMap.put("1,2,0", "1");
        keyMap.put("1,2,1", "2");
        keyMap.put("1,2,2", "3");
        // connectionType = conjunction
        keyMap.put("2,0,1", "1");
        keyMap.put("2,0,2", "1");
        keyMap.put("2,1,0", "1");
        keyMap.put("2,1,1", "1");
        keyMap.put("2,1,2", "1");
        keyMap.put("2,2,0", "1");
        keyMap.put("2,2,1", "1");
        keyMap.put("2,2,2", "1");
        // connectionType = implication
        keyMap.put("3,0,1", "1");
        keyMap.put("3,0,2", "1");
        keyMap.put("3,1,0", "1");
        keyMap.put("3,2,0", "1");
        // connectionType = bi-implication
        keyMap.put("4,0,1", "1");
        keyMap.put("4,0,2", "1");
        keyMap.put("4,1,0", "1");
        keyMap.put("4,2,0", "1");
        // connectionType = mixed connectives
        keyMap.put("5,0,1", "1");
        keyMap.put("5,0,2", "1");
        keyMap.put("5,1,0", "2");
        keyMap.put("5,1,1", "2");
        keyMap.put("5,1,2", "2");
        keyMap.put("5,2,0", "2");
        keyMap.put("5,2,1", "2");
        keyMap.put("5,2,2", "2");
    }

    /**
     * Checks if there are enough currRankAtoms available to generate a statement for a given key and returns the key if it is valid.
     *
     * @param key           The key representing the statement to be generated.
     * @param curRankAtomNum The number of available current rank atoms.
     * @return The key if it is valid; otherwise, "1,0,0" indicating the generation of a simple DI.
     */
    public static String checker(String key, int curRankAtomNum){
        String temp = keyMap.get(key);
        // Do this to avoid problems with implication and bi-implication.
        if(temp == null){
            return "1,0,0"; // Generates simple DI.
        }
        else{
            int curRankAtomMin = Integer.parseInt(temp.substring(0,1));
            return (curRankAtomNum>=curRankAtomMin) ? key : "1,0,0";
        }
    }

    /**
     * Generates a statement key based on available connection types and complexities for antecedent and consequent.
     *
     * @param connectionType The available connection types.
     * @param complexityAnt  The available complexities for the antecedent.
     * @param complexityCon  The available complexities for the consequent.
     * @param curRankAtomNum The number of available current rank atoms.
     * @return A statement key representing the type of statement to be generated.
     */
    public static String keyGenerator(int[] connectionType, int[] complexityAnt, int[] complexityCon, int curRankAtomNum){
        int i = (int)(Math.random() * connectionType.length); // Get random connectionType from those available.
        int j = (int)(Math.random() * complexityAnt.length); // Get random connectionType from those available.
        int z; do{ z = (int)(Math.random() * complexityCon.length);} while ((complexityCon[z] == 0 && complexityAnt[j] == 0)); // Get random connectionType from those available (z & j can't both be 0).
        String key = connectionType[i] + "," + complexityAnt[j] + "," + complexityCon[z];
        return checker(key, curRankAtomNum);
    }
}