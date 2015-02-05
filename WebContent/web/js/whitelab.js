var Whitelab = {
	baseUrl : null,
	blsUrl : null,
	language : null,
	tab : null,
	doDebug : true,
	doDebugXhrResponse : false, // log full XHR responses? (long)
	exportLimit : 50000,
	
	confirmExport : function() {
		if (Whitelab.language === "en")
			return confirm("Your query exceeds the maximum export size. Only the first "+Whitelab.exportLimit+" results will be exported.\n\nDo you want to continue?\n");
		else
			return confirm("Uw zoekopdracht overschrijdt de export limiet. Alleen de eerste "+Whitelab.exportLimit+" resultaten worden geëxporteerd.\n\nWilt u doorgaan?\n");
	},
	
	cookies : {
		accept : function() {
			Whitelab.cookies.setCookie("opensonar",true,30);
			$("div.cookies").removeClass("active");
			$("nav.topbar").css({top : 0});
		},
		
		checkCookie : function(name) {
			return $.cookie(name);
		},
		
		setCookie : function(name,value,days) {
			var date = new Date();
			date.setTime(date.getTime() + ( 1000 * 60 * 60 * 24 * parseInt(days)));
			
			$.cookie(name, "'"+value+"'", {path: '/', expires: date });
			return;
		}
	},
	
	createRequest : function(method, url) {
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
	},
	
	debug : function(msg) {
		if (Whitelab.doDebug) {
			console.log(msg);
		}
	},
	
	debugXhrResponse : function(msg) {
		if (Whitelab.doDebug && Whitelab.doDebugXhrResponse) {
			console.log(msg);
		}
	},
	
	getBlacklabData : function(type, params, callback, target) {
		var xhr = Whitelab.createRequest('GET', Whitelab.blsUrl + type);
		if (!xhr) {
			return;
		}
		
		if (params != null && params.indexOf("outputformat=") == -1) {
			params = params + "&outputformat=json";
		} else if (params == null || params.length == 0) {
			params = "outputformat=json";
		}

		Whitelab.debug(Whitelab.blsUrl + type + "?" + params);
		
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		xhr.onload = function() {
//			if (/^[\],:{}\s]*$/.test(xhr.responseText.replace(/\\["\\\/bfnrtu]/g, '@').
//					replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
//					replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
				Whitelab.debugXhrResponse(xhr.responseText);
				var resp = JSON.parse(xhr.responseText);
				Whitelab.debugXhrResponse("response:");
				Whitelab.debugXhrResponse(resp);
				callback(resp,target);
//			} else {
//				Whitelab.debug("invalid JSON");
//			}
		};

		xhr.onerror = function() {
			Whitelab.debug("Failed to proces request.");
		};

		xhr.send(params);
	},
	
	getData : function(params, callback, target, update) {
		var xhr = Whitelab.createRequest('POST', Whitelab.baseUrl+"query");
		if (!xhr) {
			return;
		}
		
		if (params != null && params.indexOf("lang=") == -1) {
			params = params+"&lang="+Whitelab.language;
		}

		Whitelab.debug("params: "+decodeURI(params));
		
		xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
		
		xhr.onload = function() {
			if (/^[\],:{}\s]*$/.test(xhr.responseText.replace(/\\["\\\/bfnrtu]/g, '@').
					replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').
					replace(/(?:^|:|,)(?:\s*\[)+/g, ''))) {
				var resp = JSON.parse(xhr.responseText);
				Whitelab.debugXhrResponse("response:");
				Whitelab.debugXhrResponse(resp);
				callback(resp,target,update);
			} else {
				$("#status_"+target).html("ERROR");
				$("#result_"+target).html("ERROR - Could not process request.");
			}
		};

		xhr.onerror = function() {
			$("#status_"+target).html("ERROR");
			$("#result_"+target).html("ERROR - Could not connect to server.");
		};

		xhr.send(params);
	},
	
	readFile : function(f, callback) {
		if (!f) {
	        alert("Failed to load file");
	    } else if (!f.type.match('text.*')) {
			    alert(f.name + " is not a valid text file.");
	    } else {
	    	var reader = new FileReader();
	        reader.onload = callback;
	        reader.readAsText(f);
	    }
	},
	
	switchTab : function(target) {
		if (target !== Whitelab.tab) {
			if (Whitelab.language != null) {
				window.location = window.location.protocol+target+"?lang="+Whitelab.language;
			} else {
				window.location = window.location.protocol+target;
			}
		}
	},
	
	switchLanguage : function(lang) {
		Whitelab.language = lang;
		if (Whitelab.tab === "search" && Whitelab.search.tab !== "result" && Whitelab.search.tab !== "document") {
			window.location = window.location.protocol+Whitelab.tab+"?lang="+Whitelab.language+"&tab="+Whitelab.search.tab;
		} else {
			window.location = window.location.protocol+Whitelab.tab+"?lang="+Whitelab.language;
		}
	},
	
	transform : function(xml, xslSheet) {	
		// get stylesheet
		xhttp = new XMLHttpRequest();
		xhttp.open("GET", xslSheet, false);
		xhttp.send("");
		
		var parser = new DOMParser();
		var sheet = parser.parseFromString( xhttp.responseText, "text/xml");
		
		// apply translation
		var result = "";
		if(window.ActiveXObject) {
			// Internet Explorer has to be the special child of the class -_-
			sheet = new ActiveXObject("Microsoft.XMLDOM");
			sheet.async = false;
			sheet.loadXML(xhttp.responseText);
			result = xml.transformNode(sheet);
		} else {
			var processor = new XSLTProcessor();
			processor.importStylesheet(sheet);
			result = processor.transformToFragment(xml, document);
		}
		
		return result;
	},
	
	home : {
		
		setSizes : function() {
			var h = $(window).innerHeight() - 135;
			$("#homepage").css("height",h+"px");
		}
	}
};
