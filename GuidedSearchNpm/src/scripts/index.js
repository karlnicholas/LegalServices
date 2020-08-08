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
function loadPage() {
	var sURLVariables = getURLParameters();
	var path = "";
	var term = "";
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameter = sURLVariables[i].split('=');
		if (sParameter[0].toLowerCase() === 'path' ) {
			path = sParameter[1];
		}
		if (sParameter[0].toLowerCase() === 'term' ) {
			term = sParameter[1];
		}
	}
	$.getJSON( "http://localhost:8080?path="+path+"&term="+term, function( viewModel ) {
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
$( document ).ready(function() {
	loadPage();
    $(document).on("click", "div.row" , function() {
    	var clickedBtnID = $(this).attr('id');
    	setGetParam('path',clickedBtnID);
    	var searchInput = document.getElementById('search-input').value;
    	setGetParam('term',searchInput);
    	console.log("searchInput" + searchInput);
    	loadPage();
    });
    $(document).on("click", "li.breadcrumb-item" , function() {
    	var clickedBtnID = $(this).attr('id');
    	setGetParam('path',clickedBtnID);
    	loadPage();
    });
    $("#search-clear").click(function(event) {
    	$("#search-input").val('');
    	event.preventDefault();
    });
});

