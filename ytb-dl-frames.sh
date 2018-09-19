#!/bin/sh
format=136
fps=0.5
w=500
h=150
ow=0
oh=0
video_id=$1
dir=$2
suffix=$3
stream=$(youtube-dl -f $format -g https://www.youtube.com/watch?v=$video_id)
ffmpeg -i $stream \
 -filter_complex \
 "[0:v]crop=$w:$h:${ow}:${oh}[crop]; \
 [crop]format=gray" \
-r $fps $dir/%04d$suffix.png
