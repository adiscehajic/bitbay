/**
 * Created by adnan.lapendic on 14.10.2015..
 */

$(document).ready(function() {

    var remember = $.cookie('remember');
    if (remember == 'true')	{
        var email = $.cookie('email');
        var password = $.cookie('password');
        var email1 = CryptoJS.AES.decrypt(email, "email");
        var password1 = CryptoJS.AES.decrypt(password, "password");
        var password2 = password1.toString(CryptoJS.enc.Utf8);
        var email2 = email1.toString(CryptoJS.enc.Utf8);

        $('#email').val(email2);
        $('#password').val(password2);
        $('#login-check').attr('checked',true);
    }
    $("#login-form").submit(function() {
        if ($('#login-check').is(':checked')) {
            var email = $('#email').val();
            var encryptedemail = CryptoJS.AES.encrypt(email, "email");
            var password = $('#password').val();
            var encryptedpass = CryptoJS.AES.encrypt(password, "password");

            $.cookie('email', encryptedemail, { expires: 14 });
            $.cookie('password', encryptedpass, { expires: 14 });
            $.cookie('remember', true, { expires: 14 });
        }else{
            $.cookie('email', null);
            $.cookie('password', null);
            $.cookie('remember', null);
        }

    });
});
