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
	<div class="left-label">
		<div>
			
				<label  for="pendingSamples">Show pending samples</label>
				<input id="pendingSamples" type="checkbox" name="pend" value="pending">
			
			<br>
			
			
				<label  for="concentrationLevel"> Concentration level </label>
				<a id="inequalitySign2" onclick="changeSign('inequalitySign2')"><</a>
				<input id="concentrationLevel" type="text" name="fname" size="7" placeholder="0">
				<a id="button2" onclick="clearBox('concentrationLevel')">Clear</a>
			
			
				<label  for="precipitationLevel"> Precipitation Level </label>
				<a id="inequalitySign1" onclick="changeSign('inequalitySign1')"><</a>
				<input id="precipitationLevel" type="text" name="fname" size="7" placeholder="0">
				<a id="button2" onclick="clearBox('precipitationLevel')">Clear</a>
				<form action="">
				  <input type="radio" name="degree" value="celsius" id = "celsius" onclick = "changeDegree('c')"> Celsius<br>
				  <input type="radio" name="degree" value="fahrenheit" id = "fahrenheit" onclick = "changeDegree('f')"> Fahrenheit<br>
				</form>
				
			<div class="input-group date" data-provide="datepicker" data-date-format="mm/dd/yyyy">
				<input type="text" class="form-control">
				<div class="input-group-addon">
					<span class="glyphicon glyphicon-th"></span>
				</div>
			</div>
		</div>
		<div>
			<a  type="button" id = "button"   onclick="createTable()">Create Table</a>
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
