/**
 * Created by Adis Cehajic on 11/2/2015.
 */
$(document).ready(function() {
    var comm = commentId;
    var thumbUp = "true";
    var thumbDown = "false";
    $("#like" + comm).click(function(e) {
        e.preventDefault();
        $.ajax({
            type: "post",
            url: saveThumbUp,
            data: "comm=" + comm + "&thumb=" + thumbUp
        }).success(function(response) {
            console.log(response);
            $("#thumbs-up" + comm).html(response[0]);
            $("#thumbs-down" + comm).html(response[1]);
            $("#like" + comm).css('color', 'grey');
            $("#dislike" + comm).css('color', 'blue');
        })
    })
    $("#dislike" + comm).click(function(e) {
        e.preventDefault();
        $.ajax({
            type: "post",
            url: saveThumbUp,
            data: "comm=" + comm + "&thumb=" + thumbDown
        }).success(function(response) {
            $("#thumbs-up" + comm).html(response[0]);
            $("#thumbs-down" + comm).html(response[1]);
            $("#like" + comm).css('color', 'blue');
            $("#dislike" + comm).css('color', 'grey');
        })
    })
});