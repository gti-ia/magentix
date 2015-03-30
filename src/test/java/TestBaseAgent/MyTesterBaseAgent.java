package TestBaseAgent;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.BaseAgent;

/**
 * Created by svalero on 30/03/15.
 */
public class MyTesterBaseAgent extends BaseAgent{
    private boolean finalize = false;

    public boolean isFinalize() {
        return finalize;
    }

    public void setFinalize(boolean finalize) {
        this.finalize = finalize;
    }



    public MyTesterBaseAgent(AgentID aid) throws Exception {
        super(aid);
    }
    public void execute() {

        /**
         * This agent finalizes when setFinalize(True) is called
         */
        while (!finalize) {

        }
    }
    public boolean idRegistered (AgentID aid){
        return existAgent(aid);

    }


}
