# Xenial-specific ulog runtime requirements, undo this beyond xenial.
. /etc/lsb-release

if [[ ${DISTRIB_RELEASE} < 18.04 ]]; then
	cat > /etc/apt/sources.list.d/ulog.list <<EOF
deb [arch=amd64 trusted=yes] http://repo.test.netflix.net/artifactory/debian-local $DISTRIB_CODENAME ulogbackport
EOF
fi

