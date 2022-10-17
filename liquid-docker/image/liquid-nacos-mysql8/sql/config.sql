INSERT INTO `config_info` (`data_id`, `tenant_id`, `group_id`, `content`, `type`, `encrypted_data_key`)
VALUES (
'liquid-media.yaml', 'liquid', 'DEFAULT_GROUP',
'minio:
  endpoint: "http://liquid-minio:9000"
  root-user: liquid
  root-password: liquid_media_minio
  timeout: 10000
',
'yaml', ''
);
