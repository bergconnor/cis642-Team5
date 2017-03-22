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
  <body class="marker">
    <h1>Water Quality</h1>
    <ul>
      <li class="home"><a href="home.php">Home</a></li>
      <li class="samples"><a href="table.php">Table</a></li>
      <li class="account"><a href="account.php">Account</a></li>
      <li class="marker"><a href="">Marker</a></li>
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
        <tr>
          <td id="date">Row 1, Column 1</td>
          <td id="name">Row 1, Column 2</td>
          <td id="orgnization">Row 1, Column 3</td>
          <td id="email">Row 1, Column 4</td>
          <td id="type">Row 1, Column 5</td>
          <td id="lat">Row 1, Column 6</td>
          <td id="log">Row 1, Column 7</td>
          <td id="temperature">Row 1, Column 8</td>
          <td id="precipitation">Row 1, Column 9</td>
          <td id="comment">Row 1, Column 10</td>
        </tr>
      </table>
    </div>
    <script src="../js/marker.js"></script>
  </body>
</html>
