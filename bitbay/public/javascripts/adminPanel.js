/**
 * Created by Kerim on 21.9.2015.
 */


function validateCategory() {
    var category = document.getElementsByName("categoryName")[0].value;
    var inpCategory = document.getElementsByName("categoryName")[0];
    var save = document.getElementsByName("save")[0];

    save.disabled = true;

    if(category.length < 1) {
        inpCategory.style.borderColor = "red";
        save.disabled = true;
        return;
    }
    inpCategory.style.borderColor = "lightgray";
    save.disabled = false;
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