package uct.cs.klm.algorithms.models;

import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.ranking.ModelRank;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.*;

// Base class for Entailment
public abstract class Entailment {

    protected final KnowledgeBase knowledgeBase;
    protected final PlFormula queryFormula;
    protected final ModelRankCollection baseRanking;
    protected final ModelRankCollection _miniBaseRanking;
    protected final boolean entailed;
    protected final double timeTaken;
    protected final ModelRankCollection removedRanking;
    protected final ModelRankCollection _remainingRanking;
    protected final KnowledgeBase _entailmentKnowledgeBase;
    protected ArrayList<KnowledgeBase> _justification;

    protected Entailment(EntailmentBuilder<?> builder) {
        this.knowledgeBase = builder._knowledgeBase;
        this.queryFormula = builder._queryFormula;
        this.baseRanking = builder._baseRanking;
        this.entailed = builder._entailed;
        this.timeTaken = builder._timeTaken;
        this.removedRanking = builder._removedRanking;
        this._justification = new ArrayList<>();
        this._remainingRanking = builder._remainingRanking;
        this._entailmentKnowledgeBase = builder._entailmentKnowledgeBase;
        this._miniBaseRanking = builder._miniBaseRanking;
    }

    public KnowledgeBase getKnowledgeBase() {
        return knowledgeBase;
    }

    public PlFormula getQueryFormula() {
        return queryFormula;
    }

    public PlFormula getNegation() {
        return queryFormula == null ? null : new Negation(((Implication) queryFormula).getFirstFormula());
    }

    public ModelRankCollection getRemovedRanking() {
        return removedRanking;
    }

    public ModelRankCollection getRemainingRanking() {
        return _remainingRanking;
    }

    public ModelRankCollection getBaseRanking() {
        return baseRanking;
    }

    public KnowledgeBase getEntailmentKnowledgeBase() {
        return _entailmentKnowledgeBase;
    }

    public ModelRankCollection getRemainingRanks() {
        return baseRanking;
    }
    
     public ModelRankCollection getMiniBaseRanking() {
        return _miniBaseRanking;
    }

    public void setJustification(ArrayList<KnowledgeBase> justification) {
        if (justification != null && !justification.isEmpty()) {
            justification.sort(Comparator.comparingInt(a -> a.size()));
        }

        this._justification = justification;
    }

    public ArrayList<KnowledgeBase> getJustification() {
        return _justification;
    }

    public ModelRankCollection getEntailmentRanking() {
        ModelRankCollection ranking = new ModelRankCollection();

        if (!getEntailed()) {
            return ranking;
        }

        for (ModelRank rank : getBaseRanking()) {
            Optional<ModelRank> result = getRemovedRanking().stream()
                    .filter(p -> rank.getFormulas().equals(p.getFormulas()))
                    .findFirst();

            // If the result is empty, add the rank to the ranking list
            if (!result.isPresent()) {
                ranking.add(rank);
            }
        }

        return baseRanking;
    }

    public boolean getEntailed() {
        return entailed;
    }

    public double getTimeTaken() {
        return timeTaken;
    }

    // Builder for Entailment
    public static abstract class EntailmentBuilder<T extends EntailmentBuilder<T>> {

        private KnowledgeBase _knowledgeBase;
        private PlFormula _queryFormula;
        private ModelRankCollection _baseRanking;
        private ModelRankCollection _miniBaseRanking;
        private boolean _entailed;
        private double _timeTaken;
        private ModelRankCollection _removedRanking;
        private ModelRankCollection _remainingRanking;
        private KnowledgeBase _entailmentKnowledgeBase;

        public T withRemovedRanking(ModelRankCollection removedRanking) {

            if (removedRanking != null && !removedRanking.isEmpty()) {
                removedRanking.sort(Comparator.comparing(ModelRank::getRankNumber).reversed());
            }

            _removedRanking = removedRanking;
            return self();
        }

        public T withRemainingRanking(ModelRankCollection remainingRanking) {

            if (remainingRanking != null && !remainingRanking.isEmpty()) {
                remainingRanking.sort(Comparator.comparing(ModelRank::getRankNumber).reversed());
            }

            _remainingRanking = remainingRanking;
            return self();
        }

        public T withEntailmentKnowledgeBase(KnowledgeBase entailmentKnowledgeBase) {
            _entailmentKnowledgeBase = entailmentKnowledgeBase;
            return self();
        }

        public T withKnowledgeBase(KnowledgeBase knowledgeBase) {
            _knowledgeBase = knowledgeBase;
            return self();
        }

        public T withQueryFormula(PlFormula queryFormula) {
            _queryFormula = queryFormula;
            return self();
        }

        public T withBaseRanking(ModelRankCollection baseRanking) {

            if (baseRanking != null && !baseRanking.isEmpty()) {
                baseRanking.sort(Comparator.comparing(ModelRank::getRankNumber).reversed());
            }

            _baseRanking = baseRanking;
            return self();
        }
        
        public T withMiniBaseRanking(ModelRankCollection miniBaseRanking) {

            if (miniBaseRanking != null && !miniBaseRanking.isEmpty()) {
                miniBaseRanking.sort(Comparator.comparing(ModelRank::getRankNumber).reversed());
            }

            _miniBaseRanking = miniBaseRanking;
            return self();
        }

        public T withEntailed(boolean entailed) {
            _entailed = entailed;
            return self();
        }

        public T withTimeTaken(double timeTaken) {
            _timeTaken = timeTaken;
            return self();
        }

        protected abstract T self();

        public abstract Entailment build();
    }
}
