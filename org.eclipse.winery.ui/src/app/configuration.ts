export const sections = {
    nodetypes : 'nodeType',
    servicetemplates : 'serviceTemplate',
    relationshiptypes : 'relationshipType',
    artifacttypes: 'artifactType',
    artifacttemplates: 'artifactTemplate',
    requirementtypes: 'requirementType',
    capabilitytypes: 'capabilityType',
    nodetypeimplementations : 'nodeTypeImplementation',
    relationshiptypeimplementations : 'relationshipTypeImplementation',
    policytypes: 'policyType',
    policytemplate: 'policyTemplate',
    imports: 'xSDImport',
};

export class Configuration {

    public static getSubMenuByResource(type: string): string[] {
        let subMenu: string[];

        switch (type.toLowerCase()) {
            case 'nodetype':
                subMenu = ['Visual Appearance', 'Instance States', 'Interfaces', 'Implementations',
                    'Requirement Definitions' , 'Capability Definitions', 'Property Definition',
                    'Inheritance', 'Documentation', 'XML'];
                break;
                //TODO: add all;
            default:
                subMenu = [''];
        }

        return subMenu;
    }

}
