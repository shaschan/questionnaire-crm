//global variable for total option count specific to a question
window.optCount = [];
//total question count
window.quesCount = 0;

//session restore if saved 'quesDIV' and 'modalsDIV'
$(document).ready(function(){
	var form_data = new FormData();
	form_data.append('projectID', $('#projectID').val());
	form_data.append('delQuesEntry','FETCHONLY');
	$.ajax({
			url: "login/saveIntoDB.php",
			type: "POST",
			cache: false,
			contentType: false,
			processData: false,
			dataType: 'json',
			data: form_data,
			success: function(resp) {
				//console.log(resp); //resp[0]['QuesType'];
				if(resp !== 'false'){
					$("#loadMsg").html("<p class='text-center'>Successfully Loaded!</p>");
					
					quesCount = resp.length;
					
					for(var quesItr = 0; quesItr < resp.length; quesItr++){
						var fileshowtext = (resp[quesItr]["file_name"].length>0)?resp[quesItr]["file_name"].split('_')[3]:'nothing';
						
						var appStrForNewQues = '<div style="display:flex; vertical-align:top; margin-left: 550px" id="quesNo_'+resp[quesItr]['QuesNum']+'" name="quesNo_'+resp[quesItr]['QuesNum']+'"><button id="btnAddQues_'+resp[quesItr]['QuesNum']+'" name="btnAddQues_'+resp[quesItr]['QuesNum']+'" type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#addQues_'+resp[quesItr]['QuesNum']+'">Input Question No. '+resp[quesItr]['QuesNum']+'</button><button type="button" class="addNextQues">+</button><button type="button" class="deleteQues">X</button></div>';
						$("#quesList").append(appStrForNewQues);
						
						var appStrForNewModal = '<div id="addQues_'+resp[quesItr]['QuesNum']+'" name="addQues_'+resp[quesItr]['QuesNum']+'" class="modal fade" role="dialog"><div class="modal-dialog"><div class="modal-content" style="width: 200%;left: -50%;"><div class="modal-header"><button type="button" class="close" data-dismiss="modal">&times;</button><h4 class="modal-title" id="quesNoInHead_'+resp[quesItr]['QuesNum']+'" name="quesNoInHead_'+resp[quesItr]['QuesNum']+'">Input Fields For Question No. <strong>'+resp[quesItr]['QuesNum']+'</strong></h4></div><div class="modal-body"><input name="quesText_'+resp[quesItr]['QuesNum']+'" id="quesText_'+resp[quesItr]['QuesNum']+'" type="text" class="form-control" value="'+resp[quesItr]['questionText']+'" placeholder="Question Text" style="height: 200px" autofocus><select name="quesType_'+resp[quesItr]['QuesNum']+'" id="quesType_'+resp[quesItr]['QuesNum']+'" class="quesType" tabindex="1" style="width: 190px"><option selected disabled>Select Question Type</option><option value="Single Choice" id="SC" name="SC">Single Choice</option><option value="Multiple Choice" id="MC" name="MC">Multiple Choice</option><option value="Plain Text" id="TXT" name="TXT">Plain Text</option><option value="Image based" id="ÌMG" name="IMG">Image based</option><option value="Video based" id="VID" name="VID">Video Based</option><option value="Conjoint" id="CNG" name="CNG">Conjoint</option><option value="Max Diff Conjoint" id="MaxDiffCNG" name="MaxDiffCNG">Max Diff Conjoint</option></select><select type="button" id="inclRotNonOf_'+resp[quesItr]['QuesNum']+'" name="inclRotNonOf_'+resp[quesItr]['QuesNum']+'" tabindex="1" class="inclRotNonOf" style="display:none"><option value="InclNoneInclRot">Include Both Rotation and None of These</option><option value="InclRot">Include Rotation only</option><option value="InclNone">Include None of These only</option><option selected value="Dont">Do not Include Anything</option></select><input type="number" name="rotateAfter_'+resp[quesItr]['QuesNum']+'" id="rotateAfter_'+resp[quesItr]['QuesNum']+'" class="rotateAfter" style="display:none" placeholder="Select buffer Start Question"></input><input type="file" class="form-control" name="picUpld_'+resp[quesItr]['QuesNum']+'" id="picUpld_'+resp[quesItr]['QuesNum']+'" style="display:none"><div id="IsUpAllow_'+resp[quesItr]["QuesNum"]+'" name="IsUpAllow_'+resp[quesItr]["QuesNum"]+'" value="'+resp[quesItr]["file_name"]+'" style="display:none">Already uploaded -- '+fileshowtext+'</div><button type="button" id="blankEnab_'+resp[quesItr]['QuesNum']+'" name="blankEnab_'+resp[quesItr]['QuesNum']+'" class="blankEnable" style="display:none; background-color: cadetblue;">Nothing Enabled... Click to Enable Blank Fill</button><div id="optList_'+resp[quesItr]['QuesNum']+'" name="optList_'+resp[quesItr]['QuesNum']+'"><div><button type="button" class="addNext">Click to Start Adding options</button></div></div></div><div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button><button type="button" class="saveButton" data-dismiss="modal" id="save_'+resp[quesItr]['QuesNum']+'" name="save_'+resp[quesItr]['QuesNum']+'">Save</button></div></div></div></div>';
						
						$("#modalList").append(appStrForNewModal);
						
						$("#quesType_"+resp[quesItr]['QuesNum']).val(resp[quesItr]['QuesType'].split("_")[0]);
						
						var IsquesConj = resp[quesItr]['QuesType'].split("_");
						
						if(IsquesConj[0].toLowerCase() === "conjoint" || IsquesConj[0].toLowerCase() === "max diff conjoint"){
							if(IsquesConj[1] === "inclnoneinclrot")
								$("#inclRotNonOf_"+resp[quesItr]['QuesNum']).val('InclNoneInclRot');
							else if(IsquesConj[1] === "inclrot")
								$("#inclRotNonOf_"+resp[quesItr]['QuesNum']).val('InclRot');
							else if(IsquesConj[1] === "inclnone")
								$("#inclRotNonOf_"+resp[quesItr]['QuesNum']).val('InclNone');
							else if(IsquesConj[1] === "dont")
								$("#inclRotNonOf_"+resp[quesItr]['QuesNum']).val('Dont');
							
							$("#rotateAfter_"+resp[quesItr]['QuesNum']).val(IsquesConj[2]);
							$('#IsUpAllow_'+resp[quesItr]['QuesNum']).val(resp[quesItr]["file_name"]);
							$('#IsUpAllow_'+resp[quesItr]['QuesNum']).css('display', 'block');
							$('#picUpld_'+resp[quesItr]['QuesNum']).css('display', 'block');
							$("#rotateAfter_"+resp[quesItr]['QuesNum']).css('display', 'block');
							$("#inclRotNonOf_"+resp[quesItr]['QuesNum']).css('display', 'block');
						}
						
						if(IsquesConj[0].toLowerCase() === "video based" || IsquesConj[0].toLowerCase() === "image based"){								
							$('#IsUpAllow_'+resp[quesItr]['QuesNum']).val(resp[quesItr]["file_name"]);
							$('#picUpld_'+resp[quesItr]['QuesNum']).css('display', 'block');
							$('#blankEnab_'+resp[quesItr]['QuesNum']).css('display', 'block');
							$('#IsUpAllow_'+resp[quesItr]['QuesNum']).css('display', 'block');
							if(resp[quesItr]['EnableBlank'] === "Blank choice"){
								$('#blankEnab_'+resp[quesItr]['QuesNum']).css("background-color","rgb(0, 0, 255)");
								$('#blankEnab_'+resp[quesItr]['QuesNum']).text("Blank Choice Enabled! Click Again To Enable Single Choice!");
							}else if(resp[quesItr]['EnableBlank'] === "Multiple choice"){
								$('#blankEnab_'+resp[quesItr]['QuesNum']).css("background-color","rgb(138, 160, 95)");
								$('#blankEnab_'+resp[quesItr]['QuesNum']).text("Multiple Choice Enabled! Click Again To Enable Blank Choice!");
							}else if(resp[quesItr]['EnableBlank'] === "Single choice"){
								$('#blankEnab_'+resp[quesItr]['QuesNum']).css("background-color","rgb(111, 217, 222)");
								$('#blankEnab_'+resp[quesItr]['QuesNum']).text("Single Choice Enabled! Click Again To Enable Multiple Choice!");
							}							
						}
						
						optCount[parseInt(resp[quesItr]['QuesNum'])] = parseInt(resp[quesItr]['totalOpts']);					
						
						var optionsHere = (resp[quesItr]['options']+'').split("|");

						for(var optItr = 1; optItr <= optCount[parseInt(resp[quesItr]['QuesNum'])]; optItr++){
							var optsvariant = optionsHere[optItr-1].split(";");
							
							var appStrForNewOpts = '<div style="display:flex; vertical-align:top;" name="optNo_'+optItr+'_'+resp[quesItr]['QuesNum']+'" id="optNo_'+optItr+'_'+resp[quesItr]['QuesNum']+'"><input name="optionNum_'+optItr+'_'+resp[quesItr]['QuesNum']+'" id="optionNum_'+optItr+'_'+resp[quesItr]['QuesNum']+'" type="text" class="form-control" placeholder="Option Code" style="width: 110px" autofocus><input name="optionText_'+optItr+'_'+resp[quesItr]['QuesNum']+'" id="optionText_'+optItr+'_'+resp[quesItr]['QuesNum']+'" type="text" class="form-control" placeholder="Option Text" autofocus><select id="skpQuesSin_'+optItr+'_'+resp[quesItr]['QuesNum']+'" multiple="multiple" name="skpQuesSin_'+optItr+'_'+resp[quesItr]['QuesNum']+'" class="sinSelCh" tabindex="1" style="width:70px">';
	
							for(var currQuesCount = 1; currQuesCount <= quesCount; currQuesCount++){
								appStrForNewOpts = appStrForNewOpts + '<option value="quesSinNo_'+optItr+'_'+resp[quesItr]['QuesNum']+'_'+currQuesCount+'">'+currQuesCount+'</option>';
							}
	
							appStrForNewOpts = appStrForNewOpts + '</select><select id="skpQuesMul_'+optItr+'_'+resp[quesItr]['QuesNum']+'" multiple="multiple" name="skpQuesMul_'+optItr+'_'+resp[quesItr]['QuesNum']+'" class="mulSelCh" tabindex="1" style="width:70px">';
	
							for(var currQuesCount = 1; currQuesCount <= quesCount; currQuesCount++){
								appStrForNewOpts = appStrForNewOpts + '<option value="quesMulNo_'+optItr+'_'+resp[quesItr]['QuesNum']+'_'+currQuesCount+'">'+currQuesCount+'</option>';
							}
	
							appStrForNewOpts = appStrForNewOpts + '</select><button type="button" id="Del12depen_'+optItr+'_'+resp[quesItr]['QuesNum']+'" name="Del12depen_'+optItr+'_'+resp[quesItr]['QuesNum']+'" class="del12dep" style="width: 150px; background-color: cadetblue;">Delta-1-2 Depen</button><button type="button" id="Del12undepen_'+optItr+'_'+resp[quesItr]['QuesNum']+'" name="Del12undepen_'+optItr+'_'+resp[quesItr]['QuesNum']+'" class="del12Undep" style="width: 150px; background-color: cadetblue;">Delta-1-2 UnDepen</button><button type="button" class="deleteMe">X</button><button type="button" class="addNext">+</button></div>';
	
							$('#optList_'+resp[quesItr]['QuesNum']).append(appStrForNewOpts);
	
							$('.mulSelCh').multiselect({
								nonSelectedText: "Skip questions MUL",
								includeSelectAllOption: true
							});
							$('.sinSelCh').multiselect({
								nonSelectedText: "Skip questions SIN",
								includeSelectAllOption: true
							});
							
							//console.log(optsvariant);
							$('#optionNum_'+optItr+'_'+resp[quesItr]['QuesNum']).val(optsvariant[0]);
							$('#optionText_'+optItr+'_'+resp[quesItr]['QuesNum']).val(optsvariant[1]);
							$('#skpQuesSin_'+optItr+'_'+resp[quesItr]['QuesNum']).val(optsvariant[2].substring(1,optsvariant[2].length-1).split(","));
							$('#skpQuesMul_'+optItr+'_'+resp[quesItr]['QuesNum']).val(optsvariant[3].substring(1,optsvariant[3].length-1).split(","));
							
							if(optsvariant[4] == "Y"){
								$('#Del12depen_'+optItr+'_'+resp[quesItr]['QuesNum']).css("background-color", "rgb(0, 0, 255)");
							}else if(optsvariant[4] == "N"){
								$('#Del12depen_'+optItr+'_'+resp[quesItr]['QuesNum']).css("background-color", "rgb(95, 158, 160)");
							}
							
							if(optsvariant[5] == "Y"){
								$('#Del12undepen_'+optItr+'_'+resp[quesItr]['QuesNum']).css("background-color", "rgb(0, 0, 255)");
							}else if(optsvariant[5] == "N"){
								$('#Del12undepen_'+optItr+'_'+resp[quesItr]['QuesNum']).css("background-color", "rgb(95, 158, 160)");
							}
						}
						
						
						$('.mulSelCh').multiselect('rebuild');$('.sinSelCh').multiselect('rebuild');
					}
				}else{
					$("#loadMsg").html("<p class='text-center'>Nothing earlier saved to load!!!</p>");
				}
					
			},			
			error: function (textStatus, errorThrown) {
				console.log(textStatus);
				console.log(errorThrown);
				$("#loadMsg").html("<p class='text-center'>Error Loading!!!</p>");
			},
			beforeSend: function () {
				$("#loadMsg").html("<p class='text-center'><img src='login/images/ajax-loader.gif'></p>");
			}
		});
});

