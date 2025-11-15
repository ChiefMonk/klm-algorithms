package uct.cs.klm.algorithms.models;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a model hitting set tree for a given query.
 * 
 * @author Chipo Hamayobe (chipo@cs.uct.ac.za)
 * @version 1.0.1
 * @since 2024-01-01
 */
public class ModelHittingSetTree 
{   
    private List<ModelNode> nodes;
    private ModelNode rootNode;
    
    public ModelHittingSetTree(ModelNode rootNode)
    {
        this.rootNode = rootNode;
        this.nodes = new ArrayList<ModelNode>();
    }
    
    public void addNode(ModelNode node)
    {
        this.nodes.add(node);
    }
    
}
