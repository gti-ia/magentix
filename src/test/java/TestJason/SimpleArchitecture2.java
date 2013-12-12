package TestJason;

import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.List;

import es.upv.dsic.gti_ia.jason.MagentixAgArch;

public class SimpleArchitecture2 extends MagentixAgArch {
	// this method just add some perception for the agent
	@Override
	public List<Literal> perceive() {
		List<Literal> l = new ArrayList<Literal>();
		l.add(Literal.parseLiteral("s(10)"));
		return l;
	}
}
