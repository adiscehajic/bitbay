# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

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

alter table comment add constraint fk_comment_user_1 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_comment_user_1 on comment (user_id);
alter table comment add constraint fk_comment_product_2 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_comment_product_2 on comment (product_id);
alter table image add constraint fk_image_product_3 foreign key (product_id) references product (id) on delete restrict on update restrict;
create index ix_image_product_3 on image (product_id);
alter table product add constraint fk_product_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_product_user_4 on product (user_id);
alter table product add constraint fk_product_category_5 foreign key (category_id) references category (id) on delete restrict on update restrict;
create index ix_product_category_5 on product (category_id);
alter table user add constraint fk_user_userType_6 foreign key (user_type_id) references user_type (id) on delete restrict on update restrict;
create index ix_user_userType_6 on user (user_type_id);
alter table user add constraint fk_user_country_7 foreign key (country_id) references country (id) on delete restrict on update restrict;
create index ix_user_country_7 on user (country_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table category;

drop table comment;

drop table country;

drop table image;

drop table product;

drop table user;

drop table user_type;

SET FOREIGN_KEY_CHECKS=1;

