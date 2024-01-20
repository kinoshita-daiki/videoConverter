drop table IF EXISTS VIDEO_META;

create table VIDEO_META (
    ID serial primary key,
    FILE_NAME_FROM_CLIENT varchar not null,
    OUTPUT_FILE_NAME varchar not null,
    EXPIRED_DATE_TIME timestamp not null);
