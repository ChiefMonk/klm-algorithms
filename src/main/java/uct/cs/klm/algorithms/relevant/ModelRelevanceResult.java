package uct.cs.klm.algorithms.relevant;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.Symbols;
import uct.cs.klm.algorithms.ranking.*;

/**
 *
 * @author ChipoHamayobe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelRelevanceResult {
    
    private final ModelRankCollection _relevantRanking;
    private final ModelRankCollection _irrelevantRanking;
    
    public ModelRelevanceResult(ModelRankCollection relevantRanking, ModelRankCollection irrelevantRanking) {
    _relevantRanking = relevantRanking;
    _irrelevantRanking = irrelevantRanking;
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
