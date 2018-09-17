#!/bin/sh
video_id=$1
dir=test/$video_id
rm -rf $dir
mkdir -p $dir
./ytb-dl-frames.sh $video_id $dir "-mono"
./ocr-dir.sh $dir "-mono" "-mono"
