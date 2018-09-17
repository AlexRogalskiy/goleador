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
ffmpeg -i $stream -f lavfi -i color=gray:size=${w}x${h} -f lavfi -i color=black:size=${w}x${h} -f lavfi -i color=white:size=${w}x${h} \
 -filter_complex \
 "[0:v]crop=$w:$h:${ow}:${oh}[crop]; \
 [crop][1:v][2:v][3:v]threshold" \
-r $fps $dir/%04d$suffix.png
