package org.eclipse.winery.model.substitution;

import java.util.HashMap;
import java.util.stream.Stream;

import javax.xml.namespace.QName;

import org.eclipse.winery.common.ids.definitions.NodeTypeId;
import org.eclipse.winery.common.ids.definitions.ServiceTemplateId;
import org.eclipse.winery.model.tosca.TNodeType;
import org.eclipse.winery.model.tosca.TServiceTemplate;
import org.eclipse.winery.repository.TestWithGitBackedRepository;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubstitutionUtilsTestWithGitBackedRepository extends TestWithGitBackedRepository {

    @ParameterizedTest(name = "{index} => ''{2}''")
    @MethodSource("containsPatternArguments")
    void testContainsPatterns(ServiceTemplateId stId, boolean expected, String description) throws Exception {
        this.setRevisionTo("origin/plain");

        HashMap<QName, TNodeType> nodeTypes = new HashMap<>();
        this.repository.getAllDefinitionsChildIds(NodeTypeId.class)
            .forEach(id ->
                nodeTypes.put(id.getQName(), this.repository.getElement(id))
            );
        TServiceTemplate serviceTemplate = this.repository.getElement(stId);

        assertEquals(
            expected,
            SubstitutionUtils.containsPatterns(serviceTemplate.getTopologyTemplate().getNodeTemplates(), nodeTypes)
        );
    }

    private static Stream<Arguments> containsPatternArguments() {
        ServiceTemplateId st1 = new ServiceTemplateId("http://plain.winery.opentosca.org/servicetemplates",
            "ServiceTemplateWithTwoNodeTemplates_w1-wip2", false);
        ServiceTemplateId st2 = new ServiceTemplateId("http://plain.winery.org/pattern-based/servicetemplates",
            "ServiceTemplateContainingAbstractNodeTemplates_w2-wip1", false);

        return Stream.of(
            Arguments.of(st1, false, "expecting no patterns in the topology"),
            Arguments.of(st2, true, "expecting patterns in the topology")
        );
    }
}
