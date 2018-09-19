#!/bin/sh
video_id=$1
dir=test/process/$video_id
rm -rf $dir
mkdir -p $dir
./ytb-dl-frames.sh $video_id $dir "-gray"
./threshold-dir.sh $dir "-gray" "-monochrome"
./negate-dir.sh $dir "-monochrome" "-monochrome-neg"
./ocr-dir.sh $dir "-gray" "-gray"
./ocr-dir.sh $dir "-monochrome" "-monochrome"
./ocr-dir.sh $dir "-monochrome-neg" "-monochrome-neg"
