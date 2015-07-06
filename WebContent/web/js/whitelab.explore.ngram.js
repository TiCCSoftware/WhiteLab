Whitelab.explore.ngram = {
	query : "",
	filterQuery : "",
	first : 0,
	number : 50,
	group_by : "word",
	sort : "",
	view : 10,
	params : "",
	size : 5,
	
	cqlToInterface : function(cql,size,group) {
		cql = cql.substr(1);
		var parts = cql.split('[');
		var k = 0;
		var set = [];
		if (parts.length == size) {
			set = parts;
		} else {
			for (var i = 0; i < parts.length; i++) {
				var part = parts[i];
				var quant = part.substring(part.indexOf(']')+1);
				if (quant.length > 0) {
					var q = parseInt(quant.substring(1,2));
					part = part.substring(0,part.indexOf(']')+1);
					for (var k = 0; k < q; k++) {
						set.push(part);
					}
				} else {
					set.push(part);
				}
			}
		}
		for (var i = 0; i < set.length; i++) {
			var part = set[i];
			var val = part.substring(0,part.indexOf(']'))
			var type = "word";
			var input = "";
			if (val.length > 0) {
				var v = val.split('=');
				type = v[0];
				input = v[1].replace(/"/g,'');
			}
			var j = i + 1;
			$("#n"+j+" select.type").val(type);
			$("#n"+j+" input.input").val(input);
		}
		if (size < 5) {
			for (var i = size+1; i <= 5; i++) {
				$('#n'+i+' select.type').prop('disabled', 'disabled');
				$('#n'+i+' input.input').prop('disabled', 'disabled');
			}
		}
		group = group.replace(/hit:/,'');
		$("#ngram-groupSelect").val(group);
	},
	
	displayResult : function(response) {
		if (response != null) {
			if (response.hasOwnProperty("html") && response.html.indexOf("ERROR") > -1) {
				$("#result_ngram").html(response.html);
			} else {
				if (response.hasOwnProperty("html")) {
					$("#result_ngram").html(response.html);
				}
				if (response.hasOwnProperty("groups") && response.hits !== "-1") {
					$("#ngram-count").html(response.groups);
				}
				if (response.hasOwnProperty("hits") && response.hits !== "-1") {
					$("#ngram-hits").html(response.hits);
				}
				if (response.hasOwnProperty("docs") && response.docs !== "-1") {
					$("#ngram-docs").html(response.docs);
				}
				if (Whitelab.explore.ngram.filterQuery.length > 0) {
					$("#ngram-filter").html(Whitelab.explore.ngram.filterQuery);
				}
			}
		} else {
			$("#result_ngram").html("ERROR - Failed to retrieve result from server.");
		}
	},
	
	composeQuery : function() {
		Whitelab.explore.ngram.setDefaults();
		Whitelab.explore.ngram.query = Whitelab.explore.ngram.parseQuery();
		Whitelab.explore.ngram.group_by = $("#ngram-groupSelect").val();
		Whitelab.debug("Whitelab.explore.ngram.group_by: "+Whitelab.explore.ngram.group_by);
		if (Whitelab.hasOwnProperty("meta"))
			Whitelab.explore.ngram.filterQuery = Whitelab.meta.parseQuery();
		if (Whitelab.search.within != null && Whitelab.search.within == "paragraph") {
			Whitelab.explore.ngram.query += " within (<p/>|<event/>)";
		} else if (Whitelab.search.within != null && Whitelab.search.within == "sentence") {
			Whitelab.explore.ngram.query += " within <s/>";
		}
		Whitelab.explore.ngram.setQueryDetails();
		Whitelab.explore.ngram.setSearchParams();
		return Whitelab.explore.ngram.params;
	},
	
	reset : function() {
		$("#ngram #nsize").val("5");
		$("#ngram select.type").prop('disabled', false);
		$("#ngram input.input").prop('disabled', false);
		$("#ngram select.type").val("word");
		$("#ngram-groupSelect").val("word");
		$("#ngram .input").replaceWith("<input class=\"input\" type=\"text\" placeholder=\"&lt;any&gt;\" />");
		$("#ngram .input").val("");
		$("#ngram div.info").addClass("hidden");
		$("#result_ngram").html("");
		Whitelab.explore.ngram.setDefaults();
		if (Whitelab.hasOwnProperty("meta"))
			Whitelab.meta.reset();
	},
	
	parseQuery : function() {
		var parts = new Array();
		Whitelab.explore.ngram.size = $("#nsize").val();
		for (var i = 1; i <= Whitelab.explore.ngram.size; i++) {
			var value = $("#ngram td#n"+i+" .input").val();
			var type = $("#n"+i+" select.type option:selected").val();
			if (value.length == 0) {
				parts.push("[]");
			} else {
				parts.push("["+encodeURIComponent(type+"=\""+value)+"\"]");
			}
		}
		var query = parts.join("");
		query = query.replace("[][][][][]","[]{5}");
		query = query.replace("[][][][]","[]{4}");
		query = query.replace("[][][]","[]{3}");
		query = query.replace(/\[\]\[\]/g,"[]{2}");
		query = query.replace("[][","[]{1}[");
		if (query.substring(query.length-2) === "[]")
			query = query+"{1}";
		
		return query;
	},
	
	setDefaults : function() {
		Whitelab.explore.ngram.query = "";
		Whitelab.explore.ngram.filterQuery = "";
		Whitelab.explore.ngram.first = 0;
		Whitelab.explore.ngram.number = 50;
		Whitelab.explore.ngram.group_by = "word";
		Whitelab.explore.ngram.sort = "";
		Whitelab.explore.ngram.view = 10;
		Whitelab.explore.ngram.params = "";
		Whitelab.explore.ngram.size = 5;
	},
	
	setInput : function(td) {
		var val = $(td).find("select.type").val();
		Whitelab.debug("Whitelab.explore.ngram.setInput value: "+val);
		var input = $(td).find(".input")[0];
		var textInput = $(td).find("input.input");
		if (val === "pos" && Whitelab.explore.options != null && textInput.length > 0) {
			$(input).replaceWith(Whitelab.explore.options);
		} else if (textInput.length == 0) {
			$(input).replaceWith("<input class=\"input\" type=\"text\" placeholder=\"&lt;any&gt;\" />");
		}
	},
	
	setNgramBoxes : function() {
		var max = $("#nsize").val();
		Whitelab.debug("setNgramBoxes("+max+")");
		for (var i = 1; i <= 5; i++) {
			if (i > max) {
				$("#ngram td#n"+i+" select.type").val("word");
				$("#ngram td#n"+i+" .input").val("");
				$("#ngram td#n"+i+" select.type").prop('disabled', true);
				$("#ngram td#n"+i+" .input").prop('disabled', true);
			} else {
				$("#ngram td#n"+i+" select.type").prop('disabled', false);
				$("#ngram td#n"+i+" .input").prop('disabled', false);
			}
		}
	},
	
	setQueryDetails : function() {
		$("#ngram div.info").removeClass("hidden");
		$("#ngram-query").html(decodeURIComponent(Whitelab.explore.ngram.query));
		if (Whitelab.explore.ngram.filterQuery.length > 0)
			$("#ngram-filter").html(Whitelab.explore.ngram.filterQuery);
		$("#ngram-size").html(Whitelab.explore.ngram.size);
		$("#ngram-group").html(Whitelab.explore.ngram.group_by);
	},
	
	setSearchParams : function() {
		Whitelab.explore.ngram.params = "query=" + encodeURIComponent(Whitelab.explore.ngram.query) 
		+ "&view=" + Whitelab.explore.ngram.view
		+ "&sort=" + Whitelab.explore.ngram.sort
		+ "&first=" + Whitelab.explore.ngram.first
		+ "&group=hit:" + Whitelab.explore.ngram.group_by
		+ "&number=" + Whitelab.explore.ngram.number;
		
		if (Whitelab.explore.ngram.filterQuery.length > 0)
			Whitelab.explore.ngram.params = Whitelab.explore.ngram.params + "&" + Whitelab.explore.ngram.filterQuery;
		
		Whitelab.debug("search ngram params set to: "+Whitelab.explore.ngram.params);
	}
};