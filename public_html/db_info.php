<?php
$hostname="mysql.cis.ksu.edu";
$username="cberg1";
$password="3c340d";
$database="cberg1";

try {
        $conn = new PDO("mysql:host=$hostname;dbname=$database", $username, $password);
        $conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    }
catch(PDOException $e)
    {
        die("OOPs something went wrong");
    }

?>