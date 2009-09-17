package EjemploRequest;

public class Llamadas {

	
	synchronized void esperar()
	{
		
        try{
 

         	
				this.wait();
         	

     	}catch(InterruptedException e){
				
				System.out.println("ERROR: " + e.getMessage());
			}
	}
	
	synchronized void dar()
	{
		try{
		notifyAll();
		}catch(Exception e)
		{
			System.out.println("Mensaje :"+ e.getMessage());
		}
		
	}
}
