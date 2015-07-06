Whitelab.search.extended = {
	eraseBatchList : function(el) {
		$(el).parent().parent().parent().find(".batchrow").removeClass("active");
		$(el).parent().parent().parent().find(".inputrow").addClass("active");
//		$(el).parent().parent().parent().find(".loadbutton").html("");
		$(el).parent().parent().parent().find(".loadbutton").html("<div style=\"position:relative; height: 44px;\"> \
		<div class=\"batchWordListBtn\"><button class=\"load\"></button></div> \
				<input class=\"loadlist\" type=\"file\" onchange=\"Whitelab.search.extended.loadBatchList(this);\"> \
		</div>");
		$("#extended .splitcheck").prop("checked",false);
	},
	
	loadBatchList : function(el) {
		Whitelab.readFile(el.files[0], function(e) {
	        var text = e.target.result;
	        text.replace(/(\r\n\r\n|\n\n|\r\r)/gm,/\n/);
	        text.trim();
	        var batch = $(el).parent().parent().parent().prev();
	        batch.addClass("active");
	    	batch.find(".batchlist").html(text);
	    	var input = $(el).parent().parent().parent();
	    	input.removeClass("active");
	    });
		$("#extended .splitcheck").prop("checked",true);
	},
	
	parseQuery : function() {
		var batch = false;
		if (!$("#extended_word .batchrow").is(':hidden') ||
			!$("#extended_lemma .batchrow").is(':hidden') ||
			!$("#extended_pos .batchrow").is(':hidden')) {
			batch = true;
		}
		var split = $("#extended .splitcheck").is(":checked");
		var query = new Cql(split,batch);
		
		var types = ["word","lemma","pos"];
		$.each(types, function(t,type) {
			if ($("#extended_"+type+" .batchrow").is(':hidden')) {
				if ($("#"+type+"_text").val().length > 0) {
					var vals = $("#"+type+"_text").val().split(" ");
					for (var v = 0; v < vals.length; v++) {
						if (query.columns.length <= v) {
							query.addEmptyColumn();
						}
						var f = query.addEmptyFieldToColumn(v);
						f.type = type;
						f.operator = "is";
						if (type === "word" || type === "lemma") {
							f.sensitive = $("#word input").prop('checked');
						}
						f.value = Whitelab.search.removeQuantifier(vals[v]);
						f.quantifier = Whitelab.search.removeValue(vals[v]);
					}
				}
			} else {
				var lines = $("#extended_"+type+" .batchlist").val().split("\n");
				$.each(lines, function(l,line) {
					if (line.length > 0) {
						var vals = line.split(" ");
						for (var v = 0; v < vals.length; v++) {
							if (query.columns.length <= v) {
								query.addEmptyColumn();
							}
							var column = query.columns[v];
							var f = column.getFieldByType(type);
							if (f == null) {
								f = query.addEmptyFieldToColumn(v);
								f.type = type;
							}
							var sub = new CqlField(type,null,false,"is",true,null);
							if (type === "word") {
								sub.sensitive = $("#word input").prop('checked');
							}
							sub.value = Whitelab.search.removeQuantifier(vals[v]);
							sub.quantifier = Whitelab.search.removeValue(vals[v]);
							f.addSubField(sub);
						}
					}
				});
			}
		});
		
		return query.getQuery();
	},
	
	parseQueryToInterface : function(query) {
		Whitelab.debug("extended.parseQueryToInterface: "+query);
		var n = query.indexOf("[");
		var m = -1;
		var columns = [];
		var quants = [];
		var batch = false;
		var split = false;
		var wordsensitive = false;
		var lemmasensitive = false;
		if (query.indexOf("|") > 1) {
			batch = true;
		}
		while (n > -1) {
			m = query.indexOf("]",n);
			var c = query.substring(n,m+1);
			columns.push(c);
			n = query.indexOf("[",m+1);
			if (n-m > 1) {
				quants.push(query.substring(m+1,n));
			} else {
				quants.push('');
			}
		}
		
		var cql = new Cql(split,batch);
		for (var i = 0; i < columns.length; i++) {
			var x = cql.addEmptyColumn();
			var column = columns[i];
			var quant = quants[i];
			
			if (column.length > 0) {
				var ands = column.split('&');
				while (ands.length > 0) {
					var and = ands.shift();
					var ors = and.split('|');
					if (and.indexOf(' | ') > -1) {
						ors = and.split(' | ');
					}
					while (ors.length > 0) {
						var or = ors.shift();
						var type = 'word';
						if (or.indexOf('lemma') > -1) {
							type = 'lemma';
						} else if (or.indexOf('pos') > -1) {
							type = 'pos';
						}
						
						var field = cql.columns[x].getFieldByType(type);
						if (field == null) {
							field = cql.addEmptyFieldToColumn(x);
							field.type = type;
						}
						
						var term = or.substring(or.indexOf('"')+1);
						var q2 = term.indexOf('"');
						term = term.substring(0,q2);
						var sensitive = false;
						if (term.indexOf('(?i)') > -1) {
							term = term.substring(4);
						} else if (term.indexOf('(?c)') > -1) {
							term = term.substring(5);
							sensitive = true;
							if (type === "word")
								wordsensitive = true;
							else if (type === "lemma")
								lemmasensitive = true;
						}
						if (term.length == 0) {
							term = '[]';
						}

						var sub = new CqlField(type,term,sensitive,"is",batch,quant);
						field.addSubField(sub);
						Whitelab.debug("("+type+") "+x+" subfield: "+term);
					}
				}
			}
		}
		
		if (cql.batch) {
			Whitelab.debug("cql batch");
			var types = ["word","lemma","pos"];
			for (var t = 0; t < types.length; t++) {
				var type = types[t];
				
				var rows = 0;
				for (var i = 0; i < cql.columns.length; i++) {
					var column = cql.columns[i];
					var field = column.getFieldByType(type);
					if (field != null) {
						var r = field.subfields.length;
						Whitelab.debug("("+type+") field "+i+" has "+r+" rows");
						if (r > rows)
							rows = r;
					}
				}
				if (rows > 0) {
					var vals = [];
					for (var i = 0; i < cql.columns.length; i++) {
						var column = cql.columns[i];
						var field = column.getFieldByType(type);
						var r = field.subfields.length;
						for (var j = 0; j < rows; j++) {
							if (j >= r) {
								// add last non-empty field to row
								if (vals.length == j) {
									vals[j] = [];
								}
								vals[j][i] = field.subfields[r-1].value;
							} else {
								// add value to row
								if (vals.length == j) {
									vals[j] = [];
								}
								Whitelab.debug("("+type+") adding: "+field.subfields[j].value);
								vals[j][i] = field.subfields[j].value;
							}
						}
					}

					$("#extended_"+type+" div.batchrow").addClass("active");
					$("#extended_"+type+" div.inputrow").removeClass("active");
					$("#extended_"+type+" textarea.batchlist").val("");
					
					for (var i = 0; i < vals.length; i++) {
						var val = $("#extended_"+type+" textarea.batchlist").val();
						if (val.length > 0) {
							val = val+"\n"+vals[i].join(" ");
						} else {
							val = vals[i].join(" ");
						}
						Whitelab.debug("VALUE: "+val);
						$("#extended_"+type+" textarea.batchlist").val(val);
					}
				}
			}
			
		} else {
			Whitelab.debug("cql non-batch");
			var types = ["word","lemma","pos"];
			for (var t = 0; t < types.length; t++) {
				var type = types[t];
				
				if (type === "word") {
					Whitelab.debug("wordsensitive: "+wordsensitive);
					$("#extended_word input[type='checkbox']").prop("checked",wordsensitive);
				}
				if (type === "lemma") {
					Whitelab.debug("lemmasensitive: "+lemmasensitive);
					$("#extended_lemma input[type='checkbox']").prop("checked",lemmasensitive);
				}
				
				var vals = [];
				for (var i = 0; i < cql.columns.length; i++) {
					var column = cql.columns[i];
					var field = column.getFieldByType(type);
					if (field != null) {
						var v = field.subfields[0].value;
						Whitelab.debug("field "+type+" has value: "+v);
						if (field.subfields[0].value.length == 0)
							vals.push("[]");
						else
							vals.push(field.subfields[0].value);
					} else {
						Whitelab.debug("field "+type+" is null");
					}
				}
				if (vals.length > 0) {
					var val = vals.join(" ");
					if (type === "pos") {
						if ($("#extended #pos_text option[value='"+val+"']").length > 0) {
							$("#extended #"+type+"_text").val(val);
						} else {
							$("#extended_pos .searchinput").html('<input type="text" data-target="#pos" id="pos_text" name="pos" data-toggle="collapse" value="'+val+'" placeholder="pos" />');
						}
					} else {
						$("#extended #"+type+"_text").val(val);
					}
				}
			}
		}
	},
	
	reset : function() {
		$("#extended div.batchrow").removeClass("active");
		$("#extended textarea.batchlist").html("");
		$("#extended div.loadbutton").html('<div style="position:relative; height: 36px;">'+
				'<div class="batchWordListBtn"><button class="load" onclick="event.preventDefault();"></button></div>'+
				'<input class="loadlist" type="file" onchange="Whitelab.search.extended.loadBatchList(this);">'+
			'</div>');
		$("#extended div.inputrow").addClass("active");
		$("#extended #word_text").val("");
		$("#extended #lemma_text").val("");
		$("#extended #pos_text").val("");
		$("#extended input[type='checkbox']").prop("checked",false);
	}
};