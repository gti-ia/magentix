package normative_jason;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import jason.asSyntax.Rule;

import java.util.ArrayList;
import java.util.List;

import es.upv.dsic.gti_ia.norms.MagentixAgArch;

/**
 * Example of an agent architecture that only uses Jason BDI engine. It runs
 * without all Jason IDE stuff. (see Jason FAQ for more information about this
 * example) This agent uses AMQP standard to send FIPA ACL messages
 * 
 * The class must extend MagentixAgArch class to be used by the Jason engine. In
 * this class the user has to define how the agent perceives, and agent actions
 * 
 * 
 * @author Ricard López Fogués - rilopez@dsic.upv.es
 */
public class SimpleArchitecture extends MagentixAgArch {

	// this method just add some perception for the agent
	/*
	@Override
	public List<Literal> perceive() {
		/*
		List<Literal> l = new ArrayList<Literal>();
		l.add(Literal.parseLiteral("isUnit(virtual)"));
		
		
		l.add(Literal.parseLiteral("isUnit(foro)"));
		l.add(Literal.parseLiteral("isUnit(foro2)"));
		l.add(Literal.parseLiteral("isUnit(foro3)"));
		l.add(Literal.parseLiteral("isUnit(vb)"));
		l.add(Literal.parseLiteral("hasParent(foro, virtual)"));
		l.add(Literal.parseLiteral("hasParent(foro2, virtual)"));
		l.add(Literal.parseLiteral("hasParent(foro3, foro)"));
		
		
		l.add(Literal.parseLiteral("playsRole(agente,virtual,rolVirtual)"));
		l.add(Literal.parseLiteral("playsRole(agente,foro,rolForo)"));
		
		l.add(Literal.parseLiteral("hasPosition(rolVirtual,virtual,member)"));
		l.add(Literal.parseLiteral("hasPosition(rolForo,foro,creator)"));
		
		l.add(Literal.parseLiteral("roleCardinality(creator, foro, 15)"));
		
		
		 
		Literal normLiteral = Literal.parseLiteral("isAgent(vb) & vb == Agent");
		Rule regla = new Rule(Literal.parseLiteral("deregisterUnit(UnitName, Agent)"),(LogicalFormula) normLiteral.getTerm(0));
		
		System.out.println("Es una regla: "+ regla.isRule());
		l.add(regla);
		*/
		/*
		l.add(Literal.parseLiteral("x(20,1)"));
		l.add(Literal.parseLiteral("information(weatherBa,1)"));

		// Como a percepciones
	

		Literal percepcion1 = Literal.parseLiteral("pos(box1,coord(9,9))");
		Literal percepcion2 = Literal.parseLiteral("pos(box1,coord(0,0))");
		
		Literal percepcion5 = Literal.parseLiteral("weatherBad");

		Literal percepcion4 = Literal.parseLiteral("colour(sphere,yellow)");
		
	//	l.add(Literal.parseLiteral("zone(nord)"));
		l.add(Literal.parseLiteral("zone(sud)"));
		//l.add(Literal.parseLiteral("shape(box1,box)"));
		//l.add(Literal.parseLiteral("person(Paco,son)"));
		
		//l.add(percepcion4);
		l.add(Literal.parseLiteral("colour(box1,blue)"));
		l.add(Literal.parseLiteral("colour(circulo,red)"));
	//	l.add(Literal.parseLiteral("shape(sphere2,sphere)"));
	//	l.add(Literal.parseLiteral("vehicle(Seat, car)"));
		//		l.add(Literal.parseLiteral("basura(ERT, bas)"));
		
		
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Vamos a desactivar una norma instanciada.
		l.remove(percepcion1);
		l.add(percepcion3);
		l.add(percepcion4);
		return l;
	}*/
	

}