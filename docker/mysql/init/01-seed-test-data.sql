-- Password for created test accounts: test1234!

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS quiz_options;
DROP TABLE IF EXISTS quizzes;
DROP TABLE IF EXISTS neuron_likes;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS neurons;
DROP TABLE IF EXISTS brain_waitings;
DROP TABLE IF EXISTS brain_members;
DROP TABLE IF EXISTS brain_topics;
DROP TABLE IF EXISTS merge_brains;
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

-- 유저
CREATE TABLE users (
    uid        BINARY(16)   NOT NULL,
    email      VARCHAR(100) NOT NULL,
    role       ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    name       VARCHAR(45)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6)  NULL,
	
	available_email VARCHAR(100) GENERATED ALWAYS AS (IF(deleted_at IS NULL, email, NULL)) VIRTUAL,
	UNIQUE KEY uk_users_email (available_email),
    PRIMARY KEY (uid)
);

-- 뇌
CREATE TABLE brains (
    bid         INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200) NOT NULL,
    join_policy ENUM('PUBLIC', 'PROTECTED') NOT NULL DEFAULT 'PROTECTED',
    is_merged   TINYINT(1)   NOT NULL DEFAULT FALSE,
    created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (bid)
);

-- 주제
CREATE TABLE topics (
    tid        INT          NOT NULL AUTO_INCREMENT,
    pid        INT          NULL,
    name       VARCHAR(100) NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (tid),
    UNIQUE KEY uk_topics_pid_name (pid, name),
    CONSTRAINT fk_topics_parent FOREIGN KEY (pid) REFERENCES topics (tid)
);

-- 뇌에서 사용하는 주제
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

-- 뇌에 속한 멤버들
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

-- 뇌에 가입 신청한 멤버
CREATE TABLE brain_waitings (
    bid        INT         NOT NULL,
    uid        BINARY(16)  NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    PRIMARY KEY (bid, uid),
    CONSTRAINT fk_brain_waitings_brains FOREIGN KEY (bid) REFERENCES brains (bid) ON DELETE CASCADE,
    CONSTRAINT fk_brain_waitings_users FOREIGN KEY (uid) REFERENCES users (uid) ON DELETE CASCADE
);

CREATE TABLE merge_brains (
	mainid	 INT NOT NULL,
	memberid INT NOT NULL,
    
	PRIMARY KEY (mainid, memberid),
	CONSTRAINT fk_merge_brains_brains_member FOREIGN KEY (mainid) REFERENCES brains (bid) ON DELETE CASCADE,
	CONSTRAINT fk_merge_brains_brains_main FOREIGN KEY (memberid) REFERENCES brains (bid) ON DELETE CASCADE
);

-- 뉴런
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

-- 댓글/답글
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

-- 뉴런에 대한 좋아요
CREATE TABLE neuron_likes (
    uid        BINARY(16)  NOT NULL,
    nid        INT         NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    PRIMARY KEY (uid, nid),
    CONSTRAINT fk_neuron_likes_users FOREIGN KEY (uid) REFERENCES users (uid) ON DELETE CASCADE,
    CONSTRAINT fk_neuron_likes_neurons FOREIGN KEY (nid) REFERENCES neurons (nid) ON DELETE CASCADE
);

-- 퀴즈
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

-- 퀴즈 옵션
CREATE TABLE quiz_options (
    qid            INT          NOT NULL,
    option_content VARCHAR(255) NOT NULL,
    is_correct     TINYINT(1)   NOT NULL,

    CONSTRAINT fk_quiz_options_quizzes FOREIGN KEY (qid) REFERENCES quizzes (qid) ON DELETE CASCADE
);

