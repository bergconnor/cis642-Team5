<?php

$host     = 'mysql.cis.ksu.edu';
$db       = 'cberg1';
$user     = 'cberg1';
$pass     = '3c340d';
$charset  = 'utf8';

try {
  $pdo = new PDO("mysql:host=$host;dbname=$db;charset=$charset", $user, $pass);
  $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $ex) {
  die("Error: " . $ex->getMessage());
}

?>
