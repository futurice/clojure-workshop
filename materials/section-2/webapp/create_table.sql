CREATE TABLE todo(
  id serial PRIMARY KEY NOT NULL,
  name varchar(50) NOT NULL,
  done BOOL DEFAULT FALSE
);
