CREATE TABLE IF NOT EXISTS Book (
  id varchar(255) PRIMARY KEY,
  title varchar(255) NOT NULL,
  author varchar(255) NOT NULL,
  yearOfRelease int
)