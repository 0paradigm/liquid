INSERT INTO `t_ds_user` (`login`, `name`, `email`, `twitter_username`,
                         `bio`, `location`, `company`,
                         `created_at`, `updated_at`,
                         `password`)

VALUES ('admin', 'Administrator', 'admin@liquid.com', 'liquid_cn',
        'coder', 'CA', '@liquid',
        1664478281845, 1664478281848,
        '123'),

       ('foo', NULL, NULL, NULL,
        ' ', NULL, 'myCompany',
        1664478281848, 1664478281858,
        'abc');
