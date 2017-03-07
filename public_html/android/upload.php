<?php
/**
 * Script to process a data upload
 * of information from a specific test.
 * The upload is initiated from the
 * android app.
 */
require_once '../config.php';   // database connection
require_once '../modules.php';  // modules for database queries

$data = ("user_id", "date", "test", "serial", "concentration", "latitude",
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
    if(empty($_POST[$field])) {
      $flag = true;
    }
  }

  if($flag) {
    // empty field
    $json['success'] = false;
    $json['message'] = 'Please fill out all fields.';
  } else {
    // attempt to upload data
    $user_id        = mysqli_real_escape_string($conn, $_POST['user_id']);
    $date           = mysqli_real_escape_string($conn, $_POST['date']);
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

    $stmt = $conn->stmt_init();
    if($stmt->prepare('INSERT INTO markers (user_id, date, test_id, serial,
                          latitude, longitude, city, state, temperature,
                          precipitation, comment, verified)
                       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)')) {
      $stmt->bind_param('ssssssssssss', $user_id, $date, $test_id, $serial,
                        $latitude, $longitude, $city, $state, $temperature,
                        $precipitation, $comment, false);

      if($stmt->execute()) {
        // success
        mysqli_close($conn);
        return 0;
      } else {
        // insertion error
        $stmt->close();
        mysqli_close($conn);
        return 1;
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
  $type = mysqli_real_escape_string($conn, $test)

  $stmt = $conn->stmt_init();
  if($stmt->prepare('SELECT id FROM tests WHERE type=?')) {
    $stmt->bind_param('s', $type);
    $stmt->execute();
    $stmt->bind_result($id);

    if($stmt->fetch() > 0) {
      // user info found
      $stmt->close();
      mysqli_close($conn);
      return $id;
    }
    else {
      // failed to retrieve id
      $stmt->close();
      mysqli_close($conn);
      return -1;
    }
  } else {
    $stmt->close();
    mysqli_close($conn);
    return -1;
  }
}

?>
