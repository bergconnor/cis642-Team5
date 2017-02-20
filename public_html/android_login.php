<?php
require_once 'config.php'; /* database connection */

if(!empty($_POST)) {
  $result = '';

  if(empty($_POST['email']) || empty($_POST['pass'])) {
    /* empty email or password field */
    $result = 'One of the fields is empty.';
  }
  elseif(!filter_var($_POST['email'], FILTER_VALIDATE_EMAIL)) {
    /* invalid email format */
    $result = 'Invalid email.';
  }
  else {
    /* check database for information */
    $result = login($conn);
    switch($result) {
      case 0:
        $result = 'true';
        break;
      case 1:
        $result = 'You need to activate your account.';
        break;
      case 2:
        $result = 'Invalid account.';
        break;
      case 3:
        $result = 'SQL error.';
        break;
      default:
        $result = 'An unknown error has occured.';
        break;
    }
  }
  echo $result;
}

/**
 * Verify user's login information with the database.
 * @param object $conn A MySQL connection object.
 * @return string A success or error message.
 */
function login($conn) {
  $email = mysqli_real_escape_string($conn, $_POST['email']);
  $pass = mysqli_real_escape_string($conn, $_POST['pass']);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT email, password, active FROM users
                     WHERE email=? AND password=?')) {

    $stmt->bind_param('ss', $email, $pass);
    $stmt->execute();

    $stmt->bind_result($email, $password, $active);

    if($stmt->fetch() > 0) {
      if($active == 0) {
        /* account not activated */
        $stmt->close();
        return 1;
      } else {
          /* valid account */
          $stmt->close();
          return 0;
        }
    } else {
        /* invalid account */
        $stmt->close();
        return 2;
    }
  } else {
    // TO DO: handle error if $stmt->prepare() fails
    $stmt->close();
    return 3;
  }
}
?>
