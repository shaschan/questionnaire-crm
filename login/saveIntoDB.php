<?php
	ob_start();
	require "loginheader.php";
	require 'includes/functions.php';
	
	$response 	 = '';
	$save_data = array();
	//print_r($_POST);die;
	if(isset($_POST) && !empty($_POST) && $_POST['delQuesEntry'] == 'N'){
		$save_data = array(
						'projID'		=> $_POST['projectID'],
						'QuesNum'   	=> $_POST['QuesNum'],
						'QuesText'  	=> $_POST['QuesText'],
						'QuesType'  	=> $_POST['QuesType'],
						'totalOpts' 	=> $_POST['totalOpts'],
						'file_name'     => $_POST['upAllow'],
						'EnableBlank' 	=> $_POST['blankEnab'],
						'options'   	=> array(
										)
					);
			
		for($count = 1; $count <= $_POST['totalOpts']; $count++) {
			$save_data['options'][$count]['opsNum']       = $_POST['opsNum_'.$count];
			$save_data['options'][$count]['opsText'] 	  = $_POST['opsText_'.$count];
			$save_data['options'][$count]['skipSin']      = $_POST['skipSin_'.$count];
			$save_data['options'][$count]['skipMul'] 	  = $_POST['skipMul_'.$count];
			$save_data['options'][$count]['Del12depen']   = $_POST['Del12depen_'.$count];
			$save_data['options'][$count]['Del12undepen'] = $_POST['Del12undepen_'.$count];
		}
		
		if($save_data['file_name'] != "" && file_exists('uploads/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_".$save_data['file_name'])){
			$save_data['file_name'] = 'file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_".$save_data['file_name'];
			if((substr($_POST['QuesType'],0,8) == "Conjoint") || (substr($_POST['QuesType'],0,3) == "Max")){
				foreach (glob('../basicquestionnaire-master/app/src/main/assets/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_*") as $filename) {
					unlink($filename);
				}
				copy('uploads/'.$save_data['file_name'],'../basicquestionnaire-master/app/src/main/assets/'.$save_data['file_name']);
			}else if($_POST['QuesType'] == "Image based" || $_POST['QuesType'] == "Video based"){
				foreach (glob('../basicquestionnaire-master/app/src/main/res/raw/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_*") as $filename) {
					unlink($filename);
				}
				copy('uploads/'.$save_data['file_name'],'../basicquestionnaire-master/app/src/main/res/raw/'.$save_data['file_name']);
			}else{
				$save_data['file_name'] = "";
			}
		}else{
			$save_data['file_name'] = "";
		}
	}
	if(isset($_FILES) && !empty($_FILES)){
		$save_data['file_name'] =  'file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_".$_FILES['file']['name'];
		
		if((substr($_POST['QuesType'],0,8) == "Conjoint")  || ((substr($_POST['QuesType'],0,3) == "Max"))){
			
			foreach (glob('../basicquestionnaire-master/app/src/main/assets/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_*") as $filename) {
				unlink($filename);
			}
			
			if ($_FILES['file']['error'] == '0' && move_uploaded_file($_FILES['file']['tmp_name'],'../basicquestionnaire-master/app/src/main/assets/'.$save_data['file_name'])) {
				echo "File Successfully Uploaded";

				foreach (glob('uploads/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_*") as $filename) {
					unlink($filename);
				}				
				copy('../basicquestionnaire-master/app/src/main/assets/'.$save_data['file_name'],'uploads/'.$save_data['file_name']);
			}else{
				echo $_FILES['file']['error'];
			}
		}else{
			
			foreach (glob('../basicquestionnaire-master/app/src/main/res/raw/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_*") as $filename) {
				unlink($filename);
			}
			
			if ($_FILES['file']['error'] == '0' && move_uploaded_file($_FILES['file']['tmp_name'],'../basicquestionnaire-master/app/src/main/res/raw/'.$save_data['file_name'])) {
				echo "File Successfully Uploaded";
				
				foreach (glob('uploads/file_'.$_SESSION['projectID']."_".$save_data['QuesNum']."_*") as $filename) {
					unlink($filename);
				}	
				copy('../basicquestionnaire-master/app/src/main/res/raw/'.$save_data['file_name'],'uploads/'.$save_data['file_name']);
			}else{
				echo $_FILES['file']['error'];
			}
		}
	}
	if(isset($_POST) && !empty($_POST) && $_POST['delQuesEntry'] == 'N'){
		//print_r($save_data);die;
		$quesOpsData = new QuestionOption;
		if(!empty($save_data)){
			$isExisting = $quesOpsData->checkExisting($save_data['projID'],$save_data['QuesNum']);
			if($isExisting == 'true'){
				$response = $quesOpsData->updateData($save_data);
			}else{
				if($isExisting == 'false'){
					$response = $quesOpsData->insertData($save_data);
				}else{
					$response = $isExisting;
				}
			}
		}
	}
	//print_r($response);die;
	if(isset($_POST) && !empty($_POST) && $_POST['delQuesEntry'] == 'Y'){
		$quesOpsData = new QuestionOption;
		$isExisting = $quesOpsData->checkExisting($_POST['projectID'],$_POST['QuesNum']);
		if($isExisting == 'true'){
			$response = $quesOpsData->delExisting($_POST['projectID'],$_POST['QuesNum']);
			foreach (glob('../basicquestionnaire-master/app/src/main/res/raw/file_'.$_POST['projectID']."_".$_POST['QuesNum']."_*") as $filename) {
				unlink($filename);
			}
			foreach (glob('../basicquestionnaire-master/app/src/main/assets/file_'.$_POST['projectID']."_".$_POST['QuesNum']."_*") as $filename) {
				unlink($filename);
			}
			foreach (glob('uploads/file_'.$_POST['projectID']."_".$_POST['QuesNum']."_*") as $filename) {
				unlink($filename);
			}
		}else{
			$response = "Question Doesnot Exist to Delete!!!";
		}
		print_r($response);die;
	}
	
	if(isset($_POST) && !empty($_POST) && $_POST['delQuesEntry'] == 'FETCHONLY'){
		$quesOpsData = new QuestionOption;
		echo $response = json_encode($quesOpsData->findProj($_POST['projectID']));
	}
	
	if(isset($_POST) && !empty($_POST) && $_POST['delQuesEntry'] == 'RENAMEFILE'){
		if(file_exists('uploads/'.$_POST['fromFile'])){
			return rename('uploads/'.$_POST['fromFile'],'uploads/'.$_POST['toFile']);
		}
	}
	
	return $response;
	
	//print_r($save_data);die;
	//print_r($_POST);die;
	/*POST is array(
	[QuesNum] => 2
    [QuesText] => 
    [QuesType] => null
    [file] => undefined
    [totalOpts] => 2
    [opsNum_1] => 
    [opsText_1] => 
    [skipSin_1] => null
    [skipMul_1] => null
    [Del12depen_1] => Y
    [Del12undepen_1] => N
    [opsNum_2] => 
    [opsText_2] => 
    [skipSin_2] => null
    [skipMul_2] => null
    [Del12depen_2] => N
    [Del12undepen_2] => Y)
	*/
	//print_r($_FILES);die;
	/*[file] array is:
	[file] => Array
        (
            [name] => test.png
            [type] => image/png
            [tmp_name] => C:\xampp\tmp\php9E21.tmp
            [error] => 0
            [size] => 7443
        )
	*/
	ob_end_flush();
?>