<?php
// Start the session
session_start();
?>
<!DOCTYPE html>
<html>
  <head>
    <title>Account Management</title>
     
  </head>
  
<body>
<section>
  <h1>Account Management</h1>
</section>
<section>
  
   
  <button type="button"  onclick="location.href='welcome.php';">Map <br> Page</button>
  <button type="button"  onclick="location.href='index.php';">Logout</button>
</section>
<section>
  Welcome <?php echo $_SESSION["myName"]; ?><br>
  Your email address is: <?php echo $_SESSION["myEmail"]; ?>
</section>
 
</body>
</html>