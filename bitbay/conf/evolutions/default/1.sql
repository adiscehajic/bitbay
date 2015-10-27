# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table auction (
  id                        integer auto_increment not null,
  product_id                integer,
  starting_price            double,
  is_active                 tinyint(1) default 0,
  starting_date             datetime,
  ending_date               datetime,
  constraint uq_auction_product_id unique (product_id),
  constraint pk_auction primary key (id))
;

create table bid (
  id                        integer auto_increment not null,
  user_id                   integer,
  auction_id                integer,
  amount                    double,
  biding_date               datetime,
  constraint pk_bid primary key (id))
;

create table cart (
  id                        integer auto_increment not null,
  user_id                   integer,
  constraint uq_cart_user_id unique (user_id),
  constraint pk_cart primary key (id))
;

create table cart_item (
  id                        integer auto_increment not null,
  user_id                   integer,
  product_id                integer,
  cart_id                   integer,
  quantity                  integer,
  price                     double,
  constraint pk_cart_item primary key (id))
;

create table category (
  id                        integer auto_increment not null,
  name                      varchar(255),
  parent_id                 integer,
  constraint pk_category primary key (id))
;

create table comment (
  id                        integer auto_increment not null,
  title                     varchar(255),
  text                      TEXT,
  user_id                   integer,
  product_id                integer,
  comment_date              datetime,
  constraint pk_comment primary key (id))
;

create table country (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_country primary key (id))
;

create table faq (
  id                        integer auto_increment not null,
  question                  varchar(255),
  answer                    TEXT,
  constraint pk_faq primary key (id))
;

create table image (
  id                        integer auto_increment not null,
  public_id                 varchar(255),
  secret_image_url          varchar(255),
  image_url                 varchar(255),
  product_id                integer,
  user_id                   integer,
  constraint uq_image_user_id unique (user_id),
  constraint pk_image primary key (id))
;

create table message (
  id                        integer auto_increment not null,
  sender_id                 integer,
  receiver_id               integer,
  title                     varchar(255),
  message                   TEXT,
  receiver_visible          tinyint(1) default 0,
  sender_visible            tinyint(1) default 0,
  is_read                   tinyint(1) default 0,
  date                      datetime,
  constraint pk_message primary key (id))
;

create table product (
  id                        integer auto_increment not null,
  user_id                   integer,
  name                      varchar(255),
  description               TEXT,
  manufacturer              varchar(255),
  category_id               integer,
  price                     double,
  quantity                  integer,
  selling_type              varchar(255),
  registration              datetime,
  updated                   datetime,
  cancelation               integer,
  constraint pk_product primary key (id))
;

create table purchase (
  id                        integer auto_increment not null,
  payment_id                varchar(255),
  bit_payment_id            varchar(255),
  sale_id                   varchar(255),
  total_price               double,
  token                     varchar(255),
  user_id                   integer,
  purchase_date             datetime,
  constraint pk_purchase primary key (id))
;

create table purchase_item (
  id                        integer auto_increment not null,
  user_id                   integer,
  product_id                integer,
  purchase_id               integer,
  quantity                  integer,
  price                     double,
  cancelation_due_date      datetime,
  is_refunded               integer,
  constraint pk_purchase_item primary key (id))
;

create table rating (
  id                        integer auto_increment not null,
  user_id                   integer,
  product_id                integer,
  rate                      integer,
  constraint pk_rating primary key (id))
;

create table recommendation (
  id                        integer auto_increment not null,
  user_id                   integer,
  category_id               integer,
  count                     integer,
  constraint pk_recommendation primary key (id))
;

create table thumb (
  id                        integer auto_increment not null,
  comment_id                integer,
  user_id                   integer,
  is_up                     tinyint(1) default 0,
  constraint pk_thumb primary key (id))
;

