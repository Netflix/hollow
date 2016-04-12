
. /etc/profile.d/netflix_environment.sh

JRE_HOME=/apps/java
JAVA_HOME=$JRE_HOME
JAVA_OPTS=""

if [ -r "$CATALINA_BASE"/conf/logging.properties ]; then
    LOGGING_CONFIG="-Djava.util.logging.config.file=$CATALINA_BASE/conf/logging.properties"
fi

GCLOG=/apps/tomcat/logs/gc.log

# 'm2.xlarge'   => { 'ram' => 17.1, 'vcpu' =>  6.5, 'ncpu' =>   2, 'disk' =>  420, 'storage' => ''             , 'karch' => 64, 'iops' => 'Moderate' , 'price' => 0.50  },
# 'm2.2xlarge'  => { 'ram' => 34.2, 'vcpu' => 13  , 'ncpu' =>   4, 'disk' =>  850, 'storage' => ''             , 'karch' => 64, 'iops' => 'High'     , 'price' => 1.00  },
# 'm2.4xlarge'  => { 'ram' => 68.4, 'vcpu' => 26  , 'ncpu' =>   8, 'disk' => 1690, 'storage' => ''             , 'karch' => 64, 'iops' => 'High'     , 'price' => 2.00  },
# 'm1.small'    => { 'ram' =>  1.7, 'vcpu' =>  1  , 'ncpu' =>   1, 'disk' =>  160, 'storage' => '150 plus 10'  , 'karch' => 32, 'iops' => 'Moderate' , 'price' => 0.085 },
# 'm1.large'    => { 'ram' =>  7.5, 'vcpu' =>  4  , 'ncpu' =>   2, 'disk' =>  850, 'storage' => '2x420 plus 10', 'karch' => 64, 'iops' => 'High'     , 'price' => 0.34  },
# 'm1.xlarge'   => { 'ram' => 15  , 'vcpu' =>  8  , 'ncpu' =>   4, 'disk' => 1690, 'storage' => '4x420 plus 10', 'karch' => 64, 'iops' => 'High'     , 'price' => 0.68  },
# 't1.micro'   =>  { 'ram' =>  0.5, 'vcpu' =>  2  , 'ncpu' =>   1, 'disk' =>    0, 'storage' => 'EBS only'     , 'karch' => 64, 'iops' => 'Low'      , 'price' => 0.02  }, #32/64
# 'c1.medium'   => { 'ram' =>  1.7, 'vcpu' =>  5  , 'ncpu' =>   2, 'disk' =>  350, 'storage' => ''             , 'karch' => 32, 'iops' => 'Moderate' , 'price' => 0.17  },
# 'c1.xlarge'   => { 'ram' =>  7  , 'vcpu' => 20  , 'ncpu' =>   8, 'disk' => 1690, 'storage' => ''             , 'karch' => 64, 'iops' => 'High'     , 'price' => 0.68  },
# 'cc1.4xlarge' => { 'ram' => 23  , 'vcpu' => 33.5, 'ncpu' =>   8, 'disk' => 1690, 'storage' => ''             , 'karch' => 64, 'iops' => 'Very-High', 'price' => 1.60  }

let GB=`free -m | grep '^Mem:' | awk '{print $2}'`/1000
  if [ $GB -ge 240 ] ; then HEAP_GB=235 YOUNG_GB=10
elif [ $GB -ge 120 ] ; then HEAP_GB=115 YOUNG_GB=5
elif [ $GB -ge 60 ] ; then HEAP_GB=58 YOUNG_GB=6
elif [ $GB -ge 38 ] ; then HEAP_GB=54 YOUNG_GB=6
elif [ $GB -ge 35 ] ; then HEAP_GB=30 YOUNG_GB=6
elif [ $GB -ge 28 ] ; then HEAP_GB=26 YOUNG_GB=6
else
    echo "total memory = $GB GB, heap too small"
    exit 0
fi

echo GB=$GB HEAP_GB=$HEAP_GB YOUNG_GB=$YOUNG_GB

## NETFLIX_AUTO_SCALE_GROUP=videometadata-slewfoot-27-USA-A
if [ ! -z "$NETFLIX_AUTO_SCALE_GROUP" ]; then
    save_ifs=$IFS
    IFS=- asg_parts=($NETFLIX_AUTO_SCALE_GROUP)
    export NETFLIX_VMS_APP=${asg_parts[0]}
    export NETFLIX_VMS_STACK=${asg_parts[1]}
    export NETFLIX_VMS_VERSION=${asg_parts[2]}
    export NETFLIX_VMS_COUNTRY=${asg_parts[3]}
    export NETFLIX_VMS_REDBLACK=${asg_parts[4]}
    IFS=$save_ifs
    VMS_ASG_OVERRIDES=" \
        -Dcom.netflix.videometadata.vipaddress=$NETFLIX_VMS_STACK \
    "
    # what if NETFLIX_VMS_APP != NETFLIX_APP
    # or NETFLIX_VMS_STACK != NETFLIX_STACK
    # -Dcom.netflix.videometadata.country=$NETFLIX_VMS_COUNTRY
fi


export YJPVER=11.0.5
export YJPDIR=/tmp/yjp-${YJPVER}
if [ -d ${YJPDIR} ]; then
    export YOURKIT_PROFILER_SERVER_DEBUG_OPTS="-agentlib:yjpagent=port=7002"
    export LD_LIBRARY_PATH=${LD_LIBRARY_PATH}:${YJPDIR}/bin/linux-x86-64
else
    export YOURKIT_PROFILER_SERVER_DEBUG_OPTS=""
fi

if [ "$1" == "start" ]; then
    export JAVA_OPTS=" \
        ${YOURKIT_PROFILER_SERVER_DEBUG_OPTS} \
        -Dnetflix.environment=$NETFLIX_ENVIRONMENT \
        -Dcom.netflix.cloud.namespace=sharedSystem \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false \
        -verbose:sizes \
        -Xloggc:$GCLOG \
        -Xmx${HEAP_GB}000m -Xms${HEAP_GB}000m \
        -Xmn${YOUNG_GB}000m \
        -Xss1024k \
        -XX:PermSize=256m \
        -XX:MaxPermSize=256m \
        -XX:HeapDumpPath=/apps/tomcat/logs/ \
        -XX:+HeapDumpOnOutOfMemoryError \
        -XX:-UseGCOverheadLimit \
        -XX:+ExplicitGCInvokesConcurrent \
        -XX:+PrintGCDateStamps -XX:+PrintGCDetails \
        -XX:+PrintTenuringDistribution \
        -XX:+UseParallelOldGC \
        -XX:SurvivorRatio=6 \
        -XX:MaxTenuringThreshold=15 \
        -XX:+UseLargePages \
        -XX:+PrintCommandLineFlags \
        -XX:ReservedCodeCacheSize=256m
    "
    /usr/sbin/logrotate -f /apps/tomcat/bin/.logrotate.conf
fi

CATALINA_PID=${CATALINA_PID:-"/apps/tomcat/logs/catalina.pid"}
