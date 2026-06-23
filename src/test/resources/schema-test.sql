SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS quiz_options;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS neuron_likes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS neurons;
DROP TABLE IF EXISTS brain_waitings;
DROP TABLE IF EXISTS brain_members;
DROP TABLE IF EXISTS brain_topics;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS brains;
DROP TABLE IF EXISTS users;

DROP TABLE IF EXISTS quiz_option;
DROP TABLE IF EXISTS quiz;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS node;
DROP TABLE IF EXISTS brain_waiting;
DROP TABLE IF EXISTS brain_member;
DROP TABLE IF EXISTS brain_topic;
DROP TABLE IF EXISTS brain;
DROP TABLE IF EXISTS topic;
DROP TABLE IF EXISTS quiz_seq;
DROP TABLE IF EXISTS topic_seq;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
    uid        BINARY(16)   NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    role       ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    name       VARCHAR(45)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6)  NULL,

    PRIMARY KEY (uid)
);

CREATE TABLE brains (
    bid         INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200) NOT NULL,
    join_policy ENUM('PUBLIC', 'PROTECTED') NOT NULL DEFAULT 'PROTECTED',
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (bid)
);

CREATE TABLE topics (
    tid        INT          NOT NULL AUTO_INCREMENT,
    pid        INT          NULL,
    name       VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (tid),
    CONSTRAINT fk_topics_parent FOREIGN KEY (pid) REFERENCES topics (tid)
);

CREATE TABLE brain_topics (
    btid           INT         NOT NULL AUTO_INCREMENT,
    bid            INT         NOT NULL,
    tid            INT         NOT NULL,
    generate_count TINYINT     NOT NULL DEFAULT 0,
    created_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at     DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (btid),
    UNIQUE KEY uk_brain_topics_bid_tid (bid, tid),
    CONSTRAINT fk_brain_topics_brains FOREIGN KEY (bid) REFERENCES brains (bid) ON DELETE CASCADE,
    CONSTRAINT fk_brain_topics_topics FOREIGN KEY (tid) REFERENCES topics (tid) ON DELETE CASCADE
);

CREATE TABLE brain_members (
    bid        INT         NOT NULL,
    uid        BINARY(16)  NOT NULL,
    role       ENUM('USER', 'MANAGER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (bid, uid),
    CONSTRAINT fk_brain_members_brains FOREIGN KEY (bid) REFERENCES brains (bid) ON DELETE CASCADE,
    CONSTRAINT fk_brain_members_users FOREIGN KEY (uid) REFERENCES users (uid) ON DELETE CASCADE
);

CREATE TABLE brain_waitings (
    bid        INT         NOT NULL,
    uid        BINARY(16)  NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (bid, uid),
    CONSTRAINT fk_brain_waitings_brains FOREIGN KEY (bid) REFERENCES brains (bid) ON DELETE CASCADE,
    CONSTRAINT fk_brain_waitings_users FOREIGN KEY (uid) REFERENCES users (uid) ON DELETE CASCADE
);

CREATE TABLE neurons (
    nid        INT            NOT NULL AUTO_INCREMENT,
    btid       INT            NOT NULL,
    uid        BINARY(16)     NOT NULL,
    title      VARCHAR(100)   NOT NULL,
    content    VARCHAR(10000) NOT NULL,
    created_at DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (nid),
    CONSTRAINT fk_neurons_brain_topics FOREIGN KEY (btid) REFERENCES brain_topics (btid) ON DELETE CASCADE,
    CONSTRAINT fk_neurons_users FOREIGN KEY (uid) REFERENCES users (uid)
);

CREATE TABLE comments (
    cid        INT          NOT NULL AUTO_INCREMENT,
    nid        INT          NOT NULL,
    pid        INT          NULL,
    uid        BINARY(16)   NOT NULL,
    content    VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6)  NULL,

    PRIMARY KEY (cid),
    CONSTRAINT fk_comments_neurons FOREIGN KEY (nid) REFERENCES neurons (nid) ON DELETE CASCADE,
    CONSTRAINT fk_comments_parent FOREIGN KEY (pid) REFERENCES comments (cid) ON DELETE CASCADE,
    CONSTRAINT fk_comments_users FOREIGN KEY (uid) REFERENCES users (uid)
);

CREATE TABLE neuron_likes (
    uid        BINARY(16)  NOT NULL,
    nid        INT         NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (uid, nid),
    CONSTRAINT fk_neuron_likes_users FOREIGN KEY (uid) REFERENCES users (uid) ON DELETE CASCADE,
    CONSTRAINT fk_neuron_likes_neurons FOREIGN KEY (nid) REFERENCES neurons (nid) ON DELETE CASCADE
);

CREATE TABLE quizzes (
    qid         INT           NOT NULL AUTO_INCREMENT,
    btid        INT           NOT NULL,
    question    VARCHAR(500)  NOT NULL,
    explanation VARCHAR(1000) NOT NULL,
    created_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)   NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (qid),
    CONSTRAINT fk_quizzes_brain_topics FOREIGN KEY (btid) REFERENCES brain_topics (btid) ON DELETE CASCADE
);

CREATE TABLE quiz_options (
    qid            INT          NOT NULL,
    option_content VARCHAR(255) NOT NULL,
    is_correct     TINYINT(1)   NOT NULL,

    CONSTRAINT fk_quiz_options_quizzes FOREIGN KEY (qid) REFERENCES quizzes (qid) ON DELETE CASCADE
);
