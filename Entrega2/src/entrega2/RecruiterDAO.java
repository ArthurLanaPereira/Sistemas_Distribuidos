package entrega2;

import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RecruiterDAO {
	
	public void testeCadastro(Company recruiter) {
		
		String sql = "INSERT INTO RECRUITER (NAME, PASSWORD, EMAIL, INDUSTRY, DESCRIPTION) VALUES (?, ?, ?, ?, ?)";
		
		PreparedStatement ps = null;
		
		try {
			ps = ConnectionBD.conectaBD().prepareStatement(sql);
			ps.setString(1, recruiter.getName());
			ps.setString(2, recruiter.getPassword());
			ps.setString(3, recruiter.getEmail());
			ps.setString(4, recruiter.getIndustry());
			ps.setString(5, recruiter.getDescription());
			ps.execute();
			ps.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
