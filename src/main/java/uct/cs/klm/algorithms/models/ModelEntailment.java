package uct.cs.klm.algorithms.models;

import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.ranking.ModelRank;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.*;

// Base class for ModelEntailment
public abstract class ModelEntailment {

    protected KnowledgeBase _knowledgeBase;
    protected PlFormula _queryFormula;
    protected ModelRankCollection _baseRanking;   
    protected boolean _entailed;
    protected double _timeTaken;
    
    protected ModelRankCollection _removedRanking;       
    protected ModelRankCollection _remainingRanking; 
    
    protected KnowledgeBase _removedKnowledgeBase;       
    protected KnowledgeBase _remainingKnowledgeBase;   
      
    protected KnowledgeBase _entailmentKnowledgeBase;       
    protected ArrayList<KnowledgeBase> _justification;

    public  ModelEntailment() {}

    protected ModelEntailment(EntailmentBuilder<?> builder) {
        _knowledgeBase = builder._knowledgeBase;
        _queryFormula = builder._queryFormula;
        _baseRanking = builder._baseRanking;
        _entailed = builder._entailed;
        _timeTaken = builder._timeTaken;
        _removedRanking = builder._removedRanking;
        _justification = new ArrayList<>();
        _remainingRanking = builder._remainingRanking;
        _entailmentKnowledgeBase = builder._entailmentKnowledgeBase;        
    }

    public KnowledgeBase getKnowledgeBase() {
        return _knowledgeBase;
    }

    public PlFormula getQueryFormula() {
        return _queryFormula;
    }

    public PlFormula getNegation() {
        return _queryFormula == null 
                ? null 
                : new Negation(((Implication) _queryFormula).getFirstFormula());
    }

    public ModelRankCollection getRemovedRanking() {
        return _removedRanking;
    }

    public ModelRankCollection getRemainingRanking() {
        return _remainingRanking;
    }

    public ModelRankCollection getBaseRanking() {
        return _baseRanking;
    }

    public KnowledgeBase getEntailmentKnowledgeBase() {
        return _entailmentKnowledgeBase;
    }

    public ModelRankCollection getRemainingRanks() {
        return _baseRanking;
    }
    
    public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
        _knowledgeBase = knowledgeBase;
    }

    public void setQueryFormula(PlFormula queryFormula) {
        _queryFormula = queryFormula;
    }

    public void setBaseRanking(ModelRankCollection baseRanking) {
        _baseRanking = baseRanking;
    }
   
    public void setEntailed(boolean entailed) {
        _entailed = entailed;
    }

    public void setTimeTaken(double timeTaken) {
        _timeTaken = timeTaken;
    }

    public void setRemovedRanking(ModelRankCollection removedRanking) {
        _removedRanking = removedRanking;
    }

    public void setRemainingRanking(ModelRankCollection remainingRanking) {
        _remainingRanking = remainingRanking;
    }

    public void setEntailmentKnowledgeBase(KnowledgeBase entailmentKnowledgeBase) {
        _entailmentKnowledgeBase = entailmentKnowledgeBase;
    }

    public void setJustification(ArrayList<KnowledgeBase> justification) {
        if (justification != null && !justification.isEmpty()) {
            justification.sort(Comparator.comparingInt(a -> a.size()));
        }

        _justification = justification;
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

        return _baseRanking;
    }

    public boolean getEntailed() {
        return _entailed;
    }

    public double getTimeTaken() {
        return _timeTaken;
    }

    // Builder for ModelEntailment
    public static abstract class EntailmentBuilder<T extends EntailmentBuilder<T>> {

        private KnowledgeBase _knowledgeBase;
        private PlFormula _queryFormula;
        private ModelRankCollection _baseRanking;      
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
               
        public T withEntailed(boolean entailed) {
            _entailed = entailed;
            return self();
        }

        public T withTimeTaken(double timeTaken) {
            _timeTaken = timeTaken;
            return self();
        }

        protected abstract T self();

        public abstract ModelEntailment build();
    }
}
