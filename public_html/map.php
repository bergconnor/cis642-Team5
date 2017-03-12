<?php
// Start the session
session_start();
?>
 
<html>
  <head>
    <title>Simple Map</title>
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
    <style>
	body
	{
		background-color: #bcd2f4;
		
		
	}
	
	#text 
	{
		background-color: #9aafd1;
		float: right;
	}
	
	#text2
	{
		background-color: #9aafd1;
		margin-left: 200;
		font-weight: bold
	}
	#button  {
		background-color:#8080ff;
		color: white;
		padding: 14px 25px;
		text-align: center;
		text-decoration: none;
		display: inline-block;
		cursor: pointer;
		border: 3px solid black;
	}
	#button:hover, #button:active {
		background-color: #b3b3ff;
		 cursor: pointer;
	}
	
	#button2  {
		background-color: #8080ff;
		color: white;
		padding:  0px 14px;
		text-align: center;
		text-decoration: none;
		display: inline-block;
		cursor: pointer;
		border: 2px solid black;
	}
	#button2:hover, #button2:active {
		background-color: #b3b3ff;
		 cursor: pointer;
	}
	
	#inequalitySign1, #inequalitySign2 {
		background-color: #8080ff;
		color: white;
		 
		text-align: center;
		text-decoration: none;
		display: inline-block;
		cursor: pointer;
		border: 1.9px solid black;
	}
	#inequalitySign1:hover, #inequalitySign1:active {
		background-color: #b3b3ff;
		 cursor: pointer;
	}
	
	#inequalitySign2:hover, #inequalitySign2:active {
		background-color: #b3b3ff;
		 cursor: pointer;
	}
	li {
		display: inline;
	}
	
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
		border: 3px solid grey;
        height: 75%;
		width: 75%;
		margin-left: 30;
		padding: 12;
		
      }
      
	  
	  
    </style>
  </head>
  
<body>


<section>
	 
	<ul>
	  <li><a  type="button" id = "button"   onclick="">Home</a></li>
	  <li><a  align= "right" id = "button" href="login.php" target="_blank">Login</a></li>
	  <li><a  id = "button" href="all_samples.php" target="_blank">All Samples</a></li>
	<text  id = 'text'  > This should be aligned to right</text>
	
	</ul>
</section>

<section>
    
  <!-- <text id = "text2";"> Accepted Samples Map</text>   a title for the map -->
  Show pending samples<input type="checkbox" name="pend" value="pending" id = 'pendingSamples'>
  include xxxx <input type="checkbox" name="vehicle" value="Bike" id = 'include1'>  <!--  placeholder -->
  <br>
  
  ID Number
  <a  id = "inequalitySign1"   onclick="changeSign('inequalitySign1')"><</a> 
  <input type="text" name="fname" size = 7 placeholder = 0  id = 'numberLevel'>
  <a  id = "button2"   onclick="clearBox('numberLevel')">Clear</a>
  
  xxx level  <!--  placeholder -->
  <a  id = "inequalitySign2"   onclick="changeSign('inequalitySign2')"><</a> <!--  placeholder -->
  <input type="text" name="fname" size = 7 placeholder = 0 id = 'Level1'> <!--  placeholder -->
   <a  id = "button2"   onclick="clearBox('Level1')">Clear</a><!--  placeholder -->
   
   <a  type="button" id = "button"   onclick="initMap()">Create Map</a>
