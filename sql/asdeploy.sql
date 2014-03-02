-- user
DROP TABLE IF EXISTS "user";
CREATE TABLE "user" (
	id integer PRIMARY KEY AUTOINCREMENT,
	username varchar(40),
	password varchar(40),
	create_time datetime,
	update_time datetime
);

-- project
DROP TABLE IF EXISTS "project";
CREATE TABLE "project" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "name" varchar(30) NOT NULL,
    "war_name" varchar(30) NOT NULL
);

-- deploy_item
DROP TABLE IF EXISTS "deploy_item";
CREATE TABLE "deploy_item" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id" integer NOT NULL REFERENCES "user" ("id"),
    "project_id" integer NOT NULL REFERENCES "project" ("id"),
    "version" varchar(11) NOT NULL,
    "deploy_type" varchar(15) NOT NULL,
    "file_name" varchar(100) NOT NULL,
    "folder_path" varchar(512),
    "create_time" datetime NOT NULL,
    "update_time" datetime,
    "patch_group_id" integer);
CREATE INDEX "deploy_item_user_id" ON "deploy_item" ("user_id");
CREATE INDEX "deploy_item_project_id" ON "deploy_item" ("project_id");

-- deploy_record
DROP TABLE IF EXISTS "deploy_record";
CREATE TABLE "deploy_record" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id" integer NOT NULL REFERENCES "user" ("id"),
    "project_id" integer NOT NULL REFERENCES "project" ("id"),
    "deploy_item_id" integer REFERENCES "project_item" ("id"),
    "create_time" datetime NOT NULL,
    "status" varchar(30) NOT NULL,
    "is_conflict_with_others" bool NOT NULL
);
CREATE INDEX "deploy_record_user_id" ON "deploy_record" ("user_id");
CREATE INDEX "deploy_record_project_id" ON "deploy_record" ("project_id");
CREATE INDEX "deploy_record_item_id" ON "deploy_record" ("deploy_item_id");

-- deploy_lock
DROP TABLE IF EXISTS "deploy_lock";
CREATE TABLE "deploy_lock" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "user_id" integer NOT NULL REFERENCES "user" ("id"),
    "deploy_record_id" integer NOT NULL REFERENCES "deploy_record" ("id"),
    "is_locked" bool NOT NULL,
    "locked_time" datetime NOT NULL
);
CREATE INDEX "deploy_lock_user_id" ON "deploy_lock" ("user_id");
CREATE INDEX "deploy_lock_record_id" ON "deploy_lock" ("deploy_record_id");

----------------------------------------------

-- patch_group
DROP TABLE IF EXISTS "patch_group";
CREATE TABLE "patch_group" (
    "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
    "creator_id" integer NOT NULL REFERENCES "useer" ("id"),
    "project_id" integer NOT NULL REFERENCES "project" ("id"),
    "name" varchar(20) NOT NULL,
    "check_code" varchar(10) NOT NULL,
    "status" varchar(20) NOT NULL,
    "create_time" datetime NOT NULL,
    "finish_time" datetime
);
CREATE INDEX "patch_group_creator_id" ON "patch_group" ("creator_id");
CREATE INDEX "patch_group_project_id" ON "patch_group" ("project_id");



