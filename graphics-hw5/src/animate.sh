make build
PREFIX=$1;
FPS=$2;
mkdir $PREFIX;
make bones file=${PREFIX}.txt; 
rm anim.png
mv ${PREFIX}-* $PREFIX
ffmpeg -r $FPS -i ${PREFIX}/${PREFIX}-%03d.png -f apng -plays 0 anim.png
open -a "Google Chrome" anim.png