//Start Compiling and create apk
$(document).on("click","#compileApk",function(){
	$.ajax
    ({
        type: "POST",
        dataType : 'json',
        url: 'login/compileForApk.php',
        data: {goMake:"Y"},
        error: function(err) {
			console.log(err)
			if(err.statusText == 'OK'){
				$("#submitMsg").html("<p class='text-center'>Successful Compiled, Now you can download it!!!</p>");
				$('#downloadAPK').css('display', 'block');
			}else{
				$("#submitMsg").html("<p class='text-center'>Error Compiling!!!</p>");
			}
		},
		beforeSend: function () {
			$("#submitMsg").html("<p class='text-center'><img src='login/images/ajax-loader.gif'></p>");
		}
    });
});

//generate json file out of total submitted
$(document).on("click","#saveDataAll",function(){
	var json_data = [];
	for(var currQuesStrike=1;currQuesStrike<=quesCount;currQuesStrike++){
		var file_data = $('#picUpld_'+currQuesStrike).prop("files")[0];
		var prev_file_data = $('#IsUpAllow_'+currQuesStrike).val().split("_");
		if(file_data != null){
			file_data =  "file_"+$('#projectID').val()+"_"+currQuesStrike+'_'+file_data.name;
		}else if(prev_file_data.length > 1){
			file_data = "file_"+$('#projectID').val()+"_"+currQuesStrike+'_'+prev_file_data[3];
		}else{
			file_data = "";
		}
		var blankEnabVar = 'None';

		if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
			blankEnabVar = "Blank choice";
		}else{
			if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(111, 217, 222)"){
				blankEnabVar = "Single choice";
			}else{
				if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(138, 160, 95)"){
					blankEnabVar = "Multiple choice";
				}else{
					blankEnabVar = "";
				}
			}
		}
