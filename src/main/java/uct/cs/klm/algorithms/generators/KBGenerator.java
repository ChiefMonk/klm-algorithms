/*
 * It provides methods for generating unique identifiers, negating atoms, and checking equality.
 */
package uct.cs.klm.algorithms.generators;

import java.util.*;

/**
 * The KBGenerator class controls the generation of defeasible knowledge bases.
*/
public class KBGenerator{
    
    private static AtomBuilder gen = AtomBuilder.getInstance();

    /**
     * Generates a knowledge base (KB) consisting of a collection of ranks, each containing defeasible implications (DIs).
     *
     * @param DiDistribution    The distribution of DIs for each rank.
     * @param simpleOnly        If true, only simple DIs are generated; if false, complex DIs are also generated.
     * @param reuseConsequent   If true, the original rankBaseCons is reused in all ranks; if false, it is not reused.
     * @param complexityAnt     The complexity of antecedents for complex DIs.
     * @param complexityCon     The complexity of consequents for complex DIs.
     * @param connectiveType    The type of connectives used in complex DIs.
     * @return A LinkedHashSet representing the generated KB.
     */
    public static LinkedHashSet<LinkedHashSet<DefImplication>> KBGenerate(int[] DiDistribution, boolean simpleOnly, boolean reuseConsequent, int[] complexityAnt, int[] complexityCon, int[] connectiveType){

        Random random = new Random();
        int rank = 0;
        LinkedHashSet<LinkedHashSet<DefImplication>> KB = new LinkedHashSet<LinkedHashSet<DefImplication>>(); // Creating KB.
        Atom rankBaseCons = gen.generateAtom(); // Atom acts as the lynchpin for generating new ranks.
        Atom rankBaseAnt = gen.generateAtom(); // Atom acts as the lynchpin for generating new ranks.
        ArrayList<Atom> anyRankAtoms = new ArrayList<Atom>(); // Reusable atoms in any rank.

        while(rank!=DiDistribution.length){
            ArrayList<DefImplication> DIs = new ArrayList<DefImplication>();
            ArrayList<Atom> curRankAtoms = new ArrayList<Atom>(); // Reusable atoms in current ranks antecedent.
            ArrayList<Atom> anyRankAtomsTemp = new ArrayList<Atom>();
            int DiNum = DiDistribution[rank];
            if(rank==0){
                DiNum--;
                DefImplicationBuilder.rankZero(DIs, rankBaseCons, rankBaseAnt);
            }
            else{
                if(reuseConsequent == false && DiNum >=3){ // Don't reuse the original rankBaseCons in all ranks
                    DefImplicationBuilder.rankBuilder(gen, DIs, rankBaseCons, rankBaseAnt);
                    DiNum--;
                }
                else{ // Reuse the original rankBaseCons in all ranks
                    DefImplicationBuilder.rankBuilderConstricted(gen, DIs, rankBaseCons, rankBaseAnt);
                }
                DiNum = DiNum - 2;
            }
            curRankAtoms.add(rankBaseAnt);
            while(DiNum!=0){
                if(simpleOnly == true){
                    int decision = random.nextInt(3);
                    DefImplicationBuilder.simpleDI(decision, gen, DIs, anyRankAtoms, curRankAtoms, anyRankAtomsTemp);
                }
                else{
                    String key = Rules.keyGenerator(connectiveType, complexityAnt, complexityCon, curRankAtoms.size());
                    DefImplicationBuilder.complexDI(key, gen, DIs, curRankAtoms);
                }
                DiNum--;
            }
            rankBaseCons.negateAtom(); // Negates the consequent for formation of next rank.
            KB.add(new LinkedHashSet<DefImplication>(DIs));
            anyRankAtoms.addAll(anyRankAtomsTemp);
            rank++;
        }
        return KB;
    }
}