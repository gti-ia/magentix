package _thomas_Example;

import java.util.ArrayList;

import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.SFProxy;

public class AgentBasic extends QueueAgent {

	public AgentBasic(AgentID aid) throws Exception {

		super(aid);

	}
	
	OMSProxy serviciosOMS = new OMSProxy();
	SFProxy sfservice = new SFProxy();
	
	
	public void execute(){
		ArrayList<String> resultados = new ArrayList<String>();
		ArrayList<AgentID> agentes = new ArrayList<AgentID>();
		String result = this.serviciosOMS.AcquireRole(this, "member", "virtual");
		//System.out.println("Register in the organization: "+this.getAid().name+" "+result);
		
		
		result = serviciosOMS.AcquireRole(this,"customer", "travelagency");
		
	//	System.out.println(this.getAid().name+ " conencted with rol customer: "
		//		+ result);
		
		
		resultados = sfservice.searchService(this, "SearchCheapHotel");
		System.out.println("Soy el "+this.getAid().name+"  y el searchServices nos ha devuelto: "+ resultados.get(0));


		
	}
}
