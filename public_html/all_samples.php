<?php
// Start the session
session_start();
?>
<!DOCTYPE HTML>
<html>  
<body>

<table  id = 'table' border="1">
<tr>
<th>Marker ID</th>
<th>Name</th>
<th>Orgnization</th>
<th>Email</th>
<th>Test Type</th>
<th>Latitude</th>
<th>Longitude</th>
<th>Temperature</th>
<th>Precipitation</th>
<th>Comment</th>
</tr>
</table>
<script>
var markerid = 1;
var name = "none"
var orgnization = "none"
var email = "none"
var type = "none"
var lat = "none"
var log = "none"
var temperature = "none"
var precipitation = "none"
var comment = "none"
downloadUrl('create_xml.php', function(data) {
            var xml = data.responseXML;
            var markers = xml.documentElement.getElementsByTagName('marker');
            Array.prototype.forEach.call(markers, function(markerElem) {
				 

					name = markerElem.getAttribute('name');
					orgnization = markerElem.getAttribute('organization');
					email = markerElem.getAttribute('email');
					type = markerElem.getAttribute('type');
					lat = markerElem.getAttribute('latitude');
					log = markerElem.getAttribute('longitude');
					temperature = markerElem.getAttribute('temperature');
					precipitation = markerElem.getAttribute('precipitation');
					comment = markerElem.getAttribute('comment');
					
					markerid = markerElem.getAttribute('id');
					var row = document.createElement("tr");
					 
					var nameCell  = document.createElement("td");
					var orgnizationCell  = document.createElement("td");
					var emailCell = document.createElement("td");
					var typeCell = document.createElement("td");
					var temperatureCell  = document.createElement("td");
					var precipitationCell  = document.createElement("td");
					var commentCell  = document.createElement("td");
					var latCell = document.createElement("td");
					var logCell  = document.createElement("td");
					var markeridCell = document.createElement("td");
					
					nameCell.innerHTML = name;
					orgnizationCell.innerHTML = orgnization;
					temperatureCell.innerHTML = temperature;
					precipitationCell.innerHTML = precipitation;
					commentCell.innerHTML = comment;
					typeCell.innerHTML = type;
					emailCell.innerHTML = email;
					latCell.innerHTML = lat;
					logCell.innerHTML = log;
					markeridCell.innerHTML = markerid;
					
					row.appendChild(markeridCell);
					row.appendChild(nameCell);
					row.appendChild(orgnizationCell);
					row.appendChild(emailCell);
					row.appendChild(typeCell);
					row.appendChild(latCell);
					row.appendChild(logCell);
					row.appendChild(temperatureCell);
					row.appendChild(precipitationCell);
					row.appendChild(commentCell);
					

					document.getElementById('table').appendChild(row);
					
	
				 
   
            });
          });
function downloadUrl(url, callback) {
        var request = window.ActiveXObject ?
        new ActiveXObject('Microsoft.XMLHTTP') :
        new XMLHttpRequest;

        request.onreadystatechange = function() {
          if (request.readyState == 4) {
            request.onreadystatechange = doNothing;
            callback(request, request.status);
          }
        };
		var query = "select "+ 
			 " m.id 'id', m.user_id 'userid', m.latitude 'latitude' , m.longitude 'longitude',"+
			 " m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation',"+
			 " m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization',"+
			 " u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'"+
				
			 " from markers m "+
					" join users u on m.user_id = u.id"+
					" join tests t on m.test_id = t.id ";
        request.open('GET', url, true);
		request.open("GET", url+"?q=" + query, true);
        request.send(null);
      }

      function doNothing() {}
</script>
</body>
</html>