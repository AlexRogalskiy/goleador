CREATE TABLE video (
  id bigserial PRIMARY KEY,
  video_id varchar(20) UNIQUE NOT NULL,
  title varchar(255),
  definition varchar(2),
  published_at bigint,
  processing_started_at bigint,
  times varchar(255),
  error varchar(255),
  comment_id varchar(50) UNIQUE
);

CREATE TABLE channel (
  id bigserial PRIMARY KEY,
  channel_id varchar(30) UNIQUE NOT NULL,
  since bigint
);
