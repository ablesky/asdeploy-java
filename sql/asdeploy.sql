create table user (
	id integer primary key not null,
	username varchar(40),
	password varchar(40),
	create_time datetime,
	update_time datetime
);