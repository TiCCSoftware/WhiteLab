Whitelab.meta = {
	rule : null,
	
	addRule : function() {
		$("#"+Whitelab.tab+"-meta .rules").append(Whitelab.meta.rule);
	},
	
	removeRule : function(element) {
		if ($("#"+Whitelab.tab+"-meta").find(".rule").length > 1) {
			$(element).parent().parent().remove();
		}
	},
	
	reset : function() {
		$("#"+Whitelab.tab+"-meta .rules").html("");
		Whitelab.meta.addRule();
		$("#"+Whitelab.tab+"-meta #group-check").prop("checked",false);
		$("#"+Whitelab.tab+"-meta #group-select").val("hits");
		$("#"+Whitelab.tab+"-meta #group_by-select").val("");
		$("#"+Whitelab.tab+"-meta #search-within").val("");
	},
	
	parseQuery : function() {
		var filters = new Array();
		$("#"+Whitelab.tab+"-meta .rule").each(function( index ) {
			var label = $(this).find(".metaLabel").val();
			var input = $(this).find(".metaInput").val().replace(/&/g,"%26");
			var op = $(this).find(".metaOperator").val();
			if (op === 'not') {
				input = "-"+input;
			}
			if (label && input && input.length > 0) {
				var f = label+"=\""+input+"\"";
				f = f.replace(/field\:/g,"");
				filters.push(f);
			}
		});
		var filterQuery = filters.join("&");
		if (Whitelab.tab === "search") {
			var v = $("#"+Whitelab.tab+"-meta #group-select").val();
			if (v === "hits") {
				Whitelab.search.view = 1;
			} else {
				Whitelab.search.view = 2;
			}
			
			if ($("#"+Whitelab.tab+"-meta #group-check").prop("checked") == true) {
				Whitelab.search.group_by = $("#"+Whitelab.tab+"-meta #group_by-select").val();
				if (v === "hits") {
					Whitelab.search.view = 8;
				} else {
					Whitelab.search.view = 16;
				}
			}
			Whitelab.search.within = $("#"+Whitelab.tab+"-meta #search-within").val();
			if (Whitelab.search.within == null) {
				Whitelab.search.within = "";
			}
			$("#"+Whitelab.tab+"-meta #search-within").val("");
		}
		return filterQuery;
	},
	
	switchState : function(item) {
		if ($(item).parent().find("div.content-meta").hasClass("active"))
			$(item).find("img").attr("src","../web/img/plus.png");
		else
			$(item).find("img").attr("src","../web/img/minus.png");
	}
};