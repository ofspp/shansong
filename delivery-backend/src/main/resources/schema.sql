-- 创建数据库
CREATE DATABASE IF NOT EXISTS delivery_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE delivery_db;

-- 用户表（管理员）
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) DEFAULT 'ADMIN' COMMENT '角色',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 商家表
CREATE TABLE IF NOT EXISTS merchant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商家ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    shop_name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    shop_logo VARCHAR(255) COMMENT '店铺logo',
    phone VARCHAR(20) COMMENT '联系电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    address VARCHAR(255) COMMENT '详细地址',
    business_hours VARCHAR(100) COMMENT '营业时间',
    delivery_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
    min_order_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '起送金额',
    likes_count INT DEFAULT 0 COMMENT '点赞数',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-营业中，2-已禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

-- 骑手表
CREATE TABLE IF NOT EXISTS rider (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '骑手ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    id_card VARCHAR(18) COMMENT '身份证号',
    vehicle_type VARCHAR(20) COMMENT '车辆类型',
    vehicle_no VARCHAR(20) COMMENT '车牌号',
    avatar VARCHAR(255) COMMENT '头像',
    working_status TINYINT DEFAULT 0 COMMENT '工作状态：0-休息，1-接单中',
    status TINYINT DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='骑手表';

-- 顾客表
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '顾客ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾客表';

-- 菜品分类表
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS dish (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜品ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    description VARCHAR(500) COMMENT '描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    image VARCHAR(255) COMMENT '图片',
    sort INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-下架，1-上架',
    sales INT DEFAULT 0 COMMENT '销量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品表';

-- 购物车表
CREATE TABLE IF NOT EXISTS cart (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 顾客地址表
CREATE TABLE IF NOT EXISTS customer_address (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail_address VARCHAR(255) COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认：0-否，1-是',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='顾客地址表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    rider_id BIGINT COMMENT '骑手ID',
    address_id BIGINT COMMENT '收货地址ID',
    status VARCHAR(20) NOT NULL COMMENT '订单状态',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '商品总金额',
    delivery_fee DECIMAL(10,2) DEFAULT 0.00 COMMENT '配送费',
    pay_amount DECIMAL(10,2) COMMENT '应付金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    pay_method VARCHAR(20) COMMENT '支付方式',
    pay_time DATETIME COMMENT '支付时间',
    accept_time DATETIME COMMENT '商家接单时间',
    grab_time DATETIME COMMENT '骑手接单时间',
    complete_time DATETIME COMMENT '完成时间',
    cancel_reason VARCHAR(255) COMMENT '取消原因',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) COMMENT '省',
    city VARCHAR(50) COMMENT '市',
    district VARCHAR(50) COMMENT '区',
    detail_address VARCHAR(255) COMMENT '详细地址',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    dish_name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    dish_image VARCHAR(255) COMMENT '菜品图片',
    price DECIMAL(10,2) NOT NULL COMMENT '单价',
    quantity INT NOT NULL COMMENT '数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 商家点赞表
CREATE TABLE IF NOT EXISTS merchant_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_customer_merchant (customer_id, merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家点赞表';

-- 评价表
CREATE TABLE IF NOT EXISTS evaluation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评价ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    rating INT NOT NULL COMMENT '评分：1-5',
    content VARCHAR(500) COMMENT '评价内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

-- 创建索引
CREATE INDEX idx_merchant_status ON merchant(status);
CREATE INDEX idx_category_merchant ON category(merchant_id);
CREATE INDEX idx_dish_merchant ON dish(merchant_id);
CREATE INDEX idx_dish_category ON dish(category_id);
CREATE INDEX idx_cart_customer ON cart(customer_id);
CREATE INDEX idx_address_customer ON customer_address(customer_id);
CREATE INDEX idx_order_customer ON orders(customer_id);
CREATE INDEX idx_order_merchant ON orders(merchant_id);
CREATE INDEX idx_order_rider ON orders(rider_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_merchant_like_merchant ON merchant_like(merchant_id);
CREATE INDEX idx_merchant_like_customer ON merchant_like(customer_id);

-- 初始化管理员账户（密码：123456，使用BCrypt加密）
INSERT INTO sys_user (username, password, real_name, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 1);

-- 初始化测试商家（密码：123456）
INSERT INTO merchant (username, password, shop_name, phone, province, city, district, address, business_hours, delivery_fee, min_order_amount, status) VALUES
('merchant1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '美味餐厅', '13800138001', '河北省', '唐山市', '路北区', '建设路100号', '08:00-22:00', 5.00, 20.00, 1),
('merchant2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '黄焖鸡米饭', '13800138002', '河北省', '唐山市', '路北区', '大学道88号', '09:00-21:00', 3.00, 15.00, 1),
('merchant3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '兰州拉面馆', '13800138003', '河北省', '唐山市', '路南区', '新华道56号', '07:00-22:00', 4.00, 12.00, 1),
('merchant4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '麻辣香锅店', '13800138004', '河北省', '唐山市', '路北区', '学院路66号', '10:00-22:00', 5.00, 25.00, 1),
('merchant5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '奶茶甜品屋', '13800138005', '河北省', '唐山市', '路北区', '文化路120号', '08:00-23:00', 2.00, 10.00, 1);

-- 初始化测试骑手（密码：123456）
INSERT INTO rider (username, password, real_name, phone, id_card, vehicle_type, vehicle_no, working_status, status) VALUES
('rider1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '张三', '13900139001', '130203199001011234', '电动车', '唐A12345', 1, 1);

-- 初始化测试顾客（密码：123456）
INSERT INTO customer (username, password, nickname, phone, status) VALUES
('customer1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', '13700137001', 1);

-- 初始化菜品分类
INSERT INTO category (merchant_id, name, sort) VALUES
(1, '热销推荐', 1),
(1, '主食', 2),
(1, '小吃', 3),
(1, '饮品', 4),
(2, '招牌黄焖鸡', 1),
(2, '套餐', 2),
(2, '饮品', 3),
(3, '拉面系列', 1),
(3, '炒菜', 2),
(3, '凉菜', 3),
(4, '麻辣香锅', 1),
(4, '烤鱼', 2),
(4, '饮品', 3),
(5, '奶茶', 1),
(5, '果茶', 2),
(5, '甜品', 3);

-- 初始化菜品
INSERT INTO dish (merchant_id, category_id, name, description, price, sort, status, sales) VALUES
-- 美味餐厅
(1, 1, '招牌炒饭', '精选食材，大火翻炒，粒粒分明', 18.00, 10, 1, 256),
(1, 1, '红烧肉套餐', '经典红烧肉配米饭，入口即化', 28.00, 9, 1, 189),
(1, 1, '宫保鸡丁', '花生酥脆，鸡丁滑嫩，微辣开胃', 26.00, 8, 1, 167),
(1, 2, '蛋炒饭', '简单美味，鸡蛋炒饭经典搭配', 12.00, 7, 1, 312),
(1, 2, '牛肉面', '手工拉面，浓郁牛骨汤底', 22.00, 6, 1, 145),
(1, 2, '鸡腿饭', '香酥鸡腿配蔬菜和米饭', 20.00, 5, 1, 198),
(1, 3, '炸鸡翅', '外酥里嫩，金黄诱人', 15.00, 4, 1, 88),
(1, 3, '薯条', '香脆可口，配番茄酱', 8.00, 3, 1, 76),
(1, 4, '可乐', '冰镇可口可乐 330ml', 3.00, 2, 1, 420),
(1, 4, '柠檬茶', '新鲜柠檬现泡，清爽解腻', 6.00, 1, 1, 156),
-- 黄焖鸡米饭
(2, 5, '经典黄焖鸡', '秘制酱料，鸡肉鲜嫩，汤汁浓郁', 22.00, 10, 1, 523),
(2, 5, '黄焖鸡翅', '整翅焖制，肉质酥烂', 26.00, 9, 1, 234),
(2, 5, '黄焖排骨', '排骨入味，酱香浓郁', 28.00, 8, 1, 189),
(2, 6, '黄焖鸡套餐', '黄焖鸡+米饭+小菜+饮品', 28.00, 7, 1, 356),
(2, 6, '排骨套餐', '黄焖排骨+米饭+小菜', 32.00, 6, 1, 145),
(2, 7, '酸梅汤', '古法熬制，开胃解腻', 5.00, 1, 1, 267),
-- 兰州拉面馆
(3, 8, '兰州牛肉拉面', '正宗兰州拉面，牛肉大块', 15.00, 10, 1, 678),
(3, 8, '牛肉板面', '劲道板面，浓郁牛肉汤', 14.00, 9, 1, 345),
(3, 8, '鸡蛋拉面', '加鸡蛋的经典拉面', 12.00, 8, 1, 234),
(3, 9, '西红柿炒蛋', '家常美味，酸甜可口', 16.00, 5, 1, 189),
(3, 9, '青椒肉丝', '青椒脆嫩，肉丝滑嫩', 18.00, 4, 1, 156),
(3, 10, '凉拌黄瓜', '清脆爽口，蒜香四溢', 8.00, 2, 1, 234),
(3, 10, '凉拌木耳', '口感脆嫩，酸辣开胃', 10.00, 1, 1, 167),
-- 麻辣香锅店
(4, 11, '经典麻辣香锅', '自选食材，麻辣鲜香', 38.00, 10, 1, 345),
(4, 11, '鸡肉香锅', '鸡肉为主，配蔬菜', 35.00, 9, 1, 234),
(4, 11, '牛肉香锅', '牛肉为主，香辣过瘾', 42.00, 8, 1, 198),
(4, 12, '烤鱼（微辣）', '鲜嫩烤鱼，微辣入味', 58.00, 5, 1, 156),
(4, 12, '烤鱼（麻辣）', '麻辣烤鱼，过瘾刺激', 58.00, 4, 1, 134),
(4, 13, '王老吉', '怕上火喝王老吉', 5.00, 1, 1, 289),
-- 奶茶甜品屋
(5, 14, '珍珠奶茶', '经典珍珠奶茶，Q弹珍珠', 12.00, 10, 1, 567),
(5, 14, '抹茶拿铁', '日式抹茶配牛奶，香浓顺滑', 15.00, 9, 1, 234),
(5, 14, '焦糖奶茶', '焦糖风味，甜而不腻', 13.00, 8, 1, 189),
(5, 15, '芒果果茶', '新鲜芒果打制，果香浓郁', 14.00, 7, 1, 345),
(5, 15, '柠檬绿茶', '清新柠檬配绿茶，消暑解渴', 10.00, 6, 1, 423),
(5, 15, '百香果茶', '酸甜百香果，维C满满', 12.00, 5, 1, 267),
(5, 16, '芒果班戟', '新鲜芒果裹奶油，软糯香甜', 16.00, 3, 1, 134),
(5, 16, '双皮奶', '顺德双皮奶，奶香浓郁', 12.00, 2, 1, 198),
(5, 16, '杨枝甘露', '芒果西柚椰奶，清爽甜蜜', 15.00, 1, 1, 223);

-- 初始化顾客地址
INSERT INTO customer_address (customer_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(1, '测试用户', '13700137001', '河北省', '唐山市', '路北区', '大学道1号', 1);
