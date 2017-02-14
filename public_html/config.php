<?php

$hostname = 'mysql.cis.ksu.edu';
$username = 'cberg1';
$password = '3c340d';
$database = 'cberg1';



// set up database connection
$conn = mysqli_connect($hostname, $username, $password, $database)
        or die(mysqli_error($conn));

if(mysqli_connect_errno()) {
  // handle connection ErrorException
  printf("Connect failed: %s\n", mysqli_connect_error());
  exit();
}
?>
