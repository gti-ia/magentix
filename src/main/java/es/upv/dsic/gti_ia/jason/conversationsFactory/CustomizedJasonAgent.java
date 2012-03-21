package es.upv.dsic.gti_ia.jason.conversationsFactory;

import java.util.Iterator;
import java.util.List;
import jason.RevisionFailedException;
import jason.architecture.AgArch;
import jason.asSemantics.Agent;
import jason.asSemantics.Intention;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.LogExpr;
import jason.asSyntax.Rule;
import jason.asSyntax.Term;
import jason.asSyntax.VarTerm;


public class CustomizedJasonAgent extends Agent {
	
    /**
     * This function should revise the belief base with the given literal to
     * add, to remove, and the current intention that triggered the operation.
     * 
     * <p>In its return, List[0] has the list of actual additions to
     * the belief base, and List[1] has the list of actual deletions;
     * this is used to generate the appropriate internal events. If
     * nothing change, returns null.
     */


       public List<Literal>[] brf(Literal beliefToAdd, Literal beliefToDel,  Intention i) throws RevisionFailedException {
       System.out.println("***  beliefToAdd - "+beliefToAdd);
        System.out.println("***  beliefToDel - "+beliefToDel);
        
        Unifier u = null;
        try {
            u = i.peek().getUnif(); // get from current intention
        } catch (Exception e) {
            u = new Unifier();
        }
        
       

       
		
		if (beliefToDel!=null)
		{
			//Iterator<Unifier> iun = mylogicalConsequence(beliefToDel,this, u);
			
			//if (iun.hasNext())
				//System.out.println("Tiene Next : iun.hasNext() : "+iun.next().toString());
			//else
			//	System.out.println("No tiene Next : iun.hasNext()");
			beliefToDel.addAnnot(Literal.parseLiteral("source(self)"));
			
			Iterator<Unifier> iun = mylogicalConsequence(beliefToDel,this, u);
			System.out.println("@@@@ - believes(beliefToDel, u) : "+believes(beliefToDel, u));
			if (iun != null && iun.hasNext()) {
                u.compose(iun.next());
                System.out.println("@@@@ - believes devuelve true");
            }else
				System.out.println("@@@@ - believes devuelve false");
			
			


			
			

		}
		
         System.out.println("---");       
   return super.brf(beliefToAdd, beliefToDel, i);
    }

