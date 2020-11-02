# Installation in Raspian on a RaspberryPi

* Install [Liberica JDK13](https://download.bell-sw.com/java/13/bellsoft-jdk13-linux-arm32-vfp-hflt.deb) which is specifically for Raspian OS and includes JavaFX13.
```
$ cd /home/pi
$ wget https://download.bell-sw.com/java/13/bellsoft-jdk13-linux-arm32-vfp-hflt.deb
$ sudo apt-get install ./bellsoft-jdk13-linux-arm32-vfp-hflt.deb
$ sudo update-alternatives --config javac
$ sudo update-alternatives --config java
```
