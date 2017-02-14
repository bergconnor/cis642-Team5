<?php
if(isset($_GET['reset_flag'])) {
  // reset password clicked
  header('location: reset.php');
  exit();
}

if(isset($_POST["sign_up"])) {
  // sign up button pressed
  header('location: sign_up.php');
  exit();
}

if(isset($_POST['login'])) {
  // process data if the login button is pressed
  if(empty($_POST['email']) || empty($_POST['pass'])) {
    $msg = 'Please provide your email address and password.';
  }
  elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    $msg = 'Invalid email.';
  }
  else {
    $msg = login();
  }
}

function login() {
  $email = mysqli_real_escape_string($conn, $_POST['email']);
  $pass = mysqli_real_escape_string($conn, $_POST['pass']);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT email, password, active FROM users
                     WHERE email=? AND password=?')) {
    // bind parameters and execute
    $stmt->bind_param('ss', $email, $pass);
    $stmt->execute();

    // bind result variables
    $stmt->bind_result($email, $password, $active);

    if($stmt->fetch() > 0) {
      // email and password match
      if($active == 0) {
        // account not activated
        return 'Please check your email to activate your account.';
      } else {
          // valid information
          return 'Success.';
          header('location: map.php');
          exit();
        }
    } else {
        // invalid information
        return 'Invalid email address or password.';
    }
  } else {
      return 'Error';
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

      <div class="login-help">
        <p>Forgot your password? <a class="reset" href="?reset_flag=true">Click here to reset it</a>.</p>
      </div>
    </div>
  </body>
</html>
