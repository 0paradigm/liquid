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
    `created_at`       bigint,
    `updated_at`       bigint,
    `password`         text
);
