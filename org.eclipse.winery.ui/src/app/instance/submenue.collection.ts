import { NodeTypeSubmenue } from "./subMenueEnums/nodeTypesSubmenue.enum";
import { ResourceTypes } from "../resourceTypes.enum";
import { ServiceTemplateSubmenue } from "./subMenueEnums/serviceTemplateSubmenue.enum";
import { RelationshipTypeSubmenue } from "./subMenueEnums/relationshipTypesSubmenue.enum";
import { AdminSubmenue } from "./subMenueEnums/adminSubmenue.enum";

export class SubmenueCollection {
    nodeTypeSubmenue = NodeTypeSubmenue;

    getSubmenue(resType: ResourceTypes): any {
        switch (ResourceTypes[ResourceTypes[resType]]) {
            case ResourceTypes[ResourceTypes.serviceTemplates]:
                return ServiceTemplateSubmenue;
            case ResourceTypes[ResourceTypes.nodeTypes]:
                return NodeTypeSubmenue;
            case ResourceTypes[ResourceTypes.relationshipTypes]:
                return RelationshipTypeSubmenue;
            case ResourceTypes[ResourceTypes.admin]:
                return AdminSubmenue;
            default:
                return ServiceTemplateSubmenue;
        }
    }
}
