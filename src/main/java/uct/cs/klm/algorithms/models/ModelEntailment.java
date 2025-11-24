package uct.cs.klm.algorithms.models;

import uct.cs.klm.algorithms.ranking.ModelRankCollection;
import uct.cs.klm.algorithms.ranking.ModelRank;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.util.*;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 * This class represents a model entailment for a given query.
 *
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public abstract class ModelEntailment {

    protected KnowledgeBase _knowledgeBase;
    protected PlFormula _queryFormula;
    protected ModelRankCollection _baseRanking;
    protected boolean _entailed;
    protected double _timeTaken;

    protected double _justificationTime;

    protected ModelRankCollection _removedRanking;
    protected ModelRankCollection _remainingRanking;
    protected ModelRankCollection _relevantRanking;

    protected KnowledgeBase _entailmentKnowledgeBase;
    protected ArrayList<KnowledgeBase> _justification;
    protected ArrayList<ModelRankResponse> _powersetRanking;
    protected KnowledgeBase _relevantKnowledgeBase;
    protected ArrayList<KnowledgeBase> _relevantJustification;
    protected int _consistentRank;

    public ModelEntailment() {
    }

    protected ModelEntailment(EntailmentBuilder<?> builder) {
        _knowledgeBase = builder._knowledgeBase;
        _queryFormula = builder._queryFormula;
        _baseRanking = builder._baseRanking;
        _entailed = builder._entailed;
        _timeTaken = builder._timeTaken;
        _justificationTime = builder._justificationTime;
        _removedRanking = builder._removedRanking;
        _justification = new ArrayList<>();
        _remainingRanking = builder._remainingRanking;
        _entailmentKnowledgeBase = builder._entailmentKnowledgeBase;
        _relevantRanking = builder._relevantRanking;
        _powersetRanking = builder._powersetRanking;
        _consistentRank = builder._consistentRank;
        _relevantKnowledgeBase = builder._relevantKnowledgeBase;
        _relevantJustification = builder._relevantJustification;
    }

    public ArrayList<String> getKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(_baseRanking, _knowledgeBase);
    }

    public KnowledgeBase getKnowledgeBaseKb() {
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
        if (_removedRanking == null || _removedRanking.isEmpty()) {
            return new ModelRankCollection();
        }
        return _removedRanking;
    }

    public ModelRankCollection getRemainingRanking() {
        if (_remainingRanking == null || _remainingRanking.isEmpty()) {
            return new ModelRankCollection();
        }
        return _remainingRanking;
    }

    public ModelRankCollection getRelevantRanking() {
        if (_relevantRanking == null || _relevantRanking.isEmpty()) {
            return new ModelRankCollection();
        }
        return _relevantRanking;
    }

    public ModelRankCollection getBaseRanking() {
        return _baseRanking;
    }

    public ArrayList<String> getEntailmentKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(_baseRanking, _entailmentKnowledgeBase);
    }

    public KnowledgeBase getEntailmentKnowledgeBaseKb() {
        return _entailmentKnowledgeBase;
    }

    public ArrayList<String> getDecidingKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(_baseRanking, _entailmentKnowledgeBase);
    }

    public ArrayList<String> getRelevantKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(_baseRanking, _relevantKnowledgeBase);
    }

    public ArrayList<String> getRemovedKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(_baseRanking, getRemovedRanking().getKnowledgeBase());
    }

    public ArrayList<String> getRemainingKnowledgeBase() {
        return ReasonerUtils.toResponseKnowledgebase(_baseRanking, getRemainingRanking().getKnowledgeBase());
    }

    public ModelRankCollection getRemainingRanks() {
        return _baseRanking;
    }

    public ArrayList<ModelRankResponse> getPowersetRanking() {
        return _powersetRanking;
    }

    public int getConsistentRank() {
        return _consistentRank;
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

    public void setJustificationTime(double justificationTime) {
        _justificationTime = justificationTime;
    }

    public double getJustificationTime() {
        return _justificationTime;
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

    public void setRelevantKnowledgeBase(KnowledgeBase relevantKnowledgeBase) {
        _relevantKnowledgeBase = relevantKnowledgeBase;
    }

    public void setPowersetRanking(ArrayList<ModelRankResponse> powersetRanking) {
        _powersetRanking = powersetRanking;
    }

    public void setJustification(ArrayList<KnowledgeBase> justification) {
        if (justification != null && !justification.isEmpty()) {
            justification.sort(Comparator.comparingInt(a -> a.size()));
        }

        _justification = justification;
    }

    public ArrayList<ArrayList<String>> getJustification() {

        ArrayList<ArrayList<String>> just = new ArrayList<>();

        for (var kb : _justification) {
            just.add(ReasonerUtils.toResponseKnowledgebase(_baseRanking, kb));
        }

        return just;
    }

    public ArrayList<KnowledgeBase> getJustificationKb() {
        
        if (_justification == null || _justification.isEmpty()) {
            return new ArrayList<>();
        }
        
        return _justification;
    }

    public void setRelevantJustification(ArrayList<KnowledgeBase> relevantJustification) {
        if (relevantJustification != null && !relevantJustification.isEmpty()) {
            relevantJustification.sort(Comparator.comparingInt(a -> a.size()));
        }

        _relevantJustification = relevantJustification;
    }

    public ArrayList<ArrayList<String>> getRelevantJustification() {

        if (_relevantJustification == null || _relevantJustification.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayList<ArrayList<String>> just = new ArrayList<>();

        for (var kb : _relevantJustification) {
            just.add(ReasonerUtils.toResponseKnowledgebase(_baseRanking, kb));
        }

        return just;
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
        private int _consistentRank;
        private ModelRankCollection _baseRanking;
        private boolean _entailed;
        private double _timeTaken;
        private double _justificationTime;

        private ModelRankCollection _removedRanking;
        private ModelRankCollection _remainingRanking;
        private KnowledgeBase _entailmentKnowledgeBase;
        private ModelRankCollection _relevantRanking;
        private ArrayList<ModelRankResponse> _powersetRanking;
        private KnowledgeBase _relevantKnowledgeBase;
        private ArrayList<KnowledgeBase> _relevantJustification;

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

        public T withRelevantRanking(ModelRankCollection relevantRanking) {

            if (relevantRanking != null && !relevantRanking.isEmpty()) {
                relevantRanking.sort(Comparator.comparing(ModelRank::getRankNumber).reversed());
            }

            _relevantRanking = relevantRanking;
            return self();
        }

        public T withEntailmentKnowledgeBase(KnowledgeBase entailmentKnowledgeBase) {
            _entailmentKnowledgeBase = entailmentKnowledgeBase;
            return self();
        }

        public T withRelevantKnowledgeBase(KnowledgeBase relevantKnowledgeBase) {
            _relevantKnowledgeBase = relevantKnowledgeBase;
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

        public T withConsistentRank(int consistentRank) {
            _consistentRank = consistentRank;
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

        public T withJustificationTime(double justificationTime) {
            _justificationTime = justificationTime;
            return self();
        }

        public T withPowersetRanking(ArrayList<ModelRankResponse> powersetRanking) {
            _powersetRanking = powersetRanking;
            return self();
        }

        public T withRelevantJustification(ArrayList<KnowledgeBase> relevantJustification) {
            _relevantJustification = relevantJustification;
            return self();
        }

        protected abstract T self();

        public abstract ModelEntailment build();
    }
}
