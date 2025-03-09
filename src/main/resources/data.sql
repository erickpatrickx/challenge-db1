-- Inserindo categorias principais (nível 1)
INSERT INTO category (id, name) VALUES
                                    (nextval('category_seq'), 'Electronics'),
                                    (nextval('category_seq'), 'Clothing'),
                                    (nextval('category_seq'), 'Home & Kitchen'),
                                    (nextval('category_seq'), 'Sports & Outdoors'),
                                    (nextval('category_seq'), 'Books'),
                                    (nextval('category_seq'), 'Toys & Games')
    ON CONFLICT (name) DO NOTHING;

-- Inserindo subcategorias (nível 2)
INSERT INTO category (id, name, parent_id) VALUES
                                               (nextval('category_seq'), 'Phones & Accessories', (SELECT id FROM category WHERE name = 'Electronics')),
                                               (nextval('category_seq'), 'Computers & Laptops', (SELECT id FROM category WHERE name = 'Electronics')),
                                               (nextval('category_seq'), 'Men Clothing', (SELECT id FROM category WHERE name = 'Clothing')),
                                               (nextval('category_seq'), 'Women Clothing', (SELECT id FROM category WHERE name = 'Clothing')),
                                               (nextval('category_seq'), 'Kitchen Appliances', (SELECT id FROM category WHERE name = 'Home & Kitchen')),
                                               (nextval('category_seq'), 'Furniture', (SELECT id FROM category WHERE name = 'Home & Kitchen')),
                                               (nextval('category_seq'), 'Outdoor Gear', (SELECT id FROM category WHERE name = 'Sports & Outdoors')),
                                               (nextval('category_seq'), 'Fitness Equipment', (SELECT id FROM category WHERE name = 'Sports & Outdoors')),
                                               (nextval('category_seq'), 'Fiction', (SELECT id FROM category WHERE name = 'Books')),
                                               (nextval('category_seq'), 'Non-Fiction', (SELECT id FROM category WHERE name = 'Books')),
                                               (nextval('category_seq'), 'Educational Toys', (SELECT id FROM category WHERE name = 'Toys & Games')),
                                               (nextval('category_seq'), 'Board Games', (SELECT id FROM category WHERE name = 'Toys & Games'))
    ON CONFLICT (name) DO NOTHING;

-- Inserindo subcategorias (nível 3)
INSERT INTO category (id, name, parent_id) VALUES
                                               (nextval('category_seq'), 'Smartphones', (SELECT id FROM category WHERE name = 'Phones & Accessories')),
                                               (nextval('category_seq'), 'Chargers & Cables', (SELECT id FROM category WHERE name = 'Phones & Accessories')),
                                               (nextval('category_seq'), 'Laptops', (SELECT id FROM category WHERE name = 'Computers & Laptops')),
                                               (nextval('category_seq'), 'Gaming Laptops', (SELECT id FROM category WHERE name = 'Computers & Laptops')),
                                               (nextval('category_seq'), 'Shirts', (SELECT id FROM category WHERE name = 'Men Clothing')),
                                               (nextval('category_seq'), 'Pants', (SELECT id FROM category WHERE name = 'Men Clothing')),
                                               (nextval('category_seq'), 'Dresses', (SELECT id FROM category WHERE name = 'Women Clothing')),
                                               (nextval('category_seq'), 'Shoes', (SELECT id FROM category WHERE name = 'Women Clothing')),
                                               (nextval('category_seq'), 'Blenders', (SELECT id FROM category WHERE name = 'Kitchen Appliances')),
                                               (nextval('category_seq'), 'Microwaves', (SELECT id FROM category WHERE name = 'Kitchen Appliances')),
                                               (nextval('category_seq'), 'Sofas', (SELECT id FROM category WHERE name = 'Furniture')),
                                               (nextval('category_seq'), 'Tables', (SELECT id FROM category WHERE name = 'Furniture')),
                                               (nextval('category_seq'), 'Tents', (SELECT id FROM category WHERE name = 'Outdoor Gear')),
                                               (nextval('category_seq'), 'Hiking Boots', (SELECT id FROM category WHERE name = 'Outdoor Gear')),
                                               (nextval('category_seq'), 'Dumbbells', (SELECT id FROM category WHERE name = 'Fitness Equipment')),
                                               (nextval('category_seq'), 'Treadmills', (SELECT id FROM category WHERE name = 'Fitness Equipment'))
    ON CONFLICT (name) DO NOTHING;
