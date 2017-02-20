package org.eclipse.winery.repository.resources.jsonClasses;

import org.eclipse.winery.common.ids.definitions.TOSCAComponentId;

import javax.xml.namespace.QName;

public class QNameJson {

	private String name;
	private QName qName;

	public QNameJson() {}

	public QNameJson(TOSCAComponentId id) {
		this.name = id.getXmlId().getDecoded();
		this.qName = id.getQName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public QName getqName() {
		return qName;
	}

	public void setqName(QName qName) {
		this.qName = qName;
	}
}
