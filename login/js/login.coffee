$(document).ready ->
  $("#submit").click ->
    username = $("#myusername").val()
    password = $("#mypassword").val()
	projID   = $("#projID").val()
    if (username is "") or (password is "")
      $("#message").html "<div class=\"alert alert-danger alert-dismissable\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>Please enter a username and a password</div>"
    else if (projID is "")
      $("#message").html "<div class=\"alert alert-danger alert-dismissable\"><button type=\"button\" class=\"close\" data-dismiss=\"alert\" aria-hidden=\"true\">&times;</button>Please enter a Project ID</div>"
	else  
      $.ajax
        type: "POST"
        url: "checklogin.php"
        data: "myusername=" + username + "&mypassword=" + password + "&projID=" + projID
        success: (html) ->
          if html is "true"
            window.location = "index.php"
          else
            $("#message").html html

        beforeSend: ->
          $("#message").html "<p class='text-center'><img src='images/ajax-loader.gif'></p>"

    false

