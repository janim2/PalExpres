<?php
    if(isset($_POST["phone"]) && isset($_POST["password"])){
        require_once 'config.php';
        
        $phone = $_POST["phone"];
        $password  = $_POST["password"];

$encrypt_password = crypt($password,'$6$rounds=1000$ThisshouldBeCool$');

        $loginMeIn = $con->prepare("SELECT COUNT(1) AS login_check FROM PalExpressusers WHERE telephone= ? AND password=?");
        $loginMeIn->execute(array($phone,$encrypt_password));

        $loggedIn = $loginMeIn->fetch();

        if($loggedIn['login_check'] == 1){
            echo "login Successful";
        }else{
            echo "Incorrect Credentials";
        }
    }
    else{
        die("<h1>Cannot Access This Page</h1>");
    }
?>


<!-- the url for the login and register -->
<!-- http://iamjesse75.000webhostapp.com/PalExpress/login.php

http://iamjesse75.000webhostapp.com/PalExpress/signup.php -->
