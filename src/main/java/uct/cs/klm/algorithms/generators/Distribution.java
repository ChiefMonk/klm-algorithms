
package uct.cs.klm.algorithms.generators;

import java.util.Arrays;

/**
 * The Distribution class provides methods for calculating the distribution of defeasible implications (DIs) over ranks.
 */
public class Distribution{

    /**
     * Controls the distribution calculation for DIs over the ranks based on the specified distribution type.
     *
     * @param numDIs      The total number of DIs to distribute.
     * @param numRanks    The number of ranks over which DIs are distributed.
     * @param distribution The type of distribution to calculate (f: flat, lg: linear growth, ld: linear decline, r: random).
     * @return An array representing the calculated distribution of DIs over the ranks.
     */
    public static int[] distributeDIs(int numDIs, int numRanks, String distribution){
        int[] ranks = new int[numRanks];

        switch (distribution){
            case "f":
                distributeFlat(numDIs, numRanks, ranks);
                break;
            case "lg":
                distributeLinearGrowth(numDIs, numRanks, ranks);
                break;
            case "ld":
                distributeLinearDecline(numDIs, numRanks, ranks);
                break;
            case "r":
                distributeRandom(numDIs, numRanks, ranks);
                break;
        }
        return ranks;
    }

    /**
     * Calculates a flat distribution of DIs over the ranks.
     *
     * @param numDIs   The total number of DIs to distribute.
     * @param numRanks The number of ranks over which DIs are distributed.
     * @param ranks    An array to store the calculated distribution of DIs.
     */
    private static void distributeFlat(int numDIs, int numRanks, int[] ranks){
        int defImplicationsPerRank = numDIs / numRanks;
        int remainder = numDIs % numRanks;

        for (int i = 0; i < numRanks; i++){
            ranks[i] = defImplicationsPerRank;
        }

        int i = numRanks-1;
        while (remainder > 0){
            ranks[i]++;
            remainder--;
            i--;
        }
    }

    /**
     * Calculates a linear-growth distribution of DIs over the ranks.
     *
     * @param numDIs   The total number of DIs to distribute.
     * @param numRanks The number of ranks over which DIs are distributed.
     * @param ranks    An array to store the calculated distribution of DIs.
     */
    private static void distributeLinearGrowth(int numDIs, int numRanks, int[] ranks){
        int remainingDIs = numDIs;
        
        for (int i = 0; i < numRanks; i++){
            int defImplicationsToAdd = Math.min(remainingDIs, i + 1);
            ranks[i] = defImplicationsToAdd;
            remainingDIs -= defImplicationsToAdd;
        }
    
        int currentRank = numRanks - 1;
        while (remainingDIs > 0){
            if(currentRank < 0){
                currentRank = numRanks - 1;
            }
            int defImplicationsToAdd = Math.min(remainingDIs, 1);
            ranks[currentRank] += defImplicationsToAdd;
            remainingDIs -= defImplicationsToAdd;
            currentRank--;
        }
    }
    
    /**
     * Calculates a linear-decline distribution of DIs over the ranks.
     *
     * @param numDIs   The total number of DIs to distribute.
     * @param numRanks The number of ranks over which DIs are distributed.
     * @param ranks    An array to store the calculated distribution of DIs.
     */
    private static void distributeLinearDecline(int numDIs, int numRanks, int[] ranks){
        int remainingDIs = numDIs;
    
        for (int i = 0; i < numRanks; i++){
            int defImplicationsToAdd = Math.min(remainingDIs, i + 1);
            ranks[i] = defImplicationsToAdd;
            remainingDIs -= defImplicationsToAdd;
        }
    
        int currentRank = numRanks - 1;
        while (remainingDIs > 0){
            if(currentRank < 0){
                currentRank = numRanks - 1;
            }
            int defImplicationsToAdd = Math.min(remainingDIs, 1);
            ranks[currentRank] += defImplicationsToAdd;
            remainingDIs -= defImplicationsToAdd;
            currentRank--;
        }
    
        for (int i = 0; i < numRanks / 2; i++){
            int temp = ranks[i];
            ranks[i] = ranks[numRanks - i - 1];
            ranks[numRanks - i - 1] = temp;
        }
    }

    /**
     * Calculates a random distribution of DIs over the ranks.
     *
     * @param numDIs   The total number of DIs to distribute.
     * @param numRanks The number of ranks over which DIs are distributed.
     * @param ranks    An array to store the calculated distribution of DIs.
     */
    private static void distributeRandom(int numDIs, int numRanks, int[] ranks){
        int remainingDIs = numDIs - numRanks * 2;
        Arrays.fill(ranks, 2);

        while(remainingDIs > 0){
            int i = (int)(Math.random() * ranks.length);
            ranks[i]++;
            remainingDIs--;
        }
    }
     
    /**
     * Calculates the minimum number of DIs needed for a linear-growth distribution.
     *
     * @param numRanks The number of ranks over which DIs are distributed.
     * @return The minimum number of DIs needed for a linear-growth distribution.
     */
    public static int minDIsLinear(int numRanks){
        int sum = numRanks * (numRanks + 1) / 2;
        return sum;
    }

    /**
     * Calculates the minimum number of DIs needed for a linear-decline distribution.
     *
     * @param numRanks The number of ranks over which DIs are distributed.
     * @return The minimum number of DIs needed for a linear-decline distribution.
     */
    public static int minDIsLinearDecline(int numRanks){
        int sum = 0;
        int x = 2;
        for(int i = 0; i < numRanks; i++){
            sum += (x);
            x++;
        }
        return sum;
    }
}