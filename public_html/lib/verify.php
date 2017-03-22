<?php
require_once 'config.php';

if(isset($_GET['email']) && !empty($_GET['email']) AND isset($_GET['hash']) && !empty($_GET['hash'])){
    // verify data
    $email = mysqli_real_escape_string($conn, $_GET['email']);
    $hash = mysqli_real_escape_string($conn, $_GET['hash']);

    // create a prepared statement
    $stmt = $conn->stmt_init();

    if($stmt->prepare('SELECT email, hash, active FROM users WHERE email=? AND hash=?')) {
      // bind parameters and execute
      $stmt->bind_param('ss', $email, $hash);
      $stmt->execute();
      $stmt->bind_result($email, $hash, $active);

      if($stmt->fetch() > 0) {
        $stmt->close();
        $stmt = $conn->stmt_init();

        if($stmt->prepare('UPDATE users SET active="1" WHERE email=? AND hash=? AND active="0"')) {
          // bind parameters and execute
          $stmt->bind_param('ss', $email, $hash);
          if($stmt->execute()) {
            $msg = 'Your acount has been activated, you can now login';
            echo '<div class="statusmsg">'.$msg.'</div>';
          }
        }
      } else {
        $msg = 'Please use the link that has been sent to your email to verify your account.';
        echo '<div class="statusmsg">'.$msg.'</div>';
      }
    } else {
      $msg = 'No account has been verified.';
      echo '<div class="statusmsg">'.$msg.'</div>';
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
  <div class="login-help">
    <p><a class="reset" href="index.php">Click here to return to the login page</a>.</p>
  </div>
</body>
</html>