</section>
<section>
  <div id="map"></div>
   
    <script>
		//these variables will be used to keep the view consistent each time a new map is created
		var zoom = 4;
		var center = {lat: 39.8333333, lng: -98.585522}; // center is where the user is looking at in the map now
		
		//Create a map based on the configuration
		//note: a new map will be created each time this method is called, the new map will overwrite the old one
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
		
		  /* used for debugging*********************************
		  //console.log('Show pending samples: '+document.getElementById('pendingSamples').checked);//.'AcceptedSamples');
		  //console.log('include xxxx: '+document.getElementById('include1').checked);
		  //console.log('Number level: '+document.getElementById('numberLevel').value);
		  //console.log('xxx level: '+document.getElementById('Level1').value);
		    used for debugging********************************* */
		  
          // Change this depending on the name of your PHP or XML file
          downloadUrl('create_xml.php', function(data) {
            var xml = data.responseXML;
            var markers = xml.documentElement.getElementsByTagName('marker');
            Array.prototype.forEach.call(markers, function(markerElem) {
              var name = markerElem.getAttribute('name');
              var organization = markerElem.getAttribute('organization');
              var point = new google.maps.LatLng(
                  parseFloat(markerElem.getAttribute('latitude')),
                  parseFloat(markerElem.getAttribute('longitude')));

              var infowincontent = document.createElement('div');
              var strong = document.createElement('strong');
              strong.textContent = name
              infowincontent.appendChild(strong);
              infowincontent.appendChild(document.createElement('br'));

              var text = document.createElement('text');
              text.textContent = organization
              infowincontent.appendChild(text);
			  
			  infowincontent.appendChild(document.createElement('br'));
			  var text2 = document.createElement('text');
			  text2.textContent = "more info";
			  var ul =  document.createElement('a');
			  ul.setAttribute('href', "marker_info.php");
			  ul.setAttribute('target', "_blank");
			  var d = markerElem.getAttribute('id');
			  ul.setAttribute('onclick', "setId('"+d+"')");
			  ul.setAttribute('id_number', markerElem.getAttribute('id'));
			  ul.appendChild(text2);
			  infowincontent.appendChild(ul);
			  
			    // change the color of the marker based on the id 
				var pinColor = 0xFFFF00;
				var myId = markerElem.getAttribute('id');
				
				var colorRange = (myId%4)+1;
				switch (colorRange){
					case 1:
					pinColor = 0x3399FF;
					break;
					case 2:
					pinColor = 0x6673BF;
					break;
					case 3:
					pinColor = 0x992966;
					break;
					case 4:
					pinColor = 0xFF0000;
					break;
					
				}
				 
				 //create the pin marker image 
				 //toString(16) is used because pinColor is in hexdecimal
				var pinImage = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|" + pinColor.toString(16),
				new google.maps.Size(21, 34),
				new google.maps.Point(0,0),
				new google.maps.Point(10, 34));
				var pinShadow = new google.maps.MarkerImage("http://chart.apis.google.com/chart?chst=d_map_pin_shadow",
				new google.maps.Size(40, 37),
				new google.maps.Point(0, 0),
				new google.maps.Point(12, 35));
				var infoWindow = new google.maps.InfoWindow;
				
			  //create marker in the map 
              var marker = new google.maps.Marker({
                map: map,
				icon: pinImage,
                shadow: pinShadow,
                position: point,
				strokeColor: 'green',
              });
			  if (markerElem.getAttribute('verified')== 0)
				{
				 marker = new google.maps.Marker({
                map: map,
				icon: pinImage,
                shadow: pinShadow,
                position: point,
				strokeColor: 'green',
				label: {text: 'P', color: 'white'},
				
              });
			  marker.label.color = 'white';
			  marker.icon.labelOrigin = {x: 11, y: 11};
			  console.log(marker);
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
		//pass the query to create_xml page in a request 
        request.open("GET", url+"?q=" + query, true);
		
		
		
        request.send(null);
      }

      function doNothing() {}
	  
	  function changeSign(inequalitySign){
		  console.log(inequalitySign)
		  if (document.getElementById(inequalitySign).textContent == '<')
			  document.getElementById(inequalitySign).textContent = '>'
		  else
			  document.getElementById(inequalitySign).textContent = '<'
		  
	  }
	  
	  function clearBox(numberBox){
		  document.getElementById(numberBox).value = '';
		  
	  }
	  
	  function createQuery(){
		 var pendingSamples = document.getElementById('pendingSamples').checked;
		 var include1 = document.getElementById('include1').checked ;
		 var numberLevel = document.getElementById('numberLevel').value;
		 var level1 = document.getElementById('Level1').value;
		 
		 var verified = " = 1";
		 var id = '';
		 if (pendingSamples)
			 verified = "and verified > -1";
		 console.log(isNaN(numberLevel));
		  console.log(0>(numberLevel));
		 
		 if (isNaN(numberLevel) || numberLevel < 0 ){
			 numberLevel = '';
			 document.getElementById('numberLevel').value = '';
		 }
			 
		 if (numberLevel!=''){
			 
			 
			 if (document.getElementById('inequalitySign1').textContent == '<')
				 id = '< ' +   document.getElementById('numberLevel').value ;
			 else 
				 id = '> ' +   document.getElementById('numberLevel').value ;
		 }
		 
		 /*  select 
			 m.id 'id', m.user_id 'userid', m.latitude 'latitude' , m.longitude 'longitude',
			 m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation',
			 m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization',
			 u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'

			 from markers m
					join users u on m.user_id = u.id
					join tests t on m.test_id = t.id 
		
			where true 
        */
		 var querySetUp = "select "+ 
			 " m.id 'id', m.user_id 'userid', m.latitude 'latitude' , m.longitude 'longitude',"+
			 " m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation',"+
			 " m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization',"+
			 " u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'"+
				
			 " from markers m "+
					" join users u on m.user_id = u.id"+
					" join tests t on m.test_id = t.id "+
		
			" where true "
		 var query = querySetUp;
		                                          
		 return query;
		  
	  }
	  
	  function setId (id){
		  sessionStorage.setItem('marker', id);
		  
	  }
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEK7qksAR1dzc8HD6EdqIZL8rEogRRv-0&callback=initMap"
    async defer></script>
	
</section>
<ul>
  <li><a  id = "button" href="" target="_blank">This is a link</a></li>
  <li><a  id = "button" href="" target="_blank">This is a link</a></li>
  <li><a  id = "button" href="" target="_blank">This is a link</a></li>
</ul>

</body>
</html>