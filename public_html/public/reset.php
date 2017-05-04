<?php
/**
 * A page allowing the user to reset
 * their password.
 */
session_start();
require_once './../lib/config.php';

/**
 *
 */
if(isset($_POST['submit'])) {
  if($_SESSION['email_sent']) {
    if(empty($_POST['temp']) || empty($_POST['pass1']) ||
       empty($_POST['pass2'])) {
      // one of the fields is empty
      $msg = 'Please fill out all fields.';
    } elseif(strcmp($_POST['pass1'], $_POST['pass2']) != 0) {
        // passwords do not match
        $msg = 'Passwords do not match.';
    } else {
        $msg = update_pass($conn, $_SESSION['email'], $_POST['pass1']);
        $_SESSION['email_sent'] = false;
    }
  } else {
      if(empty($_POST['email'])) {
        // email field is empty
        $msg = 'Please provide your email.';
      } else {
          $msg = reset_pass($conn);
          $_SESSION['email'] = $_POST['email'];
          $_SESSION['email_sent'] = true;
      }
  }

  if(isset($msg)) {
    echo '<div class="statusmsg">'.$msg.'</div>';
  }
}

function update_pass($conn, $email, $password) {
  $stmt = $conn->stmt_init();

  if($stmt->prepare('UPDATE users SET password=? WHERE email=?')) {
    $stmt->bind_param('ss', $password, $email);
    if($stmt->execute()) {
      $stmt->close();
      return 'Password successfully reset.';
    } else {
      // TO DO: handle execute error
      return 'Execution error.';
    }
  } else {
    // TO DO: handle prepare error
    return 'Prepare error.';
  }
}

function reset_pass($conn) {
  global $pdo;

  $stmt = $pdo->prepare('SELECT count(*) as count FROM users
                         WHERE email=?'));
  $stmt->execute([$_POST['email']]);
  if($stmt->prepare('SELECT count(*) AS count FROM users
                     WHERE email=?')) {

    if($stmt->rowCount() > 0) {
      // email address found, send email

      $password = rand(100000,999999);
      $stmt = $pdo->prepare('UPDATE users SET password=? WHERE email=?');

      if($stmt->prepare('UPDATE users SET password=? WHERE email=?')) {
        $stmt->bind_param('ss', $password, $email);
        if($stmt->execute()) {
          $stmt->close();
          send_email($email, $password);
          return 'Email sent, please enter the code and reset your password.';
        }
      }
    } else {
      // email address not found
      return 'Email address not registered.';
    }
  }
}

function send_email($email, $password) {
  $to      = $email; // Send email to our user
  $subject = 'Password Reset'; // Give the email a subject
  $message = '

    Please use the following temporary password to
    reset your account: '.$password.'

  '; // Our message above including the link

  $headers = 'From:noreply@people.cs.ksu.edu' . "\r\n"; // Set from headers
  mail($to, $subject, $message, $headers); // Send our email
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
    <div class="container ">
      <div class="form">
        <h1>Reset Password</h1>
        <form action="" method="post">
          <?php if(!$_SESSION['email_sent']) : ?>
            <p><input type="email" name="email" placeholder="Confirm email"></p>
          <?php else : ?>
            <p><input type="password" name="temp" placeholder="Temporary password"></p>
            <p><input type="password" name="pass1" placeholder="New password"></p>
            <p><input type="password" name="pass2" placeholder="Verify new password"></p>
          <?php endif; ?>
          <p class="submit">
            <input class="btn btn-default" type="submit" name="submit" value="Submit" align="left">
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
