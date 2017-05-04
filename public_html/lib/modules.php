<?php

require_once('config.php');

/**
 * Verify user's login information with the database.
 * @return int   Indicates an error or success.
 */
function login($email, $pass) {
  global $pdo;

  $stmt = $pdo->prepare('SELECT email, cryptedPassword, active FROM users
                         WHERE email=?');
  $stmt->execute([$email]);

  if($stmt->rowCount() > 0) {
    $user = $stmt->fetch();
    if(password_verify($pass, $user['cryptedPassword'])) {
      if($user['active'] == 1) {
        // valid account
        return 0;
      } else {
        // account not activated
        return 1;
      }
    } else {
      // invalid account
      return 2;
    }
  }
}

/**
 * Checks if the provided email is in use or not .
 * @param string $email The user's email.
 * @return bool  Indicates a valid or invalid email.
 */
function check_email($email) {
  global $pdo;

  $stmt = $pdo->prepare('SELECT first, last FROM users WHERE email=?');
  $stmt->execute([$email]);
  $user = $stmt->fetch();

  if ($stmt->rowCount() > 0) {
    // email is in use
    return false;
  } else {
    // email is not in use
    return true;
  }
}

/**
 * Insert the user's information into the database
 * to create a new account.
 * @param string $first The user's first name.
 * @param string $last  The user's last name.
 * @param string $email The user's email.
 * @param string $pass  The user's password.
 * @return int   Indicates an error or success.
 */
function sign_up($first, $last, $org, $email, $pass) {
  global $pdo;

  $stmt = $pdo->prepare('INSERT INTO users (first, last, organization, email, cryptedPassword, hash)
                         VALUES (?, ?, ?, ?, ?, ?)');
  $pass = password_hash($pass, PASSWORD_DEFAULT);
  $hash = md5(rand(0, 1000)); // account verification hash

  if($stmt->execute([$first, $last, $org, $email, $pass, $hash])) {
    // success, send email
    send_email($email, $hash);
    return 0;
  } else {
    // insertion error
    return 1;
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
  http://people.cs.ksu.edu/~cberg1/public/verify.php?email='.$email.'&hash='.$hash.'

  ';

  $headers = 'From:noreply@people.cs.ksu.edu' . "\r\n";
  mail($to, $subject, $message, $headers);
}

?>
