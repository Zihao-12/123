package com.example.webapp.utils.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.bitwalker.useragentutils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.net.util.IPAddressUtil;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 描述:<br>IP工具类</br>
 * 网络标识  = 子网掩码（二进制） & IP地址 （二进制）  ， 网络标识一样属于同一网段
 *   子网掩码为 1 的部分表示网络号，子网掩码为 0 的部分表示主机号 (左边一半都是 1，右边一半都是 0)
 *   主机号全0：子网网络地址  主机号全1:广播地址
 *       子网掩码决定一个子网的主机数量：2的 主机号位数的 次方
 *       常见的C类IP地址，网络号（Net-ID）=24位，主机号（Host-ID）=8位
 *                      拥有2的8次方-2=254个ip（-2，扣除表示子网网络地址0、与广播255）
 *  查看出口IP命令: curl cip.cc
 * @author ghs
 * @since created by 2022/4/2 14:49
 */
@Slf4j
public class IpUtil {

    private static final String IP_PREFIX_10 = "10.";
    private static final String IP_PREFIX_192_168 = "192.168.";
    private static final String IP_PREFIX_172 = "172.";
    private static final Integer IP_PREFIX_172_16 = 16;
    private static final Integer IP_PREFIX_172_31 = 31;
    public static final String STRING_32 = "32";
    public static final String STRING_31 = "31";


    /**
     * 获取客户端IP
     * @return
     */
    public static String getIpAddr() {
//        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
//        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
//        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        return getIpAddr(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 判断是否是pc端的请求
     * @return
     */
    public static boolean isComputer() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //解析agent字符串
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        //浏览器对象
        Browser browser = userAgent.getBrowser();
        //操作系统对象
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        DeviceType deviceType = operatingSystem.getDeviceType();
//        String browerInfo ="浏览器名:"+browser.getName()+ " ,浏览器类型:"+browser.getBrowserType()+ " ,浏览器家族:"+browser.getGroup()+ " ,浏览器生产厂商:"+browser.getManufacturer()+ " ,浏览器使用的渲染引擎:"+browser.getRenderingEngine()+ " ,浏览器版本:"+userAgent.getBrowserVersion()+ " ,操作系统名:"+operatingSystem.getName()+ " ,访问设备类型:"+operatingSystem.getDeviceType()+ " ,操作系统家族:"+operatingSystem.getGroup()+ " ,操作系统生产厂商:"+operatingSystem.getManufacturer();
//        log.info("\n浏览器信息:{}",browerInfo);
        Manufacturer manufacturer = operatingSystem.getManufacturer();
        if(DeviceType.COMPUTER.equals(deviceType)){
            //PC 浏览器
            log.info("PC端-设备厂商：{},ip:{}",manufacturer,getIpAddr());
        }else  if(DeviceType.MOBILE.equals(deviceType)){
            //手机浏览器
            if(Manufacturer.APPLE.equals(manufacturer)){
                log.info("移动端-苹果手机 设备厂商：{},ip:{}",manufacturer,getIpAddr());
            }else {
                log.info("移动端-安卓手机 设备厂商：{},ip:{}",manufacturer,getIpAddr());
            }

        }
        return DeviceType.COMPUTER.equals(deviceType);
    }


