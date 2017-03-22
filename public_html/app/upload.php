<?php
/**
 * Script to process a data upload
 * of information from a specific test.
 * The upload is initiated from the
 * android app.
 */
require_once '../config.php';   // database connection
require_once '../modules.php';  // modules for database queries

$data = array("user_id", "date", "test", "serial", "concentration", "latitude",
         "longitude", "city", "state", "temperature", "precipitation", "comment");

/**
 * Handle event when sign up button
 * is pressed.
 */
if(!empty($_POST)) {
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
    $date = date("Y-m-d", strtotime($_POST['date']));

    $date           = mysqli_real_escape_string($conn, $date);
    $user_id        = mysqli_real_escape_string($conn, (int)$_POST['user_id']);
    $test_id        = mysqli_real_escape_string($conn, getTestId($conn, $_POST['test']));
    $serial         = mysqli_real_escape_string($conn, $_POST['serial']);
    $concentration  = mysqli_real_escape_string($conn, $_POST['concentration']);
    $latitude       = mysqli_real_escape_string($conn, $_POST['latitude']);
    $longitude      = mysqli_real_escape_string($conn, $_POST['longitude']);
    $city           = mysqli_real_escape_string($conn, $_POST['city']);
    $state          = mysqli_real_escape_string($conn, $_POST['state']);
    $temperature    = mysqli_real_escape_string($conn, $_POST['temperature']);
    $precipitation  = mysqli_real_escape_string($conn, $_POST['precipitation']);
    $comment        = mysqli_real_escape_string($conn, $_POST['comment']);
    $verified       = mysqli_real_escape_string($conn, false);

    $stmt = $conn->stmt_init();
    if($stmt->prepare('INSERT INTO markers (user_id, date, test_id, serial,
                          latitude, longitude, city, state, temperature,
                          precipitation, comment, verified)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)')) {
      $stmt->bind_param('ssssssssssss', $user_id, $date, $test_id, $serial,
                        $latitude, $longitude, $city, $state, $temperature,
                        $precipitation, $comment, $verified);

      if($stmt->execute()) {
        // success
        $json['success'] = true;
        $json['message'] = 'Successfully uploaded data.';
        $stmt->close();
        mysqli_close($conn);
      } else {
        // insertion error
        $json['success'] = false;
        $json['message'] = mysqli_error($conn);//'Failed to upload data.';
        $json['var'] = $var;
        $stmt->close();
        mysqli_close($conn);
      }
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
function getTestId($conn, $test) {
  $type = mysqli_real_escape_string($conn, $test);

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT id FROM tests WHERE type=?')) {
    $stmt->bind_param('s', $type);
    $stmt->execute();
    $stmt->bind_result($id);

    if($stmt->fetch() > 0) {
      // user info found
      $stmt->close();
      return (int)$id;
    }
    else {
      // failed to retrieve id
      $stmt->close();
      return -1;
    }
  } else {
    $stmt->close();
    return -1;
  }
}

?>
