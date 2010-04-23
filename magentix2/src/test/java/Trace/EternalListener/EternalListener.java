package EternalListener;

import org.apache.qpid.transport.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EternalListener implements SessionListener
{
    private static CountDownLatch _countDownLatch = new CountDownLatch(1);

    public void opened(Session ssn) {}

    public void resumed(Session ssn) {}

    public void message(Session ssn, MessageTransfer xfr)
    {
        String body = xfr.getBodyString();
        System.out.println("[ETERNAL LISTENER]: Received: " + body);
//        if ( body.equals("That's all, folks!"))
//        {
//            System.out.println("Received final message");
//            _countDownLatch.countDown();
//        }
    }

    public void exception(Session ssn, SessionException exc)
    {
        exc.printStackTrace();
    }

    public void closed(Session ssn) {}

    /**
     * Receives messages from queue ANY and then ALL
     */
    public static void main(String[] args) throws InterruptedException
    {
        // Create connection
        Connection con = new Connection();
        con.connect("localhost", 5672, "test", "guest", "guest",false);

        // Create session
        Session session = con.createSession(0);
        // declare and bind queues
        String queueName = "event_queue";
        session.queueDeclare(queueName, null, null);
        
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-match", "any");
        arguments.put("event_type", "trace_test");
        session.exchangeBind(queueName, "amq.match", "useless", arguments);
        
        session.sync();
        System.out.println("Queue created...");
        
        System.out.println("Consuming messages for queue " + queueName);
        _countDownLatch = new CountDownLatch(1);
        // Create an instance of the listener
        EternalListener listener = new EternalListener();
        session.setSessionListener(listener);

        // create a subscription
        session.messageSubscribe(queueName,
                                 "listener_destination",
                                 MessageAcceptMode.NONE,
                                 MessageAcquireMode.PRE_ACQUIRED,
                                 null, 0, null);


        // issue credits
        session.messageFlow("listener_destination", MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
        //session.messageFlow("listener_destination", MessageCreditUnit.MESSAGE, 100);
        session.messageFlow("listener_destination", MessageCreditUnit.MESSAGE, 0xFFFFFFFF);
        // confirm completion
        session.sync();

        // wait to receive all the messages
        System.out.println("Waiting for messages from queue " + queueName);
        
        // we expect to receive all the messages  
        //Consume(session, "event_queue");
        // we expect to receive only messages that have both properties set.
        //Consume(session, "headers_queue_all");

        while(true){}
        
        //cleanup
//        session.close();
//        con.close();
    }

    private static void Consume(Session session, String queueName) throws InterruptedException
    {
        System.out.println("Consuming messages for queue " + queueName);
        _countDownLatch = new CountDownLatch(1);
        // Create an instance of the listener
        EternalListener listener = new EternalListener();
        session.setSessionListener(listener);

        // create a subscription
        session.messageSubscribe(queueName,
                                 "listener_destination",
                                 MessageAcceptMode.NONE,
                                 MessageAcquireMode.PRE_ACQUIRED,
                                 null, 0, null);


        // issue credits
        session.messageFlow("listener_destination", MessageCreditUnit.BYTE, Session.UNLIMITED_CREDIT);
        //session.messageFlow("listener_destination", MessageCreditUnit.MESSAGE, 100);
        session.messageFlow("listener_destination", MessageCreditUnit.MESSAGE, 0xFFFFFFFF);
        // confirm completion
        session.sync();

        // wait to receive all the messages
        System.out.println("Waiting for messages from queue " + queueName);

//        _countDownLatch.await(30, TimeUnit.SECONDS);
        
        while(true){}
        
//        System.out.println("Shutting down listener for " + queueName);
//        System.out.println("=========================================");
//        session.messageCancel("listener_destination");
    }

}
