package uct.cs.klm.algorithms.services;

import java.io.IOException;
import org.tweetyproject.logics.pl.syntax.Implication;
import org.tweetyproject.logics.pl.syntax.Negation;
import org.tweetyproject.logics.pl.syntax.Proposition;

import uct.cs.klm.algorithms.models.DefeasibleImplication;
import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.utils.DefeasibleParser;

public class KnowledgeBaseServiceImpl implements IKnowledgeBaseService {

    private KnowledgeBase getDefault() {
        Proposition birds = new Proposition("b");
        Proposition wings = new Proposition("w");
        Proposition fly = new Proposition("f");
        Proposition penguins = new Proposition("p");
       
        Proposition tweety = new Proposition("t");
        Proposition skipper = new Proposition("s");

        KnowledgeBase knowledgeBase = new KnowledgeBase();
       
        knowledgeBase.add(new DefeasibleImplication(birds, wings));
        knowledgeBase.add(new DefeasibleImplication(birds, fly));         
        knowledgeBase.add(new Implication(penguins, birds));
        knowledgeBase.add(new DefeasibleImplication(penguins, wings));
        knowledgeBase.add(new DefeasibleImplication(penguins, new Negation(fly)));
        knowledgeBase.add(new Implication(tweety, birds));
        knowledgeBase.add(new Implication(skipper, penguins));       
        
        return knowledgeBase;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return getDefault();
    }
     
    @Override    
     public KnowledgeBase generateKnowledgeBase()
     {
          return getDefault();
     }

    @Override
    public KnowledgeBase getKnowledgeBase(
            String kbFilePath) throws Exception, IOException {

       return new DefeasibleParser().parseFormulasFromFile(kbFilePath);
    }
      
}
