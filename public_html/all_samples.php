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
<th>Latitude</th>
<th>Longitude</th>
</tr>
</table>
<script>
var markerid = 1;
var name = "none"
var orgnization = "none"
var lat = "none"
var log = "none"
downloadUrl('create_xml.php', function(data) {
            var xml = data.responseXML;
            var markers = xml.documentElement.getElementsByTagName('marker');
            Array.prototype.forEach.call(markers, function(markerElem) {
				 

					name = markerElem.getAttribute('name');
					orgnization = markerElem.getAttribute('organization');
					lat = markerElem.getAttribute('latitude');
					log = markerElem.getAttribute('longitude');
					markerid = markerElem.getAttribute('id');
					var row = document.createElement("tr");
					 
					var nameCell  = document.createElement("td");
					var orgnizationCell  = document.createElement("td");
					var latCell = document.createElement("td");
					var logCell  = document.createElement("td");
					var markeridCell = document.createElement("td");
					
					nameCell.innerHTML = name;
					orgnizationCell.innerHTML = orgnization;
					latCell.innerHTML = lat;
					logCell.innerHTML = log;
					markeridCell.innerHTML = markerid;
					
					row.appendChild(markeridCell);
					row.appendChild(nameCell);
					row.appendChild(orgnizationCell);
					row.appendChild(latCell);
					row.appendChild(logCell);
					

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