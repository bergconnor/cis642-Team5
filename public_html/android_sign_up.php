<?php
require_once 'config.php'; /* database connection */

if(!empty($_POST)) {
  $result = '';

  if(empty($_POST['first']) || empty($_POST['last']) ||
     empty($_POST['email']) || empty($_POST['pass'])) {
    /* empty field */
    $result = 'One of the fields is empty.';
  }
  elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    /* invalid email format */
    $result = 'Invalid email.';
  }
  else {
    /* check database for information */
    /* create new account */
    $first = mysqli_real_escape_string($conn, $_POST['first']);
    $last  = mysqli_real_escape_string($conn, $_POST['last']);
    $email = mysqli_real_escape_string($conn, $_POST['email']);
    $pass  = mysqli_real_escape_string($conn, $_POST['pass']);

    if(check_email($conn, $email)) {
      $result = sign_up($conn, $first, $last, $email, $pass);
      switch($result) {
        case 0:
          $result = 'true';
          break;
        case 1:
          $result = 'SQL error.';
          break;
        default:
          $result = 'An unknown error has occured.';
          break;
      }
    } else {
      $result = 'Email address already in use.';
    }
  }
  echo $result;
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
      return 0;
    } else {
      // TO DO: handle insertion error
      $stmt->close();
      return 1;
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
