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
      /* Always set the map height explicitly to define the size of the div
       * element that contains the map. */
      #map {
        height: 75%;
		width: 75%;
		margin-left: 30;
		padding: 12;
		
      }
      /* Optional: Makes the sample page fill the window. */
      html, body {
        height: 100%;
		width: 100%;
        margin: 0;
        padding: 10;
      }
	  
	  
    </style>
  </head>
  
<body>

<?php 
/*  <?php session_unset(); session_destroy(); ?> */
  
  
?>

<section>
  
  Welcome  
   
</section>
<section>
 
  <button type="button"  onclick="location.href='account_management.php';">Account <br> Management</button>
  <button type="button"  onclick="location.href='index.php';">Logout</button>
</section>

<section>
    
  <text style="margin-left: 200;"> Accepted Samples Map</text>
</section>
<section>
  <div id="map"></div>
   
    <script>
           var customLabel = {
        restaurant: {
          label: 'R'
        },
        bar: {
          label: 'B'
        }
      };

        function initMap() {
        var map = new google.maps.Map(document.getElementById('map'), {
          center: new google.maps.LatLng(39.8333333, -98.585522),
          zoom: 5
        });
        var infoWindow = new google.maps.InfoWindow;

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
			  ul.appendChild(text2);
			  infowincontent.appendChild(ul);
			  
              var marker = new google.maps.Marker({
                map: map,
                position: point,
              });
              marker.addListener('click', function() {
				sessionStorage.setItem('marker', markerElem.getAttribute('id'))
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

        request.open('GET', url, true);
        request.send(null);
      }

      function doNothing() {}
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEK7qksAR1dzc8HD6EdqIZL8rEogRRv-0&callback=initMap"
    async defer></script>
	
</section>

</body>
</html>