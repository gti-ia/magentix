package es.upv.dsic.gti_ia.argAgents.argCBR;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.semanticweb.owlapi.apibinding.OWLManager;
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

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.AcceptabilityStatus;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentJustification;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentProblem;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentationScheme;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Author;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Conclusion;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DialogueGraph;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Group;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Norm;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Premise;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ValPref;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgNode.NodeType;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.ArgumentSolution.ArgumentType;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialContext.DependencyRelation;

/**
 * @author stella Class to parse the OWL ArgCBROnto ontology into a vector of
 *         argument-cases
 */

public class OWLArgCBRParser {

	private String ontoID = null;
	private OWLOntologyManager manager = null;
	private OWLOntology argOntology = null;
	private OWLDataFactory factory = null;
	private IRI documentIRI = null;
	private PelletReasoner reasoner = null;

	// OWLReasoner reasoner;
	// OWLReasonerFactory reasonerFactory;

	/**
	 * Constructor of the class OWlArgCBRParser
	 */
	public OWLArgCBRParser() {

	}

	/**
	 * Loads the ArgCBR ontology and assigns it a unique identifier IRI
	 */

	public void loadOntology(String ontoFile) {
		try {
			// Get hold of an ontology manager
			manager = OWLManager.createOWLOntologyManager();

			// Load an ontology from the web
			// IRI iri = IRI.create(ontoFile);
			// domainOntology = manager.loadOntologyFromOntologyDocument(iri);
			// System.out.println("Loaded ontology: " + domainOntology);

			// Remove the ontology so that we can load a local copy.
			// manager.removeOntology(domainOntology);

			// Load an ontology from a file
			File file = new File(ontoFile);

			// Load the ontology
			argOntology = manager.loadOntologyFromOntologyDocument(file);
			System.out.println("Loaded ontology: " + argOntology);

			// Obtain the location where the ontology was loaded from
			documentIRI = manager.getOntologyDocumentIRI(argOntology);
			System.out.println(" from: " + documentIRI);

			// Obtain the ontology ID
			ontoID = argOntology.getOntologyID().getOntologyIRI().toString();
			System.out.println("ontoID: " + ontoID);

			// Remove the ontology again so we can reload it later
			// manager.removeOntology(domainOntology);

		} catch (OWLOntologyCreationIOException e) {
			// IOExceptions during loading get wrapped in an
			// OWLOntologyCreationIOException
			IOException ioException = e.getCause();
			if (ioException instanceof FileNotFoundException) {
				System.out.println("Could not load ontology. File not found: "
						+ ioException.getMessage());
			} else if (ioException instanceof UnknownHostException) {
				System.out.println("Could not load ontology. Unknown host: "
						+ ioException.getMessage());
			} else {
				System.out.println("Could not load ontology: "
						+ ioException.getClass().getSimpleName() + " "
						+ ioException.getMessage());
			}
		} catch (UnparsableOntologyException e) {
			// If there was a problem loading an ontology because there are
			// syntax errors in the document (file) that
			// represents the ontology then an UnparsableOntologyException is
			// thrown
			System.out.println("Could not parse the ontology: "
					+ e.getMessage());
			// A map of errors can be obtained from the exception
			Map<OWLParser, OWLParserException> exceptions = e.getExceptions();
			// The map describes which parsers were tried and what the errors
			// were
			for (OWLParser parser : exceptions.keySet()) {
				System.out.println("Tried to parse the ontology with the "
						+ parser.getClass().getSimpleName() + " parser");
				System.out.println("Failed because: "
						+ exceptions.get(parser).getMessage());
			}
		} catch (UnloadableImportException e) {
			// If our ontology contains imports and one or more of the imports
			// could not be loaded then an
			// UnloadableImportException will be thrown (depending on the
			// missing imports handling policy)
			System.out.println("Could not load import: "
					+ e.getImportsDeclaration());
			// The reason for this is specified and an
			// OWLOntologyCreationException
			OWLOntologyCreationException cause = e
					.getOntologyCreationException();
			System.out.println("Reason: " + cause.getMessage());
		} catch (OWLOntologyCreationException e) {
			System.out.println("Could not load ontology: " + e.getMessage());
		}
	}

	/**
	 * Parses the ArgCBROnto ontology in a vector of argument-cases. The
	 * ontology must be previously loaded.
	 * 
	 * @return argCases The vector of argument-cases to parse the ArgCBROnto
	 *         ontology
	 */

	public Vector<ArgumentCase> parseArgCBROnto(String ontoFile)
			throws Exception {

		this.loadOntology(ontoFile);

		Vector<ArgumentCase> argCases = new Vector<ArgumentCase>();
		// Pellet pellet = new Pellet();
		// VersionInfo ver=Pellet.getVersionInfo();
		// System.out.println(ver.getVersionString());
		com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory pell = com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory
				.getInstance();
		reasoner = pell.createReasoner(argOntology);

		reasoner.getKB().realize();
//		reasoner.getKB().printClassTree();

		System.out.println("Reasoner Loaded");

		// Ask the reasoner to do all the necessary work now
		reasoner.precomputeInferences();

		// We can determine if the ontology is actually consistent (in this
		// case, it should be).
		boolean consistent = reasoner.isConsistent();
		System.out.println("Consistent: " + consistent);
		// System.out.println("\n");

		// We can easily get a list of unsatisfiable classes. (A class is
		// unsatisfiable if it
		// can't possibly have any instances). Note that the
		// getUnsatisfiableClasses method
		// is really just a convenience method for obtaining the classes that
		// are equivalent
		// to owl:Nothing. In our case there should be just one unsatisfiable
		// class - "mad_cow"
		// We ask the reasoner for the unsatisfiable classes, which returns the
		// bottom node
		// in the class hierarchy (an unsatisfiable class is a subclass of every
		// class).
		Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
		// This node contains owl:Nothing and all the classes that are
		// equivalent to owl:Nothing -
		// i.e. the unsatisfiable classes.
		// We just want to print out the unsatisfiable classes excluding
		// owl:Nothing, and we can
		// used a convenience method on the node to get these
		Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
		if (!unsatisfiable.isEmpty()) {
			System.out.println("The following classes are unsatisfiable: ");
			for (OWLClass cls : unsatisfiable) {
				System.out.println(" " + cls);
			}
		} else {
			System.out.println("There are no unsatisfiable classes");
		}
		// System.out.println("\n");

		// create property and resources to query the reasoner

		// for ArgumentCase
		OWLClass ArgCaseOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#ArgumentCase"));
		OWLDataProperty argCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty argCaseDate = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#creationDate"));
		OWLObjectProperty argCaseProblem = manager.getOWLDataFactory()
				.getOWLObjectProperty(
						IRI.create(ontoID + "#hasArgumentProblem"));
		OWLObjectProperty argCaseSolution = manager.getOWLDataFactory()
				.getOWLObjectProperty(
						IRI.create(ontoID + "#hasArgumentSolution"));
		OWLObjectProperty argCaseJustification = manager.getOWLDataFactory()
				.getOWLObjectProperty(
						IRI.create(ontoID + "#hasArgumentJustification"));

		// for ArgumentProblem
		OWLClass ArgProbOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#ArgumentProblem"));
		OWLObjectProperty domainContext = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasDomainContext"));
		OWLObjectProperty socialContext = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasSocialContext"));

		// for DomainContext
		OWLClass DomContOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#DomainContext"));
		OWLObjectProperty premises = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasPremise"));

		// for Premise
		// OWLClass PremiseOWL =
		// manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID +
		// "#Premise"));
		// OWLDataProperty premID =
		// manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID +
		// "#hasID"));
		// OWLDataProperty premName =
		// manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID +
		// "#hasName"));
		// OWLDataProperty premContent =
		// manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID +
		// "#hasContent"));

		// for SocialContext
		OWLClass SocContOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#SocialContext"));
		OWLDataProperty depRel = manager.getOWLDataFactory()
				.getOWLDataProperty(
						IRI.create(ontoID + "#hasDependencyRelation"));
		OWLObjectProperty proponent = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasProponent"));
		OWLObjectProperty opponent = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasOpponent"));
		OWLObjectProperty group = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasGroup"));

		// for SocialEntity
		OWLClass SocialEntityOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#SocialEntity"));
		OWLDataProperty socEnID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty socEnRole = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasRole"));
		OWLObjectProperty socEnNorm = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasNorm"));
		OWLObjectProperty socEnValPref = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasValPref"));

		// for Norm
		// OWLClass NormOWL =
		// manager.getOWLDataFactory().getOWLClass(IRI.create(ontoID +
		// "#Norm"));
		// OWLDataProperty normID =
		// manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID +
		// "#hasID"));
		// OWLDataProperty normDesc =
		// manager.getOWLDataFactory().getOWLDataProperty(IRI.create(ontoID +
		// "#hasDescription"));

		// for ValPref
		OWLClass ValPrefOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#ValPref"));
		OWLDataProperty values = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasValues"));

		// for Group
		OWLClass GroupOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#Group"));
		OWLDataProperty groupID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty groupRole = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasRole"));
		OWLObjectProperty groupNorm = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasNorm"));
		OWLObjectProperty groupValPref = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasValPref"));
		OWLObjectProperty groupMember = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasMember"));

		// for ArgumentSolution
		OWLClass ArgSolOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#ArgumentSolution"));
		OWLDataProperty argType = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasArgumentType"));
		OWLDataProperty argAccState = manager.getOWLDataFactory()
				.getOWLDataProperty(
						IRI.create(ontoID + "#hasAcceptabilityState"));
		OWLDataProperty timesUsed = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#timesUsed"));
		OWLObjectProperty argConc = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasConclusion"));
		OWLDataProperty promValue = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#promotesValue"));
		OWLObjectProperty argDistPrem = manager.getOWLDataFactory()
				.getOWLObjectProperty(
						IRI.create(ontoID + "#hasDistinguishingPremise"));
		OWLDataProperty argCounterExDomCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(
						IRI.create(ontoID + "#hasCounterExampleDomCaseID"));
		OWLDataProperty argCounterExArgCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(
						IRI.create(ontoID + "#hasCounterExampleArgCaseID"));
		OWLObjectProperty argExc = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasException"));
		OWLObjectProperty argPres = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasPresumption"));

		// for Conclusion
		OWLClass ConclusionOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#Conclusion"));
		OWLDataProperty concID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty concDesc = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasDescription"));

		// for Case
		OWLClass CaseOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#Case"));
		OWLDataProperty caseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty caseDate = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#creationDate"));

		// for ArgumentJustification
		OWLClass ArgJusOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#ArgumentJustification"));
		OWLDataProperty argDesc = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasDescription"));
		OWLDataProperty argDomCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasDomainCaseID"));
		OWLDataProperty argArgCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasArgumentCaseID"));
		OWLObjectProperty argArgSch = manager.getOWLDataFactory()
				.getOWLObjectProperty(
						IRI.create(ontoID + "#hasArgumentationScheme"));
		OWLObjectProperty argDiaGra = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasDialogueGraph"));

		// for ArgumentationScheme
		OWLClass ArgSchOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#ArgumentationScheme"));
		OWLDataProperty argSchID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty argSchTitle = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#argTitle"));
		OWLDataProperty argSchDate = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#creationDate"));
		OWLObjectProperty argSchAuthor = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasAuthor"));
		OWLObjectProperty argSchConc = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasConclusion"));
		OWLObjectProperty argSchPremise = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasPremise"));
		OWLObjectProperty argSchPres = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasPresumption"));
		OWLObjectProperty argSchExc = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasException"));

		// for Author
		OWLClass AuthorOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#Author"));
		OWLDataProperty authorName = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#authorName"));

		// for DialogueGraph
		OWLClass DiaGraOWL = manager.getOWLDataFactory().getOWLClass(
				IRI.create(ontoID + "#DialogueGraph"));
		OWLObjectProperty nodes = manager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create(ontoID + "#hasNodes"));

