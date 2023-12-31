import React from 'react';
import * as d3 from 'd3';
import { zoom } from 'd3-zoom';

const DependencyGraph = ({ dependencyData }) => {
  const svgRef = React.useRef();

  React.useEffect(() => {
    d3.select(svgRef.current).selectAll("*").remove();

    const width = 3800;
    const height = 1800;
    
    const svg = d3.select(svgRef.current)
    .attr('width', width)
    .attr('height', height)
    .attr('viewBox', [-width / 2, -height / 2, width, height])
    .attr('style', 'width: 100%; height: 80vh');
    
    svg.append("defs").selectAll("marker")
    .data(["arrow"])
    .enter().append("marker")
    .attr("id", d => d)
    .attr("viewBox", "0 -5 10 10")
    .attr("refX", 15)
    .attr("refY", 0)
    .attr("markerWidth", 8)
    .attr("markerHeight", 8)
    .attr("orient", "auto")
    .append("path")
    .attr("fill", "#999")
    .attr("d", "M0,-5L10,0L0,5");

    const zoomBehavior = zoom()
    .scaleExtent([0.5, 10]) 
    .on("zoom", zoomed);

    svg.call(zoomBehavior);

    function zoomed(event) {
      const { transform } = event;
      svg.selectAll("g")
        .attr("transform", transform);
    }

    const nodes = dependencyData.dependency.map((dependency) => ({
      id: dependency.versionKey.name + " " + dependency.versionKey.version,
    }));

    const links = dependencyData.edges.map((edge) => ({
      source: edge.fromNode,
      target: edge.toNode,
    }));

    const simulation = d3
    .forceSimulation(nodes)
    .force('link', d3.forceLink(links).id((d) => d.index).distance(250))
    .force('charge', d3.forceManyBody().strength(-1000))
    .on("tick", ticked);

    const link = svg.append("g")
    .attr("stroke", "#999")
    .attr("stroke-opacity", 0.6)
    .selectAll()
    .data(links)
    .join("line")
    .attr("stroke-width", d => Math.sqrt(d.value))
    .attr("marker-end", "url(#arrow)");

    const node = svg.append("g")
    .attr("stroke", "#fff")
    .attr("stroke-width", 1.5)
    .selectAll()
    .data(nodes)
    .join("circle")
    .attr("r", 5)

    node.append('title').text((d) => d.id);

    node.call(d3.drag()
    .on("start", dragstarted)
    .on("drag", dragged)
    .on("end", dragended));

    const textLabels = svg.append("g")
    .selectAll("text")
    .data(nodes)
    .join("text")
    .attr("class", "node-label")
    .attr("dy", -10)
    .attr("fill", "#fff")
    .text(d => d.id);

    function ticked() {
      node
      .attr("cx", d => Math.max(-width / 2, Math.min(width / 2, d.x)))
      .attr("cy", d => Math.max(-height / 2, Math.min(height / 2, d.y)));

      link
      .attr("x1", d => d.source.x)
      .attr("y1", d => d.source.y)
      .attr("x2", d => d.target.x)
      .attr("y2", d => d.target.y);

      node
      .attr("cx", d => d.x)
      .attr("cy", d => d.y);

      textLabels
      .attr("x", d => d.x)
      .attr("y", d => d.y);
    }

    function dragstarted(event, d) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      d.fx = d.x; 
      d.fy = d.y; 
    }

    function dragged(event) {
      event.subject.fx = event.x;
      event.subject.fy = event.y;
    }

    function dragended(event) {
      if (!event.active) simulation.alphaTarget(0);
    }

    return () => {
      simulation.stop();
    };
  }, [dependencyData]);

  return (
    <div className="dependency-graph-container">
      <svg ref={svgRef} />
      <div className="ui-container">
      </div>
    </div>
  );
};

export default DependencyGraph;
