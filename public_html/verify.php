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
            $msg = 'Invalid approach, please use the link that has been sent to your email.';
          }
        } else {
          $msg = 'Error';
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
</body>
</html>
