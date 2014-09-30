Whitelab.search.document = {
	id : null,
	position : -1,
	anchors : new Array(),
	
	display : function(response) {
		if (response != null && response.hasOwnProperty("data") && response.data != null) {
			$("#document").html(response.data);
			Whitelab.search.document.initialiseAnchors();
		} else {
			$("#document").html("<p>ERROR - Could not process request.</p>");
		}
	},
	
	getCurrentAnchorName : function() {
		return Whitelab.search.document.anchors[Whitelab.search.document.position];
	},
	
	getNextAnchorName : function() {
		Whitelab.search.document.position++;
		if (Whitelab.search.document.position >= Whitelab.search.document.anchors.length)
			Whitelab.search.document.position = 0;
		
		return Whitelab.search.document.anchors[Whitelab.search.document.position];
	},
	
	getPreviousAnchorName : function() {
		Whitelab.search.document.position--;
		if (Whitelab.search.document.position < 0)
			Whitelab.search.document.position = Whitelab.search.document.anchors.length - 1;
		
		return Whitelab.search.document.anchors[Whitelab.search.document.position];
	},
	
	gotoAnchor : function() {
		var current = Whitelab.search.document.getCurrentAnchorName();
		$('html, body').stop(true,true).animate({
	        scrollTop: $('a[name=' + current + ']').offset().top - 150
	    }, 1000);
	},
	
	gotoNextAnchor : function(e) {
		e.preventDefault();
		var current = Whitelab.search.document.getCurrentAnchorName();
		var next = Whitelab.search.document.getNextAnchorName();
		$('a[name=' + current + ']').removeClass('activeLink');
		$('a[name=' + next + ']').addClass('activeLink');
		Whitelab.search.document.gotoAnchor();
	},
	
	gotoPreviousAnchor : function(e) {
		e.preventDefault();
		var current = Whitelab.search.document.getCurrentAnchorName();
		var previous = Whitelab.search.document.getPreviousAnchorName();
		$('a[name=' + current + ']').removeClass('activeLink');
		$('a[name=' + previous + ']').addClass('activeLink');
		Whitelab.search.document.gotoAnchor();
	},
	
	initialiseAnchors : function() {
		Whitelab.search.document.anchors = new Array();
		Whitelab.search.document.position = -1;
		
		$("#document a.anchor.hl").each(function() {
			Whitelab.search.document.anchors.push($(this).attr("name"));
		});
		
		if(Whitelab.search.document.anchors.length == 0)
			$("#document .hitscroll").hide();
	},
	
	load : function(pid,query) {
		var params = "docpid="+pid+"&query="+query;
		params = params.replace(/ /g,"%20");
		$("#document").html("<p>Loading document "+pid+"...</p>");
		Whitelab.search.switchTab("document");
		
		var xhr = Whitelab.createRequest('POST', Whitelab.baseUrl+"document");
		if (!xhr) {
			return;
		}
		
		Whitelab.debug(params);
		
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		xhr.onload = function() {
			Whitelab.search.document.id = pid;
			var resp = JSON.parse(xhr.responseText);
			Whitelab.search.document.display(resp);
		};

		xhr.onerror = function() {
			$("#document").html("<p>ERROR - Could not connect to server.</p>");
		};

		xhr.send(params);
	},
	
	switchTab : function(target) {
		Whitelab.debug("switching tab");
		$("#document .doc_link").removeClass("active");
		$("#document .doc-pane").removeClass("active");
		$("#document #"+target+"_link").addClass("active");
		$("#document #"+target+"_tab").addClass("active");

		if (target === "cloud") {
			generateCloud("#docCloudDisplay", $("#docCloudSettings > input.split").is(':checked'));
		}
	}
};