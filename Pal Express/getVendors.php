<?php 
	require_once('config.php');

	$getVendor = $con->prepare("SELECT vendorid,vendorimage,vendorname,foodtags,paymentplans,foodpreptime,minorder,deliveryPrize FROM PalExpressVendors");
	$getVendor->execute();

	$vendor_array = array();

	while($gettingIt = $getVendor->fetch()){
		array_push($vendor_array,array(

		"vendorid" => $gettingIt["vendorid"],
		"vendorimage" => $gettingIt["vendorimage"],
		"vendorname" => $gettingIt["vendorname"],	
		"foodtags" => $gettingIt["foodtags"],	
		"paymentplans" => $gettingIt["paymentplans"],
		"foodpreptime" => $gettingIt["foodpreptime"],
		"minorder" => $gettingIt["minorder"],						
		"deliveryPrize" => $gettingIt["deliveryPrize"],								
	));
	}

	echo json_encode(array("Server_response" => $vendor_array));

?>

