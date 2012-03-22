package mWaterWeb.webInterface;

import java.util.Iterator;

import mWaterWeb.webInterface.WebComm.AccreditationOutJSONObject;
import mWaterWeb.webInterface.WebComm.InJsonObject;
import mWaterWeb.webInterface.WebComm.JoinTableOutJSONObject;
import mWaterWeb.webInterface.WebComm.NewTableOutJSONObject;
import mWaterWeb.webInterface.WebComm.AuctionOutJSONObject;
import mWaterWeb.webInterface.WebComm.OutJsonObject;
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
			AgentID initiatorAg, String convPurpose) {
		super(jasonID, internalID, initiatorAg);
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
				if (purposeDescription.compareTo("accreditation")==0)
				{
					ListTerm invitations = (ListTermImpl) result.getTerm(0);
					ListTerm ttables = (ListTermImpl) result.getTerm(1);
					Term wm = result.getTerm(2);
					conversationResult = comm.new AccreditationOutJSONObject(invitations.size(),ttables.size(),wm.toString());
					conversationResult.purpose = purposeDescription;
					((AccreditationOutJSONObject)conversationResult).content.registeredUser = true;

					//Filling invitations
					TradingTable invittoinsert;
					for (Term i: invitations){ //i must have the format: recruited_participant(...)
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)i).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)i).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)i).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)i).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)i).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)i).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)i).getTerms(),u);
						invittoinsert = comm.new TradingTable(tableid, wmarket, conf_id, opDate, cond, opUser,protType);
						((AccreditationOutJSONObject)conversationResult).content.invitations[cont] = invittoinsert;
						cont++;
					}

					//Filling tables
					cont = 0;
					TradingTable tabletoinsert;
					for (Term t: ttables){ //i must have the format: recruited_participant(...)
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)t).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)t).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)t).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)t).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)t).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)t).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)t).getTerms(),u);
						tabletoinsert = comm.new TradingTable(tableid, wmarket, conf_id, opDate, cond, opUser,protType);
						((AccreditationOutJSONObject)conversationResult).content.tradingTables[cont] = tabletoinsert;

						cont++;
					}

				}else
					//result(trading_table(...),[water_right(...),water_right(...),...]."buyer")
					if (purposeDescription.compareTo("tradinghall")==0 )
					{
						Literal tt = (LiteralImpl) result.getTerm(0);
						ListTerm wrights = (ListTermImpl) result.getTerm(1);
						StringTermImpl rol = (StringTermImpl) result.getTerm(2);
						conversationResult = comm.new JoinTableOutJSONObject(wrights.size(),rol.getString());
						conversationResult.purpose = purposeDescription;

						TradingTable tradingtable;
						String tableid = mWaterBB.searchFieldValueInTermList("trading_table","id",((Literal)tt).getTerms(),u);
						String wmarket = mWaterBB.searchFieldValueInTermList("trading_table","wmarket",((Literal)tt).getTerms(),u);
						String conf_id = mWaterBB.searchFieldValueInTermList("trading_table","configuration_id",((Literal)tt).getTerms(),u);
						String opDate = mWaterBB.searchFieldValueInTermList("trading_table","opening_date",((Literal)tt).getTerms(),u);
						String cond = mWaterBB.searchFieldValueInTermList("trading_table","conditions",((Literal)tt).getTerms(),u);
						String opUser = mWaterBB.searchFieldValueInTermList("trading_table","opening_user",((Literal)tt).getTerms(),u);
						String protType = mWaterBB.searchFieldValueInTermList("trading_table","protocol_type",((Literal)tt).getTerms(),u);
						tradingtable = comm.new TradingTable(tableid, wmarket, conf_id, opDate, cond, opUser,protType);
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

