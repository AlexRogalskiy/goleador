#!/bin/sh
for f in $1/*.png;do tesseract $f $2"/$(basename "$f" .png)";done
