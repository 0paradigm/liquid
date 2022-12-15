CREATE TABLE `t_ds_user`
(
    `id`               int PRIMARY KEY AUTO_INCREMENT,
    `login`            varchar(20) UNIQUE NOT NULL,
    `name`             varchar(30),
    `email`            text,
    `twitter_username` varchar(20),
    `bio`              varchar(200),
    `location`         varchar(100),
    `company`          varchar(50),

    `password`         text               NOT NULL,
--         CHECK (`password` REGEXP '^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[\s\S]{8,}$'),
--         COMMENT 'at least 8 characters, 1 upper case, 1 lower case, and 1 number',

    `created_at`       bigint             NOT NULL,
    `updated_at`       bigint             NOT NULL
);

CREATE INDEX `idx__t_ds_user__login` ON `t_ds_user` (`login`);
