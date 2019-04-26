<?php
    if(isset($_POST["userid"])){
        require_once 'config.php';

        $userid = $_POST["userid"];
        $firstname = $_POST["firstname"];
        $lastname = $_POST["lastname"];
        $email = $_POST["email"];
        $telephone = $_POST["phone"];
        $password = $_POST["password"];

        $encrypt_password = crypt($password, '$6$rounds=1000$ThisshouldBeCool$');

        
        // $userid = "a";
        // $firstname = "b";
        // $lastname = "c";
        // $email = "a@gmail.com";
        // $telephone = "+233565656";
        // $password = "hi";
        
        $register = $con->prepare("INSERT INTO PalExpressusers(userid,firstname,lastname,email,telephone,password) VALUES(?,?,?,?,?,?)");
        $registering = $register->execute(array($userid,$firstname,$lastname,$email,$telephone,$encrypt_password));

        if($registering){
            echo "Registration Complete";
        }else{
            echo "Registration Failed.Try Again Later";
        }
    }
    else{
        die("Access Denied.");
    }
?>
