-- Создание таблицы скинов
CREATE TABLE skins (
                       id BIGSERIAL PRIMARY KEY,
                       market_hash_name VARCHAR(255) UNIQUE NOT NULL,
                       weapon_type VARCHAR(100),
                       skin_name VARCHAR(100),
                       wear VARCHAR(50),
                       rarity VARCHAR(50),
                       image_url TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание таблицы истории цен
CREATE TABLE price_history (
                               id BIGSERIAL PRIMARY KEY,
                               skin_id BIGINT NOT NULL,
                               price DECIMAL(10, 2) NOT NULL,
                               volume INT,
                               source VARCHAR(50),
                               recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               CONSTRAINT fk_skin FOREIGN KEY (skin_id) REFERENCES skins(id) ON DELETE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_skins_market_hash ON skins(market_hash_name);
CREATE INDEX idx_skins_weapon_type ON skins(weapon_type);
CREATE INDEX idx_price_history_skin_id ON price_history(skin_id);
CREATE INDEX idx_price_history_recorded_at ON price_history(recorded_at DESC);
CREATE INDEX idx_price_history_skin_date ON price_history(skin_id, recorded_at DESC);

-- Комментарии к таблицам
COMMENT ON TABLE skins IS 'Таблица с информацией о скинах CS:GO';
COMMENT ON TABLE price_history IS 'Таблица с историей цен скинов';

COMMENT ON COLUMN skins.market_hash_name IS 'Уникальное имя скина на Steam Market (например: AK-47 | Redline (Field-Tested))';
COMMENT ON COLUMN skins.weapon_type IS 'Тип оружия (Rifle, Pistol, Knife и т.д.)';
COMMENT ON COLUMN skins.skin_name IS 'Название скина (например: Redline)';
COMMENT ON COLUMN skins.wear IS 'Степень износа (Factory New, Minimal Wear, Field-Tested, Well-Worn, Battle-Scarred)';
COMMENT ON COLUMN skins.rarity IS 'Редкость скина (Consumer, Industrial, Mil-Spec, Restricted, Classified, Covert, Contraband)';

COMMENT ON COLUMN price_history.price IS 'Цена в долларах США';
COMMENT ON COLUMN price_history.volume IS 'Объём продаж за последние 24 часа';
COMMENT ON COLUMN price_history.source IS 'Источник данных (Steam Market, CSGOFloat, etc.)';