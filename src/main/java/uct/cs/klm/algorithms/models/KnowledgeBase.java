package uct.cs.klm.algorithms.models;

import java.util.Collection;
import java.util.Set;

import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.ranking.ModelRank;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 * This class represents a defeasible knowledge base of propositional formulae.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za
 */

public class KnowledgeBase extends PlBeliefSet {    
    /**
     * Creates new (empty) knowledge base.
     */
    public KnowledgeBase() {
        super();
    }

    /**
     * Creates a new knowledge base with the given set of formulas.
     *
     * @param formulas A set of formulas.
     */
    public KnowledgeBase(Collection<? extends PlFormula> formulas) {
        super(formulas);
    }
    
    public KnowledgeBase(KnowledgeBase knowledgeBase) {
         this(knowledgeBase.formulas);                 
    }
     
    public Set<PlFormula> getFormulas()
    {
         return this.formulas;
    }       
     
     public boolean removeFormula(PlFormula formula) {
      
      if(this.formulas.isEmpty())
       {
           return true;
       }
      
      if(this.formulas.remove(formula))
      {
          return true;
      }
      
      if(this.formulas.remove(ReasonerUtils.toMaterialisedFormula(formula)))
      {
          return true;
      }
      
      return this.formulas.remove(ReasonerUtils.toDematerialisedFormula(formula));
  }
     
     public void addKnowledgeBase(KnowledgeBase knowledgeBase) {       
        this.addAll(knowledgeBase);       
    }
               

    /**
     * Computes union of this knowledge base with a collection of other
     * knowledge bases.
     *
     * @param knowledgeBases Set of knowledge bases.
     * @return Knowledge base representing the union.
     */
    public KnowledgeBase union(
            Collection<KnowledgeBase> knowledgeBases) {
        KnowledgeBase result = new KnowledgeBase();

        knowledgeBases.forEach(kb
                -> {
            result.addAll(kb);
        });

        return result;
    }

    /**
     * Computes the intersection of this knowledge base and other knowledge
     * base.
     *
     * @param knowledgeBase Other knowledge base.
     * @return Knowledge base representing the intersection.
     */
    public KnowledgeBase intersection(
            KnowledgeBase knowledgeBase) {
        KnowledgeBase result = new KnowledgeBase();

        this.forEach(formula
                -> {
            if (knowledgeBase.contains(formula)) {
                result.add(formula);
            }
        });

        return result;
    }

    /**
     * Computes a set difference of this knowledge base and other knowledge
     * base.
     *
     * @param knowledgeBase other knowledge base.
     * @return Knowledge base representing the set difference.
     */
    public KnowledgeBase difference(KnowledgeBase knowledgeBase) {
        KnowledgeBase result = new KnowledgeBase(this);
        result.removeAll(knowledgeBase);
        return result;
    }

    /**
     * Convert the defeasible implication statements to classical implication.
     *
     * @return Materialization of this knowledge base.
     */
    public KnowledgeBase materialise() {
        KnowledgeBase result = new KnowledgeBase();
        this.forEach(formula -> {
            if (formula instanceof DefeasibleImplication defeasibleImplication) {
                result.add(new Implication(defeasibleImplication.getFormulas()));
            }
        });
        return result;
    }
    
    public KnowledgeBase materialisedKnowledgeBase() {
        KnowledgeBase result = new KnowledgeBase();
        this.forEach(formula -> {
            if (formula instanceof DefeasibleImplication defeasibleImplication) {
                result.add(new Implication(defeasibleImplication.getFormulas()));
            }
            else
            {
                 result.add(formula);
            }
        });
        return result;
    }

    /**
     * Convert the classical implication statements to defeasible implication.
     *
     * @return Dematerialisation of this knowledge base.
     */
    public KnowledgeBase dematerialise() {
        KnowledgeBase result = new KnowledgeBase();
        this.forEach(formula -> {
            if ((formula instanceof Implication implication) && !(formula instanceof DefeasibleImplication)) {
                result.add(new DefeasibleImplication(implication.getFormulas()));
            }
        });
        return result;
    }

    /**
     * Separates the knowledge base into defeasbile and classical statemetents.
     *
     * @return KnowledgeBase array where index 0 is defeasible knowledge base
     * and index 1 is classical knowledge base.
     */
    public KnowledgeBase[] separate() {
        KnowledgeBase defeasible = new KnowledgeBase();
        KnowledgeBase classical = new KnowledgeBase();
        this.forEach(formula -> {
            if (formula instanceof DefeasibleImplication) {
                defeasible.add(formula);
            } else {
                classical.add(formula);
            }
        });
        return new KnowledgeBase[]{defeasible, classical};
    }
    
    public KnowledgeBase getClassicalFormulas() {      
        KnowledgeBase classical = new KnowledgeBase();
        this.forEach(formula -> {
            if (!(formula instanceof DefeasibleImplication)) {
                 classical.add(formula);
            }
        });
        return classical;
    }
    
    public KnowledgeBase getDefeasibleFormulas() {      
        KnowledgeBase classical = new KnowledgeBase();
        this.forEach(formula -> {
            if (formula instanceof DefeasibleImplication) {
                 classical.add(formula);
            }
        });
        return classical;
    }

    public boolean remove(PlFormula formula) {      
        return this.formulas.remove(formula);        
    }
    
    public void removeAll(KnowledgeBase knowledgeBase) {
        
        KnowledgeBase result = new KnowledgeBase(this);
        
        for(PlFormula formula : knowledgeBase)
        {
            this.formulas.remove(formula);
        }       
    }
    
    public void removeAll(ModelRank rank) {        
       removeAll(rank.getFormulas());
    }   
}
