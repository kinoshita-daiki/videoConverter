drop table IF EXISTS VIDEO_META;
create table VIDEO_META (
    ID serial primary key,
    FILE_NAME_FROM_CLIENT varchar not null,
    OUTPUT_FILE_NAME varchar not null,
    EXPIRED_DATE_TIME timestamp not null);
INSERT INTO video_meta(file_name_from_client , output_file_name , expired_date_time ) VALUES ( 'fileName1.mp4' , 'outputfile1.mp4' , '2024-02-17 17:00' );
INSERT INTO video_meta(file_name_from_client , output_file_name , expired_date_time ) VALUES ( 'fileName2.mp4' , 'outputfile2.mp4' , '2024-03-18 0:15' );
INSERT INTO video_meta(file_name_from_client , output_file_name , expired_date_time ) VALUES ( 'fileName3.mp4' , 'outputfile3.mp4' , '2024-03-17 23:59' );
INSERT INTO video_meta(file_name_from_client , output_file_name , expired_date_time ) VALUES ( 'fileName4.mp4' , 'outputfile4.mp4' , '2024-03-18 0:31' );