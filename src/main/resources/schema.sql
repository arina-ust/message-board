CREATE SCHEMA message_board;


CREATE SEQUENCE message_board.s_users_id START WITH 1;

CREATE TABLE message_board.users (
    id INT NOT NULL DEFAULT NEXT VALUE FOR message_board.s_users_id,
    username VARCHAR(50) NOT NULL,
    passwordHash VARCHAR(60) NOT NULL,

    CONSTRAINT pk_t_users PRIMARY KEY (id)
);

CREATE UNIQUE INDEX i_t_users_username ON message_board.users (username);


CREATE SEQUENCE message_board.s_messages_id START WITH 1;

CREATE TABLE message_board.messages (
    id INT NOT NULL DEFAULT NEXT VALUE FOR message_board.s_messages_id,
    user_id INT NOT NULL,
    title VARCHAR(4000) NOT NULL,
    text CLOB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT pk_t_messages PRIMARY KEY (id),
    CONSTRAINT fk_t_messages_user_id FOREIGN KEY (user_id) REFERENCES message_board.users(id)
);


