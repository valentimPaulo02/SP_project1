package pt.unl.fct.pds.proj1server.DTO;

import java.util.List;

public class LinkageSummary {

    private int medUniques;         
    private int workUniques;        
    private int totalLinked;        
    private int correctMatches;     
    private double pctCorrect;      
    private double pctReidentified; 
    private List<LinkedRow> samples;

    public LinkageSummary(int medUniques, int workUniques, int totalLinked,
                              int correctMatches, double pctCorrect, double pctReidentified,
                              java.util.List<LinkedRow> samples) {
        this.medUniques = medUniques;
        this.workUniques = workUniques;
        this.totalLinked = totalLinked;
        this.correctMatches = correctMatches;
        this.pctCorrect = pctCorrect;
        this.pctReidentified = pctReidentified;
        this.samples = samples;
    }

    public int getMedUniques() {return medUniques;}
    public int getWorkUniques() {return workUniques;}
    public int getTotalLinked() {return totalLinked;}
    public int getCorrectMatches() {return correctMatches;}
    public double getPctCorrect() {return pctCorrect;}
    public double getPctReidentified() {return pctReidentified;}
    public java.util.List<LinkedRow> getSamples() {return samples;}

    public void setMedUniques(int medUniques) {this.medUniques = medUniques;}
    public void setWorkUniques(int workUniques) {this.workUniques = workUniques;}
    public void setTotalLinked(int totalLinked) {this.totalLinked = totalLinked;}
    public void setCorrectMatches(int correctMatches) {this.correctMatches = correctMatches;}
    public void setPctCorrect(double pctCorrect) {this.pctCorrect = pctCorrect;}
    public void setPctReidentified(double pctReidentified) {this.pctReidentified = pctReidentified;}
    public void setSamples(List<LinkedRow> samples) {this.samples = samples;}
}
