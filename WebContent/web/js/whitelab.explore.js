Whitelab.explore = {
	tab : "treemap",
	treemap : null,
	options : null,
	
	getTreemapData : function(field) {
		var params = "field="+field;
		Whitelab.debug(Whitelab.baseUrl+"page/treemap");
		var xhr = Whitelab.createRequest('POST', Whitelab.baseUrl+"page/treemap");
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
		reset : function() {
			$("#docpid").val("");
			$("#docdisplay").html("");
		}
	},
	
	setSizes : function() {
		var h = $(window).innerHeight() - 65;
		$("#explorepage").css("height",h+"px");
	}
};