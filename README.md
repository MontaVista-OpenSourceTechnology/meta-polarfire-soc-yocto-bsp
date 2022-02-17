# Microchip PolarFire SoC Yocto BSP

Microchip Polarfire-SoC Yocto 'Board Support Package' (BSP) is based on OpenEmbedded (OE). The 'Polarfire SoC Yocto BSP' layer is build on top of the RISC-V Architectural layer (meta-riscv) to provide hardware specific features and additional disk images. 
Using Yocto 'Openembedded' you will build the following:

  - RISC-V Toolchain
  - Predefined Disk Images 
  - Bootloader Binaries (FSBL / U-Boot)
  - Device Tree Binary (DTB)
  - Linux Kernel Images

The complete User Guides for each development platform, containing board and boot instructions, are available for the following supported platforms:

  - [ICICLE-KIT-ES](https://github.com/polarfire-soc/polarfire-soc-documentation/blob/master/boards/mpfs-icicle-kit-es/icicle-kit-sw-developer-guide/icicle-kit-sw-developer-guide.md) (Icicle Kit Engineering Sample) (Requires Design Tag 2021.11 )
  
## Build Instructions 
Before continuing, ensure that the prerequisite packages are present on your system. Please see the [Host PC setup for Yocto section](#Dependencies) for further details.

### Create the Workspace
This needs to be done every time you want a clean setup based on the latest BSP.
```bash
mkdir yocto-dev && cd yocto-dev
repo init -u https://github.com/polarfire-soc/meta-polarfire-soc-yocto-bsp.git -b 2021.11 -m tools/manifests/riscv-yocto.xml
```
### Update the repo workspace
```bash
repo sync
repo rebase
```
### Setup Bitbake environment
```bash
. ./meta-polarfire-soc-yocto-bsp/polarfire-soc_yocto_setup.sh
```
### Building board Disk Image
Using yocto bitbake command and setting the MACHINE and image requried.
```bash
MACHINE=icicle-kit-es bitbake mpfs-dev-cli
```
### Copy the created Disk Image to flash device (USB mmc flash/SD/uSD)

> Be very careful while picking /dev/sdX device! Look at dmesg, lsblk, GNOME Disks, etc. before and after plugging in your usb flash device/uSD/SD to find a proper device. Double check it to avoid overwriting any of system disks/partitions!
> 
```bash
cd yocto-dev/build
zcat tmp-glibc/deploy/images/icicle-kit-es/mpfs-dev-cli-icicle-kit-es.wic.gz | sudo dd of=/dev/sdX bs=4096 iflag=fullblock oflag=direct conv=fsync status=progress
```

### Supported Machine Targets
The `MACHINE` (board) option can be used to set the target board for which linux is built, and if left blank it will default to `MACHINE=icicle-kit-es`.           
The following table details the available targets:

| `MACHINE`                  | Board Name                                                            |
| -------------------------- | --------------------------------------------------------------------- |
| `MACHINE=icicle-kit-es`    | ICICLE-KIT-ES, Icicle Kit engineering samples                         |
| `MACHINE=icicle-kit-es-amp`| ICICLE-KIT-ES, Icicle Kit engineering samples in AMP mode             |
| `MACHINE=qemuriscv64`      | Simulation                                                            |

The `icicle-kit-es-amp` machine can be used to build the Icicle Kit engineering sample with AMP support. Please see the [Asymmetric Multiprocessing (AMP)](https://github.com/polarfire-soc/polarfire-soc-documentation/blob/master/asymmetric-multiprocessing/amp.md) documentation for further details.

When building for different 'Machines' or want a 'clean' build, we recommend deleting the 'build' directory when switching.
This will delete all cache / configurations and downloads. 
```
cd yocto-dev
rm -rf build
```

### Linux Images
The table below summarizes the most common Linux images that can be built using this BSP.

| bitbake `<image>` argument | Description                                        |
| -------------------------- | -------------------------------------------------- |
| `core-image-minimal-dev`   | A small console image with some development tools. |
| `mpfs-dev-cli`             | A console image with development tools.            |

For more information on available images refer to [Yocto reference manual](https://docs.yoctoproject.org/ref-manual/images.html)

#### Target machine Linux login
Login with `root` account, there is no password set.


## Bitbake commands

With the bitbake environment setup, execute the bitbake command in the following format to build the disk images.
```bash
MACHINE=<machine> bitbake <image>
```
Example building the icicle-kit-es machine and the mpfs-dev-cli Linux image
```bash
MACHINE=icicle-kit-es bitbake mpfs-dev-cli
```
To work with individual recipes:
```bash
MACHINE=<MACHINE> bitbake <recipe> -c <command>
```
View/Edit the Kernel menuconfig:
```bash
MACHINE=<MACHINE> bitbake mpfs-linux -c menuconfig
```
Run the diffconfig command to prepare a configuration fragment. 
The resulting file fragment.cfg will be placed in the recipes-kernel/linux/files directory:
```bash
MACHINE=<MACHINE> bitbake mpfs-linux -c diffconfig
```

**Available BSP recipes:**
 
  - hss (Microchip Hart Software Services) payload generator
  - u540-c000-bootloader (Sifive FSBL)
  - u-boot 
  - mpfs-linux (BSP kernel)
  
**Available commands:**

  - clean
  - configure
  - compile
  - install

### Yocto Image and Binaries directory
```
build/tmp-glibc/deploy/images/{MACHINE}
```
For Example the following is the path for the Icicle-kit-es
```
build/tmp-glibc/deploy/images/icicle-kit-es
```

### Running wic.gz image on hardware

Compressed Disk images files use `<image>-<machine>.wic.gz` format, for example,

`mpfs-dev-cli-<machine>.wic.gz`. We are interested in `.wic.gz` disk images for writing to emmc/uSD/SD card.

> Be very careful while picking /dev/sdX device! Look at dmesg, lsblk, GNOME Disks, etc. before and after plugging in your usb flash device/uSD/SD to find a proper device. Double check it to avoid overwriting any of system disks/partitions!
> 
> Unmount any mounted partitions from uSD card before writing!
> 
> We advice to use 16GB or 32GB uSD cards. 8GB cards (shipped with HiFive Unleashed) can still be used with CLI images.

Example write the disk image to the SD card for the icicle kit:

```bash
cd yocto-dev/build
zcat tmp-glibc/deploy/images/icicle-kit-es/mpfs-dev-cli-icicle-kit-es.wic.gz | sudo dd of=/dev/sdX bs=4096 iflag=fullblock oflag=direct conv=fsync status=progress
```
## Run in Simulation (QEMU)

```bash
MACHINE=qemuriscv64 runqemu nographic
```

<a name="Dependencies"></a>
## Host PC setup for Yocto

### Yocto Dependencies
This document assumes you are running on a modern Linux system. The process documented here was tested using Ubuntu 18.04 LTS. 
It should also work with other Linux distributions if the equivalent prerequisite packages are installed.

The BSP uses the Yocto RISCV Architecture Layer, and the Yocto release Honister (Revision 3.4)	(Released April 2020).

**Make sure to install the [repo command](https://source.android.com/setup/develop#installing-repo) by Google first.**

Detailed instructions for various distributions can be found in the "[Required Packages for the Build Host](https://docs.yoctoproject.org/3.4/ref-manual/system-requirements.html#required-packages-for-the-build-host)" section in the Yocto Project Reference Manual.

```bash
**Note: Some extra packages are requried to support this Yocto 3.4 Release (codename “honister”) from the prior release.**
```

<a name="OtherDeps"></a>
### Other Dependencies

For Ubuntu 18.04 (or newer) install python3-distutils:
```
sudo apt install python3-distutils
```
HSS Payload Generator uses libelf and libyaml, as well as zlib (a dependency of libelf).

```
sudo apt-get install libyaml-dev
sudo apt-get install libelf-dev
```

## Additional Reading

[Yocto Overview Manual](https://docs.yoctoproject.org/overview-manual/index.html) 

[Yocto Development Task Manual](https://docs.yoctoproject.org/dev-manual/index.html) 
 
[Yocto Bitbake User Manual](https://docs.yoctoproject.org/bitbake/index.html)

[Yocto Application Development and Extensible Software Development Kit (sSDK)](https://docs.yoctoproject.org/sdk-manual/index.html) 
 
[PolarFire SoC Buildroot BSP](https://github.com/polarfire-soc/polarfire-soc-buildroot-sdk) 
 
[U-Boot Documentation](https://www.denx.de/wiki/U-Boot/Documentation) 

[Kernel Documentation for v5.12](https://www.kernel.org/doc/html/v5.12/) 

## Known issues

### Issue 001: Data Transfer issues using M.2 PCIe NVMe 
PCIe device is detected/enumerated correctly and transfer of small files (< ~50Mb>) operate correctly. When trying to transfer larger files it appears to lock up the device.

While some NVMe drives have been shown to work, they will not all operate correctly. This issue is currently under investigation.

### Issue 002 fs.inotify.max_user_watches

Listen uses inotify by default on Linux to monitor directories for changes. It's not uncommon to encounter a system limit on the number of files you can monitor. For example, Ubuntu Lucid's (64bit) inotify limit is set to 8192.

You can get your current inotify file watch limit by executing:

```bash
$ cat /proc/sys/fs/inotify/max_user_watches
```

When this limit is not enough to monitor all files inside a directory, the limit must be increased for Listen to work properly.

Run the following command:

```bash
echo fs.inotify.max_user_watches=524288 | sudo tee -a /etc/sysctl.conf && sudo sysctl -p
```

[Details on max_user_watches](https://github.com/guard/listen/wiki/Increasing-the-amount-of-inotify-watchers)

See [Other Dependencies](#OtherDeps) for installation instructions.
