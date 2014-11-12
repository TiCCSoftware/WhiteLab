Whitelab.explore.statistics = {
	query : "[]",
	filterQuery : "",
	first : 0,
	number : 50,
	group_by : "word",
	sort : "",
	view : 12,
	params : "",
	cloud : null,
	tab : "result_list",
	detailsSet : false,
	hitsLoaded : false,
	docsLoaded : false,
	docCount : 0,
	
	// variables for vocab growth chart
	chart : null,
	chartData : null,
	chartDataOffset : 0,
	chartOptions : null,
	typesSeen : {},
	lemmasSeen : {},
	tokensProcessed : 0,
	vocabLoaded : false,
	vocabDocOffset : -1,
	
	addVocabData : function(response) {
		Whitelab.debug("addVocabData");
		if (response != null && response.hasOwnProperty("data")) {
			Whitelab.debug("doc hits: "+response.data.length);
			for (var i = 1; i < response.data.length; i++) { // skip zero line
				Whitelab.explore.statistics.chartDataOffset++;
				var hit = response.data[i];
				var token = hit[2];
				if (!(token in Whitelab.explore.statistics.typesSeen))
					Whitelab.explore.statistics.typesSeen[token] = 1;
				var lemma = hit[4];
				if (!(lemma in Whitelab.explore.statistics.lemmasSeen))
					Whitelab.explore.statistics.lemmasSeen[lemma] = 1;
				if (Whitelab.language === "nl") {
					Whitelab.explore.statistics.chartData.addRow([Whitelab.explore.statistics.chartDataOffset,Object.keys(Whitelab.explore.statistics.typesSeen).length,
					                                              "token: "+Whitelab.explore.statistics.chartDataOffset+"\nwoordvorm: "+token+"\nunieke woordvormen: "+Object.keys(Whitelab.explore.statistics.typesSeen).length,
					                                              Object.keys(Whitelab.explore.statistics.lemmasSeen).length,
					                                              "token: "+Whitelab.explore.statistics.chartDataOffset+"\nlemma: "+lemma+"\nunieke lemmas: "+Object.keys(Whitelab.explore.statistics.lemmasSeen).length]);
				} else {
					Whitelab.explore.statistics.chartData.addRow([Whitelab.explore.statistics.chartDataOffset,Object.keys(Whitelab.explore.statistics.typesSeen).length,
					                                              "token: "+Whitelab.explore.statistics.chartDataOffset+"\ntype: "+token+"\nunique types: "+Object.keys(Whitelab.explore.statistics.typesSeen).length,
					                                              Object.keys(Whitelab.explore.statistics.lemmasSeen).length,
					                                              "token: "+Whitelab.explore.statistics.chartDataOffset+"\nlemma: "+lemma+"\nunique lemmas: "+Object.keys(Whitelab.explore.statistics.lemmasSeen).length]);
				}
				
				Whitelab.explore.statistics.tokensProcessed++;
				
			}
			Whitelab.explore.statistics.chart.draw(Whitelab.explore.statistics.chartData,Whitelab.explore.statistics.chartOptions);
			$("#growth div.loading").addClass("hidden");
		}
	},
	
	clearData : function() {
		Whitelab.explore.statistics.hitsLoaded = false;
		Whitelab.explore.statistics.docsLoaded = false;
		Whitelab.explore.statistics.vocabLoaded = false;
		Whitelab.explore.statistics.detailsSet = false;
		Whitelab.explore.statistics.cloud = null;
		Whitelab.explore.statistics.chart = null;
		Whitelab.explore.statistics.chartData = null;
		Whitelab.explore.statistics.chartDataOffset = 0;
		Whitelab.explore.statistics.chartOptions = null;
		Whitelab.explore.statistics.typesSeen = {};
		Whitelab.explore.statistics.lemmasSeen = {};
		Whitelab.explore.statistics.tokensProcessed = 0;

		$("#stats div.info span > div").html("");
		$("#stats div.info").addClass("hidden");
		Whitelab.explore.statistics.switchTab('result_list');
		$("#result_statistics").addClass("hidden");
		$("#result_status").addClass("hidden");
		$("#result_status").html("");
		$("#result_list").html("");
		$("#result_doclist").html("");
		$("#vocabChartDisplay").html("");
		$("#cloudDisplay").html("");
	},
	
	displayResult : function(response) {
		if (response != null) {
			if (response.hasOwnProperty("html") && response.view == 12) {
				$("#result_list").html(response.html);
				if (response.hasOwnProperty("cloud")) {
					$("#cloudDisplay").html("");
					clearCloud();
					for (var i = 0; i < response.cloud.length; i++) {
						var item = response.cloud[i];
						addNode(item.lemma,item.freq,"BW",item.clean);
					}
					generateCloud("#cloudDisplay", false, true);
				}
				if (response.hasOwnProperty("groups") && response.groups !== "-1") {
					$("#stats-count").html(response.groups);
				}
				if (response.hasOwnProperty("hits") && response.hits !== "-1") {
					$("#stats-hits").html(response.hits);
				}
				if (response.hasOwnProperty("docs") && response.docs !== "-1") {
					Whitelab.explore.statistics.docCount = response.docs;
					$("#stats-docs").html(response.docs);
				}
				if (Whitelab.explore.statistics.filterQuery.length > 0) {
					$("#stats-filter").html(Whitelab.explore.statistics.filterQuery);
				}
				Whitelab.explore.statistics.hitsLoaded = true;
			} else if (response.hasOwnProperty("html") && response.view == 4) {
				$("#result_doclist").html(response.html);
				Whitelab.explore.statistics.docsLoaded = true;
			}
		} else {
			Whitelab.explore.statistics.reset();
			$("#result_status").removeClass("hidden");
			$("#result_status").html("ERROR - No data received!");
		}
	},
	
	doExport : function(type) {
		Whitelab.explore.statistics.group_by = $("#stats-group").html();
		Whitelab.explore.statistics.filterQuery = $("#stats-filter").html();
		Whitelab.explore.statistics.first = 0;
		if (type === "docs") {
			Whitelab.explore.statistics.number = $("#stats-docs").html();
			Whitelab.explore.statistics.view = 4;
		} else {
			Whitelab.explore.statistics.number = $("#stats-count").html();
			Whitelab.explore.statistics.view = 12;
		}
		
		var ask = false;
		if (Whitelab.explore.statistics.number > Whitelab.exportLimit) {
			ask = Whitelab.confirmExport();
		} else {
			ask = true;
		}
		
		if (ask) {
			Whitelab.explore.statistics.setSearchParams();
			var params = Whitelab.explore.statistics.params.replace(/ /g,"%20");
			Whitelab.debug(WHITELAB_BASE_URL + "/page/export?"+params);
			window.location = WHITELAB_BASE_URL + "/page/export?"+params;
		}
	},
	
	execute : function(type) {
		Whitelab.debug("Whitelab.explore.statistics.execute("+type+")");
		Whitelab.explore.statistics.setDefaults();

		if (Whitelab.hasOwnProperty("meta") && !Whitelab.explore.statistics.detailsSet)
			Whitelab.explore.statistics.filterQuery = Whitelab.meta.parseQuery();

		if (Whitelab.explore.statistics.filterQuery.length == 0) {
			if (!Whitelab.hasOwnProperty("tour") || Whitelab.tour.page == null)
				alert("Please define a metadata filter.");
		} else {
			
			if (type === "docs") {
				Whitelab.explore.statistics.view = 4;
				Whitelab.explore.statistics.group_by = "";
			} else {
				if (!Whitelab.explore.statistics.detailsSet) {
					Whitelab.explore.statistics.group_by = $('select#stats-groupSelect').val();
				} else {
					Whitelab.explore.statistics.group_by = $("#stats-group").html();
				}
			}
			
			Whitelab.explore.statistics.setQueryDetails();
			Whitelab.explore.statistics.setSearchParams();
			$("#result_statistics").removeClass("hidden");
			
			if (type === "docs") {
				$("#result_doclist").html("<span class=\"loading\"><img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> LOADING</span>");
				Whitelab.getData(Whitelab.explore.statistics.params,Whitelab.explore.statistics.displayResult,"status");
			} else {
				$("#result_list").html("<span class=\"loading\"><img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> LOADING</span>");
				Whitelab.getData(Whitelab.explore.statistics.params,Whitelab.explore.statistics.displayResult,"status");
			}
		}
	},
	
	getVocabParams : function(offset,number) {
		params = "patt=" + encodeURIComponent(Whitelab.explore.statistics.query)+"&outputformat=json&first="+offset+"&number="+number;
		
		if (Whitelab.explore.statistics.filterQuery.length > 0)
			params = params + "&filter=(" + Whitelab.explore.statistics.filterQuery.replace(/=/g,"%3A").replace(/&/g,"%20AND%20")+")";
		
		Whitelab.debug("search statistics vocab params set to: "+params);
		
		return params;
	},
	
	goToPage : function(type,element,number) {
		var page = $(element).parent().find(".page-select").val();
		var first = (page * number) - number;
		Whitelab.explore.statistics.update(type,{ first : first, number : number });
	},
	
	initializeVocabChart : function() {
		if (Whitelab.language === "nl")
			Whitelab.explore.statistics.chartData = google.visualization.arrayToDataTable([ ["token","Unieke woordvormen","tooltip1","Unieke lemmas","tooltip2"],[0,0,"token: 0\nunieke woordvormen: 0",0,"token: 0\nunieke lemmas: 0"] ]);
		else
			Whitelab.explore.statistics.chartData = google.visualization.arrayToDataTable([ ["token","Unique types","tooltip1","Unique lemmas","tooltip2"],[0,0,"token: 0\nunique types: 0",0,"token: 0\nunique lemmas: 0"] ]);
		Whitelab.explore.statistics.chartData.setColumnProperty(2, "type", "string");
		Whitelab.explore.statistics.chartData.setColumnProperty(2, "role", "tooltip");
		Whitelab.explore.statistics.chartData.setColumnProperty(4, "type", "string");
		Whitelab.explore.statistics.chartData.setColumnProperty(4, "role", "tooltip");
		
		var title = "Vocabulary growth";
		if (Whitelab.language === "nl")
			title = "Vocabulaire groei";

		Whitelab.explore.statistics.chartOptions = {
			title: title,
	    	'width': 1190,
	        'height':300,
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

		// Create and draw the visualization.
		Whitelab.explore.statistics.chart = new google.visualization.LineChart(document.getElementById('vocabChartDisplay'));
		Whitelab.explore.statistics.chart.draw(Whitelab.explore.statistics.chartData,Whitelab.explore.statistics.chartOptions);
	},
	
	loadVocabGrowthData : function() {
		Whitelab.explore.statistics.vocabDocOffset++;
		if (Whitelab.explore.statistics.vocabDocOffset < Whitelab.explore.statistics.docCount) {
			$("#growth div.loading").removeClass("hidden");
			console.log("http://"+window.location.host+"/blacklab-server/opensonar/docs?"+Whitelab.explore.statistics.getVocabParams(Whitelab.explore.statistics.vocabDocOffset,1));
			$.get("http://"+window.location.host+"/blacklab-server/opensonar/docs?"+Whitelab.explore.statistics.getVocabParams(Whitelab.explore.statistics.vocabDocOffset,1), function(data,status) {
				Whitelab.explore.statistics.loadVocabGrowthDataForDoc(data.docs[0].docPid);
			});
		}
	},
	
	loadVocabGrowthDataForDoc : function(docPid) {
		console.log(WHITELAB_BASE_URL + "/page/document?docpid="+docPid+"&growth=bare");
		$.ajax({
		  url: WHITELAB_BASE_URL + "/page/document?docpid="+docPid+"&growth=bare",
		  dataType: "json",
		  type: "GET",
	      contentType: 'application/json; charset=utf-8',
		}).success(function(data) {
			Whitelab.debug("success");
			Whitelab.explore.statistics.addVocabData(data);
		}).error(function(data) {
			Whitelab.debug("error");
		});
	},
	
	reset : function() {
		if (Whitelab.hasOwnProperty("meta"))
			Whitelab.meta.reset();
		$("#stats div.info").addClass("hidden");
		$("#result_statistics").addClass("hidden");
		$("#result_status").addClass("hidden");
		$("#result_status").html("");
		$("#result_list").html("");
		$("#result_doclist").html("");
		$("#vocabChartDisplay").html("");
		$("#cloudDisplay").html("");
		Whitelab.explore.statistics.clearData();
	},
	
	setDefaults : function() {
		Whitelab.explore.statistics.first = 0;
		Whitelab.explore.statistics.number = 50;
		Whitelab.explore.statistics.group_by = "word";
		Whitelab.explore.statistics.sort = "";
		Whitelab.explore.statistics.view = 12;
		Whitelab.explore.statistics.params = "";
	},
	
	setQueryDetails : function() {
		Whitelab.debug("Whitelab.explore.statistics.setQueryDetails");
		if (!Whitelab.explore.statistics.detailsSet) {
			Whitelab.debug("setQueryDetails group_by: "+Whitelab.explore.statistics.group_by);
			$("#stats div.info").removeClass("hidden");
			if (Whitelab.explore.statistics.filterQuery.length > 0)
				$("#stats-filter").html(Whitelab.explore.statistics.filterQuery);
			$("#stats-group").html(Whitelab.explore.statistics.group_by);
			Whitelab.explore.statistics.detailsSet = true;
		}
	},
	
	setSearchParams : function() {
		Whitelab.explore.statistics.params ="query=" + encodeURIComponent(Whitelab.explore.statistics.query)
		+ "&view=" + Whitelab.explore.statistics.view 
		+ "&sort=" + Whitelab.explore.statistics.sort
		+ "&first=" + Whitelab.explore.statistics.first
		+ "&number=" + Whitelab.explore.statistics.number;
		
		if (Whitelab.explore.statistics.view == 12)
			Whitelab.explore.statistics.params = Whitelab.explore.statistics.params + "&group_by=hit:" + Whitelab.explore.statistics.group_by;
		
		if (Whitelab.explore.statistics.filterQuery.length > 0)
			Whitelab.explore.statistics.params = Whitelab.explore.statistics.params + "&" + Whitelab.explore.statistics.filterQuery;
		
		Whitelab.debug("search statistics params set to: "+Whitelab.explore.statistics.params);
	},
	
	switchTab : function(target) {
		if (target != Whitelab.explore.statistics.tab) {
			Whitelab.explore.statistics.tab = target;
			
			$("#stats .tab-link").removeClass("active");
			$("#stats .tab-pane").addClass("hidden");
			$("#stats #"+target+"_link").addClass("active");
			$("#stats #"+target).removeClass("hidden");
			
			if (target === "result_list" && Whitelab.explore.statistics.hitsLoaded == false) {
				Whitelab.explore.statistics.execute("hits");
			} else if (target === "result_doclist" && Whitelab.explore.statistics.docsLoaded == false) {
				Whitelab.explore.statistics.execute("docs");
			} else if (target === 'growth' && !Whitelab.explore.statistics.vocabLoaded) {
				Whitelab.explore.statistics.initializeVocabChart();
				Whitelab.explore.statistics.loadVocabGrowthData();
				Whitelab.explore.statistics.vocabLoaded = true;
			}
		}
	},
	
	update : function(type,params) {
		Whitelab.explore.statistics.setDefaults();
		if (type === "docs") {
			Whitelab.explore.statistics.view = 4;
		}
		Whitelab.explore.statistics.filterQuery = $("#stats-filter").html().replace(/field\:/g,"").replace(/amp;/g,"");
		
		Whitelab.debug("update statistics filter query: "+Whitelab.explore.statistics.filterQuery);
		
		Whitelab.explore.statistics.group_by = $("#stats-group").html();
		
		$.each(params, function(k, v) {
			if (Whitelab.explore.statistics.hasOwnProperty(k)) {
				Whitelab.explore.statistics[k] = v;
				Whitelab.debug("Whitelab.explore.statistics."+k+": "+Whitelab.explore.statistics[k]);
			}
		});
		
		Whitelab.explore.statistics.setSearchParams();
		if (type === "docs") {
			$("#result_doclist").html("<span class=\"loading\"><img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> LOADING</span>");
			Whitelab.getData(Whitelab.explore.statistics.params,Whitelab.explore.statistics.displayResult,"doclist");
		} else {
			$("#result_list").html("<span class=\"loading\"><img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> LOADING</span>");
			Whitelab.getData(Whitelab.explore.statistics.params,Whitelab.explore.statistics.displayResult,"list");
		}
	}
};