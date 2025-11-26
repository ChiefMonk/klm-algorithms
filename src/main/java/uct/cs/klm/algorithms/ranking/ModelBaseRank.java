package uct.cs.klm.algorithms.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 * This class represents a model base rank for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelBaseRank {

    private final KnowledgeBase knowledgeBase;
    private final ModelRankCollection ranking;
    private final ModelRankCollection sequence;
    private final double timeTaken;

    public ModelBaseRank() {
        this(new KnowledgeBase(), new ModelRankCollection(), new ModelRankCollection(), 0);
    }

    public ModelBaseRank(KnowledgeBase knowledgeBase, ModelRankCollection sequence, ModelRankCollection ranking, double timeTaken) {
        this.sequence = sequence;
        this.ranking = ranking;
        this.timeTaken = timeTaken;
        this.knowledgeBase = knowledgeBase;
    }

    public ModelBaseRank(ModelBaseRank baseRank) {
        this(baseRank.getKnowledgeBaseKb(), baseRank.getSequence(), baseRank.getRanking(), baseRank.getTimeTaken());
    }

    public ModelRankCollection getRanking() {
        
        Collections.sort(ranking, Comparator.comparing(ModelRank::getRankNumber).reversed());
                     
        return ranking;
    }

    public ModelRankCollection getSequence() {
        return new ModelRankCollection(sequence);
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    public KnowledgeBase getKnowledgeBaseKb() {
        return new KnowledgeBase(knowledgeBase);
    }
    
    public ArrayList<String> getKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(ranking, getKnowledgeBaseKb());
    }
    
    @Override
    public String toString() {       
       return getRanking().toString();
    }
}
