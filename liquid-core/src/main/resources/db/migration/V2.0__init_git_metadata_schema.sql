CREATE TABLE `t_ds_repo`
(
    `id`          int PRIMARY KEY AUTO_INCREMENT,
    `owner`       int         NOT NULL,
    `name`        varchar(20) NOT NULL,

    `forked_from` int,

    CONSTRAINT `fk__ds_repo__owner_id` FOREIGN KEY (`owner`) REFERENCES `t_ds_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__ds_repo__fork_id` FOREIGN KEY (`forked_from`) REFERENCES `t_ds_repo` (`id`) ON DELETE SET NULL,
    CONSTRAINT `uniq__ds_repo__name` UNIQUE (`owner`, `name`)
);


CREATE TABLE `t_rel_repo_auth`
(
    `repo`     int  NOT NULL,
    `user`     int  NOT NULL,

    `read`     bool NOT NULL DEFAULT FALSE,
--      COMMENT 'read + clone',
    `manage`   bool NOT NULL DEFAULT FALSE,
--      COMMENT 'manage issues + pulls',
    `push`     bool NOT NULL DEFAULT FALSE,
    `settings` bool NOT NULL DEFAULT FALSE,
--      COMMENT 'manage settings',
    `admin`    bool NOT NULL DEFAULT FALSE,
--      COMMENT 'add collaborators + delete',

    CONSTRAINT `fk__rel_repo_auth__repo_id` FOREIGN KEY (`repo`) REFERENCES `t_ds_repo` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__rel_repo_auth__user_id` FOREIGN KEY (`user`) REFERENCES `t_ds_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__rel_repo_auth__record` UNIQUE (`repo`, `user`)
);


CREATE TABLE `t_rel_user_star`
(
    `repo` int NOT NULL,
    `user` int NOT NULL,

    CONSTRAINT `fk__rel_user_star__repo_id` FOREIGN KEY (`repo`) REFERENCES `t_ds_repo` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__rel_user_star__user_id` FOREIGN KEY (`user`) REFERENCES `t_ds_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__rel_user_star__record` UNIQUE (`repo`, `user`)
);


CREATE TABLE `t_rel_user_watch`
(
    `repo`            int  NOT NULL,
    `user`            int  NOT NULL,

    `participation`   bool NOT NULL DEFAULT TRUE,
    `issues`          bool NOT NULL DEFAULT FALSE,
    `pulls`           bool NOT NULL DEFAULT FALSE,
    `releases`        bool NOT NULL DEFAULT FALSE,
    `discussions`     bool NOT NULL DEFAULT FALSE,
    `security_alerts` bool NOT NULL DEFAULT FALSE,

    CONSTRAINT `fk__rel_user_watch__repo_id` FOREIGN KEY (`repo`) REFERENCES `t_ds_repo` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__rel_user_watch__user_id` FOREIGN KEY (`user`) REFERENCES `t_ds_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__rel_user_watch__record` UNIQUE (`repo`, `user`)
);



CREATE TABLE `t_ds_issue`
(
    `id`         int PRIMARY KEY AUTO_INCREMENT,
    `display_id` int    NOT NULL,
    `repo`       int    NOT NULL,
    `opener`     int    NOT NULL,

    `created_at` bigint NOT NULL,
    `closed`    bool NOT NULL DEFAULT FALSE,

    CONSTRAINT `uniq__ds_issue__display_id` UNIQUE (`repo`, `display_id`)
);


CREATE TABLE `t_rel_issue_assignees`
(
    `issue`    int NOT NULL,
    `assignee` int NOT NULL,

    CONSTRAINT `fk__rel_issue_assignees__repo_id` FOREIGN KEY (`issue`) REFERENCES `t_ds_issue` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__rel_issue_assignees__user_id` FOREIGN KEY (`assignee`) REFERENCES `t_ds_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__rel_issue_assignees__record` UNIQUE (`issue`, `assignee`)
);


CREATE TABLE `t_ds_issue_label`
(
    `id`          int PRIMARY KEY AUTO_INCREMENT,
    `repo`        int         NOT NULL,
    `name`        varchar(50) NOT NULL,
    `color`       varchar(20) NOT NULL DEFAULT '#FFFFFF',
    `description` text,

    CONSTRAINT `fk__ds_issue_label__repo` FOREIGN KEY (`repo`) REFERENCES `t_ds_repo` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__ds_issue_label__name` UNIQUE (`repo`, `name`)
);


CREATE TABLE `t_rel_issue_labels`
(
    `issue` int NOT NULL,
    `label` int NOT NULL,

    CONSTRAINT `fk__rel_issue_labels__issue` FOREIGN KEY (`issue`) REFERENCES `t_ds_issue` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__rel_issue_labels__label` FOREIGN KEY (`label`) REFERENCES `t_ds_issue_label` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__rel_issue_labels__record` UNIQUE (`issue`, `label`)
);

CREATE TABLE `t_ds_repo_milestone`
(
    `id`          int PRIMARY KEY AUTO_INCREMENT,
    `repo`        int         NOT NULL,
    `name`        varchar(50) NOT NULL,
    `description` text,

    `due_at`      bigint,
    `closed`      bool NOT NULL DEFAULT FALSE,

    CONSTRAINT `fk__ds_repo_milestone__repo` FOREIGN KEY (`repo`) REFERENCES `t_ds_repo` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__ds_repo_milestone__name` UNIQUE (`repo`, `name`)
);

CREATE TABLE `t_rel_issue_milestone`
(
    `issue`     int NOT NULL,
    `milestone` int NOT NULL,

    CONSTRAINT `fk__rel_issue_milestone__issue` FOREIGN KEY (`issue`) REFERENCES `t_ds_issue` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk__rel_issue_milestone__milestone` FOREIGN KEY (`milestone`) REFERENCES `t_ds_repo_milestone` (`id`) ON DELETE CASCADE,
    CONSTRAINT `uniq__rel_issue_milestone__record` UNIQUE (`issue`, `milestone`)
);


-- TODO: t_ds_repo_project
-- TODO: t_rel_issue_projects

