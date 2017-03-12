<?php
require("db_info.php");

// Start XML file, create parent node
$doc = new DOMDocument("1.0");
$node = $doc->createElement("markers");
$parnode = $doc->appendChild($node);

// Opens a connection to a MySQL server
$connection=mysql_connect ($hostname, $username, $password);
if (!$connection) {
  die('Not connected : ' . mysql_error());
}

// Set the active MySQL database
$db_selected = mysql_select_db($database, $connection);
if (!$db_selected) {
  die ('Can\'t use db : ' . mysql_error());
}

//Load the query from the request
//$query = "SELECT * FROM markers WHERE 1";<------------>
$query = $_REQUEST["q"];
$result = mysql_query($query);
if (!$result) {
  die('Invalid query: ' . mysql_error());
}

header("Content-type: text/xml");
 /*  select 
			 m.id 'id', m.user_id 'userid', m.latitude 'latitude' , m.longitude 'longitude',
			 m.city 'city', m.state 'state', m.temperature 'temperature', m.precipitation 'precipitation',
			 m.comment 'comment', m.verified 'verified', u.first 'first', u.last 'last', u.organization 'organization',
			 u.email 'email', u.active 'activeUser', u.admin 'admin', t.type 'type'

			 from markers m
					join users u on m.user_id = u.id
					join tests t on m.test_id = t.id 
		
			where true 
        */
// Iterate through the rows, adding XML nodes for each
while ($row = @mysql_fetch_assoc($result)){
  // Add to XML document node
  $node = $doc->createElement("marker");
  $newnode = $parnode->appendChild($node);
  /*
  $newnode->setAttribute("id", $row['id']);
  $newnode->setAttribute("name", $row['last']);
  $newnode->setAttribute("organization", $row['organization']);
  $newnode->setAttribute("latitude", $row['latitude']);
  $newnode->setAttribute("longitude", $row['longitude']);
  $newnode->setAttribute("verified", $row['verified']);
  */
  
  $name = $row['first']. " " .$row['last'];
 
  $newnode->setAttribute("id", $row['id']);
  $newnode->setAttribute("name",$name);
  $newnode->setAttribute("organization", $row['organization']);
  $newnode->setAttribute("latitude", $row['latitude']);
  $newnode->setAttribute("longitude", $row['longitude']);
  $newnode->setAttribute("temperature", $row['temperature']);
  $newnode->setAttribute("precipitation", $row['precipitation']);
  $newnode->setAttribute("comment", $row['comment']);
  $newnode->setAttribute("email", $row['email']);
  $newnode->setAttribute("type", $row['type']);
  $newnode->setAttribute("verified", $row['verified']);
}

$xmlfile = $doc->saveXML();
echo $xmlfile;
?>