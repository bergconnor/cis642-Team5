<?php
// Start the session
session_start();
?>

<html>
  <head>
    <title>Water Quality</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/home.css" type="text/css" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="utf-8">
  </head>
  <body>
    <div class="page-header">
      <!-- <div class="pull-right">
        <button type="button" class="btn btn-primary">Press me!</button>
      </div> -->
      <h1>Water Quality</h1>
    </div>
    <nav class="navbar navbar-default" role="navigation">
      <div class="container-fluid">
        <ul class="nav navbar-nav">
          <li class="active" id="home-nav">
            <a href="home.php">Home</a>
          </li>
          <li>
            <a href="table.php">Table</a>
          </li>
          <li>
            <a href="account.php">Account</a>
          </li>
        </ul>
      </div>
    </nav>
    <label class="left-label" for="pendingSamples">Show pending samples</label>
    <input id="pendingSamples" type="checkbox" name="pend" value="pending">
    
	<label for="concentrationLevel">Concentration level</label>
    <a id="inequalitySign2" onclick="changeSign('inequalitySign2')"><</a>
    <input id="concentrationLevel" type="text" name="fname" size="7" placeholder="0">
    <a id="button2" onclick="clearBox('concentrationLevel')">Clear</a>
	
    <label class="left-label" for="precipitationLevel">Precipitation Level</label>
    <a id="inequalitySign1" onclick="changeSign('inequalitySign1')"><</a>
    <input id="precipitationLevel" type="text" name="fname" size="7" placeholder="0">
    <a id="button2" onclick="clearBox('precipitationLevel')">Clear</a>
	
    <a  type="button" id = "button"   onclick="initMap()">Create Map</a>

    <div id="map"></div>

    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script async defer src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDEK7qksAR1dzc8HD6EdqIZL8rEogRRv-0&callback=initMap"></script>
    <script src="../js/home.js" type="text/javascript"></script>
    <script src="../js/details.js" type="text/javascript"></script>
  </body>
</html>
