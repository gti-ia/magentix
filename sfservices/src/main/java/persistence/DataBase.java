package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import configuration.Configuration;

/**
 * Clase encargada de conexion y consultas a base de datos.
 * @author Chuidiang
 *
 */
public class DataBase
{
    /** La conexion con la base de datos */
    public Connection connection = null;

    /** Se establece la conexion con la base de datos */
    public DataBase()
    {
        if (connection != null)
            return;

        try
        {
            // Se registra el Driver de MySQL
        	String driverName = "com.mysql.jdbc.Driver"; // MySQL MM JDBC driver
	        Class.forName(driverName);
            // Se obtiene una conexi�n con la base de datos. Hay que
            // cambiar el usuario "usuario" y la clave "" por las
            // adecuadas a la base de datos que estemos usando.
	        Configuration c= new Configuration();
        	String serverName = c.serverName;
	        String mydatabase = c.databaseName;
	        String url = "jdbc:mysql://" + serverName +  "/" + mydatabase; // a JDBC url
	        String username = c.userName;
	        String password = c.password;
	        connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e)
        {
        		System.out.println("Error al crear conexion a la BD");
            e.printStackTrace();
        }
    }

    /**
     * Realiza la consulta de personas en la tabla y devuelve el ResultSet
     * correspondiente.
     * @return El resultado de la consulta
     */
}