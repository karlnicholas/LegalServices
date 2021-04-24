import '../styles/index.scss';
function getURLParameters() {
	var sPageURL = window.location.search.substring(1);
	return sPageURL.split('&');
}
function displayReference(entry, items) {
	if ( !entry.pathPart && !entry.sectionText ) {
		var anyval = items.length;
		var hiddenFrag = $("#hidden-frag").val();
		items.push( "<div class='row' style='cursor:pointer' id='" + entry.fullFacet + "'>");
		items.push( "<div class='col-sm-1' style='cursor:pointer'>");
		if ( entry.entries.length > 0 && hiddenFrag === 'true' ) {
			items.push( "<span class='icon' data-toggle='collapse' data-target='#collapse" + anyval + "'><svg class='bi bi-chevron-right' width='20' height='20' viewBox='0 0 20 20' fill='currentColor' xmlns='http://www.w3.org/2000/svg'><path fill-rule='evenodd' d='M6.646 3.646a.5.5 0 01.708 0l6 6a.5.5 0 010 .708l-6 6a.5.5 0 01-.708-.708L12.293 10 6.646 4.354a.5.5 0 010-.708z'/></svg></span></div>" );
		} else {
	    	items.push("&nbsp;</div>");	    	
		}
		items.push( "<div class='reference col-sm-3' id='" + entry.fullFacet + "'>" + entry.displayTitle);
	    if ( entry.count > 0 ) {
	    	items.push('<span class="badge badge-primary pull-right">'+entry.count+'</span></div>');	    	
	    } else {
	    	items.push("&nbsp;</div>");	    	
	    }
	    items.push( "<div class='reference col-sm-6' id='" + entry.fullFacet + "'>" + entry.statutesBaseClass.title + "</div>" );
	    if ( entry.statutesBaseClass.statuteRange.sNumber != null && entry.statutesBaseClass.statuteRange.eNumber != null) {
	    	items.push( "<div class='reference col-sm-2' id='" + entry.fullFacet + "'>§§ " + entry.statutesBaseClass.statuteRange.sNumber.sectionNumber + " - " + entry.statutesBaseClass.statuteRange.eNumber.sectionNumber + "</div>" );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber != null && entry.statutesBaseClass.statuteRange.eNumber == null) {
	    	items.push( "<div class='reference col-sm-2' id='" + entry.fullFacet + "'>§§ " + entry.statutesBaseClass.statuteRange.sNumber.sectionNumber + "</div>" );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber == null && entry.statutesBaseClass.statuteRange.eNumber != null) {
	    	items.push( "<div class='reference col-sm-2' id='" + entry.fullFacet + "'>§§ " + entry.statutesBaseClass.statuteRange.eNumber.sectionNumber + "</div>" );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber == null && entry.statutesBaseClass.statuteRange.eNumber == null) {
	    	items.push( "<div class='reference col-sm-2' id='" + entry.fullFacet + "'></div>" );
	    }
		items.push( "</div>" );
		if ( entry.entries.length != 0 ) {
			items.push( "<div class='collapse' id='collapse" + anyval + "'>");
			for(var i=0; i < entry.entries.length; ++i) {
				items.push( "<p>"+entry.entries[i].text+"</p>");
			}
			items.push( "</div>" );
		}
	}
}
function displayText(entry, items) {
	if ( entry.sectionText ) {
		items.push( "<div class='row'><div class='col-sm-12'>" + entry.text + "</div></div>");
	}
}
function recurse(entries, index, rows) {
	if ( index < entries.length ){
		displayReference(entries[index], rows);
		displayText(entries[index], rows);
		if(entries[index].pathPart) {
	        recurse(entries[index].entries, 0, rows);
	    } else {
	    	recurse(entries, index+1, rows);
	    }
	}
}