    private static String getIpAddr(HttpServletRequest request) {
        String localIp = "127.0.0.1";
        String remoteLocalIp = "0:0:0:0:0:0:0:1";
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (isUnValidIp(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (isUnValidIp(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (isUnValidIp(ipAddress)) {
                ipAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (isUnValidIp(ipAddress)) {
                ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (isUnValidIp(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                ipAddress = remoteLocalIp.equals(ipAddress) ? localIp : ipAddress;
                if (ipAddress.equals(localIp)) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("{}",ExceptionUtils.getStackTrace(e));
                    }
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = findFirstIp(ipAddress);
                }
            }
        } catch (Exception e) {
            ipAddress = StringUtils.EMPTY;
        }
        return ipAddress;
    }

    private static boolean isUnValidIp(String ip) {
        return StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 过滤大部分的内网IP并且取出外网IP的第一个IP进行返回
     * 局域网可使用的网段（私网地址段）有三大段：
     * 10.0.0.0~10.255.255.255（A类）
     * 172.16.0.0~172.31.255.255（B类）
     * 192.168.0.0~192.168.255.255（C类）
     *
     * @param ipAddress 代理IP列表字符串(逗号分隔)
     * @return String
     */
    private static String findFirstIp(String ipAddress) {
        String firstIp = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(ipAddress) && ipAddress.contains(",")) {
            String[] ipSplit = ipAddress.split(",");
            if (ipSplit.length > 0) {
                firstIp = ipSplit[0];
                for (String ip : ipSplit) {
                    if (ip.startsWith(IP_PREFIX_10) || ip.startsWith(IP_PREFIX_192_168)) {
                        continue;
                    }
                    if (ip.startsWith(IP_PREFIX_172)) {
                        String[] split = ip.split("\\.");
                        //ip的第二段
                        int second = Integer.parseInt(split[1]);
                        if (second >= IP_PREFIX_172_16 && second <= IP_PREFIX_172_31) {
                            continue;
                        }
                    }
                    firstIp = ip;
                    break;
                }
            }
        }
        return firstIp;
    }


    /**
     * 是否是内网ip
     * 这里值得注意的是，这里不会判断本机ip，即127.0.0.1返回的事false，使用时需要注意
     *
     * @param ip 需要检查的ip地址
     * @return 是否是内网地址
     */
    public static boolean internalIp(String ip) {
        byte[] addr = IPAddressUtil.textToNumericFormatV4(ip);
        return internalIp(addr);
    }

    /**
     * 是否是内网ip或者是本地回环地址
     *
     * @param ip 需要检查的地址
     * @return 是不是内部的地址
     */
    public static boolean internalIpOrLocal(String ip) {
        boolean b = StringUtils.startsWith(ip, "127.");
        if (b) {
            return true;
        } else {
            return internalIp(ip);
        }
    }

    public static boolean internalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;
        }

    }

    /**
     *  判断 IP 是否在 指定网段范围内
     * @param ip          客户端IP
     * @param ipMaskBit   ip/掩码位: 218.240.38.234/24 或 218.240.38.234
     *          方法 getNetMask ： 根据 子网掩码 转换 掩码位  如 掩码:255.255.255.0 -->  掩码位:24
     * @return
     */
    public static boolean isInRange(String ip, String ipMaskBit) {
        try {
            if(StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(ipMaskBit)){
                if(ipMaskBit.indexOf("/") < 1 ){
                    return  ip.equals(ipMaskBit);
                }
                String specifyIp = ipMaskBit.split("/")[0];
                String maskBit = ipMaskBit.split("/")[1];
                Long beginIpLong = getIpFromString(getBeginIpStr(specifyIp,maskBit));
                Long endIpLong = getIpFromString(getEndIpStr(specifyIp,maskBit));
                Long ipLong = getIpFromString(ip);
                return  ipLong >= beginIpLong && ipLong <= endIpLong;
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return false;
    }

    /**
     * 把xx.xx.xx.xx类型的转为long类型的
     *   计算ip范围用此方法获取 long值，  getBeginIpLong 和 getEndIpLong 返回的long值 不能作为ip范围计算使用
     * @param ip
     * @return
     */
    public static Long getIpFromString(String ip){
        Long ipLong = 0L;
        String ipTemp = ip;
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf(".")));
        ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf(".")));
        ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
        ipLong = ipLong * 256
                + Long.parseLong(ipTemp.substring(0, ipTemp.indexOf(".")));
        ipTemp = ipTemp.substring(ipTemp.indexOf(".") + 1, ipTemp.length());
        ipLong = ipLong * 256 + Long.parseLong(ipTemp);
        return ipLong;
    }

