package entrega2;

public class User {
    private String email;
    private String password;
    private String name;
    private String skill;
    private int experience;

	public User(String name, String email, String password, String skill, int experience) {
		this.name = name;
        this.email = email;
        this.password = password;
        this.skill = skill;
        this.experience = experience;
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
    
    public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}
	
	public int getExperience() {
		return experience;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}
}
