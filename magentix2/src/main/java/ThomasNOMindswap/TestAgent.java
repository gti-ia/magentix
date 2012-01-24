package ThomasNOMindswap;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


import org.apache.log4j.xml.DOMConfigurator;



import es.upv.dsic.gti_ia.architecture.FIPARequestResponder;
import es.upv.dsic.gti_ia.architecture.MessageTemplate;
import es.upv.dsic.gti_ia.architecture.QueueAgent;
import es.upv.dsic.gti_ia.architecture.FIPANames.InteractionProtocol;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.organization.OMSProxy;
import es.upv.dsic.gti_ia.organization.Oracle;
import es.upv.dsic.gti_ia.organization.ProcessDescription;
import es.upv.dsic.gti_ia.organization.SFProxy;
import es.upv.dsic.gti_ia.organization.ServiceClient;

public class TestAgent extends QueueAgent {

    private OMSProxy omsProxy = new OMSProxy(this);

    private SFProxy sfProxy = new SFProxy(this);

    private ArrayList<String> results = new ArrayList<String>();

    private Oracle oracle;

    ProcessDescription processDescription = new ProcessDescription(
	    "http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl",
	    "SearchCheapHotel");

    public TestAgent(AgentID aid) throws Exception {

	super(aid);

    }

    

    public void execute() {

	DOMConfigurator.configure("configuration/loggin.xml");
	logger.info("Executing, I'm " + getName());
	

//	String token_process="http://localhost:8080/SearchCheapHotel/owl/owls/SearchCheapHotelProcess.owl";
//	//extract process' local name
//	int nameLength=token_process.split("\\.")[0].split("/").length;
//	String process_localName= token_process.split("\\.")[0].split("/")[nameLength-1];
//	System.out.println(process_localName);
//	List<String> params= new ArrayList<String>();
//	params.add("5");
//	params.add("Spain");
//	params.add("Valencia");
	
	String token_process="http://localhost:8080/omsservices/OMSservices/owl/owls/AcquireRoleProcess.owl";
	//extract process' local name
	int nameLength=token_process.split("\\.")[0].split("/").length;
	String process_localName= "AcquireRoleProcess";
	System.out.println(process_localName);
	List<String> params= new ArrayList<String>();
	
	
	params.add("virtual");
	params.add("member");
	params.add(this.getAid().getLocalName());
	
	ServiceClient serviceClient = new ServiceClient();
	System.out.println("[Provider] "+"before invoke");
    ArrayList<String> results = serviceClient.invoke(token_process, params);
    System.out.println("[Provider] "+"after invoke");
	String resultStr=process_localName+ "=" + "{";
	for(int i=0;i<results.size();i++){
		resultStr+=token_process+"#"+results.get(i);
		if(i!=results.size()-1){
			resultStr+=", ";
		}
		else{
			resultStr+="}";
		}
	}
	
	System.out.println("[Provider] "+resultStr);

	// when we do not have to create more roles we await the expiration
	// of the other roles

	es.upv.dsic.gti_ia.architecture.Monitor mon = new es.upv.dsic.gti_ia.architecture.Monitor();
	mon.waiting();

    }

}

