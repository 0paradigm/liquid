CREATE TABLE `t_ds_v3_repo_contributors`
(
    `repo_id` int,
    `user_login` varchar(20),
    constraint `uniq__repo_contributors_repo_id` unique (`repo_id`, `user_login`)
);
