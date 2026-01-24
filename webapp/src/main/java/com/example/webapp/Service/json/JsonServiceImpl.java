package com.example.webapp.Service.json;

import com.example.webapp.DO.CategoryDO;
import com.example.webapp.DTO.CategoryDTO;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CourseSectionDTO;
import com.example.webapp.DTO.ObjectCategoryDTO;
import com.example.webapp.Mapper.category.CategoryMapper;
import com.example.webapp.Mapper.course.CourseMapper;
import com.example.webapp.Mapper.json.JsonMapper;
import com.example.webapp.Service.category.CategoryService;
import com.example.webapp.common.redis.RedisLock;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.CourseSectionTypeEnum;
import com.example.webapp.enums.ObjectTypeEnum;
import com.example.webapp.query.CourseJsonQuery;
import com.example.webapp.result.Result;
import com.example.webapp.third.AccessKeyIdSecretEnum;
import com.example.webapp.third.AliVideo;
import com.example.webapp.utils.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.aliyun.vod20170321.models.GetPlayInfoResponse;
import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@EnableTransactionManagement
@Slf4j
@Service
public class JsonServiceImpl implements JsonService, Serializable {
    private static final long serialVersionUID = 4800994516532057532L;
    public static final String PACKAGE_STATIC = "package-static";
    String key  = PACKAGE_STATIC + ":generateStaticJson";
    String keyPro  = PACKAGE_STATIC + ":generateStaticJson-process";

    /**
     * json文件基础目录
     */
    @Value("${portal.static.json.base.path:/tol/htdocs/ziptemp}")
    private String BASE_PATH;
    /**
     * 内容分类
     */
    @Value("${portal.category.course.content.id}")
    private Integer courseContentId;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private JsonMapper jsonMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;

    @Override
    public Result generateStaticJson(){

        String token = redisLock.tryLock(key, TimeUnit.HOURS.toMillis(1));
        try{
            if(token != null) {
                redisUtils.set(keyPro,"正在打包中，。打包时间："+ DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS), TimeUnit.HOURS.toSeconds(1));
                CategoryDO categoryDO = categoryMapper.findNodeById(courseContentId);
                if(categoryDO == null){
                    return Result.fail("分类不存在");
                }
                List<CategoryDTO> nodeList =  categoryMapper.findChildNodeByParentId(courseContentId);
                if(CollectionUtils.isNotEmpty(nodeList)){
                    buildJavaFile(BASE_PATH+"/dfyd-static/category.json", new Gson().toJson(nodeList));
                    AtomicInteger categoryIndex = new AtomicInteger();
                    nodeList.forEach(n->{
                        categoryIndex.getAndIncrement();
                        List<CourseDTO>  courseList = findPortalCourseList(n.getId());
                        if(CollectionUtils.isNotEmpty(courseList)){
                            AtomicInteger courseIndex = new AtomicInteger();
                            buildJavaFile(BASE_PATH+"/dfyd-static/course/list/category-"+n.getId()+".json", new Gson().toJson(courseList));
                            courseList.forEach(c->{
                                courseIndex.getAndIncrement();
                                List<CourseSectionDTO> sectionList = courseMapper.viewSection(c.getId());
                                if(CollectionUtils.isNotEmpty(sectionList)){
                                    AtomicInteger sectionIndex = new AtomicInteger();
                                    sectionList.forEach(s->{
                                        sectionIndex.getAndIncrement();
                                        s.setVideoPlayName(getVideoPalyNameById(s.getType(),s.getVideo()));
                                        log.info("分类size:{},课程size:{},章节size:{}",nodeList.size(),courseList.size(),sectionList.size());
                                        log.info("当前处理 -分类index:{},课程index:{},章节index:{}",categoryIndex.get(),courseIndex.get(),sectionIndex.get());
                                    });
                                    buildJavaFile(BASE_PATH+"/dfyd-static/course/detail/course-"+c.getId()+"-section-list.json", new Gson().toJson(sectionList));
                                }
                            });
                        }
                    });
                }
                zipStaticFile();
                redisUtils.set(keyPro,"打包完成 \n 打包时间："+ DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS) +"\n 完成时间:"+ DateTimeUtil.format(new Date(),DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS)+ "\n易读运营端下载地址：https://dfydyunying.bjfuture.cn/api/bms/json/dowload");
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.ok(redisUtils.get(keyPro));
    }

