/**
 * Created by Adis Cehajic on 9/30/2015.
 */
$(document).ready(function(){

    $('form').submit(function(e, options){
        //if options is not defined define it as a empty object
        var options = options || {};
        //if allow is true let the event propagate to the server
        if(options.allow == true){
            return;
        }
        //prevent submiting the form
        e.preventDefault();
        //clean all error messages
        $("[data-error-for]").html("");
        //get a refference to the current form
        $form = $(this);
        /*
         * This will setup a AJAX call, the params are
         * url: where to go
         * method: what kind of request
         * data: the data to add to the request
         */
        $.ajax({
            url: urlPost,
            method: "post",
            data: $(this).serialize()
        }).success(function(response){
            //if the validation did not return an error, triger the form submit with options.allow = true
            $form.trigger("submit", {allow: true});
        }).error(function(response){
            //if we got a bad request we take the JSON object from the response
            var errors = response.responseJSON;
            //get an array of all the keys in the error object
            var keys = Object.keys(errors);
            //itterate over the keys
            for(var i = 0; i < keys.length; i++){
                //get the error messages for the current key (which represents one input field)
                var errorMessages = errors[keys[i]];
                //a string of all the errors for the current input
                var allErrors = "";
                for(var j = 0; j < errorMessages.length; j++){
                    allErrors += errorMessages[j];
                }
                //set the helper span message to the content of the error messages
                $('[data-error-for="'+keys[i]+'"]').html(allErrors);
            }
        });
    });

});