<?php 	require "login/loginheader.php";

?>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <title>Login</title>
	<meta name="Description" content="Information architecture, Web Design, Web Standards." />
	<meta name="Keywords" content="QRE Backend" />
	<meta name="Distribution" content="Local-Instore only" />
	<meta name="Author" content="Shashish Chandra - shashishchandra@gmail.com" />
	<meta name="Robots" content="index,follow" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <!-- Bootstrap -->
    <link href="css/bootstrap.css" rel="stylesheet" media="screen">
    <link href="css/main.css" rel="stylesheet" media="screen">
	<link href="css/bootstrap-multiselect.css" rel="stylesheet" media="screen">
	
	<style type='text/css'>

	/* footer-wrap */
	#footer-wrap {
	  clear: both;
	  width: 100%;
	  font-size: 95%;
	  padding: 20px 0;
	  text-align: left;
	}
	#footer-wrap a {
	  text-decoration: none;
	  color: #666666;
	  font-weight: bold;
	}
	#footer-wrap a:hover {
	  color: #000;  
	}
	#footer-wrap p {
	  padding: 10px 0;
	}
	#footer-wrap h2 {
	  color: #666666;
	  margin: 0;
	  padding: 0 10px; 
	}

	/* footer */
	#footer {
	  clear: both;
	  color: #666;  
	  margin: 0 auto 10px auto; 
	  width: auto;
	  padding: 5px 0;
	  text-align: center;
	  background: #F8F7F7;
	  border-top: 1px solid #F2F2F2;  
	}

	</style>
  </head>
  <body>
  <input type="hidden" id="projectID" name="projectID" value="<?php echo $_SESSION['projectID'];?>">
  <div class="alert alert-success" >Project ID :<strong><?php echo $_SESSION['projectID'];?></strong></div>
   <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
    <script src="login/js/jquery-2.2.4.min.js"></script>
    <!-- Include all compiled plugins (below), or include individual files as needed -->
    <script type="text/javascript" src="login/js/bootstrap.js"></script>
	<script type="text/javascript" src="login/js/bootstrap-multiselect.js"></script>
	<div id="loadMsg" name="loadMsg"></div>
	<div id="quesList" name="quesList">	
		<button type="button" class="addNextQues" style="width:100%">Click to Start Adding Questions</button>
	</div>
	<div id="modalList" name="modalList">
	</div>
    <div class="container">
      <div class="form-signin">
        <div class="alert alert-success">You have been <strong>successfully logged in</strong> as <?php echo $_SESSION['username']?>.</div>
		<div id="submitMsg" name="submitMsg"></div>
		<button id="saveDataAll" name="saveDataAll" class="btn btn-default btn-lg btn-block">Submit Data</button>
		<button id="compileApk" name="compileApk" class="btn btn-default btn-lg btn-block" style="display:none">Compile APK</button>
		<a id="downloadAPK" name="downloadAPK" style="display:none" class="btn btn-default btn-lg btn-block" href="QRE_APK.apk">Download APK</a>
        <a href="login/logout.php" class="btn btn-default btn-lg btn-block">Logout</a>
      </div>
    </div> <!-- /container -->
	<footer class="modal-footer">
		<!-- <p style="text-align:center;"><strong>(1)&nbsp</strong>Image&nbspFile/&nbspVideo&nbspFile/&nbspConjoint's&nbspFile&nbspShould&nbspnot&nbspcontact&nbspspace&nbspor&nbsp'_'&nbspin&nbspits&nbspname</p>
		<p style="text-align:center;"><strong>(2)&nbsp</strong>Make&nbspsure&nbspthat&nbspin&nbsp<i>#others</i>&nbspfield,&nbsprespondent&nbspshould&nbspnot&nbspenter&nbspany&nbspnumeric&nbspdata&nbspor&nbspelse&nbspit&nbspwill&nbspbe&nbspconsidered&nbspas&nbspits&nbspoption&nbspcode</p>
		<p style="text-align:center;"><strong>(3)&nbsp</strong>First&nbspQuestion&nbspshould&nbspbe&nbspthe&nbspserial&nbspnumber</p>
		<p style="position:absolute; bottom:0px; background-color:orange; width:100%; text-align:center; vertical-align: super">&copy; All rights reserved In-Store<sup>&reg;</sup></p> -->
		<div id="wrap">

		  <!-- footer starts -->      
		  <div id="footer-wrap"><div id="footer">        
			  
			  <p>
			  &copy; 2017 In-Store

					&nbsp;&nbsp;&nbsp;&nbsp;

			  Created by <a href="http://www.linkedin.com/in/shashish-chandra-30944b35//">Shashish Chandra</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			  
			  <a href="#">Home</a> |
				   <a href="help.html">Help</a>
			  </p>
			  
		  </div></div>
		  <!-- footer ends-->  

		<!-- wrap ends here -->
		</div>
	</footer>
  </body>
</html>

<script type="text/javascript" src="login/js/js_customize.js"></script>