-- DB migration for WMS (2026-04-28)
-- 1) Link inbound/outbound operator to user table
-- 2) Keep outbound.receiver as customer/receiver name

-- inbound: operator (username) -> operator_id (user.id)
ALTER TABLE inbound ADD COLUMN operator_id BIGINT NULL COMMENT '办理人(user.id)';
UPDATE inbound i
JOIN user u ON i.operator = u.username
SET i.operator_id = u.id
WHERE i.operator_id IS NULL;
ALTER TABLE inbound MODIFY operator_id BIGINT NOT NULL;
ALTER TABLE inbound DROP COLUMN operator;

-- outbound: add operator_id (user.id), default existing rows to admin(1) if unknown
ALTER TABLE outbound ADD COLUMN operator_id BIGINT NULL COMMENT '办理人(user.id)';
UPDATE outbound SET operator_id = 1 WHERE operator_id IS NULL;
ALTER TABLE outbound MODIFY operator_id BIGINT NOT NULL;

-- Optional: add foreign keys (requires InnoDB and matching charset)
-- ALTER TABLE inbound ADD CONSTRAINT fk_inbound_operator FOREIGN KEY (operator_id) REFERENCES user(id);
-- ALTER TABLE outbound ADD CONSTRAINT fk_outbound_operator FOREIGN KEY (operator_id) REFERENCES user(id);
-- ALTER TABLE stock ADD CONSTRAINT fk_stock_goods FOREIGN KEY (goods_id) REFERENCES goods(id);

