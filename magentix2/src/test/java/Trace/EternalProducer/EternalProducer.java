package EternalProducer;

import org.apache.qpid.transport.*;
import java.util.Map;
import java.util.HashMap;


public class EternalProducer
{
    /**
     * Sends 10 messages with a single property and 10 messages
     * with 2 properties to a headers exchange.  
     */
    public static void main(String[] args)
    {      
            // Create connection
            org.apache.qpid.transport.Connection con = new org.apache.qpid.transport.Connection();
            con.connect("localhost", 5672, "test", "guest", "guest",false);

            // Create session
            org.apache.qpid.transport.Session session = con.createSession(0);
            DeliveryProperties deliveryProps = new DeliveryProperties();

            // set message headers
            MessageProperties messageProperties = new MessageProperties();
            Map<String, Object> messageHeaders = new HashMap<String, Object>();
            // set the message property
            //messageHeaders.put("h1", "v1");
            messageHeaders.put("event_type", "trace_test");
            messageProperties.setApplicationHeaders(messageHeaders);
            Header header = new Header(deliveryProps, messageProperties);

            int i = 0;
            while(true)
            {
//                session.messageTransfer("test.headers", MessageAcceptMode.EXPLICIT,MessageAcquireMode.PRE_ACQUIRED,
//                                        header,
//                                        "Message H1: " + i);
//                System.out.println("Sent Message H1: " + i);
                
                //session.messageTransfer("test.headers", MessageAcceptMode.EXPLICIT,MessageAcquireMode.PRE_ACQUIRED,
                session.messageTransfer("amq.match", MessageAcceptMode.EXPLICIT,MessageAcquireMode.PRE_ACQUIRED,
                        header,
                        "event_type = trace_test (" + i + ")");
                System.out.println("[ETERNAL PRODUCER]: Sent event_type: trace_test (" + i + ")");
                try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i++;
            }

//            // set message headers
//            messageProperties = new MessageProperties();
//            messageHeaders = new HashMap<String, Object>();
//            // set the message properties
//            messageHeaders.put("h1", "v1");
//            messageHeaders.put("h2", "v2");
//            messageProperties.setApplicationHeaders(messageHeaders);
//            header = new Header(deliveryProps, messageProperties);
//
//            for (int i=0; i<10; i++)
//            {
//                session.messageTransfer("test.headers", MessageAcceptMode.EXPLICIT,MessageAcquireMode.PRE_ACQUIRED,
//                                        header,
//                                        "Message H1 and H2: " + i);
//                try {
//					Thread.currentThread().sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }


//            session.messageTransfer("test.headers", MessageAcceptMode.EXPLICIT,MessageAcquireMode.PRE_ACQUIRED,
//                                              header,
//                                              "That's all, folks!" );
//
//            // confirm completion
//            session.sync();
//
//            //cleanup
//            session.close();
//            con.close();
    }

}