INSERT INTO users (uid, email, role, name, password) VALUES
    (UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'admin@example.com', 'ADMIN', 'Test Admin', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 'user@example.com', 'USER', 'Test User', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('10000000-0000-4000-8000-000000000001', '-', '')), 'gumi3-user1@example.com', 'USER', '구미_3반_유저1', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('10000000-0000-4000-8000-000000000002', '-', '')), 'gumi3-user2@example.com', 'USER', '구미_3반_유저2', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('10000000-0000-4000-8000-000000000003', '-', '')), 'gumi3-user3@example.com', 'USER', '구미_3반_유저3', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('10000000-0000-4000-8000-000000000004', '-', '')), 'gumi3-user4@example.com', 'USER', '구미_3반_유저4', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('10000000-0000-4000-8000-000000000005', '-', '')), 'gumi3-user5@example.com', 'USER', '구미_3반_유저5', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('20000000-0000-4000-8000-000000000001', '-', '')), 'seoul5-user1@example.com', 'USER', '서울_5반_유저1', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('20000000-0000-4000-8000-000000000002', '-', '')), 'seoul5-user2@example.com', 'USER', '서울_5반_유저2', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('20000000-0000-4000-8000-000000000003', '-', '')), 'seoul5-user3@example.com', 'USER', '서울_5반_유저3', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('20000000-0000-4000-8000-000000000004', '-', '')), 'seoul5-user4@example.com', 'USER', '서울_5반_유저4', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('20000000-0000-4000-8000-000000000005', '-', '')), 'seoul5-user5@example.com', 'USER', '서울_5반_유저5', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('30000000-0000-4000-8000-000000000001', '-', '')), 'daejeon1-user1@example.com', 'USER', '대전_1반_유저1', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('30000000-0000-4000-8000-000000000002', '-', '')), 'daejeon1-user2@example.com', 'USER', '대전_1반_유저2', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('30000000-0000-4000-8000-000000000003', '-', '')), 'daejeon1-user3@example.com', 'USER', '대전_1반_유저3', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('30000000-0000-4000-8000-000000000004', '-', '')), 'daejeon1-user4@example.com', 'USER', '대전_1반_유저4', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('30000000-0000-4000-8000-000000000005', '-', '')), 'daejeon1-user5@example.com', 'USER', '대전_1반_유저5', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW');