create table user (
  id                        integer auto_increment not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  phone_number              varchar(255),
  user_type_id              integer,
  country_id                integer,
  city                      varchar(255),
  zip                       integer,
  address                   varchar(255),
  registration              datetime,
  updated                   datetime,
  token                     varchar(255),
  validated                 tinyint(1) default 0,
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table user_type (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_user_type primary key (id))
;

alter table auction add constraint fk_auction_product_1 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_auction_product_1 on auction (product_id);
alter table bid add constraint fk_bid_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_bid_user_2 on bid (user_id);
alter table bid add constraint fk_bid_auction_3 foreign key (auction_id) references auction (id) on delete restrict on update restrict;
create index ix_bid_auction_3 on bid (auction_id);
alter table cart add constraint fk_cart_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_cart_user_4 on cart (user_id);
alter table cart_item add constraint fk_cart_item_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_cart_item_user_5 on cart_item (user_id);
alter table cart_item add constraint fk_cart_item_product_6 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_cart_item_product_6 on cart_item (product_id);
alter table cart_item add constraint fk_cart_item_cart_7 foreign key (cart_id) references cart (id) on delete restrict on update restrict;
create index ix_cart_item_cart_7 on cart_item (cart_id);
alter table category add constraint fk_category_parent_8 foreign key (parent_id) references category (id) on delete restrict on update restrict;
create index ix_category_parent_8 on category (parent_id);
alter table comment add constraint fk_comment_user_9 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_comment_user_9 on comment (user_id);
alter table comment add constraint fk_comment_product_10 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_comment_product_10 on comment (product_id);
alter table image add constraint fk_image_product_11 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_image_product_11 on image (product_id);
alter table image add constraint fk_image_user_12 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_image_user_12 on image (user_id);
alter table message add constraint fk_message_sender_13 foreign key (sender_id) references user (id) on delete restrict on update restrict;
create index ix_message_sender_13 on message (sender_id);
alter table message add constraint fk_message_receiver_14 foreign key (receiver_id) references user (id) on delete restrict on update restrict;
create index ix_message_receiver_14 on message (receiver_id);
alter table product add constraint fk_product_user_15 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_product_user_15 on product (user_id);
alter table product add constraint fk_product_category_16 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_product_category_16 on product (category_id);
alter table purchase add constraint fk_purchase_user_17 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_purchase_user_17 on purchase (user_id);
alter table purchase_item add constraint fk_purchase_item_user_18 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_purchase_item_user_18 on purchase_item (user_id);
alter table purchase_item add constraint fk_purchase_item_product_19 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_purchase_item_product_19 on purchase_item (product_id);
alter table purchase_item add constraint fk_purchase_item_purchase_20 foreign key (purchase_id) references purchase (id) on delete restrict on update restrict;
create index ix_purchase_item_purchase_20 on purchase_item (purchase_id);
alter table rating add constraint fk_rating_user_21 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_rating_user_21 on rating (user_id);
alter table rating add constraint fk_rating_product_22 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_rating_product_22 on rating (product_id);
alter table recommendation add constraint fk_recommendation_user_23 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_recommendation_user_23 on recommendation (user_id);
alter table recommendation add constraint fk_recommendation_category_24 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_recommendation_category_24 on recommendation (category_id);
alter table thumb add constraint fk_thumb_comment_25 foreign key (comment_id) references comment (id) on delete restrict on update restrict;
create index ix_thumb_comment_25 on thumb (comment_id);
alter table thumb add constraint fk_thumb_user_26 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_thumb_user_26 on thumb (user_id);
alter table user add constraint fk_user_userType_27 foreign key (user_type_id) references user_type (id) on delete restrict on update restrict;
create index ix_user_userType_27 on user (user_type_id);
alter table user add constraint fk_user_country_28 foreign key (country_id) references country (id) on delete restrict on update restrict;
create index ix_user_country_28 on user (country_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table auction;

drop table bid;

drop table cart;

drop table cart_item;

drop table category;

drop table comment;

drop table country;

drop table faq;

drop table image;

drop table message;

drop table product;

drop table purchase;

drop table purchase_item;

drop table rating;

drop table recommendation;

drop table thumb;

drop table user;

drop table user_type;

SET FOREIGN_KEY_CHECKS=1;

