<?php
session_start();
require_once 'config.php';

if(isset($_POST['return'])) {
  header('location: index.php');
  exit();
}

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
  // prepare sql variables
  $email = mysqli_real_escape_string($conn, $_POST['email']);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT count(*) AS count FROM users
                     WHERE email=?')) {
    // bind parameters and execute
    $stmt->bind_param('s', $pass);
    $stmt->execute();

    // bind result variable
    $stmt->bind_result($count);

    if($stmt->fetch() > 0) {
      // email address found, send email
      $stmt->close();

      $password = mysqli_real_escape_string($conn,rand(100000,999999));
      $stmt = $conn->stmt_init();

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
    <link href="css/style.css" type="text/css" rel="stylesheet" />
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
            <input type="submit" name="return" value="Return" align="left">
            <input type="submit" name="submit" value="Submit" align="left">
          </p>
        </form>
      </div>
    </div>
  </body>
</html>
