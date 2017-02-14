<?php
/**
 * A page that allows a user to sign up
 * for a new account. A new user must
 * provide their first name, last name,
 * email address, and a password.
 */
require_once 'config.php';

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
  if(empty($_POST['first']) || empty($_POST['last']) || empty($_POST['email']) ||
     empty($_POST['pass1']) || empty($_POST['pass2'])) {
     /* one of the fields is empty field */
    $msg = 'Please fill out all fields.';
  } elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
      /* invalid email format */
      $msg = 'Please enter a valid email.';
  } elseif(strcmp($_POST['pass1'], $_POST['pass2']) != 0) {
      /* passwords do not match */
      $msg = 'Passwords do not match.';
  } else {
      /* create new account */
      $first = mysqli_real_escape_string($conn, $_POST['first']);
      $last  = mysqli_real_escape_string($conn, $_POST['last']);
      $email = mysqli_real_escape_string($conn, $_POST['email']);
      $pass  = mysqli_real_escape_string($conn, $_POST['pass1']);

      if(check_email($conn, $email)) {
        $msg = sign_up($conn, $first, $last, $email, $pass);
        header('location: index.php');
        exit();
      } else {
        $msg = 'Email address already in use.';
      }
    }

    if(isset($msg)) {
      /* show error message if set */
      echo '<div class="statusmsg">'.$msg.'</div>';
    }
}

/**
 * Insert the user's information into the database
 *   to create a new account.
 * @param object $conn  stringA MySQL connection object.
 * @param string $email The user's email.
 * @return bool  Indicates a valid or invalid email.
 */
function check_email($conn, $email) {
  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT first, last FROM users WHERE email=?')) {

    $stmt->bind_param('s', $email);
    $stmt->execute();
    $stmt->bind_result($first, $last);

    if ($stmt->fetch() > 0) {
      return false;
    } else {
      return true;
    }
  }
}

/**
 * Insert the user's information into the database
 *   to create a new account.
 * @param object $conn  A MySQL connection object.
 * @param string $first The user's first name.
 * @param string $last  The user's last name.
 * @param string $email The user's email.
 * @param string $pass  The user's password.
 * @return string A success or error message.
 */
function sign_up($conn, $first, $last, $email, $pass) {
  $stmt = $conn->stmt_init();

  if($stmt->prepare('INSERT INTO users (first, last, email, password, hash)
                     VALUES (?, ?, ?, ?, ?)')) {
    $hash = md5(rand(0, 1000)); /* account verification hash */
    $stmt->bind_param('sssss', $first, $last, $email, $pass, $hash);

    if($stmt->execute()) {
      send_email($email, $hash);
      $stmt->close();
      return 'Success.';
    } else {
      // TO DO: handle insertion error
      $stmt->close();
      return 'Error.';
    }
  }
}

/**
 * Send an email to the user to verify their account.
 * @param string $email The user's email.
 * @param string $hash A hash used to verify the user's account.
 * @return void
 */
function send_email($email, $hash) {
  $to      = $email;
  $subject = 'Signup | Verification';
  $message = '

  Thanks for signing up!
  Your account has been created, you can login after you have
  activated your account by pressing the url below.

  Please click this link to activate your account:
  http://people.cs.ksu.edu/~cberg1/verify.php?email='.$email.'&hash='.$hash.'

  ';

  $headers = 'From:noreply@people.cs.ksu.edu' . "\r\n";
  mail($to, $subject, $message, $headers);
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
          <p><input type="email" name="email" placeholder="Email"></p>
          <p><input type="password" name="pass1" placeholder="Password"></p>
          <p><input type="password" name="pass2" placeholder="Verify Password"></p>
          <p class="submit">
            <input type="submit" name="return" value="Return" align="left">
            <input type="submit" name="sign_up" value="Sign Up" align="left">
          </p>
        </form>
      </div>
    </div>
  </body>
</html>
