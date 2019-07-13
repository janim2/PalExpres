<?php 

if(isset($_POST["vendorId"])){
	require_once('config.php');

	$vendorId = $_POST["vendorId"];

	$getFood = $con->prepare("SELECT foodid,foodimage,foodname,fooddescription,foodprize FROM PalExpressMenu WHERE vendorid = ?");
	$getFood->execute(array($vendorId));

	$food_array = array();

	while($gettingIt = $getFood->fetch()){
		array_push($food_array,array(

		"foodid" => $gettingIt["foodid"],
		"foodimage" => $gettingIt["foodimage"],
		"foodname" => $gettingIt["foodname"],
		"fooddescription" => $gettingIt["fooddescription"],
		"foodprize" => $gettingIt["foodprize"],
		
	));
	}

	echo json_encode(array("Server_response" => $food_array));

	}else{
		echo "<b>Access Denied</b>";
	}

?>

