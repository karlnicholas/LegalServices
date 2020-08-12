import '../styles/index.scss';
console.log('webpack starterkit');
function getURLParameters() {
	var sPageURL = window.location.search.substring(1);
	return sPageURL.split('&');
}
function displayReference(entry, items) {
	if ( !entry.pathPart && !entry.sectionText ) {
		items.push( "<div class='row' style='cursor:pointer' id='" + entry.fullFacet + "'>");
		items.push( "<div class='col-sm-4'>" + entry.displayTitle + "</div>" );
	    items.push( "<div class='col-sm-6'>" + entry.statutesBaseClass.title + "</div>" );
	    if ( entry.statutesBaseClass.statuteRange.sNumber != null && entry.statutesBaseClass.statuteRange.eNumber != null) {
	    	items.push( "<div class='col-sm-2'>§§ " + entry.statutesBaseClass.statuteRange.sNumber.sectionNumber + " - " + entry.statutesBaseClass.statuteRange.eNumber.sectionNumber + "</div>" );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber != null && entry.statutesBaseClass.statuteRange.eNumber == null) {
	    	items.push( "<div class='col-sm-2'>§§ " + entry.statutesBaseClass.statuteRange.sNumber.sectionNumber + "</div>" );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber == null && entry.statutesBaseClass.statuteRange.eNumber != null) {
	    	items.push( "<div class='col-sm-2'>§§ " + entry.statutesBaseClass.statuteRange.eNumber.sectionNumber + "</div>" );
	    } else if ( entry.statutesBaseClass.statuteRange.sNumber == null && entry.statutesBaseClass.statuteRange.eNumber == null) {
	    	items.push( "<div class='col-sm-2'></div>" );
	    }

		items.push( "</div>" );
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
	var term = document.getElementById('search-input').value;
	var hiddenTerm = document.getElementById('hidden-term').value;

	// otherwise build the term
	var inAny = document.getElementById('inAny').value;
	var inAll = document.getElementById('inAll').value;
	var inNot = document.getElementById('inNot').value;
	var inExact = document.getElementById('inExact').value;

	// navbar clear term and fragments
	var fterm = '';
	if ( 
		!isEmpty(inAll)
		|| !isEmpty(inNot)
		|| !isEmpty(inAny)
		|| !isEmpty(inExact)
	) {
		if ( !isEmpty(inAll) ) {
			fterm = fterm + inAll + '+';
		}
		if ( !isEmpty(inNot) ) {
			fterm = fterm + inNot + '-';
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
function clearSearchTerms() {
	document.getElementById('search-input').value = '';
	document.getElementById('inAny').value = '';
	document.getElementById('inAll').value = '';
	document.getElementById('inNot').value = '';
	document.getElementById('inExact').value = '';
}
function loadPage() {
	var sURLVariables = getURLParameters();
	var urlPath = '';
	var firstArg = '?';
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameter = sURLVariables[i].split('=');
		if (sParameter[0].toLowerCase() === 'path' ) {
			urlPath = urlPath + firstArg + "path=" + sParameter[1];
			firstArg = '&';
		}
	}
	var term = getSearchTerm();
	if ( !isEmpty(term)) {
		console.log("term4: " + term);
		setGetParam("term", term);
		urlPath = urlPath + firstArg + "term=" + term;
		firstArg = '&';
	}
	$("#hidden-term").val(term);
	$("#search-input").val(term);
	$.getJSON( "http://localhost:8080" + urlPath, function( viewModel ) {
	  var entries = viewModel.entries;
	  var lis = [];
	  lis.push("<li class='breadcrumb-item' id='' style='cursor:pointer;'>Home</li>");
	  breadcrumbs(entries, lis);
	  $('#breadcrumbs').html(lis.join( "" ));
	  var rows = [];
	  recurse(entries, 0, rows);
	  $('#cand').html(rows.join( "" ));
	});
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
    $(document).on("click", "div.row" , function() {
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

