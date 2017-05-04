<?php
/**
 * Script to process user's login information
 * from the android app and allow access based on
 * information stored in the database.
 */
require_once './../lib/config.php';  // database connection
require_once './../lib/modules.php'; // modules for database queries

/**
 * Handle event when the login button
 * is pressed.
 */
if(!empty($_POST)) {
  // initialize variables
  $json = array();
  $json['success'] = false;
  $json['message'] = '';

  if(empty($_POST['email']) || empty($_POST['pass'])) {
    // empty field
    $json['success'] = false;
    $json['message'] = 'Please provide your email address and password.';
  }
  elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    // invalid email format
    $json['message'] = 'Invalid email format.';
  }
  else {
    // verify account information
    $email  = $_POST['email'];
    $pass   = $_POST['pass'];
    $result = login($email, $pass);

    switch($result) {
      case 0:
        $json['success'] = true;
        $json['message'] = 'You successfully logged in.';
        break;
      case 1:
        $json['success'] = false;
        $json['message'] = 'You need to activate your account.';
        break;
      case 2:
        $json['success'] = false;
        $json['message'] = 'Invalid account information.';
        break;
      case 3:
       $json['success'] = false;
        $json['message'] = 'Error checking account information.
                            Please try again.';
        break;
      default:
        $json['success'] = false;
        $json['message'] = 'An unknown error has occured.';
        break;
    }
  }
  // send back data as a json string
  echo json_encode($json);
}

?>
