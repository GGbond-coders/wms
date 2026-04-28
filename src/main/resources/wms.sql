
# 创建商品表
CREATE TABLE goods (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
                       name VARCHAR(100) NOT NULL COMMENT '商品名称',
                       category VARCHAR(50) COMMENT '商品分类',
                       brand VARCHAR(50) COMMENT '品牌',
                       price DECIMAL(10,2) COMMENT '单价',
                       unit VARCHAR(20) COMMENT '单位',
                       create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                       update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

# 创建库存表
CREATE TABLE stock (
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       goods_id BIGINT NOT NULL COMMENT '商品ID',
                       quantity INT DEFAULT 0 COMMENT '当前库存数量',
                       safe_stock INT DEFAULT 10 COMMENT '安全库存阈值',
                       update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

# 创建入库表
CREATE TABLE inbound (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         goods_id BIGINT NOT NULL,
                         quantity INT NOT NULL COMMENT '入库数量',
                         operator VARCHAR(50) COMMENT '操作人',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

# 创建出库表
CREATE TABLE outbound (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          goods_id BIGINT NOT NULL,
                          quantity INT NOT NULL COMMENT '出库数量',
                          receiver VARCHAR(100) COMMENT '接收方',
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);


# 创建用户表
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) UNIQUE NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      role VARCHAR(20) COMMENT '角色（ADMIN/WAREHOUSE/SALES）',
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);


# 创建操作日志表
CREATE TABLE operation_log (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               username VARCHAR(50),
                               operation VARCHAR(50) COMMENT '操作类型',
                               content TEXT COMMENT '操作内容',
                               create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);


# 插入数据
INSERT INTO goods(name, category, brand, price, unit) VALUES
                                                          ('iPhone 15', '手机数码', 'Apple', 5999, '台'),
                                                          ('iPhone 15 Pro', '手机数码', 'Apple', 7999, '台'),
                                                          ('华为Mate 60', '手机数码', 'Huawei', 4999, '台'),
                                                          ('小米14', '手机数码', 'Xiaomi', 3999, '台'),
                                                          ('MacBook Air', '电脑', 'Apple', 8999, '台'),
                                                          ('联想ThinkPad X1', '电脑', 'Lenovo', 10999, '台'),
                                                          ('华为笔记本X Pro', '电脑', 'Huawei', 7999, '台'),
                                                          ('戴尔XPS13', '电脑', 'Dell', 9999, '台'),
                                                          ('机械键盘', '外设', 'Keychron', 599, '个'),
                                                          ('无线鼠标', '外设', 'Logitech', 199, '个'),
                                                          ('27寸显示器', '显示器', 'AOC', 1299, '台'),
                                                          ('4K显示器', '显示器', 'Samsung', 2399, '台'),
                                                          ('移动硬盘1TB', '存储', 'Seagate', 399, '个'),
                                                          ('SSD 1TB', '存储', 'Samsung', 699, '个'),
                                                          ('U盘128GB', '存储', 'Kingston', 99, '个'),
                                                          ('蓝牙耳机', '音频', 'Sony', 799, '个'),
                                                          ('头戴式耳机', '音频', 'Bose', 1599, '个'),
                                                          ('智能手表', '可穿戴', 'Apple', 2999, '个'),
                                                          ('运动手环', '可穿戴', 'Xiaomi', 199, '个'),
                                                          ('路由器AX3000', '网络设备', 'TP-Link', 299, '个');


INSERT INTO stock(goods_id, quantity, safe_stock) VALUES
                                                      (1, 120, 30),
                                                      (2, 80, 20),
                                                      (3, 150, 40),
                                                      (4, 200, 50),
                                                      (5, 60, 10),
                                                      (6, 40, 10),
                                                      (7, 70, 15),
                                                      (8, 55, 10),
                                                      (9, 300, 80),
                                                      (10, 500, 100),
                                                      (11, 90, 20),
                                                      (12, 45, 10),
                                                      (13, 160, 30),
                                                      (14, 110, 25),
                                                      (15, 400, 100),
                                                      (16, 180, 40),
                                                      (17, 75, 20),
                                                      (18, 95, 20),
                                                      (19, 260, 60),
                                                      (20, 140, 30);


# 插入用户数据
INSERT INTO user(username, password, role) VALUES
                                              ('admin', '123456', 'ADMIN'),
                                              ('warehouse', '123456', 'WAREHOUSE'),
                                              ('sales', '123456', 'SALES');


INSERT INTO inbound(goods_id, quantity, operator) VALUES
                                                      (1, 50, 'admin'),
                                                      (2, 30, 'admin'),
                                                      (3, 60, 'admin'),
                                                      (4, 80, 'admin'),
                                                      (5, 20, 'admin'),
                                                      (6, 10, 'admin'),
                                                      (7, 25, 'admin'),
                                                      (8, 15, 'admin'),
                                                      (9, 100, 'admin'),
                                                      (10, 200, 'admin'),
                                                      (11, 40, 'admin'),
                                                      (12, 20, 'admin'),
                                                      (13, 70, 'admin'),
                                                      (14, 50, 'admin'),
                                                      (15, 150, 'admin');


INSERT INTO outbound(goods_id, quantity, receiver) VALUES
                                                       (1, 10, '客户A'),
                                                       (2, 5, '客户B'),
                                                       (3, 20, '客户C'),
                                                       (4, 30, '客户D'),
                                                       (5, 8, '客户E'),
                                                       (6, 3, '客户F'),
                                                       (7, 6, '客户G'),
                                                       (8, 4, '客户H'),
                                                       (9, 50, '客户I'),
                                                       (10, 80, '客户J'),
                                                       (11, 15, '客户K'),
                                                       (12, 10, '客户L'),
                                                       (13, 25, '客户M'),
                                                       (14, 20, '客户N');


INSERT INTO user(username, password, role) VALUES
                                               ('admin','123456','ADMIN'),
                                               ('sales1','123456','SALES'),
                                               ('sales2','123456','SALES'),
                                               ('user01','123456','SALES'),
                                               ('user02','123456','SALES');