var posLabels = ['ADJ','BW','LET','LID','N','SPEC','TSW','TW','VG','VNW','VZ','WW'];
var posColors = ['#272727','#ab0000','#0056e1','#00abab','#353593','#faae0f','#555555','#e06666','#0088ee','#66e0e0','#6666bb','#fde281'];
var colorScale = d3.scale.ordinal().domain(posLabels).range(posColors);
var degrees = [-90,0,0,0,0,90];
var weightScale = null;
var fontScale = null;
var maxRadius = 20;
var nodes = new Array();
var svg,node, label;
var width, height;
var split = false;
var min = 0, max = 0;
var gnodes = null;
var glabels = null;
var noLegend = false;

var cloud = null;
var container = null;


function loadCloudData(target,s,data,o) {
	for (var i = 0; i < data.length; i++) {
		var item = data[i];
		addNode(item.lemma,item.freq,item.pos,item.clean);
	}
	generateCloud(target, s, o);
}

function clearCloud() {
	nodes = new Array();
	min = 0, max = 0;
	gnodes = null;
	glabels = null;
	noLegend = false;
	split = false;
	cloud = null;
	container = null;
}

function addNode(l,w,p,c) {
	if (c.indexOf("`") > -1)
		c = c.replace(/`/g,"TICK");
	var node = {x: 200, y: 200, text: l, m: w, pos: p, clean: c};
//	console.log("Added node:");
//	console.log(node);
	nodes.push(node);
	if (max == 0) {
		max = w;
		min = w;
	} else {
		if (w > max) { max = w; }
		if (w < min) { min = w; }
	}
}

function generateCloud(target, s, omitLegend) {
	console.log("Generating cloud");
	$(target).html("");
	container = target;
	
	width = 1150;
	height = 750;
	split = s;
	if (split) {
		height = 1500;
	}
	fontScale = d3.scale.linear().domain([min,max]).range([30,100]);
	
	if (omitLegend != null && omitLegend == true) {
		noLegend = true;
	}
	
	cloud = d3.layout.cloud().size([width,height])
		.words(nodes)
		.padding(5)
		.rotate(function() { return 0; })
		.font("Impact")
		.fontSize(function(d) { return fontScale(d.m); })
		.on("end", draw)
		.start();
}

function draw() {
	console.log("Drawing cloud");
	var w = width / 2;
	var h = height / 2;
	if (split) {
		w = width / 6;
		h = height / 8;
	}
	
	var posNodes = [];
	for (var i = 0; i < posLabels.length; i++) {
		var pl = posLabels[i];
		var px = legendPosX(pl);
		var py = legendPosY(pl);
		var node = {x:px, y:py, label:pl};
		posNodes.push(node);
	}
	
	var svg = d3.select(container).append("svg")
		.attr("width", width)
		.attr("height", height);
	
	svg.append("g")
		.attr("transform", "translate("+w+","+h+")")
	.selectAll("text")
		.data(nodes)
	.enter().append("text")
		.style("font-size", function(d) { return d.size + "px"; })
		.style("font-family", "Impact")
		.style("fill", function(d) { return colorScale(d.pos); })
		.attr("id", function(d) { return "glabel_"+d.clean; })
		.attr("text-anchor", "middle")
		.attr("transform", function(d) {
			if (split) {
				return "translate(" + [posX(d),posY(d)] + ")rotate(" + d.rotate + ")";
			} else {
				return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
			}
		})
		.text(function(d) { return d.text; })
	.on("mouseover", function() {
  
	});
	
	if (noLegend == false) {
		var pnodes = svg.selectAll("g.pnode")
	    .data(posNodes)
		.enter().append("g")
		.attr("id", function(d) { return d.pl; })
		.attr("class","pnode");
		
		pnodes.append("rect")
		  .attr("class", "legend")
		  .attr("width", "50px")
		  .attr("height", "24px")
		  .style("fill", function(d) { return colorScale(d.label); })
		  .style("stroke","black")
		  .style("stroke-width","1");
		
		pnodes.append("text")
			.text(function(d) { return d.label; })
			.attr("class", "freq")
			.style("fill", "white")
			.style("font-size", "12px")
		    .attr('text-anchor', 'left');
		
		svg.selectAll("rect")
	    .attr("x", function(d) { if (split) { return d.x + posX(d) + 5 - 770; } else { return d.x + 5; } })
	    .attr("y", function(d) { if (split) { return d.y + posY(d) + (getTextHeight("glabel_"+d.clean+'_'+d.pos) + 5); } else { return d.y + (getTextHeight("glabel_"+d.clean+'_'+d.pos) + 5); } });
	}
	  svg.selectAll("text.freq")
	  .attr("transform", function(d) {
		  var x = d.x + 10;
		  if (split) {
			  x = d.x + posX(d) + 10 - 770;
		  }
		  var y = d.y + (getTextHeight("glabel_"+d.clean+'_'+d.pos) + 20);
		  if (split) {
			  y = d.y + posY(d) + (getTextHeight("glabel_"+d.clean+'_'+d.pos) + 20);
		  }
		  return "translate("+x+","+y+")rotate(0)";
		});
}

function mouseover() {
	var id = d3.select(this.parentNode).attr("id");
	id = id.replace("gnode","glabel");
	d3.select("#"+id).style("visibility","visible");
}

function mouseout() {
	var id = d3.select(this.parentNode).attr("id");
	id = id.replace("gnode","glabel");
	d3.select("#"+id).style("visibility","hidden");
}

function getRadius(d) {
	var f = fontScale(d.m);
	var radius = ((f / 2) * d.label.length) / 2; if (radius < 15) { radius = 15; } if (radius > maxRadius) { maxRadius = radius; } return radius;
}

function setSplit(s) {
	split = s;
}

function legendPosX(label) {
	var i = posLabels.indexOf(label);
	var x = 0;
	if (split) {
		if (i % 3 == 0) {
			x = 0;
		} else if ((i - 1) % 3 == 0) {
			x = (1 / 3) * width;
		} else {
			x = (2 / 3) * width;
		}
//		x = x + 50;
	} else {
		if (i % 3 == 0) {
			x = (1 / 18) * width;
		} else if ((i - 1) % 3 == 0) {
			x = (2 / 18) * width;
		} else {
			x = (3 / 18) * width;
		}
		x = x - 1/18 * width;
	}
	return x;
}

function legendPosY(label) {
	console.log("legendPosY "+label);
	var i = posLabels.indexOf(label);
	var y = 0;
	if (split) {
		if (i < 3) {
			y = 0;
			console.log("split:1 i:"+i+" y:"+y);
		} else if (i < 6) {
			y = 0.25 * height;
			console.log("split:2 i:"+i+" y:"+y);
		} else if (i < 9) {
			y = 0.5 * height;
			console.log("split:3 i:"+i+" y:"+y);
		} else {
			y = 0.75 * height;
			console.log("split:4 i:"+i+" y:"+y);
		}
//		y = y + 50;
	} else {
		if (i < 3) {
			y = 30;
		} else if (i < 6) {
			y = 60;
		} else if (i < 9) {
			y = 90;
		} else {
			y = 120;
		}
	}
	return y;
}


function posX(d) {
	var i = posLabels.indexOf(d.pos);
	var x = 0;
	if (i % 3 == 0) {
		x = (d.x / 3);
	} else if ((i - 1) % 3 == 0) {
		x = (d.x / 3) + 1 * (width / 3);
	} else {
		x = (d.x / 3) + 2 * (width / 3);
	}
	return x;
}

function posY(d) {
	var i = posLabels.indexOf(d.pos);
	var y = 0;
	if (i < 3) {
		y = (d.y / 4);
	} else if (i < 6) {
		y = (d.y / 4) + 1 * (height / 4);
	} else if (i < 9) {
		y = (d.y / 4) + 2 * (height / 4);
	} else {
		y = (d.y / 4) + 3 * (height / 4);
	}
	return y;
}

function getTextWidth(id) {
	return $("#"+id+" text").width();
}

function getTextHeight(id) {
	return $("#"+id+" text").height();
}
