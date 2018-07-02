/********************************************************************************
 * Copyright (c) 2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 ********************************************************************************/
package org.eclipse.winery.common.version;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.common.ids.definitions.ArtifactTypeId;
import org.eclipse.winery.common.ids.definitions.CapabilityTypeId;
import org.eclipse.winery.common.ids.definitions.DefinitionsChildId;
import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.NodeTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.PolicyTemplateId;
import org.eclipse.winery.common.ids.definitions.PolicyTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeId;
import org.eclipse.winery.common.ids.definitions.RelationshipTypeImplementationId;
import org.eclipse.winery.common.ids.definitions.RequirementTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.HasIdInIdOrNameField;
import org.eclipse.winery.model.tosca.TExtensibleElements;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.identity.IdentityStrategy;
import de.danielbechler.diff.node.DiffNode;
import io.github.adr.embedded.ADR;

/**
 * Utility class for working with versions.
 *
 * TODO: DefinitionsChildId specific parts should go into into the DefinitionsChildId to have a true object-oriented thinking
 */
public class VersionUtils {

    private static final Pattern VERSION_PATTERN = Pattern.compile("_(([^_]*)-)?w([0-9]+)(-wip([0-9]+))?$");

    private static String REFERENCING_OBJECT = "referencingObject";

    public static WineryVersion getVersion(DefinitionsChildId id) {
        return getVersion(id.getXmlId().getDecoded());
    }

    public static WineryVersion getVersionWithCurrentFlag(DefinitionsChildId id, DefinitionsChildId requestingElementInSet) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(requestingElementInSet);
        return getVersionWithCurrentFlag(id.getXmlId().getDecoded(), requestingElementInSet.getXmlId().getDecoded());
    }

    public static String getNameWithoutVersion(DefinitionsChildId id) {
        return getNameWithoutVersion(id.getXmlId().getDecoded());
    }

    public static String getNameWithoutVersion(String id) {
        Matcher m = VERSION_PATTERN.matcher(id);
        int idLength = id.length();

        if (m.find()) {
            idLength = m.start();
        }

        return id.substring(0, idLength);
    }

    @ADR(18)
    public static WineryVersion getVersion(String id) {
        Matcher m = VERSION_PATTERN.matcher(id);

        if (m.find()) {
            String componentVersion = Objects.nonNull(m.group(2)) ? m.group(2) : "";
            int wineryVersion = Objects.nonNull(m.group(3)) ? Integer.parseInt(m.group(3)) : 0;
            int workInProgressVersion = Objects.nonNull(m.group(4)) ? Integer.parseInt(m.group(5)) : 0;

            return new WineryVersion(componentVersion, wineryVersion, workInProgressVersion);
        }

        return new WineryVersion();
    }

    public static DefinitionsChildId getDefinitionInTheGivenVersion(DefinitionsChildId childId, WineryVersion otherVersion) {
        if (getVersion(childId).compareTo(otherVersion) == 0) {
            return childId;
        }

        String localPart = getNameWithoutVersion(childId) +
            (otherVersion.toString().length() > 0 ? WineryVersion.WINERY_NAME_FROM_VERSION_SEPARATOR + otherVersion.toString() : "");

        QName qName = new QName(childId.getNamespace().getDecoded(), localPart);
        if (childId instanceof RelationshipTypeImplementationId) {
            return new RelationshipTypeImplementationId(qName);
        } else if (childId instanceof NodeTypeImplementationId) {
            return new NodeTypeImplementationId(qName);
        } else if (childId instanceof RequirementTypeId) {
            return new RequirementTypeId(qName);
        } else if (childId instanceof NodeTypeId) {
            return new NodeTypeId(qName);
        } else if (childId instanceof RelationshipTypeId) {
            return new RelationshipTypeId(qName);
        } else if (childId instanceof CapabilityTypeId) {
            return new CapabilityTypeId(qName);
        } else if (childId instanceof ArtifactTypeId) {
            return new ArtifactTypeId(qName);
        } else if (childId instanceof PolicyTypeId) {
            return new PolicyTypeId(qName);
        } else if (childId instanceof PolicyTemplateId) {
            return new PolicyTemplateId(qName);
        } else if (childId instanceof ServiceTemplateId) {
            return new ServiceTemplateId(qName);
        } else if (childId instanceof ArtifactTemplateId) {
            return new ArtifactTemplateId(qName);
        } else {
            throw new IllegalStateException("Unhandled id branch. Could happen for XSDImportId");
        }
    }

    public static WineryVersion getVersionWithCurrentFlag(String id, String requestingElementInSet) {
        WineryVersion version = getVersion(id);
        version.setCurrentVersion(id.equals(requestingElementInSet));
        return version;
    }

    public static <T extends TExtensibleElements> ToscaDiff calculateDifferences(T oldVersion, T newVersion) {
        IdentityStrategy identityStrategy = (o, o1) -> {
            if (o instanceof HasIdInIdOrNameField && o1 instanceof HasIdInIdOrNameField) {
                return Objects.equals(
                    ((HasIdInIdOrNameField) o).getIdFromIdOrNameField(),
                    ((HasIdInIdOrNameField) o1).getIdFromIdOrNameField());
            } else {
                return Objects.equals(o, o1);
            }
        };

        DiffNode diffNode = ObjectDifferBuilder
            .startBuilding()
            .categories()
            // In the scope of winery, a source or target element will not be changed, because relationship templates will be removed if
            // either one of the source or target element is removed. Therefore, to avoid changed relationships, if one of the source or
            // target element was changed, we ignore referencing elements.
            .ofType(TRelationshipTemplate.SourceOrTargetElement.class)
            .toBe(REFERENCING_OBJECT)
            .and()
            .inclusion()
            .exclude()
            .propertyName("nodeTemplateOrRelationshipTemplate")
            .propertyName("fakeJacksonType")
            // Ignore 'any', otherwise, it crashes if a policy with a XML content is contained somehow: java.util.Collections$EmptyEnumeration
            // at nodeTemplate/policies/policy/any/[content]/parentNode/identifiers
            .propertyName("any")
            .propertyName("internalAny")
            // ignore changes of the namespace prefix 
            .propertyName("prefix")
            .category(REFERENCING_OBJECT)
            .and()
            .identity()
            // to provide a proper identification of elements in lists, use the custom identity strategy
            .setDefaultCollectionItemIdentityStrategy(identityStrategy)
            .and()
            .build()
            .compare(newVersion, oldVersion);

        return ToscaDiff.convertDiffToToscaDiff(diffNode, oldVersion, newVersion);
    }
}
