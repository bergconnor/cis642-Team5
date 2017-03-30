<?php
require_once 'config.php';

if(isset($_GET['email']) && !empty($_GET['email']) AND isset($_GET['hash']) && !empty($_GET['hash'])){
    // verify data
    $email  = $_GET['email'];
    $hash   = $_GET['hash'];

    // create a prepared statement
    $stmt = $pdo->prepare('SELECT email, hash, active FROM users WHERE email=? AND hash=?');
    $stmt->execute([$email, $hash]);

    if($stmt->rowCount() > 0) {
      $stmt = $pdo->prepare('UPDATE users SET active="1" WHERE email=? AND hash=? AND active="0"');
      $stmt->execute([$email, $hash]);
      $msg = 'Your acount has been activated, you can now login';
      echo '<div class="statusmsg">'.$msg.'</div>';
    } else {
      $msg = 'Please use the link that has been sent to your email to verify your account.';
      echo '<div class="statusmsg">'.$msg.'</div>';
    }
  }
?>

<!DOCTYPE html>

<html>
<head>
    <title>Water Quality</title>
    <link href="../css/style.css" type="text/css" rel="stylesheet" />
</head>
<body>
  <div class="login-help">
    <p><a class="reset" href="index.php">Click here to return to the login page</a>.</p>
  </div>
</body>
</html>
