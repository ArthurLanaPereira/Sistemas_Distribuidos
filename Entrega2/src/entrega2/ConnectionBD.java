package entrega2;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionBD {
	
	public static Connection conectaBD() {
		
		Connection connect = null;
		
		try {
			
			String url = "jdbc:mysql://localhost:3306/banco_distribuidos?user=root&password=sistemasdistribuidos";
			connect = (Connection) DriverManager.getConnection(url);
			
		} catch (SQLException e) {
			e.printStackTrace();
            return null;
		}
		
		return connect;
		
	}
}
