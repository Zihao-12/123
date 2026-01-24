DROP TABLE IF EXISTS `area`;
 CREATE TABLE `area` (
    `id` int(11) NOT NULL,
    `name` varchar(40) DEFAULT NULL,
    `directly` int(11) DEFAULT '0' COMMENT '1直辖 0非直辖',
    `pid` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `china_id_pid_pk` (`id`,`pid`),
    KEY `FK_CHINA_REFERENCE_CHINA` (`pid`)
--     CONSTRAINT `FK_CHINA_REFERENCE_CHINA` FOREIGN KEY (`pid`) REFERENCES `area` (`id`)
  )  ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='地区表';

INSERT INTO `area` VALUES (0,'中国',0,-1),(110000,'北京市',1,0),(110100,'东城区',0,110000),(110200,'西城区',0,110000),(110500,'朝阳区',0,110000),(110600,'丰台区',0,110000),(110700,'石景山区',0,110000),(110800,'海淀区',0,110000),(110900,'门头沟区',0,110000),(111100,'房山区',0,110000),(111200,'通州区',0,110000),(111300,'顺义区',0,110000),(111400,'昌平区',0,110000);

 DROP TABLE IF EXISTS `dictionary`;
 CREATE TABLE `dictionary` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(255) DEFAULT NULL,
   `value` int(11) DEFAULT NULL,
   `type` int(11) DEFAULT NULL COMMENT '分类：1机构属性 2新闻来源 3题目难度',
   `description` varchar(255) DEFAULT NULL COMMENT '描述',
   `sort` int(11) DEFAULT NULL COMMENT '排序',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='字典表';

