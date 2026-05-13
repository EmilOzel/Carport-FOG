CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       first_name VARCHAR(50) NOT NULL,
                       last_name VARCHAR(50) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(20) UNIQUE,
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE customers (
                           customer_id SERIAL PRIMARY KEY,
                           user_id INT REFERENCES users(user_id),
                           address VARCHAR(255),
                           zip_code VARCHAR(10)
);

CREATE TABLE carports (
                          carport_id SERIAL PRIMARY KEY,
                          width INT NOT NULL,
                          length INT NOT NULL,
                          height INT NOT NULL,
                          with_shed BOOLEAN DEFAULT FALSE,
                          roof_type VARCHAR(20) NOT NULL
);

CREATE TABLE materials (
                           material_id SERIAL PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           unit VARCHAR(20) NOT NULL,
                           price DECIMAL(10,2) NOT NULL
);

CREATE TABLE orders (
                        order_id SERIAL PRIMARY KEY,
                        customer_id INT REFERENCES customers(customer_id),
                        carport_id INT REFERENCES carports(carport_id),
                        status VARCHAR(50) DEFAULT 'pending',
                        total_price DECIMAL(10,2)
);

CREATE TABLE order_items (
                             order_item_id SERIAL PRIMARY KEY,
                             order_id INT REFERENCES orders(order_id),
                             material_id INT REFERENCES materials(material_id),
                             quantity INT NOT NULL,
                             description VARCHAR(255)
);