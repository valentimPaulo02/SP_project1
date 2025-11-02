package pt.unl.fct.pds.proj1server.model;

public class AverageResponse {
    private String attribute;
    private double averageAge;
    private double epsilonUsed;       
    private double remainingBudget;   
    private double sumSensitivity; 
    private double countSensitivity;

    public AverageResponse() {
    }

    public AverageResponse(
            String attribute,
            double averageAge) {
        this.attribute = attribute;
        this.averageAge = averageAge;
    }

    public AverageResponse(String attribute, double averageAge, Double epsilonUsed, Double remainingBudget, Double sumSensitivity, Double countSensitivity) {
        this.attribute = attribute;
        this.averageAge = averageAge;
        this.epsilonUsed = epsilonUsed;
        this.remainingBudget = remainingBudget;
        this.sumSensitivity = sumSensitivity;
        this.countSensitivity = countSensitivity;
    }

    public String getAttribute() {return attribute;}
    public Double getAverageAge() {return averageAge;}
    public Double getEpsilonUsed() {return epsilonUsed;} 
    public Double getRemainingBudget() {return remainingBudget;} 
    public Double getSumSensitivity() {return sumSensitivity;}
    public Double getCountSensitivity() {return countSensitivity;}
    
    public void setAttribute(String attribute) {this.attribute = attribute;}
    public void setAverageAge(int averageAge) {this.averageAge = averageAge;}
    public void setEpsilonUsed(Double epsilonUsed) {this.epsilonUsed = epsilonUsed;}
    public void setRemainingBudget(Double remainingBudget) {this.remainingBudget = remainingBudget;}
    public void setSumSensitivity(Double sumSensitivity) {this.sumSensitivity = sumSensitivity;}
    public void setCountSensitivity(Double countSensitivity) {this.countSensitivity = countSensitivity;}
}
