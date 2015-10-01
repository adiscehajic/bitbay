/**
 * Created by Adis Cehajic on 9/30/2015.
 */
$(document).ready(function(){
    $("#submit-button").prop("disabled", true);

    $('.form-control').blur(function(){
        $field = $(this);
        $('form [data-error-for="'+$field.attr("name")+'"]').html("");

        $form = $("form");

        $.ajax({
            url: urlPost,
            method: "post",
            data: $('form').serialize()
        }).success(function(response){
            $("#submit-button").prop("disabled", false);
            $("form [data-error-for]").html("");

        }).error(function(response){
            $("#submit-button").prop("disabled", true);
            var errors = response.responseJSON;

            console.log(errors);
            var keys = Object.keys(errors);

            var errorMessages = errors[$field.attr("name")];
            var allErrors = "";
            for(var j = 0; j < errorMessages.length; j++){
                allErrors += errorMessages[j];
            }

            $('form [data-error-for="'+$field.attr("name")+'"]').html(allErrors);
        });
    });
});