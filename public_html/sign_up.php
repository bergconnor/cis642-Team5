<?php
/**
 * A page that allows a user to sign up
 * for a new account on the web server.
 * A new user must provide their first
 * name, last name, email address, and a
 * password.
 */
require_once 'config.php';
require_once 'modules.php';

/**
 * Direct user to the login page if
 * the user presses the return button.
 */
if(isset($_POST['return'])) {
  header('location: index.php');
  exit();
}

/**
 * Handle event when sign up button
 * is pressed.
 */
if(isset($_POST['sign_up'])) {
  if(empty($_POST['first']) || empty($_POST['last']) || empty($_POST['organization']) ||
     empty($_POST['email']) || empty($_POST['password1']) || empty($_POST['password2'])) {
     // empty field
    $msg = 'Please fill out all fields.';
  } elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
      // invalid email format
      $msg = 'Please enter a valid email.';
  } elseif(strcmp($_POST['password1'], $_POST['password2']) != 0) {
      // passwords do not match
      $msg = 'Passwords do not match.';
  } else {
    // attempt to create a new account
    $first = $_POST['first'];
    $last  = $_POST['last'];
    $org   = $_POST['organization'];
    $email = $_POST['email'];
    $password  = $_POST['password1'];

    if(check_email($conn, $email)) {
      // email is not in use
      $result = sign_up($conn, $first, $last, $org, $email, $pass);
      switch($result) {
        case 0:
          $msg = 'You successfully signed up.';
          header('location: index.php');
          exit();
        case 1:
          $msg = 'Error creating account.
                  Please try again.';
          break;
        default:
          $msg = 'An unknown error has occured.';
          break;
      }
    } else {
      // email is in use
      $msg = 'Email address already in use.';
    }
  }

  if(isset($msg)) {
    // show error message if set
    echo '<div class="statusmsg">'.$msg.'</div>';
  }
}

?>

<!DOCTYPE html>
<html>
  <head>
    <title>Water Quality</title>
    <link href="css/style.css" type="text/css" rel="stylesheet" />
  </head>
  <body>
    <div class="container">
      <div class="form">
        <h1>Sign Up</h1>
        <form action="" method="post">
          <p><input type="text" name="first" placeholder="First Name"></p>
          <p><input type="text" name="last" placeholder="Last Name"></p>
          <p><input type="text" name="organization" placeholder="Organization"></p>
          <p><input type="email" name="email" placeholder="Email"></p>
          <p><input type="password" name="password1" placeholder="Password"></p>
          <p><input type="password" name="password2" placeholder="Verify Password"></p>
          <p class="submit">
            <input type="submit" name="return" value="Return" align="left">
            <input type="submit" name="sign_up" value="Sign Up" align="left">
          </p>
        </form>
      </div>
    </div>
  </body>
</html>
