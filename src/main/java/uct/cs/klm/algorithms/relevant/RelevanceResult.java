package uct.cs.klm.algorithms.relevant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.utils.Symbols;

/**
 *
 * @author ChipoHamayobe
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelevanceResult {
    
    private final ModelRankCollection _relevantRanking;
    private final ModelRankCollection _irrelevantRanking;
    
    public RelevanceResult(ModelRankCollection relevantRanking, ModelRankCollection irrelevantRanking) {
    _relevantRanking = relevantRanking;
    _irrelevantRanking = irrelevantRanking;
  }
    
    public ModelRankCollection getRelevantRanking() {
        return _relevantRanking;
    }
    
    public KnowledgeBase getRelevantKb() {                
        return _relevantRanking.getKnowledgeBase();
    }
    
    public ModelRankCollection getCorrectRelevantRanking() {                
        return _relevantRanking.getRankingCollectonExcept(Symbols.INFINITY_RANK_NUMBER);
    }
    
    public KnowledgeBase getCorrectRelevantKb() {                
        return getCorrectRelevantRanking().getKnowledgeBase();
    }
    
    public ModelRankCollection getIrrelevantRanking() {
        return _irrelevantRanking;
    }
    
    public ModelRankCollection getCorrectIrrelevantRanking() {
        
       var relInfinity = _relevantRanking.getRank(Symbols.INFINITY_RANK_NUMBER);
       if(relInfinity == null)
       {
           return _irrelevantRanking;
       }
                
        ModelRankCollection baseRankCollection = new ModelRankCollection(_irrelevantRanking.clone());
        
        for(var rank : baseRankCollection)
        {
            if(rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER)
            {
                rank.addFormulas(relInfinity.getFormulas());
                break;
            }
        }
               
        return baseRankCollection;
    }
}
