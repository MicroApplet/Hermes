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

-- 订阅者表
CREATE TABLE IF NOT EXISTS `hermes_subscriber` (
  `id` varchar(36) NOT NULL COMMENT '主键ID，自动生成UUID',
  `type` varchar(255) NOT NULL COMMENT '事件类型',
  `application` varchar(255) NOT NULL COMMENT '订阅者名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_application` (`type`,`application`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订阅者表';

-- 事件表
CREATE TABLE IF NOT EXISTS `hermes_event` (
  `id` varchar(36) NOT NULL COMMENT '事件编号，自动生成UUID',
  `type` varchar(255) NOT NULL COMMENT '事件类型',
  `data` text NOT NULL COMMENT '事件内容',
  `status` varchar(50) NOT NULL COMMENT '事件状态',
  `send_by` varchar(255) NOT NULL COMMENT '事件发送者',
  `send_to` varchar(1000) DEFAULT NULL COMMENT '事件接收者，逗号分隔的服务名称列表',
  `sub_service_num` int DEFAULT 0 COMMENT '关注此事件的服务数量',
  `succeed_service_num` int DEFAULT 0 COMMENT '成功处理此事件的服务数量',
  `failed_service_num` int DEFAULT 0 COMMENT '失败处理此事件的服务数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '事件更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_type` (`type`),
  KEY `idx_event_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事件表';

-- 事件归档表
CREATE TABLE IF NOT EXISTS `hermes_event_archive` (
  `id` varchar(36) NOT NULL COMMENT '事件编号',
  `type` varchar(255) NOT NULL COMMENT '事件类型',
  `data` text NOT NULL COMMENT '事件内容',
  `status` varchar(50) NOT NULL COMMENT '事件状态',
  `send_by` varchar(255) NOT NULL COMMENT '事件发送者',
  `sub_service_num` int DEFAULT 0 COMMENT '关注此事件的服务数量',
  `succeed_service_num` int DEFAULT 0 COMMENT '成功处理此事件的服务数量',
  `failed_service_num` int DEFAULT 0 COMMENT '失败处理此事件的服务数量',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '事件创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '事件更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_event_archive_type` (`type`),
  KEY `idx_event_archive_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='事件归档表';

-- 消费记录表
CREATE TABLE IF NOT EXISTS `hermes_consumption` (
  `id` varchar(36) NOT NULL COMMENT '主键ID，自动生成UUID',
  `event_id` varchar(36) NOT NULL COMMENT '事件ID，关联事件表',
  `subscriber` varchar(255) NOT NULL COMMENT '订阅者名称',
  `status` varchar(50) NOT NULL COMMENT '消费状态',
  `code` varchar(255) DEFAULT NULL COMMENT '状态码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `retry_times` int DEFAULT 0 COMMENT '重试次数',
  `next_time` datetime DEFAULT NULL COMMENT '下次执行时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_consumption_event_id_subscriber` (`event_id`,`subscriber`),
  KEY `idx_consumption_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费记录表';

-- 消费记录归档表
CREATE TABLE IF NOT EXISTS `hermes_consumption_archive` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `event_id` varchar(36) NOT NULL COMMENT '事件ID，关联事件表',
  `subscriber` varchar(255) NOT NULL COMMENT '订阅者名称',
  `status` varchar(50) NOT NULL COMMENT '消费状态',
  `code` varchar(255) DEFAULT NULL COMMENT '状态码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `retry_times` int DEFAULT 0 COMMENT '重试次数',
  `next_time` datetime DEFAULT NULL COMMENT '下次执行时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_consumption_archive_event_id_subscriber` (`event_id`,`subscriber`),
  KEY `idx_consumption_archive_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费记录归档表';

-- 消费详情表
CREATE TABLE IF NOT EXISTS `hermes_consumption_detail` (
  `id` varchar(36) NOT NULL COMMENT '主键ID，自动生成UUID',
  `consumption_id` varchar(36) NOT NULL COMMENT '消费记录ID，关联消费记录表',
  `event_id` varchar(36) NOT NULL COMMENT '事件ID，关联事件表',
  `subscriber` varchar(255) NOT NULL COMMENT '订阅者名称',
  `listener` varchar(255) NOT NULL COMMENT '监听器名称',
  `status` varchar(50) NOT NULL COMMENT '消费状态',
  `code` varchar(255) DEFAULT NULL COMMENT '状态码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_consumption_detail_combination` (`consumption_id`,`event_id`,`subscriber`,`listener`),
  KEY `idx_consumption_detail_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费详情表';

-- 消费详情归档表
CREATE TABLE IF NOT EXISTS `hermes_consumption_detail_archive` (
  `id` varchar(36) NOT NULL COMMENT '主键ID',
  `consumption_id` varchar(36) NOT NULL COMMENT '消费记录ID，关联消费记录表',
  `event_id` varchar(36) NOT NULL COMMENT '事件ID，关联事件表',
  `subscriber` varchar(255) NOT NULL COMMENT '订阅者名称',
  `listener` varchar(255) NOT NULL COMMENT '监听器名称',
  `status` varchar(50) NOT NULL COMMENT '消费状态',
  `code` varchar(255) DEFAULT NULL COMMENT '状态码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_consumption_detail_archive_combination` (`consumption_id`,`event_id`,`subscriber`,`listener`),
  KEY `idx_consumption_detail_archive_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费详情归档表';
