require recipes-kernel/linux/mpfs-linux-common.inc

LINUX_VERSION ?= "6.1"
KERNEL_VERSION_SANITY_SKIP="1"

SRCREV="linux4microchip+fpga-2023.06"
SRC_URI = " \
    git://github.com/linux4microchip/linux.git;protocol=https;nobranch=1 \
"
do_assemble_fitimage[depends] += "dt-overlay-mchp:do_deploy"

SRC_URI:append:icicle-kit-es-amp = "file://mpfs_amp_cmdline.cfg"

SRC_URI:append:icicle-kit = " file://mpfs_cmdline.cfg \
                              file://qspi_flash.cfg \
                              file://rpi_sense_hat.cfg \
                            "

SRC_URI:append:mpfs-video-kit = " file://mpfs_cmdline.cfg \
                                  file://mpfs-v4l2.cfg \
                                  "

SRC_URI:append:m100pfsevp = "file://m100pfsevp_configs.cfg"


do_deploy:append() {

    if [ -n "${INITRAMFS_IMAGE}" ]; then

        if [ "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
                ln -snf fitImage-${INITRAMFS_IMAGE_NAME}-${KERNEL_FIT_NAME}${KERNEL_FIT_BIN_EXT} "$deployDir/fitImage"
        fi
    fi
}

addtask deploy after do_install

