import { SelectData } from '../../interfaces/selectData';

export interface PropertiesDefinitionKVList {
    key: string;
    type: string;
}

export interface WinerysPropertiesDefinition {
    namespace: string;
    elementName: string;
    propertiesDefinitionKVList: PropertiesDefinitionKVList[];
    isDerivedFromXSD: boolean;
}

interface PropertiesDefinitions {
    element: string;
    type: string;
}

export interface PropertiesDefinitonsResourceApiData {
    xsdElementDefinitions: SelectData[];
    xsdTypeDefinitions: SelectData[];
    propertiesDefinition: any;
    winerysPropertiesDefinition: WinerysPropertiesDefinition;
}
