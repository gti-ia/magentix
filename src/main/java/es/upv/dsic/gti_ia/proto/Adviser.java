

/**
 * La clase Adviser permite controlar el estado de un agente (ejecutando, suspendido).
 * 
 * @author  Joan Bellver Faus, GTI-IA, DSIC, UPV
 * @version 2009.9.07
 */

package es.upv.dsic.gti_ia.proto;

public class Adviser {

	
    /**
     * Suspende el proceso
     */
	public synchronized void esperar()
	{
		
        try{	
			this.wait();

     	}catch(InterruptedException e){
				
				System.out.println("ERROR: " + e.getMessage());
			}
	}
	
	  /**
     * Suspende el proceso un tiempo
     * 
     * @param  timeout tiempo de espera.
     */
	public synchronized void esperar(long timeout)
	{
		
        try{	
			this.wait(timeout);

     	}catch(InterruptedException e){
				
				System.out.println("ERROR: " + e.getMessage());
			}
	}
	
    /**
     * Reactiva el proceso
     */
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
