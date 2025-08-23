package uct.cs.klm.algorithms.ranking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.Symbols;

/**
 * This class represents ranking of formulas.
 */
public class ModelRankCollection extends ArrayList<ModelRank> implements Cloneable {
  
    /**
     * Constructs an empty ranking.
     */
    public ModelRankCollection() {
        super();
    }

    /**
     * Constructs a ranking from a collection of ranks.
     *
     * @param ranks
     */
    public ModelRankCollection(Collection<? extends ModelRank> ranks)  {
        super(ranks);
    }
        
    /**
     * Constructs a ranking from a collection of ranks.
     *
     * @param rank
     */
    public ModelRankCollection(ModelRank rank) {
        super();
        this.add(rank);
    } 
    
    // Overriding clone() method
    @Override
    public ModelRankCollection clone() {
        return (ModelRankCollection) super.clone();
    }

    /**
     * Create and add new rank given a rank number and knowledge base of
     * formulas.
     *
     * @param rankNumber ModelRank number.
     * @param knowledgeBase Knowledge base of formulas.
     */
    public void addRank(int rankNumber, KnowledgeBase knowledgeBase) {
        this.add(new ModelRank(rankNumber, knowledgeBase));
    }

    /**
     * Get the rank given the rank number.
     *
     * @param rankNumber ModelRank number.
     * @return
     */
    public ModelRank getRank(int rankNumber) {
        Optional<ModelRank> result = this.stream()
                .filter(p -> p.getRankNumber() == rankNumber)
                .findFirst();

        // If result is empty, return null; otherwise, return the value
        return result.orElse(null);
    }
    
    public ModelRank getInfinityRank() {
       return getRank(Symbols.INFINITY_RANK_NUMBER);
    }

    public KnowledgeBase getKnowledgeBase() {
        KnowledgeBase kb = new KnowledgeBase();

        for (ModelRank rank : this) {
            kb.addAll(rank.getFormulas());
        }

        return kb;
    }
    
    public KnowledgeBase getKnowledgeBaseExcept(int exceptRank) {       
        return getRankingCollectonExcept(exceptRank).getKnowledgeBase();
    }
    
    public ModelRankCollection getRankingCollectonExcept(int exceptRank) {
        
        ModelRankCollection ranking = new ModelRankCollection();
        for (ModelRank rank : this) {            
            if(rank.getRankNumber() == exceptRank) {
                continue;                               
            }            
            
            ranking.add(rank);
        }

        return ranking;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        ArrayList<ModelRank> ranks = (ArrayList<ModelRank>) this.stream()
                .sorted(Comparator.comparing(ModelRank::getRankNumber).reversed())
                .collect(Collectors.toList());

        for (ModelRank rank : ranks) {
            String rankNumber = String.valueOf(rank.getRankNumber());
            
            if(rank.getRankNumber() == Symbols.INFINITY_RANK_NUMBER)
            {
                rankNumber = "α"; 
            }
                                 
            sb.append(rankNumber).append(": ");
            sb.append(rank.getFormulas());
            sb.append("\n");
        }

        return sb.toString();
    }
}
