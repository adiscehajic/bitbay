/**
 * Created by Adis Cehajic on 10/16/2015.
 */
var hours = Auction.getAuctionEndingTime(product).substring(0, 2);
var minutes = Auction.getAuctionEndingTime(product).substring(3, 5);
var seconds = Auction.getAuctionEndingTime(product).substring(6, 8);

$('#getting-started').countdown("" + Auction.getAuctionEndingDate(product) + "", function(event) {

    var countHours = event.strftime('%H');
    var countMinutes = event.strftime('%M');
    var countSeconds = event.strftime('%S');

    var resultHours = +countHours + +hours;
    var resultMinutes = +countMinutes + +minutes;

    if (event.strftime('%w') === "00") {
        $(this).html(event.strftime('%dd ' + resultHours + 'h ' + resultMinutes + 'm ' + countSeconds + "s"));
    } else if ((event.strftime('%w') && event.strftime('%d')) === "00") {
        $(this).html(event.strftime(resultHours + 'h ' + resultMinutes + 'm ' + countSeconds + "s"));
    } else {
        $(this).html(event.strftime('%ww %dd ' + resultHours + 'h ' + resultMinutes + 'm ' + countSeconds + "s"));
    }
});

