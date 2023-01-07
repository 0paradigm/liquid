ALTER TABLE `t_ds_user`
ADD COLUMN `phone` text;

ALTER TABLE `t_ds_repo`
ADD COLUMN  `description` text;

ALTER TABLE `t_ds_repo`
ADD COLUMN  `language`    text;

ALTER TABLE `t_ds_repo`
CHANGE COLUMN `private` `privated` bool NOT NULL DEFAULT FALSE;