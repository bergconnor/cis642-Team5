<?php
require_once 'config.php';

if(isset($_POST['return'])) {
  header('location: index.php');
  exit();
}

if(isset($_POST['sign_up'])) {
  // process data if the sign up button was pressed
  if(empty($_POST['first']) || empty($_POST['last']) || empty($_POST['email']) ||
     empty($_POST['pass1']) || empty($_POST['pass2'])) {
     // one of the fields is empty
    $msg = 'Please fill out all fields.';
  } elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
      // invalid email
      $msg = 'Please enter a valid email.';
  } elseif(strcmp($_POST['pass1'], $_POST['pass2']) != 0) {
      // passwords do not match
      $msg = 'Passwords do not match.';
  } else {
      // prepare sql variables
      $first = mysqli_real_escape_string($conn, $_POST['first']);
      $last  = mysqli_real_escape_string($conn, $_POST['last']);
      $email = mysqli_real_escape_string($conn, $_POST['email']);
      $pass  = mysqli_real_escape_string($conn, $_POST['pass1']);

      // create a prepared statement
      $stmt = $conn->stmt_init();
      if($stmt->prepare('SELECT first, last FROM users WHERE email=?')) {

        // bind parameters and exectue
        $stmt->bind_param('s', $email);
        $stmt->execute();

        // bind result variables
        $stmt->bind_result($first, $last);

        if ($stmt->fetch() > 0) {
          $msg = 'Email address already in use.';
        } else {
            // information is valid
            $stmt->close();
            $stmt = $conn->stmt_init();

            if($stmt->prepare('INSERT INTO users (first, last, email, password, hash)
                               VALUES (?, ?, ?, ?, ?)')) {
              // create email verification hash
              $hash = md5(rand(0, 1000));

              // bind paramaters
              $stmt->bind_param('sssss', $first, $last, $email, $pass, $hash);

              if(!$stmt->execute()) {
                // insert was not successful
                echo '<div class="statusmsg">'.mysqli_error($conn).'</div>';
                $stmt->close();
                $stmt->close();
              } else {
                  // insert was successful
                  $msg = 'Success.';

                  $to      = $email; // Send email to our user
                  $subject = 'Signup | Verification'; // Give the email a subject
                  $message = '

                  Thanks for signing up!
                  Your account has been created, you can login after you have
                  activated your account by pressing the url below.

                  Please click this link to activate your account:
                  http://people.cs.ksu.edu/~cberg1/verify.php?email='.$email.'&hash='.$hash.'

                  '; // Our message above including the link

                  $headers = 'From:noreply@people.cs.ksu.edu' . "\r\n"; // Set from headers
                  mail($to, $subject, $message, $headers); // Send our email

                  $stmt->close();
                  header('location: index.php');
                  exit();
                }
            }
          }
      }
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
