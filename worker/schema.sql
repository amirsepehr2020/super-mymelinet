CREATE TABLE IF NOT EXISTS presence (
  user_id TEXT PRIMARY KEY,
  internet_online INTEGER NOT NULL DEFAULT 0,
  screen_on INTEGER NOT NULL DEFAULT 0,
  last_seen INTEGER NOT NULL DEFAULT 0
);

INSERT OR IGNORE INTO presence(user_id) VALUES ('sepehr');
INSERT OR IGNORE INTO presence(user_id) VALUES ('amir');
