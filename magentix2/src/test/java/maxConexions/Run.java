package maxConexions;

import java.util.ArrayList;

public class Run {	
	public static void main(String[] args){
		ArrayList<Conexion> conexion = new ArrayList<Conexion>();
		for(int i =0;i< 507;i++){
			conexion.add(new Conexion());
			conexion.get(i).execute();
			System.out.println("Conexion "+i);
		}
		while(true);
	}
}
