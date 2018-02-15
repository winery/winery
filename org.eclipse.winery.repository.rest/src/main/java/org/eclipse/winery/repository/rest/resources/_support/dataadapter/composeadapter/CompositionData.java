package org.eclipse.winery.repository.rest.resources._support.dataadapter.composeadapter;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "CompositionData")
public class CompositionData {
    
    @XmlElement(name = "cspath")
    private List<String> cspath;

    @XmlElement(name = "targetid")
    private String targetid;
    
    public List<String> getCspath() {
        return cspath;
    }

    public void setCspath(List<String> cspath) {
        this.cspath = cspath;
    }
    
    public String getTargetid() {
        return targetid;
    }

    public void setTargetid(String targetid) {
        this.targetid = targetid;
    }
}
