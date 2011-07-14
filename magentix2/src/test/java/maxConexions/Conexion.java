package maxConexions;

import org.apache.qpid.transport.Connection;

public class Conexion {
	public void execute(){
		Connection con = new Connection();
        con.connect("localhost", 5672, "test", "guest", "guest",false);
	}
}
