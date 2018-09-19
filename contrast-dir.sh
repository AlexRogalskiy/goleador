#!/bin/sh
dir=$1
in_suffix=$2
out_suffix=$3
for f in $dir/*$in_suffix.png; do
  convert $f -brightness-contrast 0x100 $dir"/$(basename "$f" "$in_suffix".png)"$out_suffix".png"
done
