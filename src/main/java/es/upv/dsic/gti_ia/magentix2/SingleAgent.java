package es.upv.dsic.gti_ia.magentix2;

import org.apache.qpid.transport.Connection;
import org.apache.qpid.transport.MessageTransfer;
import org.apache.qpid.transport.Session;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class SingleAgent extends BaseAgent {

    LinkedBlockingQueue<MessageTransfer> internalQueue;

    public SingleAgent(String name, Connection connection) {
        super(name, connection);
        internalQueue = new LinkedBlockingQueue<MessageTransfer>();
    }

    public final MessageTransfer receive() {
        MessageTransfer xfr = new MessageTransfer();
        try {
            xfr = internalQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return xfr;
    }
    //Propuesta
    public final Message receiveMessage(){
    	Message msg = new Message();
    	MessageTransfer xfr = new MessageTransfer();
    	try {
            xfr = internalQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msg.body = xfr.getBodyString();
        msg.buffer = xfr.getBody();
        return msg;
    }

    public final void onMessage(Session ssn, MessageTransfer xfr) {
        internalQueue.add(xfr);
    }
}