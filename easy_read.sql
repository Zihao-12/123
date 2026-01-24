DROP TABLE IF EXISTS `banner`;
 CREATE TABLE `banner` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `image_url` char(128) DEFAULT NULL,
	 `image_name` char(20) DEFAULT NULL,
   `jump_type` tinyint(1) DEFAULT NULL COMMENT '跳转地址类型: 1.站内 2.外链 3.站内H5',
   `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
	 `sort` int(11) DEFAULT '0' COMMENT '排序',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`image_name`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='banner轮播图';
 
 
 
 DROP TABLE IF EXISTS `show_banner`;
 CREATE TABLE `show_banner` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `organ_name` char(128) DEFAULT NULL COMMENT '机构名称',
   `organ_type` char(128) DEFAULT NULL COMMENT '机构属性',
   `organ_province` char(128) DEFAULT NULL COMMENT '机构属地',
	 `status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
	 `sort` int(11) DEFAULT '0' COMMENT '排序',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='指定banner接受对象';
 
 
 DROP TABLE IF EXISTS `news`;
 CREATE TABLE `news` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `new_title` char(128) DEFAULT NULL,
   `new_list` int(1) DEFAULT NULL,
   `new_type` char(10) DEFAULT NULL COMMENT '新闻资讯分类 1 民生 2 智力 3 生产',
   `new_style` char(10) DEFAULT NULL COMMENT '列表样式 1 右侧单张 2底部单张',
   `new_image` varchar(500) DEFAULT NULL COMMENT '新闻封面',
   `video_url`  char(128) DEFAULT NULL,
	 `new_type` int(11) DEFAULT '1' COMMENT '对象类型：1图文 2.视频',
   `image_url` char(128) DEFAULT NULL COMMENT '内容链接',
   `source` text COMMENT '来源 1新华网 2北京晚报',
   `new_details` text COMMENT '正文',
	 `status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
	 `top` int(11) DEFAULT '0' COMMENT '是否置顶：0不置顶 1置顶',
   `details` text COMMENT '正文内容',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`new_title`) USING BTREE,
	 UNIQUE KEY `top` (`top`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='新闻资讯列表';
 
 
DROP TABLE IF EXISTS `organ`;
 CREATE TABLE `organ` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(100) DEFAULT NULL COMMENT '机构名称',
   `account` varchar(100) DEFAULT NULL COMMENT '机构帐号',
   `password` varchar(100) DEFAULT NULL COMMENT '密码',
   `attribute` int(11) DEFAULT '0' COMMENT '机构属性-枚举',
   `province` int(11) DEFAULT NULL COMMENT '省',
   `city` int(11) DEFAULT NULL COMMENT '市',
   `address` varchar(500) DEFAULT NULL COMMENT '机构地址',
   
   `ip_restrict` int(11) DEFAULT '0' COMMENT '限制IP：0否 1是',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`),
   UNIQUE KEY `UK_account` (`account`),
   KEY `idx_name` (`name`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构表';



 DROP TABLE IF EXISTS `organ_list`;
 CREATE TABLE `organ_list` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `organ_name` char(128) DEFAULT NULL COMMENT '机构名称',
	 `organ_username` char(20) DEFAULT '' COMMENT '机构账号',
   `password` varchar(100) DEFAULT NULL COMMENT '密码(md5)',
	 `organ_type` char(20) DEFAULT '0' COMMENT '机构属性 0未设置 1公共 2私人',
	 `organ_local` char(20) DEFAULT '' COMMENT '省份',
   `address` char(100) DEFAULT NULL COMMENT '详细地址',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
	 UNIQUE KEY `top` (`organ_username`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构列表';
 
 
  DROP TABLE IF EXISTS `organ_open`;
 CREATE TABLE `organ_list` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `organ_title` char(128) DEFAULT NULL COMMENT '机构名称',
   `course_package_id` int(11) DEFAULT '0' COMMENT '课程包ID',
   `open_days` int(11) DEFAULT '0' COMMENT '开通天数',
	 `begin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始日期',
   `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束日前',
   `account_num` char(20) DEFAULT NULL COMMENT '最大机构账号数，默认不限制',
   `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
	 UNIQUE KEY `top` (`organ_title`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构开通';
 
 
 
  DROP TABLE IF EXISTS `coures_management`;
 CREATE TABLE `coures_management` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `course_image_url` char(128) DEFAULT NULL,
	 `course_name` char(20) DEFAULT NULL,
	 'course_number' int NOT NULL COMMENT '--含课数量',
	 `status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
	 UNIQUE KEY `top` (`organ_title`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程管理';
 
 
 
 
DROP TABLE IF EXISTS `coures_package`;
CREATE TABLE `coures_package` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`package_name` char(20) DEFAULT NULL,
`description` VARCHAR(255),
`parent_id` INT(11) DEFAULT 0,
`course_package_id` INT(11),
`status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
`is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
`update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (`id`) USING BTREE,
UNIQUE KEY `top` (`organ_title`),
INDEX idx_parent_id (parent_id),
INDEX idx_course_package_id (course_package_id),
FOREIGN KEY (`parent_id`) REFERENCES course(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`course_package_id`) REFERENCES `course_package(id)` ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程包列表';
 
 

  DROP TABLE IF EXISTS `content`;
 CREATE TABLE `content` (
	`id` int(11) DEFAULT(1000380959) COMMENT '代码',
	`name` char(20) DEFAULT NULL COMMENT '名字',
	`age` char(20) DEFAULT NULL COMMENT '年龄',
  `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
  `node_id` INT PRIMARY KEY,
  `node_name` VARCHAR(255) NOT NULL,
  `parent_id` INT,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	PRIMARY KEY (`id`) USING BTREE,
  FOREIGN KEY (`parent_id`) REFERENCES Node (`node_id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程分类';
 

 --
 DROP TABLE IF EXISTS `content_class`;
 CREATE TABLE `content_class` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(100) DEFAULT NULL COMMENT '名称',
   `description` varchar(100) DEFAULT NULL COMMENT '描述',
   `parent_id` int(11) DEFAULT '0' COMMENT '父ID, 0院不可挂课程',
   `organ_id` int(11) DEFAULT NULL COMMENT '机构ID',
   `id_full_path` varchar(250) DEFAULT NULL COMMENT 'id全路径',
   `name_full_path` varchar(500) DEFAULT NULL COMMENT 'name全路径',
   `node_type` int(11) DEFAULT '0' COMMENT '0院系 1班级',
   `sort` int(11) DEFAULT '0' COMMENT '排序',
   `child_type` int(11) DEFAULT '3' COMMENT '可建孩子节点类型：0 不可创建-此节点是班级 1可创建部门(此节点含部门结点) 2可创建班级(此节点是含班级的节点) 3可创建部门班级-其他节点',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
   KEY `idx_pid` (`parent_id`) USING BTREE,
   KEY `idx_idfullpath` (`id_full_path`) USING BTREE,
   KEY `idx_organ_id` (`organ_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='内容分类';
 
 
 
 

 
 DROP TABLE IF EXISTS `age`;
 CREATE TABLE `age` (
		`age1_id` char(20)
	 `age1` int(11) DEFAULT(1000380959) COMMENT '年龄',
   	`age2_id` char(20)
	 `age2` int(11) DEFAULT(1000380959) COMMENT '年龄',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	 PRIMARY KEY (`age_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='年龄表';
 
 


 DROP TABLE IF EXISTS `teacher_list`;
 CREATE TABLE `teacher_list` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `teacher_name` char(20) DEFAULT NULL,
	 `teacher_uesrname` char(20) DEFAULT NULL,
	 `work_number` char(20) DEFAULT NULL,
	 `character` char(20) DEFAULT '1' COMMENT '身份：1教师 0助教',
   `email` char(20) DEFAULT NULL,
	 `phone_number` int(11) DEFAULT NULL,
   `gender` char(1) NOT NULL comment '1男 2女'
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`work_number`) USING BTREE,
	 UNIQUE KEY `top` (`organ_title`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='教师列表';
 




 DROP TABLE IF EXISTS `bookroom`;
 CREATE TABLE `bookroom` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `class_type` int(2) DEFAULT '0' COMMENT '0全部 1艺术素养 2科学素养 3创新素养 4文化素养 5社会实践 6家庭教育 7健康素养'
	 `course_name` char(20) DEFAULT NULL,
   `course_image` char(50) DEFAULT NULL comment '课程封面',
   `age` char(10) DEFAULT NULL,
   `detail` text COMMENT '简介',
	 `teacher_uesrname` char(20) DEFAULT NULL,
	 `work_number` char(20) DEFAULT NULL,
	 `character` char(20) DEFAULT '1' COMMENT '身份：1教师 0助教',
	 `phone_number` int(11) DEFAULT NULL
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='书房';

 DROP TABLE IF EXISTS `bookroom_voide`;
 CREATE TABLE `bookroom_voide` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `name` char(20) NOT NULL,
   `voide_url` char(100) NOT NULL,
   `voide_time` char(100) NOT NULL,
   `over` tinyint(1) DEFAULT 0 COMMENT '0未观看 1已观看',
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='书房shipin1';


DROP TABLE IF EXISTS `activity`;
 CREATE TABLE `activity` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(100) DEFAULT NULL COMMENT '名称',
   `introduction` varchar(500) DEFAULT NULL COMMENT '简介',
   `cover` varchar(500) DEFAULT NULL COMMENT '封面',
   `detail` text COMMENT '详情',
   `type` int(11) DEFAULT 1 COMMENT '活动类型 1 线上',
   `age` int(11) DEFAULT 1 COMMENT '适合年龄 ',
   `shape` int(11) DEFAULT 1 COMMENT '活动形式 1 阅读打卡',
   `status` int(11) DEFAULT '0' COMMENT '是否上架：1上架 0下架',
   `begin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
   `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束时间',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='活动表';




 
 
 
 