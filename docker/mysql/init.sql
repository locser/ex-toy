-- MySQL initialization script for x-toy application
-- This script will be executed when the MySQL container starts for the first time

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS java_demo;

-- Use the database
USE java_demo;

-- Create application user with proper privileges
CREATE USER IF NOT EXISTS 'appuser'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON java_demo.* TO 'appuser'@'%';
FLUSH PRIVILEGES;

-- Set timezone
SET GLOBAL time_zone = '+00:00';

-- Optimize MySQL for development
SET GLOBAL innodb_buffer_pool_size = 128 * 1024 * 1024; -- 128MB
SET GLOBAL max_connections = 200;

-- Log message
SELECT 'Database java_demo initialized successfully' AS message;


-- java_demo.disputes definition

CREATE TABLE `disputes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `status` int NOT NULL DEFAULT '0',
  `exchange_id` bigint NOT NULL DEFAULT '0',
  `reporter_id` bigint NOT NULL DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `reason` text NOT NULL,
  `resolution_details` text NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  CONSTRAINT `disputes_chk_1` CHECK ((`status` between 0 and 3))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.events definition

CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `version` bigint DEFAULT NULL,
  `status` int NOT NULL DEFAULT '0',
  `end_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `start_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `description` text NOT NULL,
  `name` varchar(255) NOT NULL,
  `rules` text NOT NULL,
  `theme` varchar(255) NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `type` int NOT NULL DEFAULT '1',
  `total_toys` int DEFAULT '0',
  `available_toys` int DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `events_chk_1` CHECK ((`status` between 0 and 4))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.exchange_messages definition

CREATE TABLE `exchange_messages` (
  `exchange_id` bigint NOT NULL DEFAULT '0',
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL DEFAULT '0',
  `sent_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint DEFAULT NULL,
  `message_text` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.exchanges definition

