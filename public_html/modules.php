<?php

/**
 * Verify user's login information with the database.
 * @param object $conn A MySQL connection object.
 * @return int   Indicates an error or success.
 */
function login($conn, $email, $password) {
  $email    = mysqli_real_escape_string($conn, $email);
  $password = mysqli_real_escape_string($conn, $password);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT email, password, active FROM users
                     WHERE email=? AND password=?')) {
    $stmt->bind_param('ss', $email, $password);
    $stmt->execute();
    $stmt->bind_result($email, $password, $active);

    if($stmt->fetch() > 0) {
      if($active == 1) {
        // valid account
        $stmt->close();
        mysqli_close($conn);
        return 0;
      } else {
        // account not activated
        $stmt->close();
        mysqli_close($conn);
        return 1;
        }
    } else {
      // invalid account
      $stmt->close();
      mysqli_close($conn);
      return 2;
    }
  } else {
    // prepare() failed
    $stmt->close();
    mysqli_close($conn);
    return 3;
  }
}

/**
 * Checks if the provided email is in use or not.
 * @param object $conn  stringA MySQL connection object.
 * @param string $email The user's email.
 * @return bool  Indicates a valid or invalid email.
 */
function check_email($conn, $email) {
  $email  = mysqli_real_escape_string($conn, $email);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT first, last FROM users WHERE email=?')) {
    $stmt->bind_param('s', $email);
    $stmt->execute();
    $stmt->bind_result($first, $last);

    if ($stmt->fetch() > 0) {
      // email is in use
      return false;
    } else {
      // email is not in use
      return true;
    }
  }
}

/**
 * Insert the user's information into the database
 * to create a new account.
 * @param object $conn  A MySQL connection object.
 * @param string $first The user's first name.
 * @param string $last  The user's last name.
 * @param string $email The user's email.
 * @param string $pass  The user's password.
 * @return int   Indicates an error or success.
 */
function sign_up($conn, $first, $last, $organization, $email, $password) {
  $first        = mysqli_real_escape_string($conn, $first);
  $last         = mysqli_real_escape_string($conn, $last);
  $organization = mysqli_real_escape_string($conn, $organization);
  $email        = mysqli_real_escape_string($conn, $email);
  $password     = mysqli_real_escape_string($conn, $password);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('INSERT INTO users (first, last, organization, email, password, hash)
                     VALUES (?, ?, ?, ?, ?, ?)')) {
    $hash = md5(rand(0, 1000)); // account verification hash
    $stmt->bind_param('ssssss', $first, $last, $organization, $email, $password, $hash);

    if($stmt->execute()) {
      // success, send email
      send_email($email, $hash);
      $stmt->close();
      mysqli_close($conn);
      return 0;
    } else {
      // insertion error
      $stmt->close();
      mysqli_close($conn);
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
