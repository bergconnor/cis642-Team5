// these variables will be used to keep the view consistent each time a new map is created
var zoom = 4;
var center = {lat: 39.8333333, lng: -98.585522}; // center is where the user is looking at in the map now

// create a map based on the configuration
// note: a new map will be created each time this method is called, the new map will overwrite the old one
function initMap() {
  var map = new google.maps.Map(document.getElementById('map'), {
    center: center,
    zoom: zoom
  });

//event handler to remember the new zoom every time the zoom gets changed
map.addListener('zoom_changed', function() {
zoom = map.zoom;
});

//event handler to remember the new center every time the center gets changed
map.addListener('center_changed', function() {
center = map.center;
});


  // Change this depending on the name of your PHP or XML file
  downloadUrl('../lib/create_xml.php', function(data) {
    var xml = data.responseXML;
    var markers = xml.documentElement.getElementsByTagName('marker');
    console.log(markers);
    Array.prototype.forEach.call(markers, function(markerElem) {
      var name = markerElem.getAttribute('name');
      var organization = markerElem.getAttribute('organization');
      var type = markerElem.getAttribute('type');
      var comment = markerElem.getAttribute('comment');
	  var concentration = markerElem.getAttribute('concentration');
      var point = new google.maps.LatLng
      (
        parseFloat(markerElem.getAttribute('latitude')),
        parseFloat(markerElem.getAttribute('longitude'))
      );
	  
	  //create info window when a marker gets clicked
	  var infowincontent = document.createElement('div');
	  
	  // create bolded text for name
      var strong = document.createElement('strong');
      strong.textContent = name
	  
      infowincontent.appendChild(strong);
      infowincontent.appendChild(document.createElement('br'));
	  
	  //create text for organization
      var text = document.createElement('text');
      text.textContent = organization;
      infowincontent.appendChild(text);
      infowincontent.appendChild(document.createElement('br'));

	  //create text for which kind of test this marker represents 
      var text3 = document.createElement('text');
      text3.textContent = type + " test";
	  
      infowincontent.appendChild(text3);
      infowincontent.appendChild(document.createElement('br'));
	  
	  //create text for the concentration of the test this marker represents
      var text5 = document.createElement('text');
      text5.textContent = concentration;
	  
      infowincontent.appendChild(text5);
      infowincontent.appendChild(document.createElement('br'));

	  //create text for comments written by the android app user
      var text4 = document.createElement('text');
      text4.textContent = comment;
	  
      infowincontent.appendChild(text4);
      infowincontent.appendChild(document.createElement('br'));

	  //create text for a link with more information about this marker
	  //(it is just a coloring for entry in the "table" page in the website)
      var text2 = document.createElement('text');
      text2.textContent = "More Info";
	  
	  //create a link for "More Info"
      var ul =  document.createElement('a');
      ul.setAttribute('href', "table.php");
	  
	  //create and event handler that will call the 
	  //setID function 
      var d = markerElem.getAttribute('id');
      ul.setAttribute('onclick', "setId('"+d+"')");
      ul.setAttribute('id_number', markerElem.getAttribute('id'));
	  
      ul.appendChild(text2);
      infowincontent.appendChild(ul);

      // change the color of the marker based on the kind of test 
	  //(now just Nitrate and Phosphate)
      var pinColor;
      var type = markerElem.getAttribute('type');
	 
      var colorRange = type;
	  //note: this assumes 5 color levels
      switch (colorRange) {
        case 'Phosphate':
		  var start = 80
		  var end = 88
		  var jump = (end-start)/4 // 4 because we want 5 colors
		  //chose colors dpending on how much Phosphate concentration was found
		  if (concentration<=start)
			  pinColor = 0xe6e6ff;
		  if (start<concentration&&concentration<=start+jump)
			  pinColor = 0xb3b3ff;
		  if (start+jump<concentration&&concentration<=start+jump*2)
			  pinColor = 0x8080ff;
		  if (start+jump*2<concentration&&concentration<=start+jump*3)
			  pinColor = 0x4d4dff;
		  if (start+jump*3<concentration)
			  pinColor = 0x3939ac;
          
          break;
        case 'Nitrate':
		  
		  var start = 80
		  var end = 88
		  var jump = (end-start)/4// 4 because we want 5 colors
		  //chose colors dpending on how much Nitrate concentration was found
          if (concentration<=start)
			  pinColor = 0xffe6e6;
		  if (start<concentration&&concentration<=start+jump)
			  pinColor = 0xffb3b3;
		  if (start+jump<concentration&&concentration<=start+jump*2)
			  pinColor = 0xff6666;
		  if (start+jump*2<concentration&&concentration<=start+jump*3)
			  pinColor = 0xff3333;
		  if (start+jump*3<concentration)
			  pinColor = 0xb30000;
          break;
      }
		
      //create the pin marker image from a list of google images for pins
      //toString(16) is used because pinColor is in hexdecimal
	  //Note: not all colors in color pickers have images
	  var pinImage;
		try {
			pinImage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" +
			pinColor.toString(16),
			new google.maps.Size(21, 34),
			new google.maps.Point(0,0),
			new google.maps.Point(10, 34));
		}
		catch(err) {
			console.log( err.message);
			pinImage = google.maps.SymbolPath.BACKWARD_CLOSED_ARROW
		}
       
        var pinShadow = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_shadow",
        new google.maps.Size(40, 37),
        new google.maps.Point(0, 0),
        new google.maps.Point(12, 35));
        var infoWindow = new google.maps.InfoWindow;

      //create marker in the map
      var marker = new google.maps.Marker( {
        map: map,
        icon: pinImage,
        shadow: pinShadow,
        position: point,
      });
	  
	  //Label unverified pins with P (for Pending)
      if (markerElem.getAttribute('verified')== 0) {
        marker = new google.maps.Marker( {
          map: map,
          icon: pinImage,
          shadow: pinShadow,
          position: point,
          strokeColor: 'green',
          label: {text: 'P', color: 'white'},
        });
		
		// color of the label
        marker.label.color = 'white';
        marker.icon.labelOrigin = {x: 11, y: 11};
      }

      //create the info window when the marker gets clicked
      marker.addListener('click', function() {
        infoWindow.setContent(infowincontent);
        infoWindow.open(map, marker);
      });
    });
  });
}

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
  // create a query based on configurations
  var query =  createQuery();
  console.log(query);

  // pass the query to create_xml page in a request
  request.open("GET", url+"?q=" + query, true);
  request.send(null);
}

