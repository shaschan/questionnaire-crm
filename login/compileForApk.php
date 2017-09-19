<?php 	require "loginheader.php";
ini_set('max_execution_time', 300);
$command = "cd ..\basicquestionnaire-master && gradlew assembledebug 2>&1";
$output = shell_exec($command);
//echo "<pre>$output</pre>";
$WshShell = new COM("WScript.Shell"); 
$oExec = $WshShell->Run("cmd /C dir /S %windir%", 0, false);
echo "<pre>$oExec</pre>";

if(is_file("../basicquestionnaire-master/app/build/outputs/apk/app-debug.apk")){
	copy("../basicquestionnaire-master/app/build/outputs/apk/app-debug.apk","../QRE_APK.apk");
	unlink("../basicquestionnaire-master/app/build/outputs/apk/app-debug.apk");
}

$files_ass = glob('../basicquestionnaire-master/app/src/main/assets/*'); // get all file names
foreach($files_ass as $file){ // iterate files
  if(is_file($file))
    unlink($file); // delete file
}

$files_raw = glob('../basicquestionnaire-master/app/src/main/res/raw/*'); // get all file names
foreach($files_raw as $file){ // iterate files
  if(is_file($file))
    unlink($file); // delete file
}

?>