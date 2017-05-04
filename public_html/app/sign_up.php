<?php
/**
 * Script to process the user's sign up
 * information from the android app.
 * A new user must provide their first
 * name, last name, email address, and a
 * password.
 */
require_once './../lib/config.php';   // database connection
require_once './../lib/modules.php';  // modules for database queries

/**
 * Handle event when sign up button
 * is pressed.
 */
if(!empty($_POST)) {
  // initialize variables
  $json = array();
  $json['success'] = false;
  $json['message'] = '';

  if(empty($_POST['first']) || empty($_POST['last']) || empty($_POST['organization']) ||
     empty($_POST['email']) || empty($_POST['password'])) {
    // empty field
    $json['success'] = false;
    $json['message'] = 'Please fill out all fields.';
  } elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    // invalid email format
    $json['success'] = false;
    $json['message'] = 'Invalid email format.';
  } else {
    // attempt to create a new account
    $first  = $_POST['first'];
    $last   = $_POST['last'];
    $org    = $_POST['organization'];
    $email  = $_POST['email'];
    $pass   = $_POST['password'];

    if(check_email($email)) {
      // email is not in use
      $result = sign_up($first, $last, $org, $email, $pass);
      switch($result) {
        case 0:
          $json['success'] = true;
          $json['message'] = 'You successfully signed up.';
          break;
        case 1:
          $json['success'] = false;
          $json['message'] = 'Error creating account.
                              Please try again.';
          break;
        default:
          $json['success'] = false;
          $json['message'] = 'An unknown error has occured.';
          break;
      }
    } else {
      // email is in use
      $json['success'] = false;
      $json['message'] = 'Email address already in use.';
    }
  }
  // send back data as a json string
  echo json_encode($json);
}

?>
