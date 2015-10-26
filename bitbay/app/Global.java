
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

        AuctionHandler.handleAuctions();

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
            userThree.setValidated(true);

            User userFive = new User();
            userFive.firstName = "Medina";
            userFive.lastName = "Banjic";
            userFive.email = "medina.banjic@bitcamp.ba";
            userFive.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFive.userType = seller;
            userFive.setValidated(true);

            User userTwo = new User();
            userTwo.firstName = "Kerim";
            userTwo.lastName = "Dragolj";
            userTwo.email = "kerim.dragolj@bitcamp.ba";
            userTwo.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userTwo.userType = buyer;
            userTwo.setValidated(true);

            User userFour = new User();
            userFour.firstName = "Adnan";
            userFour.lastName = "Lapendic";
            userFour.email = "adnan.lapendic@bitcamp.ba";
            userFour.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userFour.userType = buyer;
            userFour.setValidated(true);

            User userSix = new User();
            userSix.firstName = "Senadin";
            userSix.lastName = "Botic";
            userSix.email = "senadin.botic@bitcamp.ba";
            userSix.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userSix.userType = buyer;
            userSix.setValidated(true);

            User userSeven = new User();
            userSeven.firstName = "Narena";
            userSeven.lastName = "Ibrisimovic";
            userSeven.email = "narena.ibrisimovic@bitcamp.ba";
            userSeven.password = BCrypt.hashpw("12345678", BCrypt.gensalt());
            userSeven.userType = admin;

            User userEight = new User();
            userEight.firstName = "BitBay";
            userEight.lastName = "Service";
            userEight.email = "bitbayservice@gmail.com";
            userEight.userType = admin;
            userEight.setValidated(true);

            userOne.save();
            userTwo.save();
            userThree.save();
            userFour.save();
            userFive.save();
            userSix.save();
            userSeven.save();
            userEight.save();

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
            Image image1 = new Image();
            image1.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444984356/elwd7zdcibbtlfhrngwq.jpg";
            image1.public_id = "elwd7zdcibbtlfhrngwq";
            image1.secret_image_url = "v1444984356";
            p1.images.add(image1);

            Product p2 = new Product(userOne, "Samsung S5", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 500.00, 20, "1");
            Image image2 = new Image();
            image2.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445541109/eshduir8aoztbcpdumpk.jpg";
            image2.public_id = "eshduir8aoztbcpdumpk";
            image2.secret_image_url = "v1445541109";
            p2.images.add(image2);

            Product p3 = new Product(userOne, "Samsung S4", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 400.00, 10, "1");
            Image image3 = new Image();
            image3.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444984351/pz7tg4ebgnxvzppoiep2.jpg";
            image3.public_id = "pz7tg4ebgnxvzppoiep2";
            image3.secret_image_url = "v1444984351";
            p3.images.add(image3);

            Product p4 = new Product(userOne, "Samsung S3", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 300.00, 10, "1");
            Image image4 = new Image();
            image4.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445553327/samsung-i9300l-galaxy-s3-neo_q4smfa.jpg";
            image4.public_id = "samsung-i9300l-galaxy-s3-neo_q4smfa";
            image4.secret_image_url = "v1445553327";
            p4.images.add(image4);

            Product p5 = new Product(userOne, "Samsung S2", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 200.00, 10, "1");
            Image image5 = new Image();
            image5.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445553326/samsung-galaxy-s2_lkngov.jpg";
            image5.public_id = "samsung-galaxy-s2_lkngov";
            image5.secret_image_url = "v1445553326";
            p5.images.add(image5);

            Product p6 = new Product(userOne, "Samsung S", "NEW CONDITION in OPEN NON-RETAIL BOX. NO SIM CARD INCLUDED, Verizon - LTE, AT&T - 3G, STRAIGHT TALK", "Samsung", categoryTwo, 100.00, 10, "1");
            Image image6 = new Image();
            image6.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444984182/bdbav8fptm4e8py3bitb.png";
            image6.public_id = "bdbav8fptm4e8py3bitb";
            image6.secret_image_url = "v1444984182";
            p6.images.add(image6);

            Product p7 = new Product(userOne, "PlayStation 4", "The PlayStation 4 system opens the door to an incredible journey through i", "Sony", categoryTwo, 600.00, 10, "1");
            Image image7 = new Image();
            image7.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444986457/ic61rm2i0q7z7orxsyty.jpg";
            image7.public_id = "ic61rm2i0q7z7orxsyty";
            image7.secret_image_url = "v1444986457";
            p7.images.add(image7);

            Product p8 = new Product(userOne, "XBOX ONE", "Brand New, Never Used, Never Opened! Microsoft Original!, Region-Free Console!", "Microsoft", categoryTwo, 500.00, 10, "1");
            Image image8 = new Image();
            image8.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555576/xbox-console_cyy9mp.jpg";
            image8.public_id = "xbox-console_cyy9mp";
            image8.secret_image_url = "v1445555576";
            p8.images.add(image8);

            Product p9 = new Product(userOne, "Dell Latitude E6440 Laptop", "Intel Core i5 laptop from Dell. It is installed with Windows 7 Pro", "Dell", categoryTwo, 300.00, 10, "1");
            Image image9 = new Image();
            image9.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1443187214/qjmpa0a7ukkpuhbkzjhp.jpg";
            image9.public_id = "qjmpa0a7ukkpuhbkzjhp";
            image9.secret_image_url = "v1443187214";
            p9.images.add(image9);

            Product p10 = new Product(userOne, "LG Electronics 42LF5600 42-inch FULL HD 1080p", "The LG LB5600 Full HD 1080p LED TV ", "LG", categoryTwo, 500.00, 10, "1");
            Image image10 = new Image();
            image10.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444964868/r3ibse8i0zghe4p6sqx4.jpg";
            image10.public_id = "r3ibse8i0zghe4p6sqx4";
            image10.secret_image_url = "v1444964868";
            p10.images.add(image10);

            /*
            Fashion
             */
            Product p11 = new Product(userFive, "New Sexy Women Summer Casual Sleeveless", "New without tags: A brand-new", "NEW", categoryThree, 600.00, 20, "1");
            Image image11 = new Image();
            image11.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444380665/walaw8ns3dqni9slghbo.jpg";
            image11.public_id = "walaw8ns3dqni9slghbo";
            image11.secret_image_url = "v1444380665";
            p11.images.add(image11);


            Product p12 = new Product(userFive, "Fashion Women Lace Sleeveless Bodycon ", "New without tags: A brand-new", "OWN BRAND", categoryThree, 50.00, 20, "1");
            Image image12 = new Image();
            image12.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1443779594/k9yjdgjmhhclbhiehtyz.jpg";
            image12.public_id = "k9yjdgjmhhclbhiehtyz";
            image12.secret_image_url = "v1443779594";
            p12.images.add(image12);

            Product p13 = new Product(userFive, "Sexy Women Summer Casual Sleeveless", "New without tags: A brand-new", "Oviesse", categoryThree, 40.00, 10, "1");
            Image image13 = new Image();
            image13.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444279458/ld8w7v9jv5jibcqdbm4u.jpg";
            image13.public_id = "ld8w7v9jv5jibcqdbm4u";
            image13.secret_image_url = "v1444279458";
            p13.images.add(image13);

            Product p14 = new Product(userFive, "For Women Sexy Sleeveless Party Evening Cocktail", "New without tags: A brand-new", "Unbranded", categoryThree, 30.00, 10, "1");
            Image image14 = new Image();
            image14.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444380836/excvlsy9ktxdgcrcfji2.jpg";
            image14.public_id = "excvlsy9ktxdgcrcfji2";
            image14.secret_image_url = "v1444380836";
            p14.images.add(image14);

            Product p15 = new Product(userFive, "2015 Summer Vintange New Women Slim Handed Sequins s", "New without tags: A brand-new", "JC", categoryThree, 40.00, 10, "1");
            Image image15 = new Image();
            image15.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1443190526/xts6xzazocq4h1ch8ztt.jpg";
            image15.public_id = "xts6xzazocq4h1ch8ztt";
            image15.secret_image_url = "v1443190526";
            p15.images.add(image15);

            Product p16 = new Product(userFive, "haoduoyi Women's Summer Sequins Sexy Casual Prom Evening", "New without tags: A brand-new", "H&M", categoryThree, 50.00, 10, "1");
            Image image16 = new Image();
            image16.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555579/new-arrival-women-dresses-plus-size-lace_lasl3g.jpg";
            image16.public_id = "new-arrival-women-dresses-plus-size-lace_lasl3g";
            image16.secret_image_url = "v1445555579";
            p16.images.add(image16);

            Product p17 = new Product(userFive, "Women Sexy Summer bodycon Evening Cocktail Party ", "New without tags: A brand-new,", "Unbranded", categoryThree, 70.00, 10, "1");
            Image image17 = new Image();
            image17.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555576/1642-cocktail-dresses-for-women_yzpj45.jpg";
            image17.public_id = "1642-cocktail-dresses-for-women_yzpj45";
            image17.secret_image_url = "v1445555576";
            p17.images.add(image17);

            Product p18 = new Product(userFive, "Men's Slim Stylish Trench Coat Winter Long Jacket ", "New without tags: A brand-new,", "Springfield", categoryThree, 60.00, 10, "1");
            Image image18 = new Image();
            image18.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1444381282/ijevf7ybyxbnsuqbuag4.jpg";
            image18.public_id = "ijevf7ybyxbnsuqbuag4";
            image18.secret_image_url = "v1444381282";
            p18.images.add(image18);

            Product p19 = new Product(userFive, "Hot Men's Warm Jackets Parka Outerwear Fur lined", "A brand-new, unused, and unworn item ", "JC", categoryThree, 100.00, 10, "1");
            Image image19 = new Image();
            image19.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555575/New-2014-Winter-British-Double-Breasted-Wool-font-b-Coat-b-font-Men-s-Jackets-Fashion_m8sk0d.jpg";
            image19.public_id = "New-2014-Winter-British-Double-Breasted-Wool-font-b-Coat-b-font-Men-s-Jackets-Fashion_m8sk0d";
            image19.secret_image_url = "v1445555575";
            p19.images.add(image19);

            Product p20 = new Product(userFive, "2015 Hot Men's Driving Aviator Sunglasses Mirror", "A brand-new, unused, and unworn", "Brothers", categoryThree, 100.00, 10, "1");
            Image image20 = new Image();
            image20.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555576/42-off-original-price-3-49-DOWN-TO-1-99-on-sales-Full-Blue-Mirrored-Aviator_ydimhy.jpg";
            image20.public_id = "42-off-original-price-3-49-DOWN-TO-1-99-on-sales-Full-Blue-Mirrored-Aviator_ydimhy";
            image20.secret_image_url = "v1445555576";
            p20.images.add(image20);

            /*
            Motors
             */
            Product p21 = new Product(userThree, "2006 BMW K", "With enough raw power to shock even the most seasoned adrenaline ", "BMW", categoryFive, 6000.00, 1, "1");
            Image image21 = new Image();
            image21.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555572/hqdefault_mwrhnj.jpg";
            image21.public_id = "hqdefault_mwrhnj";
            image21.secret_image_url = "v1445555572";
            p21.images.add(image21);

            Product p22 = new Product(userThree, "2008 Ducati Superbike", "All %100 carbon fiber, tank is not installed.", "Ducati", categoryFive, 5500.00, 1, "1");
            Image image22 = new Image();
            image22.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555591/ducati-1199-panigale-r_2013_01_ysurrr.png";
            image22.public_id = "ducati-1199-panigale-r_2013_01_ysurrr";
            image22.secret_image_url = "v1445555591";
            p22.images.add(image22);

            Product p23 = new Product(userThree, "2008 HARLEY DAVIDSON ELECTRA GLIDE ULTRA", "We just got this bik ", "Harley Davidson", categoryFive, 14000.00, 1, "1");
            Image image23 = new Image();
            image23.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555580/Harley-Davidson-Shares-Slip_ktxjcv.jpg";
            image23.public_id = "Harley-Davidson-Shares-Slip_ktxjcv";
            image23.secret_image_url = "v1445555580";
            p23.images.add(image23);

            Product p24 = new Product(userThree, "2004 Suzuki GSX-R", "Has just over 13,000 miles. I have proof it had 5000 on it  ", "Suzuki", categoryFive, 3000.00, 1, "1");
            Image image24 = new Image();
            image24.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555578/Suzuki_GSX-R_1000_jfmza0.jpg";
            image24.public_id = "Suzuki_GSX-R_1000_jfmza0";
            image24.secret_image_url = "v1445555578";
            p24.images.add(image24);

            Product p25 = new Product(userThree, "2003 Honda CBR", "Up for auction is my 2003 Honda 954 with 4996mi.  This bike is ", "Honda", categoryFive, 6000.00, 1, "1");
            Image image25 = new Image();
            image25.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555574/honda-cbr-150-r-reps-2_800x0w_k1fhws.jpg";
            image25.public_id = "honda-cbr-150-r-reps-2_800x0w_k1fhws";
            image25.secret_image_url = "v1445555574";
            p25.images.add(image25);

            Product p26 = new Product(userThree, "2015 Ford Taurus", "Such a beautiful car, it is limited with Front wheel drive, navigation", "Ford", categoryFive, 16000.00, 1, "1");
            Image image26 = new Image();
            image26.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555577/2015-ford-taurus-sho-review_k5ahiw.jpg";
            image26.public_id = "2015-ford-taurus-sho-review_k5ahiw";
            image26.secret_image_url = "v1445555577";
            p26.images.add(image26);

            Product p27 = new Product(userThree, "2010 Mercedes-Benz S-Class", "The vehicle is in excellent mechanical and cosmetic condition.", "Mercedes", categoryFive, 33900.00, 1, "1");
            Image image27 = new Image();
            image27.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555580/2014-Mercedes-Benz-S-Class_ujfh67.jpg";
            image27.public_id = "2014-Mercedes-Benz-S-Class_ujfh67";
            image27.secret_image_url = "v1445555580";
            p27.images.add(image27);

            Product p28 = new Product(userThree, "2013 Chevrolet Camaro 2dr Coupe", "The vehicle is in excellent mechanical and cosmetic condition.", "Chevrolet", categoryFive, 36000.00, 1, "1");
            Image image28 = new Image();
            image28.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555579/6519_rpq4sw.jpg";
            image28.public_id = "6519_rpq4sw";
            image28.secret_image_url = "v1445555579";
            p28.images.add(image28);


            Product p29 = new Product(userThree, "Metallic Silver 4pcs Set #510 15 Inches ", "Build from high quality ABS and patented ", "Hub Cup", categoryFive, 100.00, 1, "4");
            Image image29 = new Image();
            image29.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555574/s-l225_ux0ouv.jpg";
            image29.public_id = "s-l225_ux0ouv";
            image29.secret_image_url = "v1445555574";
            p29.images.add(image29);

            Product p30 = new Product(userThree, "Set of 4 Ford 15\" Chrome Wheel Hub Caps", "A Complete New Set of 4 Wheel Skins & Shipping", "Coast to Coast", categoryFive, 50.00, 4, "1");
            Image image30 = new Image();
            image30.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555572/mHXNmJkMeNR24QBmrF6hzGg_shouxl.jpg";
            image30.public_id = "mHXNmJkMeNR24QBmrF6hzGg_shouxl";
            image30.secret_image_url = "v1445555572";
            p30.images.add(image30);

            /*
            Sports
             */
            Product p31 = new Product(userThree, "ADIDAS BRAZUCA FIFA", "A brand-new, unused, unopened, undamaged item in its original packaging", "Adidas", categorySix, 60.00, 20, "1");
            Image image31 = new Image();
            image31.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555580/3022879-inline-s-6-2013-fifa-world-cup-brasil-ball_pmdf3q.jpg";
            image31.public_id = "3022879-inline-s-6-2013-fifa-world-cup-brasil-ball_pmdf3q";
            image31.secret_image_url = "v1445555580";
            p31.images.add(image31);

            Product p32 = new Product(userThree, "ADIDAS 2012-13 REAL MADRID HOME SOCCER JERSEY LARGE", "A brand-new, unused, unopened,", "Adidas", categorySix, 50.00, 20, "1");
            Image image32 = new Image();
            image32.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555573/New-Real-Madrid-Home-Kit-2014-15_lvdfqm.jpg";
            image32.public_id = "New-Real-Madrid-Home-Kit-2014-15_lvdfqm";
            image32.secret_image_url = "v1445555573";
            p32.images.add(image32);

            Product p33 = new Product(userThree, "Manchester United Football Club Official Soccer Jacket", "A brand-new ", "Manchester United FC", categorySix, 40.00, 10, "1");
            Image image33 = new Image();
            image33.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555579/37759408_xxl_sh4gun.jpg";
            image33.public_id = "37759408_xxl_sh4gun";
            image33.secret_image_url = "v1445555579";
            p33.images.add(image33);

            Product p34 = new Product(userThree, "NFL RBK St Louis Rams #55 Closeout Football Jersey", "A brand-new, unused, unopened, undamaged ", "Rams", categorySix, 30.00, 10, "1");
            Image image34 = new Image();
            image34.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555573/N16717_j7wigp.jpg";
            image34.public_id = "N16717_j7wigp";
            image34.secret_image_url = "v1445555573";
            p34.images.add(image34);

            Product p35 = new Product(userThree, "WILSON Duke Micro Mini American Football Ball ", "A brand-new, unused, unopened, undamaged item ", "Wilson", categorySix, 20.00, 10, "1");
            Image image35 = new Image();
            image35.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445555576/DUKENFLLOGO_nk6jhi.jpg";
            image35.public_id = "DUKENFLLOGO_nk6jhi";
            image35.secret_image_url = "v1445555576";
            p35.images.add(image35);

            Product p36 = new Product(userThree, "Teman Hybrid Bike Racing Bike Road Bike ", "A brand-new, unused ", "Shimano", categorySix, 300.00, 10, "1");
            Image image36 = new Image();
            image36.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445558898/_35_bqcfki.jpg";
            image36.public_id = "_35_bqcfki";
            image36.secret_image_url = "v1445558898";
            p36.images.add(image36);

            Product p37 = new Product(userThree, "KAYAK ROD KAYAK FISHING ROD & REEL", "A brand-new, unused, unopened, undamaged item in its original packaging", "INSTANT FISHERMAN", categorySix, 200.00, 10, "1");
            Image image37 = new Image();
            image37.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445558882/KFL805p2_qcxghh.jpg";
            image37.public_id = "KFL805p2_qcxghh";
            image37.secret_image_url = "v1445558882";
            p37.images.add(image37);

            Product p38 = new Product(userFive, "Black NEW Outdoor Camping Hiking Trekking Travel", "A brand-new, unused, unopened, ", "Unbranded", categorySix, 40.00, 10, "1");
            Image image38 = new Image();
            image38.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445558892/Black-NEW-Outdoor-Camping-Hiking-Trekking-Travel-bag-Military-Tactical-Rucksack_dgebmm.jpg";
            image38.public_id = "Black-NEW-Outdoor-Camping-Hiking-Trekking-Travel-bag-Military-Tactical-Rucksack_dgebmm";
            image38.secret_image_url = "v1445558892";
            p38.images.add(image38);

            Product p39 = new Product(userFive, "TaylorMade SLDR #3 15* Fairway Wood-Speeder 77G D", "LEFT HANDED USED LEFT HANDED TaylorMade", "TaylotMade", categorySix, 50.00, 10, "1");
            Image image39 = new Image();
            image39.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445558918/DSC_0866-L_qjgs8x.jpg";
            image39.public_id = "DSC_0866-L_qjgs8x";
            image39.secret_image_url = "v1445558918";
            p39.images.add(image39);

            Product p40 = new Product(userFive, "3000LM CREE XM-L T6 LED Rechargeable Flashlight Torch", "A brand-new, unused, unopened", "Ultrafire", categorySix, 10.00, 10, "1");
            Image image40 = new Image();
            image40.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445558873/_35_1_kmayfo.jpg";
            image40.public_id = "_35_1_kmayfo";
            image40.secret_image_url = "v1445558873";
            p40.images.add(image40);

            /*
            Home
             */
            Product p41 = new Product(userOne, "Suncast GS9000 7 x 7 Large Outdoor Vertical Shed ", "Extreme Suncast Storage ", "Suncast", categorySeven, 600.00, 20, "1");
            Image image41 = new Image();
            image41.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589840/optim-450x450_ybo9yj.jpg";
            image41.public_id = "optim-450x450_ybo9yj";
            image41.secret_image_url = "v1445589840";
            p41.images.add(image41);

            Product p42 = new Product(userOne, "Tripod Impulse Sprinkler Pulsating Telescopic Watering ", "A brand-new, unused,", "HowPlumb", categorySeven, 500.00, 20, "1");
            Image image42 = new Image();
            image42.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589841/61Yh3t1JBmL._SY300__sbdc2k.jpg";
            image42.public_id = "61Yh3t1JBmL._SY300__sbdc2k";
            image42.secret_image_url = "v1445589841";
            p42.images.add(image42);

            Product p43 = new Product(userOne, "ADEL LS9 Biometric Fingerprint Door Lock Electronic Keyless", "A brand-new, unused", "Unbranded", categorySeven, 20.00, 10, "1");
            Image image43 = new Image();
            image43.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590724/ADEL-LS9-Biometric-Fingerprint-Door-Lock-Electronic-Keyless-Password-Lock-Black-Yellow_320x320_fwyxi4.jpg";
            image43.public_id = "ADEL-LS9-Biometric-Fingerprint-Door-Lock-Electronic-Keyless-Password-Lock-Black-Yellow_320x320_fwyxi4";
            image43.secret_image_url = "v1445590724";
            p43.images.add(image43);

            Product p44 = new Product(userFive, "Outsunny 12pc Outdoor Patio Rattan Wicker Furniture Set", "A brand-new, unused,", "Unbranded", categorySeven, 300.00, 10, "1");
            Image image44 = new Image();
            image44.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590722/619GJsS1MIL._SY355__nmmkbl.jpg";
            image44.public_id = "619GJsS1MIL._SY355__nmmkbl";
            image44.secret_image_url = "v1445590722";
            p44.images.add(image44);

            Product p45 = new Product(userFive, "Phone APP Alarm System Remote Control Wireless House Fire", "A brand-new, unused,", "Unbranded", categorySeven, 200.00, 10, "1");
            Image image45 = new Image();
            image45.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590724/21-21005851-21005851-11-150728150335_bai9po.jpg";
            image45.public_id = "21-21005851-21005851-11-150728150335_bai9po";
            image45.secret_image_url = "v1445590724";
            p45.images.add(image45);

            Product p46 = new Product(userFive, "Double Hammock With Space Saving Steel Stand  Case", "A brand-new, unused,", "Unbranded", categorySeven, 100.00, 10, "1");
            Image image46 = new Image();
            image46.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590723/81SRjuocm0L._SX466__fcfzps.jpg";
            image46.public_id = "81SRjuocm0L._SX466__fcfzps";
            image46.secret_image_url = "v1445590723";
            p46.images.add(image46);

            Product p47 = new Product(userFive, "New 10/50/100pcs Wholesale DIY Accessories Wood Baby Craft", "A brand-new, unused,", "Handmade", categorySeven, 8.00, 10, "1");
            Image image47 = new Image();
            image47.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590722/mqVMhTcOihVS5x_xxELLaOw_cusqwt.jpg";
            image47.public_id = "mqVMhTcOihVS5x_xxELLaOw_cusqwt";
            image47.secret_image_url = "v1445590722";
            p47.images.add(image47);

            Product p48 = new Product(userFive, "Hunter Hepa Allergen Removing Air Purifier- Tech", "An item that has been professionally ", "Hunter", categorySeven, 100.00, 10, "1");
            Image image48 = new Image();
            image48.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590724/Hunter-30793-PermaLife-Large-Room-Air-Purifier-with-Permanent-Filter_wmnx7f.jpg";
            image48.public_id = "Hunter-30793-PermaLife-Large-Room-Air-Purifier-with-Permanent-Filter_wmnx7f";
            image48.secret_image_url = "v1445590724";
            p48.images.add(image48);


            Product p49 = new Product(userFive, "Pink Cotton 5 Assorted Pre Cut Charm Craft 10", "A brand-new, unused, unopened, ", "Handmade", categorySeven, 6.00, 10, "1");
            Image image49 = new Image();
            image49.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590722/5-Pink-Cotton-5-Assorted-Pre-Cut-Charm-Quilt-font-b-Squares-b-font-font-b_unh4nu.jpg";
            image49.public_id = "5-Pink-Cotton-5-Assorted-Pre-Cut-Charm-Quilt-font-b-Squares-b-font-font-b_unh4nu";
            image49.secret_image_url = "v1445590722";
            p49.images.add(image49);

            Product p50 = new Product(userFive, "100Pcs 11MM DIY 2 Holes Round Resin Craft 20 Colors", "A brand-new, unused, unopened,", "Unbranded", categorySeven, 1.00, 10, "1");
            Image image50 = new Image();
            image50.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445590723/100pcs-11mm-sweet-love-heart-2-holes-resin_ivx3pp.jpg";
            image50.public_id = "100pcs-11mm-sweet-love-heart-2-holes-resin_ivx3pp";
            image50.secret_image_url = "v1445590723";
            p50.images.add(image50);

            /*
            Books
             */

            Product p51 = new Product(userOne, "Humans of New York: Stories", "“There's no judgment, just observation and in many cases reverence","St. Martin's Press", categoryFour, 20.00, 10, "1");
            Image image51 = new Image();
            image51.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589841/humans_rchv39.jpg";
            image51.public_id = "humans_rchv39";
            image51.secret_image_url = "v1445589841";
            p51.images.add(image51);

            Product p52 = new Product(userOne, "Lost", "Like everyone reading the newspapers these days, 10-year-old Barney Roberts knows ", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Image image52 = new Image();
            image52.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589842/literary-lost-front-cover-visual21_ze4hbp.jpg";
            image52.public_id = "literary-lost-front-cover-visual21_ze4hbp";
            image52.secret_image_url = "v1445589842";
            p52.images.add(image52);

            Product p53 = new Product(userOne, "Whirligig", "A terrible accident ends one life, but is just the beginning for another. . . .", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Image image53 = new Image();
            image53.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589842/0805055827_lwxrsu.jpg";
            image53.public_id = "0805055827_lwxrsu";
            image53.secret_image_url = "v1445589842";
            p53.images.add(image53);

            Product p54 = new Product(userOne, "Zero the Hero", "Zero. Zip. Zilch. Nada. That's what all the other numbers think of Zero.", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Image image54 = new Image();
            image54.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589841/12071060_snseik.jpg";
            image54.public_id = "12071060_snseik";
            image54.secret_image_url = "v1445589841";
            p54.images.add(image54);

            Product p55 = new Product(userOne, "Memories of Babi", "Piri is a city girl, but every year she goes to visit her grandmother Babi on.", "St. Martin's Press", categoryFour, 20.00, 10, "1");
            Image image55 = new Image();
            image55.image_url = "http://res.cloudinary.com/bitcamp/image/upload/v1445589841/51g-J3FXJIL._SX329_BO1_204_203_200__fdmqad.jpg";
            image55.public_id = "51g-J3FXJIL._SX329_BO1_204_203_200__fdmqad";
            image55.secret_image_url = "v1445589841";
            p55.images.add(image55);


            List<User> buyers = new ArrayList<>();
            buyers.add(userTwo);
            buyers.add(userFour);
            buyers.add(userSix);

            List<Product> products = new ArrayList<>();
            products.add(p1);
            products.add(p2);
            products.add(p3);
            products.add(p4);
            products.add(p5);
            products.add(p6);
            products.add(p7);
            products.add(p8);
            products.add(p9);
            products.add(p10);
            products.add(p11);
            products.add(p12);
            products.add(p13);
            products.add(p14);
            products.add(p15);
            products.add(p16);
            products.add(p17);
            products.add(p18);
            products.add(p19);
            products.add(p20);
            products.add(p21);
            products.add(p22);
            products.add(p23);
            products.add(p24);
            products.add(p25);
            products.add(p26);
            products.add(p27);
            products.add(p28);
            products.add(p29);
            products.add(p30);
            products.add(p31);
            products.add(p32);
            products.add(p33);
            products.add(p34);
            products.add(p35);
            products.add(p36);
            products.add(p37);
            products.add(p38);
            products.add(p39);
            products.add(p40);
            products.add(p41);
            products.add(p42);
            products.add(p43);
            products.add(p44);
            products.add(p45);
            products.add(p46);
            products.add(p47);
            products.add(p48);
            products.add(p49);
            products.add(p50);
            products.add(p51);
            products.add(p52);
            products.add(p53);
            products.add(p54);
            products.add(p55);


            for (int i = 0; i < products.size(); i++){
                products.get(i).cancelation = 2;
            }


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

            Random num = new Random();

            for(int i = 1; i < products.size(); i++){
                new Rating(buyers.get(num.nextInt(3)), Product.getProductById((num.nextInt(50) + 1)), (num.nextInt(5) + 1)).save();
            }

            FAQ f1 = new FAQ("Registration and account info\n", "Your account is your identity on bitBay. You use your account to buy, sell, communicate with bitBay members, and leave Feedback for bitBay buyers and sellers.\n" +
                    "\n" +
                    "You may sign in using your email address associated with your bitBay account.");
            FAQ f2 = new FAQ("What sells well?\n", "Electronics like cameras, smart phones, tablets and laptops\n" +
                    "Antiques & collectibles like early-issue comics, rare baseball cards, vintage toys\n" +
                    "Musical instruments like violins, trumpets, flutes, and guitars\n" +
                    "New and like-new designer clothing, shoes and handbags: See full list of eligible brands and estimated values\n" +
                    "Sporting goods and accessories like golf clubs, baseball bats, tennis rackets\n" +
                    "High-end kitchen appliances like blenders and mixers by brands like KitchenAid, Vitamix, and Breville");
            FAQ f3 = new FAQ("Forgot your password?", "Go to 'Forgot password' and enter your registered email address or username to begin.");
            FAQ f4 = new FAQ("Can I save time shopping online?\n" + "\n", "You do not have to drive to the mall, park, hike inland for a mile or so, buy stuff, hike back, and drive home.");
            FAQ f5 = new FAQ("How do I select items to purchase?", "As you browse through the bitBay, click on the 'add to cart', and the item is in the cart. ");
            FAQ f6 = new FAQ("What exactly happens after ordering?", "After ordering you will promptly receive an order confirmation via e-mail. After the order has been processed in our database, the package will leave our warehouse on the same day, " +
                    "or, at the latest, the following work day.");
            f1.save();
            f2.save();
            f3.save();
            f4.save();
            f5.save();
            f6.save();



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
