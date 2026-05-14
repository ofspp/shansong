-- 修复中文乱码数据
-- 在 Navicat 中打开此文件执行即可

USE delivery_db;

-- 修复管理员
UPDATE sys_user SET real_name = '系统管理员' WHERE username = 'admin';

-- 修复商家
UPDATE merchant SET shop_name='美味餐厅', province='河北省', city='唐山市', district='路北区', address='建设路100号' WHERE id=1;
UPDATE merchant SET shop_name='黄焖鸡米饭', province='河北省', city='唐山市', district='路北区', address='大学道88号' WHERE id=2;
UPDATE merchant SET shop_name='兰州拉面馆', province='河北省', city='唐山市', district='路南区', address='新华道56号' WHERE id=3;
UPDATE merchant SET shop_name='麻辣香锅店', province='河北省', city='唐山市', district='路北区', address='学院路66号' WHERE id=4;
UPDATE merchant SET shop_name='奶茶甜品屋', province='河北省', city='唐山市', district='路北区', address='文化路120号' WHERE id=5;

-- 修复骑手
UPDATE rider SET real_name='张三', vehicle_type='电动车', vehicle_no='唐A12345' WHERE username='rider1';

-- 修复顾客
UPDATE customer SET nickname='测试用户' WHERE username='customer1';

-- 修复分类
UPDATE category SET name='热销推荐' WHERE id=1;
UPDATE category SET name='主食' WHERE id=2;
UPDATE category SET name='小吃' WHERE id=3;
UPDATE category SET name='饮品' WHERE id=4;
UPDATE category SET name='招牌黄焖鸡' WHERE id=5;
UPDATE category SET name='套餐' WHERE id=6;
UPDATE category SET name='饮品' WHERE id=7;
UPDATE category SET name='拉面系列' WHERE id=8;
UPDATE category SET name='炒菜' WHERE id=9;
UPDATE category SET name='凉菜' WHERE id=10;
UPDATE category SET name='麻辣香锅' WHERE id=11;
UPDATE category SET name='烤鱼' WHERE id=12;
UPDATE category SET name='饮品' WHERE id=13;
UPDATE category SET name='奶茶' WHERE id=14;
UPDATE category SET name='果茶' WHERE id=15;
UPDATE category SET name='甜品' WHERE id=16;

-- 修复菜品（美味餐厅）
UPDATE dish SET name='招牌炒饭', description='精选食材，大火翻炒，粒粒分明' WHERE id=1;
UPDATE dish SET name='红烧肉套餐', description='经典红烧肉配米饭，入口即化' WHERE id=2;
UPDATE dish SET name='宫保鸡丁', description='花生酥脆，鸡丁滑嫩，微辣开胃' WHERE id=3;
UPDATE dish SET name='蛋炒饭', description='简单美味，鸡蛋炒饭经典搭配' WHERE id=4;
UPDATE dish SET name='牛肉面', description='手工拉面，浓郁牛骨汤底' WHERE id=5;
UPDATE dish SET name='鸡腿饭', description='香酥鸡腿配蔬菜和米饭' WHERE id=6;
UPDATE dish SET name='炸鸡翅', description='外酥里嫩，金黄诱人' WHERE id=7;
UPDATE dish SET name='薯条', description='香脆可口，配番茄酱' WHERE id=8;
UPDATE dish SET name='可乐', description='冰镇可口可乐 330ml' WHERE id=9;
UPDATE dish SET name='柠檬茶', description='新鲜柠檬现泡，清爽解腻' WHERE id=10;

