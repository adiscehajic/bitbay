/**
 * Created by Kerim on 10.9.2015.
 */

function validateProduct() {
    var category = document.getElementsByName("category")[0];
    var name = document.getElementsByName("name")[0];
    var price = document.getElementsByName("price")[0];
    var quantity = document.getElementsByName("quantity")[0];
    var button = document.getElementsByName("submitButton")[0];


    if(button != null){
        button.disabled = true;

        if(category == 0) {
            button.disabled = true;
            return;
        } else if(name.length == 0) {
            button.disabled = true;
            return;
        } else if(price <= 0) {
            button.disabled = true;
            return;
        } else if(quantity <= 0 || quantity == null) {
            button.disabled = true;
            return;
        }
        button.disabled = false;

        $('#booksChange').change(function(){
            var thisValue = $(this).val();
            if (thisValue == "Books"){
                $("label[for='manufacturer']").text("Publisher:*");
            } else {
                $("label[for='manufacturer']").text("Manufacturer:*");
            }
        });
    }
}

function validateName() {
    var name = document.getElementsByName("name")[0].value;
    var inpName = document.getElementsByName("name")[0];

    if(name.length == 0) {
        inpName.style.borderColor = "red";
        return;
    }
    inpName.style.borderColor = "lightgray";
}

function validatePrice() {
    var price = document.getElementsByName("price")[0].value;
    var inpPrice = document.getElementsByName("price")[0];

    if(price <= 0 || price == null) {
        inpPrice.style.borderColor = "red";
        return;
    }
    inpPrice.style.borderColor = "lightgray";
}

function validateQuantity() {
    var quantity = document.getElementsByName("quantity")[0].value;
    var inpQuantity = document.getElementsByName("quantity")[0];

    if(quantity <= 0 || quantity == null) {
        inpQuantity.style.borderColor = "red";
        return;
    }
    inpQuantity.style.borderColor = "lightgray";
}

function validateCategory(){
    var category = document.getElementsByName("category")[0].value;
    var selCategory = document.getElementsByName("category")[0];

    if(category == 0) {
        selCategory.style.borderColor = "red";
        return;
    }
    selCategory.style.borderColor = "lightgray";
}

function quantityValidator() {

    var elements = document.getElementsByName("quantity");
    var values = document.getElementsByName("quantity");


    for(var i = 0; i < values.length; i++) {
        var value = values.item(i).value;
        var element = elements.item(i);
        var min = parseInt(element.getAttribute("min")) || 0;
        var max = parseInt(element.getAttribute("max")) || 0;
        if (value < min) {
            element.value = min;
        } else if (value > max) {
            element.value = max;
        }
    }
}

function validateEditing() {
    var name = document.getElementsByName("name")[0].value;
    var price = document.getElementsByName("price")[0].value;
    var quantity = document.getElementsByName("quantity")[0].value;
    var sellingType = document.getElementsByName("selling-type")[0].value;
    var button = document.getElementsByName("save")[0];

    button.disabled = true;

    if(name.length == 0) {
        button.disabled = true;
        return;
    } else if(price <= 0) {
        button.disabled = true;
        return;
    } else if(quantity <= 0 || quantity == null) {
        button.disabled = true;
        return;
    }
    button.disabled = false;
}

function validateUploadedFile() {
    var fileName = document.getElementById("uploadImage").value
    if (fileName == "") {
        alert("Browse to upload a valid File with png extension");
        return false;
    }
    else if (fileName.split(".")[1].toUpperCase() == "PNG")
        return true;
    else {
        alert("File with " + fileName.split(".")[1] + " is invalid. Upload a validfile with png extensions");
        return false;
    }
    return true;
}

