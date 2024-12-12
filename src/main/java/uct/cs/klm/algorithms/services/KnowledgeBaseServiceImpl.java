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
        Proposition p = new Proposition("p");
        Proposition b = new Proposition("b");
        Proposition f = new Proposition("f");
        Proposition w = new Proposition("w");

        KnowledgeBase kb = new KnowledgeBase();
        kb.add(new Implication(p, b));
        kb.add(new DefeasibleImplication(b, f));
        kb.add(new DefeasibleImplication(b, w));
        kb.add(new DefeasibleImplication(p, new Negation(f)));
        return kb;
    }

    @Override
    public KnowledgeBase getKnowledgeBase() {
        return getDefault();
    }

    @Override
    public KnowledgeBase getKnowledgeBase(String kbFilePath) throws Exception, IOException {

       return new DefeasibleParser().parseFormulasFromFile(kbFilePath);
    }
      
}
