var markerid      = sessionStorage.getItem('marker');
var date          = "none";
var name          = "none";
var orgnization   = "none";
var email         = "none";
var type          = "none";
var lat           = "none";
var log           = "none";
var temperature   = "none";
var precipitation = "none";
var comment       = "none";

downloadUrl('create_xml.php', function(data) {
  var xml = data.responseXML;
  var markers = xml.documentElement.getElementsByTagName('marker');
  Array.prototype.forEach.call(markers, function(markerElem) {
	   if (markerid==markerElem.getAttribute('id')) {
       date           = markerElem.getAttribute('date');
		   name           = markerElem.getAttribute('name');
		   orgnization    = markerElem.getAttribute('organization');
		   email          = markerElem.getAttribute('email');
		   type           = markerElem.getAttribute('type');
		   lat            = markerElem.getAttribute('latitude');
		   log            = markerElem.getAttribute('longitude');
		   temperature    = markerElem.getAttribute('temperature');
		   precipitation  = markerElem.getAttribute('precipitation');
		   comment        = markerElem.getAttribute('comment');

			 document.getElementById("date").innerHTML           = date;
			 document.getElementById("name").innerHTML           = name;
			 document.getElementById("orgnization").innerHTML    = orgnization;
			 document.getElementById("email").innerHTML          = email;
			 document.getElementById("type").innerHTML           = type;
			 document.getElementById("lat").innerHTML            = lat;
			 document.getElementById("log").innerHTML            = log;
			 document.getElementById("temperature").innerHTML    = temperature;
			 document.getElementById("precipitation").innerHTML  = precipitation;
			 document.getElementById("comment").innerHTML        = comment;
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

	var query =  "SELECT "+
			 " m.id 'id', m.user_id 'userid', DATE_FORMAT(m.date, '%m-%d-%Y') 'date', m.latitude 'latitude' , m.longitude 'longitude',"+
			 " m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation',"+
			 " m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization',"+
			 " u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'"+

			 " FROM markers m "+
					" JOIN users u ON m.user_id = u.id"+
					" JOIN tests t ON m.test_id = t.id ";
  request.open('GET', url, true);
	request.open("GET", url+"?q=" + query, true);
  request.send(null);
}

function doNothing() {}
