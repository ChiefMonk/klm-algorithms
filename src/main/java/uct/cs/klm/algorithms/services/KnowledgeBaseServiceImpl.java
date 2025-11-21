package uct.cs.klm.algorithms.services;

import java.io.IOException;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;

import uct.cs.klm.algorithms.models.DefeasibleImplication;
import uct.cs.klm.algorithms.models.KbGenerationInput;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.DefeasibleParser;

/**
 * This class represents a knowledge base service implementation for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class KnowledgeBaseServiceImpl implements IKnowledgeBaseService {

    private KnowledgeBase getDefault() {
        Proposition pets = new Proposition("pets");
        Proposition animals = new Proposition("animals");
        Proposition legs = new Proposition("legs");
        Proposition wild = new Proposition("wild");
                
        Proposition cats = new Proposition("cats");
        Proposition lions = new Proposition("lions");

        KnowledgeBase knowledgeBase = new KnowledgeBase();
       
        knowledgeBase.add(new DefeasibleImplication(animals, legs));
        knowledgeBase.add(new DefeasibleImplication(animals, wild));         
        knowledgeBase.add(new Implication(pets, animals));
        knowledgeBase.add(new DefeasibleImplication(pets, legs));
        knowledgeBase.add(new DefeasibleImplication(pets, new Negation(wild)));
        knowledgeBase.add(new Implication(lions, animals));
        knowledgeBase.add(new Implication(cats, pets));       
        
        return knowledgeBase;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return getDefault();
    }
     
    @Override    
     public KnowledgeBase generateKnowledgeBase(KbGenerationInput kbGenerationInput)
     {
          return getDefault();
     }

    @Override
    public KnowledgeBase getKnowledgeBase(
            String kbFilePath) throws Exception, IOException {

       return new DefeasibleParser().parseFormulasFromFile(kbFilePath);
    }
      
}
