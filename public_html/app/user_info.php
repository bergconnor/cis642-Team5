<?php
/**
 * A page that allows a user to sign up
 * for a new account on the android app.
 * A new user must provide their first
 * name, last name, email address, and a
 * password.
 */
require_once '../config.php';   // database connection

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
    $json = getInfo($conn);
  }
  // send back data as a json string
  echo json_encode($json);
}

/**
 * Verify user's login information with the database.
 * @param object $conn A MySQL connection object.
 * @return string A success or error message.
 */
function getInfo($conn) {
  $json = array();
  $email = mysqli_real_escape_string($conn, $_POST['email']);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT id, first, last, organization FROM users WHERE email=?')) {
    $stmt->bind_param('s', $email);
    $stmt->execute();
    $stmt->bind_result($id, $first, $last, $organization);

    if($stmt->fetch() > 0) {
      // user info found
      $json['success']      = 'true';
      $json['message']      = 'Succes!';
      $json['user_id']      = $id;
      $json['name']         = $first . ' ' . $last;
      $json['organization'] = $organization;
      $stmt->close();
      mysqli_close($conn);
      return $json;
    }
    else {
      // failed to retrieve user info
      $json['success'] = 'false';
      $json['message'] = 'Error retrieving user information.';
      $stmt->close();
      mysqli_close($conn);
      return $json;
    }
  } else {
    $json['success'] = 'false';
    $json['message'] = 'Error retrieving user information.';
    $stmt->close();
    mysqli_close($conn);
    return $json;
  }
}
?>

<!DOCTYPE html>
<html>
  <head>
    <title>Test JSON</title>
    <link href="css/style.css" type="text/css" rel="stylesheet" />
  </head>
  <body>
    <h1>Test JSON</h1>
    <form action="" method="post">
      <input type="email" name="email" placeholder="Email">
      <input type="submit" name="submit" value="Submit">
    </form>
  </body>
</html>
