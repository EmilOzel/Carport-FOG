ALTER TABLE carport DROP CONSTRAINT IF EXISTS carport_width_check;
ALTER TABLE carport DROP CONSTRAINT IF EXISTS carport_length_check;
ALTER TABLE shed DROP CONSTRAINT IF EXISTS shed_width_check;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check;

ALTER TABLE carport ADD CONSTRAINT carport_width_check CHECK (width BETWEEN 240 AND 840);
ALTER TABLE carport ADD CONSTRAINT carport_length_check CHECK (length BETWEEN 240 AND 1200);
ALTER TABLE shed ADD CONSTRAINT shed_width_check CHECK (width BETWEEN 180 AND 720);
ALTER TABLE orders ADD CONSTRAINT orders_status_check CHECK (status IN ('pending', 'offer', 'approved', 'rejected', 'completed'));
