/**
 * Created by Adis Cehajic on 10/27/2015.
 */
$(document).ready(function(){
    $("#starting-price").prop("disabled", true);
    $("#buy-it-now-price").prop("disabled", true);
    $("#auction-duration").prop("disabled", true);
    $("#price").prop("disabled", true);
    $("#quantity").prop("disabled", true);
    $("#cancelation-duration").prop("disabled", true);

    console.log($("#selling-type").val());

    $("#selling-type").on('change', function() {
        console.log($("#selling-type").val());
        if($("#selling-type").val() == "1") {
            $("#price").prop("disabled", false);
            $("#quantity").prop("disabled", false);
            $("#cancelation-duration").prop("disabled", false);
            $("#starting-price").prop("disabled", true);
            $("#buy-it-now-price").prop("disabled", true);
            $("#auction-duration").prop("disabled", true);
        }
        if ($("#selling-type").val() == "2"){
            $("#starting-price").prop("disabled", false);
            $("#buy-it-now-price").prop("disabled", false);
            $("#auction-duration").prop("disabled", false);
            $("#price").prop("disabled", true);
            $("#quantity").prop("disabled", true);
            $("#cancelation-duration").prop("disabled", true);
        }
    });
});
