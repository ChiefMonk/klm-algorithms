package uct.cs.klm.algorithms.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.DefeasibleImplication;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelFormulaRanked;
import uct.cs.klm.algorithms.ranking.ModelBaseRank;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;

/**
 *
 * @author ChipoHamayobe
 */
public final class ReasonerUtils {

    private static final Logger _logger = LoggerFactory.getLogger(ReasonerUtils.class);

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

    public static KnowledgeBase toKnowledgeBase(ModelRank infinityRank, ModelRankCollection higherRanks, List<PlFormula> powersetEntry, KnowledgeBase irrelevantKb) {

        List<PlFormula> allFormulas = new ArrayList<>();

        allFormulas.addAll(infinityRank.getFormulas());

        allFormulas.addAll(higherRanks.getKnowledgeBase());

        var powersetKb = new KnowledgeBase(powersetEntry);
        allFormulas.addAll(powersetKb);

        powersetKb.addAll(allFormulas);

        if (irrelevantKb != null && !irrelevantKb.isEmpty()) {
            powersetKb.addAll(irrelevantKb);
        }

        return powersetKb;
    }

    public static KnowledgeBase toMaterialisedKnowledgeBase(ModelRank infinityRank, ModelRankCollection higherRanks, List<PlFormula> powersetEntry) {

        List<PlFormula> allFormulas = new ArrayList<>();

        allFormulas.addAll(infinityRank.getFormulas());

        allFormulas.addAll(toMaterialisedKnowledgeBase(higherRanks));

        var powersetKb = new KnowledgeBase(powersetEntry);
        allFormulas.addAll(powersetKb.materialisedKnowledgeBase());

        powersetKb.addAll(allFormulas);

        return powersetKb.materialisedKnowledgeBase();
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

    public static List<PlFormula> toFormulaList(KnowledgeBase knowledgeBase) {

        List<PlFormula> formulaList = new ArrayList<>();

        for (PlFormula formula : knowledgeBase.getFormulas()) {
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
            KnowledgeBase currentKb,
            KnowledgeBase removeKb) {

        KnowledgeBase result = new KnowledgeBase(currentKb);

        if (removeKb == null || removeKb.isEmpty()) {
            return result;
        }

        for (var formula : removeKb) {
            result.removeFormula(formula);
            result.removeFormula(toMaterialisedFormula(formula));
            result.removeFormula(toDematerialisedFormula(formula));
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

    public static ModelRankCollection toRanksFromKnowledgeBase(ModelBaseRank baseRank, KnowledgeBase knowledgeBase, boolean opposite) {

        if (knowledgeBase == null || knowledgeBase.isEmpty()) {
            return new ModelRankCollection();
        }

        ModelRankCollection baseRankCollection = new ModelRankCollection(baseRank.getRanking());
        ModelRankCollection result = new ModelRankCollection();

        var materialknowledgeBase = toMaterialisedKnowledgeBase(knowledgeBase);

        for (ModelRank rank : baseRankCollection) {

            ModelRank curentRank = new ModelRank(rank.getRankNumber());

            for (PlFormula formula : rank.getFormulas()) {

                var materialFormula = toMaterialisedFormula(formula);

                if (opposite) {
                    if (!materialknowledgeBase.contains(materialFormula)) {
                        curentRank.addFormula(formula);
                    }
                } else {
                    if (materialknowledgeBase.contains(materialFormula)) {
                        curentRank.addFormula(formula);
                    }
                }

            }

            if (!curentRank.isEmpty()) {
                result.add(curentRank);
            }

        }

        return result;
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

    public static double ToTimeDifference(long startTime, long endTime) {
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

    public static List<List<PlFormula>> AddToList(List<List<PlFormula>> addTo, List<List<PlFormula>> addFrom) {
        for (List<PlFormula> list : addFrom) {
            addTo.add(list);
        }

        return addTo;
    }

    public static Map.Entry<ModelRankCollection, List<List<PlFormula>>> powersetIterative(ModelRankCollection rankCollection, int currentRank, List<List<PlFormula>> previousPowersets) {

        rankCollection.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        previousPowersets = previousPowersets.stream()
                .distinct()
                .collect(Collectors.toList());

        List<PlFormula> formulaList = new ArrayList<>();
        ModelRankCollection outputRanks = new ModelRankCollection();

        for (ModelRank rank : rankCollection) {

            if (rank.getRankNumber() > currentRank) {
                outputRanks.add(rank);
                continue;
            }

            formulaList.addAll(rank.getFormulas());
        }

        int counter = 0;
        if (previousPowersets.isEmpty()) {
            DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::previousRankSets := %s", previousPowersets));
        } else {
            for (var ff : previousPowersets) {
                DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::previousRankSets := %s: %s", counter, ff));
                counter++;
            }
        }

        /*
        int counter = 0;
        if(previousRankSets.isEmpty())
        {           
            DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::previousRankSets := %s", previousRankSets));               
        }
        else
        {
          for (var ff : previousRankSets) {
                DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::previousRankSets := %s: %s", counter, ff));  
                counter++;
            }  
        }
        
        for (var ff : outputRanks) {
            DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::OutputRanks := %s: %s", ff.getRankNumber(), ff.getFormulas()));           
        }
        
        counter = 0;
        for (var ff : formulaList) {
            DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::formulaList := %s: %s", counter, ff));     
            counter++;
        }
         */
        List<List<PlFormula>> result = new ArrayList<>();
        int n = formulaList.size();
        int totalSubsets = 1 << n;

        for (int i = 1; i < totalSubsets; i++) {
            List<PlFormula> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                // Check if jth bit is set in i
                if ((i & (1 << j)) != 0) {
                    subset.add(formulaList.get(j));
                }
            }
            result.add(subset);
        }

        /*
        counter = 0;
        for (var ff : result) {
            DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::result := %s: %s", counter, ff));
            counter++;
        }
         */
        List<List<PlFormula>> finalResult = new ArrayList<>();

        for (List<PlFormula> list : result) {

            DisplayUtils.LogDebug(_logger, String.format("---powersetIterative::CurrentList := %s", list));

            if (previousPowersets.contains(list)) {
                continue;
            }

            boolean exists = previousPowersets.stream().anyMatch(ls -> ls.size() == list.size() && new HashSet<>(ls).equals(new HashSet<>(list)));

            if (exists) {
                continue;
            }

            finalResult.add(list);
        }

        finalResult.sort(Comparator.comparingInt(List<PlFormula>::size).reversed());

        //finalResult.add(new ArrayList<>());          
        counter = 0;
        for (var ff : finalResult) {
            DisplayUtils.LogDebug(_logger, String.format(" ==> powersetIterative::FinalResult := %s: %s", counter, ff));
            counter++;
        }

        return new AbstractMap.SimpleEntry<>(outputRanks, finalResult);
    }

    public static List<List<PlFormula>> powersetIterative(ModelRank rank, boolean startSmall) {

        var rankList = toFormulaList(rank);

        List<List<PlFormula>> result = new ArrayList<>();
        int n = rankList.size();
        int totalSubsets = 1 << n; // 2^n

        for (int i = 1; i < totalSubsets; i++) {
            List<PlFormula> subset = new ArrayList<>();
            for (int j = 0; j < n; j++) {
                // Check if jth bit is set in i
                if ((i & (1 << j)) != 0) {
                    subset.add(rankList.get(j));
                }
            }
            result.add(subset);
        }

        result.sort(Comparator.comparingInt(List::size));

        if (!startSmall) {
            result = result.reversed();
        }
        return result;
    }

    public static List<KnowledgeBase> toPowerSetOrdered(ModelRankCollection rankCollection) {

        rankCollection.sort(Comparator.comparingInt(ModelRank::getRankNumber));

        List<ModelFormulaRanked> entries = new ArrayList<>();

        for (ModelRank rank : rankCollection) {

            for (PlFormula formula : rank.getFormulas()) {
                entries.add(new ModelFormulaRanked(rank.getRankNumber() + 1, formula));
            }
        }

        // Generate subsets of ModelFormulaRanked
        List<List<ModelFormulaRanked>> subsets = new ArrayList<>();
        powersetGenerate(0, entries, new ArrayList<>(), subsets);

        // Sort formulas inside each subset by rank descending
        for (List<ModelFormulaRanked> subset : subsets) {
            subset.sort(
                    Comparator.comparingInt(ModelFormulaRanked::rank).reversed()
                            .thenComparing(m -> m.formula().toString())
            );
        }

        // Sort subsets
        subsets.sort((a, b) -> {
            // 1. Size descending
            int cmpSize = Integer.compare(b.size(), a.size());
            if (cmpSize != 0) {
                return cmpSize;
            }

            // 2. Lexicographic comparison of ranks
            for (int i = 0; i < a.size(); i++) {
                int cmpRank = Integer.compare(b.get(i).rank(), a.get(i).rank());
                if (cmpRank != 0) {
                    return cmpRank;
                }
            }

            // 3. Tie-breaker: string comparison of formulas
            for (int i = 0; i < a.size(); i++) {
                int cmpFormula = a.get(i).formula().toString()
                        .compareTo(b.get(i).formula().toString());
                if (cmpFormula != 0) {
                    return cmpFormula;
                }
            }

            return 0;
        });

        // Convert to List<List<PlFormula>>
        var resultList = subsets.stream()
                .map(list -> list.stream()
                .map(ModelFormulaRanked::formula)
                .collect(Collectors.toList()))
                .collect(Collectors.toList());

        List<KnowledgeBase> finalKb = new ArrayList<>();
        for (List<PlFormula> subset : resultList) {
            if (subset.isEmpty()) {
                continue;
            }
            KnowledgeBase kb = new KnowledgeBase();
            for (PlFormula formula : subset) {
                kb.add(formula);
            }

            finalKb.add(kb);
        }

        int counter = 1;
        DisplayUtils.LogDebug(_logger, String.format("=>Powersets of %s", rankCollection.getKnowledgeBase()));
        for (KnowledgeBase subset : finalKb) {
            DisplayUtils.LogDebug(_logger, String.format("  --> %s: %s", counter, subset));
            counter++;
        }

        return finalKb;
    }

    // Standard power-set generator
    private static void powersetGenerate(
            int index,
            List<ModelFormulaRanked> entries,
            List<ModelFormulaRanked> current,
            List<List<ModelFormulaRanked>> result
    ) {
        if (index == entries.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        // Exclude
        powersetGenerate(index + 1, entries, current, result);

        // Include
        current.add(entries.get(index));
        powersetGenerate(index + 1, entries, current, result);
        current.remove(current.size() - 1);  // backtrack
    }

    public static ModelRank removeFormulasFromRank(ModelRank currentRank, KnowledgeBase knowledgeBase) {

        ModelRank resultRank = new ModelRank(currentRank.getRankNumber());

        var materialKb = toMaterialisedKnowledgeBase(knowledgeBase);

        for (PlFormula formula : currentRank.getFormulas()) {

            var materialFormula = toMaterialisedFormula(formula);

            if (!materialKb.contains(materialFormula)) {
                resultRank.addFormula(formula);
            }
        }

        return resultRank;
    }
}
