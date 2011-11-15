package Argumentation_Example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Conclusion;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Dialogue;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.DomainCase;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Position;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.SocialEntity;
import es.upv.dsic.gti_ia.argAgents.knowledgeResources.Solution;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.SingleAgent;

public class TesterAgentArgLearn1and2 extends SingleAgent{

	int totalTickets;
	//String OWLFileName;
	ArrayList<SocialEntity> socialEntities;
	String resultFileName;
	String finishFileName;
//	DomainCBR domCBR;
	int casesPerAgent;
	int iteration;
	Vector<DomainCase> domCases;
	ArrayList<String> argCasesFiles;
	ArrayList<ArgCAgent> agents;
	String currentDialogueID="";
	long dialogueInitTime=0;
	String commitmentStoreID;
	long multTimeFactor=10;
	
	
	public TesterAgentArgLearn1and2(AgentID aid, int totalTickets, ArrayList<SocialEntity> socialEntities, String commitmentStoreID, String resultFileName, 
			String finishFileName, int casesPerAgent, int iteration, Vector<DomainCase> domCases, ArrayList<String> argCasesFiles, ArrayList<ArgCAgent> agents) throws Exception {
		super(aid);
		logger.info(this.getName()+": agent created");
		
		this.totalTickets=totalTickets;
		//this.OWLFileName=OWLFileName;
		this.socialEntities=socialEntities;
		this.commitmentStoreID=commitmentStoreID;
		this.resultFileName=resultFileName;
		this.finishFileName=finishFileName;
		this.casesPerAgent=casesPerAgent;
		this.iteration = iteration;
		this.domCases = domCases;
		this.argCasesFiles = argCasesFiles;
		this.agents = agents;
		
	}