    /**
     * 把long类型的Ip转为一般Ip类型：xx.xx.xx.xx
     * @param ip
     * @return
     */
    public static String getIpFromLong(Long ip) {
        String s1 = String.valueOf((ip & 4278190080L) / 16777216L);
        String s2 = String.valueOf((ip & 16711680L) / 65536L);
        String s3 = String.valueOf((ip & 65280L) / 256L);
        String s4 = String.valueOf(ip & 255L);
        String ipStr = s1 + "." + s2 + "." + s3 + "." + s4;
        return ipStr;
    }


    /**
     * 根据 IP 和 掩码位   生成 范围内的 IP 列表
     * @param ip
     * maskBit 掩码位
     * @return
     */
    public static List<String> parseIpMaskRange(String ip,String maskBit){
        List<String> list= Lists.newArrayList();
        if (STRING_32.equals(maskBit)) {
            list.add(ip);
        }else{
            String startIp=getBeginIpStr(ip, maskBit);
            String endIp=getEndIpStr(ip, maskBit);
            if (!STRING_31.equals(maskBit)) {
                String subStart=startIp.split("\\.")[0]+"."+startIp.split("\\.")[1]+"."+startIp.split("\\.")[2]+".";
                String subEnd=endIp.split("\\.")[0]+"."+endIp.split("\\.")[1]+"."+endIp.split("\\.")[2]+".";
                startIp=subStart+(Integer.valueOf(startIp.split("\\.")[3])+1);
                endIp=subEnd+(Integer.valueOf(endIp.split("\\.")[3])-1);
            }
            list=parseIpRange(startIp, endIp);
        }
        return list;
    }

    /**
     * 根据 起始IP 和 终止IP  生成 范围内的 IP 列表
     * @param ipfrom
     * @param ipto
     * @return
     */
    public static List<String> parseIpRange(String ipfrom, String ipto) {
        List<String> ips = Lists.newArrayList();
        String[] ipfromd = ipfrom.split("\\.");
        String[] iptod = ipto.split("\\.");
        int[] int_ipf = new int[4];
        int[] int_ipt = new int[4];
        for (int i = 0; i < 4; i++) {
            int_ipf[i] = Integer.parseInt(ipfromd[i]);
            int_ipt[i] = Integer.parseInt(iptod[i]);
        }
        for (int A = int_ipf[0]; A <= int_ipt[0]; A++) {
            for (int B = (A == int_ipf[0] ? int_ipf[1] : 0); B <= (A == int_ipt[0] ? int_ipt[1]
                    : 255); B++) {
                for (int C = (B == int_ipf[1] ? int_ipf[2] : 0); C <= (B == int_ipt[1] ? int_ipt[2]
                        : 255); C++) {
                    for (int D = (C == int_ipf[2] ? int_ipf[3] : 0); D <= (C == int_ipt[2] ? int_ipt[3]
                            : 255); D++) {
                        ips.add(new String(A + "." + B + "." + C + "." + D));
                    }
                }
            }
        }
        return ips;
    }