INSERT INTO topics (tid, pid, name) VALUES
    (1000, NULL, 'Java'),
    (1010, 1000, 'Java 기초'),
    (1011, 1010, '변수와 타입'),
    (1012, 1010, '연산자'),
    (1013, 1010, '제어문'),
    (1014, 1010, '배열'),
    (1020, 1000, 'OOP'),
    (1021, 1020, '캡슐화'),
    (1022, 1020, '상속'),
    (1023, 1020, '다형성'),
    (1024, 1020, '추상화'),
    (1025, 1020, '인터페이스'),
    (1030, 1000, 'Collection'),
    (1031, 1030, 'ArrayList'),
    (1032, 1030, 'LinkedList'),
    (1033, 1030, 'HashMap'),
    (1034, 1030, 'HashSet'),
    (1035, 1030, 'Queue'),
    (1036, 1030, 'PriorityQueue'),
    (1040, 1000, 'Exception'),
    (1041, 1040, 'Checked Exception'),
    (1042, 1040, 'Unchecked Exception'),
    (1043, 1040, 'try-catch-finally'),
    (1050, 1000, 'I/O'),
    (1051, 1050, 'Stream'),
    (1052, 1050, 'Reader Writer'),
    (1053, 1050, 'Serialization'),
    (1060, 1000, 'JVM'),
    (1061, 1060, 'Memory Area'),
    (1062, 1060, 'Garbage Collection'),
    (1063, 1060, 'Class Loader'),
    (1070, 1000, 'Generic'),
    (1071, 1070, 'Type Parameter'),
    (1072, 1070, 'Wildcard'),
    (1080, 1000, 'Lambda Stream API'),
    (1081, 1080, 'Functional Interface'),
    (1082, 1080, 'Stream Pipeline'),
    (2000, NULL, '알고리즘'),
    (2010, 2000, '시간복잡도'),
    (2011, 2010, 'Big-O'),
    (2012, 2010, '공간복잡도'),
    (2020, 2000, 'Array'),
    (2021, 2020, 'Prefix Sum'),
    (2022, 2020, 'Two Pointer'),
    (2023, 2020, 'Sliding Window'),
    (2030, 2000, 'String'),
    (2031, 2030, 'KMP'),
    (2032, 2030, '문자열 해싱'),
    (2040, 2000, 'Stack Queue'),
    (2041, 2040, 'Stack'),
    (2042, 2040, 'Queue'),
    (2043, 2040, 'Deque'),
    (2050, 2000, 'Tree'),
    (2051, 2050, 'Binary Tree'),
    (2052, 2050, 'Segment Tree'),
    (2053, 2050, 'Fenwick Tree'),
    (2060, 2000, 'Graph'),
    (2061, 2060, 'DFS'),
    (2062, 2060, 'BFS'),
    (2063, 2060, 'Topological Sort'),
    (2070, 2000, 'Shortest Path'),
    (2071, 2070, 'Dijkstra'),
    (2072, 2070, 'Bellman-Ford'),
    (2073, 2070, 'Floyd-Warshall'),
    (2080, 2000, 'DP'),
    (2081, 2080, 'Memoization'),
    (2082, 2080, 'Tabulation'),
    (2083, 2080, 'Knapsack'),
    (2084, 2080, 'LIS'),
    (2090, 2000, 'Greedy'),
    (2091, 2090, 'Activity Selection'),
    (2092, 2090, 'Huffman Coding'),
    (2100, 2000, 'Backtracking'),
    (2101, 2100, 'Permutation'),
    (2102, 2100, 'Combination'),
    (2103, 2100, 'N-Queen'),
    (2110, 2000, 'Disjoint Set'),
    (2111, 2110, 'Union Find'),
    (2112, 2110, 'Path Compression'),
    (3000, NULL, '웹'),
    (3010, 3000, 'HTTP'),
    (3011, 3010, 'Request Response'),
    (3012, 3010, 'Status Code'),
    (3013, 3010, 'Header Cookie'),
    (3020, 3000, 'REST API'),
    (3021, 3020, 'Resource URI'),
    (3022, 3020, 'HTTP Method'),
    (3023, 3020, 'Idempotency'),
    (3030, 3000, 'HTML CSS JavaScript'),
    (3031, 3030, 'Semantic HTML'),
    (3032, 3030, 'CSS Flex Grid'),
    (3033, 3030, 'DOM Event'),
    (3040, 3000, 'Frontend Vue'),
    (3041, 3040, 'Vue Component'),
    (3042, 3040, 'Props Emits'),
    (3043, 3040, 'Composition API'),
    (3044, 3040, 'Pinia'),
    (3045, 3040, 'Vue Router'),
    (3046, 3040, 'Axios'),
    (3050, 3000, 'Backend Spring'),
    (3051, 3050, 'Spring MVC'),
    (3052, 3050, 'Controller Service Repository'),
    (3053, 3050, 'Spring Boot'),
    (3054, 3050, 'Spring Security'),
    (3055, 3050, 'Validation'),
    (3056, 3050, 'Exception Handler'),
    (3060, 3050, 'JPA'),
    (3061, 3060, 'Entity Mapping'),
    (3062, 3060, 'Persistence Context'),
    (3063, 3060, 'Dirty Checking'),
    (3064, 3060, 'Lazy Loading'),
    (3065, 3060, 'N+1 Problem'),
    (4000, NULL, 'DB'),
    (4010, 4000, 'RDBMS'),
    (4011, 4010, 'Table Relation'),
    (4012, 4010, 'Primary Foreign Key'),
    (4020, 4000, 'SQL'),
    (4021, 4020, 'SELECT'),
    (4022, 4020, 'JOIN'),
    (4023, 4020, 'GROUP BY HAVING'),
    (4024, 4020, 'Subquery'),
    (4030, 4000, 'Modeling'),
    (4031, 4030, 'ERD'),
    (4032, 4030, 'Normalization'),
    (4033, 4030, 'Denormalization'),
    (4040, 4000, 'Index'),
    (4041, 4040, 'B-Tree'),
    (4042, 4040, 'Covering Index'),
    (4043, 4040, 'Cardinality'),
    (4050, 4000, 'Transaction'),
    (4051, 4050, 'ACID'),
    (4052, 4050, 'Isolation Level'),
    (4053, 4050, 'MVCC'),
    (4054, 4050, 'Lock'),
    (5000, NULL, 'AI'),
    (5010, 5000, 'Machine Learning'),
    (5011, 5010, 'Supervised Learning'),
    (5012, 5010, 'Unsupervised Learning'),
    (5013, 5010, 'Train Validation Test'),
    (5020, 5000, 'Deep Learning'),
    (5021, 5020, 'Neural Network'),
    (5022, 5020, 'Backpropagation'),
    (5023, 5020, 'CNN'),
    (5024, 5020, 'RNN'),
    (5030, 5000, 'NLP'),
    (5031, 5030, 'Tokenization'),
    (5032, 5030, 'Embedding'),
    (5033, 5030, 'Transformer'),
    (5040, 5000, 'Generative AI'),
    (5041, 5040, 'Prompt Engineering'),
    (5042, 5040, 'RAG'),
    (5043, 5040, 'Vector Database');

