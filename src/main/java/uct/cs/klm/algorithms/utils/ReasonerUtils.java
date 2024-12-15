package uct.cs.klm.algorithms.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.models.*;
import uct.cs.klm.algorithms.ranking.*;

/**
 *
 * @author ChipoHamayobe
 */
public final class ReasonerUtils {

    public static PlFormula toMaterialisedFormula(PlFormula formula) {

        if (formula instanceof DefeasibleImplication defeasibleImplication) {
            return new Implication(defeasibleImplication.getFormulas());
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

    public static KnowledgeBase toMaterialisedKnowledgeBase(ArrayList<ModelRank> baseRank) {
        return toMaterialisedKnowledgeBase(toKnowledgeBase(baseRank).materialisedKnowledgeBase());
    }

    public static ModelRankCollection toModelRankCollection(ArrayList<ModelRank> baseRank) {

        ModelRankCollection result = new ModelRankCollection();

        baseRank.forEach(rank -> {
            result.add(rank);
        });

        return result;
    }

    public static KnowledgeBase toKnowledgeBase(ArrayList<ModelRank> baseRank) {

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
    
    public static ModelRankCollection toRemainingEntailmentRanks(ArrayList<ModelRank> baseRank, ModelRankCollection removedRanks) {

        if(removedRanks == null || removedRanks.isEmpty())
        {
            return new ModelRankCollection(baseRank);
        }
                
        for (var r : removedRanks) {
            baseRank.remove(0);
        }
        
        return new ModelRankCollection(baseRank);
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

    public static ArrayList<ModelRank> generateFormulaCombinations(ModelRank rank) {

        List<PlFormula> formulaList = toFormulaList(rank);

        if (formulaList.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<PlFormula>> combinations = new ArrayList<>();

        // Generate all possible combinations
        for (int i = 1; i <= rank.getFormulas().size(); i++) {
            combinations.addAll(generateFormulaCombinations(formulaList, i));
        }
        
        if(!combinations.contains(new ArrayList<PlFormula>()))
        {
           combinations.add(new ArrayList<>());
        }
        
        // Sort combinations by size in descending order
        combinations.sort((a, b) -> Integer.compare(b.size(), a.size()));

        ArrayList<ModelRank> modelRankings = new ArrayList<>();

        for (int i = 0; i < combinations.size(); i++) {

            ModelRank model = new ModelRank();

            for (PlFormula formula : combinations.get(i)) {
                model.addFormula(formula);
            }

            modelRankings.add(model);
        }
                      
        int counter = 0;
        System.out.println();
        System.out.println(String.format("Rank SubRanks for = %s:%s", rank.getRankNumber(), rank.getFormulas()));  
        for (ModelRank input : modelRankings) {
            System.out.println(String.format("%s : %s", counter, input.getFormulas()));  
            counter++;
        }

        return modelRankings;
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
