
function Cql(s,b) {
	this.columns = new Array();
	this.split = s;
	this.batch = b;
}

function CqlColumn(i) {
	this.index = i;
	this.fields = new Array();
	this.quantifier = null;
	this.before = null;
	this.after = null;
}

function CqlField(t,v,s,o,b,q) {
	this.type = t;
	this.value = v;
	this.sensitive = s;
	this.operator = o;
	this.batch = b;
	this.quantifier = q;
	this.subfields = new Array();
}

Cql.prototype.addEmptyColumn = function() {
	var i = this.columns.length;
	var c = new CqlColumn(i);
	this.columns.push(c);
	return i;
};

Cql.prototype.addEmptyFieldToColumn = function(i) {
	var f = this.addFieldToColumn(i, null, null, false, null, false, null);
	return f;
};

Cql.prototype.addFieldToColumn = function(i,t,v,s,o,b,q) {
	var c = this.columns[i];
	var f = new CqlField(t,v,s,o,b,q);
	c.addField(f);
	return f;
};

CqlField.prototype.addSubField = function(f) {
	this.subfields.push(f);
};

CqlField.prototype.toCqlString = function() {
	var query = "";
	var sensitive = this.sensitive ? "(?-i)" : "(?i)";
	if (this.value.length == 0 || this.value === "[]") {
		query = "[]";
	} else {
		if (this.operator === "is" || this.operator === "regex") {
			query = this.type+"=\""+sensitive+this.value+"\"";
		} else if (this.operator == "not") {
			query = this.type+"!=\""+sensitive+this.value+"\"";
		} else if (this.operator == "contains") {
			query = this.type+"=\""+sensitive+".*"+this.value+".*\"";
		} else if (this.operator == "ends") {
			query = this.type+"=\""+sensitive+".*"+this.value+"\"";
		} else if (this.operator == "starts") {
			query = this.type+"=\""+sensitive+this.value+".*\"";
		}
	}
	if (this.quantifier != null) {
		query = query + this.quantifier;
	}
	return query;
};

CqlColumn.prototype.addField = function(f) {
	this.fields.push(f);
};

Cql.prototype.getQuery = function() {
	var queries = null;
	if (this.split) {
		for (var c = 0; c < this.columns.length; c++) {
			queries = combineOptions(queries,this.columns[c].stringValues());
		}
	} else {
		queries = new Array();
		var str = "";
		for (var c = 0; c < this.columns.length; c++) {
			var cstr = this.columns[c].toCqlString();
			str = str + cstr;
		}
		queries.push(str);
	}
	if (queries != null) {
		var q = "";
		if (this.split)
			q = queries.join(";");
		else
			q = queries.join("|");
		if (q === "")
			q = "[]";
		return q;
	} else {
		return null;
	}
};

CqlColumn.prototype.getFieldByType = function(type) {
	for (var f = 0; f < this.fields.length; f++) {
		if (this.fields[f].type != null && this.fields[f].type === type) {
			return this.fields[f];
		}
	}
	return null;
};

CqlColumn.prototype.stringValues = function() {
	
	this.setQuantifier();
	
	var options = new Array();
	var ands = new Array();
	var ors = new Array();
	for (var f = 0; f < this.fields.length; f++) {
		if (this.fields[f].value != null) {
			var str = this.fields[f].toCqlString();
			if (ands.indexOf(str) == -1) {
				ands.push(str);
			}
		} else {
			for(var s = 0; s < this.fields[f].subfields.length; s++) {
				var str = this.fields[f].subfields[s].toCqlString();
				if (ands.indexOf(str) == -1 && ors.indexOf(str) == -1) {
					ors.push(str);
				}
			}
		}
	}
	var and = ands.join(" & ");
	if (ands.indexOf("[]") > -1) {
		if (this.quantifier == null) {
			options.push("[]");
		} else {
			options.push("[]"+this.quantifier);
		}
	} else {
		if (ors.length > 0) {
			for (var o = 0; o < ors.length; o++) {
				var q = this.quantifier;
				if (q == null) {
					q = "";
				}
				if (ors[o] === "[]") {
					if (options.indexOf("[]"+q) == -1) {
						options.push("[]"+q);
					}
				} else if (and.length == 0) {
					options.push("["+ors[o]+"]"+q);
				} else {
					options.push("["+and+" & "+ors[o]+"]"+q);
				}
			}
		} else {
			var q = this.quantifier;
			if (q == null) {
				q = "";
			}
			options.push("["+and+"]"+q);
		}
	}
	
	for (var o = 0; o < options.length; o++) {
		if (this.before != null) {
			options[o] = this.before+" "+options[o];
		}
		if (this.after != null) {
			options[o] = options[o]+" "+this.after;
		}
	}
	
	return options;
};

CqlColumn.prototype.toCqlString = function() {
	
	this.setQuantifier();
	
	var vals = new Array();
	for (var f = 0; f < this.fields.length; f++) {
		var echk = 0;
		if (this.fields[f].subfields.length > 0) {
			var subvals = new Array();
			for (var s = 0; s < this.fields[f].subfields.length; s++) {
				var subval = this.fields[f].subfields[s].toCqlString();
				if (this.fields[f].subfields[s].value === "[]") {
					echk = 1;
					vals = new Array();
					vals.push(subval);
					break;
				} else if (subvals.indexOf(subval) == -1) {
					subvals.push(subval);
				}
			}
			if (echk == 0) {
				vals.push("("+subvals.join(" | ")+")");
			}
		} else {
			var val = this.fields[f].toCqlString();
			if (this.fields[f].value === "[]") {
				echk = 1;
				vals = new Array();
				vals.push(val);
			} else {
				if (vals.indexOf(val) == -1) {
					vals.push(val);
				}
			}
		}
		if (echk == 1) {
			break;
		}
	}
	var q = "";
	if (this.quantifier != null) {
		q = this.quantifier;
	}
	var res = "["+vals.join(" & ")+"]"+q;
	res = res.replace("[[]]","[]");
	
	if (this.before != null) {
		res = this.before+" "+res;
	}
	if (this.after != null) {
		res = res+" "+this.after;
	}
	
	return res;
	
};

CqlColumn.prototype.setQuantifier = function() {
	for (var f = 0; f < this.fields.length; f++) {
		if (this.fields[f].value != null) {
			if (this.quantifier == null && this.fields[f].quantifier != null) {
				this.quantifier = this.fields[f].quantifier;
			}
			if (this.fields[f].quantifier != null) {
				this.fields[f].quantifier = null;
			}
		} else {
			for(var s = 0; s < this.fields[f].subfields.length; s++) {
				if (this.quantifier == null && this.fields[f].subfields[s].quantifier != null) {
					this.quantifier = this.fields[f].subfields[s].quantifier;
				}
				if (this.fields[f].subfields[s].quantifier != null) {
					this.fields[f].subfields[s].quantifier = null;
				}
			}
		}
	}
	if (this.quantifier != null) {
		this.quantifier = this.quantifier.replace("{,","{1,");
		if (this.quantifier === "{1,1}") {
			this.quantifier = null;
		}
	}
};

function combineOptions(arr1,arr2) {
	var combined = new Array();
	if (arr1 == null) {
		for (var i = 0; i < arr2.length; i++) {
			combined.push(arr2[i]);
		}
	} else {
		for (var i = 0; i < arr1.length; i++) {
			for (var j = 0; j < arr2.length; j++) {
				combined.push(arr1[i]+arr2[j]);
			}
		}
	}
	return combined;
}

