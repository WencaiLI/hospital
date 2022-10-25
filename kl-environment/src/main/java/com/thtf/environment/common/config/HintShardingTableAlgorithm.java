package com.thtf.environment.common.config;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;;
import com.thtf.environment.common.utils.DateUtil;
import com.thtf.environment.mapper.TblHistoryMomentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * hint分片策略
 *
 * @author ligh
 * @date 2020-07-09
 */
@Slf4j
@Component
public class HintShardingTableAlgorithm implements HintShardingAlgorithm {

    public static HintShardingTableAlgorithm hintShardingAlgorithm;

    @PostConstruct
    public void init() {
        hintShardingAlgorithm = this;
    }

    @Resource
    private TblHistoryMomentMapper historyMomentMapper;

    @Override
    public Collection<String> doSharding(Collection availableTargetNames, HintShardingValue shardingValue) {
        List<String> result = new ArrayList<>();
       // hintShardingAlgorithm.alarmRecordService.getReportAlarmRecode("","","",null);
        String logicTableName = shardingValue.getLogicTableName();
        Collection collection = shardingValue.getValues();
        Object[] array = collection.toArray();
        if (array.length == 2) {
            String lowerEndpoint = "";
            String upperEndpoint = "";
            //比较时间大小
            if(DateUtil.compareTime(DateUtil.string2Date(array[0].toString(),"yyyy-MM-dd"),
                    DateUtil.string2Date(array[1].toString(),"yyyy-MM-dd")) <= 0){
                lowerEndpoint = array[0].toString();
                upperEndpoint = array[1].toString();
            }else{
                lowerEndpoint = array[1].toString();
                upperEndpoint = array[0].toString();
            }
            lowerEndpoint = lowerEndpoint.substring(0, 7) + "-01";
            upperEndpoint = upperEndpoint.substring(0, 7) + "-01";
            Date startDate = null;
            Date endDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                startDate = dateFormat.parse(lowerEndpoint);
                endDate = dateFormat.parse(upperEndpoint);
            } catch (ParseException e) {
                log.warn(e.getMessage(), e);
            }
            List<DateTime> dateTimeList = cn.hutool.core.date.DateUtil.rangeToList(startDate, endDate, DateField.MONTH);
            for(DateTime dateTime : dateTimeList){
                StringBuffer sb = new StringBuffer(logicTableName);
                int year = dateTime.year();
                int month = dateTime.month() + 1;
                String monthStr = month < 10 ? "0" + month : month + "";
                result.add(sb.append("_").append(year).append(monthStr).toString());
            }
        } else if (array.length == 1) {
            if (array[0] == null || array[0].toString().isEmpty()) {
                return hintShardingAlgorithm.historyMomentMapper.selectHistoryMomentTables(logicTableName + "[_]\\d{6}");
            }
            String dateStr = array[0].toString();
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr,df);

            int year = localDateTime.getYear();
            int month = localDateTime.getMonthValue();
            String tableName = logicTableName + "_" + year + (month < 10 ? "0" + month : month);
            result.add(tableName);
        }
        if (!CollectionUtils.isEmpty(result)) {
            result = hintShardingAlgorithm.historyMomentMapper.selectExistentTableName(result);
        }
        return result;
    }

}
