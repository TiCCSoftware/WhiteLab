// JavaScript Document
var sizing = "log";
var dataBAK = null;
var lang = "nl";

function buildTreemap(data){
	dataBAK = data;
	
	$("#display").html("");
	
	var min = null;
	var max = 0;
	var total = 0;
	
	for (var i = 0; i < data.children.length; i++) {
		var d = data.children[i];
		total = total + d.size;
		if ((min == null || d.size < min) && d.size > 0) {
			min = d.size;
		}
		if (d.size > max) {
			max = d.size;
		}
	}
	console.log("MIN: "+min+", MAX: "+max+", Sizing: "+sizing);
	
	var mid = max / 10;
	
	var colors = d3.scale.log()
		.domain([min,max])
		.range(["#53c4c3", "#9b0122"]);
	
	var textcolors = d3.scale.log()
		.domain([min,max])
		.range(["#000","#FFF"]);
	
	var shadowcolors = d3.scale.log()
		.domain([min,max])
		.range(["#FFF","#000"]);
	
	var margin = {top: 10, right: 0, bottom: 0, left: 0},
		width = 960,
		height = 500;

	var treemap = d3.layout.treemap()
		.size([width, height])
		.sticky(true)
		.sort(function(a,b) { return a.size - b.size; })
		.round(true)
		.value(function(d) { if (sizing === "log") { return Math.log(d.size); } else { return d.size; } });


	var div = d3.select("#display").append("div")
		.style("position", "relative")
		.style("width", (width + margin.left + margin.right) + "px")
		.style("height", (height + margin.top + margin.bottom) + "px")
		.style("left", margin.left + "px")
		.style("top", margin.top + "px")
		.attr("id", "first");

	var node = div.datum(data).selectAll(".node")
		.data(treemap.nodes)
		.enter().append("div")
		.attr("class", "node")
		.attr("id", function(d) { var t = d.name; t = t.replace(/\W/g, ''); return t; })
		.call(position)
		.style("background", function(d) { return colors(d.size); })
		.on("mouseover",mouseover)
		.on("mouseout",mouseout)
		.append("div")
		.attr("class","text")
		.text(function(d) { return d.children ? null : d.name })
		.style("color", function(d) { return "#FFF"; })
		.style("text-shadow", function(d) { var c = "#000"; return "-1px 0 "+c+", 0 1px "+c+", 1px 0 "+c+", 0 -1px "+c; });
		
	node.append("div")
	.attr("class","info")
	.text(function(d) { if (d.children) {
			return null;
		} else {
			var p = (d.size / total) * 100;
			p = Math.round(p * 100) / 100;
			return d.size + "  (" + p + " %)";
		}
	});

};

$(document).on("change", "input[name='sizing']", function() {
	sizing = $("input[name='sizing']:checked").val();
	buildTreemap(dataBAK);
});

function setLanguage(l) {
	lang = l;
	if (lang === "en") {
		$("div.controls").html('<b>Scale:</b><span class="space"></span><input type="radio" name="sizing" id="log" value="log" checked>log<span class="space"></span><input type="radio" name="sizing" id="normal" value="normal"> absolute');
	} else {
		$("div.controls").html('<b>Schaal:</b><span class="space"></span><input type="radio" name="sizing" id="log" value="log" checked>log<span class="space"></span><input type="radio" name="sizing" id="normal" value="normal"> absoluut');
	}
}

function mouseover(d) {
	var t = d.name;
	t = t.replace(/\W/g, '');
	$("#"+t+" .info").addClass("active");
	d3.selectAll(".node")
	  .transition()
	  .style("opacity", 0.5);
	d3.selectAll("#"+t)
	  .transition()
	  .style("opacity", 1.0);
}

function mouseout(d) {
	var t = d.name;
	t = t.replace(/\W/g, '');
	$("#"+t+" .info").removeClass("active");
	d3.selectAll(".node")
	  .transition()
	  .style("opacity", 1.0);
}

function position() {
  this.style("left", function(d) { return d.x + "px"; })
      .style("top", function(d) { return d.y + "px"; })
      .style("width", function(d) { return Math.max(0, d.dx - 1) + "px"; })
      .style("height", function(d) { return Math.max(0, d.dy - 1) + "px"; });
}
