var pos = ['ADJ','BW','LET','LID','N','SPEC','TSW','TW','VG','VNW','VZ','WW'];
var colors = ['#272727','#ab0000','#0056e1','#00abab','#353593','#faae0f','#555555','#e06666','#0088ee','#66e0e0','#6666bb','#fde281'];
var colorScale = d3.scale.ordinal().domain(pos).range(colors);
var lang = 'nl';

$(document).on("click", "#pos_nav a", function(e) {
	e.preventDefault();
	var id = $(this).attr("href");
	$("#pos_nav a").parent().removeClass("active");
	$("#pos_nav a").parent().css('background-color', "white");
	$("#pos_nav a").parent().css('color', function( i ) {
		  return colorScale(pos[i]);
	});
	$(this).parent().addClass("active");
	$(this).parent().css('background-color', colorScale(id.substring(1)));
	$(this).parent().css('color', "white");
	$("#pos_data .pos_tab").removeClass("active");
	$(id).addClass("active");
	$(id).find(".right").removeClass("active");
	$(id).find(".right.top10").addClass("active");
});

$(document).on("click","div.poslink > a", function(e) {
	e.preventDefault();
	if (!$(this).hasClass("active")) {
		$(this).parent().parent().find("div.poslink > a").removeClass("active");
		$(this).addClass("active");
		var href = $(this).attr("href");
		$(this).parent().parent().parent().find("div.right").removeClass("active");
		$(href).addClass("active");
	}
});

function drawToolbar(container,url) {
	var components = [
		{type: 'csv', datasource: "http://"+window.location.host + url+"&format=csv"}
    ];
    google.visualization.drawToolbar(container, components);
};

function displayStats(response,target) {
	var data = $.parseJSON(response.data);
	var table = document.createElement('table');
	var th = document.createElement('tr');
	var thd = document.createElement('th');
	$(thd).attr("colspan",2);
	$(thd).append(response.title);
	$(th).append(thd);
	$(table).append(th);
	$.each(data.items, function(i,row) {
		var tr = document.createElement('tr');
		var td1 = document.createElement('td');
		var td2 = document.createElement('td');
		$(td1).append(row.label);
		$(td2).append(row.value);
		$(tr).append(td1);
		$(tr).append(td2);
		$(table).append(tr);
	});
	$(target).append(table);
}

function displayGrowth(response,target,url) {
	var data = $.parseJSON(response.data);
	var container = document.createElement('div');
	$(container).attr("class","chart active");
	$(target).append(container);
	var d = google.visualization.arrayToDataTable(data);
	d.setColumnProperty(2, "type", "string");
	d.setColumnProperty(2, "role", "tooltip");
//	d.setColumnProperty(2, "p", {'html': true});
	d.setColumnProperty(4, "type", "string");
	d.setColumnProperty(4, "role", "tooltip");
//	d.setColumnProperty(4, "p", {'html': true});

    var options = {
	    	title: response.title,
	    	'width': 1190,
	        'height':300,
//	        tooltip: {isHtml: true},
	    	annotation: { j : {style: 'line'} },
	        titleTextStyle: {
	        	fontSize: 18,
	        	bold: true
	        },
	        chartArea: {
	        	top: 80,
	        	width: '70%'
	        }
    };

    var chart = new google.visualization.LineChart(container);
    chart.draw(d, options);
	var toolbar = document.createElement('div');
	$(toolbar).addClass("toolbar");
	$(toolbar).attr("id","growth_toolbar");
	$(target).append(toolbar);
    drawToolbar(toolbar,url);
}

function displayPosPie(response,target,url) {
	var data = $.parseJSON(response.data);
	var container = document.createElement('div');
	$(container).attr("class","chart active");
	$(target).append(container);
	var d = google.visualization.arrayToDataTable(data);
	
    var options = {
    	title: response.title,
    	'width': 400,
        'height':300,
        colors: colors,
        chartArea: {
        	top: 80,
        	width: '80%',
        	height: '80%'
        },
        titleTextStyle: {
        	fontSize: 18,
        	bold: true
        }
    };

    var chart = new google.visualization.PieChart(container);
    chart.draw(d, options);
	var toolbar = document.createElement('div');
	$(toolbar).addClass("toolbar");
	$(toolbar).attr("id",target.substring(1)+"_toolbar");
	$(target).append(toolbar);
    drawToolbar(toolbar,url);
}

