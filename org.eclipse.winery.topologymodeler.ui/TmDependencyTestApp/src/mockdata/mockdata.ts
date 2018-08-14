export const configuration = {
    readonly: true,
    endpointConfig: undefined
};

export const visuals = [{
  "color": "#1ec453",
  "nodeTypeId": "{http://www.winery.opentosca.org/test/targetallocation/nodetypes}LargeStall_-w1-wip1"
}, {
  "color": "#d39cf2",
  "nodeTypeId": "{http://www.winery.opentosca.org/test/targetallocation/nodetypes}LargeStall_-w2-wip1"
}, {
  "color": "#887591",
  "nodeTypeId": "{http://www.winery.opentosca.org/test/targetallocation/nodetypes}LargeStall_-w3-wip1"
}, {
  "imageUrl": "/winery/nodetypes/http%253A%252F%252Fwinery.opentosca.org%252Ftest%252Fnodetypes%252Ffruits/baobab/appearance/50x50",
  "color": "#89ee01",
  "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}baobab"
}, {
  "color": "#d30b26",
  "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}dressageequipment"
}, {"color": "#cea0fa", "nodeTypeId": "{http://www.winery.opentosca.org/test/ponyuniverse}field"}, {
  "color": "#89ee01",
  "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}grape"
}, {"color": "#89ee01", "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}lemon"}, {
  "color": "#89ee01",
  "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}mango"
}, {"color": "#01ace2", "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}oat"}, {
  "color": "#89ee01",
  "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}orange"
}, {"color": "#cb1016", "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}pasture"}, {
  "color": "#6f02b4",
  "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}plantage"
}, {
  "color": "#226f75",
  "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}ponycompetition"
}, {
  "color": "#bb1c9a",
  "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}shetland_pony"
}, {"color": "#8ac3a0", "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}stall"}, {
  "color": "#8b0227",
  "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}straw"
}, {"color": "#36739e", "nodeTypeId": "{http://winery.opentosca.org/test/nodetypes/fruits}tree"}, {
  "color": "#458ac5",
  "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}trough"
}, {"color": "#e47c98", "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}unicorn"}, {
  "color": "#03ec7c",
  "nodeTypeId": "{http://winery.opentosca.org/test/ponyuniverse}westernequipment"
}];

export const topologytemplate = {
  "documentation": [],
  "any": [],
  "otherAttributes": {},
  "relationshipTemplates": [{
    "id": "con_1",
    "documentation": [],
    "any": [],
    "otherAttributes": {},
    "type": "hostedOn",
    "sourceElement": {"ref": "lemon"},
    "targetElement": {"ref": "field"},
    "name": "con_1"
  }],
  "nodeTemplates": [{
    "id": "field",
    "documentation": [],
    "any": [],
    "otherAttributes": {
      "{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}x": "456",
      "{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}y": "324"
    },
    "properties": {},
    "type": "{http://www.winery.opentosca.org/test/ponyuniverse}field",
    "requirements": {"requirement": []},
    "capabilities": {
      "capability": [{
        "id": "fieldCap",
        "documentation": [],
        "any": [],
        "otherAttributes": {},
        "type": "{http://winery.opentosca.org/test/ponyuniverse}CapWarmFloor",
        "name": "CapWarmFloor"
      }]
    },
    "policies": {
      "policy": [{
        "documentation": [],
        "any": [],
        "otherAttributes": {},
        "name": "floorwarmth",
        "policyType": "{http://winery.opentosca.org/test/ponyuniverse/policytypes}FloorWarmth",
        "policyRef": "{http://winery.opentosca.org/test/ponyuniverse/policytemplates}FieldFloorWarmth"
      }]
    },
    "deploymentArtifacts": {"deploymentArtifact": []},
    "name": "field",
    "minInstances": 1,
    "maxInstances": "1",
    "x": "456",
    "y": "324"
  }, {
    "id": "lemon",
    "documentation": [],
    "any": [],
    "otherAttributes": {
      "{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}x": "517",
      "{http://www.opentosca.org/winery/extensions/tosca/2013/02/12}y": "166"
    },
    "properties": {
      "kvproperties": {
        "Antioxidants": "",
        "VitaminC": "",
        "Potassium": "",
        "Superfood": "",
        "HarvestedAt": ""
      }
    },
    "type": "{http://winery.opentosca.org/test/nodetypes/fruits}lemon",
    "requirements": {"requirement": []},
    "capabilities": {"capability": []},
    "policies": {"policy": []},
    "deploymentArtifacts": {"deploymentArtifact": []},
    "name": "lemon",
    "minInstances": 1,
    "maxInstances": "1",
    "x": "517",
    "y": "166"
  }]
};
