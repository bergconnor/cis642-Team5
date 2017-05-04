<?php
/**
 * A page that allows a user to sign up
 * for a new account on the android app.
 * A new user must provide their first
 * name, last name, email address, and a
 * password.
 */
require_once './../lib/config.php';   // database connection

/**
 * Handle event when the app calls
 * this script
 */
if(!empty($_POST)) {
    // initialize variables
  $json = array();
  $json['success'] = false;
  $json['message'] = '';

  if(empty($_POST['email'])) {
    // email address was not provided
    $json['success'] = false;
    $json['message'] = 'Error retrieving user information.';
  }
  elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    // invalid email format
    $json['success'] = false;
    $json['message'] = 'Error retrieving user information.';
  }
  else {
    // attempt to get user info from database
    $json = getInfo();
  }
  // send back data as a json string
  echo json_encode($json);
}

/**
 * Verify user's login information with the database.
 * @param object $conn A MySQL connection object.
 * @return string A success or error message.
 */
function getInfo() {
  global $pdo;

  $json = array();
  $stmt = $pdo->prepare('SELECT id, first, last, organization FROM users WHERE email=?');
  $stmt->execute([$_POST['email']]);

  if($stmt->rowCount() > 0) {
    // user info found
    $user = $stmt->fetch();

    $json['success']      = 'true';
    $json['message']      = 'Succes!';
    $json['user_id']      = $user['id'];
    $json['name']         = $user['first'] . ' ' . $user['last'];
    $json['organization'] = $user['organization'];
    return $json;
  } else {
    // failed to retrieve user info
    $json['success'] = 'false';
    $json['message'] = 'Error retrieving user information.';
    return $json;
  }
}
?>

<!DOCTYPE html>
<html>
  <head>
    <title>Test JSON</title>
    <link href="../css/style.css" type="text/css" rel="stylesheet" />
  </head>
  <body>
    <h1>Test JSON</h1>
    <form action="" method="post">
      <input type="email" name="email" placeholder="Email">
      <input type="submit" name="submit" value="Submit">
    </form>
  </body>
</html>
