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
	
	<div class = "row">
		<div class ="col-md-1">
		<label >This is the margin </label>
		</div>
		<div class ="col-md-3">
		
			<div class = "row">
				<label  for="pendingSamples">Show pending samples</label>
				<input id="pendingSamples" type="checkbox" name="pend" value="pending">
			</div> 
			<div class = "row">
				<label  for="precipitationLevel"> Precipitation Level </label>
				<a id="inequalitySign1" onclick="changeSign('inequalitySign1')"><</a>
				<input id="precipitationLevel" type="text" name="fname" size="7" placeholder="0">
				<a id="button2" onclick="clearBox('precipitationLevel')">Clear</a>
			</div> 
			<div class = "row">
				<a  type="button" id = "button"   onclick="initMap()">Create Map</a>
			</div>
		
		</div>
		<div class ="col-md-7">
			<div class = "row">	
				<div class ="col-md-2">
					<input type="radio" name="shownTest" value="All" id = "AllTest" onclick = " "> <label>All</label>
				</div>
			</div>
			
			<div class = "row">
				<div class ="col-md-2">
					<input type="radio" name="shownTest" value="Nitrate" id = "nitrate" onclick = " "> <label> Nitrate</label>
				</div>
				
				<div class ="col-md-7">
					<label id = "nitrate_color_box1" ></label>
					<label id = "nitrate_color_box2" ></label>
					<label id = "nitrate_color_box3" ></label>
					<label id = "nitrate_color_box4" ></label>
					<label id = "nitrate_color_box5" ></label>
					<label  for="concentrationLevel"> Concentration level </label>
					<a id="inequalitySign2" onclick="changeSign('inequalitySign2')"><</a>
					<input id="concentrationLevel" type="text" name="fname" size="7" placeholder="0">
					<a id="button2" onclick="clearBox('concentrationLevel')">Clear</a>
				</div>
			</div>
			<div class = "row">
				<div class ="col-md-2">
					<input type="radio" name="shownTest" value="Phosphate" id = "phosphate" onclick = " "> <label>Phosphate</label>
				</div>
				
				<div class ="col-md-7">
					<label id = "phosphate_color_box1"></label>
					<label id = "phosphate_color_box2"></label>
					<label id = "phosphate_color_box3"></label>
					<label id = "phosphate_color_box4"></label>
					<label id = "phosphate_color_box5"></label>
					<label  for="concentrationLevel"> Concentration level </label>
					<a id="inequalitySign2" onclick="changeSign('inequalitySign2')"><</a>
					<input id="concentrationLevel" type="text" name="fname" size="7" placeholder="0">
					<a id="button2" onclick="clearBox('concentrationLevel')">Clear</a>
				</div>
			</div>
			
			
		</div>
	</div>
	
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
