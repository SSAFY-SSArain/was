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
    email      VARCHAR(100) NOT NULL UNIQUE,
    role       ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    name       VARCHAR(45)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    deleted_at DATETIME(6)  NULL,

    PRIMARY KEY (uid)
);

-- 뇌
CREATE TABLE brains (
    bid         INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(200) NOT NULL,
    join_policy ENUM('PUBLIC', 'PROTECTED') NOT NULL DEFAULT 'PROTECTED',
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
    (UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), 'owner1@example.com', 'USER', 'Seed Owner 1', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 'owner2@example.com', 'USER', 'Seed Owner 2', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'admin@example.com', 'ADMIN', 'Test Admin', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW'),
    (UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 'user@example.com', 'USER', 'Test User', '$2a$10$2LXV6rjXZ1KSt4LjyAXuge03bQ8PxeCr338ZX1QVy0zNs/IWWeVtW');

INSERT INTO topics (tid, pid, name) VALUES
    (1000, NULL, '알고리즘'),
    (1010, 1000, '그래프'),
    (1020, 1010, '최단 경로'),
    (1030, 1020, '다익스트라'),
    (1040, 1030, '우선순위 큐'),
    (1110, 1000, 'DP'),
    (1120, 1110, '배낭 문제'),
    (1130, 1120, '01-Knapsack'),
    (1140, 1130, '최적화'),
    (1210, 1000, '탐색 테이블'),
    (1220, 1210, '인덱스'),
    (2000, NULL, 'DB'),
    (2010, 2000, 'MySQL'),
    (2020, 2010, '인덱스'),
    (2030, 2020, 'BTree'),
    (2040, 2030, 'covering-index'),
    (2110, 2000, '트랜잭션'),
    (2120, 2110, '격리 수준'),
    (2130, 2120, 'Repeatable read'),
    (2140, 2130, 'MVCC'),
    (3000, NULL, 'Java'),
    (3010, 3000, 'JVM'),
    (3020, 3010, '메모리 관리'),
    (3030, 3020, '힙'),
    (3040, 3030, 'GC-가비지컬렉션'),
    (3110, 3000, 'Spring'),
    (3120, 3110, 'JPA'),
    (3130, 3120, 'Persistence-context'),
    (3140, 3130, 'dirty-check'),
    (3210, 3000, 'DB'),
    (3220, 3210, '트랜잭션');

INSERT INTO brains (bid, name, description, join_policy) VALUES
    (1, 'Owner1 알고리즘 브레인', 'Graph and DP interview notes', 'PROTECTED'),
    (2, 'Owner2 DB 브레인', 'MySQL index and transaction notes', 'PROTECTED'),
    (3, 'Admin Java 브레인', 'JVM and Spring JPA admin notes', 'PUBLIC'),
    (4, 'User 기타 브레인', 'Practical backend CS notes', 'PUBLIC');

INSERT INTO brain_members (bid, uid, role) VALUES
    (1, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), 'ADMIN'),
    (1, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'USER'),
    (2, UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 'ADMIN'),
    (2, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 'USER'),
    (3, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'ADMIN'),
    (4, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 'ADMIN');

INSERT INTO brain_topics (btid, bid, tid, generate_count) VALUES
    (1, 1, 1000, 0),
    (2, 1, 1030, 0),
    (3, 1, 1140, 0),
    (4, 2, 2000, 0),
    (5, 2, 2040, 0),
    (6, 2, 2140, 0),
    (7, 3, 3000, 0),
    (8, 3, 3040, 0),
    (9, 3, 3140, 0),
    (10, 4, 1020, 0),
    (11, 4, 2020, 0),
    (12, 4, 3120, 0),
    (13, 1, 1010, 0),
    (14, 1, 1020, 0),
    (15, 1, 1110, 0),
    (16, 1, 1120, 0),
    (17, 1, 1130, 0),
    (18, 2, 2010, 0),
    (19, 2, 2020, 0),
    (20, 2, 2030, 0),
    (21, 2, 2110, 0),
    (22, 2, 2120, 0),
    (23, 2, 2130, 0),
    (24, 3, 3010, 0),
    (25, 3, 3020, 0),
    (26, 3, 3030, 0),
    (27, 3, 3110, 0),
    (28, 3, 3120, 0),
    (29, 3, 3130, 0),
    (30, 4, 1000, 0),
    (31, 4, 1010, 0),
    (32, 4, 2000, 0),
    (33, 4, 2010, 0),
    (34, 4, 3000, 0),
    (35, 4, 3110, 0);

