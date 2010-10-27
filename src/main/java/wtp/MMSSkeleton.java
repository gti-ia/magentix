
/**
 * MMSSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4  Built on : Apr 26, 2008 (06:24:30 EDT)
 */
    package wtp;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;

import es.upv.dsic.gti_ia.MMService.AgentCertificate;
/**
 *  MMSSkeleton java skeleton for the axisService
 */
public class MMSSkeleton{

	AgentCertificate agc = new AgentCertificate();
	/**
	 * Auto generated method signature
	 * 
	 * @param mMS
	 */

	public wtp.MMSResponse MMS
	(
			wtp.MMS mMS
	)
	{
		InputStream inputDataHandler;
		try {
			inputDataHandler = mMS.getPublicKey().getInputStream();

			byte[] arrayByte = IOUtils.toByteArray(inputDataHandler);

			byte[] certificate = agc.newCertificate(mMS.getAgentName(), arrayByte);

			wtp.MMSResponse response = new wtp.MMSResponse();


			DataSource dataSource = new ByteArrayDataSource(certificate
					, "application/octet-stream");
			DataHandler dataHandler = new DataHandler(dataSource);

			response.setCertificate(dataHandler);

			return response;

		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}
	}

}