//console.log("FILE:::::"+json_data)
		var innr_json_data = [];
		for(var ops=1;ops<=optCount[currQuesStrike];ops++){
			var del12depvar = 'N';
			var del12undepvar = 'N';
			if($('#Del12depen_'+ops+'_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
				del12depvar = "Y";
			}
			if($('#Del12undepen_'+ops+'_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
				del12undepvar = "Y";
			}
			var opsNoKey      = 'opsNum_'+ops;
			var opsTextKey    = 'opsText_'+ops;
			var skipsinkey    = 'skipSin_'+ops;
			var skipmulkey    = 'skipMul_'+ops;
			var del12depkey   = 'Del12depen_'+ops;
			var del12undepkey = 'Del12undepen_'+ops;
			
			innr_json_data.push({opsNoKey:$('#optionNum_'+ops+'_'+currQuesStrike).val(), opsTextKey:$('#optionText_'+ops+'_'+currQuesStrike).val(), skipsinkey :$('#skpQuesSin_'+ops+'_'+currQuesStrike).val(), skipmulkey:$('#skpQuesMul_'+ops+'_'+currQuesStrike).val(),del12depkey:del12depvar, del12undepkey:del12undepvar});
			
		}
		
		var rotAftrVar = ($("#rotateAfter_"+currQuesStrike).val() != "")?$("#rotateAfter_"+currQuesStrike).val():0;
		
		var quesTypeStr = $("#quesType_"+currQuesStrike).val();
		if($("#quesType_"+currQuesStrike).val() == "Conjoint" || $("#quesType_"+currQuesStrike).val() == "Max Diff Conjoint"){
			if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclnoneinclrot"){
				quesTypeStr = quesTypeStr+'_inclnoneinclrot_'+rotAftrVar;
			}
			else if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclrot"){
				quesTypeStr = quesTypeStr+'_inclrot_'+rotAftrVar;
			}
			else if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclnone"){
				quesTypeStr = quesTypeStr+'_inclnone_0';
			}
			else{
				quesTypeStr = quesTypeStr+'_dont_0';
			}
		}
		
		json_data.push({'QuesNum': currQuesStrike, 'QuesText': $("#quesText_"+currQuesStrike).val(), 'QuesType': quesTypeStr, 'blanks':blankEnabVar, 'file': file_data, 'totalOpts': optCount[currQuesStrike], 'options':innr_json_data});
	}
	console.log(json_data)
	var json_data_toSend = ({'questions':json_data});
	
	$.ajax
    ({
        type: "POST",
        dataType : 'json',
        url: 'login/saveJson.php',
        data: { data: JSON.stringify(json_data_toSend) },
        error: function(err) {
			console.log(err)
			if(err.statusText == 'OK'){
				$("#submitMsg").html("<p class='text-center'>Successful Submit, Now you can compile it!!!</p>");
				$('#compileApk').css('display', 'block');
			}else{
				$("#submitMsg").html("<p class='text-center'>Error Submitting!!!</p>");				
			}
		},
		beforeSend: function () {
			$("#submitMsg").html("<p class='text-center'><img src='login/images/ajax-loader.gif'></p>");
		}
    });
});

