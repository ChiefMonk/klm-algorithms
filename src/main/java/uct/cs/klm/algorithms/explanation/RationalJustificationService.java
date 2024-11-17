package uct.cs.klm.algorithms.explanation;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelHittingSetTree;
import uct.cs.klm.algorithms.models.ModelNode;
import uct.cs.klm.algorithms.models.Ranking;
import uct.cs.klm.algorithms.utils.DisplayUtils;

/**
 *
 * @author Chipo Hamayobe
 */
public class RationalJustificationService extends JustificationServiceBase implements IJustificationService 
{
    @Override
    public KnowledgeBase computeJustification(
            Ranking remainingRanking,
            PlFormula query) 
    {
        
        if(remainingRanking.getKnowledgeBase().isEmpty())
        {
            return new KnowledgeBase();
        }
        
        SatSolver.setDefaultSolver(new Sat4jSolver());
        SatReasoner reasoner = new SatReasoner();
        
        KnowledgeBase knowledgeBase = remainingRanking.getKnowledgeBase();

        // Construct root node
        KnowledgeBase rootJustification = super.computeSingleJustification(knowledgeBase, query, reasoner);
        ModelNode rootNode = new ModelNode(knowledgeBase, rootJustification);

        // Create a queue to keep track of nodes
        Queue<ModelNode> queue = new LinkedList<>();
        queue.add(rootNode);
        
        ModelHittingSetTree tree = new ModelHittingSetTree(rootNode);

        while (!queue.isEmpty()) 
        {
            ModelNode node = queue.poll();

            for (PlFormula formula : node.getJustification()) 
            {
                KnowledgeBase childKnowledgeBase = node.getKnowledgeBase().remove(formula);
                KnowledgeBase childJustification = super.computeSingleJustification(childKnowledgeBase, query, reasoner);
                ModelNode childNode = new ModelNode(childKnowledgeBase, childJustification);

                node.addChildNode(formula, childNode);
                tree.addNode(childNode);

                if (childJustification != null || childJustification.isEmpty()) 
                {
                    queue.add(childNode);
                }
            }
        }

        System.out.println("<<ALL possible classical justifications>>");
        List<KnowledgeBase> justifications = rootNode.getAllJustifications();
        
        for (KnowledgeBase justification : justifications)
        {
            System.out.println(DisplayUtils.printJustificationAsCSV(justification));
        }
        
        System.out.println("<<ALL justifications>>");
        System.out.println(DisplayUtils.printJustificationAsCSV(rootNode.getJustification()));
        
        return rootNode.getJustification();
    }
}