    @Override
    public Result procesds(int delCache) {
        String mess = (String) redisUtils.get(keyPro);
        if(StringUtils.isBlank(mess)){
            mess = "请先进行打包.....";
        }
        Integer del =1;
        if(del.equals(delCache)){
            try {
                redisUtils.del(keyPro);
            }catch (Exception e){
                log.error("{}",ExceptionUtils.getStackTrace(e));
            }
        }
        return Result.ok(mess);
    }

    /**
     * 打包文件
     */
    private void zipStaticFile() {
        toZip(BASE_PATH+"/dfyd-static",BASE_PATH+"/dfyd-static.zip" ,true);
    }

    /**
     *
     * 文件夹压缩成ZIP
     *
     * @param srcDir           压缩文件夹路径
     * @param out              压缩文件输出文件路径
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     *
     * @throws RuntimeException 压缩失败会抛出运行时异常
     *
     */
    public static void toZip(String srcDir, String out, boolean keepDirStructure)
            throws RuntimeException {
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(out));
            File sourceFile = new File(srcDir);
            compress(sourceFile, zos, sourceFile.getName(), keepDirStructure);
            long end = System.currentTimeMillis();
            log.info("(压缩完成，耗时：{} ms",end -start);
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils", e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    log.error("{}",ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }

    /**
     *
     * 递归压缩方法
     * @param sourceFile       源文件
     * @param zos              zip输出流
     * @param name             压缩后的名称
     * @param keepDirStructure 是否保留原来的目录结构,true:保留目录结构;
     *
     *                         false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     * @throws Exception
     */
    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean keepDirStructure) throws Exception {
        byte[] buf = new byte[2048];
        if (sourceFile.isFile()) {
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1) {
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                // 需要保留原来的文件结构时,需要对空文件夹进行处理
                if (keepDirStructure) {
                    // 空文件夹的处理
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy
                    zos.closeEntry();
                }
            } else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (keepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(), keepDirStructure);
                    } else {
                        compress(file, zos, file.getName(), keepDirStructure);
                    }
                }
            }
        }
    }

    /**
     * type 0章 1节
     * @param type
     * @param videoId
     * @return
     */
    private String  getVideoPalyNameById(Integer type,String videoId) {
        try {
            if( !CourseSectionTypeEnum.SECTION.getType().equals(type) || StringUtils.isBlank(videoId)){
                log.info("章或videoId 不存在：type:{},videoId:{}",type,videoId);
                return null;
            }
            String name = (String) redisUtils.get(PACKAGE_STATIC + ":edu-video-name:" +videoId);
            if(StringUtils.isBlank(name)){
                AliVideo video = AliVideo.getInstanceInfo(AccessKeyIdSecretEnum.ALI_VIDEO.getAk(), AccessKeyIdSecretEnum.ALI_VIDEO.getAks(),AccessKeyIdSecretEnum.ALI_VIDEO.getEp());
                GetPlayInfoResponse playInfo =  video.getPlayInfo(videoId);
                name = playInfo.getBody().getVideoBase().getTitle();
                redisUtils.set(videoId, name);
            }
            return name;
        } catch (Exception e) {
            log.error("换取视频名称异常:type:{},videoId:{},e:{}",type,videoId, ExceptionUtils.getStackTrace(e));
            return "视频名称异常,videoId:"+videoId;
        }
    }


    /**
     * 用户端课程列表（机构购买&自建）
     * @param categoryId
     * @return
     */
    public List<CourseDTO>  findPortalCourseList(Integer categoryId) {
        try {
            CourseJsonQuery query = new CourseJsonQuery();
            query.setCategoryId(categoryId);
            List<CourseDTO> list = jsonMapper.selectAll(query);
            if(CollectionUtils.isNotEmpty(list)){
                List<Integer> courseIdList = list.stream().map(CourseDTO::getId).collect(Collectors.toList());
                Map<Integer, ObjectCategoryDTO> categoryMap = categoryService.getObjectCategoryMap(courseIdList, ObjectTypeEnum.VIDEO.getType());
                list.stream().forEach(courseDTO -> {
                    if(categoryMap.get(courseDTO.getId()) != null){
                        courseDTO.setIdFullPathList(parseStrToStringList(categoryMap.get(courseDTO.getId()).getIdFullPaths()));
                        courseDTO.setNameFullPathList(parseStrToStringList(categoryMap.get(courseDTO.getId()).getNameFullPaths()));
                    }
                });
            }
            return list;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
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
            log.error("文件生成失败: filePath:{}",filePath);
        }
    }

    private List<String> parseStrToStringList(String names) {
        List<String> nameList = Lists.newArrayList();
        if(StringUtils.isNotBlank(names)){
            nameList = Arrays.asList(names.split(","));
        }
        return nameList;
    }

}

