<?php
// Start the session
session_start();

require_once './../lib/config.php';
require_once './../lib/modules.php';

// Check the login and acitivty status
if(isset($_SESSION['user'])) {
  if(isset($_SESSION['last_activity']) && (time() - $_SESSION['last_activity'] > 1800)) {
      // last request was more than 30 minutes ago
      // redirect to login screen
      session_unset();
      session_destroy();
      header('location: ./login.php');
  }
} else {
  // user not logged in
  // redirect to login screen
  session_unset();
  session_destroy();
  header('location: ./login.php');
}

if(isset($_GET['logout'])) {
  session_unset();
  session_destroy();
  header('location: ./login.php');
}

$email = $_SESSION['user'];
$user = get_account_info($email);
$organization = $user['organization'];

if(isset($_POST['update'])) {
  update_account($email, $_POST['email'], $_POST['organization']);
  $_SESSION['user'] = $_POST['email'];
  ChromePhp::log($_SESSION['user']);
  header('location: ./account.php');
}

?>

<html>
  <head>
    <title>Water Quality</title>
    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <link rel="stylesheet" href="../css/home.css" type="text/css" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta charset="utf-8">
  </head>
  <body>
    <div class="page-header">
      <!-- <div class="pull-right">
        <button type="button" class="btn btn-primary">Press me!</button>
      </div> -->
      <h1>Water Quality</h1>
    </div>
    <nav class="navbar navbar-default" role="navigation">
      <div class="container-fluid">
        <ul class="nav navbar-nav">
          <li id="home-nav">
            <a href="./../index.php">Home</a>
          </li>
          <li>
            <a href="./table.php">Table</a>
          </li>
          <li class="active">
            <a href="./account.php">Account</a>
          </li>
        </ul>
        <ul class="nav navbar-nav pull-right">
          <li>
            <a href="./../index.php?logout=true">Logout</a>
          </li>
        </ul>
      </div>
    </nav>
    <div account-info>
      <table class="table">
        <thead>
          <tr>
            <th colspan="2">Account Information</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>Name:</td>
            <td>Connor Berg</td>
          </tr>
          <tr>
            <td>Organization:</td>
            <td>Kansas State University</td>
          </tr>
          <tr>
            <td>Email:</td>
            <td>cberg1@ksu.edu</td>
          </tr>
        </tbody>
      </table>
      <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#accountModal">
        Update Account Information
      </button>
    </div>

    <div class="modal fade" id="accountModal" tabindex="-1" role="dialog" aria-hidden="true">
      <div class="modal-dialog" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">Update Account Information</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
            </button>
          </div>
          <div class="modal-body">
            <form action="" method="post">
              <div class="form-group">
                <input class="form-control" name="email" type="text" value="<?= $email; ?>">
              </div>
              <div class="form-group">
                <input class="form-control" name="organization" type="text" value="<?= $organization; ?>">
              </div>
              <div class="form-group">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
                <button type="submit" class="btn btn-primary" name="update">Save Changes</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <!-- jQuery library -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
    <!-- Latest compiled JavaScript -->
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="../js/details.js" type="text/javascript"></script>
  </body>
</html>
