function validateLength() {
    var pass = document.getElementsByName("password")[0].value;
    var reg = /^[a-zA-Z0-9]{8,}$/;

    if(reg.test(pass) == false) {
       //alert("Password has to have at least 8 characters");
        return false;
    }
    return true;
}

function validateMatching() {
    var pass = document.getElementsByName("password")[0].value;
    var confPass = document.getElementsByName("confirmPassword")[0].value;

    if(pass != confPass) {
       //alert("Passwords doesnt match");
        return false;
    }
    return true;
}


function validateEmail() {
        var reg = /^([A-Za-z0-9_\-\.]){1,}\@([A-Za-z0-9_\-\.]){1,}\.([A-Za-z]{2,4})$/;
        var address = document.getElementsByName("email")[0].value;


        if (reg.test(address) == false) {
            return false;
        }
    return true;
}

function validateRegistration() {
    var name = document.getElementsByName("first-name")[0].value;
    var surname = document.getElementsByName("last-name")[0].value;
    var button = document.getElementsByName("register")[0];
    var user_type = document.getElementsByName("type")[0].value;

    if(name.length == 0) {
        button.disabled = true;
        return;
    } else if(surname.length == 0) {
        button.disabled = true;
        return;
    } else if(validateEmail() == false) {
        button.disabled = true;
        return;
    } else if(validateLength() == false || validateMatching() == false) {
        button.disabled = true;
        return;
    } else if(user_type == 0) {
        button.disabled = true;
        return;
    }
    button.disabled = false;
}

function checkName() {
    var name = document.getElementsByName("first-name")[0].value;
    var inpName = document.getElementsByName("first-name")[0];

    if(name.length == 0) {
        inpName.style.borderColor = "red";
        return;
    }
    inpName.style.borderColor = "lightgray";
}

function checkSurname() {
    var surname = document.getElementsByName("last-name")[0].value;
    var inpSurname = document.getElementsByName("last-name")[0];

    if(surname.length == 0) {
        inpSurname.style.borderColor = "red";
        return;
    }
    inpSurname.style.borderColor = "lightgray";
}

function checkEmail() {
    var inpEmail = document.getElementsByName("email")[0];

    if(validateEmail() == false) {
        inpEmail.style.borderColor = "red";
        return;
    }
    inpEmail.style.borderColor = "lightgray";
}

function checkPassword() {
    var inpPass = document.getElementsByName("password")[0];

    if(validateLength() == false) {
        inpPass.style.borderColor = "red";
        return;
    }
    inpPass.style.borderColor = "lightgray";
}

function checkMatching() {
    var inpConfPass = document.getElementsByName("confirmPassword")[0];

    if(validateMatching() == false) {
        inpConfPass.style.borderColor = "red";
        return;
    }
    inpConfPass.style.borderColor = "lightgray";
}

function isAlphaKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode;
    if ((charCode==231 || charCode==199) || (charCode==241 || charCode==209) ||(charCode==8 || charCode==32) || ( (charCode >= 65 && charCode <= 90) || (charCode >= 97 && charCode <= 122) ) ) {
        return true;
    }
    else {
        return false;
    }
}

function editUser() {
    var name = document.getElementsByName("firstName")[0].value;
    var surname = document.getElementsByName("lastName")[0].value;
    var save = document.getElementsByName("save")[0];

    save.disabled = true;

    if(name.length < 1 || surname.length < 1) {
        save.disabled = true;
        return;
    }

    save.disabled = false;

}