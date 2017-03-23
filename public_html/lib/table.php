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
      <div class="pull-right">
        <button type="button" class="btn btn-primary">Press me!</button>
      </div>
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
    <div class="table-margin">
      <table class="samples-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Name</th>
            <th>Orgnization</th>
            <th>Email</th>
            <th>Type</th>
            <th>Latitude</th>
            <th>Longitude</th>
            <th>Temperature</th>
            <th>Precipitation</th>
            <th>Comment</th>
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