INSERT INTO neurons (nid, btid, uid, title, content) VALUES
    (1, 1, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '알고리즘 복잡도 읽기', '시간복잡도는 입력 크기 증가에 따른 실행 시간의 증가율을 비교하는 기준이다.'),
    (2, 2, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '다익스트라의 전제 조건', '다익스트라는 모든 간선 가중치가 음수가 아닐 때 최단 거리를 안정적으로 구한다.'),
    (3, 3, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '0/1 배낭 DP 상태', 'dp[i][w]는 i번째 물건까지 고려했을 때 무게 w에서 가능한 최대 가치를 뜻한다.'),
    (4, 4, UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 'DB 정규화 목적', '정규화는 중복 저장을 줄이고 수정 이상을 줄이기 위한 모델링 절차다.'),
    (5, 5, UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), '커버링 인덱스', '쿼리에 필요한 컬럼이 모두 인덱스에 있으면 테이블 접근을 줄일 수 있다.'),
    (6, 6, UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 'MVCC와 Repeatable Read', 'MVCC는 트랜잭션마다 일관된 스냅샷을 제공해 읽기 잠금을 줄인다.'),
    (7, 7, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'Java 타입 시스템', '기본형과 참조형은 저장 방식과 null 허용 여부에서 차이가 있다.'),
    (8, 8, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'Heap과 GC Root', 'GC는 GC Root에서 도달할 수 없는 객체를 회수 대상으로 판단한다.'),
    (9, 9, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'Dirty Checking', '영속성 컨텍스트는 엔티티 스냅샷과 현재 값을 비교해 변경 SQL을 만든다.'),
    (10, 10, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '그래프 탐색 선택', 'BFS는 간선 비용이 같은 최단 거리 문제에 적합하고 DFS는 경로 탐색에 자주 쓰인다.'),
    (11, 11, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '인덱스 선택도', '선택도가 높은 컬럼일수록 인덱스를 통한 필터링 효과가 크다.'),
    (12, 12, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 'JPA 지연 로딩', '지연 로딩은 연관 엔티티 접근 시점까지 조회를 미뤄 초기 쿼리 비용을 낮춘다.');

INSERT INTO comments (cid, nid, pid, uid, content, deleted_at) VALUES
    (1, 1, NULL, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '면접 답변용으로 O(n log n) 예시도 추가하면 좋겠다.', NULL),
    (2, 1, 1,    UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), '면접관이 이 글을 좋아하지 않습니다.', NULL),
    (3, 1, 1,    UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '정렬 알고리즘 한 번 찾아보세요.', NULL),
    (4, 1, 2,    UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '삭제된 댓글입니다.', NOW()),
    (5, 2, NULL, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), '음수 간선은 벨만-포드와 비교해서 정리해보자.', NULL),
    (6, 2, 5,    UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '좋은 접근이네.', NULL),
    (7, 2, 6,    UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '삭제된 댓글입니다.', NOW()),
    (8, 3, NULL, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '1차원 배열 최적화도 같이 보면 좋음.', NULL),
    (9, 3, 8,    UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '이름만 들어도 무시무시한걸?', NULL),
    (10, 5,  NULL, UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 'EXPLAIN 결과 예시를 붙이면 더 명확하다.', NULL),
    (11, 5,  NULL, UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), '최대한 인덱스 컬럼을 활용하자.', NULL),
    (12, 6,  NULL, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '팬텀 리드와 next-key lock도 연결해서 정리 필요.', NULL),
    (13, 8,  NULL, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'GC Root 종류를 별도 노드로 분리해도 될 듯.', NULL),
    (14, 9,  NULL, UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 'flush 시점과 commit 시점을 같이 설명하면 좋겠다.', NULL),
    (15, 11, NULL, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), '카디널리티와 선택도 차이를 예제로 보강하자.', NULL),
    (16, 12, NULL, UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 'N+1 문제로 이어지는 예시를 추가하자.', NULL);

INSERT INTO neuron_likes (uid, nid) VALUES
	(UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), 1),
	(UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), 3),
	(UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), 5),
	(UNHEX(REPLACE('5ab8561c-a697-4767-953a-ef6933e77007', '-', '')), 6),
	(UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 2),
	(UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 3),
	(UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 4),
	(UNHEX(REPLACE('11111111-1111-4111-8111-111111111111', '-', '')), 12),
	(UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 3),
	(UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 5),
	(UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 6),
	(UNHEX(REPLACE('22222222-2222-4222-8222-222222222222', '-', '')), 9),
    (UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 5),
    (UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 6),
    (UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 9),
    (UNHEX(REPLACE('9c665e0e-bed0-4d11-8eba-34b63cf2e137', '-', '')), 12);

ALTER TABLE topics AUTO_INCREMENT = 4000;
ALTER TABLE brains AUTO_INCREMENT = 5;
ALTER TABLE brain_topics AUTO_INCREMENT = 36;
ALTER TABLE neurons AUTO_INCREMENT = 13;
ALTER TABLE comments AUTO_INCREMENT = 17;
