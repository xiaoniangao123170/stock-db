package com.ming.stock.sharding;

import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 定义公共分库算法 覆盖个股 大盘 板块...
 * 分库的策略：按照年进行分库(ds-2024 ds-2025 ds-2026)
 * 自定义分库算法（同时实现精确和范围分片）
 */
public class Common4Db implements StandardShardingAlgorithm<Date> {

    /**
     * 精确分片（=、IN）
     * select * from stock_warket_info where cur_time = xxx and code=xxx
     *
     * @param dsNames        数据源名称集合 ds-2024 ds-2025 ds-2026
     * @param shardingValue  封装逻辑表、分片列、条件值
     * @return 匹配的数据源名称
     *
     */
    @Override
    // 一个类同时实现两个接口，对应STANDARD标准策略
    public String doSharding(Collection<String> dsNames, PreciseShardingValue<Date> shardingValue) {
        // 获取逻辑表名称
        String logicTableName = shardingValue.getLogicTableName();
        //获取分片键cur_time
        String columnName=shardingValue.getColumnName();
        // 获取分片键的值
        Date curTime = shardingValue.getValue();
        //获取条件值对象的年份 然后从ds集合中过滤出以该年份结尾的数据源
        String year=new DateTime(curTime).getYear()+"";
        Optional<String> result = dsNames.stream()
                .filter(dsName -> dsName.endsWith(year)).findFirst();
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    /**
     * 范围分片（BETWEEN、>、<）
     * select * from stock_market_info where user_id between xxx and xxx
     *
     * @param dsNames        所有数据源名称集合 ds-2024 ds-2025 ds-2026
     * @param shardingValue  范围查询信息封装
     * @return 匹配的数据源集合
     */
    @Override
    public Collection<String> doSharding(Collection<String> dsNames, RangeShardingValue<Date> shardingValue) {
        // 获取分片键名称
        String columnName = shardingValue.getColumnName();
        // 获取逻辑表
        String logicTableName = shardingValue.getLogicTableName();
        // 获取范围数据：Range<Date> 封装了查询的上下限时间，比如 between '2024-01-01' and '2026-01-01'
        Range<Date> valueRange = shardingValue.getValueRange();

        // 判断是否有起始值
        /**
         * 数据源列表：["ds-2024","ds-2025","ds-2026"]
         * SQL：select * from t_log where cur_time between '2024-06-01' and '2025-12-31'`
         *
         * 1. 下限：startYear=2024 → 筛掉小于 2024 的库，剩下`ds-2024,ds-2025,ds-2026`
         * 2. 上限：endYear=2025 → 筛掉大于 2025 的库，剩下`ds-2024,ds-2025`
         */
        if (valueRange.hasLowerBound()) {
            Date startTime =valueRange.lowerEndpoint();
            int startYear=new DateTime(startTime).getYear();
            dsNames=dsNames.stream().filter(dsName->Integer.parseInt(
                            dsName.substring(dsName.lastIndexOf("-")+1))>=startYear)
                    .collect(Collectors.toList());
        }
        // 判断是否有上限值
        if (valueRange.hasUpperBound()) {
            Date endTime =valueRange.upperEndpoint();
            int endYear=new DateTime(endTime).getYear();
            //例如数据源名ds-2025，lastIndexOf("-")找到横杠位置，截取后面字符串"2025"，转成数字年份
            dsNames=dsNames.stream().filter(dsName->Integer.parseInt(
                            dsName.substring(dsName.lastIndexOf("-")+1))<=endYear)
                    .collect(Collectors.toList());
        }
        return dsNames;
    }

}