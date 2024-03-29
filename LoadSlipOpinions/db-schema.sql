create table opinionbase (id integer not null auto_increment, countreferringopinions integer not null, opiniondate date, page integer not null, volume integer not null, vset integer not null, title varchar(127), primary key (id)) engine=InnoDB;
create table opinionbase_opinioncitations (referringopinions_id integer not null, opinioncitations_id integer not null, primary key (referringopinions_id, opinioncitations_id)) engine=InnoDB;
create table opinionstatutecitation (countreferences integer not null, opinionbase_id integer not null, statutecitation_id integer not null, primary key (opinionbase_id, statutecitation_id)) engine=InnoDB;
create table partyattorneypair (id integer not null auto_increment, attorney varchar(3070), party varchar(1022), slipproperties_slipopinion_id integer, primary key (id)) engine=InnoDB;
create table role (id bigint not null auto_increment, role varchar(255) not null, primary key (id)) engine=InnoDB;
create table slipproperties (author varchar(63), casecaption varchar(255), casecitation varchar(255), casetype varchar(15), completiondate datetime, county varchar(127), court varchar(15), date datetime, disposition varchar(127), dispositiondescription varchar(255), division varchar(31), fileextension varchar(7), filename varchar(31), filingdate datetime, participants varchar(255), publicationstatus varchar(31), summary varchar(4094), trialcourtcase varchar(63), trialcourtcasenumber varchar(63), trialcourtjudge varchar(127), trialcourtjudgmentdate datetime, trialcourtname varchar(127), slipopinion_id integer not null, primary key (slipopinion_id)) engine=InnoDB;
create table statutecitation (id integer not null auto_increment, designated bit not null, lawcode char(4), sectionnumber char(32), primary key (id)) engine=InnoDB;
create table user (id bigint not null auto_increment, createdate datetime, email varchar(255), emailupdates bit not null, firstname varchar(255), lastname varchar(255), locale varchar(255), optout bit not null, optoutkey varchar(255), password varchar(255), startverify bit not null, titles tinyblob, updatedate datetime, verified bit not null, verifycount integer not null, verifyerrors integer not null, verifykey varchar(255), welcomeerrors integer not null, welcomed bit not null, primary key (id)) engine=InnoDB;
create table user_roles (user_id bigint not null, roles_id bigint not null) engine=InnoDB;
create table caselistentry(id varchar(64), filename varchar(31), fileextension varchar(7), title varchar(137), opiniondate date, posteddate date, court varchar(255), searchurl varchar(128), status varchar(15) not null, primary key (id)) engine=InnoDB;
create table slipopinionlist(id bigint not null, updatetime timestamp, primary key (id)) engine=InnoDB;
create table opinionview(id bigint not null auto_increment, opiniondate date, opinionview BLOB, primary key (id)) engine=InnoDB;
create index IDXd587qslmmirn7juop20is6gwt on opinionbase (vset, volume, page);
alter table role add constraint UK_bjxn5ii7v7ygwx39et0wawu0q unique (role);
create index IDX13npyxqldj3ydwfdy3o7x2vit on statutecitation (lawcode, sectionnumber);
alter table user add constraint UKob8kqyqqgmefl0aco34akdtpe unique (email);
alter table opinionbase_opinioncitations add constraint FKta62twx4b3feuk54vu2akt219 foreign key (opinioncitations_id) references opinionbase (id);
alter table opinionbase_opinioncitations add constraint FK1p0gqi75xi3ix760cxkdym84e foreign key (referringopinions_id) references opinionbase (id);
alter table opinionstatutecitation add constraint FKj4m7y817yxpioeb629x56h8u6 foreign key (opinionbase_id) references opinionbase (id);
alter table opinionstatutecitation add constraint FKfeousb71h1ayu6myk7wp7mgfc foreign key (statutecitation_id) references statutecitation (id);
alter table partyattorneypair add constraint FK8la3eiphk0wnmel0ail3winl4 foreign key (slipproperties_slipopinion_id) references slipproperties (slipopinion_id);
alter table slipproperties add constraint FK97edwcyxhia5mmhb1qqgury8o foreign key (slipopinion_id) references opinionbase (id);
alter table user_roles add constraint FKj9553ass9uctjrmh0gkqsmv0d foreign key (roles_id) references role (id);
alter table user_roles add constraint FK55itppkw3i07do3h7qoclqd4k foreign key (user_id) references user (id);
insert into slipopinionupdate(id, updatetime) values( 1, current_timestamp());
insert into slipopinionupdate(id, updatetime) values( 2, TIMESTAMP(DATE_SUB(DATE(DATE_ADD(NOW(),INTERVAL IF(WEEKDAY(NOW())=6,6,(5-WEEKDAY(NOW()))) DAY)), INTERVAL 7 DAY)));
insert into role (id, role) values(1, 'USER');
insert into role (id, role) values(2, 'ADMIN');
DELIMITER $$
CREATE PROCEDURE `checkSlipOpinionUpdate` ()
BEGIN
	DECLARE result TEXT;
    DECLARE updateNeeded INT;
    SELECT 'NOUPDATE' into result;
	START TRANSACTION;
	SELECT TIMESTAMPDIFF(MINUTE, (select updatetime from slipopinionlist where id=1), current_timestamp()) into updateNeeded;
	IF updateNeeded >= 3 THEN
	    SELECT 'UPDATE' into result;
        update slipopinionlist set updatetime = current_timestamp() where id = 1;
    END IF;
    COMMIT;
    SELECT RESULT;
END$$
CREATE PROCEDURE `checkEmailUser` ()
BEGIN
    DECLARE result TEXT;
    DECLARE updateNeeded INT;
    SELECT 'NOEMAIL' into result;
    START TRANSACTION;
    select TIMESTAMPDIFF(DAY, (select updatetime from slipopinionupdate where id=2), TIMESTAMP(DATE(DATE_ADD(NOW(),INTERVAL IF(WEEKDAY(NOW())=6,6,(5-WEEKDAY(NOW()))) DAY)))) into updateNeeded;
    IF updateNeeded >= 7 THEN
        SELECT 'EMAIL' into result;
        update slipopinionupdate set updatetime = TIMESTAMP(DATE(DATE_ADD(NOW(),INTERVAL IF(WEEKDAY(NOW())=6,6,(5-WEEKDAY(NOW()))) DAY))) where id = 2;
    END IF;
    COMMIT;
    SELECT RESULT;
END$$
DELIMITER ;