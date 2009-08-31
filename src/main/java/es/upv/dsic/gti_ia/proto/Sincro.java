package es.upv.dsic.gti_ia.proto;

public class Sincro {

	
	public synchronized void esperar()
	{
		
        try{	
			this.wait();

     	}catch(InterruptedException e){
				
				System.out.println("ERROR: " + e.getMessage());
			}
	}
	
	public synchronized void esperar(long timeout)
	{
		
        try{	
			this.wait(timeout);

     	}catch(InterruptedException e){
				
				System.out.println("ERROR: " + e.getMessage());
			}
	}
	
	public synchronized void dar()
	{
		try{
		notifyAll();
		}catch(Exception e)
		{
			System.out.println("Mensaje :"+ e.getMessage());
		}
		
	}
}
