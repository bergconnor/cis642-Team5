<?php
// Start the session
session_start();
?>
<!DOCTYPE HTML>
<html>  
<body>

<table border="1">
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
<tr>
<td id = "markerid"> not working yet</td>
<td id = "name">Row 1, Column 2</td>
<td id = "orgnization">Row 1, Column 3</td>
<td id = "email">Row 1, Column 4</td>
<td id = "type">Row 1, Column 5</td>
<td id = "lat">Row 1, Column 6</td>
<td id = "log">Row 1, Column 7</td>
<td id = "temperature">Row 1, Column 8</td>
<td id = "precipitation">Row 1, Column 9</td>
<td id = "comment">Row 1, Column 10</td>
</tr>
</table>
<script>
var markerid = sessionStorage.getItem('marker');
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
				if (markerid==markerElem.getAttribute('id'))
				{

					name = markerElem.getAttribute('name');
					orgnization = markerElem.getAttribute('organization');
					email = markerElem.getAttribute('email');
					type = markerElem.getAttribute('type');
					lat = markerElem.getAttribute('latitude');
					log = markerElem.getAttribute('longitude');
					temperature = markerElem.getAttribute('temperature');
					precipitation = markerElem.getAttribute('precipitation');
					comment = markerElem.getAttribute('comment');
					document.getElementById("markerid").innerHTML = markerid;
					document.getElementById("name").innerHTML = name;
					document.getElementById("orgnization").innerHTML = orgnization;
					document.getElementById("email").innerHTML = email;
					document.getElementById("type").innerHTML = type;
					document.getElementById("lat").innerHTML = lat;
					document.getElementById("log").innerHTML = log;
					document.getElementById("temperature").innerHTML = temperature;
					document.getElementById("precipitation").innerHTML = precipitation;
					document.getElementById("comment").innerHTML = comment;
					
	
				}
   
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
		var query =  "select "+ 
			 " m.id 'id', m.user_id 'userid', m.latitude 'latitude' , m.longitude 'longitude',"+
			 " m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation',"+
			 " m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization',"+
			 " u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'"+
				
			 " from markers m "+
					" join users u on m.user_id = u.id"+
					" join tests t on m.test_id = t.id ";
					console.log(query);
        request.open('GET', url, true);
		request.open("GET", url+"?q=" + query, true);
        request.send(null);
      }

      function doNothing() {}
</script>
</body>
</html>