var Whitelab = {
	baseUrl : null,
	blsUrl : null,
	language : null,
	tab : null,
	doDebug : false,
	doDebugXhrResponse : false, // log full XHR responses? (long)
	exportLimit : 50000,
	
	confirmExport : function() {
		if (Whitelab.language === "en")
			return confirm("The export may take some time. Please do not close this window until the export is finished!\n\nN.B.: Only the first "+Whitelab.exportLimit+" results will be exported.\n\nContinue?");
		else
			return confirm("De export kan enige tijd in beslag nemen. Sluit dit scherm niet voordat de export afgerond is!\n\nN.B.: Alleen de eerste "+Whitelab.exportLimit+" resultaten worden geëxporteerd.\n\Doorgaan?");
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
	
	getData : function(url, params, callback, target, update) {
		var xhr = Whitelab.createRequest('POST', url);
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
	
	home : {
		setSizes : function() {
			var h = $(window).innerHeight() - 135;
			$("#homepage").css("height",h+"px");
		}
	}
};
