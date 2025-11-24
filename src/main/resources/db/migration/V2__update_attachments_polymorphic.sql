-- Xóa FK cũ
ALTER TABLE attachments 
    DROP FOREIGN KEY attachments_ibfk_1;

-- Xóa cột expense_id
ALTER TABLE attachments 
    DROP COLUMN expense_id;

-- Thêm cột polymorphic
ALTER TABLE attachments
    ADD COLUMN attachmentable_type VARCHAR(20) NOT NULL DEFAULT 'Expense',
    ADD COLUMN attachmentable_id BIGINT NOT NULL DEFAULT 0;
