CREATE TABLE video (
  id bigserial PRIMARY KEY,
  video_id varchar(20) UNIQUE NOT NULL,
  times varchar(255),
  worker_error varchar(255)
);
