package org.eclipse.winery.repository.resources.servicetemplates.boundarydefinitions;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.taglibs.standard.functions.Functions;
import org.eclipse.winery.common.ModelUtilities;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions;
import org.eclipse.winery.model.tosca.TBoundaryDefinitions.Properties;
import org.eclipse.winery.model.tosca.TPlan;
import org.eclipse.winery.model.tosca.TPlans;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.Utils;
import org.eclipse.winery.repository.backend.BackendUtils;
import org.eclipse.winery.repository.backend.Repository;
import org.eclipse.winery.repository.datatypes.TypeWithShortName;
import org.eclipse.winery.repository.datatypes.select2.Select2DataItem;
import org.eclipse.winery.repository.resources.admin.types.ConstraintTypesManager;

public class BoundaryDefinitionsJSPData {

	private final TServiceTemplate ste;
	private final TBoundaryDefinitions defs;
	private URI baseURI;


	/**
	 *
	 * @param ste the service template of the boundary definitions. Required to
	 *            get a list of all plans
	 * @param baseURI the base URI of the service. Requried for rendering the
	 *            topology template for the selections
	 */
	public BoundaryDefinitionsJSPData(TServiceTemplate ste, URI baseURI) {
		this.ste = ste;
		this.defs = ste.getBoundaryDefinitions();
		this.baseURI = baseURI;
	}

	private String getDefinedProperties() {
		Properties p = ModelUtilities.getProperties(this.defs);
		Object o = p.getAny();
		if (o == null) {
			// nothing stored -> return empty string
			return "";
		} else {
			// something stored --> return that
			return Utils.getXMLAsString(p.getAny());
		}
	}

	/**
	 * Helper method to return an initialized properties object only containing
	 * the user-defined properties. The TOSCA properties-element is not returned
	 * as the TOSCA XSD allows a single element only
	 */
	public String getDefinedPropertiesAsEscapedHTML() {
		String s = this.getDefinedProperties();
		s = StringEscapeUtils.escapeHtml4(s);
		return s;
	}

	public String getDefinedPropertiesAsJSONString() {
		String s = this.getDefinedProperties();
		s = StringEscapeUtils.escapeEcmaScript(s);
		return s;
	}

	public TBoundaryDefinitions getDefs() {
		return this.defs;
	}

	public String getBoundaryDefinitionsAsXMLStringEncoded() {
		String res = Utils.getXMLAsString(this.defs);
		return Functions.escapeXml(res);
	}

	public Collection<TypeWithShortName> getConstraintTypes() {
		return ConstraintTypesManager.INSTANCE.getTypes();
	}

	public Collection<QName> getAllPolicyTypes() {
		SortedSet<PolicyTypeId> allTOSCAComponentIds = Repository.INSTANCE.getAllTOSCAComponentIds(PolicyTypeId.class);
		return BackendUtils.convertTOSCAComponentIdCollectionToQNameCollection(allTOSCAComponentIds);
	}

	public String getRepositoryURL() {
		return this.baseURI.toString();
	}

	public List<Select2DataItem> getlistOfAllPlans() {
		TPlans plans = this.ste.getPlans();
		if (plans == null) {
			return null;
		} else {
			List<Select2DataItem> res = new ArrayList<>(plans.getPlan().size());
			for (TPlan plan : plans.getPlan()) {
				String id = plan.getId();
				String name = ModelUtilities.getNameWithIdFallBack(plan);
				Select2DataItem di = new Select2DataItem(id, name);
				res.add(di);
			}
			return res;
		}
	}
}
