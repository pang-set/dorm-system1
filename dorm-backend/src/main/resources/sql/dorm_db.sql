-- =====================================================
-- 学生宿舍管理系统数据库脚本
-- Database: dorm_db
-- =====================================================

CREATE DATABASE IF NOT EXISTS dorm_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dorm_db;

-- =====================================================
-- 1. 用户表
-- =====================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT '角色：ADMIN-管理员 HOUSEMASTER-宿管 STUDENT-学生',
    building_id BIGINT COMMENT '管理楼栋ID（宿管用）',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- =====================================================
-- 2. 楼栋表
-- =====================================================
DROP TABLE IF EXISTS building;
CREATE TABLE building (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    building_no VARCHAR(20) NOT NULL COMMENT '楼栋编号',
    gender VARCHAR(10) COMMENT '性别：male-男寝 female-女寝',
    floor_count INT DEFAULT 0 COMMENT '楼层数',
    room_count INT DEFAULT 0 COMMENT '房间总数',
    bed_count INT DEFAULT 0 COMMENT '床位总数',
    occupied_count INT DEFAULT 0 COMMENT '已入住床位数',
    manager_id BIGINT COMMENT '楼管ID',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

-- =====================================================
-- 3. 宿舍表
-- =====================================================
DROP TABLE IF EXISTS dorm_room;
CREATE TABLE dorm_room (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    room_no VARCHAR(20) NOT NULL COMMENT '宿舍号',
    floor INT COMMENT '楼层',
    bed_total INT DEFAULT 0 COMMENT '总床位数',
    bed_used INT DEFAULT 0 COMMENT '已用床位数',
    area DECIMAL(5,2) COMMENT '面积',
    room_type VARCHAR(20) COMMENT '房间类型',
    status TINYINT DEFAULT 1 COMMENT '状态：0-停用 1-正常',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍表';

-- =====================================================
-- 4. 床位表
-- =====================================================
DROP TABLE IF EXISTS bed;
CREATE TABLE bed (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    room_id BIGINT NOT NULL COMMENT '宿舍ID',
    building_id BIGINT NOT NULL COMMENT '楼栋ID',
    bed_no VARCHAR(30) NOT NULL COMMENT '床位号',
    status TINYINT DEFAULT 0 COMMENT '状态：0-空闲 1-已入住',
    student_id BIGINT COMMENT '学生ID',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_room_id (room_id),
    INDEX idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位表';

-- =====================================================
-- 5. 学生表
-- =====================================================
DROP TABLE IF EXISTS student;
CREATE TABLE student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_no VARCHAR(30) NOT NULL UNIQUE COMMENT '学号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    id_card VARCHAR(20) COMMENT '身份证号',
    phone VARCHAR(20) COMMENT '手机号',
    college VARCHAR(100) COMMENT '学院',
    major VARCHAR(100) COMMENT '专业',
    class_name VARCHAR(50) COMMENT '班级',
    grade INT COMMENT '年级',
    address VARCHAR(255) COMMENT '家庭住址',
    avatar VARCHAR(255) COMMENT '头像',
    building_id BIGINT COMMENT '楼栋ID',
    room_id BIGINT COMMENT '宿舍ID',
    bed_id BIGINT COMMENT '床位ID',
    status TINYINT DEFAULT 1 COMMENT '状态：1-未入住 2-已入住 3-已退宿',
    check_in_date DATE COMMENT '入住日期',
    check_out_date DATE COMMENT '退宿日期',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_college (college),
    INDEX idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- =====================================================
-- 6. 卫生检查记录表
-- =====================================================
DROP TABLE IF EXISTS check_record;
CREATE TABLE check_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    building_id BIGINT COMMENT '楼栋ID',
    room_id BIGINT COMMENT '宿舍ID',
    room_no VARCHAR(20) COMMENT '宿舍号',
    inspector_id BIGINT COMMENT '检查人ID',
    inspector_name VARCHAR(50) COMMENT '检查人姓名',
    check_time DATETIME COMMENT '检查时间',
    hygiene_score INT COMMENT '卫生分数',
    hygiene_level VARCHAR(20) COMMENT '卫生等级：优秀/良好/合格/不合格',
    remark VARCHAR(500) COMMENT '备注',
    images VARCHAR(1000) COMMENT '检查图片（逗号分隔）',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_building_id (building_id),
    INDEX idx_check_time (check_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卫生检查记录表';

-- =====================================================
-- 7. 违纪记录表
-- =====================================================
DROP TABLE IF EXISTS violation_record;
CREATE TABLE violation_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT COMMENT '学生ID',
    student_name VARCHAR(50) COMMENT '学生姓名',
    student_no VARCHAR(30) COMMENT '学号',
    building_id BIGINT COMMENT '楼栋ID',
    room_id BIGINT COMMENT '宿舍ID',
    violation_type VARCHAR(50) COMMENT '违纪类型',
    description VARCHAR(500) COMMENT '违纪描述',
    violation_time DATETIME COMMENT '违纪时间',
    handler_id BIGINT COMMENT '处理人ID',
    handler_name VARCHAR(50) COMMENT '处理人姓名',
    handle_result VARCHAR(500) COMMENT '处理结果',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待处理 1-已处理',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='违纪记录表';

-- =====================================================
-- 8. 报修表
-- =====================================================
DROP TABLE IF EXISTS repair;
CREATE TABLE repair (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    repair_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报修编号',
    student_id BIGINT COMMENT '报修学生ID',
    student_name VARCHAR(50) COMMENT '报修学生姓名',
    student_phone VARCHAR(20) COMMENT '学生联系电话',
    building_id BIGINT COMMENT '楼栋ID',
    room_id BIGINT COMMENT '宿舍ID',
    room_no VARCHAR(20) COMMENT '宿舍号',
    repair_type VARCHAR(50) COMMENT '报修类型',
    title VARCHAR(100) COMMENT '报修标题',
    description VARCHAR(1000) COMMENT '报修描述',
    images VARCHAR(1000) COMMENT '报修图片（逗号分隔）',
    status TINYINT DEFAULT 1 COMMENT '状态：1-待处理 2-处理中 3-已完成 4-已评价',
    repairer_id BIGINT COMMENT '维修人员ID',
    repairer_name VARCHAR(50) COMMENT '维修人员姓名',
    repair_time DATETIME COMMENT '维修时间',
    repair_result VARCHAR(500) COMMENT '维修结果',
    rating INT COMMENT '评分（1-5星）',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

-- =====================================================
-- 9. 晚归登记表
-- =====================================================
DROP TABLE IF EXISTS late_return;
CREATE TABLE late_return (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT COMMENT '学生ID',
    student_name VARCHAR(50) COMMENT '学生姓名',
    student_no VARCHAR(30) COMMENT '学号',
    building_id BIGINT COMMENT '楼栋ID',
    room_id BIGINT COMMENT '宿舍ID',
    return_time DATETIME COMMENT '归寝时间',
    reason VARCHAR(255) COMMENT '晚归原因',
    registrar_id BIGINT COMMENT '登记人ID',
    registrar_name VARCHAR(50) COMMENT '登记人姓名',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_student_id (student_id),
    INDEX idx_return_time (return_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='晚归登记表';

-- =====================================================
-- 10. 请假申请表
-- =====================================================
DROP TABLE IF EXISTS leave_request;
CREATE TABLE leave_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT COMMENT '学生ID',
    student_name VARCHAR(50) COMMENT '学生姓名',
    student_no VARCHAR(30) COMMENT '学号',
    building_id BIGINT COMMENT '楼栋ID',
    room_id BIGINT COMMENT '宿舍ID',
    start_date DATE COMMENT '请假开始日期',
    end_date DATE COMMENT '请假结束日期',
    reason VARCHAR(500) COMMENT '请假原因',
    destination VARCHAR(255) COMMENT '去向',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审批 1-已通过 2-已拒绝',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_remark VARCHAR(255) COMMENT '审批备注',
    approve_time DATETIME COMMENT '审批时间',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';

-- =====================================================
-- 11. 调宿申请表
-- =====================================================
DROP TABLE IF EXISTS change_room_request;
CREATE TABLE change_room_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    student_id BIGINT COMMENT '学生ID',
    student_name VARCHAR(50) COMMENT '学生姓名',
    student_no VARCHAR(30) COMMENT '学号',
    old_building_id BIGINT COMMENT '原楼栋ID',
    old_room_id BIGINT COMMENT '原宿舍ID',
    old_room_no VARCHAR(20) COMMENT '原宿舍号',
    new_building_id BIGINT COMMENT '新楼栋ID',
    new_room_id BIGINT COMMENT '新宿舍ID',
    new_room_no VARCHAR(20) COMMENT '新宿舍号',
    new_bed_id BIGINT COMMENT '新床位ID',
    reason VARCHAR(500) COMMENT '调宿原因',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审批 1-已通过 2-已拒绝',
    approver_id BIGINT COMMENT '审批人ID',
    approver_name VARCHAR(50) COMMENT '审批人姓名',
    approve_remark VARCHAR(255) COMMENT '审批备注',
    approve_time DATETIME COMMENT '审批时间',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调宿申请表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 初始化用户（密码都是123456，BCrypt加密）
INSERT INTO sys_user (username, password, real_name, phone, role, building_id, status, create_time) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', '13800138000', 'ADMIN', NULL, 1, NOW()),
('housemaster', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王宿管', '13800138001', 'HOUSEMASTER', 1, 1, NOW()),
('student', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '13800138002', 'STUDENT', NULL, 1, NOW());

-- 初始化楼栋
INSERT INTO building (building_name, building_no, gender, floor_count, room_count, bed_count, occupied_count, remark, create_time) VALUES
('1号楼', 'B01', 'male', 6, 120, 720, 0, '男生宿舍', NOW()),
('2号楼', 'B02', 'male', 6, 120, 720, 0, '男生宿舍', NOW()),
('3号楼', 'B03', 'female', 6, 120, 720, 0, '女生宿舍', NOW()),
('4号楼', 'B04', 'female', 6, 120, 720, 0, '女生宿舍', NOW());

-- 初始化宿舍（1号楼的部分宿舍）
INSERT INTO dorm_room (building_id, room_no, floor, bed_total, bed_used, area, room_type, status, create_time) VALUES
(1, '101', 1, 6, 0, 25.00, '六人间', 1, NOW()),
(1, '102', 1, 6, 0, 25.00, '六人间', 1, NOW()),
(1, '103', 1, 6, 0, 25.00, '六人间', 1, NOW()),
(1, '201', 2, 6, 0, 25.00, '六人间', 1, NOW()),
(1, '202', 2, 6, 0, 25.00, '六人间', 1, NOW()),
(1, '301', 3, 6, 0, 25.00, '六人间', 1, NOW()),
(3, '101', 1, 6, 0, 25.00, '六人间', 1, NOW()),
(3, '102', 1, 6, 0, 25.00, '六人间', 1, NOW());

-- 初始化床位
INSERT INTO bed (room_id, building_id, bed_no, status, create_time) VALUES
(1, 1, '101-1', 0, NOW()),
(1, 1, '101-2', 0, NOW()),
(1, 1, '101-3', 0, NOW()),
(1, 1, '101-4', 0, NOW()),
(1, 1, '101-5', 0, NOW()),
(1, 1, '101-6', 0, NOW()),
(2, 1, '102-1', 0, NOW()),
(2, 1, '102-2', 0, NOW()),
(2, 1, '102-3', 0, NOW()),
(2, 1, '102-4', 0, NOW()),
(2, 1, '102-5', 0, NOW()),
(2, 1, '102-6', 0, NOW()),
(4, 1, '201-1', 0, NOW()),
(4, 1, '201-2', 0, NOW()),
(4, 1, '201-3', 0, NOW()),
(4, 1, '201-4', 0, NOW()),
(4, 1, '201-5', 0, NOW()),
(4, 1, '201-6', 0, NOW());

-- 初始化学生
INSERT INTO student (student_no, name, gender, id_card, phone, college, major, class_name, grade, address, status, create_time) VALUES
('2024001', '张三', 'male', '110101200001011234', '13800000001', '计算机学院', '软件工程', '软工1班', 2024, '北京市海淀区', 1, NOW()),
('2024002', '李四', 'male', '110101200001011235', '13800000002', '计算机学院', '软件工程', '软工1班', 2024, '北京市朝阳区', 1, NOW()),
('2024003', '王五', 'male', '110101200001011236', '13800000003', '计算机学院', '计算机科学', '计科1班', 2024, '北京市西城区', 1, NOW()),
('2024004', '赵六', 'female', '110101200001011237', '13800000004', '信息学院', '通信工程', '通信1班', 2024, '北京市东城区', 1, NOW()),
('2024005', '钱七', 'female', '110101200001011238', '13800000005', '经管学院', '会计学', '会计1班', 2024, '上海市浦东新区', 1, NOW()),
('2024006', '孙八', 'male', '110101200001011239', '13800000006', '机械学院', '机械设计', '机械1班', 2024, '广州市天河区', 1, NOW()),
('2024007', '周九', 'male', '110101200001011240', '13800000007', '电气学院', '电气工程', '电气1班', 2024, '深圳市南山区', 1, NOW()),
('2024008', '吴十', 'female', '110101200001011241', '13800000008', '外国语学院', '英语', '英语1班', 2024, '杭州市西湖区', 1, NOW());

-- 初始化一些报修记录
INSERT INTO repair (repair_no, student_id, student_name, student_phone, building_id, room_id, room_no, repair_type, title, description, status, create_time) VALUES
('BX20240101001', 1, '张三', '13800000001', 1, 1, '101', '水电维修', '水龙头漏水', '卫生间水龙头一直滴水，影响正常使用', 1, NOW()),
('BX20240101002', 2, '李四', '13800000002', 1, 2, '102', '家具维修', '椅子损坏', '书桌前的椅子靠背断裂', 2, NOW()),
('BX20240101003', 3, '王五', '13800000003', 1, 1, '101', '网络维修', '网络连接不上', '宿舍宽带无法连接，路由器灯不亮', 3, NOW());

-- 初始化卫生检查记录
INSERT INTO check_record (building_id, room_id, room_no, inspector_id, inspector_name, check_time, hygiene_score, hygiene_level, remark, create_time) VALUES
(1, 1, '101', 2, '王宿管', NOW() - INTERVAL 2 DAY, 90, '优秀', '卫生状况良好，物品摆放整齐', NOW() - INTERVAL 2 DAY),
(1, 2, '102', 2, '王宿管', NOW() - INTERVAL 2 DAY, 75, '良好', '整体整洁，角落有少量灰尘', NOW() - INTERVAL 2 DAY),
(1, 4, '201', 2, '王宿管', NOW() - INTERVAL 1 DAY, 60, '合格', '地面有垃圾，需要加强', NOW() - INTERVAL 1 DAY);

-- 初始化违纪记录
INSERT INTO violation_record (student_id, student_name, student_no, building_id, room_id, violation_type, description, violation_time, status, create_time) VALUES
(1, '张三', '2024001', 1, 1, '使用违禁电器', '宿舍内发现使用电热锅', NOW() - INTERVAL 3 DAY, 1, NOW() - INTERVAL 3 DAY),
(2, '李四', '2024002', 1, 2, '晚归', '凌晨1点才返回宿舍', NOW() - INTERVAL 1 DAY, 0, NOW() - INTERVAL 1 DAY);

INSERT INTO violation_record (student_id, student_name, student_no, building_id, room_id, violation_type, description, violation_time, handler_id, handler_name, handle_result, status, create_time) VALUES
(1, '张三', '2024001', 1, 1, '使用违禁电器', '宿舍内发现使用电热锅', NOW() - INTERVAL 3 DAY, 2, '王宿管', '给予警告处分，没收违规电器', 1, NOW() - INTERVAL 3 DAY);

-- 初始化晚归记录
INSERT INTO late_return (student_id, student_name, student_no, building_id, room_id, return_time, reason, registrar_id, registrar_name, create_time) VALUES
(2, '李四', '2024002', 1, 2, NOW() - INTERVAL 1 DAY + INTERVAL 1 HOUR, '参加社团活动', 2, '王宿管', NOW() - INTERVAL 1 DAY),
(3, '王五', '2024003', 1, 1, NOW() - INTERVAL 2 DAY + INTERVAL 30 MINUTE, '图书馆学习', 2, '王宿管', NOW() - INTERVAL 2 DAY);

-- 初始化请假记录
INSERT INTO leave_request (student_id, student_name, student_no, building_id, room_id, start_date, end_date, reason, destination, emergency_contact, status, create_time) VALUES
(4, '赵六', '2024004', 3, 7, CURDATE(), CURDATE() + INTERVAL 2 DAY, '回家处理家事', '杭州市', '父亲 13900000001', 0, NOW()),
(5, '钱七', '2024005', 3, 8, CURDATE() - INTERVAL 5 DAY, CURDATE() - INTERVAL 3 DAY, '参加竞赛', '上海市', '母亲 13900000002', 1, NOW() - INTERVAL 5 DAY);

-- =====================================================
-- 数据完成
-- =====================================================
SELECT '数据库初始化完成！' AS message;