//to save a question into db on save button click
$(document).on("click",".saveButton",function(){
	var currQuesStrike = parseInt((this.id.split("_"))[1]);

	var form_data = new FormData();
	
	var file_data = $('#picUpld_'+currQuesStrike).prop("files")[0];
	if(file_data != null){ 
		$('#IsUpAllow_'+currQuesStrike).val("file_"+$('#projectID').val()+"_"+currQuesStrike+'_'+file_data.name);
		$('#IsUpAllow_'+currQuesStrike).html("Already uploaded -- "+file_data.name);
	}
	form_data.append('projectID', $('#projectID').val());
	form_data.append('QuesNum', currQuesStrike);
    form_data.append('QuesText', $("#quesText_"+currQuesStrike).val());

	var quesTypeStr = $("#quesType_"+currQuesStrike).val();
	var rotAftrVar = ($("#rotateAfter_"+currQuesStrike).val() != "")?$("#rotateAfter_"+currQuesStrike).val():0;
	if($("#quesType_"+currQuesStrike).val() == "Conjoint" || $("#quesType_"+currQuesStrike).val() == "Max Diff Conjoint"){
		if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclnoneinclrot"){
			quesTypeStr = quesTypeStr+'_inclnoneinclrot_'+rotAftrVar;
		}
		else if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclrot"){
			quesTypeStr = quesTypeStr+'_inclrot_'+rotAftrVar;
		}
		else if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclnone"){
			quesTypeStr = quesTypeStr+'_inclnone_0';
		}
		else{
			quesTypeStr = quesTypeStr+'_dont_0';
		}
	}
	var upAll = ($("#IsUpAllow_"+currQuesStrike).val().split("_").length>1)?$("#IsUpAllow_"+currQuesStrike).val().split("_")[3]:$("#IsUpAllow_"+currQuesStrike).val();

	form_data.append('QuesType', quesTypeStr);	
    form_data.append('file', file_data);
	form_data.append('upAllow',upAll);

	if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
		form_data.append('blankEnab',"Blank choice");
	}else{
		if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(111, 217, 222)"){
			form_data.append('blankEnab',"Single choice");
		}else{
			if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(138, 160, 95)"){
				form_data.append('blankEnab',"Multiple choice");
			}else{
				form_data.append('blankEnab',"");
			}
		}
	}
	form_data.append('totalOpts', optCount[currQuesStrike]);
	form_data.append('delQuesEntry','N');
	
	for(var ops=1;ops<=optCount[currQuesStrike];ops++){
		form_data.append('opsNum_'+ops,$('#optionNum_'+ops+'_'+currQuesStrike).val());
		form_data.append('opsText_'+ops,$('#optionText_'+ops+'_'+currQuesStrike).val());
		form_data.append('skipSin_'+ops,'['+$('#skpQuesSin_'+ops+'_'+currQuesStrike).val()+']');
		form_data.append('skipMul_'+ops,'['+$('#skpQuesMul_'+ops+'_'+currQuesStrike).val()+']');
		if($('#Del12depen_'+ops+'_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
			form_data.append('Del12depen_'+ops,"Y");
		}else{
			form_data.append('Del12depen_'+ops,"N");
		}
		if($('#Del12undepen_'+ops+'_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
			form_data.append('Del12undepen_'+ops,"Y");
		}else{
			form_data.append('Del12undepen_'+ops,"N");
		}
	}
	//console.log(form_data)
	$.ajax({
		url: "login/saveIntoDB.php",
		type: "POST",
		cache: false,
		contentType: false,
		processData: false,
		data: form_data,
		success: function(err,txt) {
			console.log(err);
			console.log(txt);
			if(txt == 'success'){
				$("#loadMsg").html("<p class='text-center'>Successfully Saved Question Number "+currQuesStrike+"!</p>");
			}else{
				$("#loadMsg").html("<p class='text-center'>Error Saving Question Number "+currQuesStrike+"!!!</p>");
			}
		},
		beforeSend: function () {
			$("#loadMsg").html("<p class='text-center'><img src='login/images/ajax-loader.gif'></p>");
		}
	});
});

