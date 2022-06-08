ffmpeg -r 8 -i ${1}-%03d.png -f apng -plays 0 anim.png
open -a "Google Chrome" anim.png


