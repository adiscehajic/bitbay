import com.avaje.ebean.Ebean;
import com.cloudinary.Cloudinary;
import models.*;
import org.mindrot.jbcrypt.BCrypt;
import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import play.mvc.Result;
import play.libs.F;
import play.mvc.Http;
import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.filters.csrf.CSRFFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            admin.name = "Admin";

            UserType buyer = new UserType();

            buyer.id = 2;
            buyer.name = "Buyer";

            UserType seller = new UserType();

            seller.id = 3;
            seller.name = "Seller";

            Ebean.save(admin);
            Ebean.save(buyer);
            Ebean.save(seller);

            User userOne = new User();
            userOne.firstName = "Adis";
            userOne.lastName = "Cehajic";
            userOne.email = "adis.cehajic@bitcamp.ba";
            userOne.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userOne.userType = seller;
            userOne.setValidated(true);

            User userThree = new User();
            userThree.firstName = "Dinko";
            userThree.lastName = "Hodzic";
            userThree.email = "dinko.hodzic@bitcamp.ba";
            userThree.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userThree.userType = seller;
            userOne.setValidated(true);

            User userFive = new User();
            userFive.firstName = "Medina";
            userFive.lastName = "Banjic";
            userFive.email = "medina.banjic@bitcamp.ba";
            userFive.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFive.userType = seller;
            userOne.setValidated(true);

            User userTwo = new User();
            userTwo.firstName = "Kerim";
            userTwo.lastName = "Dragolj";
            userTwo.email = "kerim.dragolj@bitcamp.ba";
            userTwo.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userTwo.userType = buyer;
            userOne.setValidated(true);

            User userFour = new User();
            userFour.firstName = "Adnan";
            userFour.lastName = "Lapendic";
            userFour.email = "adnan.lapendic@bitcamp.ba";
            userFour.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFour.userType = buyer;
            userOne.setValidated(true);

            User userSix = new User();
            userFive.firstName = "Senadin";
            userFive.lastName = "Botic";
            userFive.email = "senadin.botic@bitcamp.ba";
            userFive.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFive.userType = buyer;
            userOne.setValidated(true);

            User userSeven = new User();
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
            userSix.save();
            userSeven.save();

            Category categoryOne = new Category("Other");
            Category categoryTwo = new Category("Electronics");
            Category categoryThree = new Category("Fashion");
            Category categoryFour = new Category("Books");
            Category categoryFive = new Category("Motors");
            Category categorySix = new Category("Sports");
            Category categorySeven = new Category("Home");


            categoryOne.save();
            categoryTwo.save();
            categoryThree.save();
            categoryFour.save();
            categoryFive.save();
            categorySix.save();
            categorySeven.save();

            /*
            Electronics
             */
            Product p1 = new Product(userOne, "Samsung S6", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 600.00, 20, "1");
            Product p2 = new Product(userOne, "Samsung S5", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 500.00, 20, "1");
            Product p3 = new Product(userOne, "Samsung S4", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 400.00, 10, "1");
            Product p4 = new Product(userOne, "Samsung S3", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 300.00, 10, "1");
            Product p5 = new Product(userOne, "Samsung S2", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 200.00, 10, "1");
            Product p6 = new Product(userOne, "Samsung S", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 100.00, 10, "1");
            Product p7 = new Product(userOne, "PlayStation 4", "The PlayStation 4 system opens the door to an incredible journey through immersive new gaming worlds and a deeply connected gaming community. Step into living, breathing worlds where you are hero of your epic journey.", "Sony", categoryTwo, 600.00, 10, "1");
            Product p8 = new Product(userOne, "XBOX ONE", "Brand New, Never Used, Never Opened! Microsoft Original!, Region-Free Console!", "Microsoft", categoryTwo, 500.00, 10, "1");
            Product p9 = new Product(userOne, "Dell Latitude E6440 Laptop", "Intel Core i5 laptop from Dell. It is installed with Windows 7 Pro 64bit and updated with Open office software, which is compatible with word, excel, powerpoint etc.. . It has been fully updated and is ready to go!", "Dell", categoryTwo, 300.00, 10, "1");
            Product p10 = new Product(userOne, "LG Electronics 42LF5600 42-inch FULL HD 1080p", "The LG LB5600 Full HD 1080p LED TV shows an incredible picture with nearly double the pixel resolution of standard HD and beautiful, bright, clear images. ", "LG", categoryTwo, 500.00, 10, "1");

            /*
            Fashion
             */
            Product p11 = new Product(userFive, "New Sexy Women Summer Casual Sleeveless Party Evening Cocktail Short Mini Dress", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials (such as the original box or bag). ", "NEW", categoryThree, 600.00, 20, "1");
            Product p12 = new Product(userFive, "Fashion Women Lace Sleeveless Bodycon Cocktail Evening Party Short Mini Dress", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "OWN BRAND", categoryThree, 50.00, 20, "1");
            Product p13 = new Product(userFive, "Sexy Women Summer Casual Sleeveless Party Evening Cocktail Short Mini Dress New", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "Oviesse", categoryThree, 40.00, 10, "1");
            Product p14 = new Product(userFive, "For Women Sexy Sleeveless Party Evening Cocktail Summer Beach Short Mini Dress", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "Unbranded", categoryThree, 30.00, 10, "1");
            Product p15 = new Product(userFive, "2015 Summer Vintange New Women Slim Handed Sequins Evening Party Formal Dresses", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "JC", categoryThree, 40.00, 10, "1");
            Product p16 = new Product(userFive, "haoduoyi Women's Summer Sequins Sexy Casual Prom Evening Party Cocktail Dress", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "H&M", categoryThree, 50.00, 10, "1");
            Product p17 = new Product(userFive, "Women Sexy Summer bodycon Evening Cocktail Party Sleevesless Floral Mini Dress", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "Unbranded", categoryThree, 70.00, 10, "1");
            Product p18 = new Product(userFive, "Men's Slim Stylish Trench Coat Winter Long Jacket Double Breasted Overcoat New", "New without tags: A brand-new, unused, and unworn item (including handmade items) that is not in original packaging or may be missing original packaging materials", "Springfield", categoryThree, 60.00, 10, "1");
            Product p19 = new Product(userFive, "Hot Men's Warm Jackets Parka Outerwear Fur lined Winter thicken Long Coat Hooded", "A brand-new, unused, and unworn item (including handmade items) in the original packaging (such as the original box or bag) and/or with the original tags attached. ", "JC", categoryThree, 100.00, 10, "1");
            Product p20 = new Product(userFive, "2015 Hot Men's Driving Aviator Sunglasses Mirror Goggles Eyewear Glasses", "A brand-new, unused, and unworn item (including handmade items) in the original packaging (such as the original box or bag) and/or with the original tags attached. ", "Brothers", categoryThree, 100.00, 10, "1");

            /*
            Motors
             */
            Product p21 = new Product(userThree, "2006 BMW K", "With enough raw power to shock even the most seasoned adrenaline junky, the K 1200 S hurls you from a dead stop to sixty miles per hour in just 2.8 seconds.", "BMW", categoryFive, 6000.00, 1, "1");
            Product p22 = new Product(userThree, "2008 Ducati Superbike", "All %100 carbon fiber, tank is not installed.", "Ducati", categoryFive, 5500.00, 1, "1");
            Product p23 = new Product(userThree, "2008 HARLEY DAVIDSON ELECTRA GLIDE ULTRA CLASSIC", "We just got this bike in and she is priced to move!!  All of our bikes are priced well back of the competition. Our service team will go over the bike with our 24 point inspection process so when you get her she will be ready to ride!!", "Harley Davidson", categoryFive, 14000.00, 1, "1");
            Product p24 = new Product(userThree, "2004 Suzuki GSX-R", "Has just over 13,000 miles. I have proof it had 5000 on it when it was bought at 6000 miles a few years back. Runs great. Has good breaks and tires.", "Suzuki", categoryFive, 3000.00, 1, "1");
            Product p25 = new Product(userThree, "2003 Honda CBR", "Up for auction is my 2003 Honda 954 with 4996mi.  This bike is very clean and well taken care of.", "Honda", categoryFive, 6000.00, 1, "1");
            Product p26 = new Product(userThree, "2015 Ford Taurus", "Such a beautiful car, it is limited with Front wheel drive, navigation, back up camera and a clear title.", "Ford", categoryFive, 16000.00, 1, "1");
            Product p27 = new Product(userThree, "2010 Mercedes-Benz S-Class", "The vehicle is in excellent mechanical and cosmetic condition.  It runs perfectly and the body is in excellent condition (no parking lot door dings, no curb rash on wheels).  No accidents - the Carfax and Autocheck are clean.", "Mercedes", categoryFive, 33900.00, 1, "1");
            Product p28 = new Product(userThree, "2013 Chevrolet Camaro 2dr Coupe", "The vehicle is in excellent mechanical and cosmetic condition.", "Chevrolet", categoryFive, 36000.00, 1, "1");
            Product p29 = new Product(userThree, "Metallic Silver 4pcs Set #510 15 Inches ", "Build from high quality ABS and patented metal retention clip design. This hubcaps offer the best wheel gripping action and superior resistance than any other hubcap on the market.", "Hub Cup", categoryFive, 100.00, 1, "4");
            Product p30 = new Product(userThree, "Set of 4 Ford 15\" Chrome Wheel Hub Caps", "A Complete New Set of 4 Wheel Skins & Shipping", "Coast to Coast", categoryFive, 50.00, 4, "1");

            /*
            Sports
             */
            Product p31 = new Product(userThree, "ADIDAS BRAZUCA FIFA", "A brand-new, unused, unopened, undamaged item in its original packaging", "Adidas", categorySix, 60.00, 20, "1");
            Product p32 = new Product(userThree, "ADIDAS 2012-13 REAL MADRID HOME SOCCER JERSEY EXTRA LARGE", "A brand-new, unused, unopened, undamaged item in its original packaging", "Adidas", categorySix, 50.00, 20, "1");
            Product p33 = new Product(userThree, "Manchester United Football Club Official Soccer Gift Mens Retro Track Jacket", "A brand-new, unused, unopened, undamaged item in its original packaging", "Manchester United FC", categorySix, 40.00, 10, "1");
            Product p34 = new Product(userThree, "NFL RBK St Louis Rams #55 Closeout Football Jersey", "A brand-new, unused, unopened, undamaged item in its original packaging", "Rams", categorySix, 30.00, 10, "1");
            Product p35 = new Product(userThree, "WILSON Duke Micro Mini American Football Ball ", "A brand-new, unused, unopened, undamaged item in its original packaging", "Wilson", categorySix, 20.00, 10, "1");
            Product p36 = new Product(userThree, "Teman Hybrid Bike Racing Bike Road Bike Bicycles bicycle- Shimano 21 Speed", "A brand-new, unused, unopened, undamaged item in its original packaging", "Shimano", categorySix, 300.00, 10, "1");
            Product p37 = new Product(userThree, "KAYAK ROD KAYAK FISHING ROD & REEL", "A brand-new, unused, unopened, undamaged item in its original packaging", "INSTANT FISHERMAN", categorySix, 200.00, 10, "1");
            Product p38 = new Product(userFive, "Black NEW Outdoor Camping Hiking Trekking Travel bag Military Tactical Rucksack", "A brand-new, unused, unopened, undamaged item in its original packaging.", "Unbranded", categorySix, 40.00, 10, "1");
            Product p39 = new Product(userFive, "TaylorMade SLDR #3 15* Fairway Wood-Speeder 77G Stiff Flex LEFT HANDED", "LEFT HANDED USED LEFT HANDED TaylorMade Adjustable SLDR #3 15 Degree LEFT Handed Fairway Wood\n", "TaylotMade", categorySix, 50.00, 10, "1");
            Product p40 = new Product(userFive, "3000LM CREE XM-L T6 LED Rechargeable Flashlight Torch w/ 18650 Battery & Charger", "A brand-new, unused, unopened, undamaged item in its original packaging.", "Ultrafire", categorySix, 10.00, 10, "1");

            /*
            Home
             */
            Product p41 = new Product(userOne, "Suncast GS9000 7 x 7 Large Outdoor Vertical Shed Storage Building 200 Cubic Ft.", "Extreme Suncast Storage Shed GS9000 7 x 7 - Extra Large. Today people are spending a larger amount of time outdoors on the deck or patio. Relaxing. Entertaining. Pool side.", "Suncast", categorySeven, 600.00, 20, "1");
            Product p42 = new Product(userOne, "Tripod Impulse Sprinkler Pulsating Telescopic Watering Lawn Yard and Garden", "A brand-new, unused, unopened, undamaged item in its original packaging", "HowPlumb", categorySeven, 500.00, 20, "1");
            Product p43 = new Product(userOne, "ADEL LS9 Biometric Fingerprint Door Lock Electronic Keyless Password Door Lock", "A brand-new, unused, unopened, undamaged item in its original packaging", "Unbranded", categorySeven, 20.00, 10, "1");
            Product p44 = new Product(userFive, "Outsunny 12pc Outdoor Patio Rattan Wicker Sofa Chaise Lounge Furniture Set", "A brand-new, unused, unopened, undamaged item in its original packaging", "Unbranded", categorySeven, 300.00, 10, "1");
            Product p45 = new Product(userFive, "Phone APP Alarm System Remote Control Wireless Wired GSM SMS Home House Fire", "A brand-new, unused, unopened, undamaged item in its original packaging", "Unbranded", categorySeven, 200.00, 10, "1");
            Product p46 = new Product(userFive, "Double Hammock With Space Saving Steel Stand Includes Portable Carrying Case", "A brand-new, unused, unopened, undamaged item in its original packaging", "Unbranded", categorySeven, 100.00, 10, "1");
            Product p47 = new Product(userFive, "New 10/50/100pcs Wholesale DIY Accessories Wood Baby Kid's Penguin Sewing Craft", "A brand-new, unused, unopened, undamaged item in its original packaging", "Handmade", categorySeven, 8.00, 10, "1");
            Product p48 = new Product(userFive, "Hunter Hepa Allergen Removing Air Purifier- Whisper-Quiet Fan Tech", "An item that has been professionally restored to working order by a manufacturer or manufacturer-approved vendor. ", "Hunter", categorySeven, 100.00, 10, "1");
            Product p49 = new Product(userFive, "Blue Cotton 5 Assorted Pre Cut Charm Quilt Squares Fabric Craft 10", "A brand-new, unused, unopened, undamaged item in its original packaging", "Handmade", categorySeven, 6.00, 10, "1");
            Product p50 = new Product(userFive, "100Pcs 11MM DIY 2 Holes Round Resin Buttons Scrapbooking Sewing Craft 20 Colors", "A brand-new, unused, unopened, undamaged item in its original packaging", "Unbranded", categorySeven, 1.00, 10, "1");

            /*
            Books
             */

            Product p51 = new Product(userOne, "Humans of New York: Stories", "“There's no judgment, just observation and in many cases reverence, making for an inspiring reading and visual experience.” ", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Product p52 = new Product(userOne, "Lost", "Like everyone reading the newspapers these days, 10-year-old Barney Roberts knows the killer will strike again soon. ", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Product p53 = new Product(userOne, "Whirligig", "A terrible accident ends one life, but is just the beginning for another. . . .", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Product p54 = new Product(userOne, "Zero the Hero", "Zero. Zip. Zilch. Nada. That's what all the other numbers think of Zero. He doesn't add anything in addition. ", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Product p55 = new Product(userOne, "Memories of Babi", "Piri is a city girl, but every year she goes to visit her grandmother Babi on her farm in the Ukrainian village of Komjaty.", "St. Martin's Press", categoryFour, 20.00, 10, "1");

            p1.save();
            p2.save();
            p3.save();
            p4.save();
            p5.save();
            p6.save();
            p7.save();
            p8.save();
            p9.save();
            p10.save();
            p11.save();
            p12.save();
            p13.save();
            p14.save();
            p15.save();
            p16.save();
            p17.save();
            p18.save();
            p19.save();
            p20.save();
            p21.save();
            p22.save();
            p23.save();
            p24.save();
            p25.save();
            p26.save();
            p27.save();
            p28.save();
            p29.save();
            p30.save();
            p31.save();
            p32.save();
            p33.save();
            p34.save();
            p35.save();
            p36.save();
            p37.save();
            p38.save();
            p39.save();
            p40.save();
            p41.save();
            p42.save();
            p43.save();
            p44.save();
            p45.save();
            p46.save();
            p47.save();
            p48.save();
            p49.save();
            p50.save();
            p51.save();
            p52.save();
            p53.save();
            p54.save();
            p55.save();


            List<User> buyers = new ArrayList<>();
            buyers.add(userTwo);
            buyers.add(userFour);
            buyers.add(userSix);

            Random num = new Random();

            for(int i = 1; i < 1000; i++){
                new Rating(buyers.get(num.nextInt(3)), Product.getProductById((num.nextInt(50) + 1)), (num.nextInt(5) + 1)).save();
            }

        }
    }

    @Override
    public <T extends EssentialFilter> Class<T>[] filters() {
        Class[] filters = {CSRFFilter.class};
        return filters;
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
