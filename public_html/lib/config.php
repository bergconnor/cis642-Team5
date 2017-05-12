<?php

$json_str = file_get_contents('./../config.json');
$json = json_decode($json_str, true);

$host     = $json['host'];
$db       = $json['db'];
$user     = $json['user'];
$pass     = $json['pass'];
$charset  = 'utf8';

try {
  $pdo = new PDO("mysql:host=$host;dbname=$db;charset=$charset", $user, $pass);
  $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch(PDOException $ex) {
  die("Error: " . $ex->getMessage());
}

?>
