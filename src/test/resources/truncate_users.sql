SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE message_board.users;

ALTER SEQUENCE message_board.s_users_id RESTART WITH 1;

SET REFERENTIAL_INTEGRITY TRUE;
