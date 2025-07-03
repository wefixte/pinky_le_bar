-- Ajout de catégories
INSERT INTO category (name) VALUES
('Classique'),
('Tropical'),
('Sans alcool'),
('Épicé'),
('Dessert')
ON CONFLICT DO NOTHING;

-- Ajout d'ingrédients
INSERT INTO ingredient (name) VALUES
('Rhum blanc'),
('Jus de citron'),
('Sucre de canne'),
('Menthe'),
('Glace pilée'),
('Vodka'),
('Triple sec'),
('Limonade'),
('Glaçons'),
('Tequila'),
('Jus de tomate'),
('Tabasco'),
('Worcestershire sauce'),
('Sel de céleri'),
('Piment de Cayenne'),
('Gin'),
('Vermouth sec'),
('Angostura bitters'),
('Champagne'),
('Cognac'),
('Crème fraîche'),
('Cacao amer'),
('Orange sanguine'),
('Fraise écrasée'),
('Sirop de fraise'),
('Liqueur de pêche'),
('Lait de coco'),
('Sirop de grenadine'),
('Lime'),
('Gingembre'),
('Miel'),
('Cointreau'),
('Crème de cacao'),
('Café expresso'),
('Lait'),
('Vanille'),
('Cannelle'),
('Poivre')
ON CONFLICT DO NOTHING;

-- Récupération des ids des catégories
WITH cat_ids AS (
    SELECT id, name FROM category WHERE name IN ('Classique', 'Tropical', 'Épicé', 'Dessert')
),
-- Insertion des cocktails
insert_cocktails AS (
    INSERT INTO cocktail (name, description, category_id, image_url) VALUES
    ('Pink Dream', 'Cocktail doux et fruité aux notes de fraise et citron vert.', (SELECT id FROM cat_ids WHERE name='Classique'), 'https://i.pinimg.com/736x/5d/5e/f5/5d5ef5f6f136b9c8d4a2067e7a837322.jpg'),
    ('Chic Sunset', 'Explosion tropicale avec rhum et touche d’orange sanguine.', (SELECT id FROM cat_ids WHERE name='Tropical'), 'https://i.pinimg.com/736x/99/91/0d/99910df8b368424d819ec7f12a8073b7.jpg'),
    ('Pink Spice', 'Cocktail relevé, mélange de gin, piment de Cayenne et citron vert.', (SELECT id FROM cat_ids WHERE name='Épicé'), 'https://i.pinimg.com/736x/03/cf/4b/03cf4bfdede6563f1b93bec30c9a8ce9.jpg'),
    ('Sweet Pink Bliss', 'Douceur lactée avec crème, fraise et vanille.', (SELECT id FROM cat_ids WHERE name='Dessert'), 'https://i.pinimg.com/736x/66/25/94/662594abe7ddf5df3227747a31eed0d9.jpg')
    RETURNING id, name
),
-- Récupération des ingrédients nécessaires
ingredient_ids AS (
    SELECT ingredient_id, name FROM ingredient WHERE name IN
    ('Vodka', 'Sirop de fraise', 'Jus de citron', 'Glaçons',
     'Rhum blanc', 'Orange sanguine', 'Sirop de grenadine', 'Glace pilée',
     'Gin', 'Piment de Cayenne', 'Sucre de canne',
     'Crème fraîche', 'Fraise écrasée', 'Vanille', 'Lait')
)
-- Insertion des associations cocktail-ingredient avec quantité arbitraire
INSERT INTO cocktail_ingredient (cocktail_id, ingredient_id, quantity)
SELECT c.id, i.ingredient_id,
CASE 
    WHEN i.name IN ('Glaçons', 'Glace pilée') THEN 50
    WHEN i.name IN ('Sirop de fraise', 'Sirop de grenadine', 'Sucre de canne', 'Piment de Cayenne') THEN 15
    WHEN i.name IN ('Vodka', 'Rhum blanc', 'Gin', 'Crème fraîche', 'Lait') THEN 40
    WHEN i.name IN ('Jus de citron', 'Orange sanguine', 'Fraise écrasée', 'Vanille') THEN 20
    ELSE 10
END as quantity
FROM insert_cocktails c
JOIN ingredient_ids i ON
    (c.name = 'Pink Dream' AND i.name IN ('Vodka', 'Sirop de fraise', 'Jus de citron', 'Glaçons'))
    OR (c.name = 'Chic Sunset' AND i.name IN ('Rhum blanc', 'Orange sanguine', 'Sirop de grenadine', 'Glace pilée'))
    OR (c.name = 'Pink Spice' AND i.name IN ('Gin', 'Piment de Cayenne', 'Jus de citron', 'Sucre de canne'))
    OR (c.name = 'Sweet Pink Bliss' AND i.name IN ('Crème fraîche', 'Fraise écrasée', 'Vanille', 'Lait'));

-- Ajout des tailles et prix pour chaque cocktail
WITH cts AS (
    SELECT id, name FROM cocktail WHERE name IN ('Pink Dream', 'Chic Sunset', 'Pink Spice', 'Sweet Pink Bliss')
)
INSERT INTO cocktail_size (cocktail_id, size, price) VALUES
((SELECT id FROM cts WHERE name='Pink Dream'), 'S', 6.0),
((SELECT id FROM cts WHERE name='Pink Dream'), 'M', 8.0),
((SELECT id FROM cts WHERE name='Pink Dream'), 'L', 10.0),

((SELECT id FROM cts WHERE name='Chic Sunset'), 'S', 7.0),
((SELECT id FROM cts WHERE name='Chic Sunset'), 'M', 9.0),
((SELECT id FROM cts WHERE name='Chic Sunset'), 'L', 11.0),

((SELECT id FROM cts WHERE name='Pink Spice'), 'S', 6.5),
((SELECT id FROM cts WHERE name='Pink Spice'), 'M', 8.5),
((SELECT id FROM cts WHERE name='Pink Spice'), 'L', 10.5),

((SELECT id FROM cts WHERE name='Sweet Pink Bliss'), 'S', 7.5),
((SELECT id FROM cts WHERE name='Sweet Pink Bliss'), 'M', 9.5),
((SELECT id FROM cts WHERE name='Sweet Pink Bliss'), 'L', 11.5);