DROP TABLE IF EXISTS `banner`;
 CREATE TABLE `banner`(
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `image_url` varchar(128) DEFAULT NULL,
	 `image_name` varchar(20) NOT NULL,
   `jump_type` tinyint(1) DEFAULT NULL COMMENT '跳转地址类型: 1.站内 2.外链 3.站内H5',
   `jump_url` varchar(500) DEFAULT NULL COMMENT '跳转地址',
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
	 `sort` int(11) DEFAULT '0' COMMENT '排序',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='banner轮播图';
 
 
 
 DROP TABLE IF EXISTS `show_banner`;
 CREATE TABLE `show_banner` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `organ_id` int(11) DEFAULT NULL COMMENT '机构ID',
   `banner_id` int(11) DEFAULT NULL COMMENT '轮播图ID',
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
    `title` varchar(500) DEFAULT NULL COMMENT '标题',
   `type` varchar(10) DEFAULT NULL COMMENT '新闻资讯分类 1 民生 2 智力 3 生产',
   `style` varchar(10) DEFAULT NULL COMMENT '列表样式 1 右侧单张 2底部单张',
   `image` varchar(500) DEFAULT NULL COMMENT '新闻封面',
   `video_url`  varchar(128) DEFAULT NULL,
	 `type1` int(11) DEFAULT '1' COMMENT '对象类型：1图文 2.视频',
   `image_url` varchar(128) DEFAULT NULL COMMENT '内容链接',
   `source` text COMMENT '来源 1新华网 2北京晚报',
   `details` text COMMENT '正文',
	 `status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
	 `top` int(11) DEFAULT '0' COMMENT '是否置顶：0不置顶 1置顶',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
	 UNIQUE KEY `top` (`top`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='新闻资讯列表';
 
 DROP TABLE IF EXISTS `news_image_ref`;
CREATE TABLE `news_image_ref` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `news_id` int(11) NOT NULL COMMENT 'news表主键',
  `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='新闻资讯-内容图片关联表';


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
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构信息表';



 DROP TABLE IF EXISTS `organ_list`;
 CREATE TABLE `organ_list` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `organ_name` varchar(128) DEFAULT NULL COMMENT '机构名称',
	 `organ_username` varchar(20) DEFAULT '' COMMENT '机构账号',
   `password` varchar(100) DEFAULT NULL COMMENT '密码(md5)',
	 `organ_type` varchar(20) DEFAULT '0' COMMENT '机构属性 0未设置 1公共 2私人',
	 `organ_local` varchar(20) DEFAULT '' COMMENT '省份',
   `address` varchar(100) DEFAULT NULL COMMENT '详细地址',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
	 UNIQUE KEY `top` (`organ_username`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构列表';
 
 
  DROP TABLE IF EXISTS `organ_open`;
  CREATE TABLE `organ_open` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
   `organ_id` int(11) NOT NULL COMMENT '机构id',
    `course_package_id` int(11) DEFAULT '0' COMMENT '课程包ID，只能选择待使用课程包',
   `open_days` int(11) DEFAULT '0' COMMENT '开通天数',
 	 `begin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始日期',
    `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束日前',
   `account_num` varchar(20) DEFAULT NULL COMMENT '最大机构账号数，默认不限制',
    `open_type` int(11) DEFAULT '0' COMMENT '开通类型：0 通用开通 ',
    `status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
    `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构开通';
 
  DROP TABLE IF EXISTS `organ_contact_person`;
 CREATE TABLE `organ_contact_person` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `organ_id` int(11) DEFAULT NULL COMMENT '机构ID',
   `name` varchar(100) DEFAULT NULL COMMENT '联系人姓名',
   `phone` varchar(100) DEFAULT NULL COMMENT '手机号',
   `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
   `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构联系人表';


 DROP TABLE IF EXISTS `organ_open_delay_record`;
 CREATE TABLE `organ_open_delay_record` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `organ_open_id` int(11) DEFAULT '0' COMMENT '机构开通ID',
   `delay_days` int(11) DEFAULT '0' COMMENT '延期天数',
   `begin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '延期开始日期',
   `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '延期结束日前',
   `last_open_days` int(11) DEFAULT '0' COMMENT '上次开通天数',
   `last_end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上次开通结束日期',
   `is_delay` int(11) DEFAULT '0' COMMENT '是否延期成功：0没有 1延期成功',
   `description` varchar(250) DEFAULT '' COMMENT '延期描述',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='机构开通延期记录表';


DROP TABLE IF EXISTS `user_favorite_ref`;
CREATE TABLE `user_favorite_ref` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `organ_id` int(11) NOT NULL COMMENT '机构id',
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `object_id` int(11) NOT NULL COMMENT '对象ID',
   `object_type` int(11) DEFAULT '1' COMMENT '对象类型：1课程',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_object_id` (`user_id`,`object_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户收藏表';


 DROP TABLE IF EXISTS `course_package`;
 CREATE TABLE `course_package` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(100) DEFAULT NULL COMMENT '课程包名称',
   `introduction` varchar(500) DEFAULT NULL COMMENT '简介',
   `type` int(2) DEFAULT '1' COMMENT '1.通用包',
   `status` int(11) DEFAULT '0' COMMENT '是否上架：1上架 0下架',
   `try_status` int(11) DEFAULT '0' COMMENT '试看设置：0全部可看 1试看第一节 2试看前三节',
   `used` int(2) DEFAULT '0' COMMENT '课程包状态：0待使用 1已使用，机构开通只能选择待使用的',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程包表';


  DROP TABLE IF EXISTS `coures_management`;
 CREATE TABLE `coures_management` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `course_image_url` varchar(128) DEFAULT NULL,
	 `course_name` varchar(20) DEFAULT NULL,
	 `status` int(11) DEFAULT '0' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE,
	 UNIQUE KEY `top` (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程管理';
 

 DROP TABLE IF EXISTS `course`;
 CREATE TABLE `course` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `name` varchar(100) DEFAULT NULL COMMENT '课程名称',
   `introduction` varchar(500) DEFAULT NULL COMMENT '课程简介',
   `cover` varchar(500) DEFAULT NULL COMMENT '课程封面',
   `detail` text COMMENT '课程详情',
   `type` int(11) DEFAULT 1 COMMENT '1.视频课 2.签到视频',
   `status` int(11) DEFAULT '0' COMMENT '是否上架：1上架 0下架',
   `mechanism_id` int(11) DEFAULT NULL COMMENT '机构ID（自建课）',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程表';


  DROP TABLE IF EXISTS `course_package_ref`;
 CREATE TABLE `course_package_ref` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
   `course_id` int(11) NOT NULL COMMENT '课程ID',
   `course_package_id` int(11) NOT NULL COMMENT '课程包ID',
   `sort` int(11) DEFAULT '0' COMMENT '排序 升序',
   `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`),
   KEY `idx_course_package_id` (`course_id`,`course_package_id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='课程包与课程关联表';

   DROP TABLE IF EXISTS `content`;
  CREATE TABLE `content` (
 	`id` int(11) NOT NULL COMMENT '代码',
 	`name` varchar(20) DEFAULT NULL COMMENT '名字',
 	`age` varchar(20) DEFAULT NULL COMMENT '年龄',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `node_id` INT ,
   `node_name` VARCHAR(255) NOT NULL,
   `parent_id` INT,
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
 	PRIMARY KEY (`id`) USING BTREE
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
	 `id` int(11) NOT NULL,
   `age` int(11) NOT NULL COMMENT '年龄',
    `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	 PRIMARY KEY (id) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='年龄表';


 DROP TABLE IF EXISTS `age_tag_mapping`;
 CREATE TABLE age_tag_mapping (
    age_id INT,
    tag_id varchar(50)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='年龄标签表';
 
INSERT INTO age_tag_mapping (`age_id`, `tag_id`)
VALUES (1, 1); -- 将ageID为1的人与标签ID为1的“年龄”进行关联


 DROP TABLE IF EXISTS `teacher_list`;
 CREATE TABLE `teacher_list` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `teacher_name` varchar(20) DEFAULT NULL,
	 `teacher_uesrname` varchar(20) DEFAULT NULL,
	 `work_number` varchar(20) DEFAULT NULL,
	 `character` varchar(20) DEFAULT '1' COMMENT '身份：1教师 0助教',
   `email` varchar(20) DEFAULT NULL,
	 `phone_number` int(11) DEFAULT NULL,
   `gender` varchar(1) NOT NULL comment '1男 2女',
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='教师列表';
 




 DROP TABLE IF EXISTS `bookroom`;
 CREATE TABLE `bookroom` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `class_type` int(2) DEFAULT '0' COMMENT '0全部 1艺术素养 2科学素养 3创新素养 4文化素养 5社会实践 6家庭教育 7健康素养',
	 `course_name` varchar(20) DEFAULT NULL,
   `course_image` varchar(50) DEFAULT NULL comment '课程封面',
   `age` varchar(10) DEFAULT NULL,
   `detail` text COMMENT '简介',
	 `teacher_uesrname` varchar(20) DEFAULT NULL,
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='书房';

 DROP TABLE IF EXISTS `bookroom_voide`;
 CREATE TABLE `bookroom_voide` (
   `id` int(11) NOT NULL AUTO_INCREMENT,
	 `name` varchar(20) NOT NULL,
   `voide_url` varchar(100) NOT NULL,
   `voide_time` varchar(100) NOT NULL,
   `over` tinyint(1) DEFAULT 0 COMMENT '0未观看 1已观看',
	 `status` int(11) DEFAULT '1' COMMENT '0未设置 1上架 2下架',
   `is_delete` int(11) DEFAULT 0 COMMENT '0正常 1删除',
   `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
   `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY (`id`) USING BTREE
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='书房shipin1';


-- DROP TABLE IF EXISTS `activity`;
--  CREATE TABLE `activity` (
--    `id` int(11) NOT NULL AUTO_INCREMENT,
--    `name` varchar(100) DEFAULT NULL COMMENT '名称',
--    `coures_id` NOT NULL COMMENT '课程包id',
--   `begin_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开始日期',
--    `end_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '结束日前',
--    `is_delete` int(11) DEFAULT '0' COMMENT '0正常 1删除',
--    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
--    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--    PRIMARY KEY (`id`) USING BTREE
--  ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='活动表';




INSERT INTO `dictionary`(`id`, `name`, `value`,  `type`,`description`, `sort`) VALUES (1, '市级馆', 1, 1, '机构属性', 1);
  INSERT INTO `dictionary`(`id`, `name`, `value`,  `type`,`description`, `sort`) VALUES (2, '省级馆', 2, 1, '机构属性', 2);
  INSERT INTO `dictionary`(`id`, `name`, `value`,  `type`,`description`, `sort`) VALUES (3, '县级馆', 3, 1, '机构属性', 3);
  INSERT INTO `dictionary`(`id`, `name`, `value`,  `type`,`description`, `sort`) VALUES (4, '少儿馆', 4, 1, '机构属性', 4);
  INSERT INTO `dictionary`(`id`, `name`, `value`,  `type`,`description`, `sort`) VALUES (5, '中小学', 5, 1, '机构属性', 5);
  INSERT INTO `dictionary`(`id`, `name`, `value`,  `type`,`description`, `sort`) VALUES (6, '运营商', 6, 1, '机构属性', 6);
 
 
 