"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var AbstractTTemplate = (function () {
    function AbstractTTemplate(documentation, any, otherAttributes) {
        this.documentation = documentation;
        this.any = any;
        this.otherAttributes = otherAttributes;
    }
    return AbstractTTemplate;
}());
exports.AbstractTTemplate = AbstractTTemplate;
var TTopologyTemplate = (function (_super) {
    __extends(TTopologyTemplate, _super);
    function TTopologyTemplate() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.nodeTemplates = [];
        _this.relationshipTemplates = [];
        return _this;
    }
    return TTopologyTemplate;
}(AbstractTTemplate));
exports.TTopologyTemplate = TTopologyTemplate;
var TNodeTemplate = (function (_super) {
    __extends(TNodeTemplate, _super);
    function TNodeTemplate(properties, id, type, name, minInstances, maxInstances, color, imageUrl, documentation, any, otherAttributes) {
        var _this = _super.call(this, documentation, any, otherAttributes) || this;
        _this.properties = properties;
        _this.id = id;
        _this.type = type;
        _this.name = name;
        _this.minInstances = minInstances;
        _this.maxInstances = maxInstances;
        _this.color = color;
        _this.imageUrl = imageUrl;
        return _this;
    }
    return TNodeTemplate;
}(AbstractTTemplate));
exports.TNodeTemplate = TNodeTemplate;
var TRelationshipTemplate = (function (_super) {
    __extends(TRelationshipTemplate, _super);
    /*
    get targetElement(): string {
      return this.targetElement;
    }
    get sourceElement(): string {
      return this.sourceElement;
    }
    */
    function TRelationshipTemplate(sourceElement, targetElement, name, id, type, documentation, any, otherAttributes) {
        var _this = _super.call(this, documentation, any, otherAttributes) || this;
        _this.sourceElement = sourceElement;
        _this.targetElement = targetElement;
        _this.name = name;
        _this.id = id;
        _this.type = type;
        return _this;
    }
    return TRelationshipTemplate;
}(AbstractTTemplate));
exports.TRelationshipTemplate = TRelationshipTemplate;
var Visuals = (function () {
    function Visuals(color, nodeTypeId, localName, imageUrl) {
        this.color = color;
        this.nodeTypeId = nodeTypeId;
        this.localName = localName;
        this.imageUrl = imageUrl;
    }
    return Visuals;
}());
exports.Visuals = Visuals;
