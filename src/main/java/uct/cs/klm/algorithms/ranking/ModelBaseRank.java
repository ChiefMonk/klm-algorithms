package uct.cs.klm.algorithms.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;
import uct.cs.klm.algorithms.models.KnowledgeBase;

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
        this(baseRank.getKnowledgeBase(), baseRank.getSequence(), baseRank.getRanking(), baseRank.getTimeTaken());
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

    public KnowledgeBase getKnowledgeBase() {
        return new KnowledgeBase(knowledgeBase);
    }
    
    @Override
    public String toString() {
        int rankNumber = 1;

        StringBuilder sb = new StringBuilder();
        
         ArrayList<ModelRank> ranks = (ArrayList<ModelRank>) ranking.stream()
        .sorted(Comparator.comparing(ModelRank::getRankNumber).reversed())
        .collect(Collectors.toList());

        for (ModelRank rank : ranks) {
           
            sb.append(rank.getRankNumber()).append(": ");
            sb.append(rank.getFormulas());   
            sb.append("\n");
            
            rankNumber++;
        }
        
        return sb.toString();
    }
}