    /**
     * 根据掩码位获取掩码
     *
     * @param maskBit
     *            掩码位数，如"28"、"30"
     * @return
     */
    public static String getMaskByMaskBit(String maskBit) {
        String r = StringUtils.isEmpty(maskBit) ? "error, maskBit is null !" : maskBitMap().get(maskBit);
        return r;
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 起始IP的字符串表示
     */
    public static String getBeginIpStr(String ip, String maskBit) {
        String ipStr = getIpFromLong(getBeginIpLong(ip, maskBit));
        return ipStr;
    }

    /**
     * 根据 ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/30
     *     计算ip范围用 getIpFromString 方法获取 long值，  getBeginIpLong 和 getEndIpLong 返回的long值 不能作为ip范围计算使用
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 起始IP的长整型表示
     */
    public static Long getBeginIpLong(String ip, String maskBit) {
        Long ipLong = getIpFromString(ip) & getIpFromString(getMaskByMaskBit(maskBit));
        return ipLong;
    }


    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 终止IP的字符串表示
     */
    public static String getEndIpStr(String ip, String maskBit) {
        String ipStr = getIpFromLong(getEndIpLong(ip, maskBit));
        return ipStr;
    }

    /**
     * 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
     *  计算ip范围用 getIpFromString 方法获取 long值，  getBeginIpLong 和 getEndIpLong 返回的long值 不能作为ip范围计算使用
     * @param ip
     *            给定的IP，如218.240.38.69
     * @param maskBit
     *            给定的掩码位，如30
     * @return 终止IP的长整型表示
     */
    public static Long getEndIpLong(String ip, String maskBit) {
        Long ipLong = getBeginIpLong(ip, maskBit)
                + ~getIpFromString(getMaskByMaskBit(maskBit));
        return ipLong;
    }


    /**
     * 计算子网大小 ( IP数量 )
     *
     * @param maskBit
     *            掩码位
     * @return
     */
    public static int getPoolMax(String  maskBit) {
        int maskBitInt = Integer.parseInt(maskBit);
        if (maskBitInt <= 0 || maskBitInt >= 32) {
            return 0;
        }
        return (int) Math.pow(2, 32 - maskBitInt) - 2;
    }

    /**
     * 根据子网掩码转换为掩码位 如 255.255.255.252转换为掩码位 为 30
     * @param netmarks
     * @return
     */
    public static int getNetMask(String netmarks) {
        StringBuffer sbf;
        String str;
        int inetmask = 0, count = 0;
        String[] ipList = netmarks.split("\\.");
        for (int n = 0; n < ipList.length; n++) {
            sbf = toBin(Integer.parseInt(ipList[n]));
            str = sbf.reverse().toString();
            count = 0;
            for (int i = 0; i < str.length(); i++) {
                i = str.indexOf('1', i);
                if (i == -1)
                {
                    break;
                }
                count++;
            }
            inetmask += count;
        }
        return inetmask;
    }

    /**
     * 存储着所有的掩码位及对应的掩码 key:掩码位 value:掩码（x.x.x.x）
     * @return
     */
    private static Map<String, String> maskBitMap()
    {
        Map<String, String> maskBit = Maps.newHashMap();
        maskBit.put("1", "128.0.0.0");
        maskBit.put("2", "192.0.0.0");
        maskBit.put("3", "224.0.0.0");
        maskBit.put("4", "240.0.0.0");
        maskBit.put("5", "248.0.0.0");
        maskBit.put("6", "252.0.0.0");
        maskBit.put("7", "254.0.0.0");
        maskBit.put("8", "255.0.0.0");
        maskBit.put("9", "255.128.0.0");
        maskBit.put("10", "255.192.0.0");
        maskBit.put("11", "255.224.0.0");
        maskBit.put("12", "255.240.0.0");
        maskBit.put("13", "255.248.0.0");
        maskBit.put("14", "255.252.0.0");
        maskBit.put("15", "255.254.0.0");
        maskBit.put("16", "255.255.0.0");
        maskBit.put("17", "255.255.128.0");
        maskBit.put("18", "255.255.192.0");
        maskBit.put("19", "255.255.224.0");
        maskBit.put("20", "255.255.240.0");
        maskBit.put("21", "255.255.248.0");
        maskBit.put("22", "255.255.252.0");
        maskBit.put("23", "255.255.254.0");
        maskBit.put("24", "255.255.255.0");
        maskBit.put("25", "255.255.255.128");
        maskBit.put("26", "255.255.255.192");
        maskBit.put("27", "255.255.255.224");
        maskBit.put("28", "255.255.255.240");
        maskBit.put("29", "255.255.255.248");
        maskBit.put("30", "255.255.255.252");
        maskBit.put("31", "255.255.255.254");
        maskBit.put("32", "255.255.255.255");
        return maskBit;
    }

    private static StringBuffer toBin(int x) {
        StringBuffer result = new StringBuffer();
        result.append(x % 2);
        x /= 2;
        while (x > 0)
        {
            result.append(x % 2);
            x /= 2;
        }
        return result;
    }

    public static void main(String[] args) {
        String ip = "218.240.38.234";
        String maskBit = "27";
        String netmarks = "255.255.255.0";
        // 把xx.xx.xx.xx类型的转为long类型的
        Long ipLong  = getIpFromString(ip);
        log.info("[IP 转 Long] ip:{},Long:{}",ip,getIpFromString(ip));
        // 把long类型的Ip转为一般Ip类型：xx.xx.xx.xx
        log.info("[Long 转 IP] Long:{},ip:{}",ipLong,getIpFromLong(ipLong));

        // 根据掩码位数 获取掩码： 掩码位数，如"28"、"30"
        log.info("[掩码位 转 掩码] 掩码位:{},掩码:{}",maskBit,getMaskByMaskBit(maskBit));
        //根据子网掩码转换为掩码位 如 255.255.255.252转换为掩码位 为 30
        log.info("[掩码 转 掩码位] 掩码:{},掩码位:{}",netmarks,getNetMask(netmarks));

        // ip/掩码位 计算IP段的起始IP 如 IP串 218.240.38.69/24
        log.info("[ip/掩码位 计算 起始IP] ip/掩码位:{},起始IP:{}",ip+"/"+maskBit,getBeginIpStr(ip,maskBit));
        Long beginIp = getBeginIpLong(ip,maskBit);
        log.info("[ip/掩码位 计算 起始IP] ip/掩码位:{},起始IP:{}",ip+"/"+maskBit,beginIp);
        log.info("[验证IP:{}]",getIpFromLong(beginIp));

        // 根据 ip/掩码位 计算IP段的终止IP 如 IP串 218.240.38.69/30
        log.info("[ip/掩码位 计算 终止IP] ip/掩码位:{},终止IP:{}",ip+"/"+maskBit,getEndIpStr(ip,maskBit));
        Long endIp = getEndIpLong(ip,maskBit);
        log.info("[ip/掩码位 计算 终止IP] ip/掩码位:{},终止IP:{}",ip+"/"+maskBit,endIp);
        log.info("[验证IP:{}]",getIpFromLong(endIp));

        log.info("[掩码位 计算 IP数量], 掩码:{} IP数量;{}",maskBit,getPoolMax(maskBit));
//        log.info("==============================");
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.0",getIpFromString("218.240.38.0"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.1",getIpFromString("218.240.38.1"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.3",getIpFromString("218.240.38.3"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.10",getIpFromString("218.240.38.10"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.20",getIpFromString("218.240.38.20"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.244",getIpFromString("218.240.38.244"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.252",getIpFromString("218.240.38.252"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.254",getIpFromString("218.240.38.254"));
//        log.info("[IP 转 Long] ip:{},Long:{}","218.240.38.255",getIpFromString("218.240.38.255"));

//        218.240.38.224  218.240.38.255
        log.info("{}",isInRange("218.240.38.123",ip+"/"+maskBit));
        log.info("{}",isInRange("218.240.38.22",ip+"/"+maskBit));
        log.info("{}",isInRange("218.240.38.223",ip+"/"+maskBit));
        log.info("{}",isInRange("218.240.38.224",ip+"/"+maskBit));
        log.info("{}",isInRange("218.240.38.251",ip+"/"+maskBit));
        log.info("{}",isInRange("218.240.38.254",ip+"/"+maskBit));
        log.info("{}",isInRange("218.240.38.255",ip+"/"+maskBit));

        log.info("{}",parseIpMaskRange(ip,maskBit));

    }

    /**
     * 用户获取本机局域网IP地址
     * @return
     */
    public static String getIpServerAddr() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                } else {
                    Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            log.info("获取到的ip地址：{}", ip.getHostAddress());
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取ip地址失败，{}",e);
        }
        return null;
    }
}
