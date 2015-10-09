# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table cart (
  id                        integer not null,
  user_id                   integer,
  constraint uq_cart_user_id unique (user_id),
  constraint pk_cart primary key (id))
;

create table cart_item (
  id                        integer not null,
  product_id                integer,
  quantity                  integer,
  price                     double,
  constraint uq_cart_item_product_id unique (product_id),
  constraint pk_cart_item primary key (id))
;

create table category (
  id                        integer not null,
  name                      varchar(255),
  constraint pk_category primary key (id))
;

create table comment (
  id                        integer not null,
  title                     varchar(255),
  text                      TEXT,
  user_id                   integer,
  product_id                integer,
  comment_date              datetime,
  constraint pk_comment primary key (id))
;

create table country (
  id                        integer not null,
  name                      varchar(255),
  constraint pk_country primary key (id))
;

create table image (
  id                        integer not null,
  public_id                 varchar(255),
  secret_image_url          varchar(255),
  image_url                 varchar(255),
  product_id                integer,
  constraint pk_image primary key (id))
;

create table message (
  id                        integer not null,
  sender_id                 integer,
  receiver_id               integer,
  title                     varchar(255),
  message                   TEXT,
  receiver_visible          boolean,
  sender_visible            boolean,
  is_read                   boolean,
  date                      datetime,
  constraint pk_message primary key (id))
;

create table product (
  id                        integer not null,
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
  id                        integer not null,
  user_id                   integer,
  product_id                integer,
  rate                      integer,
  constraint pk_rating primary key (id))
;

create table recommendation (
  id                        integer not null,
  user_id                   integer,
  category_id               integer,
  count                     integer,
  constraint pk_recommendation primary key (id))
;

create table thumb (
  id                        integer not null,
  comment_id                integer,
  user_id                   integer,
  is_up                     boolean,
  constraint pk_thumb primary key (id))
;

create table user (
  id                        integer not null,
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
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table user_type (
  id                        integer not null,
  name                      varchar(255),
  constraint pk_user_type primary key (id))
;


create table cart_cart_item (
  cart_id                        integer not null,
  cart_item_id                   integer not null,
  constraint pk_cart_cart_item primary key (cart_id, cart_item_id))
;
create sequence cart_seq;

create sequence cart_item_seq;

create sequence category_seq;

create sequence comment_seq;

create sequence country_seq;

create sequence image_seq;

create sequence message_seq;

create sequence product_seq;

create sequence rating_seq;

create sequence recommendation_seq;

create sequence thumb_seq;

create sequence user_seq;

create sequence user_type_seq;

alter table cart add constraint fk_cart_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_cart_user_1 on cart (user_id);
alter table cart_item add constraint fk_cart_item_product_2 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_cart_item_product_2 on cart_item (product_id);
alter table comment add constraint fk_comment_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_comment_user_3 on comment (user_id);
alter table comment add constraint fk_comment_product_4 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_comment_product_4 on comment (product_id);
alter table image add constraint fk_image_product_5 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_image_product_5 on image (product_id);
alter table message add constraint fk_message_sender_6 foreign key (sender_id) references user (id) on delete restrict on update restrict;
create index ix_message_sender_6 on message (sender_id);
alter table message add constraint fk_message_receiver_7 foreign key (receiver_id) references user (id) on delete restrict on update restrict;
create index ix_message_receiver_7 on message (receiver_id);
alter table product add constraint fk_product_user_8 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_product_user_8 on product (user_id);
alter table product add constraint fk_product_category_9 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_product_category_9 on product (category_id);
alter table rating add constraint fk_rating_user_10 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_rating_user_10 on rating (user_id);
alter table rating add constraint fk_rating_product_11 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_rating_product_11 on rating (product_id);
alter table recommendation add constraint fk_recommendation_user_12 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_recommendation_user_12 on recommendation (user_id);
alter table recommendation add constraint fk_recommendation_category_13 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_recommendation_category_13 on recommendation (category_id);
alter table thumb add constraint fk_thumb_comment_14 foreign key (comment_id) references comment (id) on delete restrict on update restrict;
create index ix_thumb_comment_14 on thumb (comment_id);
alter table thumb add constraint fk_thumb_user_15 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_thumb_user_15 on thumb (user_id);
alter table user add constraint fk_user_userType_16 foreign key (user_type_id) references user_type (id) on delete restrict on update restrict;
create index ix_user_userType_16 on user (user_type_id);
alter table user add constraint fk_user_country_17 foreign key (country_id) references country (id) on delete restrict on update restrict;
create index ix_user_country_17 on user (country_id);



alter table cart_cart_item add constraint fk_cart_cart_item_cart_01 foreign key (cart_id) references cart (id) on delete restrict on update restrict;

alter table cart_cart_item add constraint fk_cart_cart_item_cart_item_02 foreign key (cart_item_id) references cart_item (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists cart;

drop table if exists cart_cart_item;

drop table if exists cart_item;

drop table if exists category;

drop table if exists comment;

drop table if exists country;

drop table if exists image;

drop table if exists message;

drop table if exists product;

drop table if exists rating;

drop table if exists recommendation;

drop table if exists thumb;

drop table if exists user;

drop table if exists user_type;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists cart_seq;

drop sequence if exists cart_item_seq;

drop sequence if exists category_seq;

drop sequence if exists comment_seq;

drop sequence if exists country_seq;

drop sequence if exists image_seq;

drop sequence if exists message_seq;

drop sequence if exists product_seq;

drop sequence if exists rating_seq;

drop sequence if exists recommendation_seq;

drop sequence if exists thumb_seq;

drop sequence if exists user_seq;

drop sequence if exists user_type_seq;

