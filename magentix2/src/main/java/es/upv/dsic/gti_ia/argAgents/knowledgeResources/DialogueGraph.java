package es.upv.dsic.gti_ia.argAgents.knowledgeResources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode.NodeType;



/**
 * Implementation of the owl concept <i>DialogueGraph</i>
 * 
 */
public class DialogueGraph implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6633212130690006957L;
	private ArrayList<ArgNode> nodes;
	
	/*
	 * TODO each argument given by an agent, and received, will be stored as a argnode in a list
	 * of dialogue graphs of the current dialogue (take into account that a dialogue includes a lot
	 * of subdialogues between two agents, this is why we store a list of dialogue graphs.
	 * Then, when the agent stores all the arguments of the whole dialogue, will use the dialogue graphs
	 * to introduce it in every corresponding argument case
	 */
	
    public DialogueGraph(ArrayList<ArgNode> nodes) {
		this.nodes = nodes;
	}

    public DialogueGraph() {
       	nodes = new ArrayList<ArgNode>();
    }
    
    // Property hasNode

    public ArrayList<ArgNode> getNodes() {
        return nodes;
    }


    //public Iterator listHasNode() {
    //    return listPropertyValuesAs(getHasNodeProperty(), ArgumentNode.class);
    //}


    public void addNode(ArgNode newNode) {
        nodes.add(newNode);
    }


    public void removeNode(ArgNode oldNode) {
        nodes.remove(oldNode);
    }


    public void setNodes(ArrayList<ArgNode> newNodes) {
        nodes = newNodes;
    }


    public ArgNode getRoot() {
        Iterator<ArgNode> iter=nodes.iterator();
        while(iter.hasNext()){
        	ArgNode argNode=iter.next();
        	if(argNode.getNodeType()==NodeType.FIRST)
        		return argNode;
        }
        return null;
    }
    
    public int size(){
    	return nodes.size();
    }
    
    public boolean contains(long argID){
    	 Iterator<ArgNode> iter=nodes.iterator();
         while(iter.hasNext()){
         	ArgNode argNode=iter.next();
         	if(argNode.getArgCaseID()==argID)
         		return true;
         }
         return false;
    }
    
    public ArgNode getNode (long argID){
    	for(int i=0;i<nodes.size();i++)
    		if(nodes.get(i).getArgCaseID()==argID)
    			return nodes.get(i);
    		
    	return null;
    }
    
    public ArrayList<ArgNode> getNodes (long argID){
    	ArrayList<ArgNode> argN = null;
    	for(int i=0;i<nodes.size();i++)
    		if(nodes.get(i).getArgCaseID() == argID){
    			if (argN == null)
    				argN = new ArrayList<ArgNode>();
    			argN.add(nodes.get(i));
    		}
    	return argN;
    }
    
    public int distanceToFinal(long argID) throws Exception{
    	int argPos=-1;
    	int distance=0;
    	for(int i=0;i<nodes.size();i++){
    		if(nodes.get(i).getArgCaseID()==argID){
    			argPos=i;
    			break;
    		}
    	}
    	for(int j=argPos;j<nodes.size();j++){
    		if(nodes.get(j).getNodeType()==NodeType.AGREE)
    			break;
    		distance++;
    	}
    	return distance;
    }
    
    public int distanceToFinal(ArgNode argNode) throws Exception{
    	int argPos=-1;
    	int distance=0;
    	for(int i=0;i<nodes.size();i++){
    		if(nodes.get(i).equals(argNode)){
    			argPos=i;
    			break;
    		}
    	}
    	for(int j=argPos;j<nodes.size();j++){
    		if(nodes.get(j).getNodeType()==NodeType.AGREE)
    			break;
    		distance++;
    	}
    	return distance;
    }
}
