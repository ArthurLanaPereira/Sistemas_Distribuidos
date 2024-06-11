package entrega2;


public class TestaBanco {
	
	public static void main(String[] args) {
		
		Company u = new Company("Arthur Lana", "arthurlana@email.com", "1234", "Gostosuras", "Travessuras");
		/*u.setName("Arthur Lana Pereira");
		u.setEmail("arthurlanapereira@email.com");
		u.setPassword("1234");*/
		new RecruiterDAO().testeCadastro(u);
	}
	
}
