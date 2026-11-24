package com.ming.stock.sharding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * 定义公共得分表算法：个股流水表按照月分表
 * 分表策略：按照年月分表（stock_rt_info_202401 ~ stock_rt_info_202612）
 */
public class Common4Tb implements StandardShardingAlgorithm<Date> {

    /**
     * 精确分片（=、IN）
     * select * from stock_rt_info where cur_time=xxx
     * tbNames:  eg:stock_rt_info_202401 ~ stock_rt_info_202612
     * shardingValue 分片值封装
     * 匹配得表名
     */
    @Override
    public String doSharding(Collection<String> tbNames, PreciseShardingValue<Date> shardingValue) {
        // 获取逻辑表名称
        String logicTableName = shardingValue.getLogicTableName();
        //获取分片键cur_time
        String columnName=shardingValue.getColumnName();
        // 获取分片键的值
        Date curTime = shardingValue.getValue();
        //获取条件值对象的年月 然后从tb集合中过滤出以该年月结尾的表
        String yearMonth=new DateTime(curTime).toString("yyyyMM");
        Optional<String> result = tbNames.stream()
                .filter(tbName -> tbName.endsWith(yearMonth)).findFirst();
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }


    /**
     * 精确分片（=、IN）
     * select * from stock_rt_info where cur_time=xxx
     * tbNames:  eg:stock_rt_info_202401 ~ stock_rt_info_202612
     * shardingValue 分片值封装
     * 匹配得表名
     */
    @Override
    public Collection<String> doSharding(Collection<String> tbNames, RangeShardingValue<Date> shardingValue) {
        // 获取分片键名称
        String columnName = shardingValue.getColumnName();
        // 获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        // 获取范围数据
        Range<Date> valueRange = shardingValue.getValueRange();

        // 判断是否有起始值
        if (valueRange.hasLowerBound()) {
            Date startTime =valueRange.lowerEndpoint();
            int startYearMonth=Integer.parseInt(new DateTime(startTime).toString("yyyyMM"));
            //过滤出表名中的年月>=startYearMonth
            tbNames=tbNames.stream().filter(tbName->{
                //从表名提取年月部分 比如stock_rt_info_202401 -> 202401
                String suffix=tbName.substring(tbName.lastIndexOf("_")+1);
                try {
                    return Integer.parseInt(suffix)>=startYearMonth;
                } catch (NumberFormatException e) {
                    return false;
                }
            }).collect(Collectors.toList());
        }
        // 判断是否有上限值
        if (valueRange.hasUpperBound()) {
            Date endTime =valueRange.upperEndpoint();
            int endYearMonth=Integer.parseInt(new DateTime(endTime).toString("yyyyMM"));
            //过滤出表名中的年月<=endYearMonth
            tbNames=tbNames.stream().filter(tbName->{
                //从表名提取年月部分 比如
                String suffix=tbName.substring(tbName.lastIndexOf("_")+1);
                try {
                    return Integer.parseInt(suffix)<=endYearMonth;
                } catch (NumberFormatException e) {
                    return false;
                }
            }).collect(Collectors.toList());
        }
        return tbNames;
    }


    @Override
    public void init(Properties properties) {
    }
}