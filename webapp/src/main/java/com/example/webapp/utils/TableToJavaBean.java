package com.example.webapp.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TableToJavaBean {
    public static final String STATUS = "status";
    /**
     * 项目包名
     */
    private final AtomicInteger atom = new AtomicInteger(0);
    private static final String PROJECT_NAME = "jcz";
    private static final String do_packages = "package com."+PROJECT_NAME+".business.entity;";
    private static final String dto_packages = "package com."+PROJECT_NAME+".business.dto;";
    private static final String query_packages = "package com."+PROJECT_NAME+".business.query;";
    private static final String DATABASE_NAME = "jcz";
    private static final String JDBC_URL="jdbc:mysql://nacos.utechworld.com:3306/"+DATABASE_NAME+"?allowMultiQueries=true&characterEncoding=utf8&autoReconnect=true&rewriteBatchedStatemklbs=true&nullCatalogMeansCurrent=true";
    /**
     * 表名模糊查询：过滤转换表  % 表示全部
     */
    private static final String FILTER_CONVER_TABLE="%";

    /**
     * &nullCatalogMeansCurrent=true : mysql驱动由5.0升级到8.0之后出现，原来升级后默认的nullCatalogMeansCurrent属性为false
     */
    private static final String JDBC_DRIVER="com.mysql.cj.jdbc.Driver";
    private static final String USER_NAME = "root";
    private static final String USER_PASSWORD = "sjy@xy_100";
    private static final String LINE = "\r\n";
    private static final String TAB = "\t";
    /**
     *
     */
    private static final String BASE_PAHT="/Users/tol/data/"+PROJECT_NAME;
    private static final String API_PATH=BASE_PAHT+"/api";
    private static final String APP_PATH=BASE_PAHT+"/webapp";
    //导出表名前缀，空表示导出全部
    private static final String startsWithTableName="";

    /**转换类型
     * {"TABLE","VIEW","SYSTEM TABLE","GLOBAL TEMPORARY","LOCAL TEMPORARY","ALIAS" , "SYNONYM"}
     */
    private static final String[] TABLE_TYPE = {"TABLE", "VIEW" };

    public static final String COLUMN_NAME = "COLUMN_NAME";
    public static final String COLUMN_COMMENT = "COLUMN_COMMENT";
    public static final String SELECT_COLUM = "selectColum";
    public static final String INSERT_COLUM = "insertColum";
    public static final String INSERT_COLUM_VALUE = "insertColumValue";
    public static final String UPDATE_COLUM = "updateColum";

    private static Map<String, String> map;

    public static final String IMPORT_JAVA_UTIL_DATE = "import java.util.Date";
    public static final String IMPORT_DECIMAL = "import java.math.BigDecimal";

    static {
        map = new HashMap<String, String>();
        //数据库数据类型，java数据类型
        map.put("VARCHAR", "String");
        map.put("TEXT", "String");
        map.put("INTEGER", "Integer");
        map.put("INT", "Integer");
        map.put("BIT", "Integer");
        map.put("TINYINT", "Integer");
        map.put("BIGINT", "Long");
        map.put("FLOAT", "float");
        map.put("TIMESTAMP", "Date");
        map.put("CHAR", "String");
        map.put("DATETIME", "Date");
        map.put("DATE", "Date");
        map.put("DOUBLE", "Double");
        map.put("DECIMAL", "BigDecimal");
        //属性所需导入类包
        map.put("TIMESTAMP_IMPORT", IMPORT_JAVA_UTIL_DATE);
        map.put("DATETIME_IMPORT",IMPORT_JAVA_UTIL_DATE);
        map.put("DECIMAL_IMPORT",IMPORT_DECIMAL);
    }
    /**
     * 获取java属性类型
     * @param dataType 数据库字段类型
     * @return
     */
    public static String getPojoType(String dataType) {
        StringTokenizer st = new StringTokenizer(dataType);
        String type =  map.get(st.nextToken());
        if(StringUtils.isBlank(type)){
            System.out.println(dataType);
        }
        return type;
    }

    public static String getImport(String dataType) {
        if (map.get(dataType)==null||"".equals(map.get(dataType))) {
            return null;
        }else{
            return map.get(dataType);
        }
    }

    /**
     * 根据数据库表名 创建对应java实体对象
     * @param connection
     * @param tableName
     * @param tableComment  表注释
     * @throws SQLException
     */
    public void tableToBean(Connection connection, String tableName,String tableComment) throws SQLException {
        try {
            String sql = "select * from " + tableName + " where 1 <> 1";
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            StringBuffer sb = new StringBuffer();
            String oldTableName=tableName;
            tableName = tableName.substring(0, 1).toUpperCase() + tableName.subSequence(1, tableName.length());
            tableName = this.dealLine(tableName);
            sb.append(do_packages);
            sb.append(LINE);
            importPackage(md, columnCount, sb);
            sb.append(LINE);
            sb.append(LINE);
            sb.append("/**").append(LINE).append("* @author gehaisong").append(LINE).append("*/").append(LINE);
            sb.append("@Data ").append(LINE);
            String apiModel ="@ApiModel( \""+tableComment+"\")"+LINE;
            sb.append(apiModel);
            String className = tableName+"DO";
            sb.append("public class " + className + " implements Serializable {");
            sb.append(LINE);
            sb.append(TAB).
                    append("private static final long serialVersionUID = 1L;").append(LINE).append(LINE);
            //获取字段注释Map集合
            Map<String,String> commentMap= getColumnCommentTable( connection, oldTableName);
            Map<String,String> sqlMap = defProperty(oldTableName,md, columnCount, sb,commentMap);
            //上下架状态空，表示字段不存在
            String statusComment =sqlMap.get(STATUS);
            if(oldTableName.equals("message")){
                System.out.println(statusComment);
            }
            // genSetGet(md, columnCount, sb);
            sb.append("}");
            String paths = System.getProperty("user.dir");
            String doStr = sb.toString();
            buildJavaFile(API_PATH+"/entity/" + tableName + "DO.java", doStr);

            String apiModelDto ="@ApiModel( \""+tableComment+"DTO\")"+LINE;
            doStr = doStr.replace(apiModel,apiModelDto)
                    .replace(do_packages,dto_packages)
                    .replace(className,tableName+"DTO");
            if(tableName.toLowerCase().endsWith("ref")){
                // ref 关联表处理
                doStr = doStr.replace("@ApiModelProperty(value = \"id\")","")
                        .replace("private Integer id;","")
                        .replace("@ApiModelProperty(value = \"创建时间\" ,hidden = true)","")
                        .replace("private Date createTime;","")
                        .replace("@ApiModelProperty(value = \"更新时间\" ,hidden = true)","")
                        .replace("private Date updateTime;","")
                        .replace("@ApiModelProperty(value = \"0正常 其它值删除\" ,hidden = true)","")
                        .replace("@ApiModelProperty(value = \"0正常 1删除\" ,hidden = true)","")
                        .replace("private Integer isDelete;","");
            }else {
                String query =query_packages+"\n" +
                        "\n" +
                        "import com."+PROJECT_NAME+".framework.result.ResultPage;\n" +
                        "import io.swagger.annotations.ApiModel;\n" +
                        "import io.swagger.annotations.ApiModelProperty;\n" +
                        "import lombok.Data;\n" +
                        "\n" +
                        "import java.io.Serializable;\n" +
                        "\n" +
                        "@Data\n" +
                        "@ApiModel( \""+tableComment+"query\")\n" +
                        "public class "+tableName+"Query extends ResultPage implements Serializable {\n" +
                        "    private static final long serialVersionUID = 1L;\n" +
                        "    @ApiModelProperty(notes = \"搜索关键字\")\n" +
                        "    private String name;\n" ;
                        if(StringUtils.isNotBlank(statusComment)){
                            query  += " @ApiModelProperty(notes = \""+statusComment+"\")\n" +
                                    "    private Integer status;\n";
                        }
                        query += "}";
                buildJavaFile(API_PATH+"/query/" + tableName + "Query.java", query);

                String service ="package com."+PROJECT_NAME+".business.service."+tableName.toLowerCase()+";\n" +
                        "\n" +
                        "\n" +
                        "import com."+PROJECT_NAME+".business.entity."+tableName+"DO;\n" +
                        "import com."+PROJECT_NAME+".business.query."+tableName+"Query;\n" +
                        "import com."+PROJECT_NAME+".framework.result.Result;\n" +
                        "import com."+PROJECT_NAME+".framework.result.ResultPage;\n" +
                        "\n" +
                        "/**\n" +
                        " * "+tableComment+"\n" +
                        " * @author ghs\n" +
                        " */\n" +
                        "public interface "+tableName+"Service {\n" +
                        "\n" +
                        "    /**\n" +
                        "     * 分页列表\n" +
                        "     * @param query\n" +
                        "     * @return\n" +
                        "     */\n" +
                        "    ResultPage list("+tableName+"Query query);\n" +
                        "\n" +
                        "    /**\n" +
                        "     * 新建\n" +
                        "     * @return\n" +
                        "     */\n" +
                        "    Result insert("+tableName+"DO "+tableName.toLowerCase()+"DO);\n" +
                        "\n" +
                        "    /**\n" +
                        "     * 更新\n" +
                        "     * @param "+tableName+"DO\n" +
                        "     * @return\n" +
                        "     */\n" +
                        "    Result update("+tableName+"DO "+tableName.toLowerCase()+"DO);\n" +
                        "\n" +
                        "    /**\n" +
                        "     * 删除\n" +
                        "     * @param id\n" +
                        "     * @return\n" +
                        "     */\n" +
                        "    Result delete(Integer id);\n" +
                        "\n" +
                        "    /**\n" +
                        "     * 详情\n" +
                        "     * @param id\n" +
                        "     * @return\n" +
                        "     */\n" +
                        "    Result view(Integer id);\n" ;
                    if(StringUtils.isNotBlank(statusComment)){
                        service +=" /**\n" +
                                "     * "+statusComment+"\n" +
                                "     * @param id\n" +
                                "     * @param status\n" +
                                "     * @return\n" +
                                "     */\n" +
                                "    Result updateStatus(int id, int status);\n";
                    }
                        service +="\n" +
                        "\n" +
                        "}\n";
                buildJavaFile(API_PATH+"/service/"+tableName.toLowerCase()+"/" + tableName + "Service.java", service);
                buildServiceImpl( tableName,tableComment,statusComment);
                buildMapper( tableName ,oldTableName,tableComment,sqlMap,statusComment);
                buildController( tableName ,tableComment,statusComment);


            }
            buildJavaFile(API_PATH+"/dto/" + tableName + "DTO.java", doStr);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }

    }

    /**
     * 生成对应 controller
     * @param beanName
     * @param tableComment
     */
    private void buildController(String beanName, String tableComment,String statusComment) {
        //首字母小写
        String firstLowercaseBeanName = StringUtils.uncapitalize(beanName);
//        StringUtils.uncapitalize()
        String controller ="package com."+PROJECT_NAME+".bms.controller;\n" +
                "\n" +
                "import com."+PROJECT_NAME+".business.dto."+beanName+"DTO;\n" +
                "import com."+PROJECT_NAME+".business.entity."+beanName+"DO;\n" +
                "import com."+PROJECT_NAME+".business.query."+beanName+"Query;\n" +
                "import com."+PROJECT_NAME+".business.service."+firstLowercaseBeanName.toLowerCase()+"."+beanName+"Service;\n" +
                "import com."+PROJECT_NAME+".framework.annotation.LoginRequired;\n" +
                "import com."+PROJECT_NAME+".framework.enums.PlatformMarkEnum;\n" +
                "import com."+PROJECT_NAME+".framework.result.Result;\n" +
                "import com."+PROJECT_NAME+".framework.result.ResultPage;\n" +
                "import io.swagger.annotations.Api;\n" +
                "import io.swagger.annotations.ApiImplicitParam;\n" +
                "import io.swagger.annotations.ApiImplicitParams;\n" +
                "import io.swagger.annotations.ApiOperation;\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.cloud.context.config.annotation.RefreshScope;\n" +
                "import org.springframework.stereotype.Component;\n" +
                "import org.springframework.web.bind.annotation.*;\n" +
                "\n" +
                "/** "+tableComment+"\n" +
                " * @author ghs \n" +
                " */\n" +
                "@Api(tags = {\"100"+atom.incrementAndGet()+"-"+tableComment+"\"})\n" +
                "@RefreshScope\n" +
                "@RestController\n" +
                "@Component\n" +
                "@Slf4j\n" +
                "@LoginRequired\n" +
                "@RequestMapping(\"/api/bms/"+firstLowercaseBeanName.toLowerCase()+"\")\n" +
                "public class "+beanName+"Controller {\n" +
                "    @Autowired\n" +
                "    "+beanName+"Service "+firstLowercaseBeanName+"Service;\n" +
                "\n" +
                "    @ApiOperation(value = \"列表\", notes = \"列表\")\n" +
                "    @PostMapping(value = \"/list\")\n" +
                "    public ResultPage<"+beanName+"DTO> list(@RequestBody "+beanName+"Query query) {\n" +
                "        ResultPage result = "+firstLowercaseBeanName+"Service.list(query);\n" +
                "        return result;\n" +
                "    }\n" +
                "\n" +
                "    @ApiOperation(value = \"新建\", notes = \"新建\")\n" +
                "    @PostMapping(value = \"/add\")\n" +
                "    public Result add(@RequestBody "+beanName+"DO "+firstLowercaseBeanName+"DO) {\n" +
                "        Result result =  "+firstLowercaseBeanName+"Service.insert("+firstLowercaseBeanName+"DO);\n" +
                "        return result;\n" +
                "    }\n" +
                "\n" +
                "    @ApiOperation(value = \"详情\", notes = \"详情\")\n" +
                "    @ApiImplicitParams({@ApiImplicitParam(name = \"id\", value = \""+tableComment+"ID\", required = true)})\n" +
                "    @PostMapping(value = \"/view/{id}\")\n" +
                "    public Result<"+beanName+"DTO> view(@PathVariable int id) {\n" +
                "        return "+firstLowercaseBeanName+"Service.view(id);\n" +
                "    }\n" +
                "\n" +
                "    @ApiOperation(value = \"更新\", notes = \"更新\")\n" +
                "    @PostMapping(value = \"/update\")\n" +
                "    public Result update(@RequestBody "+beanName+"DO "+firstLowercaseBeanName+"DO) {\n" +
                "        return "+firstLowercaseBeanName+"Service.update("+firstLowercaseBeanName+"DO);\n" +
                "    }\n" +
                "\n" +
                "    @ApiOperation(value = \"删除\", notes = \"删除\")\n" +
                "    @ApiImplicitParams({@ApiImplicitParam(name = \"id\", value = \""+tableComment+"ID\", required = true)})\n" +
                "    @PostMapping(value = \"/delete/{id}\")\n" +
                "    public Result delete(@PathVariable int id) {\n" +
                "        return "+firstLowercaseBeanName+"Service.delete(id);\n" +
                "    }\n" ;
                if(StringUtils.isNotBlank(statusComment)){
                    controller +="@ApiOperation(value = \"更新状态\", notes = \"更新状态\")\n" +
                            "    @ApiImplicitParams({@ApiImplicitParam(name = \"id\", value = \""+tableComment+"ID\", required = true),\n" +
                            "                            @ApiImplicitParam(name = \"status\", value = \""+statusComment+"\", required = true)})\n" +
                            "    @RequestMapping(value = \"/update-status/{id}/{status}\", method = RequestMethod.POST)\n" +
                            "    public Result updateStatus(@PathVariable int id, @PathVariable int status) {\n" +
                            "        return "+firstLowercaseBeanName+"Service.updateStatus(id,status);\n" +
                            "} \n";
                }
                controller +="}\n";
        buildJavaFile(APP_PATH+"/bms/controller/" + beanName + "Controller.java", controller);
    }

    /**
     * 生成对应 mapper
     * @param beanName
     * @param tableName
     * @param tableComment
     * @param sqlMap
     */
    private void buildMapper(String beanName, String tableName, String tableComment, Map<String, String> sqlMap,String statusComment) {
        //首字母小写
        String firstLowercaseBeanName = StringUtils.uncapitalize(beanName);
        String mapperProvider = "package com."+PROJECT_NAME+".service."+firstLowercaseBeanName.toLowerCase()+".mapper;\n" +
                "\n" +
                "import com."+PROJECT_NAME+".business.entity."+beanName+"DO;\n" +
                "import com."+PROJECT_NAME+".business.query."+beanName+"Query;\n" +
                "import org.apache.commons.lang3.StringUtils;\n" +
                "import org.apache.ibatis.jdbc.SQL;\n" +
                "\n" +
                "import java.text.MessageFormat;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "\n" +
                "public class "+beanName+"MapperDynaSqlProvider {\n" +
                "\n" +
                "    public String selectAll(final "+beanName+"Query query) {\n" +
                "        SQL sql = new SQL() {\n" +
                "            {\n" +
                "                SELECT( \""+sqlMap.get(SELECT_COLUM)+"\" );\n" +
                "                FROM(\" "+tableName+" a \");\n" +
                "                if (StringUtils.isNotBlank(query.getName())) {\n" +
                "                    WHERE(\" a.name like #{name}\");\n" +
                "                }\n" ;
                if(StringUtils.isNotBlank(statusComment)){
                    mapperProvider +="if (query.getStatus()!=null) {\n" +
                            "                    WHERE(\" a.status=#{status}\");\n" +
                            "                }\n";
                }
                mapperProvider +="                WHERE(\" a.is_delete=0\");\n" +
                "                ORDER_BY(\" a.create_time DESC\");\n" +
                "            }\n" +
                "        };\n" +
                "        return sql.toString();\n" +
                "    }\n" +
                "\n" +
                "    public String update(final "+beanName+"DO "+firstLowercaseBeanName+"DO) {\n" +
                "        return new SQL() {\n" +
                "            {\n" +
                "                UPDATE(\" "+tableName+" \");\n" + sqlMap.get(UPDATE_COLUM)+
                "            }\n" +
                "        }.toString();\n" +
                "    }\n" +
                "\n" +
                "   // /**\n" +
                "   //  * 保存图片列表\n" +
                "   //  * @param map\n" +
                "   //  * @return\n" +
                "   //  */\n" +
                "   // public String insertImageList(Map map) {\n" +
                "   //     List<"+beanName+"ImageRefDO> insertList = (List<"+beanName+"ImageRefDO>) map.get(\"list\");\n" +
                "   //     StringBuilder sb = new StringBuilder();\n" +
                "   //     sb.append(\"INSERT INTO "+firstLowercaseBeanName+"_image_ref \");\n" +
                "   //     sb.append(\"(image_url ,"+firstLowercaseBeanName+"_id) \");\n" +
                "   //     sb.append(\"VALUES \");\n" +
                "   //     MessageFormat mf = new MessageFormat(\n" +
                "   //             \"( #'{'list[{0}].imageUrl},#'{'list[{0}]."+firstLowercaseBeanName+"Id})\");\n" +
                "   //     for (int i = 0; i < insertList.size(); i++) {\n" +
                "   //         sb.append(mf.format(new Object[]{i}));\n" +
                "   //         if (i < insertList.size() - 1) {\n" +
                "   //             sb.append(\",\");\n" +
                "   //         }\n" +
                "   //     }\n" +
                "   //     return sb.toString();\n" +
                "   // }\n" +
                "}\n";
        buildJavaFile(APP_PATH+"/service/"+firstLowercaseBeanName.toLowerCase()+"/mapper/" + beanName + "MapperDynaSqlProvider.java", mapperProvider);
        String mapper = "package com."+PROJECT_NAME+".service."+firstLowercaseBeanName.toLowerCase()+".mapper;\n" +
                "\n" +
                "import com."+PROJECT_NAME+".business.dto."+beanName+"DTO;\n" +
                "import com."+PROJECT_NAME+".business.entity."+beanName+"DO;\n" +
                "import com."+PROJECT_NAME+".business.query."+beanName+"Query;\n" +
                "import org.apache.ibatis.annotations.*;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "/** "+tableComment+"\n" +
                " * @author ghs\n" +
                " */\n" +
                "@Service\n" +
                "@Mapper\n" +
                "public interface "+beanName+"Mapper {\n" +
                "\n" +
                "    @SelectProvider(type= "+beanName+"MapperDynaSqlProvider.class,method=\"selectAll\")\n" +
                "    List<"+beanName+"DTO> selectAll("+beanName+"Query query);\n" +
                "\n" +
                "\n" +
                "    @Insert(\" INSERT INTO `"+tableName+"`("+sqlMap.get(INSERT_COLUM)+") \" +\n" +
                "            \" VALUES ("+sqlMap.get(INSERT_COLUM_VALUE)+")\")\n" +
                "    @Options(useGeneratedKeys = true, keyColumn = \"id\", keyProperty = \"id\")\n" +
                "    int insert("+beanName+"DO "+firstLowercaseBeanName+"DO);\n" +
                "\n" +
                "\n" +
                "    @UpdateProvider(type = "+beanName+"MapperDynaSqlProvider.class,method = \"update\")\n" +
                "    int update("+beanName+"DO "+beanName+"DO);\n" +
                "\n" +
                "    @Update(\"update "+tableName+" set is_delete=1 where id=#{id}\")\n" +
                "    int delete(Integer id);\n" +
                "\n" +
                "    @Select(\"select "+sqlMap.get(SELECT_COLUM)+"\" +\n" +
                "            \" from "+tableName+" a  \" +\n" +
                "            \" where a.id=#{id}\")\n" +
                "    "+beanName+"DTO view(Integer id);\n\n" ;
                if(StringUtils.isNotBlank(statusComment)){
                    mapper += " @Update(\"update "+tableName+" set status=#{status} where id=#{id}\")\n" +
                            "    int updateStatus(int id, int status);\n";
                }
                mapper += "\n" +
                "    /**\n" +
                "     * 保存图片列表\n" +
                "     * @param imageList\n" +
                "     * @return\n" +
                "     */\n" +
                "    @InsertProvider(type = "+beanName+"MapperDynaSqlProvider.class, method = \"insertImageList\")\n" +
                "    @Options(useGeneratedKeys = true, keyColumn = \"id\", keyProperty = \"id\")\n" +
                "    Integer insertImageList(List<"+beanName+"ImageRefDO> imageList);\n" +
                "\n" +
                "    @Update(\"update "+tableName+"_image_ref set is_delete=1 where "+tableName+"_id=#{"+firstLowercaseBeanName+"Id}\")\n" +
                "    void deleteImageBy"+beanName+"Id(Integer "+firstLowercaseBeanName+"Id);\n" +
                "\n" +
                "    @Select(\"<script>\" +\n" +
                "            \" select image_url,"+tableName+"_id \" +\n" +
                "            \" from "+tableName+"_image_ref \" +\n" +
                "            \" where  is_delete =0 and "+tableName+"_id in(\"+\n" +
                "            \"       <foreach collection='list' separator=',' item='id'> #{id} </foreach> ) \" +\n" +
                "            \" </script>\")\n" +
                "    List<"+beanName+"ImageRefDTO> getImageListBy"+beanName+"Id(List<Integer> list);\n" +
                "}";
        buildJavaFile(APP_PATH+"/service/"+firstLowercaseBeanName+"/mapper/" + beanName + "Mapper.java", mapper);
    }


    /**
     * 生成对应 impl
     * @param beanName  表类名
     * @param tableComment 表注释
     */
    private void buildServiceImpl(String beanName,String tableComment,String statusComment) {
        //首字母小写
        String firstLowercaseBeanName = StringUtils.uncapitalize(beanName);
        String content ="package com."+PROJECT_NAME+".service."+firstLowercaseBeanName.toLowerCase()+".impl;\n" +
                "\n" +
                "import com.github.pagehelper.PageHelper;\n" +
                "import com.github.pagehelper.PageInfo;\n" +
                "import com.google.common.collect.Lists;\n" +
                "import com.google.common.collect.Maps;\n" +
                "import com."+PROJECT_NAME+".business.dto."+beanName+"DTO;\n" +
                "import com."+PROJECT_NAME+".business.entity."+beanName+"DO;\n" +
                "import com."+PROJECT_NAME+".business.query."+beanName+"Query;\n" +
                "import com."+PROJECT_NAME+".business.service."+firstLowercaseBeanName.toLowerCase()+"."+beanName+"Service;\n" +
                "import com."+PROJECT_NAME+".common.annotation.RepeatableCommit;\n" +
                "import com."+PROJECT_NAME+".common.redis.RedisUtils;\n" +
                "import com."+PROJECT_NAME+".framework.result.Result;\n" +
                "import com."+PROJECT_NAME+".framework.result.ResultPage;\n" +
                "import com."+PROJECT_NAME+".service."+firstLowercaseBeanName.toLowerCase()+".mapper."+beanName+"Mapper;\n" +
                "import lombok.extern.slf4j.Slf4j;\n" +
                "import org.apache.commons.collections.CollectionUtils;\n" +
                "import org.apache.commons.lang3.StringUtils;\n" +
                "import org.apache.commons.lang3.exception.ExceptionUtils;\n" +
                "import org.springframework.beans.factory.annotation.Autowired;\n" +
                "import org.springframework.stereotype.Service;\n" +
                "import org.springframework.transaction.annotation.EnableTransactionManagement;\n" +
                "import org.springframework.transaction.annotation.Transactional;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.util.List;\n" +
                "import java.util.Map;\n" +
                "import java.util.stream.Collectors;\n" +
                "\n" +
                "import static com."+PROJECT_NAME+".business.common.Constant.PAGE_SIZE;\n" +
                "\n" +
                "\n" +
                "/**\n" +
                " * "+tableComment+"\n" +
                " * @author ghs\n" +
                " */\n" +
                "@EnableTransactionManagement\n" +
                "@Slf4j\n" +
                "@Service\n" +
                "public class "+beanName+"ServiceImpl implements "+beanName+"Service,Serializable{\n" +
                "    private static final long serialVersionUID = 4800994516532057532L;\n" +
                "    @Autowired\n" +
                "    private "+beanName+"Mapper "+firstLowercaseBeanName+"Mapper;\n" +
                "\n" +
                "    @Autowired\n" +
                "    private RedisUtils redisUtils;\n" +
                "\n" +
                "\n" +
                "    @Override\n" +
                "    public ResultPage list("+beanName+"Query query) {\n" +
                "        PageInfo pageInfo = null;\n" +
                "        try {\n" +
                "            query.setPageNo(query.getPageNo() == null ? 0 : query.getPageNo());\n" +
                "            query.setPageSize(query.getPageSize() == null ? PAGE_SIZE : query.getPageSize());\n" +
                "            if(StringUtils.isNotBlank(query.getName())){\n" +
                "                query.setName(\"%\"+query.getName()+\"%\");\n" +
                "            }\n" +
                "            //使用分页插件,核心代码就这一行 #分页配置#\n" +
                "            PageHelper.startPage(query.getPageNo(), query.getPageSize());\n" +
                "            List<"+beanName+"DTO> list = "+firstLowercaseBeanName+"Mapper.selectAll(query);\n" +
                "            pageInfo = new PageInfo(list);\n" +
                "            if (CollectionUtils.isEmpty(list)) {\n" +
                "                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());\n" +
                "            }else {\n" +
                "                //List<Integer> idList = list.stream().map("+beanName+"DTO::getId).collect(Collectors.toList());\n" +
                "               // Map<Integer,List<"+beanName+"ImageRefDTO>> map = getImageListBy"+beanName+"Id(idList);\n" +
                "                //list.stream().forEach(s->s.setImageList(map.get(s.getId())));\n" +
                "            }\n" +
                "        }catch (Exception e){\n" +
                "            log.error(\"{}\", ExceptionUtils.getStackTrace(e));\n" +
                "            throw e;\n" +
                "        }\n" +
                "        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());\n" +
                "    }\n" +
                "\n" +
                "    @RepeatableCommit\n" +
                "    @Transactional(rollbackFor = Exception.class)\n" +
                "    @Override\n" +
                "    public Result insert("+beanName+"DO "+firstLowercaseBeanName+"DO) {\n" +
                "        try {\n" +
                "            "+firstLowercaseBeanName+"Mapper.insert("+firstLowercaseBeanName+"DO);\n" +
                "            //if(CollectionUtils.isNotEmpty("+firstLowercaseBeanName+"DO.getImageList())){\n" +
                "            //  "+firstLowercaseBeanName+"DO.getImageList().stream().forEach(i->i.set"+beanName+"Id("+firstLowercaseBeanName+"DO.getId()));\n" +
                "            //  "+firstLowercaseBeanName+"Mapper.insertImageList("+firstLowercaseBeanName+"DO.getImageList());\n" +
                "            //}\n" +
                "        }catch (Exception e){\n" +
                "            log.error(\"save error >>>>>>\");\n" +
                "            throw e;\n" +
                "        }\n" +
                "        return Result.ok("+firstLowercaseBeanName+"DO.getId());\n" +
                "    }\n" +
                "\n" +
                "    @RepeatableCommit\n" +
                "    @Transactional(rollbackFor = Exception.class)\n" +
                "    @Override\n" +
                "    public Result update("+beanName+"DO "+firstLowercaseBeanName+"DO) {\n" +
                "        try {\n" +
                "            if("+firstLowercaseBeanName+"DO.getId() != null){\n" +
                "                "+firstLowercaseBeanName+"Mapper.update("+firstLowercaseBeanName+"DO);\n" +
                "                "+firstLowercaseBeanName+"Mapper.deleteImageBy"+firstLowercaseBeanName+"Id("+firstLowercaseBeanName+"DO.getId());\n" +
                "                //if(CollectionUtils.isNotEmpty("+firstLowercaseBeanName+"DO.getImageList())){\n" +
                "                //    "+firstLowercaseBeanName+"DO.getImageList().stream().forEach(i->i.setSupport"+firstLowercaseBeanName+"Id("+firstLowercaseBeanName+"DO.getId()));\n" +
                "                //    "+firstLowercaseBeanName+"Mapper.insertImageList("+firstLowercaseBeanName+"DO.getImageList());\n" +
                "                //}\n" +
                "            }\n" +
                "        }catch (Exception e){\n" +
                "            log.error(\"update error >>>>>>\");\n" +
                "            throw e;\n" +
                "        }\n" +
                "        return Result.ok("+firstLowercaseBeanName+"DO.getId());\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Result delete(Integer id) {\n" +
                "        try {\n" +
                "            "+firstLowercaseBeanName+"Mapper.delete(id);\n" +
                "        }catch (Exception e){\n" +
                "            log.error(\"delete error >>>>>>\");\n" +
                "        }\n" +
                "        return Result.ok(id);\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Result view(Integer id) {\n" +
                "        "+beanName+"DTO dto = null;\n" +
                "        try {\n" +
                "            dto = "+firstLowercaseBeanName+"Mapper.view(id);\n" +
                "            if(dto==null){\n" +
                "                dto = new "+beanName+"DTO();\n" +
                "            }else {\n" +
                "                // List<Integer> idList =Lists.newArrayList();\n" +
                "                // idList.add(id);\n" +
                "                // Map<Integer,List<"+beanName+"ImageRefDTO>> map = getImageListBy"+beanName+"Id(idList);\n" +
                "                // dto.setImageList(map.get(id));\n" +
                "            }\n" +
                "        }catch (Exception e){\n" +
                "            log.error(\"{}\", ExceptionUtils.getStackTrace(e));\n" +
                "            throw e;\n" +
                "        }\n" +
                "        return Result.ok(dto);\n" +
                "\n" +
                "    }\n" ;
                if(StringUtils.isNotBlank(statusComment)){
                    content += "/**\n" +
                            "     * "+statusComment+"\n" +
                            "     *\n" +
                            "     * @param id\n" +
                            "     * @param status\n" +
                            "     * @return\n" +
                            "     */\n" +
                            "    @Override\n" +
                            "    public Result updateStatus(int id, int status) {\n" +
                            "        int count = 0;\n" +
                            "        try {\n" +
                            "            count = "+firstLowercaseBeanName+"Mapper.updateStatus(id,status);\n" +
                            "        }catch (Exception e){\n" +
                            "            log.error(\"{}\", ExceptionUtils.getStackTrace(e));\n" +
                            "            throw e;\n" +
                            "        }\n" +
                            "        return Result.ok(count);\n" +
                            "    }";
                }
                content += "\n" +
                "    // private Map<Integer,List<"+beanName+"ImageRefDTO>> getImageListBy"+beanName+"Id(List<Integer> idList){\n" +
                "    //     Map<Integer,List<"+beanName+"ImageRefDTO>> map = Maps.newHashMap();\n" +
                "    //     List<"+beanName+"ImageRefDTO> list = "+firstLowercaseBeanName+"Mapper.getImageListBy"+beanName+"Id(idList);\n" +
                "    //     list.stream().forEach(image->{\n" +
                "    //         List<"+beanName+"ImageRefDTO> subList = map.get(image.get"+beanName+"Id());\n" +
                "    //         if(CollectionUtils.isEmpty(subList)){\n" +
                "    //             subList = Lists.newArrayList();\n" +
                "    //         }\n" +
                "    //         subList.add(image);\n" +
                "    //         map.put(image.get"+beanName+"Id(),subList);\n" +
                "    //     });\n" +
                "    //     return map;\n" +
                "    // }\n" +
                "\n" +
                "}\n";
        buildJavaFile(APP_PATH+"/service/"+firstLowercaseBeanName+"/impl/" + beanName + "ServiceImpl.java", content);
    }

    /**
     * 属性生成get、 set 方法
     * @param md
     * @param columnCount
     * @param sb
     * @throws SQLException
     */
    private void genSetGet(ResultSetMetaData md, int columnCount, StringBuffer sb) throws SQLException {
        for (int i = 1; i <= columnCount; i++) {
            sb.append(TAB);
            String pojoType = getPojoType(md.getColumnTypeName(i));
            String columnName = dealLine(md, i);
            String getName = null;
            String setName = null;
            if (columnName.length() > 1) {
                getName = "public " + pojoType + " get" + columnName.substring(0, 1).toUpperCase()
                        + columnName.substring(1, columnName.length()) + "() {";
                setName = "public void set" + columnName.substring(0, 1).toUpperCase()
                        + columnName.substring(1, columnName.length()) + "(" + pojoType + " " + columnName + ") {";
            } else {
                getName = "public get" + columnName.toUpperCase() + "() {";
                setName = "public set" + columnName.toUpperCase() + "(" + pojoType + " " + columnName + ") {";
            }
            sb.append(LINE).append(TAB).append(getName);
            sb.append(LINE).append(TAB).append(TAB);
            sb.append("return " + columnName + ";");
            sb.append(LINE).append(TAB).append("}");
            sb.append(LINE);
            sb.append(LINE).append(TAB).append(setName);
            sb.append(LINE).append(TAB).append(TAB);
            sb.append("this." + columnName + " = " + columnName + ";");
            sb.append(LINE).append(TAB).append("}");
            sb.append(LINE);

        }
    }

    /**
     * 导入属性所需包
     * @param md
     * @param columnCount
     * @param sb
     * @throws SQLException
     */
    private void importPackage(ResultSetMetaData md, int columnCount, StringBuffer sb) throws SQLException {
        sb.append("import java.io.Serializable;").append(LINE);
        sb.append("import io.swagger.annotations.ApiModel;").append(LINE);
        sb.append("import io.swagger.annotations.ApiModelProperty;").append(LINE);
        sb.append("import lombok.Data;").append(LINE);
        boolean isadded=false;
        boolean isaddedDecimal=false;
        for (int i = 1; i <= columnCount; i++) {
            String im=getImport(md.getColumnTypeName(i)+"_IMPORT");
            if (IMPORT_JAVA_UTIL_DATE.equals(im) && !isadded) {
                sb.append(im+ ";");
                sb.append(LINE);
                isadded = true;
            }
            if (IMPORT_DECIMAL.equals(im) && !isaddedDecimal) {
                sb.append(im+ ";");
                sb.append(LINE);
                isaddedDecimal = true;
            }
        }
    }
    /**
     * 属性定义  返回sql 字段
     * @param md     ResultSetMetaData
     * @param columnCount
     * @param sb
     * param Map<String,String> commentMap  字段名，注释
     * @throws SQLException
     */
    private Map<String,String> defProperty(String tableName,ResultSetMetaData md, int columnCount, StringBuffer sb,Map<String,String> commentMap) throws SQLException {
        StringBuilder selectColum =new StringBuilder();
        StringBuilder insertColum =new StringBuilder();
        StringBuilder insertColumValue =new StringBuilder();
        StringBuilder updateColum =new StringBuilder();
        Map<String,String> sqlMap= Maps.newHashMap();
        for (int i = 1; i <= columnCount; i++) {
            sb.append(TAB);
            //数据库字段名
            String databaseColumnName=md.getColumnName(i);
            //属性名
            String columnName = dealLine(md, i);
            String tableColumnName = md.getColumnName(i);
            //获取字段注释
            String comment=getColumnComment(commentMap,databaseColumnName);
            sb.append("@ApiModelProperty(")
                    .append("value = \"").append(comment).append("\"");
            if(columnName.equals("isDelete") || columnName.equals("updateTime") || columnName.equals("createTime") ){
                sb.append(" ,hidden = true");
            }
            sb.append(")").append(LINE);
            sb.append(TAB);
            String dbType = md.getColumnTypeName(i);
            String dataType = getPojoType(dbType);
            sb.append("private ").append(dataType).append(" ").append(columnName).append(";");
            if(StringUtils.isBlank(dataType)){
                log.error("tableName:{},cloumn:{},dbType:{}",tableName,columnName,dbType);
            }
            sb.append(LINE);
            if(!"id".equalsIgnoreCase(columnName) && !"isDelete".equalsIgnoreCase(columnName)
                    && !"updateTime".equalsIgnoreCase(columnName) && !"createTime".equalsIgnoreCase(columnName)){
                updateColum.append("SET(\"`").append(tableColumnName).append("`=#{").append(columnName).append("}\");").append(LINE);
                insertColum.append("`").append(tableColumnName).append("`,");
                insertColumValue.append("#{").append(columnName).append("},");
            }
            selectColum.append("a.`").append(tableColumnName).append("`,");
            if(columnName.endsWith(STATUS)){
                //添加上下架逻辑
                sqlMap.put(STATUS,comment);
            }
        }
        updateColum.append(" WHERE(\"id=#{id}\");");
        sqlMap.put(SELECT_COLUM,selectColum.substring(0,selectColum.length()-1));
        sqlMap.put(INSERT_COLUM,insertColum.substring(0,insertColum.length()-1));
        sqlMap.put(INSERT_COLUM_VALUE,insertColumValue.substring(0,insertColumValue.length()-1));
        sqlMap.put(UPDATE_COLUM,updateColum.toString());
        return sqlMap;
    }
    /**
     * 获取数据库字段注释
     * @param columnComment  字段名称注释 map
     * @param columnName     字段名称
     * @return
     */
    private String getColumnComment(Map<String,String> columnComment,String columnName) {
        StringTokenizer st = new StringTokenizer(columnName);
        String comment = columnComment.get(st.nextToken());
        if(StringUtils.isNotBlank(comment)){
            comment = comment.trim();
        }else {
            comment=columnName;
        }
        return comment;
    }
    private Map<String,String> getColumnCommentTable(Connection connection,String tableName) {
        try {
            String sql="select COLUMN_COMMENT,COLUMN_NAME,COLUMN_TYPE from information_schema.columns "+
                    " where table_schema = '"+DATABASE_NAME+"' and table_name = '"+tableName+"' ";
            PreparedStatement ps = null;
            ResultSet rs = null;
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            Map<String,String> nameCommentMap=new HashMap<String, String>();
            while(rs.next()){
                String name=rs.getString(COLUMN_NAME);
                String comment=rs.getString(COLUMN_COMMENT);
                nameCommentMap.put(name, comment);
            }
            return nameCommentMap;
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
    private String dealLine(ResultSetMetaData md, int i) throws SQLException {
        String columnName = md.getColumnName(i);
        // 处理下划线情况，把下划线后一位的字母变大写；
        columnName = dealName(columnName);
        return columnName;
    }

    private String dealLine(String tableName) {
        // 处理下划线情况，把下划线后一位的字母变大写；
        tableName = dealName(tableName);
        return tableName;
    }

    /**
     * 下划线后一位字母大写
     * @param columnName
     * @return
     */
    private static String dealName(String columnName) {
        if (columnName.contains("_")) {
            StringBuffer names = new StringBuffer();
            String arrayName[] = columnName.split("_");
            names.append(arrayName[0]);
            for (int i = 1; i < arrayName.length; i++) {
                String arri=arrayName[i];
                String tmp=arri.substring(0, 1).toUpperCase()+ arri.substring(1, arri.length());
                names.append(tmp);
            }
            columnName=names.toString();
        }
        return columnName;
    }

    /**
     * 生成java文件
     * @param filePath
     * @param fileContent
     */
    public void buildJavaFile(String filePath, String fileContent) {
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream osw = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(osw);
            pw.println(fileContent);
            pw.close();
        } catch (Exception e) {
            System.out.println("生成txt文件出错：" + e.getMessage());
        }
    }

    public static void main(String[] args)  {
        try {
            parseMapper();
            List<Integer> correctOptionIdList = Lists.newArrayList();
            correctOptionIdList.add(4);
            correctOptionIdList.add(1);
            correctOptionIdList.add(2);
            correctOptionIdList.add(8);
            correctOptionIdList.add(5);
            correctOptionIdList.sort((a, b) -> a.compareTo(b));
            String answerStr =StringUtils.join(correctOptionIdList,"_");
            System.out.println(answerStr);
            tableToJabaByMysql();
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }

    }

    private static void parseMapper() {
        String[] param={"id", "table_name", "type", "old_data", "new_data", "user_name", "operation_time", "data_time"};
        StringBuilder sb =new StringBuilder();
        for (String s : param) {
                sb.append(" #{item."+dealName(s)+"},\n");
        }
        System.out.println(sb.toString());
    }

    /**
     * Mysql数据库
     * @throws Exception
     */
    public static void tableToJabaByMysql() throws Exception {
        Properties props =new Properties();
        Class.forName(JDBC_DRIVER);
        props.setProperty("user", USER_NAME);
        props.setProperty("password", USER_PASSWORD);
        //设置可以获取remarks信息
        props.setProperty("remarks", "true");
        //设置可以获取tables remarks信息
        props.setProperty("useInformationSchema", "true");
        Connection con = DriverManager.getConnection(JDBC_URL,props);
        DatabaseMetaData databaseMetaData = con.getMetaData();
        //数据库名，登录名，表名，类型
        ResultSet rs = databaseMetaData.getTables(null, null, FILTER_CONVER_TABLE,TABLE_TYPE);
        TableToJavaBean d = new TableToJavaBean();
        long start=System.currentTimeMillis();
        while(rs.next()){
            //REMARKS  表注释
            String tableComment=rs.getString(5).toString(); ;
            String tableComment1=rs.getString("REMARKS").toString(); ;
            String dbName=rs.getString(1).toString(); ;
            String tableType=rs.getString(4).toString(); ;
            //TABLE_NAME
            String tableName=rs.getString(3).toString();
            String tableName1=rs.getString("TABLE_NAME").toString();
            if(StringUtils.isBlank(startsWithTableName)){
                if(StringUtils.isNotBlank(tableComment)){
                    tableComment = tableComment.trim();
                }
                d.tableToBean(con,tableName,tableComment);
            }else if(StringUtils.isNotBlank(startsWithTableName)&&
                    tableName.startsWith(startsWithTableName)){
                d.tableToBean(con,tableName,tableComment);
            }
        }
        System.out.println("转换时长："+(System.currentTimeMillis()-start));
        /**
         * TABLE_CAT String => 表类别（可为 null）
         TABLE_SCHEM String => 表模式（可为 null）
         TABLE_NAME String => 表名称
         TABLE_TYPE String => 表类型。
         REMARKS String => 表的解释性注释
         TYPE_CAT String => 类型的类别（可为 null）
         TYPE_SCHEM String => 类型模式（可为 null）
         TYPE_NAME String => 类型名称（可为 null）
         SELF_REFERENCING_COL_NAME String => 有类型表的指定 "identifier" 列的名称（可为 null）
         REF_GENERATION String
         */
    }
}