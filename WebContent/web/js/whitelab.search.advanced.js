Whitelab.search.advanced = {
	components : {
		column : null,
		and : null,
		or : null
	},
	
	addColumn : function() {
		if (Whitelab.search.advanced.components.column != null) {
			$(Whitelab.search.advanced.components.column).insertBefore($("#advanced .plus-column"));
			if ($("#advanced").find(".construct-column").length > 1)
				$("#advanced").find(".close").removeClass("hide");
		}
	},
	
	addColumnAnd : function(item) {
		Whitelab.debug("addColumnAnd");
		if (Whitelab.search.advanced.components.and != null) {
			var column = $(item).parent().parent().parent();
			$(column).find(".column-content").append("<div class=\"row and\"><p>AND</p></div>");
			$(column).find(".column-content").append(Whitelab.search.advanced.components.and);
			$(column).find(".remove-token").removeClass("hide");
		}
	},
	
	addColumnOr : function(item) {
		Whitelab.debug("addColumnOr");
		if (Whitelab.search.advanced.components.or != null) {
			Whitelab.debug($(item).parent().html());
			$(Whitelab.search.advanced.components.or+"<hr>").insertBefore($(item).parent());
			$(item).parent().parent().find(".remove-token").removeClass("hide");
		}
		
		var b = $(item).parent().parent().find(".token-box").length;
		if (b > 1)
			$(item).parent().parent().find(".or-header").removeClass("hide");
	},
	
	getBoxValue : function(box,type,op) {
		if (type == 'pos' && (op == 'is' || op == 'not')) {
			var term = $(box).find(".advanced-pos-select").first().val();
			return term;
		} else {
			var term = $(box).find(".token-input-field input").first().val();
			return term;
		}
	},
	
	getBoxValues : function(box) {
		var terms = new Array();
		var val = $(box).find(".batchlist").val();
		var vals = val.split("\n");
		$.each(vals, function(i,v) {
			var term = v.trim();
			if (term.length > 0) {
				terms.push(term);
			}
		});
		return terms;
	},

	eraseBatchList : function(el) {
		$(el).parent().parent().parent().find(".batchrow").removeClass("active");
		$(el).parent().parent().parent().find(".batchlist").html("");
		$(el).parent().parent().parent().find(".inputrow").addClass("active");
//				$(el).parent().parent().parent().find(".loadbutton").html("");
		$(el).parent().parent().parent().find(".loadbutton").html("<div class=\"batchWordListBtn\"><button class=\"load-small\"></button></div> \
				<input class=\"small-loadlist\" type=\"file\" onchange=\"Whitelab.search.advanced.loadBatchList(this);\"> \
		</div>");
		$("#advanced .splitcheck").prop("checked",false);
	},
	
	inputAsDropdown : function(item) {
		if (Whitelab.language == 'en') {
			$(item).find(".token-input-field").html("<select class=\"advanced-pos-select\"> \
					<option value=\"ADJ.*\" selected>Adjective</option> \
					<option value=\"BW.*\">Adverb</option> \
					<option value=\"VG.*\">Conjunction</option> \
					<option value=\"LID.*\">Determiner</option> \
					<option value=\"TSW.*\">Interjection</option> \
					<option value=\"N.*\">Noun</option> \
					<option value=\"TW.*\">Numeral</option> \
					<option value=\"VZ.*\">Preposition</option> \
					<option value=\"VNW.*\">Pronoun</option> \
					<option value=\"SPEC.*\">Proper Name</option> \
					<option value=\"LET.*\">Punctuation</option> \
					<option value=\"WW.*\">Verb</option> \
				</select>");
		} else {
			$(item).find(".token-input-field").html("<select class=\"advanced-pos-select\"> \
					<option value=\"ADJ.*\" selected>Bijvoeglijk Naamwoord</option> \
					<option value=\"BW.*\">Bijwoord</option> \
					<option value=\"SPEC.*\">Eigennaam</option> \
					<option value=\"LID.*\">Lidwoord</option> \
					<option value=\"LET.*\">Punctuatie</option> \
					<option value=\"TW.*\">Telwoord</option> \
					<option value=\"TSW.*\">Tussenwerpsel</option> \
					<option value=\"VG.*\">Voegwoord</option> \
					<option value=\"VNW.*\">Voornaamwoord</option> \
					<option value=\"VZ.*\">Voorzetsel</option> \
					<option value=\"WW.*\">Werkwoord</option> \
					<option value=\"N.*\">Zelfstandig Naamwoord</option> \
				</select>");
		}
	},
	
	inputAsText : function(item) {
		$(item).find(".token-input-field").html("<input type=\"text\">");
	},
	
	loadBatchList : function(el) {
		Whitelab.readFile(el.files[0], function(e) {
	        var text = e.target.result;
	        text.replace(/(\r\n\r\n|\n\n|\r\r)/gm,/\n/);
	        text.trim();
	        var batch = $(el).parent().parent().prev();
	        batch.addClass("active");
	    	batch.find(".batchlist").html(text);
	    	var input = $(el).parent().parent();
	    	input.removeClass("active");
	    });
		$("#advanced .splitcheck").prop("checked",true);
	},
	
	parseQuery : function() {
		var nrBatchFields = 0;
		$("#advanced").find(".batchrow").each(function() {
			if ($(this).hasClass('hide') == false) {
				nrBatchFields++;
			}
		});
		
		var split = $("#advanced .splitcheck").is(":checked");
		var batch = nrBatchFields > 0 ? true : false;
		var query = new Cql(split,batch);
		
		$(document).find(".construct-column").each(function() {
			var i = query.addEmptyColumn();
			
			$(this).find(".and-box").each(function(j,and) {
				var f = query.addEmptyFieldToColumn(i);
				if ($(and).find(".token-box").length > 1) {
					$.each($(and).find(".token-box"), function(j,or) {
						if (!$(or).find(".batchrow").first().hasClass("active")) {
							var sub = new CqlField(null,null,false,null,false,null);
							sub.type = $(or).find(".token-type").first().val();
							Whitelab.debug("sub type: "+sub.type);
							sub.operator = $(or).find(".token-operator").first().val();
							Whitelab.debug("sub operator: "+sub.operator);
							sub.sensitive = $(or).find("div.token-case input").prop('checked');
							Whitelab.debug("sub sensitive: "+sub.sensitive);
							var boxval = Whitelab.search.advanced.getBoxValue(or,sub.type,sub.operator);
							Whitelab.debug("sub boxval: "+boxval);
							sub.value = Whitelab.search.removeQuantifier(boxval);
							Whitelab.debug("sub value: "+sub.value);
							sub.quantifier = Whitelab.search.removeValue(boxval);
							Whitelab.debug("sub quantifier: "+sub.quantifier);
							f.addSubField(sub);
						} else {
							f.batch = true;
							var vals = Whitelab.search.advanced.getBoxValues(or);
							for (var v = 0; v < vals.length; v++) {
								var sub = new CqlField(null,null,false,null,false,null);
								sub.type = $(or).find(".token-type").first().val();
								sub.operator = $(or).find(".token-operator").first().val();
								sub.sensitive = $(or).find("div.token-case input").prop('checked');
								sub.value = Whitelab.search.removeQuantifier(vals[v]);
								sub.quantifier = Whitelab.search.removeValue(vals[v]);
								f.addSubField(sub);
							}
						}
					});
				} else if (!$(and).find(".batchrow").first().hasClass("active")) {
					// 1 field filled, no batch
					f.type = $(and).find(".token-type").first().val();
					f.operator = $(and).find(".token-operator").first().val();
					f.sensitive = $(and).find("div.token-case input").prop('checked');
					var boxval = Whitelab.search.advanced.getBoxValue(and,f.type,f.operator);
					f.value = Whitelab.search.removeQuantifier(boxval);
					f.quantifier = Whitelab.search.removeValue(boxval);
				} else {
					// 1 field filled, batch
					var vals = Whitelab.search.advanced.getBoxValues(and);
					f.batch = true;
					f.type = $(and).find(".token-type").first().val();
					for (var v = 0; v < vals.length; v++) {
						var sub = new CqlField(null,null,false,null,false,null);
						sub.type = $(and).find(".token-type").first().val();
						sub.operator = $(and).find(".token-operator").first().val();
						sub.sensitive = $(and).find("div.token-case input").prop('checked');
						sub.value = Whitelab.search.removeQuantifier(vals[v]);
						sub.quantifier = Whitelab.search.removeValue(vals[v]);
						f.addSubField(sub);
					}
				}
			});
			
			if ($(this).find("div.repeat").hasClass("active")) {
				var from = $(this).find("input.from").val();
				var to = $(this).find("input.to").val();
				query.columns[i].quantifier = "{"+from+","+to+"}";
			}
			
			if ($(this).find("span.startsen").hasClass("active")) {
				query.columns[i].before = "<s>";
			}
			
			if ($(this).find("span.endsen").hasClass("active")) {
				query.columns[i].after = "</s>";
			}
			
		});
		
		return query.getQuery();
	},
	
	parseQueryToInterface : function(query) {
		Whitelab.search.advanced.removeAllColumns();
		Whitelab.search.advanced.addColumn();
		
		var n = query.indexOf("[");
		var m = -1;
		var columns = [];
		var quants = [];
		var qua1 = '';
		if (n > 1) {
			qua1 = query.substring(0,n);
			if (qua1.indexOf('<s>') == -1) {
				qua1 = '';
			}
		}
		while (n > -1) {
			m = query.indexOf("]",n);
			var c = query.substring(n,m+1);
			columns.push(c);
			n = query.indexOf("[",m+1);
			if (n == -1) {
				if (query.length > m+1) {
					quants.push(query.substring(m+1));
				}
			} else {
				if (n-m > 1) {
					quants.push(query.substring(m+1,n));
				} else {
					quants.push('');
				}
			}
		}
		while (columns.length > 0) {
			var column = columns.shift();
			var quant = quants.shift();
			
			if (qua1.length > 0) {
				$("#construct-canvas .construct-column").last().find("span.startsen").addClass("active");
				qua1 = '';
			}
			
			if (quant && quant.indexOf('</s>') > 1) {
				$("#construct-canvas .construct-column").last().find("span.endsen").addClass("active");
			} else if (quant && quant.indexOf('<s>') > 1) {
				qua1 = quant;
			} else if (quant && quant.indexOf('{') > -1) {
				$("#construct-canvas div.repeat").last().addClass("active");
				if (quant.indexOf('{,') > -1) {
					$("#construct-canvas .construct-column").last().find("input.from").val(0);
				} else {
					var nrs = quant.match(/\d+/);
					$("#construct-canvas .construct-column").last().find("input.from").val(nrs[0]);
					quant = quant.replace(nrs[0],'');
				}
				var nrs = quant.match(/\d+/);
				if (nrs != null && nrs.length > 0) {
					$("#construct-canvas .construct-column").last().find("input.to").val(nrs[0]);
				} else {
					$("#construct-canvas .construct-column").last().find("input.to").val('');
				}
			} else if (quant && quant.indexOf('+') > -1) {
				$("#construct-canvas .construct-column").last().find("div.repeat").addClass("active");
				$("#construct-canvas .construct-column").last().find("input.from").val(1);
				$("#construct-canvas .construct-column").last().find("input.to").val('');
			} else if (quant && quant.indexOf('*') > -1) {
				$("#construct-canvas .construct-column").last().find("div.repeat").addClass("active");
				$("#construct-canvas .construct-column").last().find("input.from").val(0);
				$("#construct-canvas .construct-column").last().find("input.to").val('');
			} else if (quant && quant.indexOf('within') > -1) {
				if (quant.indexOf('s') > -1) {
					$("#search-within").val('sentence');
				} else if (quant.indexOf('p') > -1 || quant.indexOf('event') > -1) {
					$("#search-within").val('paragraph');
				}
			}
			
			var ands = column.split('&');
			while (ands.length > 0) {
				var and = ands.shift();
				
				var ors = and.split('|');
				while (ors.length > 0) {
					var or = ors.shift();
					if (or === '[]') {
						// do nothing
					} else {
						var type = 'word';
						if (or.indexOf('lemma') > -1) {
							type = 'lemma';
							$("#construct-canvas .construct-column select.token-type").last().val('lemma');
						} else if (or.indexOf('pos') > -1) {
							type = 'pos';
							$("#construct-canvas .construct-column select.token-type").last().val('pos');
						}
						
						var not = 0;
						if (or.indexOf('!=') > -1) {
							not = 1;
						}
						
						var term = or.substring(or.indexOf('"')+1);
						var q2 = term.indexOf('"');
						term = term.substring(0,q2);
						if (term.indexOf('(?i)') > -1) {
							term = term.substring(4);
						} else if (term.indexOf('(?-i)') > -1) {
							term = term.substring(5);
							if (type === 'word' || type === 'lemma') {
								$("#construct-canvas .construct-column div.token-case > input").last().prop('checked', true);
							}
						}
						
						var dd = 1;
						if (not == 1) {
							$("#construct-canvas .construct-column select.token-operator").last().val('not');
						} else {
							var regex = /\.(\*|\+)/gi, result, indices = [];
							while ( (result = regex.exec(term)) ) {
							    indices.push(result.index);
							}
							if (indices.length == 2 && indices[0] == 0 && indices[1] == term.length - 2) {
								dd = 0;
								$("#construct-canvas .construct-column select.token-operator").last().val('contains');
							} else if (indices.length > 2 || (indices.length == 2 && (indices[0] != 0 || indices[1] != term.length - 2))) {
								dd = 0;
								$("#construct-canvas .construct-column select.token-operator").last().val('regex');
							} else if (indices.length == 1) {
								if (indices[0] == 0) {
									dd = 0;
									$("#construct-canvas .construct-column select.token-operator").last().val('ends');
								} else if (indices[0] == term.length - 2) {
									if (type === 'pos' && term.match(/^[A-Z]+\.\*/)) {
										dd = 1;	
									} else {
										dd = 0;
										$("#construct-canvas .construct-column select.token-operator").last().val('starts');
									}
								} else {
									dd = 0;
									$("#construct-canvas .construct-column select.token-operator").last().val('regex');
								}
							}
						}
						
						if (type === 'pos' && dd == 1) {
							Whitelab.search.advanced.inputAsDropdown($("#construct-canvas .construct-column div.token-input").last());
							$("#construct-canvas .construct-column select.advanced-pos-select").last().val(term);
						} else {
							$("#construct-canvas .construct-column div.token-input-field > input").last().val(term);
						}
					}
					
					if (ors.length > 0) {
						$("#construct-canvas .construct-column a.add-or").last().click();
					}
				}
				
				if (ands.length > 0) {
					$("#construct-canvas .construct-column a.add-and").last().click();
				}
			}
			
			if (columns.length > 0) {
				Whitelab.search.advanced.addColumn();
			}
		}
	},
	
	removeAllColumns : function() {
		$("#construct-canvas").find(".construct-column").remove();
	},
	
	removeColumn : function(item) {
		var column = $(item).parent().parent();
		var columns = $(item).parent().parent().parent().find(".construct-column").length;
		if (columns == 2) {
			$(item).parent().parent().parent().find(".close").addClass("hide");
		}
		$(column).remove();
	},
	
	removeColumnOr : function(item) {
		var column = $(item).parent().parent().parent().parent();
		var and = $(item).parent().parent().parent();
		var or = $(item).parent().parent();
		var tboxes = $(and).find(".token-box").length;
		
		if (tboxes == 1) {
			$(and).remove();
			if ($(column).find(".row.and:first-child").html() === "<p>AND</p>") {
				$(column).find(".row.and:first-child").remove();
			} else if ($(column).find(".row.and:last-child").html() === "<p>AND</p>") {
				$(column).find(".row.and:last-child").remove();
			} else {
				$(column).html($(column).html().replace("<div class=\"row and\"><p>AND</p></div><div class=\"row and\"><p>AND</p></div>","<div class=\"row and\"><p>AND</p></div>"));
			}
		} else {
			$(or).remove();
			$(and).html($(and).html().replace(/\<hr\>\<hr\>/g,"<hr>"));
		}

		tboxes = $(and).find(".token-box").length;
		if (tboxes == 1) {
			$(and).find(".or-header").addClass("hide");
		} else {
			$(and).find(".or-header").removeClass("hide");
		}
		
		var ctboxes = $(column).find(".token-box").length;
		if (ctboxes == 1) {
			$(column).find(".remove-token").addClass("hide");
		}
	},
	
	reset : function() {
		Whitelab.search.advanced.removeAllColumns();
		Whitelab.search.advanced.addColumn();
	},
	
	setTokenInput : function(item) {
		var row = $(item).parent().parent();
		var typeValue = $(row).find(".token-type").find(":selected").val();
		var operatorValue = $(row).find(".token-operator").find(":selected").val();
		if (typeValue == "pos" && (operatorValue == "is" || operatorValue == "not")) {
			Whitelab.search.advanced.inputAsDropdown($(row).parent());
		} else {
			Whitelab.search.advanced.inputAsText($(row).parent());
		}
	}
};