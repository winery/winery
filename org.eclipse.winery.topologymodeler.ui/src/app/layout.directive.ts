import { AfterViewInit, Directive } from '@angular/core';
import ELK from 'elkjs/lib/elk.bundled.js';

@Directive({
  selector: '[appLayout]',
})
export class LayoutDirective implements AfterViewInit {

  public layoutNodes(nodeTemplates: any[], relationshipTemplates: any[], jsPlumbInstance: any): void {

    const children: any[] = [];
    const edges: any[] = [];

    // get with and height of nodes
    nodeTemplates.forEach((node) => {
      const width = document.getElementById(node.id).offsetWidth;
      const height = document.getElementById(node.id).offsetHeight;
      children.push({id: node.id, width: width, height: height});
      // also get their current positions and apply them to the internal list
      const left = document.getElementById(node.id).offsetLeft;
      const top = document.getElementById(node.id).offsetTop;
      // apply the old positions to the nodeslist
      node.otherAttributes['x'] = left;
      node.otherAttributes['y'] = top;
    });

    // get source and targets of relationships
    relationshipTemplates.forEach((rel, index) => {
      const sourceElement = rel.sourceElement;
      const targetElement = rel.targetElement;
      edges.push({id: index.toString(), sources: [sourceElement], targets: [targetElement]});
    });

    // initialize elk object which will layout the graph
    const elk = new ELK({});
    const graph = {
      id: 'root',
      properties: {
        'elk.algorithm': 'layered',
        'elk.spacing.nodeNode': '200',
        'elk.direction': 'DOWN',
        'elk.layered.spacing.nodeNodeBetweenLayers': '200'
      },
      children: children,
      edges: edges,
    };

    const promise = elk.layout(graph);
    promise.then((data) => {
      this.applyPositions(data, nodeTemplates, jsPlumbInstance);
    });
  }

  private applyPositions(data: any, nodeTemplates: any[], jsPlumbInstance: any): void {
    nodeTemplates.forEach((node, index) => {
      // apply the new positions to the nodes
      node.otherAttributes['x'] = data.children[index].x;
      node.otherAttributes['y'] = data.children[index].y + 40;
    });

    this.repaintEverything(jsPlumbInstance);
  }

  public alignHorizontal(selectedNodes: any[], jsPlumbInstance: any): void {
    let smallestVal = 0;
    let biggestVal = 0;
    let result;
    // if there is only 1 node selected, do nothing
    if (!( selectedNodes.length === 1)) {
      selectedNodes.forEach((node, index) => {
        // if its the first iteration, inititalize
        if (index === 0) {
          smallestVal = document.getElementById(node.id).offsetTop;
          biggestVal = document.getElementById(node.id).offsetTop;
        } else {
          // if the biggestValue is smaller than the current value, save it
          if (biggestVal < document.getElementById(node.id).offsetTop) {
            biggestVal = document.getElementById(node.id).offsetTop;
          }
          // if the smallest val is bigger than the current value, save it.
          if (smallestVal > document.getElementById(node.id).offsetTop) {
            smallestVal = document.getElementById(node.id).offsetTop;
          }
        }
      });

      result = biggestVal - smallestVal;
      result = (result / 2);
      result = smallestVal + result;
      // iterate over the nodes again, and apply positions
      selectedNodes.forEach((node) => {
        node.otherAttributes['y'] = result;
      });
      this.repaintEverything(jsPlumbInstance);
    }
  }

  public alignVertical(selectedNodes: any[], jsPlumbInstance: any): void {
    let smallestVal = 0;
    let biggestVal = 0;
    let result;
    // if there is only 1 node selected, do nothing
    if (!( selectedNodes.length === 1)) {
      selectedNodes.forEach((node, index) => {
        // if its the first iteration, inititalize
        console.log(node, index);
        if (index === 0) {
          smallestVal = document.getElementById(node.id).offsetLeft;
          biggestVal = document.getElementById(node.id).offsetLeft;
        } else {
          // if the biggestValue is smaller than the current value, save it
          if (biggestVal < document.getElementById(node.id).offsetLeft) {
            biggestVal = document.getElementById(node.id).offsetLeft;
          }
          // if the smallest val is bigger than the current value, save it.
          if (smallestVal > document.getElementById(node.id).offsetLeft) {
            smallestVal = document.getElementById(node.id).offsetLeft;
          }
        }
      });

      result = biggestVal - smallestVal;
      result = (result / 2);
      result = smallestVal + result;
      // iterate over the nodes again, and apply positions
      selectedNodes.forEach((node) => {
        node.otherAttributes['x'] = result;
      });
      this.repaintEverything(jsPlumbInstance);
    }
  }

  public repaintEverything(jsPlumbInstance: any): void {
    setTimeout(() => jsPlumbInstance.repaintEverything(), 1);
  }

  ngAfterViewInit() {

  }
}
