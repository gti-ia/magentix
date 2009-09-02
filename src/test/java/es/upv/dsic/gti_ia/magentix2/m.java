package es.upv.dsic.gti_ia.magentix2;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;

public class m {
	public static void main(String args[]) throws UnknownHostException
	{
		Calendar c1 = Calendar.getInstance();
		String dia = Integer.toString(c1.get(Calendar.DATE));
		String mes = Integer.toString(c1.get(Calendar.MONTH));
		String annio = Integer.toString(c1.get(Calendar.YEAR));
		int hora =c1.get(Calendar.HOUR_OF_DAY);
		int minutos = c1.get(Calendar.MINUTE);
		int segundos = c1.get(Calendar.SECOND);
		
		System.out.println(dia+" "+"  "+mes+" "+annio+"   "+hora+"  "+minutos+"  "+ segundos);
	
		
		
		InetAddress Address = InetAddress.getLocalHost(); 
		System.out.println(Address); 
		Address = InetAddress.getByName("buraki"); 
		System.out.println(Address); 
	}

}
