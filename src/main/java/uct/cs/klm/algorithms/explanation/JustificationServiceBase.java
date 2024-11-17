package uct.cs.klm.algorithms.explanation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.Proposition;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;


/**
 *
 * @author Chipo Hamayobe
 */
public abstract class JustificationServiceBase 
{         
    protected KnowledgeBase computeSingleJustification(
            KnowledgeBase entailmentKb, 
            PlFormula query, 
            SatReasoner reasoner)
    {
        KnowledgeBase result = new KnowledgeBase();
               
        if (entailmentKb.contains(query))
        {
            result.add(query);
            return result;
        }
        
        result = expandFormulas(entailmentKb, query, reasoner);

        if (result.isEmpty())
            return result;
        
        result = contractFormuls(result, query, reasoner);

        return result;
    }
    
    private KnowledgeBase expandFormulas(
            KnowledgeBase knowledgeBase, 
            PlFormula query, 
            SatReasoner reasoner) 
    {
        KnowledgeBase result = new KnowledgeBase();
        
        if (!reasoner.query(knowledgeBase, query))
        {
            return new KnowledgeBase();
        }
        
        KnowledgeBase sPrime = new KnowledgeBase();
        List<Proposition> sigma = getSignature(query);
        
        while (result != sPrime)
        {
            sPrime = result;
            result = result.union(findRelatedFormulas(sigma, knowledgeBase));
            PlBeliefSet resultKownledgeBase = new PlBeliefSet(result);
            
            if (reasoner.query(resultKownledgeBase, query))
            {
                return result;
            }
                
            sigma = getSignature(result);
        }
        
        return result;
    }
    
    private KnowledgeBase findRelatedFormulas(
            List<Proposition> signatures, 
            KnowledgeBase knowledgeBase)
    {
        KnowledgeBase result = new KnowledgeBase();
        
        for(PlFormula formula: knowledgeBase)
        {
            if (!Collections.disjoint(getSignature(formula), signatures))
                result.add(formula);
        }
        
        return result;
    }
    
    private List<Proposition> getSignature(
            KnowledgeBase formulas)
    {
        List<Proposition> result = new ArrayList<>();
        
        for (PlFormula formula: formulas)
        {
            List<Proposition> signature = getSignature(formula);
            for (Proposition atom : signature)
            {
                if (!result.contains(atom))
                    result.add(atom);
            }
        }
        return result;
    }
    
    private List<Proposition> getSignature(
            PlFormula query)
    {
        List<Proposition> result = new ArrayList<>();
        Set<Proposition> atoms = query.getAtoms();
        result.addAll(atoms);
        return result;
    }
       
    private KnowledgeBase contractFormuls(
            KnowledgeBase result, 
            PlFormula query, 
            SatReasoner reasoner) 
    {
        return contractRecursive(new KnowledgeBase(), result, query, reasoner);
    }
    
    private KnowledgeBase contractRecursive(
            KnowledgeBase support, 
            KnowledgeBase whole, 
            PlFormula query, 
            SatReasoner reasoner) 
    {
        if (whole.size() == 1)
        {
            return whole;
        }
        
        List<KnowledgeBase> splitList = split(whole);
        KnowledgeBase left = splitList.get(0);
        KnowledgeBase right = splitList.get(1);
        
        KnowledgeBase leftUnion = support.union(left);
        PlBeliefSet leftKB = new PlBeliefSet(leftUnion);
        
        KnowledgeBase rightUnion = support.union(right);
        PlBeliefSet rightKB = new PlBeliefSet(rightUnion);
        
        if (reasoner.query(leftKB, query))
            return contractRecursive(support, left, query, reasoner);
        if (reasoner.query(rightKB, query))
            return contractRecursive(support, right, query, reasoner);
        
        KnowledgeBase leftPrime = contractRecursive(rightUnion, left, query, reasoner);
        KnowledgeBase leftPrimeUnion = support.union(leftPrime);
        KnowledgeBase rightPrime = contractRecursive(leftPrimeUnion, right, query, reasoner);
        
        return leftPrime.union(rightPrime);
    }
    
    private List<KnowledgeBase> split(
            KnowledgeBase whole)
    {              
        List<PlFormula> kbList = new ArrayList<>();   
        
        for(PlFormula formula : whole)
        {
            kbList.add(formula);
        }
        
        int length = kbList.size();
        int halfLength = length / 2;
        
              
        KnowledgeBase left = new KnowledgeBase(kbList.subList(0, halfLength));
        KnowledgeBase right = new KnowledgeBase(kbList.subList(halfLength, length));
        
        List<KnowledgeBase> result = new ArrayList<>();
              
        result.add(left);
        result.add(right);
        
        return result;
    }
}
