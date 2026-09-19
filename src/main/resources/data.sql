MERGE INTO employee (id, username, password, name, role, enabled) KEY(id)
VALUES (1, 'admin', '123456', 'liurui', 'ADMIN', TRUE);

MERGE INTO customer (id, username, password, name, phone, enabled) KEY(id)
VALUES (1, 'liurui', '123456', 'liurui', '13800000000', TRUE);

MERGE INTO category (id, name, type, sort, enabled) KEY(id) VALUES
(1, '热销菜品', 1, 1, TRUE),
(2, '家常小炒', 1, 2, TRUE),
(3, '商务套餐', 2, 3, TRUE),
(4, '营养套餐', 2, 4, TRUE);

MERGE INTO dish (id, name, category_id, price, description, image, enabled) KEY(id) VALUES
(1, '黑椒牛柳饭', 1, 28.00, '黑椒酱汁搭配时蔬，适合工作餐。', '', TRUE),
(2, '宫保鸡丁饭', 1, 24.00, '微辣口味，花生香脆，经典下饭。', '', TRUE),
(3, '番茄鸡蛋面', 2, 18.00, '酸甜番茄汤底，配现煮面条。', '', TRUE),
(4, '青椒肉丝', 2, 22.00, '经典家常菜，咸鲜下饭。', '', TRUE);

MERGE INTO setmeal (id, name, category_id, price, description, enabled) KEY(id) VALUES
(1, '单人工作餐', 3, 36.00, '主食加热菜，适合午餐。', TRUE),
(2, '双人分享餐', 4, 58.00, '两人份组合，包含两道热菜。', TRUE);

MERGE INTO setmeal_dish (setmeal_id, dish_id) KEY(setmeal_id, dish_id) VALUES
(1, 1),
(1, 3),
(2, 2),
(2, 4);

MERGE INTO address_book (id, customer_id, consignee, phone, detail, default_address) KEY(id)
VALUES (1, 1, '刘睿', '13800000000', '软件园 1 号楼 808 室', TRUE);

MERGE INTO shop_status (id, open_flag) KEY(id) VALUES (1, TRUE);
