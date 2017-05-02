<?php

// Start the session
session_start();

?>

<!DOCTYPE HTML>
<html>
  <head>
    <title>Water Quality</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/home.css" type="text/css" />
    <meta name="viewport" content="width=device-width,
                        initial-scale=1, shrink-to-fit=no">
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
          <li id="home-nav">
            <a href="home.php">Home</a>
          </li>
          <li class="active">
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
		 
		</div>
		<div class ="col-md-3">
		
			<div class = "row">
				<label  for="pendingSamples">Show pending samples</label>
				<input id="pendingSamples" type="checkbox" name="pend" value="pending">
			</div> 
			
			<div class = "row">
				<label  for="precipitationLevel"> Precipitation Level </label>
				<a id="precipitationInequalitySign" onclick="changeSign('precipitationInequalitySign')"><</a>
				<input id="precipitationLevel" type="text" name="fname" size="7" placeholder="0">
				<a id="button2" onclick="clearBox('precipitationLevel')">Clear</a>
			</div> 
			
			<div class = "row">
				<label  for="testDate1">Test Date </label> <a id="dateButton" onclick="changeDate()">After</a><input id = "testDate1" type="date">
			</div>
			<div class = "row">
				<label  for="testDate2" id= "testDate2Label" style = "visibility:hidden" >and   </label><input id = "testDate2" type="date" style = "visibility:hidden">
			</div>
			<div class = "row">
				<a  type="button" id = "button"   onclick="createTable()()">Create Table</a>
			</div>
			
		</div>
		<div class ="col-md-8">
			<div class = "row">	
				<div class ="col-md-2">
					<input type="radio" name="shownTest" value="all" id = "showAllTests" onclick = "highlightTest('all')" checked> <label>All</label>
				</div>
			</div>
			
			<div class = "row">
				<div class ="col-md-2">
					<input type="radio" name="shownTest" value="nitrate" id = "showNitrateTests" onclick = "highlightTest('nitrate')"> <label> Nitrate</label>
				</div>
				
				<div class ="col-md-10">
					<label  for="concentrationLevel">Nitrate Concentration level </label>
					<a id="nitrateInequalitySign" onclick="changeSign('nitrateInequalitySign')"><</a>
					<input id="nitrateConcentrationLevel" type="text" name="fname" size="7" placeholder="0">
					<a id="button2" onclick="clearBox('nitrateConcentrationLevel')">Clear</a>
					<label>above recommended</label><input type="checkbox" id="useRecommendedConcentrationNitrate" onclick = "useRecommendedConcentration('nitrate')"> 
				</div>
			</div>
			<div class = "row">
				<div class ="col-md-2">
					<input type="radio" name="shownTest" value="phosphate" id = "showPhosphateTests" onclick = "highlightTest('phosphate')"> <label>Phosphate</label>
				</div>
				
				<div class ="col-md-9">
					<label  for="phosphateConcentrationLevel">Phosphate Concentration level </label>
					<a id="phosphateInequalitySign" onclick="changeSign('phosphateInequalitySign')"><</a>
					<input id="phosphateConcentrationLevel" type="text" name="fname" size="7" placeholder="0">
					<a id="button2" onclick="clearBox('phosphateConcentrationLevel')">Clear</a>
					<label>above recommended</label><input type="checkbox" id="useRecommendedConcentrationPhosphate" onclick = "useRecommendedConcentration('phosphate')"> 
				</div>
			</div>
			<div class = "row">
				<form action="">
					  <input type="radio" name="degree" value="celsius" id = "celsius" onclick = "changeDegree('c')"> Celsius<br>
					  <input type="radio" name="degree" value="fahrenheit" id = "fahrenheit" onclick = "changeDegree('f')"> Fahrenheit<br>
				</form>
			</div>
			
			
		</div>
	</div>
	
    <div class="table-margin">
      <table class="samples-table">
        <thead>
          <tr id= columns_headers>
            <th id ="date" onclick = changeOrder(1)>Date<span id = order_arrow class="glyphicon glyphicon-triangle-bottom" aria-hidden="true"></span></th>
            <th id ="name" onclick = changeOrder(2)>Name</th>
            <th id ="orgnization" onclick = changeOrder(3)>Orgnization</th>
            <th id ="email" onclick = changeOrder(4)>Email</th>
            <th id ="type" onclick = changeOrder(5)>Type</th>
            <th id ="latitude" onclick = changeOrder(6)>Latitude</th>
            <th id ="longitude" onclick = changeOrder(7)>Longitude</th>
            <th id ="temperature" onclick = changeOrder(8)><text id = "temperature_text">Temperature °F</text></th>
            <th id ="precipitation" onclick = changeOrder(9)>Precipitation</th>
			<th id ="concentration" onclick = changeOrder(10)>Concentration</th>
            <th id ="comment" onclick = changeOrder(11)>Comment</th>
          </tr>
        </thead>
        <tbody id="table-body">
        </tbody>
      </table>
    </div>
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="../js/table.js" type="text/javascript"></script>
    <script src="../js/details.js" type="text/javascript"></script>
  </body>
</html>
