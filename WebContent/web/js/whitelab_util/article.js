

$(document).on("change", "#docCloudSettings > input.split", function(e) {
	e.preventDefault();
	generateCloud("#docCloudDisplay", $(this).is(':checked'));
});

$(document).on("mouseover", "#text_tab .word", function(e){
	$(this).find(".hoverdiv").addClass("active");
	var ww = $(window).innerWidth()-40;
	var w = $(this).find(".hoverdiv").width();
	var l = $(this).offset().left + 15;
	var r = l+w;
	if (r > ww) {
		l = l - (r - ww) - $(this).offset().left;
		$(this).find(".hoverdiv").css({left: l});
	}
});

$(document).on("mouseout", "#text_tab .word", function(e){
	if (!$(this).hasClass("clicked")) {
		$(this).find(".hoverdiv").removeClass("active");
		$(this).find(".hoverdiv").css({left: 15});
	}
});

$(document).on("click", "#text_tab .word", function(e){
	e.preventDefault();
	$(this).find(".hoverdiv").toggleClass("clicked");
});

function getData(query, params, callback, target) {
	var url = Whitelab.baseUrl + query;
//	console.log(url);
	
	var xhr = createCORSRequest('POST', url);
	if (!xhr) {
		return;
	}
	
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	
	xhr.onload = function() {
//		console.log(xhr.responseText);
		callback(target, JSON.parse(xhr.responseText), url+"?"+params);
	};

	xhr.onerror = function() {
	};

	xhr.send(params);
}

function getPosData(query, params, callback, target, pos, color) {
	var url = Whitelab.baseUrl + query;
//	console.log(url);
	
	var xhr = createCORSRequest('POST', url);
	if (!xhr) {
		return;
	}
	
	xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
	
	xhr.onload = function() {
//		console.log(xhr.responseText);
		callback(target, JSON.parse(xhr.responseText), pos, color, url+"?"+params);
	};

	xhr.onerror = function() {
	};

	xhr.send(params);
}

function createCORSRequest(method, url) {
	var xhr = new XMLHttpRequest();
	if ("withCredentials" in xhr) {
		// XHR for Chrome/Firefox/Opera/Safari.
		xhr.open(method, url, true);
	} else if (typeof XDomainRequest != "undefined") {
		// XDomainRequest for IE.
		xhr = new XDomainRequest();
		xhr.open(method, url);
	} else {
		// CORS not supported.
		xhr = null;
	}
	return xhr;
}

