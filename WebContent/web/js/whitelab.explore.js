Whitelab.explore = {
	tab : "treemap",
	treemap : null,
	options : null,
	
	getTreemapData : function(field) {
		var params = "field="+field;
		Whitelab.debug(Whitelab.baseUrl+"treemap");
		var xhr = Whitelab.createRequest('POST', Whitelab.baseUrl+"treemap");
		if (!xhr) {
			return;
		}
		
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		xhr.onload = function() {
			if (/^[\],:{}\s]*$/.test(xhr.responseText.replace(/\\["\\\/bfnrtu]/g, '@').
					replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
					replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
				var resp = JSON.parse(xhr.responseText);
				Whitelab.explore.treemap.buildTreemap(resp.data);
			}
		};

		xhr.onerror = function() {
		};

		xhr.send(params);
	},
	
	document : {
		
		display : function(response) {
			if (response.hasOwnProperty("data"))
				$("#docdisplay").html(response.data);
			else
				$("#docdisplay").html(response.message);
		},
		
		load : function(id,params) {
			params = "docpid="+id+"&type=explore"+params;
			params = params.replace(/ /g,"%20");
			$("#docdisplay").html("<p>Loading document "+$("#docpid").val()+"...</p>");
			
			var xhr = Whitelab.createRequest('POST', Whitelab.baseUrl+"document");
			if (!xhr) {
				return;
			}
			
			Whitelab.debug(params);
			
			xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
			
			xhr.onload = function() {
				var resp = JSON.parse(xhr.responseText);
				Whitelab.explore.document.display(resp);
			};

			xhr.onerror = function() {
				$("#docdisplay").html("<p>ERROR - Could not connect to server.</p>");
			};

			xhr.send(params);
		},
		
		reset : function() {
			$("#docpid").val("");
			$("#docdisplay").html("");
		},
		
		switchTab : function(target) {
			$("#document .doc_link").removeClass("active");
			$("#document .doc-pane").removeClass("active");
			$("#document #"+target+"_link").addClass("active");
			$("#document #"+target+"_tab").addClass("active");

			if (target === "cloud") {
				generateCloud("#docCloudDisplay", $("#docCloudSettings > input.split").is(':checked'));
			}
		}
	},
	
	setSizes : function() {
		var h = $(window).innerHeight() - 65;
		$("#explorepage").css("height",h+"px");
	},
	
	showDocument : function(docId) {
		$("#doc-link").click();
		$("#docpid").val(docId);
		$("#document button.search").click();
	},

	switchTab : function(target) {
		Whitelab.debug("switching to explore tab "+target+" from "+Whitelab.explore.tab);
		if (target !== Whitelab.explore.tab) {
			$("#explore .content").removeClass("active");
			$("#"+target).addClass("active");
			
			if (target === "ngram" || target === "stats") {
				$("#metadata").show();
				$("#explore-meta").addClass("active");
			} else {
				$("#metadata").hide();
			}
			
			Whitelab.explore.tab = target;
		}
	}
};