import '../styles/index.scss';
console.log('webpack starterkit');
function getURLParameters() {
	var sPageURL = window.location.search.substring(1);
	return sPageURL.split('&');
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
	  var items = [];
	  $.each( viewModel.entries, function( key, value ) {
		items.push( "<div class='row'>" );
		items.push( "<div class='col-sm-4'>" + value.displayTitle + "</div>" );
	    items.push( "<div class='col-sm-6'>" + value.statutesBaseClass.title + "</div>" );
	    items.push( "<div class='col-sm-2'>§§ " + value.statutesBaseClass.statuteRange.sNumber.sectionNumber + " - " + value.statutesBaseClass.statuteRange.eNumber.sectionNumber + "</div>" );
		items.push( "</div>" );
	  });
	  $('#cand').html(items.join( "" ));
	});
});