CREATE TABLE `exchanges` (
  `status` int NOT NULL DEFAULT '0',
  `campaign_id` bigint NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `owner_id` bigint NOT NULL DEFAULT '0',
  `owner_toy_id` bigint NOT NULL DEFAULT '0',
  `requester_id` bigint NOT NULL DEFAULT '0',
  `requester_toy_id` bigint NOT NULL DEFAULT '0',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version` bigint DEFAULT NULL,
  `request_message` text NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `exchanges_chk_1` CHECK ((`status` between 0 and 8))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.notifications definition

CREATE TABLE `notifications` (
  `is_read` tinyint(1) NOT NULL DEFAULT '0',
  `related_entity_type` int NOT NULL DEFAULT '0',
  `type` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `related_entity_id` bigint NOT NULL DEFAULT '0',
  `user_id` bigint NOT NULL DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `message` text NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `notifications_chk_1` CHECK ((`related_entity_type` between 0 and 5)),
  CONSTRAINT `notifications_chk_2` CHECK ((`type` between 0 and 12))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.reviews definition

CREATE TABLE `reviews` (
  `rating` int NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `exchange_id` bigint NOT NULL DEFAULT '0',
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reviewed_user_id` bigint NOT NULL DEFAULT '0',
  `reviewer_id` bigint NOT NULL DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `comment` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.toy_participations definition

CREATE TABLE `toy_participations` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `toy_id` bigint NOT NULL,
  `campaign_id` bigint NOT NULL,
  `participation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_toy_id` (`toy_id`),
  KEY `idx_campaign_id` (`campaign_id`),
  KEY `idx_participation_date` (`participation_date`)
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.toy_photos definition

CREATE TABLE `toy_photos` (
  `is_primary` tinyint(1) NOT NULL DEFAULT '0',
  `id` bigint NOT NULL AUTO_INCREMENT,
  `toy_id` bigint NOT NULL DEFAULT '0',
  `uploaded_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `version` bigint DEFAULT NULL,
  `photo_url` varchar(512) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.toys definition

CREATE TABLE `toys` (
  `status` int NOT NULL DEFAULT '0',
  `toy_condition` int NOT NULL DEFAULT '0',
  `campaign_id` bigint NOT NULL DEFAULT '0',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id` bigint NOT NULL AUTO_INCREMENT,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` bigint NOT NULL DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `category` varchar(100) NOT NULL DEFAULT '',
  `description` text NOT NULL,
  `desired_exchange_items` text NOT NULL,
  `name` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000001 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


-- java_demo.users definition

CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '',
  `average_rating` decimal(3,2) NOT NULL DEFAULT '0.00',
  `is_admin` tinyint(1) NOT NULL DEFAULT '0',
  `version` bigint DEFAULT NULL,
  `location` varchar(255) NOT NULL DEFAULT '',
  `password_hash` varchar(255) NOT NULL DEFAULT '',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(1, 0, 1, '2025-05-01 23:59:59', '2025-05-01 00:00:00', 'Annual summer toy exchange event', 'Summer Toy Exchange 2024', '{"maxAge": 12, "minAge": 3, "maxToys": 5}', 'Summer Fun', '2025-04-25 08:54:21', '2025-04-25 08:54:21', 1, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(5, 0, 1, '2025-05-01 23:59:59', '2025-05-01 00:00:00', '2 Annual summer toy exchange event', '2 Summer Toy Exchange 2024', '{"maxAge": 12, "minAge": 3, "maxToys": 5}', '2 Summer Fun', '2025-04-25 09:05:43', '2025-04-25 09:05:43', 1, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(6, 0, 1, '2025-05-10 23:59:59', '2025-05-04 00:00:00', '3 Annual summer toy exchange event', '3 Summer Toy Exchange 2024', '{"maxAge": 12, "minAge": 3, "maxToys": 5}', '3 Summer Fun', '2025-04-25 09:12:58', '2025-04-25 09:12:58', 1, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(7, 0, 1, '2025-02-10 18:00:59', '2025-02-01 09:00:00', 'Tặng quà đầu năm cho khách hàng thân thiết', 'Chiến dịch Tết 2025', 'Mỗi người chỉ được tham gia 1 lần trong thời gian diễn ra chiến dịch.', 'Tết sum vầy', '2025-06-05 03:33:37', '2025-06-05 03:33:37', 2, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(8, 0, 1, '2025-12-10 18:00:59', '2025-05-01 09:00:00', 'Tặng quà đầu năm cho khách hàng thân thiết', 'Chiến dịch Tết 2025 123', 'Mỗi người chỉ được tham gia 1 lần trong thời gian diễn ra chiến dịch.', 'Tết sum vầy', '2025-06-05 04:07:34', '2025-06-05 04:07:34', 2, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(9, 0, 1, '2025-12-10 18:00:59', '2025-05-01 09:00:00', 'Tặng quà đầu năm cho khách hàng thân thiết', 'Chiến dịch Tết 2025 123', 'Mỗi người chỉ được tham gia 1 lần trong thời gian diễn ra chiến dịch.', 'Tết sum vầy', '2025-06-05 04:33:51', '2025-06-05 04:33:51', 2, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(10, 0, 1, '2025-12-10 18:00:59', '2025-05-01 09:00:00', 'Tặng quà đầu năm cho khách hàng thân thiết', 'Chiến dịch Tết 2025 123', 'Mỗi người chỉ được tham gia 1 lần trong thời gian diễn ra chiến dịch.', 'Tết sum vầy', '2025-06-05 04:39:00', '2025-06-05 04:39:00', 2, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(11, 0, 1, '2025-12-10 18:00:59', '2025-05-01 09:00:00', 'Tặng quà đầu năm cho khách hàng thân thiết', 'Chiến dịch Tết 2025 123', 'Mỗi người chỉ được tham gia 1 lần trong thời gian diễn ra chiến dịch.', 'Tết sum vầy', '2025-06-05 04:40:39', '2025-06-05 04:40:39', 2, 0, 0);
INSERT INTO java_demo.events
(id, version, status, end_date, start_date, description, name, rules, theme, created_at, updated_at, `type`, total_toys, available_toys)
VALUES(12, 0, 2, '2025-12-10 18:00:59', '2025-05-01 09:00:00', 'Tặng quà đầu năm cho khách hàng thân thiết', 'Chiến dịch Tết 2025 123', 'Mỗi người chỉ được tham gia 1 lần trong thời gian diễn ra chiến dịch.', 'Tết sum vầy', '2025-06-05 04:41:37', '2025-07-18 21:16:19', 2, 200, 197);

-- Method 4A: Generate numbers using cross joins (works with all MySQL versions)
SET foreign_key_checks = 0;
SET autocommit = 0;
SET unique_checks = 0;

-- Create temporary numbers table using cross joins
DROP TEMPORARY TABLE IF EXISTS numbers;
CREATE TEMPORARY TABLE numbers (n INT PRIMARY KEY);

-- Generate numbers 1 to 1,000,000 using cross joins
INSERT INTO numbers (n)
SELECT
    (t1.i * 100000) + (t2.i * 10000) + (t3.i * 1000) + (t4.i * 100) + (t5.i * 10) + t6.i + 1 as n
FROM
    (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t1,
    (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t2,
    (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t3,
    (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t4,
    (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t5,
    (SELECT 0 i UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t6
WHERE
    (t1.i * 100000) + (t2.i * 10000) + (t3.i * 1000) + (t4.i * 100) + (t5.i * 10) + t6.i + 1 <= 1000000;

-- Now insert using the numbers table
INSERT INTO toys (
    status, toy_condition, campaign_id, created_at, updated_at,
    user_id, version, category, description, desired_exchange_items, name
)
SELECT
    7 as status,
    (n % 5) as toy_condition,
    12 as campaign_id,
    DATE_SUB(NOW(), INTERVAL (n % 365) DAY) as created_at,
    DATE_SUB(NOW(), INTERVAL (n % 365) DAY) as updated_at,
    0 as user_id,
    0 as version,
    CASE (n % 10)
        WHEN 0 THEN 'Action Figures'
        WHEN 1 THEN 'Dolls'
        WHEN 2 THEN 'Educational'
        WHEN 3 THEN 'Electronic'
        WHEN 4 THEN 'Board Games'
        WHEN 5 THEN 'Building Blocks'
        WHEN 6 THEN 'Outdoor'
        WHEN 7 THEN 'Arts & Crafts'
        WHEN 8 THEN 'Puzzle'
        WHEN 9 THEN 'Remote Control'
    END as category,
    CONCAT('High quality toy #', n, ' in excellent condition') as description,
    CASE ((n + 1) % 10)
        WHEN 0 THEN 'Looking for Action Figures'
        WHEN 1 THEN 'Looking for Dolls'
        WHEN 2 THEN 'Looking for Educational toys'
        WHEN 3 THEN 'Looking for Electronic toys'
        WHEN 4 THEN 'Looking for Board Games'
        WHEN 5 THEN 'Looking for Building Blocks'
        WHEN 6 THEN 'Looking for Outdoor toys'
        WHEN 7 THEN 'Looking for Arts & Crafts'
        WHEN 8 THEN 'Looking for Puzzles'
        WHEN 9 THEN 'Looking for Remote Control toys'
    END as desired_exchange_items,
    CASE (n % 10)
        WHEN 0 THEN CONCAT('Super Robot ', n)
        WHEN 1 THEN CONCAT('Princess Doll ', n)
        WHEN 2 THEN CONCAT('Learning Tablet ', n)
        WHEN 3 THEN CONCAT('Racing Car ', n)
        WHEN 4 THEN CONCAT('Chess Set ', n)
        WHEN 5 THEN CONCAT('LEGO Castle ', n)
        WHEN 6 THEN CONCAT('Soccer Ball ', n)
        WHEN 7 THEN CONCAT('Paint Set ', n)
        WHEN 8 THEN CONCAT('Jigsaw Puzzle ', n)
        WHEN 9 THEN CONCAT('Drone ', n)
    END as name
FROM numbers;

COMMIT;

-- Restore settings
SET foreign_key_checks = 1;
SET autocommit = 1;
SET unique_checks = 1;

-- Clean up
DROP TEMPORARY TABLE numbers;

-- Verify results
SELECT COUNT(*) as total_inserted FROM toys;
SELECT 'Insertion completed successfully!' as status;


-- UPDATE java_demo.toys
-- SET status=7, campaign_id = 12
-- WHERE id > 0 and id < 1000000;
