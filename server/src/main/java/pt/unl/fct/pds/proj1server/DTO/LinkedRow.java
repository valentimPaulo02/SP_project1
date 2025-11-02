package pt.unl.fct.pds.proj1server.DTO;

public class LinkedRow {

    private Long medDeidId;
    private String age;
    private String gender;
    private String postal;
    private String candidateName;
    private String trueName;
    private boolean correct;

    public LinkedRow(Long medDeidId, String age, String gender, String postal, String candidateName) {
        this.medDeidId = medDeidId;
        this.age = age; 
        this.gender = gender; 
        this.postal = postal;
        this.candidateName = candidateName;
    }
    public Long getMedDeidId() {return medDeidId;}
    public String getAge() {return age;}
    public String getGender() {return gender;}
    public String getPostal() {return postal;}
    public String getCandidateName() {return candidateName;}
    public String getTrueName() {return trueName;}
    public boolean getCorrect() {return correct;}

    public void setMedDeidId(Long medDeidId) {this.medDeidId = medDeidId;}
    public void setAge(String age) {this.age = age;}
    public void setGender(String gender) {this.gender = gender;}
    public void setPostal(String postal) {this.postal = postal;}
    public void setCandidateName(String candidateName) {this.candidateName = candidateName;}
    public void setTrueName(String trueName) {this.trueName = trueName;}
    public void setCorrect(boolean correct) {this.correct = correct;}
}