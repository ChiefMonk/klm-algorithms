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
  protected final boolean entailed;
  protected final double timeTaken;
  protected final ModelRankCollection removedRanking;
  protected final KnowledgeBase _entailmentKnowledgeBase;
   
  protected KnowledgeBase justification;

  protected Entailment(EntailmentBuilder<?> builder) {
    this.knowledgeBase = builder.knowledgeBase;
    this.queryFormula = builder.queryFormula;
    this.baseRanking = builder.baseRanking;
    this.entailed = builder.entailed;
    this.timeTaken = builder.timeTaken;
    this.removedRanking = builder.removedRanking;
    this.justification = new KnowledgeBase();
    this._entailmentKnowledgeBase = builder._entailmentKnowledgeBase;
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

  public ModelRankCollection getBaseRanking() {
    return baseRanking;
  }
  
   public KnowledgeBase getEntailmentKnowledgeBase() {
    return _entailmentKnowledgeBase;
  }
   
  public ModelRankCollection getRemainingRanks() {
    return baseRanking;
  }
  
  public void setJustification(KnowledgeBase justification) 
  {
    if(!getEntailed())
    {
        return;
    }
    
    this.justification = justification;
  }
  
  public KnowledgeBase getJustification() 
  {
    return justification;
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
    private KnowledgeBase knowledgeBase;
    private PlFormula queryFormula;
    private ModelRankCollection baseRanking;
    private boolean entailed;
    private double timeTaken;
    private ModelRankCollection removedRanking;
    private KnowledgeBase _entailmentKnowledgeBase;

    public T withRemovedRanking(ModelRankCollection removedRanking) {
      this.removedRanking = removedRanking;
      return self();
    }
    
     public T withEntailmentKnowledgeBase(KnowledgeBase entailmentKnowledgeBase) {
      this._entailmentKnowledgeBase = entailmentKnowledgeBase;
      return self();
    }

    public T withKnowledgeBase(KnowledgeBase knowledgeBase) {
      this.knowledgeBase = knowledgeBase;
      return self();
    }

    public T withQueryFormula(PlFormula queryFormula) {
      this.queryFormula = queryFormula;
      return self();
    }

    public T withBaseRanking(ModelRankCollection baseRanking) {
      this.baseRanking = baseRanking;
      return self();
    }

    public T withEntailed(boolean entailed) {
      this.entailed = entailed;
      return self();
    }

    public T withTimeTaken(double timeTaken) {
      this.timeTaken = timeTaken;
      return self();
    }

    protected abstract T self();

    public abstract Entailment build();
  }
}