INSERT INTO brains (bid, name, description, join_policy) VALUES
    (1, '구미 3반', 'SSAFY 구미 3반 Java 트랙 커리큘럼 학습 브레인', 'PUBLIC'),
    (2, '서울 5반', 'SSAFY 서울 5반 웹 프로젝트와 알고리즘 정리 브레인', 'PUBLIC'),
    (3, '대전 1반', 'SSAFY 대전 1반 전공 Java반 복습 브레인', 'PROTECTED');

INSERT INTO brain_members (bid, uid, role) VALUES
    (1, UNHEX(REPLACE('10000000-0000-4000-8000-000000000001', '-', '')), 'ADMIN'),
    (1, UNHEX(REPLACE('10000000-0000-4000-8000-000000000002', '-', '')), 'MANAGER'),
    (1, UNHEX(REPLACE('10000000-0000-4000-8000-000000000003', '-', '')), 'USER'),
    (1, UNHEX(REPLACE('10000000-0000-4000-8000-000000000004', '-', '')), 'USER'),
    (1, UNHEX(REPLACE('10000000-0000-4000-8000-000000000005', '-', '')), 'USER'),
    (2, UNHEX(REPLACE('20000000-0000-4000-8000-000000000001', '-', '')), 'ADMIN'),
    (2, UNHEX(REPLACE('20000000-0000-4000-8000-000000000002', '-', '')), 'MANAGER'),
    (2, UNHEX(REPLACE('20000000-0000-4000-8000-000000000003', '-', '')), 'USER'),
    (2, UNHEX(REPLACE('20000000-0000-4000-8000-000000000004', '-', '')), 'USER'),
    (2, UNHEX(REPLACE('20000000-0000-4000-8000-000000000005', '-', '')), 'USER'),
    (3, UNHEX(REPLACE('30000000-0000-4000-8000-000000000001', '-', '')), 'ADMIN'),
    (3, UNHEX(REPLACE('30000000-0000-4000-8000-000000000002', '-', '')), 'MANAGER'),
    (3, UNHEX(REPLACE('30000000-0000-4000-8000-000000000003', '-', '')), 'USER'),
    (3, UNHEX(REPLACE('30000000-0000-4000-8000-000000000004', '-', '')), 'USER'),
    (3, UNHEX(REPLACE('30000000-0000-4000-8000-000000000005', '-', '')), 'USER');

