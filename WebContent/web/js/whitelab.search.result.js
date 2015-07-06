Whitelab.search.result = {
	displayGroupContent : function(response,target) {
		Whitelab.debug("displayGroupContent");
		Whitelab.debug(target);
		Whitelab.debug(response);
		if (response != null && response.hasOwnProperty("html")) {
			$(target).append(response.html);
		}
	},
	
	toggleDocGroupContent : function(target,term,group,filter,cql) {
		$(target).toggleClass("hide");
		if ($(target).hasClass("first") || $(target).find(".row-fluid").length == 0) {
			$(target).removeClass("first");
			Whitelab.search.result.docGroupContent(target.replace(/\./,'#'),term,group,filter,cql);
		}
	},
	
	toggleHitGroupContent : function(target,term,group,filter,cql) {
		$(target).toggleClass("hide");
		if ($(target).hasClass("first") || $(target).find(".row-fluid").length == 0) {
			$(target).removeClass("first");
			Whitelab.search.result.hitGroupContent(target.replace(/\./,'#'),term,group,filter,cql);
		}
	},
	
	docGroupContent : function(target,term,group,filter,cql) {
		var first = $(target).find("input.start").first().val();
		var max = $(target).find("input.count").first().val();
		max = +max;
		first = +first;
		if (first < max) {
			Whitelab.getData("/whitelab/search/results", "query="+cql+"&view=17&number=20&first="+first+"&"+filter, Whitelab.search.result.displayGroupContent, target+'_content', null);
			first = first + 20;
			$(target).find("input.start").first().val(first);
		}
	},
	
	hitGroupContent : function(target,term,group,filter,cql) {
		var first = $(target).find("input.start").first().val();
		var max = $(target).find("input.count").first().val();
		max = +max;
		first = +first;
		if (first < max) {
			Whitelab.getData("/whitelab/search/results", "query="+cql+"&view=9&number=20&first="+first+"&"+filter, Whitelab.search.result.displayGroupContent, target+'_content', null);
			first = first + 20;
			$(target).find("input.start").first().val(first);
		}
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
	
	toggleTitles : function() {
		$("#results .titlerow").toggleClass("hidden");
	}
};