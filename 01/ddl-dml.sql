create database "stockbit" owner postgres;

create table if not exists "USER"(
    "ID" int not null,
    "UserName" varchar not null,
    "Parent" int,
    primary key("ID")
);

create index "USER_idx_parent" ON "USER"("Parent");

insert into "USER" values(1, 'Ali', 2);
insert into "USER" values(2, 'Budi', 0);
insert into "USER" values(3, 'Cecep', 1);
