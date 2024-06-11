package entrega2;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class CandidateDAO {
	
	public void testeCadastro(User candidate) {
		
		String sql = "INSERT INTO CANDIDATE (NAME, PASSWORD, EMAIL) VALUES (?, ?, ?)";
		
		PreparedStatement ps = null;
		
		try {
			ps = ConnectionBD.conectaBD().prepareStatement(sql);
			ps.setString(1, candidate.getName());
			ps.setString(2, candidate.getPassword());
			ps.setString(3, candidate.getEmail());
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
