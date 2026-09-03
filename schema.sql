CREATE TABLE IF NOT EXISTS status (
  user TEXT PRIMARY KEY,
  last_seen TEXT NOT NULL,
  internet INTEGER NOT NULL DEFAULT 0,
  screen_on INTEGER NOT NULL DEFAULT 0,
  online_since TEXT
);
INSERT OR IGNORE INTO status(user,last_seen,internet,screen_on,online_since) VALUES ('sepehr','1970-01-01T00:00:00Z',0,0,NULL);
INSERT OR IGNORE INTO status(user,last_seen,internet,screen_on,online_since) VALUES ('amir','1970-01-01T00:00:00Z',0,0,NULL);
