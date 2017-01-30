<?php

include("db_info.php");

// Check whether username or password is set from android  
if(isset($_POST['email']) && isset($_POST['username']) && isset($_POST['password']))
{
  // Innitialize Variable
  $result='';
  $email = $_POST['email'];
  $username = $_POST['username'];
  $password = $_POST['password'];

  // Query database to try and insert new user info
  $sql = 'INSERT INTO accounts (email, username, password) VALUES (:email, :username, :password)';
  $stmt = $conn->prepare($sql);
  $stmt->bindParam(':email', $email, PDO::PARAM_STR);
  $stmt->bindParam(':username', $username, PDO::PARAM_STR);
  $stmt->bindParam(':password', $password, PDO::PARAM_STR);
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