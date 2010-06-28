package MMS_Example;



import es.upv.dsic.gti_ia.architecture.Monitor;
import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

public class AgentClient extends BaseAgent {



		private Monitor m;


		public AgentClient(AgentID aid) throws Exception {
			super(aid);
		}

		public void init() {

			m = new Monitor();
			// this.createCertificate(null);
		}

		public void execute() {
			logger.info("Executing, I'm " + getName());

			while (true) {

			}
		
		}


		public void exit() {
			this.m.advise();
		}
	}
