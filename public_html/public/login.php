<?php
/**
 * Login page to process user's login information
 * from the web server and allow access based on
 * information stored in the database.
 */

session_start();
require_once('./../lib/modules.php'); // query modules

$_SESSION['email_sent'] = false;  // password reset flag
$msg = '';

if(isset($_SESSION['message'])) {
  // show error message if set
  echo '<div class="statusmsg">'.$_SESSION['message'].'</div>';
}

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
  ChromePhp::log("BLAH");
  $email = $_POST['email'];
  $pass = $_POST['pass'];
  if(!isset($email) || !isset($pass)) {
    // empty field
    $_SESSION['message'] = 'Please provide your email address and password.';
  } elseif(!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    // invalid email format
    $_SESSION['message'] = 'Invalid email format.';
  } else {
    // verify account information
    $result = login($email, $pass);

    switch($result) {
      case 0:
        $_SESSION['message'] = 'You successfully logged in.';
        $_SESSION['user'] = $email;
        $_SESSION['last_activity'] = time();
        header('location: ./../index.php');
        exit();
      case 1:
        $_SESSION['message'] = 'You need to activate your account.';
        break;
      case 2:
        $_SESSION['message'] = 'Invalid account information.';
        break;
      default:
        $_SESSION['message'] = 'An unknown error has occured.';
        break;
    }
  }

  // Redirect to this page.
  header("Location: " . $_SERVER['REQUEST_URI'], true, 303);
  exit();
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