INSERT INTO brain_waitings (bid, uid) VALUES
    (3, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', ''))),
    (3, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')));

INSERT INTO brain_topics (bid, tid, generate_count)
SELECT b.bid, t.tid, CASE WHEN t.tid % 5 = 0 THEN 1 ELSE 0 END
FROM brains b
CROSS JOIN topics t
WHERE
    b.bid = 1
    OR (b.bid = 2 AND (
        t.tid BETWEEN 1000 AND 4999
        OR t.tid IN (5000, 5010, 5011, 5013, 5030, 5031, 5040, 5041, 5042)
    ))
    OR (b.bid = 3 AND (
        t.tid BETWEEN 1000 AND 3999
        OR t.tid IN (4000, 4020, 4021, 4022, 4040, 4041, 4050, 4051, 4052, 5000, 5040, 5041)
    ))
ORDER BY b.bid, t.tid;

INSERT INTO neurons (btid, uid, title, content)
SELECT
    bt.btid,
    CASE
        WHEN bt.bid = 1 THEN UNHEX(REPLACE(CONCAT('10000000-0000-4000-8000-00000000000', ((bt.tid % 5) + 1)), '-', ''))
        WHEN bt.bid = 2 THEN UNHEX(REPLACE(CONCAT('20000000-0000-4000-8000-00000000000', ((bt.tid % 5) + 1)), '-', ''))
        ELSE UNHEX(REPLACE(CONCAT('30000000-0000-4000-8000-00000000000', ((bt.tid % 5) + 1)), '-', ''))
    END,
    CONCAT(t.name, ' 핵심 정리'),
    CONCAT(t.name, '는 SSAFY 전공 Java반 커리큘럼에서 반복해서 쓰이는 개념이다. 정의, 대표 예제, 주의할 점을 함께 정리한다.')
FROM brain_topics bt
JOIN topics t ON t.tid = bt.tid;

INSERT INTO neurons (btid, uid, title, content)
SELECT
    bt.btid,
    CASE
        WHEN bt.bid = 1 THEN UNHEX(REPLACE(CONCAT('10000000-0000-4000-8000-00000000000', (((bt.tid + 1) % 5) + 1)), '-', ''))
        WHEN bt.bid = 2 THEN UNHEX(REPLACE(CONCAT('20000000-0000-4000-8000-00000000000', (((bt.tid + 1) % 5) + 1)), '-', ''))
        ELSE UNHEX(REPLACE(CONCAT('30000000-0000-4000-8000-00000000000', (((bt.tid + 1) % 5) + 1)), '-', ''))
    END,
    CONCAT(t.name, ' 실습 포인트'),
    CASE
        WHEN t.tid BETWEEN 1000 AND 1999 THEN CONCAT(t.name, '는 Java 코드로 직접 작성하면서 컴파일 오류와 런타임 동작 차이를 확인한다.')
        WHEN t.tid BETWEEN 2000 AND 2999 THEN CONCAT(t.name, '는 문제 유형, 상태 정의, 경계 조건을 먼저 적고 풀이를 구현한다.')
        WHEN t.tid BETWEEN 3000 AND 3999 THEN CONCAT(t.name, '는 요청 흐름과 계층 책임을 나누어 작은 예제로 검증한다.')
        WHEN t.tid BETWEEN 4000 AND 4999 THEN CONCAT(t.name, '는 샘플 테이블과 실행 계획을 보면서 성능 차이를 확인한다.')
        ELSE CONCAT(t.name, '는 입력 데이터와 모델 출력의 한계를 같이 기록한다.')
    END
FROM brain_topics bt
JOIN topics t ON t.tid = bt.tid
WHERE t.pid IS NOT NULL AND (t.tid % 3 <> 0);

INSERT INTO neurons (btid, uid, title, content)
SELECT
    bt.btid,
    CASE
        WHEN bt.bid = 1 THEN UNHEX(REPLACE(CONCAT('10000000-0000-4000-8000-00000000000', (((bt.tid + 2) % 5) + 1)), '-', ''))
        WHEN bt.bid = 2 THEN UNHEX(REPLACE(CONCAT('20000000-0000-4000-8000-00000000000', (((bt.tid + 2) % 5) + 1)), '-', ''))
        ELSE UNHEX(REPLACE(CONCAT('30000000-0000-4000-8000-00000000000', (((bt.tid + 2) % 5) + 1)), '-', ''))
    END,
    CONCAT(t.name, ' 면접 질문'),
    CONCAT(t.name, '를 설명할 때 용어 정의만 말하지 말고 언제 선택하고 언제 피하는지까지 답변할 수 있어야 한다.')
FROM brain_topics bt
JOIN topics t ON t.tid = bt.tid
WHERE t.pid IS NOT NULL AND (t.tid % 4 = 0);

INSERT INTO comments (nid, pid, uid, content, deleted_at)
SELECT
    n.nid,
    NULL,
    CASE
        WHEN bt.bid = 1 THEN UNHEX(REPLACE(CONCAT('10000000-0000-4000-8000-00000000000', (((n.nid + 2) % 5) + 1)), '-', ''))
        WHEN bt.bid = 2 THEN UNHEX(REPLACE(CONCAT('20000000-0000-4000-8000-00000000000', (((n.nid + 2) % 5) + 1)), '-', ''))
        ELSE UNHEX(REPLACE(CONCAT('30000000-0000-4000-8000-00000000000', (((n.nid + 2) % 5) + 1)), '-', ''))
    END,
    CASE
        WHEN n.nid % 4 = 0 THEN '예제 코드나 문제 번호를 같이 붙이면 복습할 때 더 빠르게 찾을 수 있을 듯.'
        WHEN n.nid % 4 = 1 THEN '이 개념은 관통 프로젝트에서 어디에 적용했는지도 적어두면 좋겠다.'
        WHEN n.nid % 4 = 2 THEN '헷갈리는 용어 비교표를 추가하면 팀원들이 보기 편할 것 같아.'
        ELSE '스터디 때 이 내용으로 5분 발표해도 괜찮겠다.'
    END,
    NULL
FROM neurons n
JOIN brain_topics bt ON bt.btid = n.btid
WHERE n.nid % 2 = 0;

INSERT INTO comments (nid, pid, uid, content, deleted_at)
SELECT
    c.nid,
    c.cid,
    CASE
        WHEN bt.bid = 1 THEN UNHEX(REPLACE(CONCAT('10000000-0000-4000-8000-00000000000', (((c.cid + 3) % 5) + 1)), '-', ''))
        WHEN bt.bid = 2 THEN UNHEX(REPLACE(CONCAT('20000000-0000-4000-8000-00000000000', (((c.cid + 3) % 5) + 1)), '-', ''))
        ELSE UNHEX(REPLACE(CONCAT('30000000-0000-4000-8000-00000000000', (((c.cid + 3) % 5) + 1)), '-', ''))
    END,
    '좋아. 다음 리뷰 때 예시 하나 더 붙여볼게.',
    NULL
FROM comments c
JOIN neurons n ON n.nid = c.nid
JOIN brain_topics bt ON bt.btid = n.btid
WHERE c.cid % 5 = 0;

INSERT INTO neuron_likes (uid, nid)
SELECT DISTINCT bm.uid, n.nid
FROM neurons n
JOIN brain_topics bt ON bt.btid = n.btid
JOIN brain_members bm ON bm.bid = bt.bid
WHERE ((n.nid + CAST(CONV(HEX(SUBSTRING(bm.uid, 16, 1)), 16, 10) AS UNSIGNED)) % 3) = 0;

ALTER TABLE topics AUTO_INCREMENT = 6000;
ALTER TABLE brains AUTO_INCREMENT = 4;
ALTER TABLE brain_topics AUTO_INCREMENT = 500;
ALTER TABLE neurons AUTO_INCREMENT = 900;
ALTER TABLE comments AUTO_INCREMENT = 600;
