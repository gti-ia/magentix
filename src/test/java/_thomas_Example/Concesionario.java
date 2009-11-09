package _thomas_Example;



import es.upv.dsic.gti_ia.organization.Configuration;



import es.upv.dsic.gti_ia.architecture.*;
import es.upv.dsic.gti_ia.organization.*;




import es.upv.dsic.gti_ia.core.*;
import java.util.ArrayList;

public class Concesionario extends QueueAgent {

	public Concesionario(AgentID aid) throws Exception{

		super(aid);
		

	}

	public void execute()
    {
		

        OMSProxy serviciosOMS  = new OMSProxy();
        
        
        Configuration c = Configuration.getConfiguration();
        
        
        
        //**************** AcquireRole ****************************
        System.out.println("Entro en la organizacion.");
        String resultado = serviciosOMS.AcquireRole(this,"member", "virtual");
        	
        System.out.println("AcquireRole: "+ resultado+ " Agent: "+ this.getName());
        
        SFAgentDescription descripcionsf = new SFAgentDescription(c.getTHServiceDesciptionLocation(),c.getTHServiceDesciptionLocation());
        SFProxy serviciosf = new SFProxy();
   
        
       descripcionsf.setServiceGoal("SearchCheapHotel");
  
        System.out.println("Cambio de rol a rol provider.");
        resultado = serviciosOMS.AcquireRole(this,"provider", "travelagency");
        	
        //**********************************************************
        
        //****************** RegisterUnit *************************
        serviciosOMS.RegisterUnit(this, "news", "congregation", "receivenews", "virtual");
        
        //*********************************************************

		//****************** RegisterRole ***************
		 
		//serviciosOMS.RegisterRole(agent, RegisterRoleInputRoleID, UnitID, Accessibility, Position, Visibility, Inheritance);
		serviciosOMS.RegisterRole(this, "broadcaster","news" , "external", "member", "public", "member");
		//*********************************************
   
		 
        //************ RegisterProfile *****************
        
        serviciosf.registerProfile(this, descripcionsf);
        System.out.println("El register Profile nos ha devuelto: "+ descripcionsf.getID());
        
        //************************************************
        
        //************ RegisterProcess *****************
        
        serviciosf.registerProcess(this,descripcionsf);
        
        //************************************************
        
        
        System.out.println("El register Process nos ha devuelto: "+ descripcionsf.getImplementationID());
        
        //************ DeregisterProfile *****************
        /*
        
        //************************************************
        

        //************ GetProcess *****************
        
        serviciosf.getProfile(this, descripcionsf);
        
        
        //************************************************

        
    

        //************ ModifyProcess *****************
        
        
        //************************************************

        //************ ModifyProfile *****************
        
        
        //************************************************




	
        
        
        //************ RemoveProvider *****************
        
        
        //************************************************
        
        */
        
        serviciosOMS.AcquireRole(this,"customer", "travelagency");
        //************ SearchService *****************
        ArrayList<String> valores = new ArrayList<String>();
        
        valores = serviciosf.searchService(this, "SearchCheapHotel");
        
        System.out.println("Valores devueltos: "+ valores.get(0));
        
        
        
        //************************************************


        
        //************ GetProfile *****************
        
        ArrayList<AgentID> agentes = new ArrayList<AgentID>();
        
        agentes = serviciosf.getProcess(this,valores.get(0));

        for(AgentID agent : agentes)
        System.out.println("Agentes que tiene ese servicio: "+ agent.protocol);
        
        
        
        
        //************************************************
        
        /*
        
       
        
         
        
        
        

        
        //el provider va a registar el servicio SearchCheapHotel
        
        
        System.out.println("Voy a registrar un servicio");
      
       
        
        
    
        
 
        
   
        if (serviciosf.registerProfile(this, descripcionsf))
        	System.out.println("Profile del servicio registrado correctamente.");
        else
        	System.out.println("Profile del servicio no se ha registrado correctamente.");
        
        
        

        
        
        if (serviciosf.registerProcess(this, descripcionsf))
        {
        	System.out.println("Se ha registrado el proces");
        }
        else
        {
        	System.out.println("No se ha podido registrar el process");
        }
        
      
        ArrayList<String> res;
        /*
        
        res = serviciosOMS.InformAgentRole(this,this.getAllName());
        
        for (String s: res)
        {
        	System.out.println("AgentRole "+s);
        	
        }
        
        res = serviciosOMS.InformMembers(this,"member", "virtual");
        
       

        for (String s: res)
        {
        	System.out.println("InformMembers   "+s);
        	
        }
      
        res.clear();
          
        /*
       res = serviciosOMS.InformRoleNorms(this,"member");
        

        for (String s: res)
        {
        	System.out.println("InformRoleNorms"+s);
        	
        }
        
       
       /* 
      res = serviciosOMS.InformRoleProfiles(this,"virtual");
        

        for (String s: res)
        {
        	System.out.println("InformRoleProfiles"+s);
        	
        }
 */
      /*  
      res = serviciosOMS.InformUnit(this,"member");
        

        for (String s: res)
        {
        	System.out.println("InformUnit "+s);
        	
        }

 
      res = serviciosOMS.InformUnitRoles(this,"travelagency");
        

        for (String s: res)
        {
        	System.out.println("InformUnitRoles "+s);
        	
        }
   
      res.clear();
      
      
      int cantidad = serviciosOMS.QuantityMembers(this, "member", "virtual");
        

    
      System.out.println("QuantityMembers "+cantidad);
        
    
        
    
	 System.out.println("Deregistro el servicio");
	 
	 if (serviciosf.DeregisterProfile(this,"SearchCheapHotel"))
	 {
		 System.out.println("Se ha eliminado el servicio");
	 }
	 else
	 {
		 System.out.println("El servicio no se ha podido eliminar");
	 }
	 
	 */
			
        /*
        System.out.println("Modifico el profile del servicio");
   	 
    //  descripcionsf.setNewServiceGoal("SearchCheapHotelProfile");  
        
   	 if (serviciosf.ModifyProfile(this,descripcionsf))
   	 {
   		 System.out.println("Se ha modificado el servicio");
   	 }
   	 else
   	 {
   		 System.out.println("El servicio no se ha podido modificar");
   	 } 
        
   	 
     System.out.println("Modificio el process del servicio");
   	 
     //  descripcionsf.setNewServiceGoal("SearchCheapHotelProfile");  
         
    	 if (serviciosf.ModifyProcess(this,this.getArraySFAgentDescription().get(0)))
    	 {
    		 System.out.println("Se ha modificado el process del servicio");
    	 }
    	 else
    	 {
    		 System.out.println("El servicio no se ha podido modificar el process del servicio");
    	 } 
   	 
   	 //provar de registra otro servicio
   	 

   	 
    //Eliminar el profile
    	
    	 if (serviciosf.DeregisterProfile(this,this.getArraySFAgentDescription().get(0)))
    	     System.out.println("Deregistrado correctamente");
    	 
    	 if (serviciosf.removeProvider(this, this.getArraySFAgentDescription().get(0)))
    		 System.out.println("Proveedor borrado correctamente");
   	 
   	 for(SFAgentDescription sf : this.getArraySFAgentDescription())
   	 {
   		 System.out.println("SF: ID: "+ sf.getID()+ " serviceGoal "+ sf.getServiceGoal()+ " Implementation ID "+ sf.getImplementationID());
   	 }
   	 */
   	 
   	 
   	 
    System.out.printf("Autos %s: A la espera de clientes...\n", this.getName());
    


    // Se crea una plantilla que filtre los mensajes a recibir.
    MessageTemplate template = new MessageTemplate(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

    // Añadimos los comportamientos ante mensajes recibidos
    CrearOferta oferta = new CrearOferta(this, template);
    
    do
    {
    	System.out.println("Espero");
    	oferta.action();
    	
    }while(true);
    
}

	// Hacemos una simulación para que pueda dar que existe o no coche (sobre un
	// 80% probab).
	private boolean existeCoche() {
		return (Math.random() * 100 > 20);
	}

	// Calculamos un precio para el coche aleatoriamente (estará entre 8000 y
	// 30000).
	private int obtenerPrecio() {
		return (int) (Math.random() * 22000) + 8000;
	}

	// Simula fallos en el cálculo de precios.
	private boolean devolverPrecio() {
		return (int) (Math.random() * 10) > 1;
	}

	private class CrearOferta extends FIPAContractNetResponder {
		public CrearOferta(QueueAgent agente, MessageTemplate plantilla) {
			super(agente, plantilla);
		}

		protected ACLMessage prepareResponse(ACLMessage cfp)
				throws NotUnderstoodException, RefuseException {
			System.out
					.printf("Autos %s: Peticion de oferta recibida de %s.\n",
							getName(), cfp.getSender()
									.getLocalName());

			// Comprobamos si existen ofertas disponibles
			if (Concesionario.this.existeCoche()) {
				// Proporcionamos la información necesaria
				int precio = Concesionario.this.obtenerPrecio();
				System.out.printf("Autos %s: Preparando oferta (%d euros).\n",
						getName(), precio);

				// Se crea el mensaje
				ACLMessage oferta = cfp.createReply();
				oferta.setPerformative(ACLMessage.PROPOSE);
				oferta.setContent(String.valueOf(precio));
				return oferta;
			} else {
				// Si no hay ofertas disponibles rechazamos el propose
				System.out.printf(
						"Autos %s: No tenemos ofertas disponibles.\n",
						getName());
				throw new RefuseException("Fallo en la evaluación.");
			}
		}

		protected ACLMessage prepareResultNotification(ACLMessage cfp,
				ACLMessage propose, ACLMessage accept) throws FailureException {
			// Hemos recibido una aceptación de nuestra oferta, enviamos el
			// albarán
			System.out.printf("Autos %s: Hay una posible oferta.\n",
					getName());

			if (devolverPrecio()) {
				System.out.printf("Autos %s: Enviando contrato de compra.\n",
						getName());
			

				ACLMessage inform = accept.createReply();

				inform.setPerformative(ACLMessage.INFORM);
				return inform;
			} else {
				System.out.printf(
						"Autos %s: Vaya!, ha fallado al enviar el contrato.\n",
						getName());
				throw new FailureException("Error al enviar contrato.");
			}
		}

		protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose,
				ACLMessage reject) {
			// Nuestra oferta por el coche ha sido rechazada
			System.out.printf(
					"Autos %s: Oferta rechazada por su excesivo precio.\n",
					getName());
		}
	}

}
