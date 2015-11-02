/**
 * Created by Adis Cehajic on 11/2/2015.
 */
$('#getting-started').countdown(countTime, function(event) {

    var countHours = event.strftime('%H');
    var countMinutes = event.strftime('%M');
    var countSeconds = event.strftime('%S');

    if ((event.strftime('%w') === "00" && event.strftime('%d')) === "00" && countHours === "00" && countMinutes === "00") {
        $(this).html(event.strftime(countSeconds + "s"));
    } else if ((event.strftime('%w') === "00" && event.strftime('%d')) === "00" && countHours === "00") {
        $(this).html(event.strftime(countMinutes + 'm ' + countSeconds + "s"));
    } else if ((event.strftime('%w') === "00" && event.strftime('%d')) === "00") {
        $(this).html(event.strftime(countHours + 'h ' + countMinutes + 'm ' + countSeconds + "s"));
    } else if (event.strftime('%w') === "00") {
        $(this).html(event.strftime('%dd ' + countHours + 'h ' + countMinutes + 'm ' + countSeconds + "s"));
    } else {
        $(this).html(event.strftime('%ww %dd ' + countHours + 'h ' + countMinutes + 'm ' + countSeconds + "s"));
    }
});