//on delete question click:
//1. Remove this div
//2. Change other questions attributes
//3. Remove its modal
$("#quesList").on("click",".deleteQues",function(){
	if(quesCount > 1){

	   var tobeDelQues = parseInt(((this.parentNode.id).split("_"))[1]);
	   
	   for(var quesItr=(tobeDelQues+1); quesItr<=quesCount; quesItr++){
		   optCount[quesItr-1] = optCount[quesItr];
		   console.log($('#btnAddQues_'+quesItr).text())
		   $('#quesNo_'+quesItr).attr('id','quesNo_'+(quesItr-1)).attr('name','quesNo_'+(quesItr-1));
		   $('#btnAddQues_'+quesItr).text('Input Question No. '+(quesItr-1)).attr('data-target','#addQues_'+(quesItr-1)).attr('id','btnAddQues_'+(quesItr-1)).attr('name','btnAddQues_'+(quesItr-1));
		   
		   $('#addQues_'+quesItr).attr('id','addQues_'+(quesItr-1)).attr('name','addQues_'+(quesItr-1));
		   $('#quesNoInHead_'+quesItr).html('Input Fields For Question No. <strong>'+(quesItr-1)+'</strong>').attr('id','quesNoInHead_'+(quesItr-1)).attr('name','quesNoInHead_'+(quesItr-1));
		   $('#quesText_'+quesItr).attr('id','quesText_'+(quesItr-1)).attr('name','quesText_'+(quesItr-1));
		   $('#quesType_'+quesItr).attr('id','quesType_'+(quesItr-1)).attr('name','quesType_'+(quesItr-1));
		   $('#inclRotNonOf_'+quesItr).attr('id','inclRotNonOf_'+(quesItr-1)).attr('name','inclRotNonOf_'+(quesItr-1));
		   $('#rotateAfter_'+quesItr).attr('id','rotateAfter_'+(quesItr-1)).attr('name','rotateAfter_'+(quesItr-1));
		   $('#picUpld_'+quesItr).attr('id','picUpld_'+(quesItr-1)).attr('name','picUpld_'+(quesItr-1));
		   
		   $('#blankEnab_'+quesItr).attr('id','blankEnab_'+(quesItr-1)).attr('name','blankEnab_'+(quesItr-1));
		   $('#optList_'+quesItr).attr('id','optList_'+(quesItr-1)).attr('name','optList_'+(quesItr-1));
		   $('#save_'+quesItr).attr('id','save_'+(quesItr-1)).attr('name','save_'+(quesItr-1));
		   
		   var file_proj_ques_name = $('#IsUpAllow_'+quesItr).val().split("_");

		   if(file_proj_ques_name.length > 1){
			   $('#IsUpAllow_'+quesItr).attr('id','IsUpAllow_'+(quesItr-1)).attr('name','IsUpAllow_'+(quesItr-1)).attr('value',file_proj_ques_name[0]+'_'+file_proj_ques_name[1]+'_'+(parseInt(file_proj_ques_name[2])-1)+'_'+file_proj_ques_name[3]);
				
				var form_data = new FormData();
				form_data.append('fromFile', file_proj_ques_name[0]+'_'+file_proj_ques_name[1]+'_'+file_proj_ques_name[2]+'_'+file_proj_ques_name[3]);
				form_data.append('toFile', file_proj_ques_name[0]+'_'+file_proj_ques_name[1]+'_'+(parseInt(file_proj_ques_name[2])-1)+'_'+file_proj_ques_name[3]);
				form_data.append('delQuesEntry','RENAMEFILE');
				$.ajax({
						url: "login/saveIntoDB.php",
						type: "POST",
						cache: false,
						contentType: false,
						processData: false,
						data: form_data,
						success: function(result){
							console.log(result);
						}
					});
		   }else{
			   $('#IsUpAllow_'+quesItr).attr('id','IsUpAllow_'+(quesItr-1)).attr('name','IsUpAllow_'+(quesItr-1));
		   }
		   
		   for(var optItr=1; optItr <= optCount[quesItr]; optItr++){
			    $('#optionNum_'+optItr+'_'+quesItr).attr('id','optionNum_'+optItr+'_'+(quesItr-1)).attr('name','optionNum_'+optItr+'_'+(quesItr-1));
				$('#optionText_'+optItr+'_'+quesItr).attr('id','optionText_'+optItr+'_'+(quesItr-1)).attr('name','optionText_'+optItr+'_'+(quesItr-1));
				$('#Del12depen_'+optItr+'_'+quesItr).attr('id','Del12depen_'+optItr+'_'+(quesItr-1)).attr('name','Del12depen_'+optItr+'_'+(quesItr-1));
				$('#Del12undepen_'+optItr+'_'+quesItr).attr('id','Del12undepen_'+optItr+'_'+(quesItr-1)).attr('name','Del12undepen_'+optItr+'_'+(quesItr-1));

				var eS = $("#skpQuesSin_"+optItr+"_"+quesItr);
				var selcValArr = eS.val();

				eS.empty(); // remove old options
				for(var innrqcnt = 1; innrqcnt <= quesCount-1; innrqcnt++){
					var optionApp = '';
					
					if(jQuery.inArray("quesSinNo_"+optItr+"_"+quesItr+"_"+innrqcnt, selcValArr) !== -1){
						optionApp = $('<option></option>').attr("value", "quesSinNo_"+optItr+"_"+(quesItr-1)+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
					}else{
						optionApp = $('<option></option>').attr("value", "quesSinNo_"+optItr+"_"+(quesItr-1)+"_"+innrqcnt).text(innrqcnt);
					}
					eS.append(optionApp);
				}
				$('#skpQuesSin_'+optItr+'_'+quesItr).attr('id','skpQuesSin_'+optItr+'_'+(quesItr-1)).attr('name','skpQuesSin_'+optItr+'_'+(quesItr-1));
				$('.sinSelCh').multiselect('rebuild');
				
				
				var eM = $("#skpQuesMul_"+optItr+"_"+quesItr);
				var selcValArr2 = eM.val();
				
				eM.empty(); // remove old options
				for(var innrqcnt = 1; innrqcnt <= quesCount-1; innrqcnt++){
					var optionApp = '';
					
					if(jQuery.inArray("quesMulNo_"+optItr+"_"+quesItr+"_"+innrqcnt, selcValArr2) !== -1){
						optionApp = $('<option></option>').attr("value", "quesMulNo_"+optItr+"_"+(quesItr-1)+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
					}else{
						optionApp = $('<option></option>').attr("value", "quesMulNo_"+optItr+"_"+(quesItr-1)+"_"+innrqcnt).text(innrqcnt);
					}
					eM.append(optionApp);
				}
				$('#skpQuesMul_'+optItr+'_'+quesItr).attr('id','skpQuesMul_'+optItr+'_'+(quesItr-1)).attr('name','skpQuesMul_'+optItr+'_'+(quesItr-1));
				$('.mulSelCh').multiselect('rebuild');
		   }
	   }
	   
		for(var quesItr=1; quesItr < tobeDelQues; quesItr++){
			for(var optItr=1; optItr <= optCount[quesItr]; optItr++){
				var eS = $("#skpQuesSin_"+optItr+"_"+quesItr);
				var selcValArr = eS.val();

				eS.empty(); // remove old options
				for(var innrqcnt = 1; innrqcnt <= quesCount-1; innrqcnt++){
					var optionApp = '';
					
					if(jQuery.inArray("quesSinNo_"+optItr+"_"+quesItr+"_"+innrqcnt, selcValArr) !== -1){
						//console.log("quesSinNo_"+optItr+"_"+quesItr+"_"+innrqcnt)
						//console.log(selcValArr)
						optionApp = $('<option></option>').attr("value", "quesSinNo_"+optItr+"_"+quesItr+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
					}else{
						optionApp = $('<option></option>').attr("value", "quesSinNo_"+optItr+"_"+quesItr+"_"+innrqcnt).text(innrqcnt);
					}
					eS.append(optionApp);
				}
				$('.sinSelCh').multiselect('rebuild');


				var eM = $("#skpQuesMul_"+optItr+"_"+quesItr);
				var selcValArr2 = eM.val();

				eM.empty(); // remove old options
				for(var innrqcnt = 1; innrqcnt <= quesCount-1; innrqcnt++){
					var optionApp = '';
					
					if(jQuery.inArray("quesMulNo_"+optItr+"_"+quesItr+"_"+innrqcnt, selcValArr2) !== -1){
						optionApp = $('<option></option>').attr("value", "quesMulNo_"+optItr+"_"+quesItr+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
					}else{
						optionApp = $('<option></option>').attr("value", "quesMulNo_"+optItr+"_"+quesItr+"_"+innrqcnt).text(innrqcnt);
					}
					eM.append(optionApp);
				}
				$('.mulSelCh').multiselect('rebuild');
			}
		}
	   
	   $(this).closest("div").remove();
	   $("#addQues_"+tobeDelQues).remove();
		optCount[quesCount] = 0;
	    quesCount = quesCount-1;
		
		$("#loadMsg").html("<p class='text-center'><img src='login/images/ajax-loader.gif'></p>");

		for(var currQuesStrike = 1; currQuesStrike <= quesCount; currQuesStrike++){

			var form_data = new FormData();
			
			var file_data = $('#picUpld_'+currQuesStrike).prop("files")[0];
			if(file_data != null){
				$('#IsUpAllow_'+currQuesStrike).val("file_"+$('#projectID').val()+"_"+currQuesStrike+'_'+file_data.name);
				$('#IsUpAllow_'+currQuesStrike).html("Already uploaded -- "+file_data.name);
			}
			form_data.append('projectID', $('#projectID').val());
			form_data.append('QuesNum', currQuesStrike);
			form_data.append('QuesText', $("#quesText_"+currQuesStrike).val());
			
			var quesTypeStr = $("#quesType_"+currQuesStrike).val();
			var rotAftrVar = ($("#rotateAfter_"+currQuesStrike).val() != "")?$("#rotateAfter_"+currQuesStrike).val():0;
			if($("#quesType_"+currQuesStrike).val() == "Conjoint" || $("#quesType_"+currQuesStrike).val() == "Max Diff Conjoint"){
				if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclnoneinclrot"){
					quesTypeStr = quesTypeStr+'_inclnoneinclrot_'+rotAftrVar;
				}
				else if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclrot"){
					quesTypeStr = quesTypeStr+'_inclrot_'+rotAftrVar;
				}
				else if($("#inclRotNonOf_"+currQuesStrike).val().toLowerCase() == "inclnone"){
					quesTypeStr = quesTypeStr+'_inclnone_0';
				}
				else{
					quesTypeStr = quesTypeStr+'_dont_0';
				}
			}

			var upAll = ($("#IsUpAllow_"+currQuesStrike).val().length>0)?$("#IsUpAllow_"+currQuesStrike).val().split("_")[3]:$("#IsUpAllow_"+currQuesStrike).val();
				
			form_data.append('QuesType', quesTypeStr);	
			form_data.append('file', file_data);
			form_data.append('upAllow',upAll);
			
			if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
				form_data.append('blankEnab',"Blank choice");
			}else{
				if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(111, 217, 222)"){
					form_data.append('blankEnab',"Single choice");
				}else{
					if($('#blankEnab_'+currQuesStrike).css("background-color") == "rgb(138, 160, 95)"){
						form_data.append('blankEnab',"Multiple choice");
					}else{
						form_data.append('blankEnab',"");
					}
				}
			}
			
			form_data.append('totalOpts', optCount[currQuesStrike]);
			form_data.append('delQuesEntry','N');
			
			for(var ops=1;ops<=optCount[currQuesStrike];ops++){
				form_data.append('opsNum_'+ops,$('#optionNum_'+ops+'_'+currQuesStrike).val());
				form_data.append('opsText_'+ops,$('#optionText_'+ops+'_'+currQuesStrike).val());
				form_data.append('skipSin_'+ops,'['+$('#skpQuesSin_'+ops+'_'+currQuesStrike).val()+']');
				form_data.append('skipMul_'+ops,'['+$('#skpQuesMul_'+ops+'_'+currQuesStrike).val()+']');
				if($('#Del12depen_'+ops+'_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
					form_data.append('Del12depen_'+ops,"Y");
				}else{
					form_data.append('Del12depen_'+ops,"N");
				}
				if($('#Del12undepen_'+ops+'_'+currQuesStrike).css("background-color") == "rgb(0, 0, 255)"){
					form_data.append('Del12undepen_'+ops,"Y");
				}else{
					form_data.append('Del12undepen_'+ops,"N");
				}
			}
			
			//console.log(form_data)
			$.ajax({
				url: "login/saveIntoDB.php",
				type: "POST",
				cache: false,
				contentType: false,
				processData: false,
				data: form_data,
				success: function(result){
					console.log(result);
				}
			});
		}
		
		var form_data = new FormData();
		form_data.append('projectID', $('#projectID').val());
		form_data.append('QuesNum', (quesCount+1));
		form_data.append('delQuesEntry','Y');
		$.ajax({
				url: "login/saveIntoDB.php",
				type: "POST",
				cache: false,
				contentType: false,
				processData: false,
				data: form_data,
				success: function(result){
					console.log(result);
				}
			});
		
		$("#loadMsg").html("<p class='text-center'>Successfully Saved For All the questions!</p>");
	   //alert(optCount)
	}else{
		alert("Cant remove all the questions!!!")
	}
});

//on add new question click:
//1. add another div
//2. add another modal
//3. Change other questions attributes
$("#quesList").on("click",".addNextQues",function(){
	quesCount = quesCount + 1;
	var appStrForNewQues = '<div style="display:flex; vertical-align:top; margin-left: 550px" id="quesNo_'+quesCount+'" name="quesNo_'+quesCount+'"><button id="btnAddQues_'+quesCount+'" name="btnAddQues_'+quesCount+'" type="button" class="btn btn-info btn-lg" data-toggle="modal" data-target="#addQues_'+quesCount+'">Input Question No. '+quesCount+'</button><button type="button" class="addNextQues">+</button><button type="button" class="deleteQues">X</button></div>';
	$("#quesList").append(appStrForNewQues);
	
	var appStrForNewModal = '<div id="addQues_'+quesCount+'" name="addQues_'+quesCount+'" class="modal fade" role="dialog"><div class="modal-dialog"><div class="modal-content" style="width: 200%;left: -50%;"><div class="modal-header"><button type="button" class="close" data-dismiss="modal">&times;</button><h4 class="modal-title" id="quesNoInHead_'+quesCount+'" name="quesNoInHead_'+quesCount+'">Input Fields For Question No. <strong>'+quesCount+'</strong></h4></div><div class="modal-body"><input name="quesText_'+quesCount+'" id="quesText_'+quesCount+'" type="text" class="form-control" placeholder="Question Text" style="height: 200px" autofocus><select name="quesType_'+quesCount+'" id="quesType_'+quesCount+'" class="quesType" tabindex="1" style="width: 190px"><option selected disabled>Select Question Type</option><option value="Single Choice" id="SC" name="SC">Single Choice</option><option value="Multiple Choice" id="MC" name="MC">Multiple Choice</option><option value="Plain Text" id="TXT" name="TXT">Plain Text</option><option value="Image based" id="ÌMG" name="IMG">Image based</option><option value="Video based" id="VID" name="VID">Video Based</option><option value="Conjoint" id="CNG" name="CNG">Conjoint</option><option value="Max Diff Conjoint" id="MaxDiffCNG" name="MaxDiffCNG">Max Diff Conjoint</option></select><select type="button" id="inclRotNonOf_'+quesCount+'" name="inclRotNonOf_'+quesCount+'" tabindex="1" class="inclRotNonOf" style="display:none"><option value="InclNoneInclRot">Include Both Rotation and None of These</option><option value="InclRot">Include Rotation only</option><option value="InclNone">Include None of These only</option><option selected value="Dont">Do not Include Anything</option></select><input type="number" name="rotateAfter_'+quesCount+'" id="rotateAfter_'+quesCount+'" class="rotateAfter" style="display:none" placeholder="Select buffer Start Question"></input><input type="file" class="form-control" name="picUpld_'+quesCount+'" id="picUpld_'+quesCount+'" style="display:none"><div id="IsUpAllow_'+quesCount+'" name="IsUpAllow_'+quesCount+'" value="" style="display:none">Already uploaded -- nothing</div><button type="button" id="blankEnab_'+quesCount+'" name="blankEnab_'+quesCount+'" class="blankEnable" style="display:none; background-color: cadetblue;">Nothing Enabled... Click to Enable Blank Fill</button><div id="optList_'+quesCount+'" name="optList_'+quesCount+'"><div><button type="button" class="addNext">Click to Start Adding options</button></div></div></div><div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button><button type="button" class="saveButton" data-dismiss="modal" id="save_'+quesCount+'" name="save_'+quesCount+'">Save</button></div></div></div></div>';
	
	$("#modalList").append(appStrForNewModal);
	
	optCount[quesCount] = 0
	
	for(var qcnt = 1; qcnt <= quesCount; qcnt++){
		for(var ocnt=1; ocnt <= optCount[qcnt]; ocnt++){
			var eS = $("#skpQuesSin_"+ocnt+"_"+qcnt);
			var selcValArr = eS.val();

			eS.empty(); // remove old options
			for(var innrqcnt = 1; innrqcnt <= quesCount; innrqcnt++){
				var optionApp = '';
				
				if(jQuery.inArray("quesSinNo_"+ocnt+"_"+qcnt+"_"+innrqcnt, selcValArr) !== -1){
					optionApp = $('<option></option>').attr("value", "quesSinNo_"+ocnt+"_"+qcnt+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
				}else{
					optionApp = $('<option></option>').attr("value", "quesSinNo_"+ocnt+"_"+qcnt+"_"+innrqcnt).text(innrqcnt);
				}
				eS.append(optionApp);
			}
			$('.sinSelCh').multiselect('rebuild');
		}
	}
	
	for(var qcnt = 1; qcnt <= quesCount; qcnt++){
		for(var ocnt=1; ocnt <= optCount[qcnt]; ocnt++){
			var eM = $("#skpQuesMul_"+ocnt+"_"+qcnt);
			var selcValArr = eM.val();

			eM.empty(); // remove old options
			for(var innrqcnt = 1; innrqcnt <= quesCount; innrqcnt++){
				var optionApp = '';
				
				if(jQuery.inArray("quesMulNo_"+ocnt+"_"+qcnt+"_"+innrqcnt, selcValArr) !== -1){
					optionApp = $('<option></option>').attr("value", "quesMulNo_"+ocnt+"_"+qcnt+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
				}else{
					optionApp = $('<option></option>').attr("value", "quesMulNo_"+ocnt+"_"+qcnt+"_"+innrqcnt).text(innrqcnt);
				}
				eM.append(optionApp);
			}
			$('.mulSelCh').multiselect('rebuild');
		}
	}
	
});

//on delete a option click:
//1. delete this div
$(document).on("click",".deleteMe",function(){
	var currStrArr = (this.parentNode.id).split("_");
	var currQues = parseInt(currStrArr[2]);
	var currOpt = parseInt(currStrArr[1]);
	//console.log(currQues)
	if(optCount[currQues] > 1){
		for(var optItr = currOpt+1; optItr <= optCount[currQues]; optItr++){
			$('#optNo_'+optItr+'_'+currQues).attr('id','optNo_'+(optItr-1)+'_'+currQues).attr('name','optNo_'+(optItr-1)+'_'+currQues);
			$('#optionNum_'+optItr+'_'+currQues).attr('id','optionNum_'+(optItr-1)+'_'+currQues).attr('name','optionNum_'+(optItr-1)+'_'+currQues);
			$('#optionText_'+optItr+'_'+currQues).attr('id','optionText_'+(optItr-1)+'_'+currQues).attr('name','optionText_'+(optItr-1)+'_'+currQues);
			$('#Del12depen_'+optItr+'_'+currQues).attr('id','Del12depen_'+(optItr-1)+'_'+currQues).attr('name','Del12depen_'+(optItr-1)+'_'+currQues);
			$('#Del12undepen_'+optItr+'_'+currQues).attr('id','Del12undepen_'+(optItr-1)+'_'+currQues).attr('name','Del12undepen_'+(optItr-1)+'_'+currQues);
			
			var eS = $("#skpQuesSin_"+optItr+"_"+currQues);
			var selcValArr = eS.val();

			eS.empty(); // remove old options
			for(var innrqcnt = 1; innrqcnt <= quesCount; innrqcnt++){
				var optionApp = '';
				
				if(jQuery.inArray("quesSinNo_"+optItr+"_"+currQues+"_"+innrqcnt, selcValArr) !== -1){
					optionApp = $('<option></option>').attr("value", "quesSinNo_"+(optItr-1)+"_"+currQues+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
				}else{
					optionApp = $('<option></option>').attr("value", "quesSinNo_"+(optItr-1)+"_"+currQues+"_"+innrqcnt).text(innrqcnt);
				}
				eS.append(optionApp);
			}
			$('#skpQuesSin_'+optItr+'_'+currQues).attr('id','skpQuesSin_'+(optItr-1)+'_'+currQues).attr('name','skpQuesSin_'+(optItr-1)+'_'+currQues);
			$('.sinSelCh').multiselect('rebuild');
			
			var eM = $("#skpQuesMul_"+optItr+"_"+currQues);
			var selcValArr2 = eM.val();
			
			eM.empty(); // remove old options
			for(var innrqcnt = 1; innrqcnt <= quesCount; innrqcnt++){
				var optionApp = '';
				
				if(jQuery.inArray("quesMulNo_"+optItr+"_"+currQues+"_"+innrqcnt, selcValArr2) !== -1){
					optionApp = $('<option></option>').attr("value", "quesMulNo_"+(optItr-1)+"_"+currQues+"_"+innrqcnt).text(innrqcnt).prop("selected", true);
				}else{
					optionApp = $('<option></option>').attr("value", "quesMulNo_"+(optItr-1)+"_"+currQues+"_"+innrqcnt).text(innrqcnt);
				}
				eM.append(optionApp);
			}
			$('#skpQuesMul_'+optItr+'_'+currQues).attr('id','skpQuesMul_'+(optItr-1)+'_'+currQues).attr('name','skpQuesMul'+(optItr-1)+'_'+currQues);
			$('.mulSelCh').multiselect('rebuild');

		}
		$('#optNo_'+currOpt+'_'+currQues).remove();
		optCount[currQues] = optCount[currQues]-1;
	}else{
		alert("Cant remove all the options!!!")
	}
});

//on add new option click:
//1. add another div
$(document).on("click",".addNext",function(){
	var currQuesStrArr = (this.parentNode.parentNode.id).split("_");
	var currQues = parseInt(currQuesStrArr[1]);
	//console.log(this.parentNode)
	optCount[currQues] = optCount[currQues]+1;
	var appStrForNewOpts = '<div style="display:flex; vertical-align:top;" name="optNo_'+optCount[currQues]+'_'+currQues+'" id="optNo_'+optCount[currQues]+'_'+currQues+'"><input name="optionNum_'+optCount[currQues]+'_'+currQues+'" id="optionNum_'+optCount[currQues]+'_'+currQues+'" type="text" class="form-control" placeholder="Option Code" style="width: 110px" autofocus><input name="optionText_'+optCount[currQues]+'_'+currQues+'" id="optionText_'+optCount[currQues]+'_'+currQues+'" type="text" class="form-control" placeholder="Option Text" autofocus><select id="skpQuesSin_'+optCount[currQues]+'_'+currQues+'" multiple="multiple" name="skpQuesSin_'+optCount[currQues]+'_'+currQues+'" class="sinSelCh" tabindex="1" style="width:70px">';
	
	for(var currQuesCount = 1; currQuesCount <= quesCount; currQuesCount++){
		appStrForNewOpts = appStrForNewOpts + '<option value="quesSinNo_'+optCount[currQues]+'_'+currQues+'_'+currQuesCount+'">'+currQuesCount+'</option>';
	}
	
	appStrForNewOpts = appStrForNewOpts + '</select><select id="skpQuesMul_'+optCount[currQues]+'_'+currQues+'" multiple="multiple" name="skpQuesMul_'+optCount[currQues]+'_'+currQues+'" class="mulSelCh" tabindex="1" style="width:70px">';
	
	for(var currQuesCount = 1; currQuesCount <= quesCount; currQuesCount++){
		appStrForNewOpts = appStrForNewOpts + '<option value="quesMulNo_'+optCount[currQues]+'_'+currQues+'_'+currQuesCount+'">'+currQuesCount+'</option>';
	}
	
	appStrForNewOpts = appStrForNewOpts + '</select><button type="button" id="Del12depen_'+optCount[currQues]+'_'+currQues+'" name="Del12depen_'+optCount[currQues]+'_'+currQues+'" class="del12dep" style="width: 150px; background-color: cadetblue;">Delta-1-2 Depen</button><button type="button" id="Del12undepen_'+optCount[currQues]+'_'+currQues+'" name="Del12undepen_'+optCount[currQues]+'_'+currQues+'" class="del12Undep" style="width: 150px; background-color: cadetblue;">Delta-1-2 UnDepen</button><button type="button" class="deleteMe">X</button><button type="button" class="addNext">+</button></div>';
	
	$('#optList_'+currQues).append(appStrForNewOpts);
	
	$('.mulSelCh').multiselect({
		nonSelectedText: "Skip questions MUL",
		includeSelectAllOption: true
	});
	$('.sinSelCh').multiselect({
		nonSelectedText: "Skip questions SIN",
		includeSelectAllOption: true
	});
	//console.log($("#optList"))
});

//on blankEnable button click
//1. toggle background color
//2. blue --> blank, rgb(111, 217, 222) --> Single Choice, rgb(0, 0, 255) --> Multiple choice
$(document).on("click",".blankEnable",function(){
	if($("#"+this.id).css("background-color") == "rgb(0, 0, 255)"){
		$("#"+this.id).css("background-color", "rgb(111, 217, 222)");
		$("#"+this.id).text("Single Choice Enabled! Click Again To Enable Multiple Choice!");
	}
	else{
		if($("#"+this.id).css("background-color") == "rgb(111, 217, 222)"){
			$("#"+this.id).css("background-color", "rgb(138, 160, 95)");
			$("#"+this.id).text("Multiple Choice Enabled! Click Again To Enable Blank Choice!");
		}else{
			$("#"+this.id).css("background-color", "rgb(0, 0, 255)");
			$("#"+this.id).text("Blank Choice Enabled! Click Again To Enable Single Choice!");
		}
	}
});

//on del12depen button click
//1. toggle background color
//2. blue --> selected, cadetblue --> not selected
$(document).on("click",".del12dep",function(){
	if($("#"+this.id).css("background-color") == "rgb(0, 0, 255)"){
		$("#"+this.id).css("background-color", "cadetblue")
	}
	else{
		$("#"+this.id).css("background-color", "blue")
	}
});

//on del12Undepen button click
//1. toggle background color
//2. blue --> selected, cadetblue --> not selected
$(document).on("click",".del12Undep",function(){
	if($("#"+this.id).css("background-color") == "rgb(0, 0, 255)"){
		$("#"+this.id).css("background-color", "cadetblue")
	}
	else{
		$("#"+this.id).css("background-color", "blue")
	}
});

//on None of these or rotation allowed change
//1. toggle after rotation number allowed
$(document).on('change', '.inclRotNonOf', function (e) {
	var valueSelected = this.value;
	var currQues = parseInt(((this.id).split("_"))[1]);
	//console.log(currQues)
	if((valueSelected.toLowerCase() == "inclnoneinclrot")||(valueSelected.toLowerCase() == "inclrot" )){
		$('#rotateAfter_'+currQues).css('display', 'block');
	}else{
		$('#rotateAfter_'+currQues).css('display', 'none');
	}
});

//on question type change
//1. toggle question type
//2. in case of image/video/conjoin section, upload image will come up
$(document).on('change', '.quesType', function (e) {
	var valueSelected = this.value;
	var currQues = parseInt(((this.id).split("_"))[1]);
	//console.log(currQues)
	if((valueSelected.toLowerCase() == "image based")||(valueSelected.toLowerCase() == "video based" )||(valueSelected.toLowerCase() == "conjoint" )||(valueSelected.toLowerCase() == "max diff conjoint" )){
		$('#picUpld_'+currQues).css('display', 'block');
		$('#IsUpAllow_'+currQues).css('display', 'block');
	}else{
		$('#picUpld_'+currQues).css('display', 'none');
		$('#IsUpAllow_'+currQues).css('display', 'none');
	}
	
	if(valueSelected.toLowerCase() == "conjoint" || valueSelected.toLowerCase() == "max diff conjoint" ){
		$('#inclRotNonOf_'+currQues).css('display', 'block');
		$('#rotateAfter_'+currQues).css('display', 'block');
	}else{
		$('#inclRotNonOf_'+currQues).css('display', 'none');
		$('#rotateAfter_'+currQues).css('display', 'none');
	}

	if((valueSelected.toLowerCase() == "image based")||(valueSelected.toLowerCase() == "video based" )){
		$('#blankEnab_'+currQues).css('display', 'block');
	}else{
		$('#blankEnab_'+currQues).css('display', 'none');
		$('#blankEnab_'+currQues).css("background-color", "cadetblue");
		$('#blankEnab_'+currQues).text('Enable Blank Fill');
	}
});