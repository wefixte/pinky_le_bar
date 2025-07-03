-- Table: user
CREATE TABLE IF NOT EXISTS "user" (
    user_id BIGSERIAL PRIMARY KEY,  -- BIGSERIAL au lieu de SERIAL
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Table: category
CREATE TABLE IF NOT EXISTS category (
    id BIGSERIAL PRIMARY KEY,       -- BIGSERIAL déjà OK
    name VARCHAR(100) NOT NULL
);

-- Table: ingredient
CREATE TABLE IF NOT EXISTS ingredient (
    ingredient_id BIGSERIAL PRIMARY KEY,  -- BIGSERIAL ici aussi
    name VARCHAR(100) NOT NULL
);

-- Table: cocktail
CREATE TABLE IF NOT EXISTS cocktail (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT REFERENCES category(id) ON DELETE SET NULL,
    image_url VARCHAR(255)
);

-- Table: cocktail_ingredient (association)
CREATE TABLE IF NOT EXISTS cocktail_ingredient (
    cocktail_id BIGINT NOT NULL REFERENCES cocktail(id) ON DELETE CASCADE,
    ingredient_id BIGINT NOT NULL REFERENCES ingredient(ingredient_id) ON DELETE CASCADE,
    quantity FLOAT NOT NULL,
    PRIMARY KEY (cocktail_id, ingredient_id)
);

-- Table: cocktail_size
CREATE TABLE IF NOT EXISTS cocktail_size (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    cocktail_id BIGINT NOT NULL REFERENCES cocktail(id) ON DELETE CASCADE,
    size VARCHAR(255) NOT NULL CHECK (size IN ('S', 'M', 'L')),
    price DOUBLE PRECISION NOT NULL
);

-- Table: order
CREATE TABLE IF NOT EXISTS "order" (
    order_id BIGSERIAL PRIMARY KEY,          -- BIGSERIAL ici aussi
    user_id BIGINT NOT NULL REFERENCES "user"(user_id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- Table: order_item
CREATE TABLE IF NOT EXISTS order_item (
    item_id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(order_id) ON DELETE CASCADE,
    size_id BIGINT NOT NULL REFERENCES cocktail_size(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    step VARCHAR(50) NOT NULL
);
