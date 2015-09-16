# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table cart (
  id                        integer auto_increment not null,
  user_id                   integer,
  constraint uq_cart_user_id unique (user_id),
  constraint pk_cart primary key (id))
;

create table category (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_category primary key (id))
;

create table comment (
  id                        integer auto_increment not null,
  title                     varchar(255),
  text                      varchar(255),
  user_id                   integer,
  product_id                integer,
  comment_date              datetime,
  constraint uq_comment_user_id unique (user_id),
  constraint pk_comment primary key (id))
;

create table country (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_country primary key (id))
;

create table image (
  id                        integer auto_increment not null,
  path                      varchar(255),
  product_id                integer,
  constraint pk_image primary key (id))
;

create table product (
  id                        integer auto_increment not null,
  user_id                   integer,
  name                      varchar(255),
  description               varchar(255),
  manufacturer              varchar(255),
  category_id               integer,
  price                     double,
  quantity                  integer,
  selling_type              varchar(255),
  registration              datetime,
  updated                   datetime,
  constraint pk_product primary key (id))
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
  constraint uq_user_email unique (email),
  constraint pk_user primary key (id))
;

create table user_type (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_user_type primary key (id))
;


create table cart_product (
  cart_id                        integer not null,
  product_id                     integer not null,
  constraint pk_cart_product primary key (cart_id, product_id))
;
alter table cart add constraint fk_cart_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_cart_user_1 on cart (user_id);
alter table comment add constraint fk_comment_user_2 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_comment_user_2 on comment (user_id);
alter table comment add constraint fk_comment_product_3 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_comment_product_3 on comment (product_id);
alter table image add constraint fk_image_product_4 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_image_product_4 on image (product_id);
alter table product add constraint fk_product_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_product_user_5 on product (user_id);
alter table product add constraint fk_product_category_6 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_product_category_6 on product (category_id);
alter table user add constraint fk_user_userType_7 foreign key (user_type_id) references user_type (id) on delete restrict on update restrict;
create index ix_user_userType_7 on user (user_type_id);
alter table user add constraint fk_user_country_8 foreign key (country_id) references country (id) on delete restrict on update restrict;
create index ix_user_country_8 on user (country_id);



alter table cart_product add constraint fk_cart_product_cart_01 foreign key (cart_id) references cart (id) on delete restrict on update restrict;

alter table cart_product add constraint fk_cart_product_product_02 foreign key (product_id) references product (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table cart;

drop table cart_product;

drop table category;

drop table comment;

drop table country;

drop table image;

drop table product;

drop table user;

drop table user_type;

SET FOREIGN_KEY_CHECKS=1;

