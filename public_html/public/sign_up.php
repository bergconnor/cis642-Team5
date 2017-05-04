<?php
/**
 * A page that allows a user to sign up
 * for a new account on the web server.
 * A new user must provide their first
 * name, last name, email address, and a
 * password.
 */
require_once './../lib/config.php';
require_once './../lib/modules.php';

/**
 * Handle event when sign up button
 * is pressed.
 */
if(isset($_POST['sign_up'])) {
  if(empty($_POST['first']) || empty($_POST['last']) || empty($_POST['org']) ||
     empty($_POST['email']) || empty($_POST['pass1']) || empty($_POST['pass2'])) {
     // empty field
    $msg = 'Please fill out all fields.';
  } elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
      // invalid email format
      $msg = 'Please enter a valid email.';
  } elseif(strcmp($_POST['pass1'], $_POST['pass2']) != 0) {
      // passwords do not match
      $msg = 'Passwords do not match.';
  } else {
    // attempt to create a new account
    $first = $_POST['first'];
    $last  = $_POST['last'];
    $org   = $_POST['org'];
    $email = $_POST['email'];
    $pass  = $_POST['pass1'];

    if(check_email($email)) {
      // email is not in use
      $result = sign_up($first, $last, $org, $email, $pass);
      switch($result) {
        case 0:
          $msg = 'You successfully signed up.';
          header('location: ../index.php');
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
    <link href="../css/login.css" type="text/css" rel="stylesheet" />
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link href="./../css/login.css" type="text/css" rel="stylesheet" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="utf-8">
  </head>
  <body>
    <div class="container">
      <div class="form">
        <h1>Sign Up</h1>
        <form action="" method="post">
          <p><input type="text" name="first" placeholder="First Name"></p>
          <p><input type="text" name="last" placeholder="Last Name"></p>
          <p><input type="text" name="org" placeholder="Organization"></p>
          <p><input type="email" name="email" placeholder="Email"></p>
          <p><input type="password" name="pass1" placeholder="Password"></p>
          <p><input type="password" name="pass2" placeholder="Verify Password"></p>
          <p class="submit">
            <input class="btn btn-default" type="submit" name="sign_up" value="Sign Up" align="right">
          </p>
        </form>
      </div>
    </div>
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  </body>
</html>
