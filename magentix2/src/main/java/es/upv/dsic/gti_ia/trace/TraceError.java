package es.upv.dsic.gti_ia.trace;

/**
 * 
 * @author L Burdalo (lburdalo@dsic.upv.es)
 *
 * Specification of error messages related to the trace support
 *
 */

public class TraceError {

	public static final int TRACE_ERROR = 0;
	public static final int ENTITY_NOT_FOUND = 1;
	public static final int PROVIDER_NOT_FOUND = 2;
	public static final int SERVICE_NOT_FOUND = 3;
	public static final int SUBSCRIPTION_NOT_FOUND = 4;
	public static final int ENTITY_DUPLICATE = 5;
	public static final int SERVICE_DUPLICATE = 6;
	public static final int SUBSCRIPTION_DUPLICATE = 7;
	public static final int BAD_ENTITY = 8;
	public static final int BAD_SERVICE = 9;
	public static final int PUBLISH_ERROR = 10;
	public static final int UNPUBLISH_ERROR = 11;
	public static final int SUBSCRIPTION_ERROR = 12;
	public static final int UNSUBSCRIPTION_ERROR = 13;
	public static final int AUTHORIZATION_ERROR = 14;
	
	public static final int MAX_TRACE_ERROR = 15;
	
	public static final TraceError[] TraceErrors = new TraceError[]{
		new TraceError("TRACE_ERROR", "Undefined trace error"),
		new TraceError("ENTITY_NOT_FOUND", "Tracing entity not present in the system"),
		new TraceError("PROVIDER_NOT_FOUND", "Provider is not offering the tracing service"),
		new TraceError("SERVICE_NOT_FOUND", "Tracing service not offered by any entity in the system"),
		new TraceError("SUBSCRIPTION_NOT_FOUND", "Subscription to the tracing service not found"),
		new TraceError("ENTITY_DUPLICATE", "Tracing entity already present in the system"),
		new TraceError("SERVICE_DUPLICATE", "Tracing service already offered by the tracing entity"),
		new TraceError("SUBSCRIPTION_DUPLICATE", "Subscription already exists"),
		new TraceError("BAD_ENTITY", "Tracing entity not correct"),
		new TraceError("BAD_SERVICE", "Tracing service not correct"),
		new TraceError("PUBLISH_ERROR", "Impossible to publish the tracing service"),
		new TraceError("UNPUBLISH_ERROR", "Impossible to unpublish the tracing service"),
		new TraceError("SUBSCRIPTION_ERROR", "Impossible to subscribe to the tracing service"),
		new TraceError("UNSUBSCRIPTION_ERROR", "Impossible to unsubscribe from tracing service"),
		new TraceError("AUTHORIZATION_ERROR", "Unauthorized to do so")
	};
	
	private String name;
	private String description;
	
	public TraceError (String name, String description){
		this.name=name;
		this.description=description;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getDescription(){
		return this.description;
	}
}
