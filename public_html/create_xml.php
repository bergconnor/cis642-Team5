<?php
require_once 'config.php';

// Start XML file, create parent node
$doc = new DOMDocument('1.0');
$node = $doc->createElement('markers');
$parnode = $doc->appendChild($node);

// select all the rows in the markers table
$stmt = $conn->stmt_init();
if($stmt->prepare('SELECT name, organization, latitude, longitude
                   FROM markers WHERE 1')) {
  $stmt->execute();
  $stmt->bind_result($name, $organization, $latitude, $longitude);

  header('Content-type: text/xml');

  // iterate through the rows, adding XML nodes for each
  while($stmt->fetch()) {
    $node = $doc->createElement('marker');
    $newnode = $parnode->appendChild($node);

    $newnode->setAttribute('name', $name);
    $newnode->setAttribute('organization', $organization);
    $newnode->setAttribute('latitude', $latitude);
    $newnode->setAttribute('longitude', $longitude);
  }

  $xmlfile = $doc->saveXML();
  echo $xmlfile;
}
?>
