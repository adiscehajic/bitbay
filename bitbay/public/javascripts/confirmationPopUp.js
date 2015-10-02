/**
 * Created by Adis Cehajic on 10/2/2015.
 */
$(document).ready(function(){
    $(deleteButton).click(function(){
        $.confirm({
            title: 'You are about to delete ' + deleteName + ' ?',
            content: 'Are you sure?',
            theme: 'supervan',
            confirmButtonClass: 'btn-danger',
            cancelButtonClass: 'btn-info',
            confirmButton: 'Delete',
            cancelButton: 'Cancel',
            backgroundDismiss: false,
            confirm: function(){
                $.ajax({
                    url: deleteUrl,
                    method: 'GET',
                }).success(function(){
                    window.location.replace(redirectUrl);
                }).error(function(response){
                    alert(response.responseText);
                });
            },
            cancel: function(){
                window.location.replace(redirectUrl);
            }
        });
    })
})