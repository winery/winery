package org.eclipse.winery.repository.resources.apiData;


public class InstanceStateApiData {
    public String state;

    public InstanceStateApiData(){
    }
    public InstanceStateApiData(String state){
        this.state = state;
    }
    public String toString() {
        return "state: "+ this.state;
    }
}
