package com.example.webapp.bms.controller;

import com.example.webapp.DO.FakeDateSetDO;
import com.example.webapp.DO.MechanismContactPersonDO;
import com.example.webapp.DO.MechanismDO;
import com.example.webapp.DTO.MechanismDTO;
import com.example.webapp.Service.mechanism.MechanismService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.query.IpQuery;
import com.example.webapp.query.MechanismQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.EncryptUtil;
import com.example.webapp.utils.http.IpUtil;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by gehaisong
 */
@Api(tags = {"1004-机构"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/mech")
public class MechanismController {
    public static final String PC = "pc";
    public static final String H5 = "h5";

    @Value("${portal.domain}")
    private String portalDomain;
    @Autowired
    private MechanismService mechanismService;

    /**
     * https://dfyd.bjfuture.cn/#/login/fd2032c72a0f13daf38c964d006583af20947fb74e8dd922
     * @param mechanismId
     * @return
     */
    @ApiOperation(value = "获取机构访问地址",notes = "获取机构访问地址（aci机构认证信息）")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismId",value="机构ID",required=true)})
    @PostMapping(value = "get-access-url/{mechanismId}")
    public Result getAccessUrl (@PathVariable  Integer mechanismId){
        try {
            MechanismDTO mechanismDTO = mechanismService.getMechanismById(mechanismId);
            if(mechanismDTO == null){
                return Result.fail("机构不存在");
            }
            String domain = portalDomain +"/#/login/";
            mechanismDTO.setAppDomain(domain);
            mechanismDTO.setDomain(domain);
            String sid = EncryptUtil.encryptString(mechanismId + "\1" +0+"\1"+2022052812);
            Map<String,String> map = Maps.newHashMap();
            map.put("aci",sid);
            if(StringUtils.isNotBlank(mechanismDTO.getDomain())){
                String xg = mechanismDTO.getDomain().endsWith("/")?"":"/";
                map.put(PC,mechanismDTO.getDomain()+xg+sid);
            }
            if(StringUtils.isNotBlank(mechanismDTO.getAppDomain())){
                String xg = mechanismDTO.getAppDomain().endsWith("/")?"":"/";
                map.put(H5,mechanismDTO.getAppDomain()+xg+sid);
            }
           return Result.ok(map);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    @ApiOperation(value = "机构列表",notes = "机构列表")
    @PostMapping(value = "list")
    public ResultPage findMechanismList (@RequestBody MechanismQuery query){
        PageInfo page = mechanismService.findMechanismList(query);
        if(page==null){
            page =new PageInfo();
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    @ApiOperation(value = "机构主键查询",notes = "机构主键查询")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismId",value="机构ID",required=true)})
    @PostMapping(value = "find/{mechanismId}")
    public Result getMechanismById (@PathVariable  Integer mechanismId){
        try {
            return mechanismService.findMechanismById(mechanismId);
        } catch (Exception e) {
           log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "创建/更新机构",notes = "同时创建学院根节点,当id不为空时,为更新")
    @PostMapping(value = "add")
    public Result add(@RequestBody MechanismDO mechanismDO){
        Result result = mechanismService.saveMechanismDO(mechanismDO);
        return result;
    }

    @ApiOperation(value = "创建/编辑联系人",notes = "id不为空是更新")
    @PostMapping(value = "add-person")
    public Result saveContactPerson(@RequestBody List<MechanismContactPersonDO> personList){
        Result result = mechanismService.saveContactPerson(personList);
        return result;
    }

    @ApiOperation(value = "验证机构账号是否可用",notes = "验证机构账号是否可用")
    @ApiImplicitParams({ @ApiImplicitParam(name="account",value="机构账号",required=true)})
    @PostMapping(value = "verify-dccount")
    public Result verifyAccount(@RequestParam("account") String account ){
        return mechanismService.verifyAccount(account);
    }

    @ApiOperation(value = "删除机构",notes = "删除机构")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismId",value="机构ID",required=true)})
    @PostMapping(value = "delete/{mechanismId}")
    public Result deleteMechanism(@PathVariable Integer mechanismId){
        return mechanismService.deleteMechanism(mechanismId);
    }
    @ApiOperation(value = "判断机构是否有开通记录",notes = "判断机构是否有开通记录，有开通记录不能删除")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismId",value="机构ID",required=true,paramType="path")})
    @PostMapping(value = "is-opened/{mechanismId}")
    public Result isOpened(@PathVariable Integer mechanismId){
        return mechanismService.isOpened(mechanismId);
    }

    @ApiOperation(value = "是否限制IP", notes = "是否限制IP")
    @ApiImplicitParams({@ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true),
            @ApiImplicitParam(name = "restrict", value = "限制IP：0否 1是", required = true)})
    @RequestMapping(value = "/restrict-ip/{mechanismId}/{restrict}", method = RequestMethod.POST)
    public Result restrictIp(@PathVariable Integer mechanismId, @PathVariable Integer restrict) {
        return mechanismService.restrictIp(mechanismId,restrict);
    }

    @ApiOperation(value = "新增机构 IP段", notes = "ip/掩码位 或 ip : 218.240.38.234/24")
    @ApiImplicitParams({@ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true),
            @ApiImplicitParam(name = "ipMaskBit", value = "ip/掩码位 或 ip : 218.240.38.234/24", required = true)})
    @RequestMapping(value = "/add-ip-maskbit", method = RequestMethod.POST)
    public Result addIpMaskBit(Integer mechanismId,String ipMaskBit) {
        if(StringUtils.isBlank(ipMaskBit)){
            return Result.fail("ip不能为空");
        }
        if( ipMaskBit.indexOf("/") > 0){
            Integer maskBit = Integer.parseInt(ipMaskBit.split("/")[1]);
            if(maskBit <1 || maskBit >32){
                return Result.fail("掩码位正确范围是1-32");
            }
        }
        return mechanismService.addIp(mechanismId,ipMaskBit);
    }

    @ApiOperation(value = "删除机构 IP段 ",notes = "删除机构 IP段 ")
    @ApiImplicitParams({@ApiImplicitParam(name = "mechanismId", value = "机构ID", required = true),
                       @ApiImplicitParam(name="ipId",value="IP段ID",required=true)})
    @PostMapping(value = "delete-ip")
    public Result deleteIp(Integer mechanismId,Integer ipId){
        return mechanismService.deleteIp(mechanismId,ipId);
    }

    @ApiOperation(value = "机构 IP段 列表", notes = "机构 IP段 列表")
    @RequestMapping(value = "/ip-list", method = RequestMethod.POST)
    public Result ipList(@RequestBody IpQuery query) {
        return Result.ok(mechanismService.ipList(query));
    }

    @ApiOperation(value = "查询IP段 可以IP范围 ",notes = "查询IP段 可以IP范围")
    @ApiImplicitParams({@ApiImplicitParam(name="ipMaskBit",value="ip/掩码位: 218.240.38.234/24",required=true)})
    @PostMapping(value = "find-ip-range")
    public Result findIpRange(String ipMaskBit){
        if(StringUtils.isBlank(ipMaskBit)){
            return Result.fail("ip不能为空");
        }
        if( ipMaskBit.indexOf("/")<0){
            return Result.fail("掩码位不能为空");
        }

        Integer maskBit = Integer.parseInt(ipMaskBit.split("/")[1]);
        if(maskBit <1 || maskBit >32){
            return Result.fail("掩码位正确范围是1-32");
        }
        String ip = ipMaskBit.split("/")[0];
        String beginIp = IpUtil.getBeginIpStr(ip,String.valueOf(maskBit));
        String endIp = IpUtil.getEndIpStr(ip,String.valueOf(maskBit));
        Map<String,Object> map = Maps.newHashMap();
        map.put("A起始IP：",beginIp);
        map.put("A终止IP：",endIp);
        if(maskBit <=10){
            map.put("IP范围：","IP范围太大 不支持查询");
        }else if(maskBit>10 && maskBit < 20){
            List<String> ipRangeList = IpUtil.parseIpMaskRange(ip,String.valueOf(maskBit));
            map.put("IP范围-size",ipRangeList.size());
        }else {
            List<String> ipRangeList = IpUtil.parseIpMaskRange(ip,String.valueOf(maskBit));
            map.put("IP范围-size",ipRangeList.size());
            map.put(".IP范围",ipRangeList);
        }
        return Result.ok(map);
    }

    @ApiOperation(value = "添加机构统计数据生成模版",notes = "数据格式： {\"homepageVisits\":{\"baseValue\":10,\"multiplier\":2},\"courseVisits\":{\"baseValue\":10,\"multiplier\":2},\"activityVisits\":{\"baseValue\":10,\"multiplier\":2},\"studyVisits\":{\"baseValue\":10,\"multiplier\":2},\"myVisits\":{\"baseValue\":10,\"multiplier\":2}}")
    @PostMapping(value = "save-fakeset")
    public Result saveFakeset(@RequestBody FakeDateSetDO fakeDateSetDO){
        Result result = mechanismService.saveFakeset(fakeDateSetDO);
        return result;
    }

    @ApiOperation(value = "获取机构统计数据生成模版")
    @ApiImplicitParams({@ApiImplicitParam(name="mechanismId",value="机构ID",required=true,paramType="path")})
    @PostMapping(value = "get-fakeset/{mechanismId}")
    public Result getFakeset(@PathVariable Integer mechanismId){
        return mechanismService.getFakeset(mechanismId);
    }

}
