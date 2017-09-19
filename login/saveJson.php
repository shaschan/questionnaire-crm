?<?php
	ob_start();
	require "loginheader.php";
	
	
	
	//C:\Users\Dell-pc\Documents\basicquestionnaire-master\app\src\main\res\raw
	//C:\Users\Dell-pc\Documents\basicquestionnaire-master\app\src\main\assets
	
	//print_r($_POST["data"]);die;
	$myFile = "questions.json";
	$fh = fopen($myFile, 'w') or die("can't open file");
	$stringData = $_POST["data"];
	fwrite($fh, $stringData);
	fclose($fh);
	copy($myFile,"../basicquestionnaire-master/app/src/main/assets/questions.json");
	ob_end_flush();
?>