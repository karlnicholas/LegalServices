import '../styles/index.scss';
console.log('webpack starterkit');
function getURLParameters() {
	var sPageURL = window.location.search.substring(1);
	return sPageURL.split('&');
}
function displayReference(entry, items) {
	if ( !entry.pathPart && !entry.sectionText ) {
		items.push( "<div class='row'>" );
		items.push( "<div class='col-sm-4'>" + entry.displayTitle + "</div>" );
	    items.push( "<div class='col-sm-6'>" + entry.statutesBaseClass.title + "</div>" );
	    items.push( "<div class='col-sm-2'>§§ " + entry.statutesBaseClass.statuteRange.sNumber.sectionNumber + " - " + entry.statutesBaseClass.statuteRange.eNumber.sectionNumber + "</div>" );
		items.push( "</div>" );
	}
}
function displayText(entry, items) {
	if ( entry.sectionText ) {
		items.push( "<div class='row'>" + entry.text + "</div>");
	}
}
function recurse(entries, index, items) {
	if ( index < entries.length ){
		displayReference(entries[index], items);
		displayText(entries[index], items);
		if(entries[index].pathPart) {
	        recurse(entries[index].entries, 0, items);
	    } else {
	    	recurse(entries, index+1, items);
	    }
	}
}
$( document ).ready(function() {
	var sURLVariables = getURLParameters();
	var path = "";
	for (var i = 0; i < sURLVariables.length; i++) {
		var sParameter = sURLVariables[i].split('=');
		if (sParameter[0].toLowerCase() === 'path' ) {
			path = sParameter[1];
		}
		console.log(sParameter[0] + ":" + sParameter[1]);
	}
	$.getJSON( "http://localhost:8080?path="+path, function( viewModel ) {
	  var entries = viewModel.entries;
	  var items = [];
	  recurse(entries, 0, items);
	  $('#cand').html(items.join( "" ));
	});
});

