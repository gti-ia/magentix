package es.upv.dsic.gti_ia.argAgents.domainCBR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.apibinding.configurables.ThreadSafeOWLManager;
import org.semanticweb.owlapi.io.OWLOntologyCreationIOException;
import org.semanticweb.owlapi.io.OWLParser;
import org.semanticweb.owlapi.io.OWLParserException;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.UnparsableOntologyException;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;



/**
 * @author stella
 * Class to parse the OWL domain ontology into a vector of Tickets
 * Format IDTicket#TipificationNode#ProblemDesc#Project#SolvingOperator#SolvingGroup#Premises#Solutions
 * where Premises = Premise$Premise
 * where Premise = ID/Content
 * where Solutions = Solution$Solution
 * where Solution = ID/TimesUsed/PromotedValue
 * 
 */

public class OWLDomainParser {
	
	private String ontoID = null;
	private OWLOntologyManager manager = null;
//	private ThreadSafeOWLManager manager = null;
	private OWLOntology domainOntology = null;
	private OWLDataFactory factory = null;
	private IRI documentIRI = null;
	private PelletReasoner reasoner = null;


	/**
	 * Constructor of the class OWlDomainParser
	 * @param ontoFile The name of the file that stores the ontology
	 */
	
	public OWLDomainParser(){
		
	}
	
	/**
	 * Loads the domain ontology and assigns it a unique identifier IRI
	 */
	
	public void loadOntology(String ontoFile){
		try {
			// Get hold of an ontology manager
			manager = OWLManager.createOWLOntologyManager();

			// Load an ontology from the web
			//IRI iri = IRI.create(ontoFile);
			//domainOntology = manager.loadOntologyFromOntologyDocument(iri);
			//System.out.println("Loaded ontology: " + domainOntology);
				
			// Remove the ontology so that we can load a local copy.
			// manager.removeOntology(domainOntology);
			
			// Load an ontology from a file
			File file = new File(ontoFile);
			 	
			// Load the ontology
			domainOntology = manager.loadOntologyFromOntologyDocument(file);
			System.out.println("Loaded ontology: " + domainOntology);
				
			// Obtain the location where the ontology was loaded from
			documentIRI = manager.getOntologyDocumentIRI(domainOntology);
			System.out.println(" from: " + documentIRI);
			
			// Obtain the ontology ID
			ontoID = domainOntology.getOntologyID().getOntologyIRI().toString();
//			System.out.println("ontoID: " + ontoID);
				
			// Remove the ontology again so we can reload it later
			//manager.removeOntology(domainOntology);

			}
			catch (OWLOntologyCreationIOException e) {
			// IOExceptions during loading get wrapped in an OWLOntologyCreationIOException
				IOException ioException = e.getCause();
				if (ioException instanceof FileNotFoundException) {
					System.out.println("Could not load ontology. File not found: " + ioException.getMessage());
					}
				else if (ioException instanceof UnknownHostException) {
					System.out.println("Could not load ontology. Unknown host: " + ioException.getMessage());
					}
				else {
					System.out.println("Could not load ontology: " + ioException.getClass().getSimpleName() + " " + ioException.getMessage());
					}
				}
			catch (UnparsableOntologyException e) {
			// If there was a problem loading an ontology because there are syntax errors in the document (file) that
			// represents the ontology then an UnparsableOntologyException is thrown
				System.out.println("Could not parse the ontology: " + e.getMessage());
				// A map of errors can be obtained from the exception
				Map<OWLParser, OWLParserException> exceptions = e.getExceptions();
				// The map describes which parsers were tried and what the errors were
				for (OWLParser parser : exceptions.keySet()) {
					System.out.println("Tried to parse the ontology with the " + parser.getClass().getSimpleName() + " parser");
					System.out.println("Failed because: " + exceptions.get(parser).getMessage());
					}
				}
			catch (UnloadableImportException e) {
			// If our ontology contains imports and one or more of the imports could not be loaded then an
			// UnloadableImportException will be thrown (depending on the missing imports handling policy)
				System.out.println("Could not load import: " + e.getImportsDeclaration());
				// The reason for this is specified and an OWLOntologyCreationException
				OWLOntologyCreationException cause = e.getOntologyCreationException();
				System.out.println("Reason: " + cause.getMessage());
				}
			catch (OWLOntologyCreationException e) {
				System.out.println("Could not load ontology: " + e.getMessage());
				}
			}
	
	
	
	/**
	 * Saves a vector of tickets in the domain ontology. The ontology must be previously loaded.
	 * @param cases The vector of ca to save in the domain ontology
	 * @param destFile The name of the ontology to be saved
	 */
	
