INSERT INTO `tenant_info` (`kp`, `tenant_id`, `tenant_name`, `tenant_desc`, `gmt_create`, `gmt_modified`)
VALUES ('1', 'liquid', 'liquid',
        'liquid configurations',
        UNIX_TIMESTAMP(STR_TO_DATE('2022-10-01', '%Y-%m-%d')),
        UNIX_TIMESTAMP(STR_TO_DATE('2022-10-01', '%Y-%m-%d'))),
       ('2', 'liquid-dubbo', 'liquid-dubbo',
        'liquid dubbo registry',
        UNIX_TIMESTAMP(STR_TO_DATE('2022-10-01', '%Y-%m-%d')),
        UNIX_TIMESTAMP(STR_TO_DATE('2022-10-01', '%Y-%m-%d')));

