#!/bin/bash

FILENAME="inputs/${1}.txt"
make build
make run file=$FILENAME
IMAGENAME="${1}.png"
mv $IMAGENAME me.png
REF="images/${1}_ref.png"

compare -fuzz 2% me.png $REF ae.png
composite me.png $REF -compose difference rawdiff.png
convert rawdiff.png -level 0%,8% diff.png
convert +append $REF me.png ae.png rawdiff.png diff.png look_at_this.png

open look_at_this.png
make clean
