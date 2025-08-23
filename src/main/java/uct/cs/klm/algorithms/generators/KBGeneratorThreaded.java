package uct.cs.klm.algorithms.generators;

import java.util.*;
import java.util.concurrent.*;

/**
 * The KBGeneratorThreaded class provides an optimised version of defeasible knowledge base generation
 * using multithreading for parallel processing.
 */
public class KBGeneratorThreaded{

    private static AtomBuilder gen = AtomBuilder.getInstance();
    private static int numThreads = Runtime.getRuntime().availableProcessors();

    /**
     * Generates a knowledge base (KB) consisting of a collection of ranks, each containing defeasible implications (DIs).
     *
     * @param DiDistribution The distribution of DIs for each rank.
     * @param simpleOnly     If true, only simple DIs are generated; if false, complex DIs are also generated.
     * @param complexityAnt  The complexity of antecedents for complex DIs.
     * @param complexityCon  The complexity of consequents for complex DIs.
     * @param connectiveType The type of connectives used in complex DIs.
     * @return A LinkedHashSet representing the generated KB.
     */
    public static LinkedHashSet<LinkedHashSet<DefImplication>> KBGenerate(int[] DiDistribution, boolean simpleOnly, int[] complexityAnt, int[] complexityCon, int[] connectiveType){
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        LinkedHashSet<LinkedHashSet<DefImplication>> KB = new LinkedHashSet<LinkedHashSet<DefImplication>>();
        ArrayList<Atom> anyRankAtoms = new ArrayList<Atom>(); // Reusable atoms in any rank.
        Atom rankBaseCons = gen.generateAtom(); // Atom acts as the lynchpin for generating new ranks.
        Atom[] rankBaseAnts = new Atom[DiDistribution.length]; // Stores each ranks rankBaseAnt to tie the ranks together.

        try{
            ArrayList<Future<LinkedHashSet<DefImplication>>> futures = new ArrayList<>();

            for (int rank = 0; rank < DiDistribution.length; rank++){
                int r = rank;
                Future<LinkedHashSet<DefImplication>> future = executor.submit(() -> generateRank(r, DiDistribution, simpleOnly, complexityAnt, complexityCon, connectiveType, rankBaseCons, rankBaseAnts, anyRankAtoms));
                futures.add(future);
            }

            for (Future<LinkedHashSet<DefImplication>> future : futures){
                KB.add(future.get());
            }
        }
        catch(InterruptedException | ExecutionException e){
            
        }
        finally{
            executor.shutdown();
        }

        boolean firstSetProcessed = false;
        int i = 1;
        for(LinkedHashSet<DefImplication> set : KB){
            if (!firstSetProcessed) {
                firstSetProcessed = true;
                continue;
            }
            set.add(new DefImplication(rankBaseAnts[i].toString(), new Atom(rankBaseAnts[i-1]).toString()));
            i++;
        }
        return KB;
    }

    /**
     * Generates a single rank of defeasible implications (DIs) within a knowledge base (KB).
     *
     * @param rank            The rank number.
     * @param DiDistribution  The distribution of DIs for each rank.
     * @param simpleOnly      If true, only simple DIs are generated; if false, complex DIs are also generated.
     * @param complexityAnt   The complexity of antecedents for complex DIs.
     * @param complexityCon   The complexity of consequents for complex DIs.
     * @param connectiveType  The type of connectives used in complex DIs.
     * @param rankBaseCons    The consequent of the rank.
     * @param rankBaseAnts    An array storing the antecedents of each rank to tie them together.
     * @param anyRankAtoms    The list of atoms that can be reused in any rank.
     * @return A LinkedHashSet representing the generated rank of DIs.
     */
    private static LinkedHashSet<DefImplication> generateRank(int rank, int[] DiDistribution, boolean simpleOnly, int[] complexityAnt, int[] complexityCon, int[] connectiveType, Atom rankBaseCons, Atom[] rankBaseAnts, ArrayList<Atom> anyRankAtoms){
        ArrayList<Atom> anyRankAtomList = new ArrayList<Atom>(anyRankAtoms);
        Random random = new Random();
        Atom rankBaseAnt = gen.generateAtom(); // Atom acts as the lynchpin for generating new ranks.
        ArrayList<Atom> anyRankAtomsTemp = new ArrayList<Atom>();

        synchronized(rankBaseAnts){
            rankBaseAnts[rank] = rankBaseAnt;
        }
        
        ArrayList<DefImplication> DIs = new ArrayList<DefImplication>();
        ArrayList<Atom> curRankAtoms = new ArrayList<Atom>(); // Reusable atoms in current ranks antecedent.
        int DiNum = DiDistribution[rank];
        
        DiNum--;
        if(rank % 2 == 0){ // 1 defImplication is generated first for each rank, no matter the rank
            DefImplicationBuilder.rankZero(DIs, rankBaseCons, rankBaseAnt);
        }
        else{
            Atom rBCNegated = new Atom(rankBaseCons);
            rBCNegated.negateAtom();
            DefImplicationBuilder.rankZero(DIs, rBCNegated, rankBaseAnt);
        }

        curRankAtoms.add(rankBaseAnt);
        if(!(rank == 0)){ // Leaves 1 extra defImplication to be generated for each rank other than for Rank 0.
            DiNum = DiNum - 1;
        }
        while(DiNum!=0){
            if(simpleOnly == true){
                int decision = random.nextInt(3);
                DefImplicationBuilder.simpleDI(decision, gen, DIs, anyRankAtomList, curRankAtoms, anyRankAtomsTemp);
                DiNum--;
            }
            else{
                String key = Rules.keyGenerator(connectiveType, complexityAnt, complexityCon, curRankAtoms.size());
                DefImplicationBuilder.complexDI(key, gen, DIs, curRankAtoms);
                DiNum--;
            }
        }
        anyRankAtoms.addAll(anyRankAtomsTemp);
        return new LinkedHashSet<>(DIs);
    }
}