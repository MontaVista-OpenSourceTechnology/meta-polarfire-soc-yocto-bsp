IMAGE_INSTALL:append:mpfs-video-kit = " \
    ffmpeg \
    apache2 \
    php \
    php-cli \
    php-fpm \
    php-cgi \
    php-modphp \
    sudo \
    fswebcam \
    v4l2-start-service \
    v4l-utils \
    gstreamer1.0 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-plugins-bad \
    gstreamer1.0-plugins-ugly \
    x264 \
    gst-examples \
    gstreamer1.0-plugins-base-videotestsrc \
    videokit-webdemo \
    python3-asyncua \
    openssl \
    openssl-engines \
    cryptodev-module \
    libkcapi \
    libnl \
    linux-firmware-microchip \
    linuxptp \
    "

IMAGE_INSTALL:append:icicle-kit-es = " \
    python3-asyncua \
    "