-- 修复菜品（黄焖鸡米饭）
UPDATE dish SET name='经典黄焖鸡', description='秘制酱料，鸡肉鲜嫩，汤汁浓郁' WHERE id=11;
UPDATE dish SET name='黄焖鸡翅', description='整翅焖制，肉质酥烂' WHERE id=12;
UPDATE dish SET name='黄焖排骨', description='排骨入味，酱香浓郁' WHERE id=13;
UPDATE dish SET name='黄焖鸡套餐', description='黄焖鸡+米饭+小菜+饮品' WHERE id=14;
UPDATE dish SET name='排骨套餐', description='黄焖排骨+米饭+小菜' WHERE id=15;
UPDATE dish SET name='酸梅汤', description='古法熬制，开胃解腻' WHERE id=16;

-- 修复菜品（兰州拉面馆）
UPDATE dish SET name='兰州牛肉拉面', description='正宗兰州拉面，牛肉大块' WHERE id=17;
UPDATE dish SET name='牛肉板面', description='劲道板面，浓郁牛肉汤' WHERE id=18;
UPDATE dish SET name='鸡蛋拉面', description='加鸡蛋的经典拉面' WHERE id=19;
UPDATE dish SET name='西红柿炒蛋', description='家常美味，酸甜可口' WHERE id=20;
UPDATE dish SET name='青椒肉丝', description='青椒脆嫩，肉丝滑嫩' WHERE id=21;
UPDATE dish SET name='凉拌黄瓜', description='清脆爽口，蒜香四溢' WHERE id=22;
UPDATE dish SET name='凉拌木耳', description='口感脆嫩，酸辣开胃' WHERE id=23;

-- 修复菜品（麻辣香锅店）
UPDATE dish SET name='经典麻辣香锅', description='自选食材，麻辣鲜香' WHERE id=24;
UPDATE dish SET name='鸡肉香锅', description='鸡肉为主，配蔬菜' WHERE id=25;
UPDATE dish SET name='牛肉香锅', description='牛肉为主，香辣过瘾' WHERE id=26;
UPDATE dish SET name='烤鱼（微辣）', description='鲜嫩烤鱼，微辣入味' WHERE id=27;
UPDATE dish SET name='烤鱼（麻辣）', description='麻辣烤鱼，过瘾刺激' WHERE id=28;
UPDATE dish SET name='王老吉', description='怕上火喝王老吉' WHERE id=29;

-- 修复菜品（奶茶甜品屋）
UPDATE dish SET name='珍珠奶茶', description='经典珍珠奶茶，Q弹珍珠' WHERE id=30;
UPDATE dish SET name='抹茶拿铁', description='日式抹茶配牛奶，香浓顺滑' WHERE id=31;
UPDATE dish SET name='焦糖奶茶', description='焦糖风味，甜而不腻' WHERE id=32;
UPDATE dish SET name='芒果果茶', description='新鲜芒果打制，果香浓郁' WHERE id=33;
UPDATE dish SET name='柠檬绿茶', description='清新柠檬配绿茶，消暑解渴' WHERE id=34;
UPDATE dish SET name='百香果茶', description='酸甜百香果，维C满满' WHERE id=35;
UPDATE dish SET name='芒果班戟', description='新鲜芒果裹奶油，软糯香甜' WHERE id=36;
UPDATE dish SET name='双皮奶', description='顺德双皮奶，奶香浓郁' WHERE id=37;
UPDATE dish SET name='杨枝甘露', description='芒果西柚椰奶，清爽甜蜜' WHERE id=38;

-- 修复顾客地址
UPDATE customer_address SET receiver_name='测试用户', province='河北省', city='唐山市', district='路北区', detail_address='大学道1号' WHERE id=1;

-- 新增点赞功能表和字段
-- 如果 likes_count 列已存在，此语句会报错可忽略
ALTER TABLE merchant ADD COLUMN likes_count INT DEFAULT 0 COMMENT '点赞数' AFTER min_order_amount;

CREATE TABLE IF NOT EXISTS merchant_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞ID',
    customer_id BIGINT NOT NULL COMMENT '顾客ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_customer_merchant (customer_id, merchant_id),
    INDEX idx_merchant_like_merchant (merchant_id),
    INDEX idx_merchant_like_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家点赞表';
