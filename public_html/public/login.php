<?php
/**
 * Login page to process user's login information
 * from the web server and allow access based on
 * information stored in the database.
 */

session_start();
require_once('./../lib/modules.php'); // query modules

$_SESSION['email_sent'] = false;  // password reset flag

/**
 * Direct user to the sign up page
 * if the sign up button is pressed.
 */
if(isset($_POST["sign_up"])) {
  /* sign up button pressed */
  header('location: ./sign_up.php');
  exit();
}

/**
 * Handle event when the login button
 * is pressed.
 */
if(isset($_POST['login'])) {
  if(empty($_POST['email']) || empty($_POST['pass'])) {
    // empty field
    $msg = 'Please provide your email address and password.';
  } elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    // invalid email format
    $msg = 'Invalid email format.';
  } else {
    // verify account information
    $email  = $_POST['email'];
    $pass   = $_POST['pass'];
    $result = login( $email, $pass);

    switch($result) {
      case 0:
        $msg = 'You successfully logged in.';
        $_SESSION['user'] = $email;
        $_SESSION['last_activity'] = time();
        header('location: ./../index.php');
        exit();
      case 1:
        $msg = 'You need to activate your account.';
        break;
      case 2:
        $msg = 'Invalid account information.';
        break;
      default:
        $msg = 'An unknown error has occured.';
        break;
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
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link href="./../css/login.css" type="text/css" rel="stylesheet" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="utf-8">
  </head>
  <body>
    <div class="container">
      <div class="form">
        <h1>Login</h1>
        <form action="" method="post">
          <p><input type="email" name="email" placeholder="Email"></p>
          <p><input type="password" name="pass" placeholder="Password"></p>
          <p class="submit">
            <input class="btn btn-default" type="submit" name="login" value="Login" align="left">
            <input class="btn btn-default" type="submit" name="sign_up" value="Sign Up" align="right">
          </p>
        </form>
      </div>

      <div class="login-help">
        <p>Forgot your password? <a class="reset" href="reset.php">Click here to reset it</a>.</p>
      </div>
    </div>
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  </body>
</html>
