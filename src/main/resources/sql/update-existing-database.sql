ALTER TABLE carport DROP CONSTRAINT IF EXISTS carport_width_check;
ALTER TABLE carport DROP CONSTRAINT IF EXISTS carport_length_check;
ALTER TABLE shed DROP CONSTRAINT IF EXISTS shed_width_check;
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_status_check;
ALTER TABLE roof DROP CONSTRAINT IF EXISTS roof_roof_style_check;
ALTER TABLE roof DROP CONSTRAINT IF EXISTS roof_roof_covering_check;
ALTER TABLE roof DROP CONSTRAINT IF EXISTS chk_flat_no_pitch;
ALTER TABLE roof DROP CONSTRAINT IF EXISTS chk_raised_pitch;
ALTER TABLE roof DROP CONSTRAINT IF EXISTS chk_raised_cover;

ALTER TABLE carport ADD CONSTRAINT carport_width_check CHECK (width BETWEEN 240 AND 840);
ALTER TABLE carport ADD CONSTRAINT carport_length_check CHECK (length BETWEEN 240 AND 1200);
ALTER TABLE shed ADD CONSTRAINT shed_width_check CHECK (width BETWEEN 180 AND 720);
ALTER TABLE orders ADD CONSTRAINT orders_status_check CHECK (status IN ('pending', 'offer', 'approved', 'rejected', 'completed'));
ALTER TABLE roof ADD CONSTRAINT roof_roof_style_check CHECK (roof_style = 'flat');
ALTER TABLE roof ADD CONSTRAINT roof_roof_covering_check CHECK (roof_covering IN ('none', 'plastic_trapez'));
ALTER TABLE roof ADD CONSTRAINT chk_flat_no_pitch CHECK (pitch IS NULL);
