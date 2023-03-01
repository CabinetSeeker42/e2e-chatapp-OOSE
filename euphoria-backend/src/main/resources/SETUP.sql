/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/12/2022 09:54:50                          */
/*==============================================================*/

drop
    database if exists jdi;

create
    database jdi;

use
    jdi;

/*==============================================================*/
/* Table: BEDRIJF                                               */
/*==============================================================*/
create table BEDRIJF
(
    BEDRIJFID   varchar(128) not null,
    BEDRIJFNAAM varchar(128) DEFAULT 'UNKNOWN',
    primary key (BEDRIJFID)
);

/*==============================================================*/
/* Table: BEDRIJF_SUPPORT                                       */
/*==============================================================*/
create table BEDRIJF_SUPPORT
(
    GEBRUIKERID varchar(128) not null,
    BEDRIJFID   varchar(128) not null,
    primary key (GEBRUIKERID, BEDRIJFID)
);

/*==============================================================*/
/* Table: BERICHT                                               */
/*==============================================================*/
create table BERICHT
(
    BERICHTID             int          not null AUTO_INCREMENT,
    VERZENDERGEBRUIKERID  varchar(128) not null,
    ONTVANGERGEBRUIKERID  varchar(128) not null,
    BERICHTINHOUD         longtext null,
    BERICHTVERSTUURDDATUM datetime     not null,
    BERICHTGELEZENDATUM   datetime,
    BERICHTBIJLAGEID      varchar(128) null,
    BERICHTISOMROEP       boolean default false,
    primary key (BERICHTID)
);

/*==============================================================*/
/* Table: BIJLAGE                                               */
/*==============================================================*/
create table BIJLAGE
(
    BIJLAGEID      varchar(128) null unique default (uuid()),
    BIJLAGETYPE    varchar(256) null,
    BIJLAGENAAM    varchar(256) not null,
    BIJLAGECONTENT longtext     not null
);

/*==============================================================*/
/* Table: GEBRUIKER                                             */
/*==============================================================*/
create table GEBRUIKER
(
    GEBRUIKERID        varchar(128) not null,
    GEBRUIKERBEDRIJFID varchar(128) not null,
    GEBRUIKERPUBLICKEY longtext     not null,
    GEBRUIKERNAAM      varchar(128) not null default 'UNKNOWN',
    GEBRUIKERISSUPPORT bool                  default false,
    primary key (GEBRUIKERID)
);

alter table BEDRIJF_SUPPORT
    add constraint FK_BEDRIJF_SUPPORT foreign key (GEBRUIKERID)
        references GEBRUIKER (GEBRUIKERID) on delete cascade on update cascade;

alter table BEDRIJF_SUPPORT
    add constraint FK_BEDRIJF_SUPPORT2 foreign key (BEDRIJFID)
        references BEDRIJF (BEDRIJFID) on delete cascade on update cascade;

alter table BERICHT
    add constraint FK_BERICHT_ATTACHMENT foreign key (BERICHTBIJLAGEID)
        references BIJLAGE (BIJLAGEID) on delete cascade on update cascade;

alter table BERICHT
    add constraint FK_BERICHT_ONTVANGER foreign key (ONTVANGERGEBRUIKERID)
        references GEBRUIKER (GEBRUIKERID) on delete cascade on update cascade;

alter table BERICHT
    add constraint FK_BERICHT_VERZENDER foreign key (VERZENDERGEBRUIKERID)
        references GEBRUIKER (GEBRUIKERID) on delete cascade on update cascade;

alter table BERICHT
    add constraint FK_BERICHT_BIJLAGE foreign key (BERICHTBIJLAGEID)
        references BIJLAGE (BIJLAGEID) on delete cascade on update cascade;

alter table GEBRUIKER
    add constraint FK_GEBRUIKER_BEDRIJF foreign key (GEBRUIKERBEDRIJFID)
        references BEDRIJF (BEDRIJFID) on delete cascade on update cascade;

