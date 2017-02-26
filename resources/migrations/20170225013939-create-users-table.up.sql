create table if not exists users (
  username        citext PRIMARY KEY,
  password_digest text   NOT NULL
);
