#!/bin/sh

format="136"
if [ -n "$1" ]; then format="$1"; fi
dir="test/video/${format}"

if [ ! -d $dir ]; then
    mkdir -p $dir
fi

for video_id in \
    "-qGLWEaa47k" \
    "5VMS71fitI4" \
    "cLjn6oF1E9Q" \
    "C9hwnys6qXM" \
    "D6hdF7gChmE" \
    "fM7TtiC-j_w" \
    "gLQf3Zp2n6g" \
    "KyW4keXAT3s" \
    "PKzvJgRx1Zw" \
    "QYlSNDwrq40" \
    "yE33DcpNZkw" \
    "Xf5z_awHVKw" \
    "ZdFEZlepWJI" \
    ; do
    youtube-dl -f $format "https://www.youtube.com/watch?v=${video_id}" -o "${dir}/%(id)s.%(ext)s"
done
