package pt.unl.fct.pds.proj1server.DTO;

public class Row {

    private long id; 
    private int age; 
    private String gender; 
    private String postal; 
    private String diagnosis;

    public Row(long id, int age, String gender, String postal, String diagnosis) {
        this.id = id; 
        this.age = age; 
        this.gender = gender; 
        this.postal = postal; 
        this.diagnosis = diagnosis;
    }

    public long getId() {return id;}
    public int getAge() {return age;}
    public String getGender() {return gender;}
    public String getPostal() {return postal;}
    public String getDiagnosis() {return diagnosis;}

    public void setId(long id) {this.id = id;}
    public void setAge(int age) {this.age = age;}
    public void setGender(String gender) {this.gender = gender;}
    public void setPostal(String postal) {this.postal = postal;}
    public void setDiagnosis(String diagnosis) {this.diagnosis = diagnosis;}

    @Override
    public String toString() {
        return String.format("{id=%d, age=%d, gender=%s, postal=%s, diagnosis=%s}",
                id, age, gender, postal, diagnosis);
    }
}