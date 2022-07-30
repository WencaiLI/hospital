package com.thtf.office.common.util;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @ClassName : IdGeneratorSnowflake
 * @Description : 分布式系统id生成器，雪花算法
 * @Author : zhaosy
 * @Date: 2020-05-12 11:14
 */
@Component
@Slf4j

/**
 * Twitter_Snowflake  雪花算法概述
 * SnowFlake的结构如下(每部分用-分开):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 *     -得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的。
 *     -41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号
 * 加起来刚好64位，为一个Long型。
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
public class IdGeneratorSnowflake {

    /**
     * 开始时间截 (2015-01-01)
     */
    private long workerId = 0; //机房编号 (0-31)
    private long dataCenterId = 1; //数据中心id (0-31)
    private Snowflake snowflake = IdUtil.createSnowflake(workerId, dataCenterId);

    @PostConstruct
    public void init() {
        try {
            String localhostStr = NetUtil.getLocalhostStr();
            workerId = NetUtil.ipv4ToLong(localhostStr);
            log.info("当前机器的workerId：{}", workerId);
        } catch (Exception e) {
            log.warn("当前机器的workerId获取失败：{}", e);
            workerId = NetUtil.getLocalhostStr().hashCode();
        }
    }

    /**
     * 调用生成唯一id
     *
     * @return
     */
    public synchronized long snowflakeId() {
        return snowflake.nextId();
    }

    /**
     * 根据传入条件调用，生成唯一id
     *
     * @param workerId
     * @param dataCenterId
     * @return
     */
    public synchronized long snowflakeId(long workerId, long dataCenterId) {
        Snowflake flake = IdUtil.createSnowflake(workerId, dataCenterId);
        return flake.nextId();
    }

}
