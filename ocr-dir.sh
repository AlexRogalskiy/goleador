#!/bin/sh
dir=$1
in_suffix=$2
out_suffix=$3
for f in $dir/*$in_suffix.png; do
  tesseract $f $dir"/$(basename "$f" "$in_suffix".png)"$out_suffix
done