function doNothing() {}

function changeSign(inequalitySign) {
  if(document.getElementById(inequalitySign).textContent == '<')
    document.getElementById(inequalitySign).textContent = '>';
  else
    document.getElementById(inequalitySign).textContent = '<';
}

function clearBox(numberBox) {
  document.getElementById(numberBox).value = '';
}

//Create the query which will be passed to create_xml
//it will be used to filter the markers that will show up in the map
function createQuery() {
  var pendingSamples = document.getElementById('pendingSamples').checked;
  var precipitationLevel = document.getElementById('precipitationLevel').value;
  var concentrationLevel = document.getElementById('concentrationLevel').value;
  var precipitation = '';
  var concentration = '';
  var verified = " and verified = 1 ";
  var id = '';
  if(pendingSamples)
    verified = " and verified > -1 ";
  console.log(isNaN(precipitationLevel));
  console.log(0>(precipitationLevel));
  //data validation for precipitation
  if(isNaN(precipitationLevel) || precipitationLevel < 0 ) {
    precipitationLevel = '';
    document.getElementById('precipitationLevel').value = '';
  }

  if(precipitationLevel!='') {
    if(document.getElementById('inequalitySign1').textContent == '<')
      precipitation = ' and precipitation < ' +   document.getElementById('precipitationLevel').value;
    else
      precipitation = ' and precipitation > ' +   document.getElementById('precipitationLevel').value;
  }
  
  //data validation for concentration
  if(isNaN(concentrationLevel) || concentrationLevel < 0 ) {
    concentrationLevel = '';
    document.getElementById('concentrationLevel').value = '';
  }
  if(concentrationLevel!='') {
    if(document.getElementById('inequalitySign2').textContent == '<')
      concentration = ' and concentration < ' +   document.getElementById('concentrationLevel').value;
    else
      concentration = ' and concentration > ' +   document.getElementById('concentrationLevel').value;
  }

  var querySetUp = "SELECT " +
    " m.id 'id', m.user_id 'userid', DATE_FORMAT(m.date, '%m-%d-%Y') 'date', m.latitude 'latitude' , m.longitude 'longitude'," +
    " m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation', m.concentration 'concentration', " +
    " m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization'," +
    " u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'" +

    " FROM markers m "+
      " JOIN users u ON m.user_id = u.id"+
      " JOIN tests t ON m.test_id = t.id "+

    " WHERE true " + precipitation + concentration+ verified;
  var query = querySetUp;

  return query;
}

//used in "More Info" when it is clicked
//to send id of the marker to the "table" page 
function setId(id) {
  sessionStorage.setItem('marker', id);
}
