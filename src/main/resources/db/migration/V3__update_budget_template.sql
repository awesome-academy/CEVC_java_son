ALTER TABLE budget_templates
    DROP COLUMN IF EXISTS default_category;

ALTER TABLE budget_templates
    ADD COLUMN category_id BIGINT;

ALTER TABLE budget_templates
    ADD CONSTRAINT fk_budget_template_category
    FOREIGN KEY (category_id)
    REFERENCES categories(id)
    ON DELETE SET NULL;