function breadcrumbs(entries, lis) {
	if ( entries != null && entries.length == 1) {
		var entry = entries[0];
		if ( entry.entries != null && entry.entries.length == 1) {
			lis.push("<li class='breadcrumb-item' id='" + entry.fullFacet + "' style='cursor:pointer;' >" + entry.text + "</li>");
		} else if ( entry.entries != null && entry.entries.length > 1) {
			lis.push("<li class='breadcrumb-item' id='" + entry.fullFacet + "' style='cursor:pointer;' >" + entry.text + " - " + entry.statutesBaseClass.title + "</a></li>");
		}
		 breadcrumbs(entry.entries, lis);
	}
}
function isEmpty(value) {
  return typeof value == 'string' && !value.trim() || typeof value == 'undefined' || value === null;
}
function getSearchTerm() {
	var term = $('#search-input').val();
	var hiddenTerm = $('#hidden-term').val();

	// otherwise build the term
	var inAny = $('#inAny').val();
	var inAll = $('#inAll').val();
	var inNot = $('#inNot').val();
	var inExact = $('#inExact').val();

	// navbar clear term and fragments
	var fterm = '';
	if ( 
		!isEmpty(inAll)
		|| !isEmpty(inNot)
		|| !isEmpty(inAny)
		|| !isEmpty(inExact)
	) {
		if ( !isEmpty(inAll) ) {
			fterm = fterm + appendOp(inAll, '+');
		}
		if ( !isEmpty(inNot) ) {
			fterm = fterm + appendOp(inNot, '-');
		}
		if ( !isEmpty(inAny) ) {
			fterm = fterm + inAny + ' ';
		}
		if ( !isEmpty(inExact) ) {
			fterm = fterm + '"' + inExact + '"';
		}
	}
	if ( !isEmpty(fterm) && ( isEmpty(hiddenTerm) || (fterm != hiddenTerm && term === hiddenTerm)) ) {
		term = fterm;
	}
	return term;
}
function appendOp(val, op) {
	val = val.trim();
	if ( isEmpty(val)) return '';
	var terms = val.trim().split(' ');
	var sb = '';
	var i;
	for (i = 0; i < terms.length; ++i) {
		sb = sb + op + terms[i] + ' ';
	}
	return sb;
}
function setAdvancedSearchFields(term) {
	if ( term == null || isEmpty(term)) return;
	var terms = term.split(' ');
	var all = '';
	var not = '';
	var any = '';
	var exact = '';
	var ex = false;
	var i;
	for(i=0; i < terms.length; ++i) {
		var t = terms[i];
		if ( !ex && t.startsWith('+')) all=all.concat(t.substring(1) + " ");
		else if ( !ex && t.startsWith('-')) not=not.concat(t.substring(1) + ' ' );
		else if ( !ex && (t.startsWith('"') && t.trim().endsWith('"')) ) {
			exact=exact.concat(t.substring(1, t.length-1) + " ");
		}
		else if ( !ex && t.startsWith('"')) {
			exact=exact.concat(t.substring(1) + ' ');
			ex = true;
		}
		else if ( ex && !t.endsWith('"') ) {
			exact=exact.concat(t) + ' ';
		}
		else if ( ex && t.endsWith('"')) {
			exact=exact.concat(t.substring(0, t.length-1)) + ' ';
			ex = false;
		}
		else any = any.concat(t) + ' ';
	}
	$('#inAll').val(all.trim());
	$('#inNot').val(not.trim());
	$('#inAny').val(any.trim());
	$('#inExact').val(exact.trim());
}
function clearSearchTerms() {
	$('#search-input').val('');
	$('#inAny').val('');
	$('#inAll').val('');
	$('#inNot').val('');
	$('#inExact').val('');
}
function loadPage() {
	var sURLVariables = getURLParameters();
	var urlPath = '';
	var firstArg = '?';
	$("#hidden-path").val();
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameter = sURLVariables[i].split('=');
		if (sParameter[0].toLowerCase() === 'path' ) {
			urlPath = urlPath + firstArg + "path=" + sParameter[1];
			$("#hidden-path").val(sParameter[1]);
			firstArg = '&';
			break;
		}
	}
	var term = getSearchTerm();
	if ( !isEmpty(term)) {
		setGetParam("term", term);
		urlPath = urlPath + firstArg + "term=" + term;
		firstArg = '&';
	}
	setAdvancedSearchFields(term);
	$("#hidden-term").val(term);
	$("#search-input").val(term);
	checkFrag();
	var hiddenFrag = $("#hidden-frag").val();
	if ( hiddenFrag === 'true') {
		$("#search-frag").addClass("btn-primary").removeClass("btn-light");
		setGetParam("frag", 'true');
		urlPath = urlPath + firstArg + "frag=true";
		firstArg = '&';
	} else {
		$("#search-frag").addClass("btn-light").removeClass("btn-primary");
	}
	var context = window.location.pathname.substring(0, window.location.pathname.indexOf("/",2)); 
	var apiUrl =window.location.protocol+"//"+ window.location.host +context+"/api";
	console.log("context " + window.location.host);
	$.getJSON( '/api' + urlPath, function( viewModel ) {
	  var entries = viewModel.entries;
	  var lis = [];
	  lis.push("<li class='breadcrumb-item' id='' style='cursor:pointer;'>Home</li>");
	  breadcrumbs(entries, lis);
	  if ( !isEmpty(term)) {
		  lis.push('<li><span class="badge badge-primary pull-right">'+viewModel.totalCount+'</span></li>');
	  }
	  $('#breadcrumbs').html(lis.join( "" ));
	  var rows = [];
	  recurse(entries, 0, rows);
	  $('#cand').html(rows.join( "" ));
	});
}
function checkFrag() {
	var hiddenTerm = $("#hidden-term").val();
	var hiddenPath = $("#hidden-path").val();
	if ( isEmpty(hiddenTerm) || isEmpty(hiddenPath) ) {
		$("#hidden-frag").val('');
	}
}
function toggleFrag() {
	var hiddenTerm = $("#hidden-term").val();
	var hiddenPath = $("#hidden-path").val();
	var hiddenFrag = $("#hidden-frag").val();
	if ( isEmpty(hiddenTerm) || isEmpty(hiddenPath) || hiddenFrag === 'true') {
		$("#hidden-frag").val('');
	} else {
		$("#hidden-frag").val('true');
	}
}
function setGetParam(key,value) {
  if (history.pushState) {
    var params = new URLSearchParams(window.location.search);
    params.set(key, value);
    var newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?' + params.toString();
    window.history.pushState({path:newUrl},'',newUrl);
  }
}
function deleteGetParam(key) {
  if (history.pushState) {
    var params = new URLSearchParams(window.location.search);
    params.delete(key);
    var newUrl = window.location.protocol + "//" + window.location.host + window.location.pathname + '?' + params.toString();
    window.history.pushState({path:newUrl},'',newUrl);
  }
}

$( document ).ready(function() {
	loadPage();
    $(document).on("click", "div.reference" , function() {
    	var clickedBtnID = $(this).attr('id');
    	setGetParam('path',clickedBtnID);
    	loadPage();
    });
    $(document).on("click", "li.breadcrumb-item" , function() {
    	var clickedBtnID = $(this).attr('id');
    	setGetParam('path',clickedBtnID);
    	loadPage();
    });
    $("#search-clear").click(function(event) {
    	event.preventDefault();
    	clearSearchTerms();
    	deleteGetParam("term");
    	loadPage();
    });
    $("#search-frag").click(function(event) {
    	event.preventDefault();
    	toggleFrag();
    	loadPage();
    });
    $("#search-submit").click(function(event) {
    	event.preventDefault();
    	loadPage();
    });
    $("#search-input").keypress(function(event) {
    	if ( event.keyCode == 13) {
	    	loadPage();
	    	return false;
    	}
    	return true;
    });
    $("#search-form-input").click(function(event) {
    	event.preventDefault();
    	loadPage();
    });
});
