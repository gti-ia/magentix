 package persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DataBaseInterface {
	private DataBase db;
	public DataBaseInterface(){
		db=new DataBase();
	} 
	
	public String AddNewProfile(String urlprofile){
		System.out.println("Start AddNewProfile");
		String profile = urlprofile;
		StringTokenizer Tok = new StringTokenizer(profile);
		urlprofile = Tok.nextToken("#");
		String profilename = Tok.nextToken();

		
			System.out.println("URL profile: "+ urlprofile);
			System.out.println("profile name: "+ profilename);
	
		String serviceprofileid=null;
		Statement stmt =null;
		ResultSet rs=null;
		
		try {
			
			//Check if the profile is unique
			  stmt = db.connection.createStatement();
	          rs = stmt.executeQuery("select serviceprofileid from serviceprofileid where urlprofile='"+urlprofile+"'");
	         
	         System.out.println("Checking if the service profile already exists...");
	         
	         if(rs.next()) {
	        	 System.out.println("[Error] in AddNewProfile: the service profile already exists");
	         }
	         
	         else{
	        	 
	        	 System.out.println("AddNewProfile: "+urlprofile);
	        	 // Add new profile
	        	 
	        	 String sql = "insert into serviceprofileid (urlprofile,profilename) values ('"+urlprofile+"','"+profilename+"')";
	        	 
	        
	        	 // Execute the insert statement 
	        	 stmt.executeUpdate(sql);
	         
	        	 //Get the service profile id
	        	 rs = stmt.executeQuery("select serviceprofileid from serviceprofileid where urlprofile='"+urlprofile+"'");
	        	 rs.next();
	        	 Integer numserviceprofileid=rs.getInt("serviceprofileid");
	        	 serviceprofileid=numserviceprofileid.toString();
	        	 
	        	 System.out.println("The serviceprofileid is: "+ serviceprofileid);
	        	 
	        	 
	         }
	        
	 		
	         
		}catch(Exception e){
			e.printStackTrace(); 
			System.out.println("[Error] in AddNewProfile: there is a problem with the DB");
		}
		System.out.println("end AddNewProfile");
		return (serviceprofileid);
	}
	
	 
	public Boolean CheckServiceProfileID(String serviceprofileid){
		System.out.println("Start CheckServiceProfileID");
		Statement stmt = null;
		ResultSet rs = null;
		 
		try{
			
			stmt = db.connection.createStatement();
			rs = stmt.executeQuery("select serviceprofileid from serviceprofileid where serviceprofileid='"+serviceprofileid+"'");
   	 		if(rs.next()){
   
   	 			System.out.println("El serviceidprofile "+ serviceprofileid+ " existe");
   	 		}
   	 		else{
   	 			
   	 			System.out.println("The profile does not exist ");
   	 			return(false);
   	 		}
   	 		
   	 		return(true);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("End CheckServiceProfileID");
		
	 	return(true);
		
	}
	
public String GetServiceProfileURL(String serviceprofileid){
	System.out.println("Start GetServiceProfileURL");
		Statement stmt = null;
		ResultSet rs = null;
		String urlprofile = null;
		 
		try{
			
			stmt = db.connection.createStatement();
			rs = stmt.executeQuery("select urlprofile from serviceprofileid where serviceprofileid='"+serviceprofileid+"'");
   	 		if(rs.next()){
   	 			urlprofile = rs.getString("urlprofile");
   	 			System.out.println("urlprofile "+ urlprofile);
   	 		}
   	 		else{
   	 			
   	 			System.out.println("The serviceidprofile does not exit ");
   	 			
   	 		}
   	 		
   	 		
		
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("End GetServiceProfileURL");
		
	 	return(urlprofile);
		
	}
	

public String GetServiceProfileName(String serviceprofileid){
	System.out.println("Start GetServiceProfileName");
	
	Statement stmt = null;
	ResultSet rs = null;
	String profilename = null;
	 
	try{
		
		stmt = db.connection.createStatement();
		rs = stmt.executeQuery("select profilename from serviceprofileid where serviceprofileid='"+serviceprofileid+"'");
	 		if(rs.next()){
	 			profilename = rs.getString("profilename");
	 			System.out.println("profilename "+ profilename);
	 		}
	 		else{
	 			
	 			System.out.println("The serviceidprofile does not exit ");
	 			
	 		}
	 		
	 		
	
	}catch(Exception e){
		e.printStackTrace();
	}
	
	System.out.println("End GetServiceProfileName");
 	return(profilename);
	
}
	
	public String AddNewProcess(String urlprocess, String serviceprofileid, String agentid){
		System.out.println("Start AddNewProcess");
		String serviceprocessid=null;
		try {
			Statement stmt = db.connection.createStatement();
			ResultSet rs = null;
			
			StringTokenizer Tok = new StringTokenizer(urlprocess);
			urlprocess = Tok.nextToken("#");
			String processname = Tok.nextToken();

			
				System.out.println("URL process: "+ urlprocess);
				System.out.println("process name: "+ processname);
			//si existe el profile y no se ha registrado anteriormente el process, insertamos el process
			if(CheckServiceProfileID(serviceprofileid)&& !CheckServiceProcessID(serviceprofileid+"@"+agentid)){
				
				
					// Add new process
					String sql = "insert into serviceprocessid (serviceprocessid,urlprocess,serviceprofileid, processname)values('"+serviceprofileid+"@"+agentid+"','"+urlprocess+"','"+serviceprofileid+"','"+processname+"')";
					
	        
					// Execute the insert statement
					stmt.executeUpdate(sql);
	        
					//recuperamos el id del process
					rs = stmt.executeQuery("select * from serviceprocessid where urlprocess='"+urlprocess+"'");
					rs.next();
					serviceprocessid=rs.getString("serviceprocessid");
					
				
			}
			
			}catch(Exception e){
				e.printStackTrace(); 
			
		}
			System.out.println("Start GetServiceProfileName");
		return (serviceprocessid);
	}
	
public Boolean CheckServiceProcessID(String serviceprocessid){
	System.out.println("Start CheckServiceProcessID");
		Statement stmt = null;
		ResultSet rs = null;
		Boolean exist = false;
		try{
			
			stmt = db.connection.createStatement();
			rs = stmt.executeQuery("select serviceprocessid from serviceprocessid where serviceprocessid='"+serviceprocessid+"'");
   	 		if(rs.next()){
   
   	 			System.out.println("El serviceidprocess "+ serviceprocessid+ " existe"); 
   	 		    exist= true;
   	 		}
   	 		else{
   	 			System.out.println("No existe el process ");
   	 			exist = false;
   	 		}
   	 		
		
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("End CheckServiceProcessID");
		return(exist);
	 	
	}


public String GetServiceProcessFromProfile(String serviceprofileid){
	System.out.println("Start GetServiceProcessFromProfile");
	Statement stmt = null;
	ResultSet rs = null;
	String processlist = null;
	 
	try{
		if(CheckServiceProfileID(serviceprofileid))
		stmt = db.connection.createStatement();
		rs = stmt.executeQuery("select * from serviceprocessid where serviceprofileid='"+serviceprofileid+"'");
	 		if(rs.next()){
	 			processlist= processlist + " , " +rs.getString("serviceprocessid")+" "+rs.getString("urlprocess");
	 		}
	 		else{
	 		
	 			System.out.println("There are not process associated with the profile id "+serviceprofileid);
	 			
	 		}
	 	
	 		
	}catch(Exception e){
		e.printStackTrace();
	}
	
	System.out.println("End GetServiceProcessFromProfile");
 	return(processlist);
	
}

public String GetServiceProcessFromProcessID(String serviceprocessid){
	System.out.println("Start GetServiceProcessFromProcessID");
	Statement stmt = null;
	ResultSet rs = null;
	String serviceProcess = "";
	 
	try{
		if(CheckServiceProfileID(serviceprocessid))
		stmt = db.connection.createStatement();
		rs = stmt.executeQuery("select * from serviceprocessid where serviceprocessid='"+serviceprocessid+"'");
	 		if(rs.next()){
	 			String urlprocess = rs.getString("urlprocess");
	 			String processname= rs.getString("processname");
	 			serviceProcess= urlprocess+"#"+processname;
	 			
	 			System.out.println("The service Process is "+serviceProcess);
	 			
	 		}
	 		
	 		
	}catch(Exception e){
		e.printStackTrace();
	}
	System.out.println("End GetServiceProcessFromProcessID");
 	return(serviceProcess);
	
}
	
	public boolean DeleteProfile(String serviceprofileid){
		System.out.println("Start DeleteProfile");	
		Boolean flag=false;
		try {
			Statement stmt = db.connection.createStatement();
			
			if(GetServiceProcessFromProfile(serviceprofileid)!= null){
				flag=false;
			}else{
				stmt.executeUpdate("delete from serviceprofileid where serviceprofileid='"+serviceprofileid+"' ");
				
				flag=true;
			}
			
		}catch(Exception e){}
		System.out.println("End DeleteProfile");
		return(flag);
	}
	
	public boolean DeleteProcess(String serviceprocessid){
		System.out.println("Start DeleteProcess");
		try {
			Statement stmt = db.connection.createStatement();
			stmt.executeUpdate("delete from serviceprocessid where serviceprocessid='"+serviceprocessid+"' ");
			
		}catch(Exception e){
			return false;
		}
		System.out.println("End DeleteProcess");
		return true;	
	}
	
}