function loadPosData(doc,target) {
	for (var i = 0; i < pos.length; i++) {
		var p = pos[i];
		var c = colorScale(p);
		var li = document.createElement('li');
		var a = document.createElement('a');
		$(li).css('background-color', "white");
		$(li).css('color', c);
		$(a).html("<h3 style='font-weight: bold;'>"+p+"</h3>");
		$(a).attr("href","#"+p);
		$(li).append(a);
		var content = document.createElement('div');
		$(content).addClass("pos_tab");
		$(content).attr("class","pos_tab row large-16");
		$(content).attr("id",p);
		var left = document.createElement('div');
		$(left).attr("class","columns large-3 left");
		$(left).html("<br /><br /><div class='poslink'><a class='active' href='#"+p+"_right_top10'>Top 10</a></div><br /><div class='poslink'><a href='#"+p+"_right_hist'>Histogram</a></div>");
		var right1 = document.createElement('div');
		$(right1).attr("class","columns large-13 right top10");
		$(right1).attr("id",p+"_right_top10");
		var right2 = document.createElement('div');
		$(right2).attr("class","columns large-13 right histogram");
		$(right2).attr("id",p+"_right_hist");
		
		if (i == 0) {
			$(li).addClass("active");
			$(li).css('background-color', c);
			$(li).css('color', "white");
			$(content).addClass("active");
			$(right1).addClass("active");
		}
		$(content).append(left);
		$(content).append(right1);
		$(content).append(right2);
		$("#pos_nav").append(li);
		$("#pos_data").append(content);
		
		var params1 = "freqlist&max=10&pos="+p+"&docpid="+doc;
		var params2 = "histogram&pos="+p+"&docpid="+doc;
		getPosData("document", params1, createPosFreqlist, "#"+p+"_right_top10", p, c);
		getPosData("document", params2, createPosHistogram, "#"+p+"_right_hist", p, c);
	}
}

function createPosFreqlist(target, response, p, color, url) {
	var d = $.parseJSON(response.data);
	if (d.length == 1) {
		var container = document.createElement('div');
		$(container).attr("class","chart nodata");
		$(container).attr("id",p+"_freq");
		$(container).text("No data");
		$(target).append(container);
	} else {
		var container = document.createElement('div');
		$(container).attr("class","chart active");
		$(container).attr("id",p+"_freq");
		$(target).append(container);
		var data = google.visualization.arrayToDataTable(d);
		var options = {
			    	'width':900,
			        'height':300,
			        legend: {position: 'none'},
		          colors: [color]
		        };
		var chart = new google.visualization.BarChart(container);
	    chart.draw(data, options);
		var toolbar = document.createElement('div');
		$(toolbar).addClass("toolbar");
		$(toolbar).attr("id",target.substring(1)+"_toolbar");
		$(target).append(toolbar);
	    drawToolbar(toolbar,url);
	}
}

function createPosHistogram(target, response, p, color, url) {
	var d = $.parseJSON(response.data);
	if (d.length == 1) {
		var container = document.createElement('div');
		$(container).attr("class","chart nodata");
		$(container).attr("id",p+"_hist");
		$(container).text("No data");
		$(target).append(container);
	} else {
		var container = document.createElement('div');
		$(container).attr("class","chart");
		$(container).attr("id",p+"_hist");
		$(target).append(container);
		var data = google.visualization.arrayToDataTable(d);
		var options = {
			    	'width':900,
			        'height':300,
			        legend: {position: 'none'},
		          colors: [color]
		        };
		var chart = new google.visualization.Histogram(container);
	    chart.draw(data, options);
		var toolbar = document.createElement('div');
		$(toolbar).addClass("toolbar");
		$(toolbar).attr("id",target.substring(1)+"_toolbar");
		$(target).append(toolbar);
	    drawToolbar(toolbar,url);
	}
}
