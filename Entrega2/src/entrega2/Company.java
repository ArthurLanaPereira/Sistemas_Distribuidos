package entrega2;

	public class Company {
    private String email;
    private String password;
    private String name;
    private String industry;
	private String description;
    
	public Company(String name, String email, String password, String industry, String description) {
		this.name = name;
		this.password = password;
        this.email = email;
        this.industry = industry;
        this.description = description;
       
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}

