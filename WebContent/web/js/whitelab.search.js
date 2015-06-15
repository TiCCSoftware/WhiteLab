Whitelab.search = {
	queryCount : 0,
	tab : "simple",
	params : "",
	query : "",
	filterQuery : "",
	within : "document",
	group_by : "",
	view : 1,
	sort : "",
	error : false,
	first : 0,
	number : 50,
	
	execute : function(tab) {
		Whitelab.search.setDefaults();
		var from = 1;
		if (typeof tab === 'undefined')
			tab = Whitelab.search.tab;
		
		if (Whitelab.search.tab === "simple") {
			var term = $("#simple").find("input[type='text']").val();
			var terms = term.split(" ");
			for (var i = 0; i < terms.length; i++) {
				if (terms[i].length > 0) {
					var sub = terms[i].substring(0,2);
					if (sub === '[]') {
						if (terms[i].indexOf('{,') > -1) {
							terms[i] = terms[i].replace('{,','{0,');
						}
						Whitelab.search.query = Whitelab.search.query + terms[i];
					} else {
						Whitelab.search.query = Whitelab.search.query + "[(word=\""+terms[i]+"\")]";
					}
				}
			}
			if (Whitelab.search.query.length == 0 || term === '[]') {
				console.log("1");
				Whitelab.search.error = true;
//			} else {
//				Whitelab.search.params = "query="+Whitelab.search.query;
			}
//			Whitelab.search.simple.reset();
		} else {
			if (Whitelab.hasOwnProperty("meta"))
				Whitelab.search.filterQuery = Whitelab.meta.parseQuery();
			
			if (Whitelab.search.tab === "extended") {
				from = 2;
				Whitelab.search.query = Whitelab.search.extended.parseQuery();
//				Whitelab.search.extended.reset();
			} else if (Whitelab.search.tab === "advanced") {
				from = 3;
				Whitelab.search.query = Whitelab.search.advanced.parseQuery();
//				Whitelab.search.advanced.reset();
			} else if (Whitelab.search.tab === "expert") {
				from = 4;
				Whitelab.search.query = $("#querybox").val();
//				Whitelab.search.expert.reset();
			}
			
			if (Whitelab.search.query.length == 0 || Whitelab.search.query.indexOf('{,') > -1) {
				console.log("QUERY: "+Whitelab.search.query);
				Whitelab.search.query = "";
				Whitelab.search.error = true;
				console.log("2");
			}
			
			Whitelab.search.query = Whitelab.search.query.replace(/ /g,'');
			
			if (Whitelab.search.within === "sentence") {
				Whitelab.debug("within sentence");
				Whitelab.search.query = Whitelab.search.query + " within <s/>";
			} else if (Whitelab.search.within === "paragraph") {
				Whitelab.debug("within paragraph");
				Whitelab.search.query = Whitelab.search.query + " within (<p/>|<event/>)";
			}
//			if (Whitelab.hasOwnProperty("meta"))
//				Whitelab.meta.reset();
		}
		if (Whitelab.search.error) {
			alert("Invalid query");
			return false;
		} else {
			var qString = "query="+Whitelab.search.query.replace(/\&/g,'%26')+"&from="+from;
			if (Whitelab.search.filterQuery.length > 0)
				qString = qString+"&"+Whitelab.search.filterQuery;
			
			if (Whitelab.search.tab !== "simple" && Whitelab.search.query.indexOf(";") > -1) {
				qString = qString+"&batch=true";
			}
			
			return qString;
			
//			if (Whitelab.search.tab !== "simple" && Whitelab.search.query.indexOf(";") > -1) {
//				var qq = Whitelab.search.query.split(";");
//				for (var q = 0; q < qq.length; q++) {
//					Whitelab.search.query = qq[q];
//					Whitelab.search.queryCount++;
//					Whitelab.search.setSearchParams(null);
//					Whitelab.search.result.addQuery(Whitelab.search.queryCount,tab);
//					Whitelab.search.switchTab("result");
//					Whitelab.getData(Whitelab.search.params, Whitelab.search.result.displayResult,Whitelab.search.queryCount);
//				}
//			} else {
//				Whitelab.search.queryCount++;
//				Whitelab.search.setSearchParams(null);
//				Whitelab.search.result.addQuery(Whitelab.search.queryCount,tab);
//				Whitelab.search.switchTab("result");
//				Whitelab.getData(Whitelab.search.params, Whitelab.search.result.displayResult,Whitelab.search.queryCount);
//			}
		}
	},
	
	doExport : function(id) {
		var n = 0;
		if (Whitelab.search.view == 1 || Whitelab.search.view == 8)
			n = $("#hits_"+id).html();
		else
			n = $("#docs_"+id).html();
		
		if ((Whitelab.search.view == 1 || Whitelab.search.view == 2) && n > Whitelab.exportLimit) {
			ask = Whitelab.confirmExport();
		} else {
			ask = true;
		}
		
		if (ask) {
			Whitelab.search.view = $("#result_"+id).find(".current-view").val();
			Whitelab.search.query = $("#query_"+id).html();
			Whitelab.search.within = $("#within_"+id).html();
            if (Whitelab.search.within === "paragraph") {
                Whitelab.search.query += " within (<p/>|<event/>)";
            } else if (Whitelab.search.within === "sentence") {
                Whitelab.search.query += " within <s/>";
            }
			Whitelab.search.query = Whitelab.search.query.replace(/amp;/g,"");
			Whitelab.debug("export query: "+Whitelab.search.query);
			Whitelab.search.group_by = $("#group_"+id).html();
			if (Whitelab.search.group_by === "-")
				Whitelab.search.group_by = "";
			Whitelab.search.first = 0;
			Whitelab.search.number = n;
			Whitelab.search.filters = new Array();
			Whitelab.search.filterQuery = "";
			if ($("#filter_"+id).html().length > 0) {
				var f = $("#filter_"+id).html();
				f = f.replace(/field\:/g,"");
				Whitelab.search.filters.push(f);
				Whitelab.search.filterQuery = f;
			}
			Whitelab.search.setSearchParams(id);
			
			params = Whitelab.search.params.replace(/ /g,"%20");
	
			window.location = Whitelab.baseUrl + "export?id="+id+"&"+params;
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
		Whitelab.search.within = "document";
		Whitelab.search.group_by = "";
		Whitelab.search.view = 1;
		Whitelab.search.first = 0;
		Whitelab.search.number = 50;
		Whitelab.search.sort = "";
		Whitelab.search.error = false;
	},
	
	setSearchParams : function(id) {
		if (id == null) {
			id = Whitelab.search.queryCount;
		}
		
		Whitelab.search.params = "query=" + encodeURIComponent(Whitelab.search.query)
		+ "&view=" + Whitelab.search.view 
		+ "&id=" + id
		+ "&sort=" + Whitelab.search.sort
		+ "&first=" + Whitelab.search.first
		+ "&group_by=" + Whitelab.search.group_by
		+ "&number=" + Whitelab.search.number;
		
		if (Whitelab.search.filterQuery.length > 0)
			Whitelab.search.params = Whitelab.search.params + "&" + Whitelab.search.filterQuery;
		
		Whitelab.debug("search params set to: "+Whitelab.search.params);
	},
	
	setSizes : function() {
		var sh = ($(window).innerHeight() - 135) / 2 - 130;
		if (sh < 100) {
			sh = 100;
		}
		$("#simple").css("margin-top",sh+"px");
	},
	
	simple : {
		parseQueryToInterface : function(query) {
			var n = query.indexOf("[");
			var qq = [];
			while (n > -1) {
				var m = query.indexOf("]",n);
				var qe = query.substring(n,m+1);
				if (m-n > 1) {
					var q1 = qe.indexOf('"');
					qe = qe.substring(q1+1);
					var q2 = qe.indexOf('"');
					qe = qe.substring(0,q2);
					if (qe.indexOf('(?i)') == 0) {
						qe = qe.substring(4);
					} else if (qe.indexOf('(?-i)') == 0) {
						qe = qe.substring(5);
					}
				}
				n = query.indexOf("[",m);
				if (n > m+1) {
					var qa = query.substring(m+1,n);
					qq.push(qe+qa);
				} else if (n == -1 && query.length > m+1) {
					var qa = query.substring(m+1);
					qq.push(qe+qa);
				} else {
					qq.push(qe);
				}
			}
			$("#simple").find("input[type='text']").first().val(qq.join(" "));
		},
		
		reset : function() {
			$("#simple").find("input[type='text']").first().val("");
		}
	},
	
	switchTab : function(target) {
		Whitelab.debug("switching to search tab "+target+" from "+Whitelab.search.tab);
		if (target !== Whitelab.search.tab) {
			$("#search .content").removeClass("active");
			$("#"+target).addClass("active");
			$("#subnav dd").removeClass("active");
			$("#"+target+"_link").addClass("active");
			if (target === "extended" || target === "advanced" || target === "expert") {
				$("#metadata").show();
			} else {
				$("#metadata").hide();
			}
			
			if (target === "result") {
				$(".sub-nav dd").removeClass("active");
				$("#result_link").removeClass("hide");
				$("#result_link").addClass("active");
			} else if (target === "document") {
				$(".sub-nav dd").removeClass("active");
				$("#document").removeClass("hide");
				$("#document_link").removeClass("hide");
				$("#document_link").addClass("active");
			}
			
			if (!$("#result_link").hasClass("hide") || !$("#document_link").hasClass("hide")) {
				$("#link-spacer").removeClass("hide");
			}
			
			Whitelab.search.tab = target;
		}
	},
	
	update : function(id, params) {
		Whitelab.search.setDefaults();
		Whitelab.search.query = $("#query_"+id).html().replace(/amp;/g,"");
		Whitelab.debug("Updating query: "+Whitelab.search.query);
		Whitelab.search.filterQuery = $("#filter_"+id).html().replace(/field\:/g,"").replace(/amp;/g,"");
		
		Whitelab.debug("update filter query: "+Whitelab.search.filterQuery);

		Whitelab.search.within = $("#within_"+id).html();
		if (Whitelab.search.within === "paragraph") {
			Whitelab.search.query += " within (<p/>|<event/>)";
		} else if (Whitelab.search.within === "sentence") {
			Whitelab.search.query += " within <s/>";
		}
		Whitelab.search.group_by = $("#group_"+id).html();
		Whitelab.search.view = $("#result_"+id+" .current-view").val();
		if (Whitelab.search.group_by === "-")
			Whitelab.search.group_by = "";
		
		var viewChanged = false;
		
		$.each(params, function(k, v) {
			if (Whitelab.search.hasOwnProperty(k)) {
				Whitelab.search[k] = v;
				Whitelab.debug("Whitelab.search."+k+": "+Whitelab.search[k]);
				if (k === "view") {
					viewChanged = true;
				}
			}
		});
		
		if (viewChanged && Whitelab.search.view > 2 && Whitelab.search.group_by.length == 0)
			Whitelab.search.query = "";
		else if (viewChanged && Whitelab.search.view <= 2) {
			Whitelab.search.group_by = "";
			$("#group_"+id).html("");
		}
		
		Whitelab.search.setSearchParams(id);
		$("#status_"+id).html("<img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> COUNTING");
		Whitelab.getData(Whitelab.search.params, Whitelab.search.result.displayResult,id);
	}
};