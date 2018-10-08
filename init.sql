CREATE TABLE video (
  id bigserial PRIMARY KEY,
  video_id varchar(20) UNIQUE NOT NULL,
  times varchar(255),
  error varchar(255),
  comment_id varchar(50) UNIQUE
);
