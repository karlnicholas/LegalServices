$(document).ready(function() {
	var displayResources = $("#display-resources");
	
	displayResources.text("Loading data from JSON source...");
	
	$.ajax({
	  type: "GET",
	  url: "http://localhost:8080/", // Using our resources.json file to serve results
	  success: function(statutesBaseClass) {
	    var output =
	      "<table><thead><tr><th>Result</th><th>Result</th><th>Result</th></thead><tbody>";
	    var entries = statutesBaseClass.entries;
	    for (var i in entries) {
	      output +=
	        "<tr><td>" + entries[i].text + "</td><td>" 
	        + "<td>" + entries[i].statutesBaseClass.title + "</td>"
        	+ "</tr>"
	        ;
	    }
	    output += "</tbody></table>";
	
	    displayResources.html(output);
	    $("table").addClass("table");
	  }
	});
});