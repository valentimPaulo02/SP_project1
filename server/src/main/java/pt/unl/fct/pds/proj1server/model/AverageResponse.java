package pt.unl.fct.pds.proj1server.model;

public class AverageResponse {
    private String attribute;
    private double value;
    private double epsilonUsed;       
    private double remainingBudget;   
    private double sumSensitivity; 
    private double countSensitivity;

    public AverageResponse() {
    }

    public AverageResponse(
            String attribute,
            int value) {
        this.attribute = attribute;
        this.value = value;
    }

    public AverageResponse(String attribute, double value, Double epsilonUsed, Double remainingBudget, Double sumSensitivity, Double countSensitivity) {
        this.attribute = attribute;
        this.value = value;
        this.epsilonUsed = epsilonUsed;
        this.remainingBudget = remainingBudget;
        this.sumSensitivity = sumSensitivity;
        this.countSensitivity = countSensitivity;
    }

    public String getAttribute() {return attribute;}
    public Double getValue() {return value;}
    public Double getEpsilonUsed() {return epsilonUsed;} 
    public Double getRemainingBudget() {return remainingBudget;} 
    public Double getSumSensitivity() {return sumSensitivity;}
    public Double getCountSensitivity() {return countSensitivity;}
    
    public void setAttribute(String attribute) {this.attribute = attribute;}
    public void setValue(int value) {this.value = value;}
    public void setEpsilonUsed(Double epsilonUsed) {this.epsilonUsed = epsilonUsed;}
    public void setRemainingBudget(Double remainingBudget) {this.remainingBudget = remainingBudget;}
    public void setSumSensitivity(Double sumSensitivity) {this.sumSensitivity = sumSensitivity;}
    public void setCountSensitivity(Double countSensitivity) {this.countSensitivity = countSensitivity;}
}
