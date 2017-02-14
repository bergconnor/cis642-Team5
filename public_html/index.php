<?php
/**
 * Login page to process user's login information
 * and allow access to website based on information
 * stored in MySQL database.
 */
session_start();
require_once 'config.php'; /* database connection */

$_SESSION['email_sent'] = false; /* password reset flag */

/**
 * Direct user to the sign up page
 * if the sign up button is pressed.
 */
if(isset($_POST["sign_up"])) {
  /* sign up button pressed */
  header('location: sign_up.php');
  exit();
}

/**
 * Handle event when the login button
 * is pressed.
 */
if(isset($_POST['login'])) {
  if(empty($_POST['email']) || empty($_POST['pass'])) {
    /* empty email or password field */
    $msg = 'Please provide your email address and password.';
  }
  elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    /* invalid email format */
    $msg = 'Invalid email.';
  }
  else {
    /* check database for information */
    $msg = login($conn);
    header('location: map.php');
    exit();
  }

  if(isset($msg)) {
    /* show error message if set */
    echo '<div class="statusmsg">'.$msg.'</div>';
  }
}

/**
 * Verify user's login information with the database.
 * @param object $conn A MySQL connection object.
 * @return string A success or error message.
 */
function login($conn) {
  $email = mysqli_real_escape_string($conn, $_POST['email']);
  $pass = mysqli_real_escape_string($conn, $_POST['pass']);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT email, password, active FROM users
                     WHERE email=? AND password=?')) {

    $stmt->bind_param('ss', $email, $pass);
    $stmt->execute();

    $stmt->bind_result($email, $password, $active);

    if($stmt->fetch() > 0) {
      if($active == 0) {
        /* account not activated */
        return 'Please check your email to activate your account.';
      } else {
          /* valid account */
          return 'Success.';
          header('location: map.php');
          exit();
        }
    } else {
        /* invalid account */
        return 'Invalid email address or password.';
    }
  } else {
    // TO DO: handle error if $stmt->prepare() fails
    return 'Error';
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
        <h1>Login</h1>
        <form action="" method="post">
          <p><input type="email" name="email" placeholder="Email"></p>
          <p><input type="password" name="pass" placeholder="Password"></p>
          <p class="submit">
            <input type="submit" name="login" value="Login" align="left">
            <input type="submit" name="sign_up" value="Sign Up" align="left">
          </p>
        </form>
      </div>

      <div class="login-help">
        <p>Forgot your password? <a class="reset" href="reset.php">Click here to reset it</a>.</p>
      </div>
    </div>
  </body>
</html>
