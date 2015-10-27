/**
 * Created by Adis Cehajic on 10/27/2015.
 */
$(document).ready(function(){
    $('#booksChange').change(function(){
        var categoryValue = $('#booksChange').val();
        $('#subcategory').empty();
        $.ajax({
            url: subcategoryUrl,
            method: "GET",
            data: "categoryName=" + categoryValue
    }).success(function(response) {
        $("#subcategory").append("<option value=\"0\" selected disabled >Please select subcategory</option>");
        for(var i = 0; i < response.length; i++){
            var subcategory = response[i];
            $("#subcategory").append("<option value='"+ subcategory +"' >" + response[i] + "</option>");
        }
    }).error(function(response) {
        console.log("Error loading subcategories.")
    });
});
});
