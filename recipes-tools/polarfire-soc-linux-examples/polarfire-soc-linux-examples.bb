SUMMARY = "Polarfire SoC Linux example applications"
DESCRIPTION = "Linux Example applications, includes the following \
-iiohttpserver collects ADC measurements and displays them via a webserver.\
-LSRAM read and write example.\
-UIO DMA interrupt example. \ 
"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=06ec214e9fafe6d4515883d77674a453"

DEPENDS = "collectd libgpiod"
inherit systemd

PV = "1.0+git${SRCPV}"
SRCREV="v2023.06"
SRC_URI = "git://github.com/polarfire-soc/polarfire-soc-linux-examples.git;protocol=https;nobranch=1 \
          "
S = "${WORKDIR}/git"

EXAMPLE_FILES = "\
    can \
    dma \
    dt-overlays \
    ethernet \
    fpga-fabric-interfaces/lsram \
    gpio \
    system-services \
    pdma \
    "

EXAMPLE_FILES:append:icicle-kit-es-amp = "\
    amp/rpmsg-pingpong \
    amp/rpmsg-tty-example \
    "

EXAMPLE_FILES:mpfs-video-kit = "\
    pdma \
    "

EXAMPLE_FILES:append:icicle-kit-es = "\
    opcua/icicle-kit \
    "

EXAMPLE_FILES:append:mpfs-video-kit = "\
    opcua/video-kit \
    "

do_compile() {
  for i in ${EXAMPLE_FILES}; do
    if [ -f ${S}/$i/Makefile ]; then
      oe_runmake -C ${S}/$i
    fi
  done
}

INSANE_SKIP_${PN} += "file-rdeps"
INSANE_SKIP:${PN} = "ldflags"
INSANE_SKIP:${PN}-dev = "ldflags"

SECURITY_CFLAGS = ""

do_install() {
  install -d ${D}/opt/microchip
  chmod a+x ${D}/opt/microchip

  for i in ${EXAMPLE_FILES}; do
    install -d ${D}/opt/microchip/`dirname $i`/`basename $i`
    cp -rfd ${S}/$i ${D}/opt/microchip/`dirname $i`

    if [ "${i}" = "ethernet" ]; then
      ## Symbolic Link for iiohttpserver
      ln -s -r ${D}/opt/microchip/ethernet/iio-http-server ${D}/opt/microchip/iiohttpserver

      ## Install the iio-http-server
      install -d ${D}${systemd_unitdir}/system
      install -m 0644 ${S}/ethernet/iio-http-server/collection/collectdiio.service ${D}${systemd_unitdir}/system
    fi

    done
}

do_install:m100pfsevp() {
	:
}

SYSTEMD_SERVICE:${PN}:append:icicle-kit-es = "collectdiio.service"
SYSTEMD_AUTO_ENABLE:${PN}:append:icicle-kit-es = "disable"

SYSTEMD_SERVICE:${PN}:append:icicle-kit-es-amp = "collectdiio.service"
SYSTEMD_AUTO_ENABLE:${PN}:append:icicle-kit-es-amp = "disable"

SYSTEMD_SERVICE:${PN}:append:icicle-kit-es-auth = "collectdiio.service"
SYSTEMD_AUTO_ENABLE:${PN}:append:icicle-kit-es-auth = "disable"

FILES:${PN} += "/opt/microchip/"

