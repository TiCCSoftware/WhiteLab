Whitelab.search.document = {
	id : null,
	position : -1,
	anchors : new Array(),
	
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
		
		$("#doc-display a.anchor.hl").each(function() {
			Whitelab.search.document.anchors.push($(this).attr("name"));
		});
		
		if(Whitelab.search.document.anchors.length == 0)
			$("#doc-display .hitscroll").hide();
	}
};