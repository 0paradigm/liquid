INSERT INTO `tenant_info` (`kp`, `tenant_id`, `tenant_name`, `gmt_create`, `gmt_modified`)
VALUES ('1', 'liquid-media', 'liquid-media',
        UNIX_TIMESTAMP(STR_TO_DATE('2022-10-01', '%Y-%m-%d')),
        UNIX_TIMESTAMP(STR_TO_DATE('2022-10-01', '%Y-%m-%d')));