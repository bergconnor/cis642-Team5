<?php
/**
 * Script to process a data upload
 * of information from a specific test.
 * The upload is initiated from the
 * android app.
 */
require_once './../lib/config.php';   // database connection
require_once './../lib/modules.php';  // modules for database queries

$data = array("user_id", "date", "test", "serial", "concentration", "latitude",
         "longitude", "city", "state", "temperature", "precipitation", "comment");

/**
 * Handle event when sign up button
 * is pressed.
 */
if(!empty($_POST)) {
  global $pdo;

  // initialize variables
  $json = array();
  $json['success'] = false;
  $json['message'] = '';

  $empty = false;
  foreach($data as $field) {
    if(empty($_POST[$field]) && $field != "comment") {
      $json['var'] = $field;
      $empty = true;
    }
  }

  if($empty) {
    // empty field
    $json['success'] = false;
    $json['message'] = 'Please fill out all fields.';
  } else {
    // attempt to upload data
    $date           = date("Y-m-d H:i", strtotime($_POST['date']));
    $json['date'] = $date;
    $user_id        = (int)$_POST['user_id'];
    $test_id        = getTestId($_POST['test']);
    $concentration  = $_POST['concentration'];
    $latitude       = $_POST['latitude'];
    $longitude      = $_POST['longitude'];
    $serial         = $_POST['serial'];
    $city           = $_POST['city'];
    $state          = $_POST['state'];
    $temperature    = $_POST['temperature'];
    $precipitation  = $_POST['precipitation'];
    $comment        = $_POST['comment'];
    $verified       = false;

    $stmt = $pdo->prepare('INSERT INTO markers (date, user_id, test_id, concentration,
                           latitude, longitude, serial, city, state, temperature,
                           precipitation, comment, verified)
                           VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)');

    if($stmt->execute([$date, $user_id, $test_id, $concentration, $latitude, $longitude,
                    $serial, $city, $state, $temperature, $precipitation, $comment, $verified])) {
      $json['success'] = true;
      $json['message'] = 'Successfully uploaded data.';
    } else {
      $json['success'] = false;
      $json['message'] = 'Something went wrong.';
    }
  }
  // send back data as a json string
  echo json_encode($json);
}

/**
 * Verify user's login information with the database.
 * @param object $conn A MySQL connection object.
 * @return int The test type id.
 */
function getTestId($test) {
  global $pdo;

  $stmt = $pdo->prepare('SELECT id FROM tests WHERE type=?');
  $stmt->execute([$test]);

  if($stmt->rowCount() > 0) {
    // user info found
    $test = $stmt->fetch();
    return (int)$test['id'];
  }
  else {
    // failed to retrieve id
    return -1;
  }
}

?>
