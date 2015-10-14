# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

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

create table image (
  id                        integer auto_increment not null,
  public_id                 varchar(255),
  secret_image_url          varchar(255),
  image_url                 varchar(255),
  product_id                integer,
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
  constraint pk_product primary key (id))
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

alter table cart add constraint fk_cart_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_cart_user_1 on cart (user_id);
alter table cart_item add constraint fk_cart_item_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_cart_item_user_2 on cart_item (user_id);
alter table cart_item add constraint fk_cart_item_product_3 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_cart_item_product_3 on cart_item (product_id);
alter table cart_item add constraint fk_cart_item_cart_4 foreign key (cart_id) references cart (id) on delete restrict on update restrict;
create index ix_cart_item_cart_4 on cart_item (cart_id);
alter table comment add constraint fk_comment_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_comment_user_5 on comment (user_id);
alter table comment add constraint fk_comment_product_6 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_comment_product_6 on comment (product_id);
alter table image add constraint fk_image_product_7 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_image_product_7 on image (product_id);
alter table message add constraint fk_message_sender_8 foreign key (sender_id) references user (id) on delete restrict on update restrict;
create index ix_message_sender_8 on message (sender_id);
alter table message add constraint fk_message_receiver_9 foreign key (receiver_id) references user (id) on delete restrict on update restrict;
create index ix_message_receiver_9 on message (receiver_id);
alter table product add constraint fk_product_user_10 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_product_user_10 on product (user_id);
alter table product add constraint fk_product_category_11 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_product_category_11 on product (category_id);
alter table rating add constraint fk_rating_user_12 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_rating_user_12 on rating (user_id);
alter table rating add constraint fk_rating_product_13 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_rating_product_13 on rating (product_id);
alter table recommendation add constraint fk_recommendation_user_14 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_recommendation_user_14 on recommendation (user_id);
alter table recommendation add constraint fk_recommendation_category_15 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_recommendation_category_15 on recommendation (category_id);
alter table thumb add constraint fk_thumb_comment_16 foreign key (comment_id) references comment (id) on delete restrict on update restrict;
create index ix_thumb_comment_16 on thumb (comment_id);
alter table thumb add constraint fk_thumb_user_17 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_thumb_user_17 on thumb (user_id);
alter table user add constraint fk_user_userType_18 foreign key (user_type_id) references user_type (id) on delete restrict on update restrict;
create index ix_user_userType_18 on user (user_type_id);
alter table user add constraint fk_user_country_19 foreign key (country_id) references country (id) on delete restrict on update restrict;
create index ix_user_country_19 on user (country_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table cart;

drop table cart_item;

drop table category;

drop table comment;

drop table country;

drop table image;

drop table message;

drop table product;

drop table rating;

drop table recommendation;

drop table thumb;

drop table user;

drop table user_type;

SET FOREIGN_KEY_CHECKS=1;

