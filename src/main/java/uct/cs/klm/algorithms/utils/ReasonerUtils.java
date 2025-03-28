package uct.cs.klm.algorithms.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.DefeasibleImplication;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

/**
 *
 * @author ChipoHamayobe
 */
public final class ReasonerUtils {

    public static KnowledgeBase toCombinedKnowledgeBases(
            KnowledgeBase firstKb,
            KnowledgeBase secondKb) {

        KnowledgeBase result = new KnowledgeBase(firstKb);
        result.addAll(secondKb);

        return result;
    }
    
     /**
     * Retrieves the antecedents of statements with implication.
     *
     * @param knowledgeBase
     * @return Knowledge base representing the antecedents.
     */
    public static KnowledgeBase getAntecedentFormulas(
            KnowledgeBase knowledgeBase) {

         KnowledgeBase antecedents = new KnowledgeBase();
        knowledgeBase.forEach(formula -> {
            if (formula instanceof Implication implication) {
                antecedents.add(implication.getFirstFormula());
            }
        });
        return antecedents;
    }
     
  
     /**
     * Convert defeasible implication to classical implication.
     *
     * @param formula Classical implication formula
     * @return Defeasible implication.
     */
    public static PlFormula toMaterialisedFormula(PlFormula formula) {

        if (formula instanceof DefeasibleImplication defeasibleImplication) {
            return new Implication(defeasibleImplication.getFormulas());
        }

        return formula;
    }
    
       /**
     * Convert classical implication to defeasible implication.
     *
     * @param formula Defeasible implication formula.
     * @return Classical implication.
     */
    public static PlFormula toDematerialisedFormula(PlFormula formula) {
        if ((formula instanceof Implication implication) && !(formula instanceof DefeasibleImplication)) {
            return new DefeasibleImplication(implication.getFormulas());
        }

        return formula;
    }


    public static KnowledgeBase toMaterialisedKnowledgeBase(KnowledgeBase knowledgeBase) {

        KnowledgeBase result = new KnowledgeBase();

        knowledgeBase.forEach(formula -> {
            result.add(toMaterialisedFormula(formula));
        });

        return result;
    }

    public static KnowledgeBase toMaterialisedKnowledgeBase(ModelRank rank) {
        return toMaterialisedKnowledgeBase(rank.getFormulas().materialisedKnowledgeBase());
    }

    public static KnowledgeBase toMaterialisedKnowledgeBase(ArrayList<ModelRank> baseRank) {
        return toMaterialisedKnowledgeBase(toKnowledgeBase(baseRank).materialisedKnowledgeBase());
    }
    
    public static KnowledgeBase toMaterialisedKnowledgeBase(List<ModelRank> baseRank) {
        return toMaterialisedKnowledgeBase(toKnowledgeBase(baseRank).materialisedKnowledgeBase());
    }

    public static ModelRankCollection toModelRankCollection(ArrayList<ModelRank> baseRank) {

        ModelRankCollection result = new ModelRankCollection();

        baseRank.forEach(rank -> {
            result.add(rank);
        });

        return result;
    }
    

    public static KnowledgeBase toKnowledgeBase(List<ModelRank> baseRank) {

        Collection<PlFormula> result = new ArrayList<>();

        baseRank.forEach(rank -> {
            rank.getFormulas().forEach(formula -> {
                result.add(formula);
            });
        });

        return new KnowledgeBase(result);
    }

    public static List<PlFormula> toFormulaList(ModelRank rank) {

        List<PlFormula> formulaList = new ArrayList<>();

        for (PlFormula formula : rank.getFormulas()) {
            formulaList.add(formula);
        }
        return formulaList;
    }

    public static KnowledgeBase removeFormula(KnowledgeBase kb, PlFormula formula) {
        KnowledgeBase result = new KnowledgeBase(kb);
        result.getFormulas().remove(formula);
        return result;
    }

    public static ModelRankCollection toRemainingFormulas(
            ModelRankCollection baseRank,
            List<Integer> removedRankNumbers) {

        // Create a copy of baseRank
        var baseRankCopy = new ModelRankCollection(baseRank);

        // Return the copy if removedRankNumbers is null or empty
        if (removedRankNumbers == null || removedRankNumbers.isEmpty()) {
            return baseRankCopy;
        }

        for (Integer rankNumber : removedRankNumbers) {
            baseRankCopy.removeIf(model -> model.getRankNumber() == rankNumber);
        }

        return baseRankCopy;
    }

    public static ModelRankCollection toRemainingFormulas(
            ModelRankCollection baseRank,
            ArrayList<PlFormula> removedFormulas) {

        var baseRankCopy = new ModelRankCollection(baseRank);

        if (removedFormulas == null || removedFormulas.isEmpty()) {
            return baseRankCopy;
        }

        for (var formula : removedFormulas) {
            for (var rank : baseRankCopy) {
                rank.removeFormula(formula);
            }
        }

        return baseRankCopy;
    }

    public static ModelRankCollection toRemovedFormulas(
            ModelRankCollection baseRank,
            List<Integer> removedRankNumbers) {

        var baseRankRemoved = new ModelRankCollection();

        if (removedRankNumbers == null || removedRankNumbers.isEmpty()) {
            return baseRankRemoved;
        }

        for (ModelRank rank : baseRank) {
            if (removedRankNumbers.contains(rank.getRankNumber())) {
                baseRankRemoved.add(rank);
            }
        }

        return baseRankRemoved;
    }

