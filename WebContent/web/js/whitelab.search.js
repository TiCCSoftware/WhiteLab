Whitelab.search = {
	queryCount : 0,
	tab : "simple",
	params : "",
	query : "",
	filterQuery : "",
	within : "",
	group_by : "",
	view : 1,
	sort : "",
	error : false,
	first : 0,
	number : 50,
	from : 1,
	
	composeQuery : function(tab) {
		Whitelab.search.setDefaults();
		if (typeof tab === 'undefined')
			tab = Whitelab.search.tab;
		
		if (tab === "simple") {
			var term = $('#simple-input > input').val();
			Whitelab.search.query = Whitelab.search.simpleStringToCQL(term,false);
			if (Whitelab.search.query.length == 0 || term === '[]') {
				Whitelab.search.error = true;
			}
		} else {
			if (Whitelab.hasOwnProperty("meta"))
				Whitelab.search.filterQuery = Whitelab.meta.parseQuery();
			
			if (Whitelab.search.tab === "extended") {
				Whitelab.search.from = 2;
				Whitelab.search.query = Whitelab.search.extended.parseQuery();
			} else if (Whitelab.search.tab === "advanced") {
				Whitelab.search.from = 3;
				Whitelab.search.query = Whitelab.search.advanced.parseQuery();
			} else if (Whitelab.search.tab === "expert") {
				Whitelab.search.from = 4;
				Whitelab.search.query = $("#querybox").val();
			}
			
			Whitelab.search.query.replace(/\{,/g,'{1,');
			if (Whitelab.search.query.length == 0) {
				Whitelab.search.query = "";
				Whitelab.search.error = true;
			}
		}
		if (Whitelab.search.error) {
			alert("Invalid query");
			return false;
		} else {
			var q = "query=" + encodeURIComponent(Whitelab.search.query)
			+ "&within=" + Whitelab.search.within 
			+ "&view=" + Whitelab.search.view 
			+ "&sort=" + Whitelab.search.sort
			+ "&first=" + Whitelab.search.first
			+ "&group=" + Whitelab.search.group_by
			+ "&number=" + Whitelab.search.number
			+ "&from=" + Whitelab.search.from;
			
			if (Whitelab.search.filterQuery.length > 0)
				q = q+"&"+Whitelab.search.filterQuery;
			
			if (Whitelab.search.tab !== "simple" && Whitelab.search.query.indexOf(";") > -1)
				q = q+"&batch=true";

			console.log(q);
			
			return q;
		}
	},
	
	simpleStringToCQL : function(str,caseSensitive) {
		var terms = str.split(" ");
		var query = "";
		var c = "";
		if (caseSensitive)
			c = "(?c)";
		for (var i = 0; i < terms.length; i++) {
			if (terms[i].length > 0) {
				var sub = terms[i].substring(0,2);
				if (sub === '[]') {
					if (terms[i].indexOf('{,') > -1) {
						terms[i] = terms[i].replace('{,','{1,');
					}
					query = query + terms[i];
				} else {
					query = query + "[word=\""+c+terms[i]+"\"]";
				}
			}
		}
		return query;
	},
	
	simpleStringToCQL_withGroup : function(str,group,orig,caseSensitive) {
		var def = ["hit:word","hit:lemma","hit:pos","word","lemma","pos"];
		if (def.indexOf(group) > -1 || group.indexOf("word") == 0) {
			var terms = str.split(" ");
			var query = "";
			var c = "";
			if (caseSensitive)
				c = "(?c)";
			for (var i = 0; i < terms.length; i++) {
				if (terms[i].length > 0) {
					var sub = terms[i].substring(0,2);
					if (sub === '[]') {
						if (terms[i].indexOf('{,') > -1) {
							terms[i] = terms[i].replace('{,','{1,');
						}
						query = query + terms[i];
					} else if (group.indexOf("word") == 0) {
						var p = group.split(":");
						var t = def[def.indexOf(p[1])];
						query = query + "["+t+"=\""+c+terms[i].replace(/\(/g,"\\(").replace(/\)/g,"\\)").replace(/\./g,"\\.")+"\"]";
						if (p[0] == "wordleft")
							query = query+orig;
						else
							query = orig+query;
					} else {
						var t = def[def.indexOf(group)];
						t = t.replace("hit:","");
						query = query + "["+t+"=\""+c+terms[i].replace(/\(/g,"\\(").replace(/\)/g,"\\)").replace(/\./g,"\\.")+"\"]";
					}
				}
			}
			return query;
		} else {
			group = group.replace("field:","");
			return orig+"&"+group+"=\""+str+"\"";
		}
	},
	
	expert : {
		reset : function() {
			$("#querybox").val("");
		},
		
		toggleInfoBox : function() {
			$(document).find("#expert #cql_info").toggleClass("active");
		}
	},
	
	removeQuantifier : function (boxval) {
		var regex = /(\{\d*,*\d*\}$)/;
		var match = regex.exec(boxval);
		if (match != null) {
			var res = boxval.replace(match[0],"");
			return res;
		} else {
			return boxval;
		}
	},
	
	removeValue : function (boxval) {
		var regex = /(\{\d*,*\d*\}$)/;
		var match = regex.exec(boxval);
		if (match != null) {
			return match[0];
		} else {
			return null;
		}
	},
	
	reset : function() {
		Whitelab.search.simple.reset();
		Whitelab.search.extended.reset();
		Whitelab.search.advanced.reset();
		Whitelab.search.expert.reset();
		if (Whitelab.hasOwnProperty("meta"))
			Whitelab.meta.reset();
	},
	
	setDefaults : function() {
		Whitelab.search.params = "";
		Whitelab.search.query = "";
		Whitelab.search.filterQuery = "";
		Whitelab.search.within = "";
		Whitelab.search.group_by = "";
		Whitelab.search.view = 1;
		Whitelab.search.first = 0;
		Whitelab.search.number = 50;
		Whitelab.search.sort = "";
		Whitelab.search.error = false;
		Whitelab.search.from = 1;
	},
	
	setSizes : function() {
		var sh = ($(window).innerHeight() - 135) / 2 - 130;
		if (sh < 100) {
			sh = 100;
		}
		$("#simple").css("margin-top",sh+"px");
	},
	
	simple : {
		reset : function() {
			$("#simple").find("input[type='text']").first().val("");
		}
	}
};