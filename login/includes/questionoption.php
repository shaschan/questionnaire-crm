<?php
class QuestionOption extends DbConn
{
	public function findProj($projID)
    {
		$tbl_questionoption = '';
		$result				= array();
        try {
            $db 				= new DbConn;
            $tbl_questionoption = $db->tbl_questionoption;
            $err = '';

        } catch (PDOException $e) {

            $err = "Error: " . $e->getMessage();

        }

        $stmt = $db->conn->prepare("SELECT * FROM ".$tbl_questionoption." WHERE project_id = :ProjID ORDER BY QuesNum");
		$stmt->bindParam(':ProjID', $projID);
        $stmt->execute();

        // Gets query result
       $result = $stmt->fetchAll(PDO::FETCH_ASSOC);

		if(!empty($result)){
			return $result;
		}
		else{
			return ($err == '') ? 'false' : $err;
		}
	}
	public function checkExisting($projID, $quesNo)
    {
		$tbl_questionoption = '';
		$result				= array();
        try {
            $db 				= new DbConn;
            $tbl_questionoption = $db->tbl_questionoption;
            $err = '';

        } catch (PDOException $e) {

            $err = "Error: " . $e->getMessage();

        }

        $stmt = $db->conn->prepare("SELECT * FROM ".$tbl_questionoption." WHERE project_id = :ProjID AND QuesNum = :QuesNum");
        $stmt->bindParam(':QuesNum', $quesNo);
		$stmt->bindParam(':ProjID', $projID);
        $stmt->execute();

        // Gets query result
        $result = $stmt->fetch(PDO::FETCH_ASSOC);

		if(!empty($result)){
			return 'true';
		}
		else{
			return ($err == '') ? 'false' : $err;
		}
	}
	public function delExisting($projID, $quesNo)
    {
		$tbl_questionoption = '';
		$result				= array();
        try {
            $db 				= new DbConn;
            $tbl_questionoption = $db->tbl_questionoption;
            $err = '';

        } catch (PDOException $e) {

            $err = "Error: " . $e->getMessage();

        }

        $stmt = $db->conn->prepare("Delete FROM ".$tbl_questionoption." WHERE project_id = :ProjID AND QuesNum = :QuesNum");
        $stmt->bindParam(':QuesNum', $quesNo);
		$stmt->bindParam(':ProjID', $projID);
        $stmt->execute();
		
		return ($err == '') ? 'Successfully Deleted!!' : $err;
	}
    public function insertData($save_data)
    {
        try {
            $db 				= new DbConn;
            $tbl_questionoption = $db->tbl_questionoption;

            $datetimeNow = date("Y-m-d H:i:s");

            $stmt = $db->conn->prepare("INSERT INTO ".$tbl_questionoption." (project_id, questionText, QuesNum, QuesType, EnableBlank, totalOpts, options, file_name, mod_timestamp) values (:projID, :questionText, :QuesNum, :QuesType, :EnableBlank,:totalOpts, :options, :file_name, :mod_timestamp)");
			
            $stmt->bindParam(':projID', $save_data['projID']);
            $stmt->bindParam(':questionText', $save_data['QuesText']);
            $stmt->bindParam(':QuesNum', $save_data['QuesNum']);
			$stmt->bindParam(':QuesType', $save_data['QuesType']);
			$stmt->bindParam(':EnableBlank', $save_data['EnableBlank']);
            $stmt->bindParam(':totalOpts', $save_data['totalOpts']);
			$tmpArr = array();
			foreach ($save_data['options'] as $sub) {
			  $tmpArr[] = implode(';', $sub);
			}
			$result = implode('|', $tmpArr);
            $stmt->bindParam(':options', $result);
            $stmt->bindParam(':file_name', $save_data['file_name']);
            $stmt->bindParam(':mod_timestamp', $datetimeNow);
            $stmt->execute();
			
            $err = '';

        } catch (PDOException $e) {
            $err = "Error: " . $e->getMessage();
        }

        //Determines returned value ('true' or error code)
        $resp = ($err == '') ? 'true' : $err;

        return $resp;

    }

    public function updateData($save_data)
    {
        try {
            $db 				= new DbConn;
            $tbl_questionoption = $db->tbl_questionoption;
            $datetimeNow = date("Y-m-d H:i:s");

            $err = '';
			
			$sql = "UPDATE ".$tbl_questionoption." SET questionText = :questionText, QuesType = :QuesType, EnableBlank = :EnableBlank, totalOpts = :totalOpts, options = :options, file_name = :file_name, mod_timestamp = :mod_timestamp where project_id = :id and QuesNum = :QuesNum";
			$stmt = $db->conn->prepare($sql);
            $stmt->bindParam(':id', $save_data['projID']);
            $stmt->bindParam(':questionText', $save_data['QuesText']);
            $stmt->bindParam(':QuesNum', $save_data['QuesNum']);
			$stmt->bindParam(':QuesType', $save_data['QuesType']);
			$stmt->bindParam(':EnableBlank', $save_data['EnableBlank']);
            $stmt->bindParam(':totalOpts', $save_data['totalOpts']);
			$tmpArr = array();
			foreach ($save_data['options'] as $sub) {
			  $tmpArr[] = implode(';', $sub);
			}
			$result = implode('|', $tmpArr);
            $stmt->bindParam(':options', $result);
            $stmt->bindParam(':file_name', $save_data['file_name']);
            $stmt->bindParam(':mod_timestamp', $datetimeNow);
            $stmt->execute();


        } catch (PDOException $e) {

            $err = "Error: " . $e->getMessage();
        }

        //Determines returned value ('true' or error code) (ternary)
        $resp = ($err == '') ? 'true' : $err;

        return $resp;

    }

}
