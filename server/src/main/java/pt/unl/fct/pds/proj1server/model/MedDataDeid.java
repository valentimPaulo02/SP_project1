package pt.unl.fct.pds.proj1server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "med_data_deid")
public class MedDataDeid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int age;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String postalCode;
    @Column(nullable = false)
    private String diagnosis;

    public MedDataDeid() {
    }

    public MedDataDeid(Long id,
                    int age,
                    String gender,
                    String postalCode,
                    String diagnosis) {
        this.id = id;
        this.age = age;
        this.gender = gender;
        this.postalCode = postalCode;
        this.diagnosis = diagnosis;
    }
    
    public Long getId() {return id;}
    public int getAge() {return age;}
    public String getGender() {return gender;}
    public String getPostalCode() {return postalCode;}
    public String getDiagnosis() {return diagnosis;}
    
    public void setId(Long id) {this.id = id;}
    public void setAge(int age) {this.age = age;}
    public void setGender(String gender) {this.gender = gender;}
    public void setPostalCode(String postalCode) {this.postalCode = postalCode;}
    public void setDiagnosis(String Diagnosis) {this.diagnosis = diagnosis;}
}
