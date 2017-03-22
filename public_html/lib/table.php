<?php

// Start the session
session_start();

?>

<!DOCTYPE HTML>
<html>
  <head>
    <title>Water Quality</title>
    <link href="../css/home.css" type="text/css" rel="stylesheet" />
  </head>
  <body class="samples">
    <h1>Samples</h1>
    <ul>
      <li class="home"><a href="home.php">Home</a></li>
      <li class="samples"><a href="table.php">Table</a></li>
      <li class="account"><a href="account.php">Account</a></li>
    </ul>
    <div class="table-margin">
      <table id="table">
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
      </table>
    </div>
    <script src="../js/table.js" type="text/javascript">
    </script>
  </body>
</html>
