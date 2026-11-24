package com.ming.stock.security.service;

import com.google.common.base.Strings;
import com.ming.stock.mapper.SysRoleMapper;
import com.ming.stock.mapper.SysUserMapper;
import com.ming.stock.pojo.domain.PermissionTreeDomain;
import com.ming.stock.pojo.entity.SysPermission;
import com.ming.stock.pojo.entity.SysRole;
import com.ming.stock.pojo.entity.SysUser;
import com.ming.stock.security.user.LoginUserDetail;
import com.ming.stock.service.PermissionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("userDetailsService")
public class LoginUserDetailService implements UserDetailsService {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    /**
     * 根据认证时传入的用户名去指定资源库下的获取用户的详情信息 用户名 密文密码 权限集合
     * @param username the username identifying the user whose data is required.
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名去数据库中获取基础信息
        SysUser tbUser = sysUserMapper.findByUserName(username);
        if (tbUser == null) {
            throw new UsernameNotFoundException("当前用户不存在");
        }
        //组装UserDetails对象
        //需要的字段 用户名 密文密码 权限集合
        //其他字段 id email phone menus permissions


//获取指定用户的权限费合 添加获取侧边样数据和按钮权限的结合信息
        List<SysPermission> permissions = permissionService.getPermissionByUserId(tbUser.getId());
        List<SysRole> roles=sysRoleMapper.getRolesByUserId(tbUser.getId());
        //获取树状权限菜单数据
        List<PermissionTreeDomain> menus = permissionService.getTree(permissions,"0",true);
        //获取菜单按钮集合
        List<String> authBthPerms = permissions.stream().filter(per -> !Strings.isNullOrEmpty(per.getCode()) && per.getType() == 3)
                .map(per -> per.getCode()).collect(Collectors.toList());
        //获取SpringSecurity的权限标识 ROLE_角色的名称+权限自身标识
        ArrayList<String> ps=new ArrayList<>();
        List<String> pers = permissions.stream()
                .filter(per -> StringUtils.isNoneBlank(per.getPerms()))
                .map(per -> per.getPerms())
                .collect(Collectors.toList());
        ps.addAll(pers);
        List<String> rs = roles.stream().map(r -> "ROLE_" + r.getName()).collect(Collectors.toList());
        ps.addAll(rs);
//将用户拥有的权限表示为权限对象
        String[] psArray = ps.toArray(new String[pers.size()]);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(psArray);
//构建用户详情服务对象
        LoginUserDetail userDetail = new LoginUserDetail();
        BeanUtils.copyProperties(tbUser, userDetail);
        userDetail.setMenus(menus);
        userDetail.setPermissions(authBthPerms);
        userDetail.setAuthorities(authorityList);
        return userDetail;


    }
}