		// for ArgNode
		OWLDataProperty argNodeArgCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasArgCaseID"));
		OWLDataProperty argNodeChildArgCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasChildArgCaseID"));
		OWLDataProperty argNodeParentArgCaseID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasParentArgCaseID"));
		OWLDataProperty argNodeType = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasNodeType"));

		// get all instances of ArgumentCase class
		NodeSet<OWLNamedIndividual> argCaseInd = reasoner.getInstances(
				ArgCaseOWL, false);

		/*
		 * System.out.println("Number of individuals: "+individuals.getNodes().size
		 * ()); System.out.println("Instances of Ticket: ");
		 * for(Node<OWLNamedIndividual> ind : individuals) {
		 * System.out.println(" " + ind); } System.out.println("\n");
		 */

		for (Node<OWLNamedIndividual> sameInd : argCaseInd) {

			// sameInd contains information about the individual (and all other
			// individuals that were inferred to be the same)
			OWLNamedIndividual ind = sameInd.getRepresentativeElement();

			// initialize the ArgumentCase
			ArgumentCase newArgCase = new ArgumentCase();

			// get the info about this specific individual
			Set<OWLLiteral> id = reasoner.getDataPropertyValues(ind, argCaseID);
			Set<OWLLiteral> creationDate = reasoner.getDataPropertyValues(ind,
					argCaseDate);
			NodeSet<OWLNamedIndividual> argCaseProb = reasoner
					.getObjectPropertyValues(ind, argCaseProblem);
			NodeSet<OWLNamedIndividual> argCaseSol = reasoner
					.getObjectPropertyValues(ind, argCaseSolution);
			NodeSet<OWLNamedIndividual> argCaseJus = reasoner
					.getObjectPropertyValues(ind, argCaseJustification);

			// NodeSet<OWLClass> types = reasoner.getTypes( ind, true );

			// get the argCaseID
			try {
				int idAC = Integer.parseInt(id.iterator().next().getLiteral()
						.trim());

				// put the ID in the ArgumentCase
				newArgCase.setID(idAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasID[ArgumentCase]");
			}

			// get the creationDate
			try {
				String dateAC = this.filter(creationDate.iterator()
						.next().getLiteral().trim());

				// put the creationDate in the ArgumentCase
				newArgCase.setCreationDate(dateAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property creationDate[ArgumentCase]");
			}

			// get the ArgumentProblem
			try {
				OWLNamedIndividual problem = argCaseProb.iterator().next()
						.getRepresentativeElement();

				// initialize the ArgumentProblem
				ArgumentProblem argProbAC = new ArgumentProblem();

				// get the domainContext
				NodeSet<OWLNamedIndividual> domCont = reasoner
						.getObjectPropertyValues(problem, domainContext);
				try {
					OWLNamedIndividual dc = domCont.iterator().next()
							.getRepresentativeElement();

					// initialize the DomainContext
					DomainContext dcAC = new DomainContext();

					// get the premises
					NodeSet<OWLNamedIndividual> premSet = reasoner
							.getObjectPropertyValues(dc, premises);
					try {
						ArrayList<Premise> premisesDC = new ArrayList<Premise>();

						premisesDC = getPremiseList(premSet);

						// put premises in the DomainContext
						dcAC.setPremisesFromList(premisesDC);
					} catch (Exception e) {
						System.err
								.println("Unknown value for property hasPremise[DomainContext]");
					}

					// put the DomainContex in the ArgumentProblem
					argProbAC.setDomainContext(dcAC);

				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasDomainContext[ArgumentProblem]");
				}

				// get the socialContext
				NodeSet<OWLNamedIndividual> socCont = reasoner
						.getObjectPropertyValues(problem, socialContext);
				try {
					OWLNamedIndividual sc = socCont.iterator().next()
							.getRepresentativeElement();

					// initialize the SocialContext
					SocialContext scAC = new SocialContext();

					// get the Proponent
					NodeSet<OWLNamedIndividual> prop = reasoner
							.getObjectPropertyValues(sc, proponent);
					try {
						OWLNamedIndividual propo = prop.iterator().next()
								.getRepresentativeElement();

						// initialize the Proponent
						SocialEntity propAC = new SocialEntity();

						// get the info about this Proponent
						Set<OWLLiteral> propID = reasoner
								.getDataPropertyValues(propo, socEnID);
						try {
							int propIDAC = Integer.parseInt(propID.iterator()
									.next().getLiteral().trim());

							// put proponent ID
							propAC.setID(propIDAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasID[Proponent]");
						}
						Set<OWLLiteral> propRole = reasoner
								.getDataPropertyValues(propo, socEnRole);
						try {
							String propRoleAC = propRole.iterator().next()
									.getLiteral().trim();

							// put proponent role
							propAC.setRole(propRoleAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasRole[Proponent]");
						}
						NodeSet<OWLNamedIndividual> propNormSet = reasoner
								.getObjectPropertyValues(propo, socEnNorm);
						try {
							ArrayList<Norm> propNormsAC = new ArrayList<Norm>();

							propNormsAC = getNormList(propNormSet);

							// put proponent Norms
							propAC.setNorms(propNormsAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasNorm[Proponent]");
						}
						NodeSet<OWLNamedIndividual> propValPref = reasoner
								.getObjectPropertyValues(propo, socEnValPref);
						try {
							OWLNamedIndividual propVP = propValPref.iterator()
									.next().getRepresentativeElement();
							ValPref propValPrefAC = new ValPref();

							// get the info about this ValPref
							Set<OWLLiteral> propValPrefValues = reasoner
									.getDataPropertyValues(propVP, values);
							try {
								for (OWLLiteral vp : propValPrefValues) {
									String newValue = vp.getLiteral().trim();
									propValPrefAC.addValue(newValue);
								}
							} catch (Exception e) {
								System.err
										.println("Unknown value for property hasValues[ProponentValPref]");
							}

							// put proponent ValPref
							propAC.setValPref(propValPrefAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasValPref[Proponent]");
						}

						// put proponent in SocialContext
						scAC.setProponent(propAC);
					} catch (Exception e) {
						System.err
								.println("Unknown value for property hasProponent");
					}

					// get the Opponent
					NodeSet<OWLNamedIndividual> opp = reasoner
							.getObjectPropertyValues(sc, opponent);
					try {
						OWLNamedIndividual oppo = opp.iterator().next()
								.getRepresentativeElement();

						// initialize the Opponent
						SocialEntity oppAC = new SocialEntity();

						// get the info about this Opponent
						Set<OWLLiteral> oppID = reasoner.getDataPropertyValues(
								oppo, socEnID);
						try {
							int oppIDAC = Integer.parseInt(oppID.iterator()
									.next().getLiteral().trim());

							// put opponent ID
							oppAC.setID(oppIDAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasID[Opponent]");
						}
						Set<OWLLiteral> oppRole = reasoner
								.getDataPropertyValues(oppo, socEnRole);
						try {
							String oppRoleAC = oppRole.iterator().next()
									.getLiteral().trim();

							// put opponent role
							oppAC.setRole(oppRoleAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasRole[Opponent]");
						}
						NodeSet<OWLNamedIndividual> oppNormSet = reasoner
								.getObjectPropertyValues(oppo, socEnNorm);
						try {
							ArrayList<Norm> oppNormsAC = new ArrayList<Norm>();

							oppNormsAC = getNormList(oppNormSet);

							// put opponent Norms
							oppAC.setNorms(oppNormsAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasNorm[Opponent]");
						}
						NodeSet<OWLNamedIndividual> oppValPref = reasoner
								.getObjectPropertyValues(oppo, socEnValPref);
						try {
							OWLNamedIndividual oppVP = oppValPref.iterator()
									.next().getRepresentativeElement();
							ValPref oppValPrefAC = new ValPref();

							// get the info about this ValPref
							Set<OWLLiteral> oppValPrefValues = reasoner
									.getDataPropertyValues(oppVP, values);
							try {
								for (OWLLiteral vp : oppValPrefValues) {
									String newValue = vp.getLiteral().trim();
									oppValPrefAC.addValue(newValue);
								}
							} catch (Exception e) {
								System.err
										.println("Unknown value for property hasValues[OpponentValPref]");
							}

							// put proponent ValPref
							oppAC.setValPref(oppValPrefAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasValPref[Opponent]");
						}

						// put proponent in SocialContext
						scAC.setOpponent(oppAC);
					} catch (Exception e) {
						System.err
								.println("Unknown value for property hasOpponent");
					}

					// get the Group
					NodeSet<OWLNamedIndividual> grou = reasoner
							.getObjectPropertyValues(sc, group);
					try {
						OWLNamedIndividual gro = grou.iterator().next()
								.getRepresentativeElement();

						// initialize the Group
						Group grAC = new Group();

						// get the info about this Group
						Set<OWLLiteral> grID = reasoner.getDataPropertyValues(
								gro, groupID);
						try {
							int grIDAC = Integer.parseInt(grID.iterator()
									.next().getLiteral().trim());

							// put group ID
							grAC.setID(grIDAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasID[Group]");
						}
						Set<OWLLiteral> grRole = reasoner
								.getDataPropertyValues(gro, groupRole);
						try {
							String grRoleAC = grRole.iterator().next()
									.getLiteral().trim();

							// put group role
							grAC.setRole(grRoleAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasRole[Group]");
						}
						NodeSet<OWLNamedIndividual> grNormSet = reasoner
								.getObjectPropertyValues(gro, groupNorm);
						try {
							ArrayList<Norm> grNormsAC = new ArrayList<Norm>();

							grNormsAC = getNormList(grNormSet);

							// put group Norms
							grAC.setNorms(grNormsAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasNorm[Group]");
						}
						NodeSet<OWLNamedIndividual> grValPref = reasoner
								.getObjectPropertyValues(gro, groupValPref);
						try {
							OWLNamedIndividual grVP = grValPref.iterator()
									.next().getRepresentativeElement();
							ValPref grValPrefAC = new ValPref();

							// get the info about this ValPref
							Set<OWLLiteral> grValPrefValues = reasoner
									.getDataPropertyValues(grVP, values);
							try {
								for (OWLLiteral vp : grValPrefValues) {
									String newValue = vp.getLiteral().trim();
									grValPrefAC.addValue(newValue);
								}
							} catch (Exception e) {
								System.err
										.println("Unknown value for property hasValues[GroupValPref]");
							}

							// put group ValPref
							grAC.setValPref(grValPrefAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasValPref[Group]");
						}
						NodeSet<OWLNamedIndividual> grMembers = reasoner
								.getObjectPropertyValues(gro, groupMember);
						try {
							ArrayList<SocialEntity> grMemListAC = new ArrayList<SocialEntity>();

							// get the info about the Members
							for (Node<OWLNamedIndividual> m : grMembers) {
								OWLNamedIndividual mem = m
										.getRepresentativeElement();

								// initialize the Member
								SocialEntity memAC = new SocialEntity();

								// get the info about this Member
								Set<OWLLiteral> memID = reasoner
										.getDataPropertyValues(mem, socEnID);
								try {
									int memIDAC = Integer.parseInt(memID
											.iterator().next().getLiteral()
											.trim());

									// put member ID
									memAC.setID(memIDAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasID[GroupMember]");
								}
								Set<OWLLiteral> memRole = reasoner
										.getDataPropertyValues(mem, socEnRole);
								try {
									String memRoleAC = memRole.iterator()
											.next().getLiteral().trim();

									// put member role
									memAC.setRole(memRoleAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasRole[Member]");
								}
								NodeSet<OWLNamedIndividual> memNormSet = reasoner
										.getObjectPropertyValues(mem, socEnNorm);
								try {
									ArrayList<Norm> memNormsAC = new ArrayList<Norm>();

									memNormsAC = getNormList(memNormSet);

									// put member Norms
									memAC.setNorms(memNormsAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasNorm[Member]");
								}
								NodeSet<OWLNamedIndividual> memValPref = reasoner
										.getObjectPropertyValues(mem,
												socEnValPref);
								try {
									OWLNamedIndividual memVP = memValPref
											.iterator().next()
											.getRepresentativeElement();
									ValPref memValPrefAC = new ValPref();

									// get the info about this ValPref
									Set<OWLLiteral> memValPrefValues = reasoner
											.getDataPropertyValues(memVP,
													values);
									try {
										for (OWLLiteral vp : memValPrefValues) {
											String newValue = vp.getLiteral()
													.trim();
											memValPrefAC.addValue(newValue);
										}
									} catch (Exception e) {
										System.err
												.println("Unknown value for property hasValues[MemberValPref]");
									}

									// put member ValPref
									memAC.setValPref(memValPrefAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasValPref[Member]");
								}

								// put member in MembersList
								grMemListAC.add(memAC);
							}

							// put group Members
							grAC.setMembers(grMemListAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasMember[Group]");
						}

						// put group in SocialContext
						scAC.setGroup(grAC);
					} catch (Exception e) {
						System.err
								.println("Unknown value for property hasGroup");
					}

					// get the dependency relation
					Set<OWLLiteral> scDR = reasoner.getDataPropertyValues(sc,
							depRel);
					try {
						DependencyRelation drAC = DependencyRelation
								.valueOf(scDR.iterator().next().getLiteral()
										.trim());
						scAC.setDependencyRelation(drAC);
					} catch (Exception e) {
						System.err
								.println("Unknown value for property hasDependencyRelation[SocialContext]");
					}

					// put the SocialContext in the ArgumentProblem
					argProbAC.setSocialContext(scAC);

				} catch (Exception e) {
					System.out
							.println("Unknown value for property hasSocialContext");
				}

				// put the ArgumentProblem in the ArgumentCase
				newArgCase.setArgumentProblem(argProbAC);

			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasArgumentProblem[ArgumentCase]");
			}

			// get the ArgumentSolution
			try {
				OWLNamedIndividual solution = argCaseSol.iterator().next()
						.getRepresentativeElement();

				// initialize the ArgumentSolution
				ArgumentSolution argSolAC = new ArgumentSolution();

				// get the AcceptabilityState
				Set<OWLLiteral> accState = reasoner.getDataPropertyValues(
						solution, argAccState);
				try {
					AcceptabilityStatus accStateAC = AcceptabilityStatus
							.valueOf(accState.iterator().next().getLiteral()
									.trim());

					// put the AcceptabilityState in the ArgumentSolution
					argSolAC.setAcceptabilityState(accStateAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasAcceptabilityState[ArgumentSolution]");
				}

				// get the ArgumentType
				Set<OWLLiteral> argTyp = reasoner.getDataPropertyValues(
						solution, argType);
				try {
					String argTypeAC = argTyp
							.iterator().next().getLiteral().trim();

					// put the ArgumentType in the ArgumentSolution
					argSolAC.setArgumentTypeString(argTypeAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasArgumentType[ArgumentSolution]");
				}

				// get the CounterExampleArgCaseIDList
				Set<OWLLiteral> countExArgCaseID = reasoner
						.getDataPropertyValues(solution, argCounterExArgCaseID);
				try {
					ArrayList<Long> ceArgCaseListAC = new ArrayList<Long>();

					for (OWLLiteral ceArg : countExArgCaseID) {
						Long ceArgCIDAC = Long.parseLong(ceArg.getLiteral()
								.trim());

						// put the CounterExampleArgCaseID in the
						// CounterExampleArgCaseIDList
						ceArgCaseListAC.add(ceArgCIDAC);
					}

					// put the CounterExampleArgCaseIDList in the
					// ArgumentSolution
					argSolAC.setCounterExamplesArgCaseIDList(ceArgCaseListAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasCounterExampleArgCaseIDList[ArgumentSolution]");
				}

				// get the CounterExampleDomCaseIDList
				Set<OWLLiteral> countExDomCaseID = reasoner
						.getDataPropertyValues(solution, argCounterExDomCaseID);
				try {
					ArrayList<Long> ceDomCaseListAC = new ArrayList<Long>();

					for (OWLLiteral ceDom : countExDomCaseID) {
						Long ceDomCIDAC = Long.parseLong(ceDom.getLiteral()
								.trim());

						// put the CounterExampleDomCaseID in the
						// CounterExampleDomCaseIDList
						ceDomCaseListAC.add(ceDomCIDAC);
					}

					// put the CounterExampleDomCaseIDList in the
					// ArgumentSolution
					argSolAC.setCounterExamplesDomCaseIDList(ceDomCaseListAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasCounterExampleDomCaseIDList[ArgumentSolution]");
				}

				// get the promotesValue
				Set<OWLLiteral> promVal = reasoner.getDataPropertyValues(
						solution, promValue);
				try {
					String promValAC = promVal.iterator().next().getLiteral()
							.trim();

					// put the promotedValue in the ArgumentSolution
					argSolAC.setPromotesValue(promValAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property promotesValue[ArgumentSolution]");
				}

				// get the timesUsed
				Set<OWLLiteral> timUsed = reasoner.getDataPropertyValues(
						solution, timesUsed);
				try {
					int timUsedAC = Integer.parseInt(timUsed.iterator().next()
							.getLiteral().trim());

					// put the timesUsed in the ArgumentSolution
					argSolAC.setTimesUsed(timUsedAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property timesUsed[ArgumentSolution]");
				}

				// get distinguishingPremises
				NodeSet<OWLNamedIndividual> dPrem = reasoner
						.getObjectPropertyValues(solution, argDistPrem);
				try {
					ArrayList<Premise> dPremList = new ArrayList<Premise>();

					dPremList = getPremiseList(dPrem);

					// put premises in the ArgumentSolution
					argSolAC.setDistinguishingPremises(dPremList);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasDistinguishingPremise[ArgumentSolution]");
				}

				// get the exceptions
				NodeSet<OWLNamedIndividual> exc = reasoner
						.getObjectPropertyValues(solution, argExc);
				try {
					ArrayList<Premise> excList = new ArrayList<Premise>();

					excList = getPremiseList(exc);

					// put exceptions in the ArgumentSolution
					argSolAC.setExceptions(excList);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasException[ArgumentSolution]");
				}

				// get the presumptions
				NodeSet<OWLNamedIndividual> pres = reasoner
						.getObjectPropertyValues(solution, argPres);
				try {
					ArrayList<Premise> presList = new ArrayList<Premise>();

					presList = getPremiseList(pres);

					// put presumptions in the ArgumentSolution
					argSolAC.setPresumptions(presList);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasPresumption[ArgumentSolution]");
				}

				// get the conclusion
				NodeSet<OWLNamedIndividual> conc = reasoner
						.getObjectPropertyValues(solution, argConc);
				try {
					OWLNamedIndividual conclusion = conc.iterator().next()
							.getRepresentativeElement();
					Conclusion concAC = new Conclusion();

					Set<OWLLiteral> cID = reasoner.getDataPropertyValues(
							conclusion, concID);
					try {
						Long cIDAC = Long.parseLong(cID.iterator().next()
								.getLiteral().trim());

						// put ID in conclusion
						concAC.setID(cIDAC);
					} catch (Exception e) {
						System.err
								.println("Unknown data for property hasID[Conclusion]");
					}

					Set<OWLLiteral> desc = reasoner.getDataPropertyValues(
							conclusion, concDesc);
					try {
						String descAC = desc.iterator().next().getLiteral()
								.trim();

						// put description in conclusion
						concAC.setDescription(descAC);
					} catch (Exception e) {
						System.err
								.println("Unknown data for property hasDescription[Conclusion]");
					}

					// put conclusion in ArgumentSolution
					argSolAC.setConclusion(concAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasConclusion[ArgumentSolution]");
				}

				// put the ArgumentSolution in the ArgumentCase
				newArgCase.setArgumentSolution(argSolAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasArgumentSolution");
			}

			// get the ArgumentJustification
			try {
				OWLNamedIndividual justification = argCaseJus.iterator().next()
						.getRepresentativeElement();

				// initialize the ArgumentJustification
				ArgumentJustification argJusAC = new ArgumentJustification();

				// get the ArgumentCases IDs
				try {
					Set<OWLLiteral> argCasesIDs = reasoner
							.getDataPropertyValues(justification, argArgCaseID);
					ArrayList<Long> argCasesIDsList = new ArrayList<Long>();

					for (OWLLiteral argID : argCasesIDs) {
						Long argIDAC = Long
								.parseLong(argID.getLiteral().trim());

						// put the id in the argumentCasesIDList
						argCasesIDsList.add(argIDAC);
					}

					// put the ArgumentCasesID in the ArgumentJustification
					argJusAC.setArgumentCases(argCasesIDsList);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasArgumentCaseID[ArgumentJustification]");
				}

				// get the DomainCases IDs
				try {
					Set<OWLLiteral> domCasesIDs = reasoner
							.getDataPropertyValues(justification, argDomCaseID);
					ArrayList<Long> domCasesIDsList = new ArrayList<Long>();

					for (OWLLiteral domID : domCasesIDs) {
						Long domIDAC = Long
								.parseLong(domID.getLiteral().trim());

						// put the id in the domainCasesIDList
						domCasesIDsList.add(domIDAC);
					}

					// put the DomainCasesID in the ArgumentJustification
					argJusAC.setDomainCases(domCasesIDsList);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasDomainCaseID[ArgumentJustification]");
				}

				// get the Description
				try {
					Set<OWLLiteral> desc = reasoner.getDataPropertyValues(
							justification, argDesc);
					String descAC = desc.iterator().next().getLiteral().trim();

					// put the description in the ArgumentJustification
					argJusAC.setDescription(descAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasDescription[ArgumentJustification]");
				}

				// get the ArgumentationSchemes
				try {
					NodeSet<OWLNamedIndividual> argSchs = reasoner
							.getObjectPropertyValues(justification, argArgSch);
					ArrayList<ArgumentationScheme> argSchsListAC = new ArrayList<ArgumentationScheme>();

					for (Node<OWLNamedIndividual> argSch : argSchs) {
						OWLNamedIndividual argS = argSch
								.getRepresentativeElement();
						ArgumentationScheme argSchAC = new ArgumentationScheme();

						// get the argTitle
						try {
							Set<OWLLiteral> argTit = reasoner
									.getDataPropertyValues(argS, argSchTitle);
							String argTitAC = argTit.iterator().next()
									.getLiteral().trim();

							// put the title in the Scheme
							argSchAC.setArgTitle(argTitAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property argTitle[ArgumentationScheme]");
						}

						// get the creationDate
						try {
							Set<OWLLiteral> creDat = reasoner
									.getDataPropertyValues(argS, argSchDate);
							String creDatAC = this.filter(creDat
									.iterator().next().getLiteral().trim());

							// put the date in the Scheme
							argSchAC.setCreationDate(creDatAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property creationDate[ArgumentationScheme]");
						}

						// get the ID
						try {
							Set<OWLLiteral> argSID = reasoner
									.getDataPropertyValues(argS, argSchID);
							long argSIDAC = Long.parseLong(argSID.iterator()
									.next().getLiteral().trim());

							// put the ID in the scheme
							argSchAC.setID(argSIDAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasID[ArgumentationScheme]");
						}

						// get the author
						try {
							NodeSet<OWLNamedIndividual> argSchAut = reasoner
									.getObjectPropertyValues(argS, argSchAuthor);
							OWLNamedIndividual argSAuth = argSchAut.iterator()
									.next().getRepresentativeElement();
							Author authAC = new Author();

							// get the author name
							try {
								Set<OWLLiteral> authName = reasoner
										.getDataPropertyValues(argSAuth,
												authorName);

								String authNameAC = authName.iterator().next()
										.getLiteral().trim();
								// put the name in the author
								authAC.setAuthorName(authNameAC);
							} catch (Exception e) {
								System.err
										.println("Unknown value for property authorName[Author]");
							}

							// put the author in the scheme
							argSchAC.setAuthor(authAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasAuthor[ArgumentationScheme]");
						}

						// get the conclusion
						NodeSet<OWLNamedIndividual> argSchC = reasoner
								.getObjectPropertyValues(argS, argSchConc);
						try {
							OWLNamedIndividual conclusion = argSchC.iterator()
									.next().getRepresentativeElement();
							Conclusion concAC = new Conclusion();

							Set<OWLLiteral> cID = reasoner
									.getDataPropertyValues(conclusion, concID);
							try {
								Long cIDAC = Long.parseLong(cID.iterator()
										.next().getLiteral().trim());

								// put ID in conclusion
								concAC.setID(cIDAC);
							} catch (Exception e) {
								System.err
										.println("Unknown data for property hasID[Conclusion]");
							}

							Set<OWLLiteral> desc = reasoner
									.getDataPropertyValues(conclusion, concDesc);
							try {
								String descAC = desc.iterator().next()
										.getLiteral().trim();

								// put description in conclusion
								concAC.setDescription(descAC);
							} catch (Exception e) {
								System.err
										.println("Unknown data for property hasDescription[Conclusion]");
							}

							// put conclusion in ArgumentScheme
							argSchAC.setConclusion(concAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasConclusion[ArgumentationScheme");
						}

						// get the exceptions
						NodeSet<OWLNamedIndividual> argSchEx = reasoner
								.getObjectPropertyValues(argS, argSchExc);
						try {
							ArrayList<Premise> excList = new ArrayList<Premise>();

							excList = getPremiseList(argSchEx);

							// put exceptions in the ArgumentationScheme
							argSchAC.setExceptions(excList);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasException[ArgumentationScheme]");
						}

						// get the presumptions
						NodeSet<OWLNamedIndividual> argSchPr = reasoner
								.getObjectPropertyValues(argS, argSchPres);
						try {
							ArrayList<Premise> presList = new ArrayList<Premise>();

							presList = getPremiseList(argSchPr);

							// put presumptions in the ArgumentationScheme
							argSchAC.setPresumptions(presList);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasPresumption[ArgumentationScheme]");
						}

						// get the premises
						NodeSet<OWLNamedIndividual> argSchPrem = reasoner
								.getObjectPropertyValues(argS, argSchPremise);
						try {
							ArrayList<Premise> premList = new ArrayList<Premise>();

							premList = getPremiseList(argSchPrem);

							// put premises in the ArgumentationScheme
							argSchAC.setPremises(premList);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasPresumption[ArgumentationScheme]");
						}

						// put scheme in the list
						argSchsListAC.add(argSchAC);
					}

					// put the list of ArgumentationSchemes in the
					// ArgumentJustification
					argJusAC.setArgumentationSchemes(argSchsListAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasArgumentationScheme[ArgumentJustification]");
				}

				// get the DialogueGraphs
				try {
					NodeSet<OWLNamedIndividual> diaGraphs = reasoner
							.getObjectPropertyValues(justification, argDiaGra);
					ArrayList<DialogueGraph> diaGraphsAC = new ArrayList<DialogueGraph>();
					for (Node<OWLNamedIndividual> dGr : diaGraphs) {
						OWLNamedIndividual dGra = dGr
								.getRepresentativeElement();
						DialogueGraph dGraAC = new DialogueGraph();

						// get the nodes
						try {
							NodeSet<OWLNamedIndividual> nods = reasoner
									.getObjectPropertyValues(dGra, nodes);
							ArrayList<ArgNode> nodesListAC = new ArrayList<ArgNode>();
							for (Node<OWLNamedIndividual> node : nods) {
								OWLNamedIndividual n = node
										.getRepresentativeElement();
								ArgNode nAC = new ArgNode();

								// get the ArgumentCase ID
								try {
									Set<OWLLiteral> nodeArgCaseID = reasoner
											.getDataPropertyValues(n,
													argNodeArgCaseID);
									long nodeArgCaseIDAC = Long
											.parseLong(nodeArgCaseID.iterator()
													.next().getLiteral().trim());

									// put the argCaseID in the node
									nAC.setArgCaseID(nodeArgCaseIDAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasArgCaseID[ArgNode]");
								}

								// get the ParentArgCase ID
								try {
									Set<OWLLiteral> parentID = reasoner
											.getDataPropertyValues(n,
													argNodeParentArgCaseID);
									long parentIDAC = Long.parseLong(parentID
											.iterator().next().getLiteral()
											.trim());

									// put the parentArgCaseID in the node
									nAC.setArgCaseID(parentIDAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasParentArgCaseID[ArgNode]");
								}

								// get the childsArgCases ID
								try {
									Set<OWLLiteral> childrenID = reasoner
											.getDataPropertyValues(n,
													argNodeChildArgCaseID);
									ArrayList<Long> childrenListAC = new ArrayList<Long>();
									for (OWLLiteral childID : childrenID) {
										long childIDAC = Long.parseLong(childID
												.getLiteral().trim());

										// put childID in the list
										childrenListAC.add(childIDAC);
									}

									// put the children list in the node
									nAC.setChildArgCaseIDList(childrenListAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasChildArgCaseID[ArgNode]");
								}

								// get the node type
								try {
									Set<OWLLiteral> nodeType = reasoner
											.getDataPropertyValues(n,
													argNodeType);
									NodeType nodeTypeAC = NodeType.valueOf(nodeType
											.iterator().next().getLiteral()
											.trim());

									// put the type in the node
									nAC.setNodeType(nodeTypeAC);
								} catch (Exception e) {
									System.err
											.println("Unknown value for property hasNodeType[ArgNode]");
								}

								// put the node in the nodes list
								nodesListAC.add(nAC);
							}

							// put the nodes list in the dialogue graph
							dGraAC.setNodes(nodesListAC);
						} catch (Exception e) {
							System.err
									.println("Unknown value for property hasNodes[DialogueGraph]");
						}

						// put the graph in the graphs list
						diaGraphsAC.add(dGraAC);
					}
					// put the graphs list in the ArgumentJustification
					argJusAC.setDialogueGraphs(diaGraphsAC);
				} catch (Exception e) {
					System.err
							.println("Unknown value for property hasDialogueGraph[ArgumentJustification]");
				}

				// put the ArgumentJustification in the ArgumentCase
				newArgCase.setArgumentJustification(argJusAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasArgumentJustification");
			}

			// put the new ArgumentCase in the Vector of ArgumentCases
			argCases.add(newArgCase);

		}
		manager.removeOntology(argOntology);
		return argCases;
	}

	/**
	 * Returns an ArrayList of Premises from a set of OWL individuals
	 * 
	 * @param Premises
	 *            NodeSet<OWLNamedIndividual> Premises set of premises
	 * @return PremisesList ArrayList<Premise>
	 */
	public ArrayList<Premise> getPremiseList(
			NodeSet<OWLNamedIndividual> premises) throws Exception {
		ArrayList<Premise> premiseList = new ArrayList<Premise>();

		for (Node<OWLNamedIndividual> pre : premises) {
			// create property and resources to query the reasoner
			// for Premise
			OWLDataProperty premID = manager.getOWLDataFactory()
					.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
			OWLDataProperty premName = manager.getOWLDataFactory()
					.getOWLDataProperty(IRI.create(ontoID + "#hasName"));
			OWLDataProperty premContent = manager.getOWLDataFactory()
					.getOWLDataProperty(IRI.create(ontoID + "#hasContent"));
			OWLNamedIndividual pr = pre.getRepresentativeElement();

			// initialize the Premise
			Premise prAC = new Premise();

			// get the info about this specific premise
			Set<OWLLiteral> prID = reasoner.getDataPropertyValues(pr, premID);
			int prIDAC = Integer.parseInt(prID.iterator().next().getLiteral()
					.trim());
			prAC.setID(prIDAC);

			Set<OWLLiteral> prName = reasoner.getDataPropertyValues(pr,
					premName);
			String prNameAC = "";
			try {
				prNameAC = prName.iterator().next().getLiteral().trim();
				prAC.setName(prNameAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasName[Premise]");
			}
			Set<OWLLiteral> prContent = reasoner.getDataPropertyValues(pr,
					premContent);
			String prContentAC = "";
			try {
				prContentAC = prContent.iterator().next().getLiteral().trim();
				prAC.setContent(prContentAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasContent[Premise]");
			}

			// add the premise to premiseList
			premiseList.add(prAC);
		}

		return premiseList;
	}

	/**
	 * Returns an ArrayList of Norms from a set of OWL individuals
	 * 
	 * @param Norms
	 *            NodeSet<OWLNamedIndividual> Norms set of norms
	 * @return NormList ArrayList<Norm>
	 */
	public ArrayList<Norm> getNormList(NodeSet<OWLNamedIndividual> norms)
			throws Exception {

		// create properties and resources to query the reasoner
		// for Norm
		OWLDataProperty normID = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasID"));
		OWLDataProperty normDesc = manager.getOWLDataFactory()
				.getOWLDataProperty(IRI.create(ontoID + "#hasDescription"));

		ArrayList<Norm> normList = new ArrayList<Norm>();

		for (Node<OWLNamedIndividual> nr : norms) {
			OWLNamedIndividual norm = nr.getRepresentativeElement();

			// initialize the Norm
			Norm normAC = new Norm();

			// get the info about this Norm
			Set<OWLLiteral> norID = reasoner
					.getDataPropertyValues(norm, normID);
			try {
				int norIDAC = Integer.parseInt(norID.iterator().next()
						.getLiteral().trim());
				normAC.setID(norIDAC);
			} catch (Exception e) {
				System.err.println("Unknown value for property hasID[Norm]");
			}
			Set<OWLLiteral> norDesc = reasoner.getDataPropertyValues(norm,
					normDesc);
			try {
				String norDescAC = norDesc.iterator().next().getLiteral()
						.trim();
				normAC.setDescription(norDescAC);
			} catch (Exception e) {
				System.err
						.println("Unknown value for property hasDesciption[Norm]");
			}

			// put Norm in normList
			normList.add(normAC);
		}
		return normList;
	}

	/**
	 * Parses a vector of argument-cases in the ArgCBROnto ontology. The
	 * ontology must be previously loaded.
	 * 
	 * @param argCases
	 *            The vector of argument-cases to parse in the ArgCBROnto
	 *            ontology
	 * @param ontoFileName
	 *            The name of the ontology to be saved
	 */

	public void saveArgumentationOntology(Vector<ArgumentCase> argumentCases,
			String ontoFile, String ontoFileName) throws Exception {

		// hold changes in the Queue as they are done
		Queue<AddAxiom> changes = new LinkedList<AddAxiom>();

		this.loadOntology(ontoFile);

		// create a Factory for our Datatypes
		factory = manager.getOWLDataFactory();

		for (ArgumentCase argCaseV : argumentCases) {

			OWLNamedIndividual argCase = null;

			long argCaseID = argCaseV.getID();
			// Creating ArgumentCase Individual
			if (argCaseID != -1 && argCaseID != 0) {
				argCase = factory.getOWLNamedIndividual(IRI.create(ontoID + "#"
						+ "ArgumentCase" + argCaseID));
				changes.add(createIndividual(argCase, "ArgumentCase"));
			} else
				continue;

			// creating Data property hasID
			changes.add(createDataPropInt(argCase, "hasID", (int) argCaseID));

			// creating Data property creationDate
			// TODO revise Date format
			String argCaseDate = argCaseV.getCreationDate();
			if (!argCaseDate.equals("") && argCaseDate != null)
				changes.add(createDataPropString(argCase, "creationDate", this.filter(argCaseDate
						.toString().trim())));

			// creating Object property hasArgumentProblem
			ArgumentProblem argProb = argCaseV.getArgumentProblem();
			if (argProb != null) {

				// creating ArgumentProblem individual
				OWLNamedIndividual argProbInd = null;
				argProbInd = factory.getOWLNamedIndividual(IRI.create(ontoID
						+ "#" + "ArgumentProblem" + argProb.hashCode()));
				changes.add(createIndividual(argProbInd, "ArgumentProblem"));

				// creating Object property hasDomainContext
				DomainContext domCont = argProb.getDomainContext();
				if (domCont != null) {
					// creating DomainContext individual
					OWLNamedIndividual domContInd = null;
					domContInd = factory.getOWLNamedIndividual(IRI
							.create(ontoID + "#" + "DomainContext"
									+ domCont.hashCode()));
					changes.add(createIndividual(domContInd, "DomainContext"));

					// creating Object property hasPremise
					HashMap<Integer, Premise> premiseMap = domCont
							.getPremises();
					ArrayList<Premise> premiseList = new ArrayList<Premise>();
					Iterator it = premiseMap.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry<Integer, Premise> prem = (Entry<Integer, Premise>) it
								.next();
						premiseList.add(prem.getValue());
					}
					createObjectPropPremiseList(domContInd, "hasPremise",
							premiseList, changes);

					// adding hasDomainContext to ArgumentProblem
					changes.add(createObjectProp(argProbInd,
							"hasDomainContext", domContInd));
				}

				// creating Object property hasSocialContext
				SocialContext socCont = argProb.getSocialContext();
				if (socCont != null) {
					// creating SocialContext individual
					OWLNamedIndividual socContInd = null;
					socContInd = factory.getOWLNamedIndividual(IRI
							.create(ontoID + "#" + "SocialContext"
									+ socCont.hashCode()));
					changes.add(createIndividual(socContInd, "SocialContext"));

					// creating Data property hasDependencyRelation
					changes.add(createDataPropString(socContInd,
							"hasDependencyRelation", this.filter(socCont
									.getDependencyRelation().toString())));

					// creating Object property hasProponent
					SocialEntity prop = socCont.getProponent();
					if (prop != null) {
						// creating SocialEntity individual
						OWLNamedIndividual propInd = null;
						if (prop.getID() != -1 && prop.getID() != 0) {
							propInd = factory.getOWLNamedIndividual(IRI
									.create(ontoID + "#" + "SocialEntity"
											+ prop.getID()));
							changes.add(createIndividual(propInd,
									"SocialEntity"));
						}

						// creating Data property hasID
						if (prop.getID() != -1 && prop.getID() != 0)
							changes.add(createDataPropInt(propInd, "hasID", (int) prop.getID()));

						// creating Data property hasRole
						if (!prop.getRole().equals(""))
							changes.add(createDataPropString(propInd, "hasRole", this.filter(prop
									.getRole())));

						// creating Object property hasNorm
						createSocialEntityHasNorm(prop, propInd, changes);

						// creating Object property hasValPref
						createSocialEntityHasValPref(prop, propInd, changes);

						// adding hasProponent to SocialContext
						if (prop.getID() != -1 && prop.getID() != 0)
							changes.add(createObjectProp(socContInd,
									"hasProponent", propInd));
					}

					// creating Object property hasOpponent
					SocialEntity oppo = socCont.getProponent();
					if (oppo != null) {
						// creating SocialEntity individual
						OWLNamedIndividual oppoInd = null;
						if (oppo.getID() != -1 && oppo.getID() != 0) {
							oppoInd = factory.getOWLNamedIndividual(IRI
									.create(ontoID + "#" + "SocialEntity"
											+ oppo.getID()));
							changes.add(createIndividual(oppoInd,
									"SocialEntity"));
						}

						// creating Data property hasID
						if (oppo.getID() != -1 && oppo.getID() != 0)
							changes.add(createDataPropInt(oppoInd, "hasID", (int) oppo.getID()));

						// creating Data property hasRole
						if (!oppo.getRole().equals(""))
							changes.add(createDataPropString(oppoInd, "hasRole", this.filter(oppo
									.getRole())));

						// creating Object property hasNorm
						createSocialEntityHasNorm(oppo, oppoInd, changes);

						// creating Object property hasValPref
						createSocialEntityHasValPref(oppo, oppoInd, changes);

						// adding hasOpponent to SocialContext
						if (oppo.getID() != -1 && oppo.getID() != 0)
							changes.add(createObjectProp(socContInd,
									"hasOponent", oppoInd));
					}

					// creating Object property hasGroup
					Group gr = socCont.getGroup();
					if (gr != null) {
						// creating Group individual
						OWLNamedIndividual grInd = null;
						if (gr.getID() != -1 && gr.getID() != 0) {
							grInd = factory
									.getOWLNamedIndividual(IRI.create(ontoID
											+ "#" + "Group" + gr.getID()));
							changes.add(createIndividual(grInd, "Group"));
						} else
							continue;

						// creating Data property hasID
						if (gr.getID() != -1 && gr.getID() != 0)
							changes.add(createDataPropInt(grInd, "hasID", (int) gr.getID()));

						// creating Data property hasRole
						if (!gr.getRole().equals(""))
							changes.add(createDataPropString(grInd, "hasRole", this.filter(gr
									.getRole())));

						// creating Object property hasNorm
						createSocialEntityHasNorm(gr, grInd, changes);

						// creating Object property hasValPref
						createSocialEntityHasValPref(gr, grInd, changes);

						// creating Object property hasMember
						ArrayList<SocialEntity> memberList = gr.getMembers();
						for (SocialEntity member : memberList) {
							if (member != null) {
								// creating SocialEntity individual
								OWLNamedIndividual memInd = null;
								if (member.getID() != -1 && member.getID() != 0) {
									memInd = factory.getOWLNamedIndividual(IRI
											.create(ontoID + "#"
													+ "SocialEntity"
													+ member.getID()));
									changes.add(createIndividual(memInd,
											"SocialEntity"));
								} else
									continue;

								// creating Data property hasID
								if (member.getID() != -1 && member.getID() != 0)
									changes.add(createDataPropInt(memInd, "hasID",
											(int) member.getID()));

								// creating Data property hasRole
								if (!member.getRole().equals(""))
									changes.add(createDataPropString(memInd,
											"hasRole", member.getRole()));

								// creating Object property hasNorm
								createSocialEntityHasNorm(member, memInd,
										changes);

								// creating Object property hasValPref
								createSocialEntityHasValPref(member, memInd,
										changes);

								// adding hasMember to Group
								if (member.getID() != -1 && member.getID() != 0)
									changes.add(createObjectProp(grInd,
											"hasMember", memInd));
							}
						}

						// adding hasGroup to SocialContext
						if (gr.getID() != -1 && gr.getID() != 0)
							changes.add(createObjectProp(socContInd,
									"hasGroup", grInd));
					}

					// adding hasSocialContext to ArgumentProblem
					changes.add(createObjectProp(argProbInd,
							"hasSocialContext", socContInd));
				}

				// adding hasArgumentProblem to ArgumentCase
				changes.add(createObjectProp(argCase, "hasArgumentProblem",
						argProbInd));
			}

			// creating Object property hasArgumentSolution
			ArgumentSolution argSol = argCaseV.getArgumentSolution();
			if (argSol != null) {

				// creating ArgumentSolution individual
				OWLNamedIndividual argSolInd = null;
				argSolInd = factory.getOWLNamedIndividual(IRI.create(ontoID
						+ "#" + "ArgumentSolution" + argSol.hashCode()));
				changes.add(createIndividual(argSolInd, "ArgumentSolution"));

				// creating Data property hasAcceptabilityState
				AcceptabilityStatus accState = argSol.getAcceptabilityState();
				if (accState != null)
					changes.add(createDataPropString(argSolInd,
							"hasAcceptabilityState", this.filter(accState.name())));

				// creating Data property hasArgumentType
				ArgumentType argType = argSol.getArgumentType();
				if (argType != null)
					changes.add(createDataPropString(argSolInd, "hasArgumentType",
							this.filter(argType.name())));

				// creating Data property hasCounterExampleArgCaseID
				ArrayList<Long> argCaseIDsList = argSol
						.getCounterExamplesArgCaseIDList();
				for (Long argCID : argCaseIDsList) {
					if (argCID != -1 && argCID != 0)
						changes.add(createDataPropInt(argSolInd,
										"hasCounterExampleArgCaseID", Integer.parseInt(argCID.toString())));
				}

				// creating Data property hasCounterExampleDomCaseID
				ArrayList<Long> domCaseIDsList = argSol
						.getCounterExamplesDomCaseIDList();
				for (Long domCID : domCaseIDsList) {
					if (domCID != -1 && domCID != 0)
						changes
								.add(createDataPropInt(argSolInd,
										"hasCounterExampleDomCaseID", Integer.parseInt(domCID.toString())));
				}

				// creating Data property promotesValue
				String val = this.filter(argSol.getPromotesValue());
				if (!val.equals("") && val != null)
					changes.add(createDataPropString(argSolInd, "promotesValue", val));

				// creating Data property timesUsed
				int tUse = argSol.getTimesUsed();
				if (tUse != -1 && tUse != 0)
					changes.add(createDataPropInt(argSolInd, "timesUsed", tUse));

				// creating Object property hasDistinguishingPremise
				ArrayList<Premise> distPremList = argSol
						.getDistinguishingPremises();
				if (!distPremList.isEmpty())
					createObjectPropPremiseList(argSolInd,
							"hasDistinguishingPremise", distPremList, changes);

				// creating Object property hasException
				ArrayList<Premise> excList = argSol.getExceptions();
				if (!excList.isEmpty())
					createObjectPropPremiseList(argSolInd, "hasException",
							excList, changes);

				// creating Object property hasPresumption
				ArrayList<Premise> presList = argSol.getPresumptions();
				if (!presList.isEmpty())
					createObjectPropPremiseList(argSolInd, "hasPresumption",
							presList, changes);

				// creating Object property hasConclusion
				Conclusion conc = argSol.getConclusion();
				if (conc != null) {
					// creating Conclusion individual
					OWLNamedIndividual concInd = null;
					concInd = factory.getOWLNamedIndividual(IRI.create(ontoID
							+ "#" + "Conclusion" + conc.getID()));
					changes.add(createIndividual(concInd, "Conclusion"));

					// creating Data property hasID
					long concID = conc.getID();
					if (concID != -1 && concID != 0)
						changes.add(createDataPropInt(concInd, "hasID", (int) concID));

					// creating Data property hasDescription
					String concDesc = this.filter(conc.getDescription());
					if (!concDesc.equals("") && concDesc != null)
						changes.add(createDataPropString(concInd, "hasDescription",
								concDesc));

					// adding hasConclusion to ArgumentSolution
					changes.add(createObjectProp(argSolInd, "hasConclusion",
							concInd));
				}

				// adding hasArgumentSolution to ArgumentCase
				changes.add(createObjectProp(argCase, "hasArgumentSolution",
						argSolInd));
			}

			// creating Object property hasArgumentJustification
			ArgumentJustification argJus = argCaseV.getArgumentJustification();
			if (argJus != null) {
				// creating ArgumentJustification individual
				OWLNamedIndividual argJusInd = null;
				argJusInd = factory.getOWLNamedIndividual(IRI.create(ontoID
						+ "#" + "ArgumentJustification" + argJus.hashCode()));
				changes
						.add(createIndividual(argJusInd,
								"ArgumentJustification"));

				// creating Data property hasDomainCaseID
				ArrayList<Long> domCaseList = argJus.getDomainCasesIDs();
				for (Long domCasID : domCaseList) {
					if (domCasID != -1 && domCasID != 0)
						changes.add(createDataPropInt(argJusInd,
								"hasDomainCaseID", Integer.parseInt(domCasID.toString())));
				}

				// creating Data property hasArgumentCaseID
				ArrayList<Long> argCaseList = argJus.getArgumentCasesIDs();
				for (Long argCasID : argCaseList) {
					if (argCasID != -1 && argCasID != 0)
						changes.add(createDataPropInt(argJusInd,
								"hasArgumentCaseID", Integer.parseInt(argCasID.toString())));
				}

				// creating Data property hasDescription
				String desc = this.filter(argJus.getDescription());
				if (!desc.equals("") && desc != null)
					changes.add(createDataPropString(argJusInd, "hasDescription",
							desc));

				// creating Object property hasArgumentationScheme
				ArrayList<ArgumentationScheme> argSchList = argJus
						.getArgumentationSchemes();
				for (ArgumentationScheme argSch : argSchList) {
					if (argSch.getID() != -1 && argSch.getID() != 0) {
						// creating ArgumentationScheme individual
						OWLNamedIndividual argSchInd = null;
						argSchInd = factory.getOWLNamedIndividual(IRI
								.create(ontoID + "#" + "ArgumentationScheme"
										+ argSch.hashCode()));
						changes.add(createIndividual(argSchInd,
								"ArgumentationScheme"));

						// creating Data property hasID
						changes.add(createDataPropInt(argSchInd, "hasID", (int) argSch.getID()));

						// creating Data property argTitle
						if (!argSch.getArgTitle().equals("")
								&& argSch.getArgTitle() != null)
							changes.add(createDataPropString(argSchInd, "argTitle",
									this.filter(argSch.getArgTitle())));

						// creating Data property creationDate
						String argSchDate = argSch.getCreationDate();
						if (!argSchDate.equals("") && argSchDate != null)
							changes.add(createDataPropString(argSchInd,
									"creationDate", this.filter(argSchDate.toString())
											.trim()));

						// creating Object property hasAuthor
						Author auth = argSch.getAuthor();
						if (auth != null) {
							// creating Author individual
							OWLNamedIndividual authInd = null;
							authInd = factory.getOWLNamedIndividual(IRI
									.create(ontoID + "#" + "Author"
											+ auth.hashCode()));
							changes.add(createIndividual(authInd, "Author"));

							// creating Data property authorName
							String authName = this.filter(auth.getAuthorName());
							if (!authName.equals("") && authName != null)
								changes.add(createDataPropString(authInd,
										"authorName", authName));

							// adding hasAuthor to ArgumentationScheme
							changes.add(createObjectProp(argSchInd,
									"hasAuthor", authInd));
						}

						// creating Object property hasConclusion
						Conclusion conc = argSch.getConclusion();
						if (conc != null) {
							// creating Conclusion individual
							OWLNamedIndividual concInd = null;
							concInd = factory.getOWLNamedIndividual(IRI
									.create(ontoID + "#" + "Conclusion"
											+ conc.getID()));
							changes
									.add(createIndividual(concInd, "Conclusion"));

							// creating Data property hasID
							Long concID = conc.getID();
							if (concID != -1 && concID != 0)
								changes.add(createDataPropInt(concInd, "hasID",
										Integer.parseInt(concID.toString())));

							// creating Data property hasDescription
							String concDesc = this.filter(conc.getDescription());
							if (!concDesc.equals("") && concDesc != null)
								changes.add(createDataPropString(concInd,
										"hasDescription", concDesc));

							// adding hasConclusion to ArgumentSolution
							changes.add(createObjectProp(argSchInd,
									"hasConclusion", concInd));
						}

						// creating Object property hasPremise
						ArrayList<Premise> premList = argSch.getPremises();
						createObjectPropPremiseList(argSchInd, "hasPremise",
								premList, changes);

						// creating Object property hasException
						ArrayList<Premise> excList = argSch.getExceptions();
						createObjectPropPremiseList(argSchInd, "hasException",
								excList, changes);

						// creating Object property hasPresumption
						ArrayList<Premise> presList = argSch.getPresumptions();
						createObjectPropPremiseList(argSchInd,
								"hasPresumption", presList, changes);

						// adding hasArgumentationScheme to
						// ArgumentJustification
						changes.add(createObjectProp(argJusInd,
								"hasArgumentationScheme", argSchInd));
					}
				}

				// creating Object property hasDialogueGraph
				ArrayList<DialogueGraph> diaGraList = argJus
						.getDialogueGraphs();
				for (DialogueGraph dg : diaGraList) {
					// creating DialogueGraph individual
					OWLNamedIndividual dgInd = null;
					dgInd = factory.getOWLNamedIndividual(IRI.create(ontoID
							+ "#" + "DialogueGraph" + dg.hashCode()));
					changes.add(createIndividual(dgInd, "DialogueGraph"));

					// creating Object property hasNodes
					ArrayList<ArgNode> nodeList = dg.getNodes();
					for (ArgNode node : nodeList) {
						// creating ArgNode individual
						OWLNamedIndividual argNodeInd = null;
						argNodeInd = factory.getOWLNamedIndividual(IRI
								.create(ontoID + "#" + "ArgNode"
										+ node.hashCode()));
						changes.add(createIndividual(argNodeInd, "ArgNode"));

						// creating Data property hasArgCaseID
						Long argCasID = node.getArgCaseID();
						if (argCasID != -1 && argCasID != 0)
							changes
									.add(createDataPropInt(argNodeInd,
											"hasArgCaseID", Integer.parseInt(argCasID.toString())));

						// creating Data property hasChildArgCaseID
						ArrayList<Long> childList = node
								.getChildArgCaseIDList();
						for (Long child : childList) {
							if (child != -1 && child != 0)
								changes.add(createDataPropInt(argNodeInd,
										"hasChildArgCaseID", Integer.parseInt(child.toString())));
						}

						// creating Data property hasParentArgCaseID
						Long parent = node.getParentArgCaseID();
						if (parent != -1 && parent != 0)
							changes.add(createDataPropInt(argNodeInd,
									"hasParentArgCaseID", Integer.parseInt(parent.toString())));

						// creating Data property hasNodeType
						NodeType nodeType = node.getNodeType();
						if (nodeType != null)
							changes.add(createDataPropString(argNodeInd,
									"hasNodeType", nodeType.name()));

						// adding hasNodes to DialogueGraph
						changes.add(createObjectProp(dgInd, "hasNodes",
								argNodeInd));
					}

					// adding hasDialogueGraph to ArgumentJustification
					changes.add(createObjectProp(argJusInd, "hasDialogueGraph",
							dgInd));
				}

				// adding hasArgumentJustification to ArgumentCase
				changes.add(createObjectProp(argCase,
						"hasArgumentJustification", argJusInd));
			}

		} // end for ArgumentCases

		System.out.println("Number of AddAxiom Changes: " + changes.size());

		// apply all changes to the Model
		for (AddAxiom addAxiom : changes) {
			// System.out.println(addAxiom.getAxiom().toString());
			manager.applyChange(addAxiom);
		}

		// save the Ontology to file
		OWLXMLOntologyFormat owlxmlFormat = new OWLXMLOntologyFormat();
		RDFXMLOntologyFormat rdfxmlFormat = new RDFXMLOntologyFormat();

		OWLOntologyFormat format = manager.getOntologyFormat(argOntology);
		// System.out.println(" format: " + format);

		// if(format.isPrefixOWLOntologyFormat()) {
		// owlxmlFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
		// }

		File file = new File(ontoFileName);
		// manager.saveOntology(domainOntology, rdfxmlFormat,
		// IRI.create(file.toURI()));

		System.out.println("OWLARGPARSER: saving ArgOntology to "
				+ ontoFileName);
		try {
			manager.saveOntology(argOntology, IRI.create(file.toURI()));
			System.out.println("OWLARGPARSER: " + ontoFileName + " saved.");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("OWLARGPARSER: " + ontoFileName + " ERROR SAVING.");
		}
		manager.removeOntology(argOntology);

	}

	/**
	 * Creates an OWL individual
	 * 
	 * @param individual
	 *            The OWL individual to create
	 * @param indName
	 *            The individual name
	 * @return axiom The axiom to add to the ontology
	 */
	public AddAxiom createIndividual(OWLNamedIndividual individual,
			String indName) {
		OWLClass Class = factory
				.getOWLClass(IRI.create(ontoID + "#" + this.filter(indName)));
		OWLClassAssertionAxiom indAxiom = factory.getOWLClassAssertionAxiom(
				Class, individual);
		AddAxiom axiom = new AddAxiom(argOntology, indAxiom);

		return axiom;
	}

	/**
	 * Creates an OWL Data Property
	 * 
	 * @param individual
	 *            The individual that holds the property
	 * @param propName
	 *            The property name
	 * @param propString
	 *            The property value
	 * @return axiom The axiom to add to the ontology
	 */
	public AddAxiom createDataPropString(OWLNamedIndividual individual,
			String propName, String propString) {
		OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID
				+ "#" + this.filter(propName)));
		OWLLiteral owlL = factory.getOWLLiteral(this.filter(propString));
		OWLDataPropertyAssertionAxiom addProp = factory
				.getOWLDataPropertyAssertionAxiom(owlP, individual, owlL);
		AddAxiom axiom = new AddAxiom(argOntology, addProp);

		return axiom;
	}
	
	/**
	 * Creates an OWL Data Property
	 * 
	 * @param individual
	 *            The individual that holds the property
	 * @param propName
	 *            The property name
	 * @param propInt
	 *            TODO
	 * @return axiom The axiom to add to the ontology
	 */
	public AddAxiom createDataPropInt(OWLNamedIndividual individual,
			String propName, int propInt) {
		OWLDataProperty owlP = factory.getOWLDataProperty(IRI.create(ontoID
				+ "#" + this.filter(propName)));
		OWLLiteral owlL = factory.getOWLLiteral(propInt);
		OWLDataPropertyAssertionAxiom addProp = factory
				.getOWLDataPropertyAssertionAxiom(owlP, individual, owlL);
		AddAxiom axiom = new AddAxiom(argOntology, addProp);

		return axiom;
	}
	

	/**
	 * Creates an OWL Object Property
	 * 
	 * @param individual
	 *            The individual that holds the property
	 * @param propName
	 *            The property name
	 * @param individualObj
	 *            The individual object of the property
	 * @return axiom The axiom to add to the ontology
	 */
	public AddAxiom createObjectProp(OWLNamedIndividual individual,
			String propName, OWLNamedIndividual individualObj) {
		OWLObjectProperty owlP = factory.getOWLObjectProperty(IRI.create(ontoID
				+ "#" + this.filter(propName)));
		OWLObjectPropertyAssertionAxiom addProp = factory
				.getOWLObjectPropertyAssertionAxiom(owlP, individual,
						individualObj);
		AddAxiom axiom = new AddAxiom(argOntology, addProp);

		return axiom;
	}

	/**
	 * Creates the Object property hasNorm for a SocialEntity individual
	 * 
	 * @param socEnt
	 *            The SocialEntity
	 * @param individual
	 *            The SocialEntity individual
	 * @param changes
	 *            The Queue<AddAxiom> changes to add in the ontology
	 */

	public void createSocialEntityHasNorm(SocialEntity socEnt,
			OWLNamedIndividual individual, Queue<AddAxiom> changes) {

		// creating Object property hasNorm
		ArrayList<Norm> normList = socEnt.getNorms();
		for (Norm n : normList) {
			// creating Norm individual
			OWLNamedIndividual normInd = null;
			if (n.getID() != -1 && n.getID() != 0) {
				normInd = factory.getOWLNamedIndividual(IRI.create(ontoID + "#"
						+ "Norm" + n.hashCode()));
				changes.add(createIndividual(normInd, "Norm"));
			} else
				continue;

			// creating Data property hasID
			changes
					.add(createDataPropInt(normInd, "hasID", (int) n.getID()));

			// creating Data property hasDescription
			if (!n.getDescription().equals(""))
				changes.add(createDataPropString(normInd, "hasDescription", this.filter(n.getDescription())));

			// adding hasNorm to SocialEntity
			changes.add(createObjectProp(individual, "hasNorm", normInd));
		}
	}

	/**
	 * Creates the Object property hasValPref for a SocialEntity individual
	 * 
	 * @param socEnt
	 *            The SocialEntity
	 * @param individual
	 *            The SocialEntity individual
	 * @param changes
	 *            The Queue<AddAxiom> changes to add in the ontology
	 */
	public void createSocialEntityHasValPref(SocialEntity socEnt,
			OWLNamedIndividual individual, Queue<AddAxiom> changes) {

		// creating Object property hasValPref
		ValPref valPref = socEnt.getValPref();
		if (!valPref.getValues().isEmpty()) {
			// creating ValPref individual
			OWLNamedIndividual valPrefInd = null;
			valPrefInd = factory.getOWLNamedIndividual(IRI.create(ontoID + "#"
					+ "ValPref" + valPref.hashCode()));
			changes.add(createIndividual(valPrefInd, "ValPref"));

			// creating Data property hasValues
			// TODO check if values are created in the correct order
			ArrayList<String> valueList = valPref.getValues();
			for (String v : valueList) {
				changes.add(createDataPropString(valPrefInd, "hasValues", this.filter(v)));
			}

			// adding ValPref to SocialEntity
			changes.add(createObjectProp(individual, "hasValPref", valPrefInd));
		}
	}

	/**
	 * Creates an object property with a list of Premises
	 * 
	 * @param individual
	 *            The individual that has the property
	 * @param propName
	 *            The name of the property
	 * @param premises
	 *            The ArrayList of Premises
	 * @param changes
	 *            The Queue<AddAxiom> to add changes to the ontology
	 */
	public void createObjectPropPremiseList(OWLNamedIndividual individual,
			String propName, ArrayList<Premise> premises,
			Queue<AddAxiom> changes) {

		for (Premise prem : premises) {
			// creating Premise individual
			OWLNamedIndividual premiseInd = null;
			if (prem.getID() != -1 && prem.getID() != 0) {
				premiseInd = factory.getOWLNamedIndividual(IRI.create(ontoID
						+ "#" + "Premise" + prem.getID() + prem.getContent()));
				changes.add(createIndividual(premiseInd, "Premise"));
			} else
				continue;

			// creating Data property hasID
			changes
					.add(createDataPropInt(premiseInd, "hasID", (int) prem.getID()));

			// creating Data property hasName
			changes.add(createDataPropString(premiseInd, "hasName", this.filter(prem.getName())));

			// creating Data property hasContent
			changes.add(createDataPropString(premiseInd, "hasContent", this.filter(prem.getContent())));

			// adding hasPremise to DomainContext
			changes.add(createObjectProp(individual, propName, premiseInd));
		}
	}

	public String filter(String userDest) {
		Pattern p = Pattern.compile("[,:.;!@\\[\\]|\\#$~%&¬/()='¿?¡+*\n]");
		Matcher m = p.matcher(userDest);
		if (m.find())
			userDest = m.replaceAll("");
		return userDest;
	}
}
