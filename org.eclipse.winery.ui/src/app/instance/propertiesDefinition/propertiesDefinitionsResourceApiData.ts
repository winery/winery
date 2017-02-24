import { SelectData } from '../../interfaces/selectData';

export interface PropertiesDefinitionKVList {
    key: string;
    type: string;
}

export interface WinerysPropertiesDefiniton {
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
    propertiesDefinitions: any;
    winerysPropertiesDefiniton: WinerysPropertiesDefiniton;
}
