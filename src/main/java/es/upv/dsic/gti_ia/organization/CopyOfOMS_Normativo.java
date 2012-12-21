package es.upv.dsic.gti_ia.organization;


import jason.asSyntax.Rule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import omsTests.DatabaseAccess;
import es.upv.dsic.gti_ia.cAgents.BeginState;
import es.upv.dsic.gti_ia.cAgents.BeginStateMethod;
import es.upv.dsic.gti_ia.cAgents.CAgent;
import es.upv.dsic.gti_ia.cAgents.CFactory;
import es.upv.dsic.gti_ia.cAgents.CProcessor;
import es.upv.dsic.gti_ia.cAgents.FinalState;
import es.upv.dsic.gti_ia.cAgents.FinalStateMethod;
import es.upv.dsic.gti_ia.cAgents.ReceiveState;
import es.upv.dsic.gti_ia.cAgents.ReceiveStateMethod;
import es.upv.dsic.gti_ia.cAgents.SendState;
import es.upv.dsic.gti_ia.cAgents.SendStateMethod;
import es.upv.dsic.gti_ia.cAgents.WaitState;
import es.upv.dsic.gti_ia.cAgents.protocols.FIPA_REQUEST_Initiator;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.norms.BeliefDataBaseInterface;
import es.upv.dsic.gti_ia.norms.Norm;
import es.upv.dsic.gti_ia.norms.NormParser;



public class CopyOfOMS_Normativo extends CAgent{

	public CopyOfOMS_Normativo(AgentID aid) throws Exception {
		super(aid);
		// TODO Auto-generated constructor stub
	}



