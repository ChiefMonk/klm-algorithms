package uct.cs.klm.algorithms.models;

import java.util.ArrayList;
import java.util.List;

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
