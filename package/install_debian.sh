#!/bin/sh
set -e

echo "[*] 1M5 Desktop installation script"

##### change paths if necessary for your system

# d1, d2, or d3
DESKTOP_TYPE=$1

ROOT_USER=root
ROOT_GROUP=root
ROOT_PKG="build-essential libtool autotools-dev automake pkg-config bsdmainutils python3 git vim screen ufw"
ROOT_HOME=/root

SYSTEMD_SERVICE_HOME=/etc/systemd/system
SYSTEMD_ENV_HOME=/etc/default

1M5_REPO_URL=https://github.com/1m5/1m5
1M5_REPO_NAME=1m5
1M5_REPO_TAG=master
1M5_LATEST_RELEASE=$(curl -s https://api.github.com/repos/1m5/1m5/releases/latest|grep tag_name|head -1|cut -d '"' -f4)
1M5_HOME=/1m5
1M5_USER=1m5

# by default, this script will build and setup bitcoin fullnode
# if you want to use an existing bitcoin fullnode, see next section
BITCOIN_INSTALL=true
BITCOIN_REPO_URL=https://github.com/bitcoin/bitcoin
BITCOIN_REPO_NAME=bitcoin
BITCOIN_REPO_TAG=$(curl -s https://api.github.com/repos/bitcoin/bitcoin/releases/latest|grep tag_name|head -1|cut -d '"' -f4)
BITCOIN_HOME=/bitcoin
BITCOIN_USER=bitcoin
BITCOIN_GROUP=bitcoin
BITCOIN_PKG="libevent-dev libboost-system-dev libboost-filesystem-dev libboost-chrono-dev libboost-test-dev libboost-thread-dev libdb-dev libssl-dev"
BITCOIN_P2P_HOST=127.0.0.1
BITCOIN_P2P_PORT=8333
BITCOIN_RPC_HOST=127.0.0.1
BITCOIN_RPC_PORT=8332

# by default, this script will build and setup monero fullnode
# if you want to use an existing monero fullnode, see next section
MONERO_INSTALL=true
MONERO_REPO_URL=https://github.com/bitcoin/bitcoin
MONERO_REPO_NAME=bitcoin
MONERO_REPO_TAG=$(curl -s https://api.github.com/repos/bitcoin/bitcoin/releases/latest|grep tag_name|head -1|cut -d '"' -f4)
MONERO_HOME=/bitcoin
MONERO_USER=bitcoin
MONERO_GROUP=bitcoin
MONERO_PKG="libevent-dev libboost-system-dev libboost-filesystem-dev libboost-chrono-dev libboost-test-dev libboost-thread-dev libdb-dev libssl-dev"
MONERO_P2P_HOST=127.0.0.1
MONERO_P2P_PORT=8333
MONERO_RPC_HOST=127.0.0.1
MONERO_RPC_PORT=8332

TOR_PKG="tor"
TOR_USER=1m5-tor
TOR_GROUP=1m5-tor
TOR_HOME=/etc/tor

#####

echo "[*] Updating apt repo sources"
sudo -H -i -u "${ROOT_USER}" DEBIAN_FRONTEND=noninteractive apt-get update -q

echo "[*] Upgrading OS packages"
sudo -H -i -u "${ROOT_USER}" DEBIAN_FRONTEND=noninteractive apt-get upgrade -qq -y

echo "[*] Installing base packages"
sudo -H -i -u "${ROOT_USER}" DEBIAN_FRONTEND=noninteractive apt-get install -qq -y ${ROOT_PKG}

echo "[*] Cloning 1M5 repo"
sudo -H -i -u "${ROOT_USER}" git config --global advice.detachedHead false
sudo -H -i -u "${ROOT_USER}" git clone --branch "${1M5_REPO_TAG}" "${1M5_REPO_URL}" "${ROOT_HOME}/${1M5_REPO_NAME}"

echo "[*] Installing Tor"
sudo -H -i -u "${ROOT_USER}" DEBIAN_FRONTEND=noninteractive apt-get install -qq -y ${TOR_PKG}

echo "[*] Installing Tor configuration"
sudo -H -i -u "${ROOT_USER}" install -c -m 644 "${ROOT_HOME}/${1M5_REPO_NAME}/desktop/torrc" "${TOR_HOME}/torrc"

if [ "${BITCOIN_INSTALL}" = true ];then

	echo "[*] Creating Bitcoin user with Tor access"
	sudo -H -i -u "${ROOT_USER}" useradd -d "${BITCOIN_HOME}" -G "${TOR_GROUP}" "${BITCOIN_USER}"

	echo "[*] Installing Bitcoin build dependencies"
	sudo -H -i -u "${ROOT_USER}" DEBIAN_FRONTEND=noninteractive apt-get install -qq -y ${BITCOIN_PKG}

	echo "[*] Creating Bitcoin homedir"
	sudo -H -i -u "${ROOT_USER}" mkdir -p "${BITCOIN_HOME}"
	sudo -H -i -u "${ROOT_USER}" chown "${BITCOIN_USER}":"${BITCOIN_GROUP}" ${BITCOIN_HOME}
	sudo -H -i -u "${BITCOIN_USER}" ln -s . .bitcoin

	echo "[*] Cloning Bitcoin repo"
	sudo -H -i -u "${BITCOIN_USER}" git config --global advice.detachedHead false
	sudo -H -i -u "${BITCOIN_USER}" git clone --branch "${BITCOIN_REPO_TAG}" "${BITCOIN_REPO_URL}" "${BITCOIN_HOME}/${BITCOIN_REPO_NAME}"

	echo "[*] Building Bitcoin from source"
	sudo -H -i -u "${BITCOIN_USER}" sh -c "cd ${BITCOIN_REPO_NAME} && ./autogen.sh --quiet && ./configure --quiet --disable-wallet --with-incompatible-bdb && make -j9"

	echo "[*] Installing Bitcoin into OS"
	sudo -H -i -u "${ROOT_USER}" sh -c "cd ${BITCOIN_HOME}/${BITCOIN_REPO_NAME} && make install >/dev/null"

	echo "[*] Installing Bitcoin configuration"
	sudo -H -i -u "${ROOT_USER}" install -c -o "${BITCOIN_USER}" -g "${BITCOIN_GROUP}" -m 644 "${ROOT_HOME}/${1M5_REPO_NAME}/desktop/bitcoin.conf" "${BITCOIN_HOME}/bitcoin.conf"
	sudo -H -i -u "${ROOT_USER}" install -c -o "${BITCOIN_USER}" -g "${BITCOIN_GROUP}" -m 755 "${ROOT_HOME}/${1M5_REPO_NAME}/desktop/blocknotify.sh" "${BITCOIN_HOME}/blocknotify.sh"

	echo "[*] Generating Bitcoin RPC credentials"
	BITCOIN_RPC_USER=$(head -150 /dev/urandom | md5sum | awk '{print $1}')
	sudo sed -i -e "s/__BITCOIN_RPC_USER__/${BITCOIN_RPC_USER}/" "${BITCOIN_HOME}/bitcoin.conf"
	BITCOIN_RPC_PASS=$(head -150 /dev/urandom | md5sum | awk '{print $1}')
	sudo sed -i -e "s/__BITCOIN_RPC_PASS__/${BITCOIN_RPC_PASS}/" "${BITCOIN_HOME}/bitcoin.conf"

	echo "[*] Installing Bitcoin init scripts"
	sudo -H -i -u "${ROOT_USER}" install -c -o "${ROOT_USER}" -g "${ROOT_GROUP}" -m 644 "${ROOT_HOME}/${1M5_REPO_NAME}/desktop/bitcoin.service" "${SYSTEMD_SERVICE_HOME}"

fi

echo "[*] Creating 1M5 user with Tor access"
sudo -H -i -u "${ROOT_USER}" useradd -d "${1M5_HOME}" -G "${TOR_GROUP}" "${1M5_USER}"

echo "[*] Creating 1M5 homedir"
sudo -H -i -u "${ROOT_USER}" mkdir -p "${1M5_HOME}"
sudo -H -i -u "${ROOT_USER}" chown "${1M5_USER}":"${1M5_GROUP}" ${1M5_HOME}

echo "[*] Moving 1M5 repo"
sudo -H -i -u "${ROOT_USER}" mv "${ROOT_HOME}/${1M5_REPO_NAME}" "${1M5_HOME}/${1M5_REPO_NAME}"
sudo -H -i -u "${ROOT_USER}" chown -R "${1M5_USER}:${1M5_GROUP}" "${1M5_HOME}/${1M5_REPO_NAME}"

echo "[*] Installing OpenJDK from 1M5 repo"
sudo -H -i -u "${ROOT_USER}" "${1M5_HOME}/${1M5_REPO_NAME}/scripts/install_java.sh"

echo "[*] Installing 1M5 init script"
sudo -H -i -u "${ROOT_USER}" install -c -o "${ROOT_USER}" -g "${ROOT_GROUP}" -m 644 "${1M5_HOME}/${1M5_REPO_NAME}/desktop/1m5.service" "${SYSTEMD_SERVICE_HOME}/1m5.service"
if [ "${BITCOIN_INSTALL}" = true ];then
	sudo sed -i -e "s/#Requires=bitcoin.service/Requires=bitcoin.service/" "${SYSTEMD_SERVICE_HOME}/1m5.service"
	sudo sed -i -e "s/#BindsTo=bitcoin.service/BindsTo=bitcoin.service/" "${SYSTEMD_SERVICE_HOME}/1m5.service"
fi
sudo sed -i -e "s/__1M5_REPO_NAME__/${1M5_REPO_NAME}/" "${SYSTEMD_SERVICE_HOME}/1m5.service"
sudo sed -i -e "s!__1M5_HOME__!${1M5_HOME}!" "${SYSTEMD_SERVICE_HOME}/1m5.service"

echo "[*] Installing 1M5 environment file with Bitcoin RPC credentials"
sudo -H -i -u "${ROOT_USER}" install -c -o "${ROOT_USER}" -g "${ROOT_GROUP}" -m 644 "${1M5_HOME}/${1M5_REPO_NAME}/desktop/1m5.env" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s/__BITCOIN_P2P_HOST__/${BITCOIN_P2P_HOST}/" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s/__BITCOIN_P2P_PORT__/${BITCOIN_P2P_PORT}/" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s/__BITCOIN_RPC_HOST__/${BITCOIN_RPC_HOST}/" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s/__BITCOIN_RPC_PORT__/${BITCOIN_RPC_PORT}/" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s/__BITCOIN_RPC_USER__/${BITCOIN_RPC_USER}/" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s/__BITCOIN_RPC_PASS__/${BITCOIN_RPC_PASS}/" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s!__1M5_APP_NAME__!${1M5_APP_NAME}!" "${SYSTEMD_ENV_HOME}/1m5.env"
sudo sed -i -e "s!__1M5_HOME__!${1M5_HOME}!" "${SYSTEMD_ENV_HOME}/1m5.env"

echo "[*] Checking out 1M5 ${1M5_LATEST_RELEASE}"
sudo -H -i -u "${1M5_USER}" sh -c "cd ${1M5_HOME}/${1M5_REPO_NAME} && git checkout ${1M5_LATEST_RELEASE}"

echo "[*] Building 1M5 from source"
sudo -H -i -u "${1M5_USER}" sh -c "cd ${1M5_HOME}/${1M5_REPO_NAME} && ./gradlew build -x test < /dev/null" # redirect from /dev/null is necessary to workaround gradlew non-interactive shell hanging issue

echo "[*] Updating systemd daemon configuration"
sudo -H -i -u "${ROOT_USER}" systemctl daemon-reload
sudo -H -i -u "${ROOT_USER}" systemctl enable tor.service
sudo -H -i -u "${ROOT_USER}" systemctl enable 1m5.service
if [ "${BITCOIN_INSTALL}" = true ];then
	sudo -H -i -u "${ROOT_USER}" systemctl enable bitcoin.service
fi

echo "[*] Preparing firewall"
sudo -H -i -u "${ROOT_USER}" ufw default deny incoming
sudo -H -i -u "${ROOT_USER}" ufw default allow outgoing

echo "[*] Starting Tor"
sudo -H -i -u "${ROOT_USER}" systemctl start tor

if [ "${BITCOIN_INSTALL}" = true ];then
	echo "[*] Starting Bitcoin"
	sudo -H -i -u "${ROOT_USER}" systemctl start bitcoin
	sudo -H -i -u "${ROOT_USER}" journalctl --no-pager --unit bitcoin
	sudo -H -i -u "${ROOT_USER}" tail "${BITCOIN_HOME}/debug.log"
fi

echo "[*] Adding notes to motd"
sudo -H -i -u "${ROOT_USER}" sh -c 'echo " " >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo "1M5 Desktop instructions:" >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo "https://github.com/1m5/1m5/tree/master/desktop" >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo " " >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo "How to check logs for 1M5-Desktop service:" >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo "sudo journalctl --no-pager --unit 1m5" >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo " " >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo "How to restart 1M5-Desktop service:" >> /etc/motd'
sudo -H -i -u "${ROOT_USER}" sh -c 'echo "sudo service 1m5 restart" >> /etc/motd'

echo '[*] Done!'

echo '  '
echo '[*] DONT FORGET TO ENABLE FIREWALL!!!11'
echo '[*] Follow all the README instructions!'
echo '  '
