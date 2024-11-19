#drop schema fitquest;

CREATE SCHEMA IF NOT EXISTS fitquest;

use fitquest;

-- User table
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    is_admin TINYINT DEFAULT 0,
    description varchar(20),
    INDEX idx_email (email)
);

-- Category table
CREATE TABLE category (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    is_public TINYINT DEFAULT 0,
    color VARCHAR(10) DEFAULT '#000000',
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Todo table
CREATE TABLE todo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    is_done TINYINT DEFAULT 0,
    content VARCHAR(100) NOT NULL,
    date DATE DEFAULT (CURRENT_DATE),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE CASCADE,
    INDEX idx_user_date (user_id, date)
);

-- Board table
CREATE TABLE board (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    tag VARCHAR(20) NOT NULL,
    date DATE DEFAULT (CURRENT_DATE),
    writer VARCHAR(50) NOT NULL,
    title VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    view_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_tag (user_id, tag)
);

-- Comment table
CREATE TABLE comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    board_id INT NOT NULL,
    user_id INT,
    writer VARCHAR(50) NOT NULL,
    content VARCHAR(500) NOT NULL,
    date DATE DEFAULT (CURRENT_DATE),
    parent_id INT,
    is_delete TINYINT DEFAULT 0,
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL,
    FOREIGN KEY (parent_id) REFERENCES comment(id) ON DELETE SET NULL,
    INDEX idx_board_id (board_id)
);

-- Article table
CREATE TABLE article (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(50) NOT NULL,
    link TEXT NOT NULL,
    description TEXT NOT NULL,
    date DATE NOT NULL,
    INDEX idx_date (date)
);

-- Token table
CREATE TABLE token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token TEXT NOT NULL,
    expiry_date DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_expiry (expiry_date)
);

-- Activity table
CREATE TABLE activity (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    date DATE NOT NULL,
    ratio DECIMAL(5,2) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_user_date (user_id, date)
);

-- Like table
CREATE TABLE hit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    board_id INT NOT NULL,
    user_id INT NOT NULL,
    FOREIGN KEY (board_id) REFERENCES board(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_board_user (board_id, user_id)
);

-- Title truncate trigger for article
DELIMITER //
CREATE TRIGGER before_article_insert 
BEFORE INSERT ON article
FOR EACH ROW
BEGIN
    IF LENGTH(NEW.title) > 50 THEN
        SET NEW.title = CONCAT(LEFT(NEW.title, 47), '...');
    END IF;
END//
DELIMITER ;

# user 가 새롭게 생성되면, 카테고리 1,카테고리 2,카테고리 3을 default로 생성함
DELIMITER //
CREATE TRIGGER create_default_categories
AFTER INSERT ON user
FOR EACH ROW
BEGIN
    -- 카테고리1 생성
    INSERT INTO category (user_id, title, is_public, color)
    VALUES (NEW.id, '어깨', 1, '#2857aa');
    
    -- 카테고리2 생성
    INSERT INTO category (user_id, title, is_public, color)
    VALUES (NEW.id, '등', 1, '#191919');
    
    -- 카테고리3 생성
    INSERT INTO category (user_id, title, is_public, color)
    VALUES (NEW.id, '가슴', 1, '#785cb4');
    
    -- 카테고리4 생성
    INSERT INTO category (user_id, title, is_public, color)
    VALUES (NEW.id, '하체', 1, '#1eae98');
    
    -- 카테고리5 생성
    INSERT INTO category (user_id, title, is_public, color)
    VALUES (NEW.id, '유산소', 1, '#cd295a');
END//
DELIMITER ;

DELIMITER ;

-- Title truncate trigger for board
DELIMITER //
CREATE TRIGGER before_board_insert 
BEFORE INSERT ON board
FOR EACH ROW
BEGIN
    IF LENGTH(NEW.title) > 50 THEN
        SET NEW.title = CONCAT(LEFT(NEW.title, 47), '...');
    END IF;
END//
DELIMITER ;

# 11/18 추가
# todo 순서 컬럼 추가
ALTER TABLE todo ADD COLUMN todo_order INT;

# 기존 데이터에 대해 임시로 순서 부여
UPDATE todo t1
JOIN (
    SELECT id, ROW_NUMBER() OVER (PARTITION BY category_id ORDER BY id) as rn
    FROM todo
) t2 ON t1.id = t2.id
SET t1.todo_order = t2.rn;


select * from activity;
select * from article;
select * from board;
select * from category;
select * from `comment`;
select * from hit;
select * from todo;
select * from token;
select * from `user`;