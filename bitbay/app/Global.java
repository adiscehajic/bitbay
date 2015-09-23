import com.avaje.ebean.Ebean;
import com.cloudinary.Cloudinary;
import models.Category;
import models.Image;
import models.User;
import models.UserType;
import org.mindrot.jbcrypt.BCrypt;
import play.Application;
import play.GlobalSettings;
import play.mvc.Result;
import play.libs.F;
import play.mvc.Http;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;

import static play.mvc.Results.badRequest;
import static play.mvc.Results.notFound;

/**
 * Created by Adis Cehajic on 9/22/2015.
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application application) {
        super.onStart(application);

        Image.cloudinary = new Cloudinary("cloudinary://"+ Play.application().configuration().getString("cloudinary.string"));

        if (Ebean.find(UserType.class).findRowCount() == 0) {

            UserType admin = new UserType();

            admin.id = 1;
            admin.name = "admin";

            UserType buyer = new UserType();

            buyer.id = 2;
            buyer.name = "buyer";

            UserType seller = new UserType();

            seller.id = 3;
            seller.name = "seller";

            Ebean.save(admin);
            Ebean.save(buyer);
            Ebean.save(seller);

            User userOne = new User();
            userOne.firstName = "Adis";
            userOne.lastName = "Cehajic";
            userOne.email = "adis.cehajic@bitcamp.ba";
            userOne.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userOne.userType = seller;

            User userTwo = new User();
            userTwo.firstName = "Kerim";
            userTwo.lastName = "Dragolj";
            userTwo.email = "kerim.dragolj@bitcamp.ba";
            userTwo.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userTwo.userType = buyer;

            User userThree = new User();
            userThree.firstName = "Dinko";
            userThree.lastName = "Hodzic";
            userThree.email = "dinko.hodzic@bitcamp.ba";
            userThree.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userThree.userType = seller;

            User userFour = new User();
            userFour.firstName = "Adnan";
            userFour.lastName = "Lapendic";
            userFour.email = "adnan.lapendic@bitcamp.ba";
            userFour.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFour.userType = buyer;

            User userFive = new User();
            userFive.firstName = "Narena";
            userFive.lastName = "Ibrisimovic";
            userFive.email = "narena.ibrisimovic@bitcamp.ba";
            userFive.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFive.userType = admin;

            userOne.save();
            userTwo.save();
            userThree.save();
            userFour.save();
            userFive.save();

            Category categoryOne = new Category("Other");
            Category categoryTwo = new Category("Electronics");
            Category categoryThree = new Category("Fashion");

            categoryOne.save();
            categoryTwo.save();
            categoryThree.save();
        }
    }

    @Override
    public F.Promise<Result> onHandlerNotFound(Http.RequestHeader requestHeader) {
        return F.Promise.<Result>pure(notFound(views.html.notFound.render()));
    }

    @Override
    public F.Promise<Result> onBadRequest(RequestHeader request, String error) {
        return F.Promise.<Result>pure(badRequest(views.html.notFound.render()));
    }
}