	public Iterator<Unifier> mylogicalConsequence(final Literal mylit, final Agent ag, final Unifier un) {
    final Iterator<Literal> il   = ag.getBB().getCandidateBeliefs(mylit, un);
    if (il == null) // no relevant bels
        return LogExpr.EMPTY_UNIF_LIST.iterator();
    
    final AgArch            arch     = (ag == null ? null : ag.getTS().getUserAgArch());
    final int               nbAnnots = (mylit.hasAnnot() && mylit.getAnnots().getTail() == null ? mylit.getAnnots().size() : 0); // if annots contains a tail (as in p[A|R]), do not backtrack on annots
    
	System.out.println("mylit.getAnnots().size() : "+mylit.getAnnots().toString());
	
    return new Iterator<Unifier>() {
        Unifier           current = null;
        Iterator<Unifier> ruleIt = null; // current rule solutions iterator
        Literal           cloneAnnon = null; // a copy of the literal with makeVarsAnnon
        Rule              rule; // current rule
        boolean           needsUpdate = true;
        
        Iterator<List<Term>>  annotsOptions = null;
        Literal               belInBB = null;
        
        public boolean hasNext() {
            if (needsUpdate)
                get();
            System.out.println("++++ current en hasNext: "+current.toString());
			return current != null;
        }

        public Unifier next() {
            if (needsUpdate)
                get();
            Unifier a = current;
            if (current != null)
                needsUpdate = true;
			System.out.println("++++ current en next: "+a.toString());
            return a;
        }

        private void get() {
            needsUpdate = false;
            current     = null;
            if (arch != null && !arch.isRunning()) return;
            
            // try annots iterator
			System.out.println("annotsOptions: "+annotsOptions);
			//System.out.println("annotsOptions.hasNext(): "+annotsOptions.hasNext());
            while (annotsOptions != null && annotsOptions.hasNext()) {
                Literal belToTry = belInBB.copy().setAnnots(null).addAnnots( annotsOptions.next() );
                
                Unifier u = un.clone();
                if (u.unifiesNoUndo(mylit , belToTry)) {
					System.out.println("Entra en unifiesNoUndo(mylit , belToTry)");
                    current = u;
                    return;
                }else
				   System.out.println("Negativo con unifiesNoUndo(mylit , belToTry)");
            }
            
            // try rule iterator
			System.out.println("ruleIt: "+ruleIt);
            while (ruleIt != null && ruleIt.hasNext()) {
                // unifies the rule head with the result of rule evaluation
                Unifier ruleUn = ruleIt.next(); // evaluation result
                Literal rhead  = rule.headClone();
                rhead.apply(ruleUn);
                useDerefVars(rhead, ruleUn); // replace vars by the bottom in the var clusters (e.g. X=_2; Y=_2, a(X,Y) ===> A(_2,_2))
                rhead.makeVarsAnnon(); // to remove vars in head with original names
                
                Unifier unC = un.clone();
                if (unC.unifiesNoUndo(mylit, rhead)) {
                    current = unC;
                    return;
                }
            }
            
            // try literal iterator
			System.out.println("il.hasNext "+il.hasNext());
            while (il.hasNext()) {
                belInBB = il.next(); // b is the relevant entry in BB
				//belInBB.addAnnot(Literal.parseLiteral("source(self)"));
				System.out.println("il.next "+belInBB.toString());
				System.out.println("belInBB.isRule() : "+belInBB.isRule());
				System.out.println("belInBB annots : "+belInBB.getAnnots());
                if (belInBB.isRule()) {
                    rule = (Rule)belInBB;
                    
                    // create a copy of this literal, ground it and 
                    // make its vars anonymous, 
                    // it is used to define what will be the unifier used
                    // inside the rule.
                    if (cloneAnnon == null) {
                        cloneAnnon = mylit.copy();
                        cloneAnnon.apply(un);
                        cloneAnnon.makeVarsAnnon();
                    }
                    Unifier ruleUn = new Unifier();
                    if (ruleUn.unifiesNoUndo(cloneAnnon, rule)) { // the rule head unifies with the literal
                        ruleIt = rule.getBody().logicalConsequence(ag,ruleUn);
                        get();
                        if (current != null) // if it get a value
                            return;
                    }
                } else { // not rule
                    if (nbAnnots > 0) { // try annots backtracking
						boolean a= belInBB.hasAnnot();
						System.out.println("belInBB.hasAnnot() : "+a);
                        if (belInBB.hasAnnot()) {

                            int nbAnnotsB = belInBB.getAnnots().size();
							
							
                            if (nbAnnotsB >= nbAnnots) {
                                annotsOptions =	belInBB.getAnnots().subSets(nbAnnots).iterator();
                                get();
								
                                if (current != null) // if it get a value
									{
										System.out.println("**** VALOR DE CURRENT TRUE.");
										return;
									
									}
                            }
                        }
                    } else {
                        Unifier u = un.clone();

						boolean b = u.unifiesNoUndo(mylit, belInBB);
						
						System.out.println("u.unifiesNoUndo(mylit, belInBB) : "+b);
						
                        if (u.unifiesNoUndo(mylit, belInBB)) {
                            current = u;
                            return;
                        }
                    }
                }
            }
        }
        
        public void remove() {}
    };
}



private void useDerefVars(Term p, Unifier un) {
    if (p instanceof Literal) {
        Literal l = (Literal)p;
        for (int i=0; i<l.getArity(); i++) {
            Term t = l.getTerm(i);
            if (t.isVar()) {
                l.setTerm(i, un.deref( (VarTerm)t));
            } else {
                useDerefVars(t, un);                    
            }
        }
    }
}
	
	
}