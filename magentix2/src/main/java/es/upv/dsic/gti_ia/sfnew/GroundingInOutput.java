package es.upv.dsic.gti_ia.sfnew;

public class GroundingInOutput {

	private String owlsParameter;
	private String wsdlMessagePart;
	private String xsltTransformationString;
	
	public GroundingInOutput(String owlsParameter, String wsdlMessagePart,
			String xsltTransformationString) {
		super();
		this.owlsParameter = owlsParameter;
		this.wsdlMessagePart = wsdlMessagePart;
		this.xsltTransformationString = xsltTransformationString;
	}

	public String getOwlsParameter() {
		return owlsParameter;
	}

	public void setOwlsParameter(String owlsParameter) {
		this.owlsParameter = owlsParameter;
	}

	public String getWsdlMessagePart() {
		return wsdlMessagePart;
	}

	public void setWsdlMessagePart(String wsdlMessagePart) {
		this.wsdlMessagePart = wsdlMessagePart;
	}

	public String getXsltTransformationString() {
		return xsltTransformationString;
	}

	public void setXsltTransformationString(String xsltTransformationString) {
		this.xsltTransformationString = xsltTransformationString;
	}
	
	
	
}