    public static ModelRankCollection toRemovedFormulas(
            ModelRankCollection baseRank,
            ArrayList<PlFormula> removedFormulas) {

        if (removedFormulas == null || removedFormulas.isEmpty()) {
            return new ModelRankCollection();
        }

        var baseRankCopy = new ModelRankCollection(baseRank);

        for (var rank : baseRankCopy) {
            for (var formula : rank.getFormulas()) {
                if (!removedFormulas.contains(formula)) {
                    rank.removeFormula(formula);
                }
            }
        }

        return baseRankCopy;
    }

    public static KnowledgeBase removeFormulasFromKnowledgeBase(
            KnowledgeBase originalKnowledgeBase,
            KnowledgeBase formulasKnowledgeBase) {

        KnowledgeBase result = new KnowledgeBase(originalKnowledgeBase);

        if (formulasKnowledgeBase == null || formulasKnowledgeBase.isEmpty()) {
            return result;
        }

        for (var formula : formulasKnowledgeBase) {
            result.removeFormula(formula);
        }

        return result;
    }

    public boolean IsDefeasibleFormula(PlFormula formula) {
        return (formula instanceof DefeasibleImplication);
    }

    public static ModelRank getInfinityRank(ModelRankCollection rankCollection) {
        return rankCollection.stream()
                .filter(rank -> rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER)
                .findFirst()
                .orElse(null);
    }

    public static ModelRankCollection toRemainingEntailmentRanks(ArrayList<ModelRank> baseRank, KnowledgeBase remainingknowledgeBase) {

        if (remainingknowledgeBase == null || remainingknowledgeBase.isEmpty()) {
            return new ModelRankCollection(baseRank.get(baseRank.size() - 1));
        }

        ModelRankCollection result = new ModelRankCollection();

        var materialknowledgeBase = toMaterialisedKnowledgeBase(remainingknowledgeBase);

        for (ModelRank rank : baseRank) {

            ModelRank curentRank = new ModelRank(rank.getRankNumber());

            for (PlFormula formula : rank.getFormulas()) {

                var materialFormula = toMaterialisedFormula(formula);

                if (materialknowledgeBase.contains(materialFormula)) {
                    curentRank.addFormula(formula);
                }
            }

            if (!curentRank.isEmpty()) {
                result.add(curentRank);
            }

        }

        return result;
    }

    public static ArrayList<ModelRank> generateFormulaCombinations(ModelRankCollection rankCollection, boolean ignoreInfinityRank) {

        ModelRank theRank = new ModelRank();
        theRank.setRankNumber(0);

        for (ModelRank rank : rankCollection) {

            theRank.addFormulas(rank.getFormulas());
        }

        return generateFormulaCombinations(theRank, ignoreInfinityRank);
    }
    
    public static ModelRankCollection generateFormulaCombinations(ModelRank rank, boolean ignoreInfinityRank) {

        ArrayList<ModelRank> rankCollection = generateFormulaCombinationList(rank, ignoreInfinityRank);

        ModelRankCollection theRank = new ModelRankCollection();

        for (var r : rankCollection) {

            theRank.add(r);
        }

        return theRank;
    }

    public static ArrayList<ModelRank> generateFormulaCombinationList(ModelRank rank, boolean ignoreInfinityRank) {

        ArrayList<ModelRank> modelRankings = new ArrayList<>();

        if (rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER) {
            modelRankings.add(rank);
            return modelRankings;
        }

        List<PlFormula> formulaList = toFormulaList(rank);

        if (formulaList.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<PlFormula>> combinations = new ArrayList<>();

        // Generate all possible combinations
        for (int i = 1; i <= rank.getFormulas().size(); i++) {
            combinations.addAll(generateFormulaCombinations(formulaList, i));
        }

        combinations.add(new ArrayList<>());

        combinations.sort((a, b) -> Integer.compare(b.size(), a.size()));

        for (int i = 0; i < combinations.size(); i++) {

            ModelRank model = new ModelRank(i);

            for (PlFormula formula : combinations.get(i)) {
                model.addFormula(formula);
            }

            modelRankings.add(model);
        }

        int counter = 0;
        System.out.println();
        System.out.println(String.format("Mini SubRanks for rank = %s:%s", rank.getRankNumber(), rank.getFormulas()));
        for (ModelRank input : modelRankings) {
            System.out.println(String.format("%s : %s", counter, input.getFormulas()));
            counter++;
        }

        return modelRankings;
    }
    
    public static double ToTimeDifference(long startTime, long endTime)
    {
        return (endTime - startTime) / 1_000_000_000.0;
    }       

    private static List<List<PlFormula>> generateFormulaCombinations(List<PlFormula> formulaList, int size) {

        List<List<PlFormula>> combinations = new ArrayList<>();

        if (size == 0) {
            combinations.add(new ArrayList<>());
        } else {

            for (int i = 0; i < formulaList.size(); i++) {
                PlFormula value = formulaList.get(i);
                List<PlFormula> rest = formulaList.subList(i + 1, formulaList.size());
                for (List<PlFormula> p : generateFormulaCombinations(rest, size - 1)) {
                    List<PlFormula> combination = new ArrayList<>();
                    combination.add(value);
                    combination.addAll(p);
                    combinations.add(combination);
                }
            }
        }

        return combinations;
    }
}
