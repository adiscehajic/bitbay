/**
 * Created by Kerim on 10.9.2015.
 */

function validateProduct() {
    var category = document.getElementsByName("category")[0].value;
    var name = document.getElementsByName("name")[0].value;
    var price = document.getElementsByName("price")[0].value;
    var quantity = document.getElementsByName("quantity")[0].value;
    var button = document.getElementsByName("submitButton")[0];

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