	@Override
	protected void execution(CProcessor firstProcessor,
			ACLMessage welcomeMessage) {
		ACLMessage msg;

		DatabaseAccess dbA = new DatabaseAccess();

		class myFIPA_REQUEST extends FIPA_REQUEST_Initiator {
			protected void doInform(CProcessor myProcessor, ACLMessage msg) {
				System.out.println(myProcessor.getMyAgent().getName() + ": "
						+ msg.getSender().name + " informs me "
						+ msg.getContent());
			}
		}

		try
		{ 


			//------------------Clean Data Base -----------//
			dbA.executeSQL("DELETE FROM normList");
			dbA.executeSQL("DELETE FROM agentPlayList");
			dbA.executeSQL("DELETE FROM agentList");
			dbA.executeSQL("DELETE FROM roleList WHERE idroleList != 1");
			dbA.executeSQL("DELETE FROM unitHierarchy WHERE idChildUnit != 1");
			dbA.executeSQL("DELETE FROM unitList WHERE idunitList != 1");


			//----------------------------------- Insert agent List---------------------------
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('vb')");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('bigBrother')");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('ea')");
			dbA.executeSQL("INSERT INTO `agentList` (`agentName`) VALUES ('sv')");

			//----------------------------------- Insert unit List---------------------------

			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('forum',(SELECT idunitType FROM unitType WHERE unitTypeName = 'hierarchy'))");
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('fraternity',(SELECT idunitType FROM unitType WHERE unitTypeName = 'team'))");
			dbA.executeSQL("INSERT INTO `unitList` (`unitName`,`idunitType`) VALUES ('panel',(SELECT idunitType FROM unitType WHERE unitTypeName = 'flat'))");

			//----------------------------------- Insert unit hierarchy---------------------------
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'virtual'),(SELECT idunitList FROM unitList WHERE unitName = 'forum'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'forum'),(SELECT idunitList FROM unitList WHERE unitName = 'fraternity'))");
			dbA.executeSQL("INSERT INTO `unitHierarchy` (`idParentUnit`,`idChildUnit`) VALUES ((SELECT idunitList FROM unitList WHERE unitName = 'forum'),(SELECT idunitList FROM unitList WHERE unitName = 'panel'))");

			//----------------------------------- Insert into roleList---------------------------



			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('moderator',(SELECT idunitList FROM unitList WHERE unitName = 'forum'),"+
					"(SELECT idposition FROM position WHERE positionName = 'supervisor'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('participant',(SELECT idunitList FROM unitList WHERE unitName = 'forum'),"+
					"(SELECT idposition FROM position WHERE positionName = 'subordinate'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('brother',(SELECT idunitList FROM unitList WHERE unitName = 'fraternity'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('president',(SELECT idunitList FROM unitList WHERE unitName = 'fraternity'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");


			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('reporter',(SELECT idunitList FROM unitList WHERE unitName = 'panel'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('follower',(SELECT idunitList FROM unitList WHERE unitName = 'panel'),"+
					"(SELECT idposition FROM position WHERE positionName = 'member'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'external'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'public'))");

			dbA.executeSQL("INSERT INTO `roleList` (`roleName`,`idunitList`,`idposition`,`idaccessibility`,`idvisibility`) VALUES"+ 
					"('moderator',(SELECT idunitList FROM unitList WHERE unitName = 'panel'),"+
					"(SELECT idposition FROM position WHERE positionName = 'creator'), "+
					"(SELECT idaccessibility FROM accessibility WHERE accessibility = 'internal'),"+ 
			"(SELECT idvisibility FROM visibility WHERE visibility = 'private'))");



			//----------------------------------- Insert agent Play List---------------------------


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'vb'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'bigBrother'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'sv'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'virtual'))))");


			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'vb'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'bigBrother'),(SELECT idroleList FROM roleList WHERE (roleName = 'moderator' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'bigBrother'),(SELECT idroleList FROM roleList WHERE (roleName = 'moderator' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'panel'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'president' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'fraternity'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'sv'),(SELECT idroleList FROM roleList WHERE (roleName = 'brother' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'fraternity'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'participant' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'ea'),(SELECT idroleList FROM roleList WHERE (roleName = 'follower' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'panel'))))");
			dbA.executeSQL("INSERT INTO `agentPlayList` (`idagentList`, `idroleList`) VALUES"+
			"((SELECT idagentList FROM agentList WHERE agentName = 'sv'),(SELECT idroleList FROM roleList WHERE (roleName = 'reporter' AND idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'panel'))))");


			//-------------------------------------------------- Normas -------------------------------------------------

			dbA.executeSQL("INSERT INTO `normList` (`idunitList`, `normName`, `iddeontic`, `idtargetType`, `targetValue`, `idactionnorm`, `normContent`, `normRule`) VALUES"+
			"((SELECT idunitList FROM unitList WHERE unitName = 'forum'),'moderatorDerU', (SELECT iddeontic FROM deontic WHERE deonticdesc = 'p'), (SELECT idtargetType FROM targetType WHERE targetName = 'roleName'), (SELECT idroleList FROM roleList WHERE roleName = 'participant' and idunitList = (SELECT idunitList FROM unitList WHERE unitName = 'forum')), (SELECT idactionNorm FROM actionNorm WHERE description = 'deregisterUnit'), 'normContent', 'rule')");



			//----------------------------wait STATE----------------------------------
			WaitState WAIT = new WaitState("WAIT", 0);




			//------------------------------------------------------------------------
			//-----------------------Initiator CFactory definition------------------
			//------------------------------------------------------------------------

			//MessageFilter filter = new MessageFilter("result = true");
			CFactory talk = new CFactory("SUMMATION_REQUEST", null, 1,this);

			//----------------------------BEGIN STATE----------------------------------
			BeginState BEGIN = (BeginState) talk.cProcessorTemplate().getState("BEGIN");
			BEGIN.setMethod(new BEGIN_Method());

			//----------------------------SEND RULES STATE----------------------------------
			SendState SRULES = new SendState("SEND_RULES");
			SRULES.setMethod(new Send_rules());
			talk.cProcessorTemplate().registerState(SRULES);
			talk.cProcessorTemplate().addTransition(BEGIN, SRULES);

			//----------------------------REQUEST STATE----------------------------------
			SendState QUERY = new SendState("QUERY");
			QUERY.setMethod(new QUERY_Method());
			talk.cProcessorTemplate().registerState(QUERY);
			talk.cProcessorTemplate().addTransition(SRULES, QUERY);

			//----------------------------WAIT STATE----------------------------------
			talk.cProcessorTemplate().registerState(WAIT);
			talk.cProcessorTemplate().addTransition(QUERY, WAIT);


			//----------------------------RECEIVE STATE----------------------------------
			ReceiveState RECEIVE = new ReceiveState("RECEIVE");
			RECEIVE.setAcceptFilter(null); // null -> accept any message
			RECEIVE.setMethod(new RECEIVE_Method());
			talk.cProcessorTemplate().registerState(RECEIVE);
			talk.cProcessorTemplate().addTransition(WAIT, RECEIVE);



			//----------------------------FINAL STATE----------------------------------
			FinalState FINAL = new FinalState("FINAL");
			FINAL.setMethod(new FINAL_Method());
			talk.cProcessorTemplate().registerState(FINAL);
			talk.cProcessorTemplate().addTransition(RECEIVE, FINAL);






			this.addFactoryAsInitiator(talk);


			this.startSyncConversation("SUMMATION_REQUEST");


			/*


			// We create the message that will be sent in the doRequest method
			// of the conversation

			msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setReceiver(new AgentID("Ejemplo"));

			String stringNorm = "@normspecification[p,"+
			"<positionName:creator>,"+
			"deregisterUnit(foro, agente),"+
			"not(playsRole(pruebas2,RoleName, UnitName) & hasPosition(RoleName, UnitName, Position) & Position==creator),"+
			"(15 / 20) == 20]";
			StringBuffer StringBuffer1 = new StringBuffer(stringNorm);

			InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));


			NormParser parser = new NormParser(input);

			Norm norma = parser.parser();







			String norm = "";
			if (norma.getExpiration().trim().equals("_"))
				norm = "normspecification("+norma.getActivation()+")";
			else
				norm = "normspecification("+norma.getActivation()+" & not("+norma.getExpiration()+"))";


			msg.setContent(norm);
			//msg.setContent(content);
			msg.setProtocol("fipa-request");
			msg.setSender(getAid());

			// The agent creates the CFactory that creates processors that initiate
			// REQUEST protocol conversations. In this
			// example the CFactory gets the name "TALK", we don't add any
			// additional message acceptance criterion other than the required
			// by the REQUEST protocol (null) and we do not limit the number of simultaneous
			// processors (value 0)

			CFactory talk = new myFIPA_REQUEST().newFactory("TALK", null , msg,1, this, 0);

			// The factory is setup to answer start conversation requests from the agent
			// using the REQUEST protocol.

			for (int i=0 ; i<10;i++)
			{
			this.addFactoryAsInitiator(talk);

			// finally the new conversation starts. Because it is synchronous, 
			// the current interaction halts until the new conversation ends.
			//myProcessor.createSyncConversation(msg);
			this.startSyncConversation("TALK");
			}



			firstProcessor.ShutdownAgent();*/

		}catch(Exception e)
		{
			System.out.println("Exception: "+ e.getMessage());
		}



		//		this.registerAbstractNorm("Ejemplo");
		//			
		//		try {
		//			Thread.sleep(12*1000);
		//		} catch (InterruptedException e) {
		//			e.printStackTrace();
		//		}

		//	this.unregisterAbstractNorm3("agentA");


	}


	@Override
	protected void finalize(CProcessor firstProcessor,
			ACLMessage finalizeMessage) {
		// TODO Auto-generated method stub

	}

	//------------------------------------------------------------------------
	//-----------------------CFactory implementation--------------------------
	//------------------------------------------------------------------------


	//	class BEGIN_MANAGER_Method implements BeginStateMethod {
	//
	//		public String run(CProcessor myProcessor, ACLMessage msg) {
	//
	//
	//			return "WAIT";
	//		};
	//
	//	}



	class BEGIN_Method implements BeginStateMethod {

		public String run(CProcessor myProcessor, ACLMessage msg) {


			return "SEND_RULES";
		};

	}


	class Send_rules implements SendStateMethod {
		int n=0;
		private BeliefDataBaseInterface bdbi = null;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {
			String state = "QUERY";
			try
			{
				bdbi = new BeliefDataBaseInterface();

				Norm norma = null;

				

				StringBuffer StringBuffer1 = new StringBuffer("@ allocateRoleNorm[p,<positionName:member>,allocateRole(RoleName, UnitName,_,AgentName),isRole(RoleName, UnitName) & roleCardinality(RoleName,UnitName,Cardinality) & Cardinality >3,isRole(moderator, forum)];");

				InputStream input =  new ByteArrayInputStream(StringBuffer1.toString().getBytes("UTF-8"));

				NormParser parser = new NormParser(input);

				norma = parser.parser();

				System.out.println("---------------------------------");

				Rule rule = bdbi.buildNormRule(norma);
				
				ArrayList<Rule> rules = new ArrayList<Rule>();
				rules.add(rule);

				System.out.println("---------------------------------");






				//Sends a message with rules.


				messageToSend.setPerformative(ACLMessage.INFORM);
				messageToSend.setLanguage("AgentSpeak");
				messageToSend.setSender(myProcessor.getMyAgent().getAid());
				messageToSend.setReceiver(new AgentID("JasonAgent"));
				messageToSend.setReplyWith(myProcessor.getConversationID());



				messageToSend.setContentObject(rules);


				System.out.println("Voy a enviar un mensaje con las reglas");

			}catch(Exception e)
			{
				System.out.println("Excepcion: "+ e.getMessage());

			}


			return state;
		}

	}

	class QUERY_Method implements SendStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageToSend) {

		
			String state = "WAIT";

			//Sends a message with rules.


			messageToSend.setPerformative(ACLMessage.QUERY_REF);
			messageToSend.setLanguage("AgentSpeak");
			messageToSend.setSender(myProcessor.getMyAgent().getAid());
			messageToSend.setReceiver(new AgentID("JasonAgent"));
			messageToSend.setReplyWith(myProcessor.getConversationID());



			messageToSend.setContent("allocateRole(participant, virtual,_,AgentName)");


			System.out.println("Voy a enviar un mensaje al agente Jason");


			return state;
		}

	}

	class RECEIVE_Method implements ReceiveStateMethod {
		int n=0;

		public String run(CProcessor myProcessor, ACLMessage messageReceived) {

			String state = "FINAL";

			System.out.println("Header: "+ messageReceived.getHeaderValue("activatedNorm"));


			System.out.println("Resultado: "+ messageReceived.getContent());

			return state;
		}

	}



	class FINAL_Method implements FinalStateMethod {
		public void run(CProcessor myProcessor, ACLMessage responseMessage) {


			myProcessor.ShutdownAgent();


		}

	}

	//	class FINAL_MANAGER_Method implements FinalStateMethod {
	//		public void run(CProcessor myProcessor, ACLMessage responseMessage) {
	//
	//		}
	//
	//	}

}
