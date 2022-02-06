-- create database MANAGE;
use MANAGE;
create table book( 
id varchar(20) primary key,
bookname varchar(20) not null,
author varchar(20) not null,
issue int not null,
student_id varchar(20)
);

create table record(
idnumber int not null auto_increment primary key,
id varchar(20) not null ,
rollno varchar(20) not null,
studentname varchar(20) not null,
issue int not null,
lastdate date
);

select * from book;