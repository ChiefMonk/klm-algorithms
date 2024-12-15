package uct.cs.klm.algorithms.explanation;

import java.util.LinkedList;
import java.util.Queue;

import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import uct.cs.klm.algorithms.models.KnowledgeBase;
import uct.cs.klm.algorithms.models.ModelHittingSetTree;
import uct.cs.klm.algorithms.models.ModelNode;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 *
 * @author Chipo Hamayobe
 */
public class LexicographicJustificationService extends JustificationServiceBase implements IJustificationService 
{   
    @Override
    public KnowledgeBase computeJustification(
            KnowledgeBase remainingKnowledgeBase, 
            PlFormula queryFormula) 
    {        
        System.out.println();
        System.out.println("==> LC Justifications : ");
        System.out.println(String.format("Query: %s", queryFormula));
         
        if(remainingKnowledgeBase == null || remainingKnowledgeBase.isEmpty())
        {
            System.out.println("KB = {}");
             System.out.println("J = {}");
            return new KnowledgeBase();
        }
        
        SatSolver.setDefaultSolver(new Sat4jSolver());
        SatReasoner reasoner = new SatReasoner();
        
        remainingKnowledgeBase = ReasonerUtils.toMaterialisedKnowledgeBase(remainingKnowledgeBase);
        queryFormula = ReasonerUtils.toMaterialisedFormula(queryFormula);
        
        System.out.println(String.format("KB = %s", remainingKnowledgeBase));
               
        // Construct root node
        KnowledgeBase rootJustification = super.computeSingleJustification(remainingKnowledgeBase, queryFormula, reasoner);
        ModelNode rootNode = new ModelNode(remainingKnowledgeBase, rootJustification);

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
                KnowledgeBase childJustification = super.computeSingleJustification(childKnowledgeBase, queryFormula, reasoner);
                ModelNode childNode = new ModelNode(childKnowledgeBase, childJustification);

                node.addChildNode(formula, childNode);
                tree.addNode(childNode);

                if (childJustification != null && childJustification.isEmpty()) 
                {
                    queue.add(childNode);
                }
            }
        }
       
        
        System.out.println(String.format("J = %s", rootNode.getJustification()));
        
        return rootNode.getJustification();
    }
}
