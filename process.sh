#!/bin/sh
video_id=$1
dir=test/process/$video_id
rm -rf $dir
mkdir -p $dir
./ytb-dl-frames.sh $video_id $dir "-gray"
./contrast-dir.sh $dir "-gray" "-contrast"
./negate-dir.sh $dir "-contrast" "-contrast-neg"
./ocr-dir.sh $dir "-contrast" "-contrast"
./ocr-dir.sh $dir "-contrast-neg" "-contrast-neg"
