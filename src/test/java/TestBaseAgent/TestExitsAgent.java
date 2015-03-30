package TestBaseAgent;
import static org.junit.Assert.*;

import es.upv.dsic.gti_ia.core.AgentID;
import es.upv.dsic.gti_ia.core.AgentsConnection;
import es.upv.dsic.gti_ia.core.BaseAgent;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by svalero on 30/03/15.
 */
public class TestExitsAgent {
    Process qpid_broker;

    @Before
    public void setUp() throws Exception {

        //Starting qpid
        qpid_broker = qpidManager.UnixQpidManager.startQpid(Runtime.getRuntime(), qpid_broker);

        /**
         * Setting the configuration
         */
        DOMConfigurator.configure("configuration/loggin.xml");

        /**
         * Connecting to Qpid Broker
         */
        AgentsConnection.connect();


    }


    @After
    public void tearDown() throws Exception {
        AgentsConnection.disconnect();
        qpidManager.UnixQpidManager.stopQpid(qpid_broker);

     }
    /**
     * task #1116 - test 1
     */
    @Test
    public void testOneAgent (){
        try {
            AgentID aid= new AgentID("tester");
            MyTesterBaseAgent baseAgent = new MyTesterBaseAgent(aid);

            assertTrue(baseAgent.idRegistered(aid));
            baseAgent.setFinalize(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * task #1116 - test 3
     */
    @Test
    public void testAgentFinalize (){
        try {

            MyTesterBaseAgent baseAgent = new MyTesterBaseAgent(new AgentID());

            AgentID aid= new AgentID("tester");
            MyTesterBaseAgent baseAgent2 = new MyTesterBaseAgent(aid);

            assertTrue(baseAgent.idRegistered(aid));

            baseAgent2.setFinalize(true);
            wait(100); //giving time to baseAgent2 to finalize

            assertTrue(!baseAgent.idRegistered(aid));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * task #1116 - test 4
     */
    @Test
    public void testAgentNonExistent (){

        try {

            MyTesterBaseAgent baseAgent = new MyTesterBaseAgent(new AgentID("tester"));
            AgentID agentIDNoExists = new AgentID("noExists");
           assertTrue(!baseAgent.idRegistered(agentIDNoExists));
            baseAgent.setFinalize(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
