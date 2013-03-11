package mWaterWeb.webInterface;

import java.util.Iterator;

import mWaterWeb.webInterface.WebComm.AccreditationOutJSONObject;
import mWaterWeb.webInterface.WebComm.FinishedRoundOutJSONObject;
import mWaterWeb.webInterface.WebComm.GetWROutJSONObject;
import mWaterWeb.webInterface.WebComm.InJsonObject;
import mWaterWeb.webInterface.WebComm.JoinTableOutJSONObject;
import mWaterWeb.webInterface.WebComm.NewTableOutJSONObject;
import mWaterWeb.webInterface.WebComm.AuctionOutJSONObject;
import mWaterWeb.webInterface.WebComm.OutJsonObject;
import mWaterWeb.webInterface.WebComm.RoundStartedOutJSONObject;
import mWaterWeb.webInterface.WebComm.TradingAgreement;
import mWaterWeb.webInterface.WebComm.TradingTable;
import mWaterWeb.webInterface.WebComm.WaterRight;
import mWaterWeb.bdConnection.mWaterBB;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.ListTermImpl;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.StringTermImpl;
import jason.asSyntax.Term;
import es.upv.dsic.gti_ia.jason.conversationsFactory.Conversation;
import es.upv.dsic.gti_ia.core.AgentID;


public class WebRequestConversation extends Conversation {
	private WebComm comm = new WebComm();
	public InJsonObject conversationRequest ;
	public OutJsonObject conversationResult ;
	public String conversationPropose;
	
	public WebRequestConversation(String jasonID, String internalID,
			AgentID initiatorAg, String convPurpose, String factName) {
		super(jasonID, internalID, initiatorAg,factName);
		conversationPropose = convPurpose;
		conversationRequest = new WebComm().new InJsonObject();
		
	}
	
