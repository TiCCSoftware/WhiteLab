Whitelab.explore.statistics = {
	query : "[]",
	filterQuery : "",
	first : 0,
	number : 50,
	group_by : "word",
	sort : "",
	view : 12,
	params : "",
	tab : "result_list",
	detailsSet : false,
	
	composeQuery : function(type) {
		Whitelab.explore.statistics.setDefaults();
		
		if (Whitelab.hasOwnProperty("meta") && !Whitelab.explore.statistics.detailsSet)
			Whitelab.explore.statistics.filterQuery = Whitelab.meta.parseQuery();
		if (Whitelab.search.within != null && Whitelab.search.within == "paragraph") {
			Whitelab.explore.statistics.query += " within (<p/>|<event/>)";
		} else if (Whitelab.search.within != null && Whitelab.search.within == "sentence") {
			Whitelab.explore.statistics.query += " within <s/>";
		}

		if (Whitelab.explore.statistics.filterQuery.length == 0) {
			if (!Whitelab.hasOwnProperty("tour") || Whitelab.tour.page == null)
				alert("Please define a metadata filter.");
		} else {
			
			if (type === "docs") {
				Whitelab.explore.statistics.view = 4;
				Whitelab.explore.statistics.group_by = "";
			} else {
				if (!Whitelab.explore.statistics.detailsSet) {
					Whitelab.explore.statistics.group_by = $('select#stats-groupSelect').val();
				} else {
					Whitelab.explore.statistics.group_by = $("#stats-group").html();
				}
			}
			
			Whitelab.explore.statistics.setQueryDetails();
			Whitelab.explore.statistics.setSearchParams();
			$("#result_statistics").removeClass("hidden");
		}
		return Whitelab.explore.statistics.params;
	},
	
	reset : function() {
		if (Whitelab.hasOwnProperty("meta"))
			Whitelab.meta.reset();
		$("#stats div.info").addClass("hidden");
		$("#result_statistics").addClass("hidden");
		$("#result_status").addClass("hidden");
		$("#result_status").html("");
		$("#result_list").html("");
		$("#result_doclist").html("");
		$("#vocabChartDisplay").html("");
		$("#cloudDisplay").html("");
		Whitelab.explore.statistics.clearData();
	},
	
	setDefaults : function() {
		Whitelab.explore.statistics.first = 0;
		Whitelab.explore.statistics.number = 50;
		Whitelab.explore.statistics.group_by = "word";
		Whitelab.explore.statistics.sort = "";
		Whitelab.explore.statistics.view = 12;
		Whitelab.explore.statistics.params = "";
	},
	
	setQueryDetails : function() {
		Whitelab.debug("Whitelab.explore.statistics.setQueryDetails");
		if (!Whitelab.explore.statistics.detailsSet) {
			Whitelab.debug("setQueryDetails group_by: "+Whitelab.explore.statistics.group_by);
			$("#stats div.info").removeClass("hidden");
			if (Whitelab.explore.statistics.filterQuery.length > 0)
				$("#stats-filter").html(Whitelab.explore.statistics.filterQuery);
			$("#stats-group").html(Whitelab.explore.statistics.group_by);
			Whitelab.explore.statistics.detailsSet = true;
		}
	},
	
	setSearchParams : function() {
		Whitelab.explore.statistics.params ="query=" + encodeURIComponent(Whitelab.explore.statistics.query)
		+ "&view=" + Whitelab.explore.statistics.view 
		+ "&sort=" + Whitelab.explore.statistics.sort
		+ "&first=" + Whitelab.explore.statistics.first
		+ "&number=" + Whitelab.explore.statistics.number;
		
		if (Whitelab.explore.statistics.view == 12)
			Whitelab.explore.statistics.params = Whitelab.explore.statistics.params + "&group=hit:" + Whitelab.explore.statistics.group_by;
		
		if (Whitelab.explore.statistics.filterQuery.length > 0)
			Whitelab.explore.statistics.params = Whitelab.explore.statistics.params + "&" + Whitelab.explore.statistics.filterQuery;
		
		Whitelab.debug("search statistics params set to: "+Whitelab.explore.statistics.params);
	}
};