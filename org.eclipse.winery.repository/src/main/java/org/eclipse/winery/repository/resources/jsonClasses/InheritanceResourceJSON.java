package org.eclipse.winery.repository.resources.jsonClasses;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.resources.AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class InheritanceResourceJSON {

	private String isAbstract;
	private String isFinal;
	private String derivedFrom;
	private List<QNameJson> availableSuperClasses;

	public InheritanceResourceJSON() {
	}

	public InheritanceResourceJSON(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
		this.isAbstract = res.getTBoolean("getAbstract");
		this.isFinal = res.getTBoolean("getFinal");
		this.derivedFrom = res.getDerivedFrom();
		this.createDerivedFromList(res);
	}

	private void createDerivedFromList(AbstractComponentInstanceResourceWithNameDerivedFromAbstractFinal res) {
		SortedSet<? extends TOSCAComponentId> allTOSCAcomponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(res.getId().getClass());
		allTOSCAcomponentIds.remove(res.getId());
		this.availableSuperClasses = new ArrayList<>();
		for (TOSCAComponentId id : allTOSCAcomponentIds) {
			QNameJson q = new QNameJson(id);
			this.availableSuperClasses.add(q);
		}
	}

	public String getIsAbstract() {
		return isAbstract;
	}

	public void setIsAbstract(String isAbstract) {
		this.isAbstract = isAbstract;
	}

	public String getIsFinal() {
		return isFinal;
	}

	public List<QNameJson> getAvailableSuperClasses() {
		return availableSuperClasses;
	}

	public void setAvailableSuperClasses(List<QNameJson> availableSuperClasses) {
		this.availableSuperClasses = availableSuperClasses;
	}

	public void setIsFinal(String isFinal) {
		this.isFinal = isFinal;
	}

	public String getDerivedFrom() {
		return derivedFrom;
	}

	public void setDerivedFrom(String derivedFrom) {
		this.derivedFrom = derivedFrom;
	}

	public String toString() {
		return "InheritanceResourceJson: { isAbstract: " + this.isAbstract + ", isFinal: " + this.isFinal + ", derivedFrom: " + this.derivedFrom + " }";
	}
}