	/**
	 * Fills the field conversationResult with the result of the accreditation 
	 * conversation. It is executed when conversationPropose has the value "accreditation"
	 * @param purposeDescription Description of the purpose of the conversation result
	 * @param result Result of the conversation in {@code Literal} format. Example: 
	 * result([recruited_participant(...),recruited_participant(...),...],[trading_table(...),trading_table(...),...]) 
	 */
	public void fillAccreditationResult(String purposeDescription, Literal result){
		if (result.getTerms().size()>=0){
			try{

				Unifier u = new Unifier();
				int cont = 0;
				//result([recruited_participant(...),recruited_participant(...),...],[trading_table(...),trading_table(...),...]) 
				//round(NewUser,LasR)
				
				if (purposeDescription.compareTo("roundstarted")==0)
				{ //result(Started,LastRound,Date)
					StringTermImpl started = (StringTermImpl) result.getTerm(0);
					StringTermImpl lastround = (StringTermImpl) result.getTerm(1);
					ListTermImpl date = (ListTermImpl) result.getTerm(2);
					conversationResult = comm.new RoundStartedOutJSONObject("","","");
					conversationResult.purpose = purposeDescription;
					((RoundStartedOutJSONObject)conversationResult).started = started.getString();
					((RoundStartedOutJSONObject)conversationResult).lastround = lastround.getString();
					((RoundStartedOutJSONObject)conversationResult).date = date.toString();
				}else
				if (purposeDescription.compareTo("finishedround")==0)
				{ 
					StringTermImpl newusr = (StringTermImpl) result.getTerm(0);
					StringTermImpl lastround = (StringTermImpl) result.getTerm(1);
					conversationResult = comm.new FinishedRoundOutJSONObject("","");
					conversationResult.purpose = purposeDescription;
					((FinishedRoundOutJSONObject)conversationResult).newuser = newusr.getString();
					((FinishedRoundOutJSONObject)conversationResult).lastround = lastround.getString();
				}else
				if (purposeDescription.compareTo("WRList")==0)
				{ 
				
					ListTerm wrights_list = (ListTermImpl) result.getTerm(0);
					conversationResult = comm.new GetWROutJSONObject(wrights_list.size(),"");
					conversationResult.purpose = purposeDescription;
					WaterRight wr ;
					for (Term i: wrights_list){
						/*water_right(owner(Own),id(ID),authorized_extraction_flow(AuthFlow),authorization_date(AuthDate),
							authorized(Auth),type_of_water(WaterType),initial_date_for_extraction(IniDateExtract),
							final_date_for_extraction(EndDayExtract),aggregation_right(AggregRight),season_unit(SeasonUnit),season(Season),
							general_water_right(GralWR))*/
						String owner = mWaterBB.searchFieldValueInTermList("water_right","owner",((Literal)i).getTerms(),u);
						String id = mWaterBB.searchFieldValueInTermList("water_right","id",((Literal)i).getTerms(),u);
						String authorized_extraction_flow = mWaterBB.searchFieldValueInTermList("water_right","authorized_extraction_flow",((Literal)i).getTerms(),u);
						String authorization_date = mWaterBB.searchFieldValueInTermList("water_right","authorization_date",((Literal)i).getTerms(),u);
						String type_of_water = mWaterBB.searchFieldValueInTermList("water_right","type_of_water",((Literal)i).getTerms(),u);
						String initial_date_for_extraction = mWaterBB.searchFieldValueInTermList("water_right","initial_date_for_extraction",((Literal)i).getTerms(),u);
						String final_date_for_extraction = mWaterBB.searchFieldValueInTermList("water_right","final_date_for_extraction",((Literal)i).getTerms(),u);
						wr = comm.new WaterRight(id,owner,authorized_extraction_flow,authorization_date,type_of_water,initial_date_for_extraction,final_date_for_extraction);
						((GetWROutJSONObject)conversationResult).content.water_rights[cont] = wr;
						cont++;
					}
				
				}else
				if (purposeDescription.compareTo("accreditation")==0)
				{// Result = result(Invitations,TTSelling,TTBuying,WMarketID);
					ListTerm invitations = (ListTermImpl) result.getTerm(0);
					ListTerm ttselling = (ListTermImpl) result.getTerm(1);
					ListTerm ttbuying = (ListTermImpl) result.getTerm(2);
					Term wm = result.getTerm(3);
					conversationResult = comm.new AccreditationOutJSONObject(invitations.size(),ttselling.size(),ttbuying.size(),wm.toString());
					conversationResult.purpose = purposeDescription;
					((AccreditationOutJSONObject)conversationResult).content.registeredUser = true;

					//Filling invitations
					cont = 0;
					TradingTable invittoinsert;
					for (Term i: invitations){ //i must have the format: recruited_participant(...)
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)i).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)i).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)i).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)i).getTerms(),u);
						String clDate = mWaterBB.searchFieldValueInTermList("trading_table","closing_date",((Literal)i).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)i).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)i).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)i).getTerms(),u);
						invittoinsert = comm.new TradingTable(tableid, wmarket, conf_id, clDate, opDate, cond, opUser,protType);
						((AccreditationOutJSONObject)conversationResult).content.invitations[cont] = invittoinsert;
						cont++;
					}

					//Filling tables selling
					cont = 0;
					TradingTable tabletoinsert;
					for (Term ts: ttselling){ //i must have the format: recruited_participant(...)
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)ts).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)ts).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)ts).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)ts).getTerms(),u);
						String clDate = mWaterBB.searchFieldValueInTermList("trading_table","closing_date",((Literal)ts).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)ts).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)ts).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)ts).getTerms(),u);
						tabletoinsert = comm.new TradingTable(tableid, wmarket, conf_id, clDate, opDate, cond, opUser,protType);
						((AccreditationOutJSONObject)conversationResult).content.tablesselling[cont] = tabletoinsert;

						cont++;
					}
					
					//Filling tables buying
					cont = 0;
					for (Term tb: ttbuying){ //i must have the format: recruited_participant(...)
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)tb).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)tb).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)tb).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)tb).getTerms(),u);
						String clDate = mWaterBB.searchFieldValueInTermList("trading_table","closing_date",((Literal)tb).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)tb).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)tb).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)tb).getTerms(),u);
						tabletoinsert = comm.new TradingTable(tableid, wmarket, conf_id, clDate, opDate, cond, opUser,protType);
						((AccreditationOutJSONObject)conversationResult).content.ttablesbuying[cont] = tabletoinsert;

						cont++;
						
					}

				}else
					//result(trading_table(...),[water_right(...),water_right(...),...]."buyer")
					if (purposeDescription.compareTo("tradinghall")==0 )
					{
						Literal tt = (LiteralImpl) result.getTerm(0);
						ListTerm wrights = (ListTermImpl) result.getTerm(1);
						StringTermImpl rol = (StringTermImpl) result.getTerm(2);
						StringTermImpl joined = (StringTermImpl) result.getTerm(3);
						conversationResult = comm.new JoinTableOutJSONObject(wrights.size(),rol.getString(),joined.toString());
						conversationResult.purpose = purposeDescription;

						TradingTable tradingtable;
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)tt).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)tt).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)tt).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)tt).getTerms(),u);
						String clDate = mWaterBB.searchFieldValueInTermList("trading_table","closing_date",((Literal)tt).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)tt).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)tt).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)tt).getTerms(),u);
						tradingtable = comm.new TradingTable(tableid, wmarket, conf_id, clDate, opDate, cond, opUser,protType);
						((JoinTableOutJSONObject)conversationResult).content.tt=tradingtable;
						
						//Filling water rights
						cont = 0;
						WaterRight wrtoinsert;
						for (Term wr: wrights){ //i must have the format: recruited_participant(...)
							String id = mWaterBB.searchFieldValueInTermList("water_right","id",((Literal)wr).getTerms(),u);
							String owner = mWaterBB.searchFieldValueInTermList("water_right","owner",((Literal)wr).getTerms(),u);
							String autextflow = mWaterBB.searchFieldValueInTermList("water_right","authorized_extraction_flow",((Literal)wr).getTerms(),u);
							String autdate = mWaterBB.searchFieldValueInTermList("water_right","authorization_date",((Literal)wr).getTerms(),u);
							String typewater = mWaterBB.searchFieldValueInTermList("water_right","type_of_water",((Literal)wr).getTerms(),u);
							String inidateex = mWaterBB.searchFieldValueInTermList("water_right","initial_date_for_extraction",((Literal)wr).getTerms(),u);
							String finaldateex = mWaterBB.searchFieldValueInTermList("water_right","final_date_for_extraction",((Literal)wr).getTerms(),u);
							wrtoinsert = comm.new WaterRight(id, owner,  autextflow, autdate,typewater, inidateex,finaldateex);
							((JoinTableOutJSONObject)conversationResult).content.water_rights[cont] = wrtoinsert;

							cont++;
						}

					}else
						//result(true)
						if (purposeDescription.compareTo("newtable")==0 )
						{
							conversationResult = comm.new NewTableOutJSONObject();
							conversationResult.purpose = purposeDescription;
							String succed = result.getTerm(0).toString();
							if (succed.compareTo("true")==0)
								((NewTableOutJSONObject)conversationResult).content = true;
							else ((NewTableOutJSONObject)conversationResult).content = false;
						}else
							//result(ExternConvID,Bid,"false",Participants,WRight,Agreement)
							if (purposeDescription.compareTo("auctionstate")==0 ||
									(purposeDescription.compareTo("bidup")==0 ))
							{
								//System.out.println("in auctionstate. literal: "+result.toString()+" purposeDescription "+purposeDescription);
								
								StringTermImpl convid = (StringTermImpl)result.getTerm(0);
								NumberTermImpl bid = ((NumberTermImpl)result.getTerm(1));
								StringTermImpl finished = (StringTermImpl)result.getTerm(2);
								ListTermImpl participants = (ListTermImpl) result.getTerm(3);
								Term wright = result.getTerm(4);
								Term agr = result.getTerm(5);
								StringTermImpl winner = (StringTermImpl)result.getTerm(6);
								Term winnerbidterm = result.getTerm(7);
								String winnerbid="";
								if (winnerbidterm.isString()){
									//System.out.println("+++++++++++++++ el winnerbidterm es string.. tomandolo....");
									winnerbid = ((StringTermImpl)winnerbidterm).getString();
									//System.out.println("+++++++++++++++ el winnerbidterm es string.. ya tomado....");
									}
								else winnerbid = winnerbidterm.toString();
								//System.out.println("+++++++++++++++ el winnerbidterm es string.. ya tomado: "+winnerbid);
								TradingAgreement tagr ;
								if ((agr.toString().trim().compareTo("")==0)||(agr.toString().trim().compareTo("\"\"")==0))
									{tagr = null; }
								else  //there is an agreement
									{
										Literal lwr = (Literal) ((Literal)agr).getTerm(0) ; WaterRight wr ;
										
										String id = mWaterBB.searchFieldValueInTermList("water_right","id",lwr.getTerms(),u);
										String owner = mWaterBB.searchFieldValueInTermList("water_right","owner",lwr.getTerms(),u);
										String autextflow = mWaterBB.searchFieldValueInTermList("water_right","authorized_extraction_flow",lwr.getTerms(),u);
										String autdate = mWaterBB.searchFieldValueInTermList("water_right","authorization_date",lwr.getTerms(),u);
										String typewater = mWaterBB.searchFieldValueInTermList("water_right","type_of_water",lwr.getTerms(),u);
										String inidateex = mWaterBB.searchFieldValueInTermList("water_right","initial_date_for_extraction",lwr.getTerms(),u);
										String finaldateex = mWaterBB.searchFieldValueInTermList("water_right","final_date_for_extraction",lwr.getTerms(),u);
										wr = comm.new WaterRight(id, owner,  autextflow, autdate,typewater, inidateex,finaldateex);
										
										tagr = comm.new TradingAgreement(wr,((Literal)agr).getTerm(1).toString(),((Literal)agr).getTerm(2).toString(),
												((Literal)agr).getTerm(3).toString(),((Literal)agr).getTerm(4).toString()) ;
									}
								boolean boolfinished;
								//System.out.println("++++++++++++++++++++ antes de finished");
								if (finished.getString().compareTo("true")==0)
									boolfinished = true;
								else boolfinished = false;
								//System.out.println("+++++++++++++++++++++ finished: "+finished.toString()+" boolfinished "+boolfinished);
								String[] part = new String[participants.size()];
								int k = 0;
								Iterator<Term> it = participants.iterator();
								while (it.hasNext())
									{
										part[k] = it.next().toString();
										k++;
									}
								conversationResult = comm.new AuctionOutJSONObject(participants.size());
								conversationResult.purpose = purposeDescription;
								((AuctionOutJSONObject)conversationResult).content.conversation_id = convid.getString();
								((AuctionOutJSONObject)conversationResult).content.bid = bid.toString();
								((AuctionOutJSONObject)conversationResult).content.finished = boolfinished;
								((AuctionOutJSONObject)conversationResult).content.participants = part;
								((AuctionOutJSONObject)conversationResult).content.water_right_id = wright.toString();
								((AuctionOutJSONObject)conversationResult).content.agreement = tagr;
								((AuctionOutJSONObject)conversationResult).content.winner = winner.getString() ;
								((AuctionOutJSONObject)conversationResult).content.winnerbid = winnerbid;

							}
			}catch (Exception e){
				System.out.println("--------- Invalid literal format!");
			}
		}
	}



}

