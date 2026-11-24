package com.ming.stock.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.google.common.base.Strings;
import com.ming.stock.constant.StockConstant;
import com.ming.stock.pojo.domain.PermissionTreeDomain;
import com.ming.stock.pojo.entity.SysPermission;
import com.ming.stock.service.PermissionService;
import com.ming.stock.service.UserService;
import com.ming.stock.mapper.SysUserMapper;
import com.ming.stock.pojo.entity.SysUser;
import com.ming.stock.utils.IdWorker;
import com.ming.stock.vo.req.LoginReqVo;
import com.ming.stock.vo.resp.LoginRespVo;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: Ming
 * @Description TODO
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PermissionService permissionService;

    /**
     * 根据用户名查询用户信息
     * @param userName 用户名称
     * @return
     */
    @Override
    public SysUser getUserByUserName(String userName) {
        SysUser user = sysUserMapper.findByUserName(userName);
        return user;
    }

    /**
     * 用户登录功能
     * @param vo
     * @return
     */
    @Override
    public R<LoginRespVo> login(LoginReqVo vo) {
        //判断参数是否合法
        if (vo == null || Strings.isNullOrEmpty(vo.getUsername())
                || Strings.isNullOrEmpty(vo.getPassword())
                || Strings.isNullOrEmpty(vo.getSessionId())
                || Strings.isNullOrEmpty(vo.getCode())) {
            return R.error(ResponseCode.USERNAME_OR_PASSWORD_ERROR.getMessage());
        }
        //从程序执行的效率看，先进行校验码的校验，成本较低
        //补充：根据传入的rkye 从redis中获取校验码
        //校验验证码和sessionId是否有效
        String rCheckCode = (String) redisTemplate.opsForValue().get(StockConstant.CHECK_PREFIX +vo.getSessionId());
        if (rCheckCode==null || ! rCheckCode.equalsIgnoreCase(vo.getCode())) {
            //响应验证码输入错误
            return R.error(ResponseCode.CHECK_CODE_ERROR.getMessage());
        }
        //是否需要添加手动淘汰redis缓存的数据，如果想快速淘汰，则可手动删除
        redisTemplate.delete("CK:" + vo.getSessionId());
        //2.根据用户名查询用户信息
        SysUser user = sysUserMapper.findByUserName(vo.getUsername());
        //3.判断用户是否存在
        if (user == null) {
            //用户不存在
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS.getMessage());
        }
        //4.调用密码匹配器匹配输入的明文密码和数据库的密文密码
        boolean isSuccess = passwordEncoder.matches(vo.getPassword(), user.getPassword());
        //4.1如果密码匹配不成功
        if (! isSuccess){
            return R.error(ResponseCode.ACCOUNT_NOT_EXISTS.getMessage());
        }
        //4.2成功则返回用户的正常信息
        LoginRespVo respVo = new LoginRespVo();
//        respVo.setUsername(user.getUsername());
//        respVo.setNickName(user.getNickName());
//        respVo.setPhone(user.getPhone());
//        respVo.setId(user.getId());
        //发现LoginRespVo与SysUser对象属性名称和类型一致
        //属性名称与类型必须相同，否则属性值无法copy
        BeanUtils.copyProperties(user, respVo);
        //获取指定用户的权限费合 添加获取侧边样数据和按钮权限的结合信息
        List<SysPermission> permissions = permissionService.getPermissionByUserId(user.getId());
        //获取树状权限菜单数据
        List<PermissionTreeDomain> tree = permissionService.getTree(permissions,"0",true);
        //获取菜单按钮集合
        List<String> authBthPerms = permissions.stream().filter(per -> !Strings.isNullOrEmpty(per.getCode()) && per.getType() == 3)
                .map(per -> per.getCode()).collect(Collectors.toList());
        respVo.setMenus(tree);
        respVo.setPermissions(authBthPerms);
        //后期 用jwt生成token
        respVo.setAccessToken(user.getId()+":"+user.getUsername());

        return R.ok(respVo);
    }

    /**
     *
     * 生成图片验证码功能
     * @return
     */
    @Override
    public R<Map> getCaptchaCode() {
        //1.生成图片验证码 参数:图片的宽 图片的高 图片的验证码长度 干扰线的数量
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(250, 40, 4, 5);
        captcha.setBackground(Color.LIGHT_GRAY);
        //自定义验证码生成方式
//        captcha.setGenerator(new CodeGenerator() {
//            @Override
//            public String generate() {
//                //自定义验证码生成逻辑
//                return RandomStringUtils.randomNumeric(4);
//            }
//
//            @Override
//            public boolean verify(String code, String userInputCode) {
//                //匹配验证码逻辑
//                return code.equalsIgnoreCase(userInputCode);
//            }
//        });
        //获取图片中的验证码 默认生成的验证码包含文字和数字 长度为4
        String checkCode = captcha.getCode();
        //获取经过base64编码处理的图片数据
        String imageData = captcha.getImageBase64();
        //2.生成sessionId 转化为String 避免精度的丢失
        String sessionId = String.valueOf(idWorker.nextId());
        log.info("当前生成的验证码:{},会话id:{}",checkCode,sessionId);
        //3.将sessionId作为key 验证码作为value保存到redis中 并且设置过期时间
        redisTemplate.opsForValue().set(StockConstant.CHECK_PREFIX +sessionId,checkCode,5, TimeUnit.MINUTES);
        //4.组装响应数据
        HashMap<String,String> data = new HashMap<>();
        data.put("imageData",imageData);
        data.put("sessionId",sessionId);
        //5.设置响应数据
        return R.ok(data);
    }
}
