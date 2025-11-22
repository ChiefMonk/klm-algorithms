package uct.cs.klm.algorithms.relevant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.utils.Symbols;
import uct.cs.klm.algorithms.ranking.*;

/**
 * This class represents a model relevance result for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRelevanceResult {
    
    private final ModelRankCollection _inconsistentRanking;
    private final ModelRankCollection _relevantRanking;
    private final ModelRankCollection _irrelevantRanking;
    
    public ModelRelevanceResult(ModelRankCollection inconsistentRanking, ModelRankCollection relevantRanking, ModelRankCollection irrelevantRanking) {
    _inconsistentRanking = inconsistentRanking;
    _relevantRanking = relevantRanking;
    _irrelevantRanking = irrelevantRanking;
  }
    
     public ModelRankCollection getInconsistentRanking() {
        return _inconsistentRanking;
    }
    
    public ModelRankCollection getRelevantRanking() {
        return _relevantRanking;
    }
       
    public ModelRankCollection getCorrectRelevantRanking() {                
        return _relevantRanking.getRankingCollectonExcept(Symbols.INFINITY_RANK_NUMBER);
    }       
    
    public ModelRankCollection getIrrelevantRanking() {
        return _irrelevantRanking;
    }
    
    public ModelRankCollection getCorrectIrrelevantRanking() {
        
        ModelRank relInfinity = _relevantRanking.getRank(Symbols.INFINITY_RANK_NUMBER);
        if (relInfinity == null) {
            return _irrelevantRanking;
        }

        // Defensive copy once; then merge into its ∞-rank if present.
        ModelRankCollection merged = new ModelRankCollection(_irrelevantRanking.clone());
        ModelRank irrInfinity = merged.getRank(Symbols.INFINITY_RANK_NUMBER);
        if (irrInfinity != null) {
            irrInfinity.addFormulas(relInfinity.getFormulas());
        }
        // If the target ∞-rank doesn't exist, original behavior was to do nothing.

        return merged;
    }
}
