/*
 *    Copyright 2014-2026 <a href="mailto:asialjim@qq.com">Asial Jim</a>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.asialjim.microapplet.hermes.infrastructure.repository.handler;

import com.asialjim.microapplet.hermes.ConsumptionStatus;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 消费状态类型处理器
 * <p>
 * 该类实现了 MyBatis 的 TypeHandler 接口，用于将数据库中的消费状态数值与 Java 枚举类型 ConsumptionStatus 进行转换。
 * Consumption status type handler
 * <p>
 * This class implements MyBatis' TypeHandler interface, used to convert consumption status values from the database to Java enum type ConsumptionStatus.
 * 
 * @author Asial Jim
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConsumptionStatusHandler implements TypeHandler<ConsumptionStatus> {
    /**
     * 消费状态映射表，用于快速查找状态枚举
     * Consumption status mapping table for quick lookup of status enums
     */
    private final Map<Integer, ConsumptionStatus> hermesStatusMap;

    /**
     * 构造函数，初始化消费状态映射表
     * Constructor, initializes the consumption status mapping table
     */
    public ConsumptionStatusHandler() {
        this.hermesStatusMap = new HashMap<>();
        for (ConsumptionStatus item : ConsumptionStatus.values()) {
            this.hermesStatusMap.put(item.getId(), item);
        }
    }

    /**
     * 设置参数，将 ConsumptionStatus 枚举转换为数据库字段值
     * <p>
     * Set parameter, convert ConsumptionStatus enum to database field value
     * 
     * @param ps PreparedStatement 对象
     * @param i 参数索引
     * @param parameter ConsumptionStatus 枚举值
     * @param jdbcType JDBC 类型
     * @throws SQLException SQL 异常
     * @since 1.0.0
     */
    @Override
    public void setParameter(PreparedStatement ps, int i, ConsumptionStatus parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getId());
    }

    /**
     * 从结果集中获取消费状态，根据列名
     * <p>
     * Get consumption status from result set, by column name
     * 
     * @param rs ResultSet 对象
     * @param columnName 列名
     * @return ConsumptionStatus 枚举值
     * @throws SQLException SQL 异常
     * @since 1.0.0
     */
    @Override
    public ConsumptionStatus getResult(ResultSet rs, String columnName) throws SQLException {
        String s = rs.getString(columnName);
        int index = NumberUtils.toInt(s, -1);
        return this.hermesStatusMap.get(index);
    }

    /**
     * 从结果集中获取消费状态，根据列索引
     * <p>
     * Get consumption status from result set, by column index
     * 
     * @param rs ResultSet 对象
     * @param columnIndex 列索引
     * @return ConsumptionStatus 枚举值
     * @throws SQLException SQL 异常
     * @since 1.0.0
     */
    @Override
    public ConsumptionStatus getResult(ResultSet rs, int columnIndex) throws SQLException {
        String s = rs.getString(columnIndex);
        int index = NumberUtils.toInt(s, -1);
        return this.hermesStatusMap.get(index);
    }

    /**
     * 从 CallableStatement 中获取消费状态，根据列索引
     * <p>
     * Get consumption status from CallableStatement, by column index
     * 
     * @param cs CallableStatement 对象
     * @param columnIndex 列索引
     * @return ConsumptionStatus 枚举值
     * @throws SQLException SQL 异常
     * @version 1.0.0
     * @since 1.0.0
     */
    @Override
    public ConsumptionStatus getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);
        int index = NumberUtils.toInt(s, -1);
        return this.hermesStatusMap.get(index);
    }
}