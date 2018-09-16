#!/bin/sh
video_id=$1
frames_dir=$video_id
texts_dir=$video_id
rm -rf $video_id
mkdir -p $video_id
#rm -rf $frames_dir
#mkdir -p $frames_dir
./ytb-dl-frames.sh $video_id $frames_dir
#rm -rf $texts_dir
#mkdir -p $texts_dir
./ocr-dir.sh $frames_dir $texts_dir
