package jasonTest_1;

import jason.asSemantics.ActionExec;
import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;

import es.upv.dsic.gti_ia.jason.MagentixAgArch;

/**
 * Example of an agent architecture that only uses Jason BDI engine. It runs without all
 * Jason IDE stuff. (see Jason FAQ for more information about this example)
 * This agent uses AMQP standard to send FIPA ACL messages
 * 
 * The class must extend MagentixAgArch class to be used by the Jason engine.
 * In this class the user has to define how the agent perceives, and agent actions
 * 
 * 
 * @author Ricard López Fogués - rilopez@dsic.upv.es
 */
public class SimpleArchitecture extends MagentixAgArch {

	// this method just add some perception for the agent
	@Override
    public List<Literal> perceive() {
        List<Literal> l = new ArrayList<Literal>();
        l.add(Literal.parseLiteral("x(10)"));
        return l;
    }
}