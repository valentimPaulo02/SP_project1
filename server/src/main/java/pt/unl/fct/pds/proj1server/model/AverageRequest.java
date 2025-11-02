package pt.unl.fct.pds.proj1server.model;

public class AverageRequest {
    private String filterAttribute;
    private String filterValue;

    public AverageRequest() {
    }

    public AverageRequest(
            String filterAttribute,
            String filterValue) {
        this.filterAttribute = filterAttribute;
        this.filterValue = filterValue;
    }

    public String getFilterAttribute() {return filterAttribute;}
    public String getFilterValue() {return filterValue;}

    public void setFilterAttribute(String filterAttribute) {this.filterAttribute = filterAttribute;}
    public void setFilterValue(String filterValue) {this.filterValue = filterValue;}
}