	public void execute(){
		
		try{
		try{
			// Create file 
			FileWriter fstream = new FileWriter(finishFileName,false);
			BufferedWriter outFile = new BufferedWriter(fstream);
			//Close the output stream
			outFile.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		try {
			Thread.sleep(2*1000);//wait 2 seconds at the beginning 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		ArrayList<Ticket> tickets=getTestTickets(totalTickets);
		int nTicket=0;
		
		//create a new instance of the ticket without the solutions
		DomainCase auxTicket=domCases.get(nTicket);
//		Ticket ticketToSend=
//			new Ticket(auxTicket.getTicketID(), auxTicket.getCategoryNode(), auxTicket.getAttributes(), auxTicket.getProblemDesc(), auxTicket.getProject(), auxTicket.getSolvingGroups(), auxTicket.getSolvingOperators(), null);

		DomainCase ticketToSend = auxTicket;
		
		long time=System.nanoTime();
		currentDialogueID=String.valueOf(time);
		ArrayList<String> agentIDs=new ArrayList<String>();
		Dialogue dialogue = new Dialogue(currentDialogueID, agentIDs, ticketToSend.getProblem());
		
		//store the init date of dialogue
		dialogueInitTime=System.currentTimeMillis();
		
		//add dialogue to Commitment Store
		sendMessage(commitmentStoreID, "ADDDIALOGUE", dialogue.getDialogueID(), dialogue);
		
		Iterator<SocialEntity> iterAgents=socialEntities.iterator();
		while(iterAgents.hasNext()){
			SocialEntity socialEntity=iterAgents.next();
			sendMessage(socialEntity.getName(), "OPENDIALOGUE", currentDialogueID, ticketToSend);
		}
		nTicket++;
		int totalErrors=0;
		int solvedProblems=0;
		logger.info("\n\n********\n"+this.getName()+": " + "PARTITION " + casesPerAgent + " ITERATION " + iteration+"\n\n********\n");
		
		long myLastCheckMillis=0l;
		Solution finalSolution=new Solution();
		
		while(true){
			
			Thread.sleep(100*multTimeFactor);
			
//			//this is to avoid a lot of test about modification date
//			if(myLastCheckMillis>0l){
//				
//				long currentTime=System.currentTimeMillis();
//				if(currentTime-myLastCheckMillis<30l*multTimeFactor){
//					continue;
//				}
//			}
			
			
			//ask to commitment store the elapsed milliseconds since the last modification (new arguments or positions) 
			sendMessage(commitmentStoreID, "LASTMODIFICATIONDATE", currentDialogueID, null);
			ArrayList<String> locutions=new ArrayList<String>();
			locutions.add("LASTMODIFICATIONDATE");
			ACLMessage msg=receiveACLMessage();
			
			if(msg==null){
				logger.error(this.getName()+": Commitment Store does not respond!!!");
				continue;
			}
			Long millisDifference = (Long) msg.getContentObject();
			logger.info("\n"+this.getName()+": millis difference="+millisDifference+"\n");
			myLastCheckMillis=System.currentTimeMillis();
			
			//dialogue must finish
			if(millisDifference>150*multTimeFactor){
				logger.info(this.getName()+": "+"DIALOGUE MUST FINISH!\n");

				iterAgents=socialEntities.iterator();
				while(iterAgents.hasNext()){
					SocialEntity socialEntity=iterAgents.next();
					sendMessage(socialEntity.getName(), "FINISHDIALOGUE", currentDialogueID, null);
				}
				
				//wait to give time to the agents to send its position (with the timesAccepted updated) to commitment Store
				try {
					Thread.sleep(10*multTimeFactor);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				
				
				// Select the most frequent position (or the most voted in case of draw). Random by default
				
				ArrayList<Position> allPositions=getAllPositions(currentDialogueID);
				
				Iterator<Position> iterPositions=allPositions.iterator();
				HashMap<Long,Solution> possibleSolutions=new HashMap<Long, Solution>();
				HashMap<Long,Integer> frequentPositions=new HashMap<Long, Integer>();
				HashMap<Long,Integer> votedPositions=new HashMap<Long, Integer>();
				HashMap<Long,ArrayList<Position>> positionsPerSolution=new HashMap<Long, ArrayList<Position>>();
				while(iterPositions.hasNext()){
					Position pos=iterPositions.next();
					Long solID=pos.getSolution().getConclusion().getID();
					Solution sol=possibleSolutions.get(solID);
					
					if(sol!=null){//solution already in possibleSolutions, increment counts
						//increment frequent
						int frequency=frequentPositions.get(solID);
						frequency++;
						frequentPositions.put(solID, frequency);
						//sum votes
						int votes=votedPositions.get(solID);
						votes+=pos.getTimesAccepted();
						votedPositions.put(solID, votes);
						//add this position to the other positions defending same solution
						ArrayList<Position> positions=positionsPerSolution.get(solID);
						positions.add(pos);
						positionsPerSolution.put(solID, positions);
					}
					else{// add solution to possibleSolutions initializing all necessary
						possibleSolutions.put(solID, pos.getSolution());
						frequentPositions.put(solID, 1);
						votedPositions.put(solID, pos.getTimesAccepted());
						
						ArrayList<Position> positions=new ArrayList<Position>();
						positions.add(pos);
						positionsPerSolution.put(solID, positions);
					}
					
				}
				
//				if (frequentPositions.size() == 1){
//					agreementReached = 1;
//					acceptanceFrequency = frequentPositions.values().iterator().next();
//				}
				
				//obtain most voted position
				Set<Long> keySet=votedPositions.keySet();
				Iterator<Long> iterKeySet=keySet.iterator();
				int maxVotes=Integer.MIN_VALUE;
				Long maxVotedSolID=0l;
				ArrayList<Long> drawSolutions=new ArrayList<Long>();
				while(iterKeySet.hasNext()){
					Long solID=iterKeySet.next();
					int voteRate=votedPositions.get(solID);
					if(voteRate>maxVotes){
						maxVotes=voteRate;
						maxVotedSolID=solID;
						drawSolutions=new ArrayList<Long>();//reset draw solutions
					}
					else if(voteRate==maxVotes){
						if(drawSolutions.size()==0)
							drawSolutions.add(maxVotedSolID);
						drawSolutions.add(solID);
					}
				}
				
				
				//if there is not draw in voted positions, take the most voted as a final solution 
				if(drawSolutions.size()==0){
					finalSolution=possibleSolutions.get(maxVotedSolID);
				}
				//if there is draw in voted positions, obtain the most frequent position
				//if there is another draw, will take the position with bigger index of the most frequent positions
				else{
					Iterator<Long> iterDrawSol=drawSolutions.iterator();
					int maxFrequency=Integer.MIN_VALUE;
					Long maxFrequentSolID=0l;
					ArrayList<Long> drawsInFreq=new ArrayList<Long>();
					while(iterDrawSol.hasNext()){
						Long solID=iterDrawSol.next();
						int frequency=frequentPositions.get(solID);
						if(frequency>maxFrequency){
							maxFrequency=frequency;
							maxFrequentSolID=solID;
							drawsInFreq=new ArrayList<Long>();
						}
						else if(frequency==maxFrequency){
							if(drawsInFreq.size()==0)
								drawsInFreq.add(maxFrequentSolID);
							drawsInFreq.add(solID);
						}
					}
					if(drawsInFreq.size()==0)
						finalSolution=possibleSolutions.get(maxFrequentSolID);
					else{//choose a solution if are in draw with votes and frequency
//						int randomSolIndex=(int)Math.random()*drawsInFreq.size();
						long maxIndSol=drawsInFreq.size()-1;
						for (int i = 0; i < drawsInFreq.size(); i++){
							if (drawsInFreq.get(i)>drawsInFreq.get((int) maxIndSol))
								maxIndSol = i;
						}
//						long randomSolID=drawsInFreq.get(randomSolIndex);
//						finalSolution=possibleSolutions.get(randomSolID);
						long maxSolID=drawsInFreq.get((int) maxIndSol);
						finalSolution=possibleSolutions.get(maxSolID);
					}
				}
				if(finalSolution==null)
					finalSolution=new Solution();
				if(finalSolution.getConclusion().getID()!=-1){
					
					//to print the agentIDs that proposed each position
					ArrayList<Position> positions=positionsPerSolution.get(finalSolution.getConclusion().getID());
					Iterator<Position> iterPositionsSol=positions.iterator();
					String agentIDs2="";
					while(iterPositionsSol.hasNext()){
						Position pos=iterPositionsSol.next();
						agentIDs2+=pos.getAgentID()+" ";
					}
					logger.info("\n++++++++++++++++\nFINAL SOLUTION:\n"+" solutionID="+finalSolution.getConclusion().getID()+" valuePromoted="+finalSolution.getPromotesValue()+" timesUsed="+finalSolution.getTimesUsed()+" proposingAgents: "+agentIDs2+" frequency:"+frequentPositions.get(finalSolution.getConclusion().getID())+" votes:"+votedPositions.get(finalSolution.getConclusion().getID()));
					int votesReceived = votedPositions.get(finalSolution.getConclusion().getID());
					float dialogueTime=(System.currentTimeMillis()-dialogueInitTime)/1000f;
					logger.info("Dialogue elapsed time: "+dialogueTime+" seconds");
					//logger.info("POSSIBLE SOLUTIONS:");
					String posSolsString="POSSIBLE SOLUTIONS:\n";
					Iterator<Solution> iterPossibleSols=possibleSolutions.values().iterator();
					while(iterPossibleSols.hasNext()){
						Solution sol=iterPossibleSols.next();
						positions=positionsPerSolution.get(sol.getConclusion().getID());
						iterPositionsSol=positions.iterator();
						agentIDs2="";
						while(iterPositionsSol.hasNext()){
							Position pos=iterPositionsSol.next();
							agentIDs2+=pos.getAgentID()+" ";
						}
						posSolsString+=" solutionID="+sol.getConclusion().getID()+" valuePromoted="+sol.getPromotesValue()+" timesUsed="+sol.getTimesUsed()+" proposingAgents: "+agentIDs2+" frequency:"+frequentPositions.get(sol.getConclusion().getID())+" votes:"+votedPositions.get(sol.getConclusion().getID())+"\n";
						//logger.info(" solutionID="+sol.getConclusion().getID()+" valuePromoted="+sol.getPromotesValue()+" timesUsed="+sol.getTimesUsed()+" proposingAgents: "+agentIDs+" frequency:"+frequentPositions.get(sol.getConclusion().getID())+" votes:"+votedPositions.get(sol.getConclusion().getID()));
					}
					posSolsString+="+++++++++++++++++\n";
					//logger.info("+++++++++++++++++\n");
					logger.info(posSolsString);
					
					
				}
				
				
				//send the solution to all agents, if solution is correct
				iterAgents=socialEntities.iterator();
				while(iterAgents.hasNext()){
					SocialEntity socialEntitie=iterAgents.next();
					sendMessage(socialEntitie.getName(), "SOLUTION", currentDialogueID, finalSolution);
				}
				
				
				if(finalSolution.getConclusion().getID()!=-1 && finalSolution.getConclusion().getID() != 0){
					//logger.info("\n\n********\n"+this.getName()+": "+"message received: "+"from: "+msg.getSender().toString()+" solutionID="+sol.getConclusion().getId()+" valuePromoted="+sol.getPromotesValue()+" timesUsed="+sol.getTimesUsed()+"\n\n********\n");
					
					DomainCase ticketSent=domCases.get(nTicket-1);
					Solution originalSolution=null;
					ArrayList<Solution> ticketSolutions=ticketSent.getSolutions();
					
					int solID=(int)finalSolution.getConclusion().getID();
					Iterator<Solution> iterSolutions=ticketSolutions.iterator();
					while(iterSolutions.hasNext()){
						Solution solu=iterSolutions.next();
						if((int)solu.getConclusion().getID()==solID){
							originalSolution=solu;
							break;
						}
					}
					String orgSolutions = "";
					Iterator<Solution> ite = ticketSolutions.iterator();
					while (ite.hasNext())
						orgSolutions += ite.next().getConclusion().getID() + " ";
										
					if(originalSolution==null)
						totalErrors++;
					
					solvedProblems++;
					
					logger.info("\n\n********\n"+this.getName()+": "+ "PARTITION " + casesPerAgent + " ITERATION " + iteration +" proposedSol="+finalSolution.getConclusion().getID()+" originalSolutions="+orgSolutions+" totalErrors="+totalErrors+"\n\n********\n");
				}
				else{//NO SOLUTION
					totalErrors++;
					logger.info("\n\n********\n"+this.getName()+": "+ "PARTITION " + casesPerAgent + " ITERATION " + iteration +" NO OBTAINED SOLUTION"+" totalErrors="+totalErrors+" solvedProblems="+solvedProblems+"\n\n********\n");
				}
				
				break;
			
			}
		
		}
			
			
		try {
			Thread.sleep(500);//wait 0.5 seconds to give time to agents to update its CBs
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
			
			
			
//			if(nTicket<domCases.size()){
//				//create a new instance of the ticket without the solutions
//				auxTicket=domCases.get(nTicket);
//				ticketToSend=new DomainCase(auxTicket.getProblem(),new ArrayList<Solution>(),auxTicket.getJustification());
//				//logger.info(this.getName()+": "+"proposing new ticket "+ticketToSend.getTicketID());
//				iterAgents=socialEntities.iterator();
//				while(iterAgents.hasNext()){
//					SocialEntity socialEntity=iterAgents.next();
//					sendMessage(socialEntity.getName(), "OPENDIALOGUE", "", ticketToSend);
//				}
//				nTicket++;
//			}
//			else{//finish the agent!
				
				// Compute the number of Locutions used per agent
				int totalLocutions = 0;
//				int totalAgents = 0;
				for (ArgCAgent agent : agents){
					totalLocutions+= agent.getMyUsedLocutions();
//					totalAgents++;
				}
//				totalLocutions = totalLocutions / totalAgents;
				
				// Compute the number of Domain-Cases per agent
				Vector<Integer> domCasesSize = new Vector<Integer>();
								
				for (int i = 0; i<agents.size(); i++){
					domCasesSize.add(agents.get(i).getNumberDomainCases());
				}
				
				float domCases = 0;
				for (int j=0; j<domCasesSize.size(); j++){
					domCases += domCasesSize.get(j);
				}
				domCases = domCases/domCasesSize.size();
				
				
				// Compute the number of Argument-Cases per agent
				Vector<Integer> argCasesSize = new Vector<Integer>();
								
				for (int i = 0; i<agents.size(); i++){
					argCasesSize.add(agents.get(i).getNumberArgumentCases());
				}
				
				float argCases = 0;
				for (int j=0; j<argCasesSize.size(); j++){
					argCases += argCasesSize.get(j);
				}
				argCases = argCases/argCasesSize.size();
				
				int agreementReached = agents.get(0).getAgreement();				
				int acceptanceFrequency = agents.get(0).getAcceptanceFrequency();
				
				float frequency = 0f;
				if (agents.size() != 0)
					frequency = (float) acceptanceFrequency / (float) agents.size();
				
				float votesPercentage = 0f;
				int selectedAsBest = 0;
				
				Position positionArgLearn = agents.get(0).getLastPositionBeforeNull();
				
				if (positionArgLearn != null && 
						positionArgLearn.getSolution().getConclusion().getID() == finalSolution.getConclusion().getID())
					selectedAsBest = 1;
				
//				if (selectedAsBest == 1 && agents.get(0).getVotes() != 0)
					votesPercentage = (float) agents.get(0).getVotes() / (float) agents.size();
				
				int currPosAccepted = agents.get(0).getAccepted();
				
			
				int iniArgCases = agents.get(0).getUsedArgCases();
				float usedArgCases = 0f;
				for (int ag = 0; ag < agents.size(); ag++){
					usedArgCases+= (float) agents.get(ag).getUsedArgCases();
				}
				if (usedArgCases != 0)
					usedArgCases/= (float) agents.size();
				
				iterAgents=socialEntities.iterator();
				while(iterAgents.hasNext()){
					SocialEntity socialEntity=iterAgents.next();
					sendMessage(socialEntity.getName(), "DIE", currentDialogueID, null);
				}
				sendMessage(commitmentStoreID, "DIE", currentDialogueID, null);
				
				
//				try {
//					Thread.sleep(1500);//wait 1.5 seconds to give time to agents to finish
//					Thread.sleep(5000);//wait to give time to agents to finish
//					boolean someoneAlive = true;
//					int ag = 0;
//					while (someoneAlive || ag < agents.size()){
//						someoneAlive = false;
//						ag = 0;
//						for (int i = 0; i < agents.size(); i++){
//							ag++;
//							try{
//								ArgLearnAgent agent = agents.get(i);
//								if (agent.isAlive()){
//									someoneAlive = true;
//									System.out.println(agent.getName() + " still alive");
//									break;
//								}
//							}catch(Exception e){
//								System.out.println("Agent " + ag + " knocked out");
//							}
//						}

//					}

//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
				
				boolean someoneAlive = true;
				while(someoneAlive){
					Thread.sleep(3000);
					someoneAlive = false;
					try{
						for (int i = 0; i< agents.size(); i++){
							FileReader fstream = new FileReader(agents.get(i).getName());
							BufferedReader file = new BufferedReader(fstream);
							String line=file.readLine();
							file.close();
							if(line==null || line.equals("")){
								someoneAlive = true;
								break;
							}
						}

					}catch (Exception e){//Catch exception if any
						System.err.println("Error reading file: " + e.getMessage());
						e.printStackTrace();
					}
				}
				
				//print solution into a file
				try{
					
					logger.info(this.getName()+": "+"finishing and writing the file");
					
					// Create file 
					FileWriter fstream = new FileWriter(resultFileName,true);
					BufferedWriter outFile = new BufferedWriter(fstream);
//					outFile.write("solutionID= "+sol.getConclusion().getId()+" valuePromoted= "+sol.getPromotesValue()+" timesUsed= "+sol.getTimesUsed());
//					outFile.write(casesPerAgent+" "+ totalErrors + " " + solvedProblems + " " + argCases + " " + totalLocutions + " " + agreementReached + " " + frequency);
					outFile.write(domCases+" "+ totalErrors + " " + solvedProblems +
							" " + argCases + " " + totalLocutions + " " + agreementReached + 
							" " + frequency+ " " + currPosAccepted + " " + votesPercentage + 
							" " + selectedAsBest + " " + iniArgCases + " " + usedArgCases);
					outFile.newLine();
					//Close the output stream
					outFile.close();
				}catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}
				
				
				try{
					
					// Create file 
					FileWriter fstream = new FileWriter(finishFileName,false);
					BufferedWriter outFile = new BufferedWriter(fstream);
					outFile.write("test2 finished");
					outFile.newLine();
					//Close the output stream
					outFile.close();
				}catch (Exception e){//Catch exception if any
					System.err.println("Error: " + e.getMessage());
				}
				
//			}
			
		
		}catch(Exception e){
			e.printStackTrace();
			logger.error("Exception Tester " + e.getMessage());
		}
	}
	
//	private ArrayList<Ticket> getTestTickets(int nTickets){
//		
//		OWLDomainParser owlDomainParser= new OWLDomainParser();
//		Vector<Ticket> allTickets=new Vector<Ticket>();
//		try {
//			allTickets = owlDomainParser.parseDomainOntology("Helpdesk-Full.owl");
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//		ArrayList<Ticket> tickets=new ArrayList<Ticket>();
//		
//		
//		for(int i=0;i<nTickets;i++){
//			int index=(int)(Math.random()*allTickets.size());
//			Ticket ticket=allTickets.get(index);
//			tickets.add(ticket);
//		}
//		
//		return tickets;
//	}
	
	
	
//	public void onMessage(ACLMessage msg){
//		//logger.info("\n\n"+this.getName()+": "+msg.toString()+"\n\n");
//		Solution sol=(Solution) msg.getContentObject();
//		logger.info("\n\n********\n"+this.getName()+": "+"message received: "+"from: "+msg.getSender().toString()+" solutionID="+sol.getConclusion().getId()+" valuePromoted="+sol.getPromotesValue()+" timesUsed="+sol.getTimesUsed()+"\n\n********\n");
//	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<Position> getAllPositions(String dialogueID){
		sendMessage(commitmentStoreID, "GETALLPOSITIONS", dialogueID, null);
		ArrayList<String> locutions=new ArrayList<String>();
		locutions.add("GETALLPOSITIONS");
		ArrayList<Position> positions;
		try{
			ACLMessage msg=receiveACLMessage();
			positions=(ArrayList<Position>) msg.getContentObject();
		}catch(Exception e){
			positions=new ArrayList<Position>();
			logger.error(this.getName()+": Exception in getAllPositions\n"+e.toString());
			e.printStackTrace();
		}
			
		return positions;
	}
	
	private void sendMessage(String agentID, String locution, String conversationID, Serializable contentObject){
		
		ACLMessage msg = new ACLMessage();
		msg.setSender(getAid());
		msg.setReceiver(new AgentID(agentID));
		msg.setHeader("locution", locution);
		msg.setConversationId(conversationID);
		msg.setPerformative(ACLMessage.INFORM);
		
		try {
			msg.setContentObject(contentObject);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//System.out.println(this.getName()+": "+"message to send: "+"to: "+msg.getReceiver().toString()+" dialogueID: "+msg.getConversationId()+" locution: "+msg.getContent());
		logger.info(this.getName()+": "+"message to send to: "+msg.getReceiver().toString()+" dialogueID: "+msg.getConversationId()+" locution: "+msg.getHeaderValue("locution"));
		send(msg);
		
	}
	
}