	public void saveDomainOntology(Vector<Ticket> tickets, String ontoFile, String destFile) throws Exception {
		
		this.loadOntology(ontoFile);
		
		// hold changes in the Queue as they are done
        Queue<AddAxiom> changes = new LinkedList<AddAxiom>();
        
        // create a Factory for our Datatypes
        factory = manager.getOWLDataFactory();
		
		for (Ticket vT : tickets){
			
			OWLNamedIndividual ticket = null;
			
			int tID = -1;
			try {
				tID = vT.getTicketID();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				continue;
			}
			// Creating Ticket Individual
			if (tID != -1 && tID != 0)
			{	
				ticket = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Ticket" + Long.toString(tID)));
				OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Ticket"));
				OWLClassAssertionAxiom ticketaxiom = factory.getOWLClassAssertionAxiom(Class, ticket);
				changes.add(new AddAxiom(domainOntology, ticketaxiom));
			}
				
			// creating Data property ticketID
			if (tID != -1 && tID != 0)
			{
				OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#ticketID"));
				OWLLiteral owlL = factory.getOWLLiteral(tID);
				OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, ticket, owlL);
				changes.add(new AddAxiom(domainOntology, addProp));
			}
			
			// creating Data property problemDesc
			String tDesc = "";
			try {
				tDesc = this.filter(vT.getProblemDesc());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (tDesc == null) tDesc = "";
			
			if (tDesc != null)
			{
				tDesc=tDesc.trim().replace(' ', '_');
				OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#problemDesc"));
				OWLLiteral owlL = factory.getOWLLiteral(tDesc);
				OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, ticket, owlL);
				changes.add(new AddAxiom(domainOntology, addProp));
			}
			
			// creating Data property project
			String tProj = "";
			try {
				tProj = this.filter(vT.getProject());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (tProj == null) tProj = "";
			
			if (tProj != null)
			{
				tProj=tProj.trim().replace(' ', '_');
				OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#project"));
				OWLLiteral owlL = factory.getOWLLiteral(tProj);
				OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, ticket, owlL);
				changes.add(new AddAxiom(domainOntology, addProp));
			}
		
			// creating Object property categoryNode
			Category tCat = null;
			try {
				tCat = vT.getCategoryNode();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			OWLNamedIndividual category = null;
			if (tCat != null)
			{
			
				int catID = -1;
				try {
					catID = tCat.getIdTipi();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Creating Category Individual
				if (catID != -1 && catID != 0)
				{
					{
						category = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Category" + Integer.toString(catID)));
						OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Category"));
						OWLClassAssertionAxiom categoryaxiom = factory.getOWLClassAssertionAxiom(Class, category);
						changes.add(new AddAxiom(domainOntology, categoryaxiom));
					}
					
			
					// creating Data property idTipi
					{
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#idTipi"));
						OWLLiteral owlL = factory.getOWLLiteral(catID);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, category, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
				
					int catPar = -1;
					try {
						catPar = tCat.getParentID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// creating Data property parentID
					if (catPar != -1 && catPar != 0)
					{
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#parentID"));
						OWLLiteral owlL = factory.getOWLLiteral(catPar);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, category, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
				
					String catTipi = "";
					try {
						catTipi = this.filter(tCat.getTipification());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
					if (catTipi ==  null) catTipi = "";
				
					// creating Data property tipification
					if (catTipi != null)
					{
						catTipi=catTipi.trim().replace(' ', '_');
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#tipification"));
						OWLLiteral owlL = factory.getOWLLiteral(catTipi);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, category, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
				
					ArrayList<Question> catQue = null;
					try {
						catQue = tCat.getQuestions();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (catQue != null){
						for (Question q: catQue){
					
							// creating Object property Question
							OWLNamedIndividual question = null;
							int qID = -1;
							try {
								qID = q.getQuestionID();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if (qID != -1 && qID != 0)
							{
					
								// Creating Question Individual
								{
									question = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Question" + Integer.toString(qID)));
									OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Question"));
									OWLClassAssertionAxiom questionaxiom = factory.getOWLClassAssertionAxiom(Class, question);
									changes.add(new AddAxiom(domainOntology, questionaxiom));
								}
					
								// creating Data property questionID
								{
									OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionID"));
									OWLLiteral owlL = factory.getOWLLiteral(qID);
									OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
									changes.add(new AddAxiom(domainOntology, addProp));
								}
					
								String qDesc = "";
								try {
									qDesc = this.filter(q.getQuestionDesc());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								if (qDesc == null) qDesc = "";
								
								// creating Data property questionDesc
								if (qDesc != null)
								{
									qDesc=qDesc.trim().replace(' ', '_');
									OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionDesc"));
									OWLLiteral owlL = factory.getOWLLiteral(qDesc);
									OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
									changes.add(new AddAxiom(domainOntology, addProp));
								}
						
								// adding question to category
								OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#questions"));
								OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, category, question);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
						}
					}		
				
					// adding categoryNode to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#categoryNode"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, category);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			}
			
			// creating Object property attributes
			HashMap<Integer, Attribute> tAtt = null;
			try {
				tAtt = (HashMap<Integer, Attribute>) vT.getAttributes();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			OWLNamedIndividual attribute = null;
			if (tAtt != null){
				Iterator<Attribute> ite = tAtt.values().iterator();
				while (ite.hasNext()){
					Attribute at = ite.next();
					
					int aID = -1;
					try {
						aID = at.getAskedQuestion().getQuestionID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String aAns = "";
					try {
						aAns = this.filter(at.getAnswer());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (aAns == null) aAns = "";
					
					// Creating Attribute Individual
					if (aID != -1 && aID != 0)
					{
						aAns=aAns.trim().replace(' ', '_');
						attribute = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Attribute" + Integer.toString(aID) + aAns));
						OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Attribute"));
						OWLClassAssertionAxiom attributeaxiom = factory.getOWLClassAssertionAxiom(Class, attribute);
						changes.add(new AddAxiom(domainOntology, attributeaxiom));
					}
					
					// creating Object property askedQuestion
					Question aQue = null;
					try {
						aQue = at.getAskedQuestion();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					OWLNamedIndividual question = null;
					
					if (aQue != null){
						// creating Object property Question
						int qID = -1;
						try {
							qID = aQue.getQuestionID();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (qID != -1 && qID != 0)
						{
					
							// Creating Question Individual
							{
								question = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Question" + Integer.toString(qID)));
								OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Question"));
								OWLClassAssertionAxiom questionaxiom = factory.getOWLClassAssertionAxiom(Class, question);
								changes.add(new AddAxiom(domainOntology, questionaxiom));
							}
					
							// creating Data property questionID
							{
								OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionID"));
								OWLLiteral owlL = factory.getOWLLiteral(qID);
								OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
					
							String qDesc = "";
							try {
								qDesc = this.filter(aQue.getQuestionDesc());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							if (qDesc == null) qDesc = "";
							
							// creating Data property questionDesc
							if (qDesc != null)
							{
								qDesc=qDesc.trim().replace(' ', '_');
								OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionDesc"));
								OWLLiteral owlL = factory.getOWLLiteral(qDesc);
								OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
						
							// adding question to attribute
							OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#askedQuestion"));
							OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, attribute, question);
							changes.add(new AddAxiom(domainOntology, addProp));
						}

					}
					
					// creating Data property answer
					if (aAns != null)
					{
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#answer"));
						OWLLiteral owlL = factory.getOWLLiteral(aAns);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, attribute, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
					
					// adding attribute to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#attributes"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, attribute);
					changes.add(new AddAxiom(domainOntology, addProp));
					
				}
				
			}
			
			// creating Object property solvingOperators
			HashMap<String, Operator> tOp = null;
			try {
				tOp = vT.getSolvingOperators();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			OWLNamedIndividual operator = null;
			if (tOp != null) {
				Iterator<String> it = tOp.keySet().iterator();
				while (it.hasNext()) {
					String opID = "";
					try {
						opID = this.filter(it.next());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (opID.equals("") || opID == null){
						continue;
					}
					if (opID != null){
						// Creating Operator Individual
						{
							opID=opID.trim().replace(' ', '_');
							operator = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Operator" + opID));
							OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Operator"));
							OWLClassAssertionAxiom operatoraxiom = factory.getOWLClassAssertionAxiom(Class, operator);
							changes.add(new AddAxiom(domainOntology, operatoraxiom));
						}
						
						// creating Data property operatorID
						{
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#operatorID"));
							OWLLiteral owlL = factory.getOWLLiteral(opID);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, operator, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					}
				
					// adding operator to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#solvingOperators"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, operator);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			}
			
			// creating Object property solvingGroups
			HashMap<String, Group> tGr = null;
			try {
				tGr = vT.getSolvingGroups();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			OWLNamedIndividual group = null;
			if (tGr!= null) {
				Iterator<String> itg = tGr.keySet().iterator();
				while (itg.hasNext()){
					String grID = "";
					try {
						grID = this.filter(itg.next());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (grID.equals("") || grID == null) {
						continue;
					}
				
					if (grID != null){
						// Creating Group Individual
						{
							grID=grID.trim().replace(' ', '_');
							group = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Group" + grID));
							OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Group"));
							OWLClassAssertionAxiom groupaxiom = factory.getOWLClassAssertionAxiom(Class, group);
							changes.add(new AddAxiom(domainOntology, groupaxiom));
						}
					
						// creating Data property groupID
						{
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#groupID"));
							OWLLiteral owlL = factory.getOWLLiteral(grID);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, group, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					
						// creating Object property members
						//TODO add members from data file
						try {
							ArrayList<Operator> grMem = null;
							grMem = tGr.get(grID).getMembers();
							if (grMem != null){
								for (Operator m : grMem){
									OWLNamedIndividual operator1 = null;
									String opID = "";
									try {
										opID = this.filter(m.getOperatorID());
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (opID.equals("") || opID == null)
										continue;
									
									if (opID != null){
										// Creating Operator Individual
										{
											opID=opID.trim().replace(' ', '_');
											operator1 = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Operator" + opID));
											OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Operator"));
											OWLClassAssertionAxiom operatoraxiom = factory.getOWLClassAssertionAxiom(Class, operator1);
											changes.add(new AddAxiom(domainOntology, operatoraxiom));
										}
									
										// creating Data property operatorID
										{
											OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#operatorID"));
											OWLLiteral owlL = factory.getOWLLiteral(opID);
											OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, operator1, owlL);
											changes.add(new AddAxiom(domainOntology, addProp));
										}
									}
									
									// adding operator to group
									OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#members"));
									OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, group, operator1);
									changes.add(new AddAxiom(domainOntology, addProp));
							
								}
							}
						} catch (Exception e) {
							System.err.println("Unknown value for property members");
						}
					}
				
					// adding group to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#solvingGroups"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, group);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			}
				
			// creating Object property solutions
			HashMap<Integer, Solution> tSol = null;
			try {
				tSol = (HashMap<Integer, Solution>) vT.getSolutions();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OWLNamedIndividual solution = null;
			if (tSol != null){
				Iterator<Solution> ite = tSol.values().iterator();
				while (ite.hasNext()){
					Solution s = ite.next();
					
					int sID = -1;
					try {
						sID = s.getSolutionID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Creating Solution Individual
					if (sID != -1 && sID != 0){
						{
							solution = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Solution" + sID));
							OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Solution"));
							OWLClassAssertionAxiom solutionaxiom = factory.getOWLClassAssertionAxiom(Class, solution);
							changes.add(new AddAxiom(domainOntology, solutionaxiom));
						}
					
						// creating Data property solutionID
						{
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#solutionID"));
							OWLLiteral owlL = factory.getOWLLiteral(sID);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					
						String sDesc = "";
						try {
							sDesc = this.filter(s.getSolutionDesc());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (sDesc == null) sDesc = "";
						
						// creating Data property solutionDesc
						if (sDesc != null)
						{
							sDesc=sDesc.trim().replace(' ', '_');
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#solutionDesc"));
							OWLLiteral owlL = factory.getOWLLiteral(sDesc);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					
						String sVal = "";
						try {
							sVal = this.filter(s.getPromotedValue());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (sVal == null) sVal = "";
						
						// creating Data property promotedValue
						if (sVal != null)
						{
							sVal=sVal.trim().replace(' ', '_');
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#promotedValue"));
							OWLLiteral owlL = factory.getOWLLiteral(sVal);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					
						int sUse = 0;
						try {
							sUse = s.getTimesUsed();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// creating Data property timesUsed
						// TODO revise the default values for times used
						if (sUse != 0)
						{
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#timesUsed"));
							OWLLiteral owlL = factory.getOWLLiteral(sUse);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					
						// adding solution to ticket
						OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#solutions"));
						OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, solution);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
				}
			}
		}
		
		// apply all changes to the Model
		for (AddAxiom addAxiom : changes) {
			manager.applyChange(addAxiom);
		}
		
		// save the Ontology to file
		OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat(); 
		RDFXMLOntologyFormat rdfxmlFormat = new RDFXMLOntologyFormat();
		
		OWLOntologyFormat format = manager.getOntologyFormat(domainOntology);
		System.out.println(" format: " + format); 
		
//		if(format.isPrefixOWLOntologyFormat()) {
//			owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
//		}
		
		File file = new File(destFile);
		
		try{
			System.out.println("OWLDOMAINPARSER: saving ontology " + destFile);
			manager.saveOntology(domainOntology, IRI.create(file.toURI()));
			System.out.println("OWLDOMAINPARSER: " + destFile + " saved");
		}catch (Exception e){
			e.printStackTrace();
			System.err.println("++++++++++++>ERROR " + e.getCause() + " SAVING: " + destFile);
		}
		 
//		manager.saveOntology(domainOntology, rdfxmlFormat, IRI.create(file.toURI())); 

		
		manager.removeOntology(domainOntology);

	}
	
	/**
	 * Parses the domain ontology in a vector of tickets. The ontology must be previously loaded.
	 * @return tickets The vector of tickets to parse the domain ontology
	 */
	
	public Vector<Ticket> parseDomainOntology(String ontoFile) throws Exception {
		
			this.loadOntology(ontoFile);
		
			Vector<Ticket> tickets = new Vector<Ticket>();
//			Pellet pellet = new Pellet();
//			VersionInfo ver=Pellet.getVersionInfo();
//			System.out.println(ver.getVersionString());
			com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory pell = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory.getInstance();
			reasoner = pell.createReasoner(domainOntology);
			
			reasoner.getKB().realize();
//			reasoner.getKB().printClassTree();
			
			System.out.println("Reasoner Loaded");
			
			// Ask the reasoner to do all the necessary work now 
			reasoner.precomputeInferences();
			
			// We can determine if the ontology is actually consistent (in this case, it should be).
			boolean consistent = reasoner.isConsistent();
			System.out.println("Consistent: " + consistent);
//			System.out.println("\n"); 
			
			// We can easily get a list of unsatisfiable classes. (A class is unsatisfiable if it
			// can't possibly have any instances). Note that the getUnsatisfiableClasses method
			// is really just a convenience method for obtaining the classes that are equivalent
			// to owl:Nothing. In our case there should be just one unsatisfiable class - "mad_cow"
			// We ask the reasoner for the unsatisfiable classes, which returns the bottom node
			// in the class hierarchy (an unsatisfiable class is a subclass of every class).
			Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
			// This node contains owl:Nothing and all the classes that are equivalent to owl:Nothing -
			// i.e. the unsatisfiable classes.
			// We just want to print out the unsatisfiable classes excluding owl:Nothing, and we can
			// used a convenience method on the node to get these
			Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
			if (!unsatisfiable.isEmpty()) {
				System.out.println("The following classes are unsatisfiable: ");
			 	for(OWLClass cls : unsatisfiable) {
			 		System.out.println(" " + cls);
			 	}
			}
			else {
				System.out.println("There are no unsatisfiable classes");
			}
//			System.out.println("\n");
			
			// create property and resources to query the reasoner
			// for Ticket
			OWLClass TicketOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Ticket"));
			OWLDataProperty ticketID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#ticketID"));
			OWLObjectProperty categoryNode = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#categoryNode"));
			OWLDataProperty  problemDesc = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#problemDesc"));
			OWLDataProperty project = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#project"));
			OWLObjectProperty solvingOperators = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#solvingOperators"));
			OWLObjectProperty solvingGroups = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#solvingGroups"));
			OWLObjectProperty attributes = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#attributes"));
			OWLObjectProperty solutions = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#solutions"));
			
			// for Attribute
			OWLClass AttributeOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Attribute"));
			OWLObjectProperty askedQuestion = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#askedQuestion"));
			OWLDataProperty answer = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#answer"));
			
			// for Category
			OWLClass CategoryOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Category"));
			OWLDataProperty idTipi = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#idTipi"));
			OWLDataProperty parentID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#parentID"));
			OWLDataProperty tipification = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#tipification"));
			OWLObjectProperty questions = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#questions"));
			
			// for Group
			OWLClass GroupOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Group"));
			OWLDataProperty groupID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#groupID"));
			OWLObjectProperty members = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#members"));
			
			// for Operator
			OWLClass OperatorOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Operator"));
			OWLDataProperty operatorID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#operatorID"));
			
			// for Question
			OWLClass QuestionOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Question"));
			OWLDataProperty questionDesc = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#questionDesc"));
			OWLDataProperty questionID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#questionID"));
			
			// for Solution
			OWLClass SolutionOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Solution"));
			OWLDataProperty promotedValue = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#promotedValue"));
			OWLDataProperty solutionDesc = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#solutionDesc"));
			OWLDataProperty solutionID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#solutionID"));
			OWLDataProperty timesUsed = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#timesUsed"));
			
			
			// get all instances of Ticket class
			NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances( TicketOWL, false);
			
			/*System.out.println("Number of individuals: "+individuals.getNodes().size());
			System.out.println("Instances of Ticket: ");
			for(Node<OWLNamedIndividual> ind : individuals) {
			 	System.out.println(" " + ind);
			}
			System.out.println("\n"); */
						
			for(Node<OWLNamedIndividual> sameInd : individuals) {
				
				Ticket newTicket = new Ticket();
				
				// sameInd contains information about the individual (and all other individuals that were inferred to be the same)
				OWLNamedIndividual ind =  sameInd.getRepresentativeElement();
				
			    // get the info about this specific individual
				Set<OWLLiteral> id = reasoner.getDataPropertyValues(ind, ticketID); 
				NodeSet<OWLNamedIndividual> cat = reasoner.getObjectPropertyValues( ind, categoryNode );
				Set<OWLLiteral> desc = reasoner.getDataPropertyValues(ind, problemDesc);
				Set<OWLLiteral> proj = reasoner.getDataPropertyValues(ind, project);
				NodeSet<OWLNamedIndividual> operSet = reasoner.getObjectPropertyValues( ind, solvingOperators );
				NodeSet<OWLNamedIndividual> groupSet = reasoner.getObjectPropertyValues( ind, solvingGroups );
				NodeSet<OWLNamedIndividual> attSet = reasoner.getObjectPropertyValues( ind, attributes );
				NodeSet<OWLNamedIndividual> solSet = reasoner.getObjectPropertyValues( ind, solutions );
				
				NodeSet<OWLClass> types = reasoner.getTypes( ind, true );		    
			    
				// get the ticketID
				try {
					String idt=id.iterator().next().getLiteral();
					int idT = Integer.parseInt(idt.trim().replace(' ','_'));
					//System.out.println( "ticketID: " + idT );
					newTicket.setTicketID(idT);
				} catch (Exception e1) {
					System.err.println("Unknown value for property ticketID");
					continue;
				}
			    
				// get the categoryNode
				try{
					Category categoryT = new Category();
					OWLNamedIndividual category = cat.iterator().next().getRepresentativeElement();
					try {
						Set<OWLLiteral> idTip = reasoner.getDataPropertyValues(category, idTipi);
						int idTipT = Integer.parseInt(idTip.iterator().next().getLiteral().trim().replace(' ','_'));
						categoryT.setIdTipi(idTipT);
						//System.out.println("idTipi: " + idTipT);
					} catch (Exception e1) {
						System.err.println("Unknown value for property idTipi[Category]");
					}
					try {
						Set<OWLLiteral> parentIDT = reasoner.getDataPropertyValues(category, parentID);
						int parIDT = Integer.parseInt(parentIDT.iterator().next().getLiteral().trim().replace(' ','_'));
						categoryT.setParentID(parIDT);
						//System.out.println("parentID: " + parIDT);
					} catch (Exception e1) {
						System.err.println("Unknown value for property parentID[Category]");
					}
//			    	String tipiT = "";
					try{
						Set<OWLLiteral> tipi = reasoner.getDataPropertyValues(category, tipification);
						String tipiT = tipi.iterator().next().getLiteral();
						categoryT.setTipification(tipiT);
						//System.out.println("tipification: " + tipiT);
					}catch(Exception e){
						System.err.println("Unknown value for property tipification[Category]");
					}
//					ArrayList<Question> questionsT = new ArrayList<Question>();
					try{
						NodeSet<OWLNamedIndividual> quest = reasoner.getObjectPropertyValues(category, questions);
						for(Node<OWLNamedIndividual> que : quest){
							//get the question
							OWLNamedIndividual q =  que.getRepresentativeElement();
							Question qT = new Question();
							
							//get the info about this specific question
							try {
								Set<OWLLiteral> qID = reasoner.getDataPropertyValues(q, questionID);
								int qIDT = Integer.parseInt(qID.iterator().next().getLiteral().trim().replace(' ','_'));
								qT.setQuestionID(qIDT);
								//System.out.println("questionID: " + qIDT);
							} catch (Exception e1) {
								System.err.println("Unknown value for property questionID[Question]");
								continue;
							}
//			    			String qDescT = "";
							try{
								Set<OWLLiteral> qDesc = reasoner.getDataPropertyValues(q, questionDesc);
								String qDescT = qDesc.iterator().next().getLiteral();
								//qDescT = qDescT.replaceAll("\254ø", "¿");
								qDescT = qDescT.replace('¿', '¿');
								qT.setQuestionDesc(qDescT);
								//qDescT = qDescT.replace('191', '¿');
								//System.out.println("questionDesc: " + qDescT);
							}catch(Exception e){
//								System.err.println("Unknown value for property questionDesc[Question]");
							}	
							//add the question to Category
							categoryT.addQuestion(qT);
			    		}
			    	}catch(Exception e){
			    			System.err.println("Unknown value for property questions[Category]");
			    	}
			    	newTicket.setCategoryNode(categoryT);
				}catch(Exception e){
					System.err.println("Unknown value for property categoryNode");
				}
			    
			    //get the attributes
			    try{
			    	for(Node<OWLNamedIndividual> att : attSet){
			    		OWLNamedIndividual attri = att.getRepresentativeElement();
		    			Attribute attT = new Attribute();
			    	
			    		//get the info about this specific attribute
		    			try {
							NodeSet<OWLNamedIndividual> aq = reasoner.getObjectPropertyValues(attri, askedQuestion);
							OWLNamedIndividual askQ = aq.iterator().next().getRepresentativeElement();
							Question aqT = new Question();
							try {
								Set<OWLLiteral> aqID = reasoner.getDataPropertyValues(askQ, questionID);
								int aqIDT = Integer.parseInt(aqID.iterator().next().getLiteral().trim().replace(' ','_'));
								aqT.setQuestionID(aqIDT);
								//System.out.println("askedQuestionID: " + aqIDT);
							} catch (Exception e) {
								System.err.println("Unkown value for property questionID[Question]");
								continue;
							}
							
							try {
								Set<OWLLiteral> aqDesc = reasoner.getDataPropertyValues(askQ, questionDesc);
								String aqDescT = aqDesc.iterator().next().getLiteral();
								aqDescT = aqDescT.replaceAll("\254ø", "¿");
								aqT.setQuestionDesc(aqDescT);
								//System.out.println("askedQuestionDesc: " + aqDescT);
							} catch (Exception e) {
//								System.err.println("Unknown value for property questiondDesc[Question]");
							}
							attT.setAskedQuestion(aqT);
						} catch (Exception e1) {
							System.err.println("Unknown value for property askedQuestion[Attribute]");
						}
			    				    	
			    		try{
			    			Set<OWLLiteral> ans = reasoner.getDataPropertyValues(attri, answer);
			    			String ansT = ans.iterator().next().getLiteral();
			    			attT.setAnswer(ansT);		
				    		//System.out.println("answer: " + ansT);
			    		}catch(Exception e){
//			    			System.err.println("Unknown value for property answer[Attribute]");
			    			attT.setAnswer("");
			    		}
			    	
			    		//put attribute in the ticket
			    		newTicket.addAttribute(attT);
			    	}
			    }catch(Exception e){
			    	System.err.println("Unknown value for property attributes");
			    }
				
			    //get the problem description
			    try{
			    	String descT = desc.iterator().next().getLiteral();
			    	newTicket.setProblemDesc(descT);
			    	//System.out.println( "problemDesc: " + descT );
			    }catch(Exception e){
			    	System.err.println("Unknown value for property problemDesc");
			    }
			    
				//get the project
				try{
					String projT = proj.iterator().next().getLiteral();
				    newTicket.setProject(projT);
					//System.out.println( "project: " + projT );
				}catch(Exception e){
					System.err.println("Unknown value for property project");
				}
				
			    //get the solvingGroups
				HashMap<String, Group> solvingGT = new HashMap<String, Group>();
				ArrayList<Operator> operatorsT = new ArrayList<Operator>();
				Group newGroup = new Group();
				for (Node<OWLNamedIndividual> g : groupSet){
					try{
						OWLNamedIndividual sg = g.getRepresentativeElement();
						Set<OWLLiteral> idG = reasoner.getDataPropertyValues(sg, groupID);
					    String sgIDT = idG.iterator().next().getLiteral().trim().replace(' ','_');
					    //System.out.println("groupID: " + sgIDT);
					    newGroup.setGroupID(sgIDT);
					    
					    try{
					    	NodeSet<OWLNamedIndividual> sgMem = reasoner.getObjectPropertyValues(sg, members);
					    	for(Node<OWLNamedIndividual> m : sgMem){
					    		OWLNamedIndividual op = m.getRepresentativeElement();
					    		
					    		//get the info about this specific operator
					    		Set<OWLLiteral> opID = reasoner.getDataPropertyValues(op, operatorID);
					    		String opIDT = opID.iterator().next().getLiteral().trim().replace(' ','_');
					    		//System.out.println("operatorID: " + opIDT);
					    		
					    		//create the operator
					    		Operator operatorT = new Operator(opIDT);
					    		
					    		//add operator to the ArrayList
					    		operatorsT.add(operatorT);
					    	}
					    }catch(Exception e){
					    	System.err.println("Unknown value for property members");
					    }
					    newGroup.setMembers(operatorsT);
					    
					    // add newGroup to groups
					    solvingGT.put(newGroup.getGroupID(), newGroup);
						
					}catch (Exception e){
						System.err.println("Unknown value for property solvingGroups");
					}
				}
				// add groups to ticket
				newTicket.setSolvingGroups(solvingGT);
								
				//get the solvingOperators
				HashMap<String, Operator> solvingOpT = new HashMap<String, Operator>();
				for (Node<OWLNamedIndividual> s: operSet){
					try{
						OWLNamedIndividual so = s.getRepresentativeElement();
						Set<OWLLiteral> opID = reasoner.getDataPropertyValues(so, operatorID);
						Operator newOperator = new Operator();
					    String opIDT = opID.iterator().next().getLiteral().trim().replace(' ','_');
					    newOperator.setOperatorID(opIDT);
					    //System.out.println("solvingOperator: " + opIDT);
					    
					    // add new operator to operators 
					    solvingOpT.put(opIDT, newOperator);
					}catch(Exception e){
						System.err.println("Unknown value for property solvingOperator");
					}
				}
				// add operators to ticket
				newTicket.setSolvingOperators(solvingOpT);
				
				//get the solutions
//				HashMap<Integer, Solution> solutionsT = new HashMap<Integer, Solution>();
				try {
					for(Node<OWLNamedIndividual> s : solSet){
						OWLNamedIndividual sol = s.getRepresentativeElement();
						Solution solutionT = new Solution();
						
						//get the info about this specific solution
						try {
							Set<OWLLiteral> solID = reasoner.getDataPropertyValues(sol, solutionID);
							int solIDT = Integer.parseInt(solID.iterator().next().getLiteral().trim().replace(' ','_'));
							solutionT.setSolutionID(solIDT);
							//System.out.println("solutionID: " + solIDT);
						} catch (Exception e1) {
							System.err.println("Unknown value for property solutionID[Solution]");
							continue;
						}
						
						try{
							Set<OWLLiteral> solDesc = reasoner.getDataPropertyValues(sol, solutionDesc);
							String solDescT = solDesc.iterator().next().getLiteral().trim().replace(' ','_');
							solutionT.setSolutionDesc(solDescT);
							//System.out.println("solutionDesc: " + solDescT);
						}catch(Exception e){
							System.err.println("Unknown value for property solutionDesc[Solution]");
						}
						
//						int timesUsT = 0;
						try{
							Set<OWLLiteral> timesUs = reasoner.getDataPropertyValues(sol, timesUsed);
							int timesUsT = Integer.parseInt(timesUs.iterator().next().getLiteral().trim().replace(' ','_'));
							solutionT.setTimesUsed(timesUsT);
							//System.out.println("timesUsed: " + timesUsT);
						}catch(Exception e){
//							System.err.println("Unknown value for property timesUsed[Solution]");
							solutionT.setTimesUsed(0);
						}
						
						try{
							Set<OWLLiteral> promVal = reasoner.getDataPropertyValues(sol, promotedValue);
							String promValT = promVal.iterator().next().getLiteral().trim().replace(' ','_');
							solutionT.setPromotedValue(promValT);
							//System.out.println("promotedValue: " + promValT);
						}catch(Exception e){
							System.err.println("Unknown value for property promotedValue[Solution]");
						}

						//put solution in Ticket
						newTicket.addSolution(solutionT);
					}
				} catch (Exception e) {
					System.err.println("Unknown value for property solutions");
				}
			    
				// at least one direct type is guaranteed to exist for each individual 
			    //OWLClass type = types.iterator().next().getRepresentativeElement();
				//System.out.println( "Type:" + type );
				
				//Add newTicket to the vector of Tickets
				tickets.add(newTicket);
				//System.out.println();			
			}
			
			// Print the vector of tickets
			int i = 0;
			for(Ticket ticket : tickets){
			//	System.out.println("Ticket " + i + ": " + ticket.getTicketID() + "#" + ticket.getCategoryNode().getIdTipi() + "#" + ticket.getProblemDesc()
			//			+ "#" + ticket.getProject() + "#" + ticket.getSolvingOperator().getOperatorID() + "#" + ticket.getSolvingGroup().getGroupID() 
			//			+ "#" + ticket.getAttributes().toString() + "#" + ticket.getSolutions().toString());
				i++;		
			}
			System.out.println(i + " Tickets parsed...");
			
			manager.removeOntology(domainOntology);
			
			return tickets;
	}
	
	/**
	 * Saves a vector of cases in the domain ontology. The ontology must be previously loaded.
	 * @param cases The vector of cases to save in the domain ontology
	 * @param destFile The name of the ontology to be saved
	 */
	
	public void saveCasesInDomainOntology(Vector<Case> cases, String ontoFile, String destFile) throws Exception {
		
		this.loadOntology(ontoFile);
		
		// hold changes in the Queue as they are done
        Queue<AddAxiom> changes = new LinkedList<AddAxiom>();
        
        // create a Factory for our Datatypes
        factory = manager.getOWLDataFactory();
        
        int tID = (int) System.currentTimeMillis();
        if (tID < 0) tID = -1 * tID;
//        int tID=0;
		
		for (Case vT : cases){
			
			if (vT.getSolutions().isEmpty()) continue;
			
			//System.out.println(vT.toString());
			
			OWLNamedIndividual ticket = null;
			tID++;
			
			// Creating Ticket Individual
			try {
				ticket = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Ticket" + tID));
				//System.out.println("CREATING TICKET Ticket" + tID);
				OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Ticket"));
				OWLClassAssertionAxiom ticketaxiom = factory.getOWLClassAssertionAxiom(Class, ticket);
				changes.add(new AddAxiom(domainOntology, ticketaxiom));
			} catch (Exception e) {
				System.err.println("Unknown value for property ticketID");
				continue;
			}
					
			// creating Data property ticketID
			try {
				OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#ticketID"));
				OWLLiteral owlL = factory.getOWLLiteral(tID);
				OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, ticket, owlL);
				changes.add(new AddAxiom(domainOntology, addProp));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			// creating Data property problemDesc
			String tDesc = "";
			try {
				tDesc = this.filter(vT.getProblemDesc());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (tDesc != null)
			{
				tDesc=tDesc.trim().replace(' ','_');
				OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#problemDesc"));
				OWLLiteral owlL = factory.getOWLLiteral(tDesc);
				OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, ticket, owlL);
				changes.add(new AddAxiom(domainOntology, addProp));
			}
			
			// creating Data property project
			String tProj="";
			try {
				tProj = this.filter(vT.getProject());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (tProj != null)
			{
				tProj=tProj.trim().replace(' ','_');
				OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#project"));
				OWLLiteral owlL = factory.getOWLLiteral(tProj);
				OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, ticket, owlL);
				changes.add(new AddAxiom(domainOntology, addProp));
			}
		
			// creating Object property categoryNode
			Category tCat = null;
			try {
				tCat = vT.getCategoryNode();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OWLNamedIndividual category = null;
			if (tCat != null)
			{
			
				int catID = -1;
				try {
					catID = tCat.getIdTipi();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Creating Category Individual
				if (catID != -1)
				{
					category = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Category" + Integer.toString(catID)));
					OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Category"));
					OWLClassAssertionAxiom categoryaxiom = factory.getOWLClassAssertionAxiom(Class, category);
					changes.add(new AddAxiom(domainOntology, categoryaxiom));
				}
			
				// creating Data property idTipi
				if (catID != -1)
				{
					OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#idTipi"));
					OWLLiteral owlL = factory.getOWLLiteral(catID);
					OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, category, owlL);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			
				int catPar = -1;
				try {
					catPar = tCat.getParentID();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// creating Data property parentID
				if (catPar != -1)
				{
					OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#parentID"));
					OWLLiteral owlL = factory.getOWLLiteral(catPar);
					OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, category, owlL);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
				
				String catTipi = "";
				try {
					catTipi = this.filter(tCat.getTipification());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// creating Data property tipification
				if (catTipi != null)
				{
					catTipi=catTipi.trim().replace(' ','_');
					OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#tipification"));
					OWLLiteral owlL = factory.getOWLLiteral(catTipi);
					OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, category, owlL);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
				
				ArrayList<Question> catQue = null;
				try {
					catQue = tCat.getQuestions();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (catQue != null){
					for (Question q: catQue){
					
						// creating Object property Question
						OWLNamedIndividual question = null;
						int qID = -1;
						try {
							qID = q.getQuestionID();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (qID != -1)
						{
					
							// Creating Question Individual
							{
								question = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Question" + Integer.toString(qID)));
								OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Question"));
								OWLClassAssertionAxiom questionaxiom = factory.getOWLClassAssertionAxiom(Class, question);
								changes.add(new AddAxiom(domainOntology, questionaxiom));
							}
					
							// creating Data property questionID
							{
								OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionID"));
								OWLLiteral owlL = factory.getOWLLiteral(qID);
								OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
					
							String qDesc = "";
							try {
								qDesc = this.filter(q.getQuestionDesc());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// creating Data property questionDesc
							if (qDesc != null)
							{
								qDesc=qDesc.trim().replace(' ','_');
								OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionDesc"));
								OWLLiteral owlL = factory.getOWLLiteral(qDesc);
								OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
						
							// adding question to category
							OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#questions"));
							OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, category, question);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					}
				}
				
				
				// adding categoryNode to ticket
				OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#categoryNode"));
				OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, category);
				changes.add(new AddAxiom(domainOntology, addProp));
				
			}
			
			// creating Object property attributes
			HashMap<Integer, Attribute> tAtt = null;
			try {
				tAtt = (HashMap<Integer, Attribute>) vT.getAttributes();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OWLNamedIndividual attribute = null;
			if (tAtt != null){
				Iterator<Attribute> ite = tAtt.values().iterator();
				while (ite.hasNext()){
					Attribute at = ite.next();
					
					int aID = -1;
					try {
						aID = at.getAskedQuestion().getQuestionID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String aAns = "";
					try {
						aAns = this.filter(at.getAnswer());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
//					if (aAns == null) aAns = "";
					
					// Creating Attribute Individual
					if (aID != -1)
					{
						aAns=aAns.trim().replace(' ','_');
						attribute = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Attribute" + Integer.toString(aID) + aAns));
						OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Attribute"));
						OWLClassAssertionAxiom attributeaxiom = factory.getOWLClassAssertionAxiom(Class, attribute);
						changes.add(new AddAxiom(domainOntology, attributeaxiom));
					}
					
					// creating Object property askedQuestion
					Question aQue = null;
					try {
						aQue = at.getAskedQuestion();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					OWLNamedIndividual question = null;
					
					if (aQue != null){
						// creating Object property Question
						int qID = -1;
						try {
							qID = aQue.getQuestionID();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (qID != -1)
						{
					
							// Creating Question Individual
							{
								question = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Question" + Integer.toString(qID)));
								OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Question"));
								OWLClassAssertionAxiom questionaxiom = factory.getOWLClassAssertionAxiom(Class, question);
								changes.add(new AddAxiom(domainOntology, questionaxiom));
							}
					
							// creating Data property questionID
							{
								OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionID"));
								OWLLiteral owlL = factory.getOWLLiteral(qID);
								OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
					
							String qDesc = "";
							try {
								qDesc = this.filter(aQue.getQuestionDesc());
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// creating Data property questionDesc
							if (qDesc != null)
							{
								qDesc=qDesc.trim().replace(' ','_');
								OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#questionDesc"));
								OWLLiteral owlL = factory.getOWLLiteral(qDesc);
								OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, question, owlL);
								changes.add(new AddAxiom(domainOntology, addProp));
							}
						
							// adding question to attribute
							OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#askedQuestion"));
							OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, attribute, question);
							changes.add(new AddAxiom(domainOntology, addProp));
						}

					}
					
					// creating Data property answer
					if (aAns != null)
					{
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#answer"));
						OWLLiteral owlL = factory.getOWLLiteral(aAns);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, attribute, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
					
					// adding attribute to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#attributes"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, attribute);
					changes.add(new AddAxiom(domainOntology, addProp));
					
				}
				
			}
			
			// creating Object property solvingOperators
			HashMap<String, Operator> tOp = null;
			try {
				tOp = vT.getSolvingOperators();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OWLNamedIndividual operator = null;
			if (tOp != null) {
				Iterator<String> it = tOp.keySet().iterator();
				while (it.hasNext()) {
					String opID = "";
					try {
						opID = this.filter(it.next());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (opID.equals("") || opID.isEmpty()){
//						tOp.remove(opID);
						continue;
					}
					if (!opID.isEmpty()){
						// Creating Operator Individual
						{
							opID=opID.trim().replace(' ','_');
							operator = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Operator" + opID));
							OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Operator"));
							OWLClassAssertionAxiom operatoraxiom = factory.getOWLClassAssertionAxiom(Class, operator);
							changes.add(new AddAxiom(domainOntology, operatoraxiom));
						}
						
						// creating Data property operatorID
						{
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#operatorID"));
							OWLLiteral owlL = factory.getOWLLiteral(opID);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, operator, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					}
				
					// adding operator to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#solvingOperators"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, operator);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			}
			
			// creating Object property solvingGroups
			HashMap<String, Group> tGr = null;
			try {
				tGr = vT.getSolvingGroups();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OWLNamedIndividual group = null;
			if (tGr!= null) {
				Iterator<String> itg = tGr.keySet().iterator();
				while (itg.hasNext()){
					String grID = "";
					try {
						grID = this.filter(itg.next());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (grID.equals("") || grID.isEmpty()) {
//						tGr.remove(grID);
						continue;
					}
				
					if (grID != null){
						// Creating Group Individual
						{
							grID=grID.trim().replace(' ','_');
							group = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Group" + grID));
							OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Group"));
							OWLClassAssertionAxiom groupaxiom = factory.getOWLClassAssertionAxiom(Class, group);
							changes.add(new AddAxiom(domainOntology, groupaxiom));
						}
					
						// creating Data property groupID
						{
							OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#groupID"));
							OWLLiteral owlL = factory.getOWLLiteral(grID);
							OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, group, owlL);
							changes.add(new AddAxiom(domainOntology, addProp));
						}
					
						// creating Object property members
						ArrayList<Operator> grMem = null;
						try {
							grMem = tGr.get(grID).getMembers();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (grMem != null){
							for (Operator m : grMem){
								OWLNamedIndividual operator1 = null;
								String opID = "";
								try {
									opID = this.filter(m.getOperatorID());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if (opID.equals("") || opID.isEmpty())
									continue;
								
								if (!opID.isEmpty()){
									// Creating Operator Individual
									{
										opID=opID.trim().replace(' ','_');
										operator1 = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Operator" + opID));
										OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Operator"));
										OWLClassAssertionAxiom operatoraxiom = factory.getOWLClassAssertionAxiom(Class, operator1);
										changes.add(new AddAxiom(domainOntology, operatoraxiom));
									}
								
									// creating Data property operatorID
									{
										OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#operatorID"));
										OWLLiteral owlL = factory.getOWLLiteral(opID);
										OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, operator1, owlL);
										changes.add(new AddAxiom(domainOntology, addProp));
									}
								}
								
								// adding operator to group
								OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#members"));
								OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, group, operator1);
								changes.add(new AddAxiom(domainOntology, addProp));
						
							}
						}
					}
				
					// adding group to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#solvingGroups"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, group);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			}
				
			// creating Object property solutions
			HashMap<Integer, Solution> tSol = null;
			try {
				tSol = (HashMap<Integer, Solution>) vT.getSolutions();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			OWLNamedIndividual solution = null;
			if (tSol != null){
				Iterator<Solution> ite = tSol.values().iterator();
				while (ite.hasNext()){
					Solution s = ite.next();
					
					int sID = -1;
					try {
						sID = s.getSolutionID();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Creating Solution Individual
					if (sID != -1)
					{
						solution = factory.getOWLNamedIndividual(IRI.create(ontoID + "#Solution" + sID));
						OWLClass Class = factory.getOWLClass(IRI.create(ontoID + "#Solution"));
						OWLClassAssertionAxiom solutionaxiom = factory.getOWLClassAssertionAxiom(Class, solution);
						changes.add(new AddAxiom(domainOntology, solutionaxiom));
					}
					
					// creating Data property solutionID
					{
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#solutionID"));
						OWLLiteral owlL = factory.getOWLLiteral(sID);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
					
					String sDesc = "";
					try {
						sDesc = this.filter(s.getSolutionDesc());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// creating Data property solutionDesc
					if (sDesc != null)
					{
						sDesc=sDesc.trim().replace(' ','_');
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#solutionDesc"));
						OWLLiteral owlL = factory.getOWLLiteral(sDesc);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
					
					String sVal = "";
					try {
						sVal = this.filter(s.getPromotedValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// creating Data property promotedValue
					if (sVal != null)
					{
						sVal=sVal.trim().replace(' ','_');
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#promotedValue"));
						OWLLiteral owlL = factory.getOWLLiteral(sVal);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
					
					int sUse=0;
					try {
						sUse = s.getTimesUsed();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// creating Data property timesUsed
					// TODO revise the default values for times used
//					if (sUse != 0)
					{
						OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID + "#timesUsed"));
						OWLLiteral owlL = factory.getOWLLiteral(sUse);
						OWLDataPropertyAssertionAxiom addProp = factory.getOWLDataPropertyAssertionAxiom(owlP, solution, owlL);
						changes.add(new AddAxiom(domainOntology, addProp));
					}
					
					// adding solution to ticket
					OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID + "#solutions"));
					OWLObjectPropertyAssertionAxiom addProp = factory.getOWLObjectPropertyAssertionAxiom(owlP, ticket, solution);
					changes.add(new AddAxiom(domainOntology, addProp));
				}
			}
		}
		
		// apply all changes to the Model
		for (AddAxiom addAxiom : changes) {
			manager.applyChange(addAxiom);
		}
		
		// save the Ontology to file
		OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat(); 
		RDFXMLOntologyFormat rdfxmlFormat = new RDFXMLOntologyFormat();
		
		OWLOntologyFormat format = manager.getOntologyFormat(domainOntology);
		System.out.println(" format: " + format); 
		
//		if(format.isPrefixOWLOntologyFormat()) {
//			owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
//		}
		
		File file = new File(destFile);
		
		System.out.println("OWLDOMAINPARSER: saving ontology " + destFile);
		
//		for (Case c : cases) {
//			c.printCase(destFile);
//		}
		
		try {
//			manager.saveOntology(domainOntology, rdfxmlFormat, IRI.create(file.toURI()));
			manager.saveOntology(domainOntology, IRI.create(file.toURI())); 
			System.out.println("OWLDOMAINPARSER: " + destFile + " saved");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("++++++++++> ERROR " + e.getCause() + " SAVING: " + destFile);
		} 
		manager.removeOntology(domainOntology);

	}
	
	/**
	 * Parses the domain ontology in a vector of cases. The ontology must be previously loaded.
	 * @return cases The vector of cases to parse the domain ontology
	 */
	
	public Vector<Case> parseDomainOntologyInCases(String ontoFile) throws Exception {
		
			this.loadOntology(ontoFile);
		
			Vector<Case> cases = new Vector<Case>();
//			Pellet pellet = new Pellet();
//			VersionInfo ver=Pellet.getVersionInfo();
//			System.out.println(ver.getVersionString());
			com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory pell = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory.getInstance();
			reasoner = pell.createReasoner(domainOntology);
			
			reasoner.getKB().realize();
//			reasoner.getKB().printClassTree();
			
			System.out.println("Reasoner Loaded");
			
			// Ask the reasoner to do all the necessary work now 
			reasoner.precomputeInferences();
			
			// We can determine if the ontology is actually consistent (in this case, it should be).
			boolean consistent = reasoner.isConsistent();
			System.out.println("Consistent: " + consistent);
//			System.out.println("\n"); 
			
			// We can easily get a list of unsatisfiable classes. (A class is unsatisfiable if it
			// can't possibly have any instances). Note that the getUnsatisfiableClasses method
			// is really just a convenience method for obtaining the classes that are equivalent
			// to owl:Nothing. In our case there should be just one unsatisfiable class - "mad_cow"
			// We ask the reasoner for the unsatisfiable classes, which returns the bottom node
			// in the class hierarchy (an unsatisfiable class is a subclass of every class).
			Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
			// This node contains owl:Nothing and all the classes that are equivalent to owl:Nothing -
			// i.e. the unsatisfiable classes.
			// We just want to print out the unsatisfiable classes excluding owl:Nothing, and we can
			// used a convenience method on the node to get these
			Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
			if (!unsatisfiable.isEmpty()) {
				System.out.println("The following classes are unsatisfiable: ");
			 	for(OWLClass cls : unsatisfiable) {
			 		System.out.println(" " + cls);
			 	}
			}
			else {
				System.out.println("There are no unsatisfiable classes");
			}
//			System.out.println("\n");
			
			// create property and resources to query the reasoner
			// for Ticket
			OWLClass TicketOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Ticket"));
			OWLDataProperty ticketID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#ticketID"));
			OWLObjectProperty categoryNode = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#categoryNode"));
			OWLDataProperty  problemDesc = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#problemDesc"));
			OWLDataProperty project = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#project"));
			OWLObjectProperty solvingOperators = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#solvingOperators"));
			OWLObjectProperty solvingGroups = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#solvingGroups"));
			OWLObjectProperty attributes = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#attributes"));
			OWLObjectProperty solutions = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#solutions"));
			
			// for Attribute
			OWLClass AttributeOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Attribute"));
			OWLObjectProperty askedQuestion = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#askedQuestion"));
			OWLDataProperty answer = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#answer"));
			
			// for Category
			OWLClass CategoryOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Category"));
			OWLDataProperty idTipi = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#idTipi"));
			OWLDataProperty parentID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#parentID"));
			OWLDataProperty tipification = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#tipification"));
			OWLObjectProperty questions = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#questions"));
			
			// for Group
			OWLClass GroupOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Group"));
			OWLDataProperty groupID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#groupID"));
			OWLObjectProperty members = manager.getOWLDataFactory().getOWLObjectProperty(IRI.create(ontoID + "#members"));
			
			// for Operator
			OWLClass OperatorOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Operator"));
			OWLDataProperty operatorID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#operatorID"));
			
			// for Question
			OWLClass QuestionOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Question"));
			OWLDataProperty questionDesc = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#questionDesc"));
			OWLDataProperty questionID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#questionID"));
			
			// for Solution
			OWLClass SolutionOWL = manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID + "#Solution"));
			OWLDataProperty promotedValue = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#promotedValue"));
			OWLDataProperty solutionDesc = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#solutionDesc"));
			OWLDataProperty solutionID = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#solutionID"));
			OWLDataProperty timesUsed = manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID + "#timesUsed"));
			
			
			// get all instances of Ticket class
			NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances( TicketOWL, false);
			
			/*System.out.println("Number of individuals: "+individuals.getNodes().size());
			System.out.println("Instances of Ticket: ");
			for(Node<OWLNamedIndividual> ind : individuals) {
			 	System.out.println(" " + ind);
			}
			System.out.println("\n"); */
						
			for(Node<OWLNamedIndividual> sameInd : individuals) {
				
				Case newCase = new Case();
				
				// sameInd contains information about the individual (and all other individuals that were inferred to be the same)
				OWLNamedIndividual ind =  sameInd.getRepresentativeElement();
				
			    // get the info about this specific individual
				Set<OWLLiteral> id = reasoner.getDataPropertyValues(ind, ticketID); 
				NodeSet<OWLNamedIndividual> cat = reasoner.getObjectPropertyValues( ind, categoryNode );
				Set<OWLLiteral> desc = reasoner.getDataPropertyValues(ind, problemDesc);
				Set<OWLLiteral> proj = reasoner.getDataPropertyValues(ind, project);
				NodeSet<OWLNamedIndividual> operSet = reasoner.getObjectPropertyValues( ind, solvingOperators );
				NodeSet<OWLNamedIndividual> groupSet = reasoner.getObjectPropertyValues( ind, solvingGroups );
				NodeSet<OWLNamedIndividual> attSet = reasoner.getObjectPropertyValues( ind, attributes );
				NodeSet<OWLNamedIndividual> solSet = reasoner.getObjectPropertyValues( ind, solutions );
				
				NodeSet<OWLClass> types = reasoner.getTypes( ind, true );		    
			    
				// get the categoryNode
				Category categoryT = new Category();
			    OWLNamedIndividual category = cat.iterator().next().getRepresentativeElement();
			    Set<OWLLiteral> idTip = reasoner.getDataPropertyValues(category, idTipi);
			    int idTipT = Integer.parseInt(idTip.iterator().next().getLiteral().trim().replace(' ','_'));
			    categoryT.setIdTipi(idTipT);
			    //System.out.println("idTipi: " + idTipT);
			    Set<OWLLiteral> parentIDT = reasoner.getDataPropertyValues(category, parentID);
			    int parIDT = Integer.parseInt(parentIDT.iterator().next().getLiteral().trim().replace(' ','_'));
			    categoryT.setParentID(parIDT);
			    //System.out.println("parentID: " + parIDT);
//			    String tipiT = "";
			    try{
			    	Set<OWLLiteral> tipi = reasoner.getDataPropertyValues(category, tipification);
			    	String tipiT = tipi.iterator().next().getLiteral();
			    	categoryT.setTipification(tipiT);
			    }catch(Exception e){
			    	System.err.println("Unknown value for property tipification");
			    }
			    //System.out.println("tipification: " + tipiT);
			    ArrayList<Question> questionsT = new ArrayList<Question>();
			    try{
			    	NodeSet<OWLNamedIndividual> quest = reasoner.getObjectPropertyValues(category, questions);
			    	for(Node<OWLNamedIndividual> que : quest){
			    		//get the question
			    		OWLNamedIndividual q =  que.getRepresentativeElement();
				    	Question qT = new Question();
			    	
			    		//get the info about this specific question
			    		Set<OWLLiteral> qID = reasoner.getDataPropertyValues(q, questionID);
			    		int qIDT = Integer.parseInt(qID.iterator().next().getLiteral().trim().replace(' ','_'));
			    		qT.setQuestionID(qIDT);
			    		//System.out.println("questionID: " + qIDT);
//			    		String qDescT = "";
			    		try{
			    			Set<OWLLiteral> qDesc = reasoner.getDataPropertyValues(q, questionDesc);
			    			String qDescT = qDesc.iterator().next().getLiteral();
			    			//qDescT = qDescT.replaceAll("\254ø", "¿");
			    			qDescT = qDescT.replace('¿', '¿');
			    			qT.setQuestionDesc(qDescT);
			    			//qDescT = qDescT.replace('191', '¿');
			    		}catch(Exception e){
			    			System.err.println("Unknown value for property questionDesc");
			    		}
			    		//System.out.println("questionDesc: " + qDescT);
			    	
			    		//add the question to the ArrayList
			    		questionsT.add(qT);
			    		}
			    	}catch(Exception e){
			    		System.err.println("Unknown value for property questions");
			    	}
			    newCase.setCategoryNode(categoryT);
			    
			    //get the attributes
			    HashMap<Integer, Attribute> attriT = new HashMap<Integer, Attribute>();
			    try{
			    	for(Node<OWLNamedIndividual> att : attSet){
			    		OWLNamedIndividual attri = att.getRepresentativeElement();
		    			Attribute attT = new Attribute();
			    	
			    		//get the info about this specific attribute
			    		NodeSet<OWLNamedIndividual> aq = reasoner.getObjectPropertyValues(attri, askedQuestion);
			    		OWLNamedIndividual askQ = aq.iterator().next().getRepresentativeElement();
			    		Question aqT = new Question();
			    		Set<OWLLiteral> aqID = reasoner.getDataPropertyValues(askQ, questionID);
			    		int aqIDT = Integer.parseInt(aqID.iterator().next().getLiteral().trim().replace(' ','_'));
			    		aqT.setQuestionID(aqIDT);
			    		//System.out.println("askedQuestionID: " + aqIDT);			    	
			    		try {
							Set<OWLLiteral> aqDesc = reasoner.getDataPropertyValues(askQ, questionDesc);
							String aqDescT = aqDesc.iterator().next().getLiteral();
							aqDescT = aqDescT.replaceAll("\254ø", "¿");
							aqT.setQuestionDesc(aqDescT);
						} catch (Exception e1) {
							System.err.println("Unknown value for property questionDescription");
							e1.printStackTrace();
						}
			    		//System.out.println("askedQuestionDesc: " + aqDescT);
			    		attT.setAskedQuestion(aqT);
			    				    	
//			    		String ansT = "";
			    		try{
			    			Set<OWLLiteral> ans = reasoner.getDataPropertyValues(attri, answer);
			    			String ansT = ans.iterator().next().getLiteral();
			    			attT.setAnswer(ansT);			
			    		}catch(Exception e){
			    			System.err.println("Unknown value for property answer");
			    		}
			    		//System.out.println("answer: " + ansT);
			    	
			    		//put attribute in the HashMap
			    		attriT.put(aqIDT, attT);
			    	}
			    }catch(Exception e){
			    	System.err.println("Unknown value for property attributes");
			    }
			    newCase.setAttributes(attriT);
				
			    //get the problem description
//			    String descT = "";
			    try{
			    	String descT = desc.iterator().next().getLiteral();
			    	newCase.setProblemDesc(descT);
			    }catch(Exception e){
			    	System.err.println("Unknown value for property problemDesc");
			    }
				//System.out.println( "problemDesc: " + descT );
			    
				//get the project
//				String projT = "";
				try{
					String projT = proj.iterator().next().getLiteral();
				    newCase.setProject(projT);
				}catch(Exception e){
					System.err.println("Unknown value for property project");
				}
				//System.out.println( "project: " + projT );
				
			    //get the solvingGroups
				HashMap<String, Group> solvingGT = new HashMap<String, Group>();
				ArrayList<Operator> operatorsT = new ArrayList<Operator>();
				Group newGroup = new Group();
				for (Node<OWLNamedIndividual> g : groupSet){
					try{
						OWLNamedIndividual sg = g.getRepresentativeElement();
						Set<OWLLiteral> idG = reasoner.getDataPropertyValues(sg, groupID);
					    String sgIDT = idG.iterator().next().getLiteral().trim().replace(' ','_');
					    //System.out.println("groupID: " + sgIDT);
					    newGroup.setGroupID(sgIDT);
					    
					    try{
					    	NodeSet<OWLNamedIndividual> sgMem = reasoner.getObjectPropertyValues(sg, members);
					    	for(Node<OWLNamedIndividual> m : sgMem){
					    		OWLNamedIndividual op = m.getRepresentativeElement();
					    		
					    		//get the info about this specific operator
					    		Set<OWLLiteral> opID = reasoner.getDataPropertyValues(op, operatorID);
					    		String opIDT = opID.iterator().next().getLiteral().trim().replace(' ','_');
					    		//System.out.println("operatorID: " + opIDT);
					    		
					    		//create the operator
					    		Operator operatorT = new Operator(opIDT);
					    		
					    		//add operator to the ArrayList
					    		operatorsT.add(operatorT);
					    	}
					    }catch(Exception e){
					    	System.err.println("Unknown value for property members");
					    }
					    newGroup.setMembers(operatorsT);
					    
					    // add newGroup to groups
					    solvingGT.put(newGroup.getGroupID(), newGroup);
						
					}catch (Exception e){
						System.err.println("Unknown value for property solvingGroups");
					}
				}
				// add groups to ticket
				newCase.setSolvingGroups(solvingGT);
								
				//get the solvingOperators
				HashMap<String, Operator> solvingOpT = new HashMap<String, Operator>();
				for (Node<OWLNamedIndividual> s: operSet){
					try{
						OWLNamedIndividual so = s.getRepresentativeElement();
						Set<OWLLiteral> opID = reasoner.getDataPropertyValues(so, operatorID);
						Operator newOperator = new Operator();
					    String opIDT = opID.iterator().next().getLiteral().trim().replace(' ','_');
					    newOperator.setOperatorID(opIDT);
					    //System.out.println("solvingOperator: " + opIDT);
					    
					    // add new operator to operators 
					    solvingOpT.put(opIDT, newOperator);
					}catch(Exception e){
						System.err.println("Unknown value for property solvingOperator");
					}
				}
				// add operators to ticket
				newCase.setSolvingOperators(solvingOpT);
				
				//get the solutions
				HashMap<Integer, Solution> solutionsT = new HashMap<Integer, Solution>();
				for(Node<OWLNamedIndividual> s : solSet){
					OWLNamedIndividual sol = s.getRepresentativeElement();
					Solution solutionT = new Solution();
					
					//get the info about this specific solution
					Set<OWLLiteral> solID = reasoner.getDataPropertyValues(sol, solutionID);
					int solIDT = Integer.parseInt(solID.iterator().next().getLiteral().trim().replace(' ','_'));
					solutionT.setSolutionID(solIDT);
					//System.out.println("solutionID: " + solIDT);
//					String solDescT = "";
					try{
						Set<OWLLiteral> solDesc = reasoner.getDataPropertyValues(sol, solutionDesc);
						String solDescT = solDesc.iterator().next().getLiteral().trim().replace(' ','_');
						solutionT.setSolutionDesc(solDescT);
						//System.out.println("solutionDesc: " + solDescT);
					}catch(Exception e){
						System.err.println("Unknown value for property solutionDesc");
					}
//					int timesUsT = 0;
					try{
						Set<OWLLiteral> timesUs = reasoner.getDataPropertyValues(sol, timesUsed);
						int timesUsT = Integer.parseInt(timesUs.iterator().next().getLiteral().trim().replace(' ','_'));
						solutionT.setTimesUsed(timesUsT);
						//System.out.println("timesUsed: " + timesUsT);
					}catch(Exception e){
						System.err.println("Unknown value for property timesUsed");
					}
//					String promValT = "";
					try{
						Set<OWLLiteral> promVal = reasoner.getDataPropertyValues(sol, promotedValue);
						String promValT = promVal.iterator().next().getLiteral().trim().replace(' ','_');
						solutionT.setPromotedValue(promValT);
						//System.out.println("promotedValue: " + promValT);
					}catch(Exception e){
						System.err.println("Unknown value for property promotedValue");
					}

					//put solution in the HashMap
					solutionsT.put(solIDT, solutionT);
				}
				newCase.setSolutions(solutionsT);
			    
				// at least one direct type is guaranteed to exist for each individual 
			    //OWLClass type = types.iterator().next().getRepresentativeElement();
				//System.out.println( "Type:" + type );
				
				//Add newTicket to the vector of Tickets
				cases.add(newCase);
				//System.out.println();			
			}
			
			// Print the vector of tickets
			int i = 0;
			for(Case cas : cases){
			//	System.out.println("Ticket " + i + ": " + ticket.getTicketID() + "#" + ticket.getCategoryNode().getIdTipi() + "#" + ticket.getProblemDesc()
			//			+ "#" + ticket.getProject() + "#" + ticket.getSolvingOperator().getOperatorID() + "#" + ticket.getSolvingGroup().getGroupID() 
			//			+ "#" + ticket.getAttributes().toString() + "#" + ticket.getSolutions().toString());
				i++;		
			}
			System.out.println(i + " Tickets parsed...");
			
			manager.removeOntology(domainOntology);
			
			return cases;
		
	}
	
	public String filter(String userDest) {
		Pattern p = Pattern.compile("[@,:.;!\"`\\[\\]|\\#$~%&¬/()='¿?¡+*\n]");
		Matcher m = p.matcher(userDest);
		if (m.find())
			userDest = m.replaceAll("");
		return userDest;
	}
}
