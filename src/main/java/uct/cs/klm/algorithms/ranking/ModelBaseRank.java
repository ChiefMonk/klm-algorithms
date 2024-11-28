package uct.cs.klm.algorithms.ranking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
        return new ModelRankCollection(ranking);
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
}
