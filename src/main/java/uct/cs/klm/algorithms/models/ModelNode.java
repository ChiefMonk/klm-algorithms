package uct.cs.klm.algorithms.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import uct.cs.klm.algorithms.utils.ReasonerUtils;

/**
 * This class represents a model node for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */

public class ModelNode {
    
    private final KnowledgeBase knowledgeBase;
    private KnowledgeBase justification;
    private Map<PlFormula, ModelNode> childrenNodes;

    public ModelNode(KnowledgeBase knowledgeBase)
    {
        this.knowledgeBase = knowledgeBase;
    }
    public ModelNode(KnowledgeBase knowledgeBase, KnowledgeBase justification)
    {
        this.knowledgeBase = knowledgeBase;
        this.justification = justification;
        this.childrenNodes = new HashMap<>();
        for (PlFormula formula : justification )
        {
            childrenNodes.put(formula, new ModelNode(ReasonerUtils.removeFormula(knowledgeBase, formula)));
        }
    }
    
    public KnowledgeBase getKnowledgeBase()
    {
        return this.knowledgeBase;
    }

    public void setJustification(KnowledgeBase justification)
    {
        this.justification = justification;
        this.childrenNodes = new HashMap<>();
        for (PlFormula formula : justification)
        {
            this.childrenNodes.put(formula, null);
        }
    }

    public KnowledgeBase getJustification()
    {
        return this.justification;
    }

    public void addChildNode(PlFormula formula, ModelNode node)
    {
        this.childrenNodes.put(formula, node);
    }
    
    public ModelNode getChildNode(PlFormula formula)
    {
        return this.childrenNodes.get(formula);
    }
    
    public void initialiseChildNodeMap()
    {
        for (PlFormula formula : justification )
        {
            childrenNodes.put(formula, null);
        }
    }
    
    public int getNumChildNodes()
    {
        return this.childrenNodes.size();
    }
    
    
    public ArrayList<KnowledgeBase> getAllJustifications()
    {
        ArrayList<KnowledgeBase> justifications = new ArrayList<>();
        
        if (this.childrenNodes != null && !this.childrenNodes.isEmpty())
        {
            for (PlFormula key : this.childrenNodes.keySet())
            {
                ModelNode childNode = this.childrenNodes.get(key);
                justifications.addAll(childNode.getAllJustifications());
            }
        }
        
        if (this.justification != null && !this.justification.isEmpty() && !justifications.contains(this.justification))
        {
            justifications.add(this.justification);
        }
        
        return justifications;
    }
    
    private String printKnowldegeBaseAsCSV()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (PlFormula formula : this.knowledgeBase)
        {
            stringBuilder.append(formula).append(", ");
        }
        String result = stringBuilder.toString();
        if (result != null && result != "" && result.length() > 2)
            return result.substring(0, result.length()-2);
        else
            return "NULL";
    }
    
    private String printJustificationAsCSV()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (PlFormula formula : justification)
        {
            stringBuilder.append(formula).append(", ");
        }
        String result = stringBuilder.toString();
        if (result != null && result != "" && result.length() > 2)
            return result.substring(0, result.length()-2);
        else
            return "NULL";
    }
    
    public String toString(String prefix)
    {
        StringBuilder stringBuilder = new StringBuilder(prefix);
        stringBuilder.append("<<Node>>\n")
                .append(prefix).append("<<KnowledegeBase == ").append(printKnowldegeBaseAsCSV()).append(">>\n")
                .append(prefix).append("<<Justification  == ").append(printJustificationAsCSV()).append(">>\n");
        if (this.childrenNodes == null || this.childrenNodes.isEmpty())
        {
            stringBuilder.append(prefix).append("No children nodes.\n");
        }
        else 
        {
            for (PlFormula key : this.childrenNodes.keySet())
            {
                stringBuilder.append(prefix).append(key).append(": \n").append(this.childrenNodes.get(key).toString(prefix+"\t")).append("\n");
            }
        }
        return stringBuilder.toString();
    }
    
    @Override
    public String toString()
    {
        StringBuilder stringBuilder = new StringBuilder("<<Node>>\n");
        stringBuilder.append("<<KnowledgeBase == ").append(printKnowldegeBaseAsCSV()).append(">>\n");
        stringBuilder.append("<<Justification == ").append(printJustificationAsCSV()).append(">>\n");
        if(this.childrenNodes == null || this.childrenNodes.isEmpty())
        {
            stringBuilder.append("No childern nodes.\n");
        }
        else
        {
            for (PlFormula key : this.childrenNodes.keySet())
            {
                stringBuilder.append(key).append(": \n").append(this.childrenNodes.get(key).toString("\t"));
            }
        }
        
        
        return stringBuilder.toString();
    }
    
}

