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