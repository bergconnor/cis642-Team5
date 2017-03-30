<?php
require_once("config.php");
include("../dbg/ChromePhp.php");

// Start XML file, create parent node
header("Content-type: text/xml");
$doc = new DOMDocument("1.0");
$node = $doc->createElement("markers");
$parnode = $doc->appendChild($node);

try {
  // load the query from the request
  ChromePhp::log($_REQUEST['q']);
  $stmt = $pdo->prepare($_REQUEST['q']);
  $stmt->execute();
  $markers = $stmt->fetchAll();
} catch(PDOException $ex) {
  die("Error: " . $ex->getMessage());
}

// Iterate through the rows, adding XML nodes for each
foreach($markers as $row) {
  // Add to XML document node
  $node = $doc->createElement("marker");
  $newnode = $parnode->appendChild($node);

  $newnode->setAttribute('id', $row['id']);
  $newnode->setAttribute('date', $row['date']);
  $newnode->setAttribute('name', $row['first'] . ' ' . $row['last']);
  $newnode->setAttribute('organization', $row['organization']);
  $newnode->setAttribute('latitude', $row['latitude']);
  $newnode->setAttribute('longitude', $row['longitude']);
  $newnode->setAttribute('temperature', $row['temperature']);
  $newnode->setAttribute('precipitation', $row['precipitation']);
  $newnode->setAttribute('concentration', $row['concentration']);
  $newnode->setAttribute('comment', $row['comment']);
  $newnode->setAttribute('email', $row['email']);
  $newnode->setAttribute('type', $row['type']);
  $newnode->setAttribute('verified', $row['verified']);
}

$xmlfile = $doc->saveXML();
echo $xmlfile;
?>
