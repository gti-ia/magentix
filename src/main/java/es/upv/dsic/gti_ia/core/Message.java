package es.upv.dsic.gti_ia.core;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import com.bubble.serializer.SerializationContext;
import com.bubble.serializer.DeserializationContext;

public class Message {
	public String body;
	public ByteBuffer buffer;
	Map<String, String> messageHeaders = new HashMap<String, String>();

	public void setHeader(String headerName, String value) {
		messageHeaders.put(headerName, value);
	}

	public String getHeader(String headerName) {
		return messageHeaders.get(headerName);
	}

	public String toString() {
		return "Body: " + body + " Headers: " + messageHeaders.toString();
	}

	public void setByteBuffer(Object obj, int size) {
		SerializationContext context = new SerializationContext();
		buffer = ByteBuffer.allocate(size);
		context.serialize(obj, buffer);
		buffer.flip();
	}

	public Object getByteBuffer() {
		DeserializationContext context = new DeserializationContext();
		Object obj = (Object) context.deserialize(buffer);
		return obj;
	}
}
