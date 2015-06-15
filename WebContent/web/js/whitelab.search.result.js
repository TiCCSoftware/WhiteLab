Whitelab.search.result = {
	ar_loadFrom : new Array(),
	
	addQuery : function(id,tab) {
		var q = Whitelab.search.query;
		var i = q.indexOf(" within");
		if (i > -1)
			q = q.substring(0,i);
		
		if (i > -1) {
            if (q.indexOf("<s/>") > -1) {
                Whitelab.search.within = "sentence";
            } else if (q.indexOf("<p/>") > -1) {
                 Whitelab.search.within = "paragraph";
            }
            q = q.substring(0,i);
         }
		
		var queryRow = "<tr onclick=\"Whitelab.search.result.switchToQuery("+id+")\" id=\"row_"+id+"\" class=\"query-row\">"
			+"<td>"+id+"</td>"
			+"<td id=\"query_"+id+"\">"+q+"</td>"
			+"<td id=\"within_"+id+"\">"+Whitelab.search.within+"</td>"
			+"<td id=\"filter_"+id+"\">"+Whitelab.search.filterQuery+"</td>";
		
		if (Whitelab.search.view == 8 || Whitelab.search.view == 16) {
			queryRow = queryRow+"<td id=\"group_"+id+"\">"+Whitelab.search.group_by+"</td>";
		} else {
			queryRow = queryRow+"<td id=\"group_"+id+"\">-</td>";
		}
			
		queryRow = queryRow+"<td id=\"status_"+id+"\"><img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> RUNNING</td>"
			+"<td id=\"hits_"+id+"\">-</td>"
			+"<td id=\"docs_"+id+"\">-</td>"
			+"<td class=\"control\"><button onclick=\"Whitelab.search.result.editQuery("+id+",'"+tab+"')\" class=\"edit\">EDIT</button>"
			+" <button onclick=\"Whitelab.search.result.removeQuery("+id+")\" class=\"remove\">X</button></td>"
		+"</tr>";
		$("#queries > tbody").append(queryRow);
		
		var resultBox = "<div id=\"result_"+id+"\" class=\"result-content\"></div>";
		$("#results").append(resultBox);
	},
	
	displayResult : function(response,target) {
		var update = false;
		if (response != null) {
			if (response.hasOwnProperty("html") && response.html.indexOf("ERROR") > -1) {
				$("#status_"+target).html("ERROR");
				$("#result_"+target).html(response.html);
			} else {
				if (response.hasOwnProperty("html")) {
					$("#result_"+target).html(response.html);
				}
				if (response.hasOwnProperty("hits") && response.hits !== "-1") {
					$("#hits_"+target).html(response.hits);
				}
				if (response.hasOwnProperty("docs") && response.docs !== "-1") {
					$("#docs_"+target).html(response.docs);
				}
				
				if (response.hasOwnProperty("counting") && response.counting === "true") {
					$("#status_"+target).html("<img class=\"icon spinner\" src=\"../web/img/spinner.gif\"> COUNTING");
					update = true;
				} else {
					$("#status_"+target).html("FINISHED");
				}
			}
		} else {
			$("#status_"+target).html("ERROR");
			$("#result_"+target).html("ERROR - Failed to retrieve result from server.");
		}
		$("#queries .query-row").removeClass("active");
		$("#results .result-content").removeClass("active");
		$("#queries #row_"+target).addClass("active");
		$("#results #result_"+target).addClass("active");
		
		Whitelab.debug("View after display: "+Whitelab.search.view);

		if (update) {
			setTimeout(function() { Whitelab.search.update(response.id, { count : true }); },1000);
		}
	},
	
	editQuery : function(id,origTab) {
		var q = $("#queries #query_"+id).html();
		
		$("#search-within").val($("#queries #within_"+id).html());
		$("#querybox").val(q);
		
		if ($("#group_"+id).html() !== "-") {
			$("#group-check").prop("checked",true);
			$("#group_by-select").val($("#group_"+id).html());
		}
		
		var view = $("#result_"+id+" input.current-view").val();
		if (view == 2 || view == 16) {
			$("#group-select").val("documents");
		} else {
			$("#group-select").val("hits");
		}
		
		if ($("#filter_"+id).html().length > 0 && Whitelab.hasOwnProperty("meta")) {
			Whitelab.meta.parseQueryToInterface($("#filter_"+id).html());
		}

		if (origTab === "advanced" || origTab === "extended" || origTab === "simple") {
			Whitelab.search.advanced.parseQueryToInterface(q);
		}
		if (origTab === "extended" || origTab === "simple") {
			Whitelab.search.extended.parseQueryToInterface(q);
		}
		if (origTab === "simple") {
			Whitelab.search.simple.parseQueryToInterface(q);
		}
		
		Whitelab.search.switchTab(origTab);
	},
	
	getDocGroupContent : function(element,query,group_by,groupid,max,queryid) {
		Whitelab.debug("getDocGroupContent("+element+","+query+","+group_by+","+groupid+","+max+","+queryid+")");
		Whitelab.search.result.getGroupContent(element,query,group_by,groupid,max,queryid,"doc");
		return false;
	},
	
	getGroupContent : function(element,query,group_by,groupid,max,queryid,type) {
		element = "#result_"+queryid+" "+element;
		if (!$(element).find("div.loading").hasClass("active")) {
			var start = $(element).parent().find("div.row-fluid").length;
			Whitelab.debug("getGroupContent from "+start);
			
			if (groupid.length == 0)
				groupid = "unknown";

			query = decodeURIComponent(query);
			query = query.replace(/amp;/g,"");
			
			if (start < max) {
				
				$(element).find("div.loading").addClass("active");
				
				var g = group_by;
				g = g.replace("field:","");
				
				Whitelab.search.filters = new Array();
				if ($("#filter_"+queryid).html().length > 0) {
					var f = $("#filter_"+queryid).html().split("&amp;");
					for (var i = 0; i < f.length; i++) {
						if (f[i].indexOf(g) == -1)
							Whitelab.search.filters.push(f[i]);
					}
				}
				
				if (g.indexOf("hit:") == 0) {
					var groupParts = groupid.split(" ");

					/*
					// If there's any tokens without square brackets, convert them to
					// square bracket form:
					var queryTmp = Whitelab.search.query.replace(/(^|[\]\"])\s*(\"\w+\")/g, "[word=$2]");
					
					// Now, split on ] to get the separate tokens.
					var queryParts = queryTmp.split(/]|$/);
					queryParts.splice(queryParts.length - 1); // remove empty last part
					
					//OLD: var queryParts = queryTmp.trim().substring(1,Whitelab.search.query.length - 1).split(/]\s*\[/);
					*/
					
					var q = "(" + query + ") & (";
					var newType = "word";
					if (g.indexOf(":lemma") > -1) {
						newType = "lemma";
					} else if (g.indexOf(":pos") > -1) {
						newType = "pos";
					}
					for (var p = 0; p < groupParts.length; p++) {
						/*
						var qq = queryParts[p];
						if (qq.substr(0,1) === "(")
							qq = qq.substr(1);
						if (qq.substr(qq.length - 1) === ")")
							qq = qq.substr(0,qq.length - 1);
						*/
						var pp = groupParts[p];
						/*
						var sameType = false;
						if (qq.indexOf(newType) > -1)
							sameType = true;*/

						//qq = qq.replace(/\(/g,"\\\(").replace(/\)/g,"\\\)").replace(/\\\(\?i\\\)/g,"(?i)").replace(/\\\(\?\-i\\\)/g,"(?-i)");
						pp = pp.replace(/\(/g,"\\\(").replace(/\)/g,"\\\)").replace(/\./g,"\\\.");
						
						/*
						if (!sameType) {
							q = q+"["+qq+"&"+newType+"=\"(?-i)"+pp+"\"]";
						} else {
							q = q+"["+newType+"=\"(?-i)"+pp+"\"]";
						}*/

//						q += "[" + newType + "=\"(?-i)" + pp + "\"]";
						q += "[" + newType + "=\"" + pp + "\"]";
					}
					q += ")";
					//q = q.replace(/&/g,"%26");
					Whitelab.debug("Combined query: "+q);
					query = q;
				} else if (g.indexOf("wordleft:") == 0) {
					var parts = groupid.split(" ");
					var q = "";
					var t = "word";
					if (g.indexOf(":lemma") > -1) {
						t = "lemma";
					} else if (g.indexOf(":pos") > -1) {
						t = "pos";
					}
					for (var p = 0; p < parts.length; p++) {
						Whitelab.debug("type: "+t);
						if (t === "pos") {
							var pp = parts[p].replace(/\(/g,"\\\(");
							pp = pp.replace(/\)/g,"\\\)");
							q = q+"["+t+"=\""+pp+"\"]";
						} else {
//							q = q+"["+t+"=\"(?-i)"+parts[p]+"\"]";
							q = q+"["+t+"=\""+parts[p]+"\"]";
						}
					}
					query = q+query;
				} else if (g.indexOf("wordright:") == 0) {
					var parts = groupid.split(" ");
					var q = "";
					var t = "word";
					if (g.indexOf(":lemma") > -1) {
						t = "lemma";
					} else if (g.indexOf(":pos") > -1) {
						t = "pos";
					}
					for (var p = 0; p < parts.length; p++) {
						Whitelab.debug("type: "+t);
						if (t === "pos") {
							var pp = parts[p].replace(/\(/g,"\\\(");
							pp = pp.replace(/\)/g,"\\\)");
							q = q+"["+t+"=\""+pp+"\"]";
						} else {
//							q = q+"["+t+"=\"(?-i)"+parts[p]+"\"]";
							q = q+"["+t+"=\""+parts[p]+"\"]";
						}
					}
					query = query+q;
				} else {
					Whitelab.search.filters.push(g+'="'+groupid+'"');
				}
				
				Whitelab.search.filterQuery = Whitelab.search.filters.join(" AND ");
				var filter = Whitelab.search.filterQuery;
				filter = filter.replace(/field\:/g,"").replace(/=/g,":");
				
				Whitelab.debug(Whitelab.blsUrl + type + "s?filter = (" + filter + ")&patt=" + query);
	
				var retriever = new AjaxRetriever(Whitelab.blsUrl + type + "s");
				retriever.putAjaxResponse(element, {
					patt: query,
					filter: filter,
					first: start,
					number: 20
				}, true, "../web/js/"+type+"group.xsl");
			}
		}

		return false;
	},
	
	getHitGroupContent : function(element,query,group_by,groupid,max,queryid) {
		Whitelab.debug("getHitGroupContent("+element+","+query+","+group_by+","+groupid+","+max+","+queryid+")");
		Whitelab.search.result.getGroupContent(element,query,group_by,groupid,max,queryid,"hit");
		return false;
	},
	
	goToPage : function(id,item,number,sort) {
		var page = $(item).parent().find(".page-select").val();
		var first = (page * number) - number;
		Whitelab.search.update(id, { first : first, number : number, sort : sort });
	},
	
	docProgress : function(item,query,group_by,groupid,max,queryid) {
		var target = $(item).attr("data-target");
		Whitelab.debug("docProgress('"+target+"')");
		if ($(target).hasClass("first") || $(target).find(".row-fluid").length == 0) {
			Whitelab.search.result.getDocGroupContent('#'+target.substring(1),query,group_by,groupid,max,queryid);
			$(target).removeClass("first");
		}
		$(target).toggleClass("hide");
	},
	
	hitProgress : function(item,query,group_by,groupid,max,queryid) {
		var target = $(item).attr("data-target");
		Whitelab.debug("hitProgress('"+target+"')");
		if ($(target).hasClass("first") || $(target).find(".row-fluid").length == 0) {
			Whitelab.search.result.getHitGroupContent('#'+target.substring(1),query,group_by,groupid,max,queryid);
			$(target).removeClass("first");
		}
		$(target).toggleClass("hide");
	},
	
	removeQuery : function(id) {
		var chk = false;
		if ($("#results #result_"+id).hasClass("active")) {
			chk = true;
		}
		$("#queries #row_"+id).remove();
		$("#results #result_"+id).remove();
		if (chk) {
			$("#queries .query-row").first().addClass("active");
			$("#results .result-content").first().addClass("active");
		}
	},
	
	selectGrouping : function(id,grouping,v) {
		if (Whitelab.search.group_by !== grouping) {
			Whitelab.search.update(id, { group_by : grouping, view : v });
			$("#group_"+id).html(grouping);
		}
	},
	
	searchHitGroupContent : function(queryId,group_byClean,groupId) {

		if (groupId.length == 0)
			groupId = "unknown";
		
		Whitelab.debug("searchHitGroupContent("+queryId+",'"+group_byClean+"','"+groupId+"')");
		Whitelab.search.view = 1;
		Whitelab.search.group_by = "";
		Whitelab.search.query = decodeURIComponent(Whitelab.search.query);
		Whitelab.search.query = Whitelab.search.query.replace(/amp;/g,"");
		
		var g = group_byClean;
		g = g.replace("field:","");
		
		Whitelab.search.filters = new Array();
		if ($("#filter_"+queryId).html().length > 0) {
			var f = $("#filter_"+queryId).html().split("&amp;");
			for (var i = 0; i < f.length; i++) {
				if (f[i].indexOf(g) == -1)
					Whitelab.search.filters.push(f[i]);
			}
		}
		
		
		// @@@@ TODO: whole block of code copy/pasted from above!!
		// (almost... groupid/groupId, query/Whitelab.search.query, etc.)
		
		if (g.indexOf("hit:") == 0) {
			var groupParts = groupId.split(" ");

			/*
			// If there's any tokens without square brackets, convert them to
			// square bracket form:
			var queryTmp = Whitelab.search.query.replace(/(^|[\]\"])\s*(\"\w+\")/g, "[word=$2]");
			
			// Now, split on ] to get the separate tokens.
			var queryParts = queryTmp.split(/]|$/);
			queryParts.splice(queryParts.length - 1); // remove empty last part
			
			//OLD: var queryParts = queryTmp.trim().substring(1,Whitelab.search.query.length - 1).split(/]\s*\[/);
			*/
			
			var q = "(" + Whitelab.search.query + ") & (";
			var newType = "word";
			if (g.indexOf(":lemma") > -1) {
				newType = "lemma";
			} else if (g.indexOf(":pos") > -1) {
				newType = "pos";
			}
			for (var p = 0; p < groupParts.length; p++) {
				/*
				var qq = queryParts[p];
				if (qq.substr(0,1) === "(")
					qq = qq.substr(1);
				if (qq.substr(qq.length - 1) === ")")
					qq = qq.substr(0,qq.length - 1);
				*/
				var pp = groupParts[p];
				/*
				var sameType = false;
				if (qq.indexOf(newType) > -1)
					sameType = true;*/

				//qq = qq.replace(/\(/g,"\\\(").replace(/\)/g,"\\\)").replace(/\\\(\?i\\\)/g,"(?i)").replace(/\\\(\?\-i\\\)/g,"(?-i)");
				pp = pp.replace(/\(/g,"\\\(").replace(/\)/g,"\\\)").replace(/\./g,"\\\.");
				
				/*
				if (!sameType) {
					q = q+"["+qq+"&"+newType+"=\"(?-i)"+pp+"\"]";
				} else {
					q = q+"["+newType+"=\"(?-i)"+pp+"\"]";
				}*/

//				q += "[" + newType + "=\"(?-i)" + pp + "\"]";
				q += "[" + newType + "=\"" + pp + "\"]";
			}
			q += ")";
			//q = q.replace(/&/g,"%26");
			Whitelab.debug("Combined query: "+q);
			Whitelab.search.query = q;
		} else if (g.indexOf("wordleft:") == 0) {
			var parts = groupId.split(" ");
			var q = "";
			var t = "word";
			if (g.indexOf(":lemma") > -1) {
				t = "lemma";
			} else if (g.indexOf(":pos") > -1) {
				t = "pos";
			}
			for (var p = 0; p < parts.length; p++) {
				Whitelab.debug("type: "+t);
				if (t === "pos") {
					var pp = parts[p].replace(/\(/g,"\\\(");
					pp = pp.replace(/\)/g,"\\\)");
					q = q+"["+t+"=\""+pp+"\"]";
				} else {
//					q = q+"["+t+"=\"(?-i)"+parts[p]+"\"]";
					q = q+"["+t+"=\""+parts[p]+"\"]";
				}
			}
			Whitelab.search.query = q+Whitelab.search.query;
		} else if (g.indexOf("wordright:") == 0) {
			var parts = groupId.split(" ");
			var q = "";
			var t = "word";
			if (g.indexOf(":lemma") > -1) {
				t = "lemma";
			} else if (g.indexOf(":pos") > -1) {
				t = "pos";
			}
			for (var p = 0; p < parts.length; p++) {
				Whitelab.debug("type: "+t);
				if (t === "pos") {
					var pp = parts[p].replace(/\(/g,"\\\(");
					pp = pp.replace(/\)/g,"\\\)");
					q = q+"["+t+"=\""+pp+"\"]";
				} else {
//					q = q+"["+t+"=\"(?-i)"+parts[p]+"\"]";
					q = q+"["+t+"=\""+parts[p]+"\"]";
				}
			}
			Whitelab.search.query = Whitelab.search.query+q;
		} else {
			Whitelab.search.filters.push(g+'="'+groupId.replace(/&/g,"%26")+'"');
		}
		Whitelab.debug("query = "+Whitelab.search.query);
		Whitelab.search.filterQuery = Whitelab.search.filters.join("&");
		Whitelab.search.queryCount++;
		Whitelab.search.setSearchParams(null);
		Whitelab.search.result.addQuery(Whitelab.search.queryCount);
		Whitelab.search.switchTab("result");
		Whitelab.getData(Whitelab.search.params, Whitelab.search.result.displayResult,Whitelab.search.queryCount);
	},
	
	searchDocGroupContent : function(queryId,group_byClean,groupId) {

		if (groupId.length == 0)
			groupId = "unknown";
		
		Whitelab.debug("searchDocGroupContent("+queryId+",'"+group_byClean+"','"+groupId+"')");
		Whitelab.search.view = 2;
		Whitelab.search.group_by = "";
		
		var g = group_byClean;
		g = g.replace("field:","");
		
		Whitelab.search.filters = new Array();
		if ($("#filter_"+queryId).html().length > 0) {
			var f = $("#filter_"+queryId).html().split("&amp;");
			for (var i = 0; i < f.length; i++) {
				if (f[i].indexOf(g) == -1)
					Whitelab.search.filters.push(f[i]);
			}
		}
		
		Whitelab.search.filters.push(g+'="'+groupId+'"');
		Whitelab.search.filterQuery = Whitelab.search.filters.join("&");
		Whitelab.search.queryCount++;
		Whitelab.search.setSearchParams(null);
		Whitelab.search.result.addQuery(Whitelab.search.queryCount);
		Whitelab.search.switchTab("result");
		Whitelab.getData(Whitelab.search.params, Whitelab.search.result.displayResult,Whitelab.search.queryCount);
	},
	
	showCitation : function(element,docPid,start,end) {
		Whitelab.debug("showCitation("+element+","+docPid+","+start+","+end+")");
		if ($(element).parent().parent().hasClass("hidden")) {
			$(element).parent().parent().removeClass("hidden");
			var retriever = new AjaxRetriever(Whitelab.blsUrl + "docs/" + docPid + "/snippet");
			var i = 49 - (end - start);
			var param = {
			    outputformat: "xml",
			    hitstart: start,
			    hitend: end,
			    wordsaroundhit: i
			};
			retriever.putAjaxResponse(element, param, false, "../web/js/concordance.xsl");
		} else {
			$(element).parent().parent().addClass("hidden");
		}
	},
	
	switchToQuery : function(id) {
		if (!$("#queries #row_"+id).hasClass("active")) {
			$("#queries .query-row").removeClass("active");
			$("#results .result-content").removeClass("active");
			$("#queries #row_"+id).addClass("active");
			$("#results #result_"+id).addClass("active");
		}
		Whitelab.search.query = $("#query_"+id).html();
		Whitelab.search.within = $("#within_"+id).html();
		if (Whitelab.search.within === "document")
			Whitelab.search.within = "";
		var f = $("#filter_"+id).html();
		if (f == null)
			f = "";
		f = f.replace(/field\:/g,"");
		Whitelab.search.filterQuery = f;
		Whitelab.search.filters = f.split("&");
		Whitelab.search.group_by = $("#group_"+id).html();
		if (Whitelab.search.group_by === "-")
			Whitelab.search.group_by = "";
	},
	
	toggleTitles : function() {
		$("#results .titlerow").toggleClass("hidden");
	}
};