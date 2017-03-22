<?php
// Start the session
session_start();
?>

<html>
  <head>
    <title>Water Quality</title>
    <link href="../css/home.css" type="text/css" rel="stylesheet" />
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
  </head>
  <body class="home">
    <h1>Water Quality</h1>
    <ul>
      <li class="home"><a href="home.php">Home</a></li>
      <li class="samples"><a href="table.php">Table</a></li>
      <li class="account"><a href="account.php">Account</a></li>
    </ul>
    <label class="label" for="pendingSamples">Show pending samples</label>
    <input id="pendingSamples" type="checkbox" name="pend" value="pending">
    <label for="include1">include xxxx</label>
    <input id="include1" type="checkbox" name="vehicle" value="Bike"><br>

    <label class="label" for="precipitationLevel">Precipitation Level</label>
    <a id="inequalitySign1" onclick="changeSign('inequalitySign1')"><</a>
    <input id="precipitationLevel" type="text" name="fname" size="7" placeholder="0">
    <a id="button2" onclick="clearBox('precipitationLevel')">Clear</a>

    <label for="inequalitySign2">xxx level</label>
    <a id="inequalitySign2" onclick="changeSign('inequalitySign2')"><</a>
    <input id="Level1" type="text" name="fname" size="7" placeholder="0">
    <a id="button2" onclick="clearBox('Level1')">Clear</a>
    <a  type="button" id = "button"   onclick="initMap()">Create Map</a>

    <div id="map"></div>

    <script src="../js/home.js" type="text/javascript"></script>
    <script async defer
      src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEK7qksAR1dzc8HD6EdqIZL8rEogRRv-0&callback=initMap">
    </script>

  </body>
</html>
