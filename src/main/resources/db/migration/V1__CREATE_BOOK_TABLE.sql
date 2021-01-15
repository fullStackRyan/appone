CREATE TABLE IF NOT EXISTS Book (
  id UUID PRIMARY KEY,
  title varchar(255) NOT NULL,
  author varchar(255) NOT NULL,
  yearOfRelease int
)