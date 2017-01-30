<?php

include("db_info.php");

// Verify that required info is present
if(isset($_POST['name']) && isset($_POST['organization']) && isset($_POST['latitude']) && isset($_POST['longitude']))
{
  // Innitialize Variable
  $result='';
  $name = $_POST['name'];
  $organization = $_POST['organization'];
  $latitude = $_POST['latitude'];
  $longitude = $_POST['longitude'];

  // Query database to try and insert new user info
  $sql = 'INSERT INTO markers (name, organization, latitude, longitude) VALUES (:name, :organization, :latitude, :longitude)';
  $stmt = $conn->prepare($sql);
  $stmt->bindParam(':name', $name, PDO::PARAM_STR);
  $stmt->bindParam(':organization', $organization, PDO::PARAM_STR);
  $stmt->bindParam(':latitude', $latitude, PDO::PARAM_STR);
  $stmt->bindParam(':longitude', $longitude, PDO::PARAM_STR);
  $success = $stmt->execute();
  if($success)
  {
    $result="true";  
  }  
  else
  {
    $result="false";
  }

  // send result back to android
  echo $result;
}
  
?>