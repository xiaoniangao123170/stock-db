package com.ming.stock.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ming.stock.mapper.SysPermissionMapper;
import com.ming.stock.mapper.SysRoleMapper;
import com.ming.stock.mapper.SysRolePermissionMapper;
import com.ming.stock.pojo.domain.PermissionTreeDomain;
import com.ming.stock.pojo.entity.SysPermission;
import com.ming.stock.pojo.entity.SysRole;
import com.ming.stock.pojo.entity.SysRolePermission;
import com.ming.stock.service.RoleService;
import com.ming.stock.utils.IdWorker;
import com.ming.stock.vo.req.RoleAddVo;
import com.ming.stock.vo.req.RolePageReqVo;
import com.ming.stock.vo.req.RoleUpdateVo;
import com.ming.stock.vo.resp.PageResult;
import com.ming.stock.vo.resp.R;
import com.ming.stock.vo.resp.ResponseCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author by Ming
 * @Description 角色服务接口实现
 */

@Service("roleService")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 分页查询当前角色信息
     *
     * @param vo
     * @return
     */
    @Override
    public R<PageResult> quryPageRole(RolePageReqVo vo) {
        //1.组装分页参数
        PageHelper.startPage(vo.getPageNum(), vo.getPageSize());
        //2.调用mapper查询
        List<SysRole> roleList = sysRoleMapper.selectAll();
        //3.封装PageResult
        PageResult<SysRole> pageResult = new PageResult<>(new PageInfo<>(roleList));
        return R.ok(pageResult);
    }

    @Override
    public R<List<PermissionTreeDomain>> getPermissionTreeAll() {
        // 根节点 pid = 0
        List<SysPermission> rootList = sysPermissionMapper.selectByPid(0L);
        List<PermissionTreeDomain> tree = buildTree(rootList);
        return R.ok(tree);
    }
//    @Override
//    public R<List<PermissionTreeDomain>> getPermissionTreeAll() {
//        List<SysPermission> allPerms = sysPermissionMapper.selectAllPermission();
//
//        // 1. 实体转VO
//        Map<Long, PermissionTreeDomain> nodeMap = allPerms.stream().collect(
//                Collectors.toMap(SysPermission::getId, p -> {
//                    PermissionTreeDomain node = new PermissionTreeDomain();
//                    node.setId(p.getId());
//                    node.setTitle(p.getTitle());
//                    node.setIcon(p.getIcon());
//                    node.setPath(p.getUrl());
//                    node.setName(p.getName());
//                    node.setChildren(new ArrayList<>());
//                    return node;
//                })
//        );
//
//        // 2. 组装父子关系
//        List<PermissionTreeDomain> rootTree = new ArrayList<>();
//        for (SysPermission perm : allPerms) {
//            if (perm.getPid() == 0L) {
//                rootTree.add(nodeMap.get(perm.getId()));
//            } else {
//                PermissionTreeDomain parent = nodeMap.get(perm.getPid());
//                if (parent != null) {
//                    parent.getChildren().add(nodeMap.get(perm.getId()));
//                }
//            }
//        }
//        return R.ok(rootTree);
//    }


    /**
     * 递归构建树形结构
     */
    private List<PermissionTreeDomain> buildTree(List<SysPermission> permissionList) {
        List<PermissionTreeDomain> result = new ArrayList<>();
        for (SysPermission p : permissionList) {
            PermissionTreeDomain node = new PermissionTreeDomain();
            node.setId(p.getId());
            node.setTitle(p.getTitle());
            node.setIcon(p.getIcon());
            node.setPath(p.getUrl());   // url → path
            node.setName(p.getName());   // name → name

            // 递归查询子节点
            List<SysPermission> children = sysPermissionMapper.selectByPid(p.getId());
            if (children != null && !children.isEmpty()) {
                node.setChildren(buildTree(children));
            } else {
                node.setChildren(new ArrayList<>());
            }
            result.add(node);
        }
        return result;
    }

    /**
     * 添加角色和角色关联权限
     *
     * @param vo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R<?> addRole(RoleAddVo vo) {
        // 1. 参数基础校验
        if (vo == null || StringUtils.isBlank(vo.getName())) {
            return R.error(ResponseCode.ERROR.getMessage());
        }
        List<Long> permIdList = vo.getPermissionsIds();
        if (permIdList == null) {
            permIdList = Collections.emptyList();
        }
        // 去重，避免同一角色重复绑定相同权限
        List<Long> distinctPermIds = permIdList.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // 2. 查重（只查未删除有效角色 deleted = 0）
        SysRole existRole = sysRoleMapper.selectByName(vo.getName());
        if (existRole != null) {
            return R.error(ResponseCode.ERROR.getMessage());
        }

        Date now = new Date();
        // 3. 构建角色，deleted=0 有效未删除
        SysRole sysRole = SysRole.builder()
                .id(idWorker.nextId())
                .name(vo.getName())
                .description(vo.getDescription())
                .status(1)
                .deleted(1) // 新建有效角色 deleted = 1
                .createTime(now)
                .updateTime(now)
                .build();
        sysRoleMapper.insert(sysRole);
        Long roleId = sysRole.getId();

        // 4. 批量保存角色权限关联
        if (!distinctPermIds.isEmpty()) {
            List<SysRolePermission> batchList = new ArrayList<>();
            for (Long permId : distinctPermIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setId(idWorker.nextId());
                rp.setRoleId(roleId);
                rp.setPermissionId(permId);
                rp.setCreateTime(now);
                batchList.add(rp);
            }
            sysRolePermissionMapper.batchInsert(batchList);
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 根据角色id查找对应的权限id集合
     * @param roleId
     * @return
     */
    @Override
    public R<Set<String>> getPermissionIdsByRoleId(String roleId) {
        //根据角色id查找权限id集合
        Set<String> permissionIds=this.sysRolePermissionMapper.getPermissionIdsByRoleId(roleId);
        permissionIds= permissionIds==null?new HashSet<>():permissionIds;
        return R.ok(permissionIds);
    }

    /**
     * 更新角色信息，包含角色关联的权限信息
     * @param vo
     * @return
     */
    @Override
    public R<String> updateRoleWithPermissions(RoleUpdateVo vo) {
        //1.更新角色信息
        SysRole role = SysRole.builder().id(vo.getId()).name(vo.getName()).updateTime(new Date())
                .description(vo.getDescription()).build();
        int updateCount = this.sysRoleMapper.updateByPrimaryKeySelective(role);
        if (updateCount!=1) {
            throw new RuntimeException(ResponseCode.ERROR.getMessage());
        }
        //2.删除当前角色关联的权限信息
        sysRolePermissionMapper.deleteByRoleId(vo.getId());
        //3.插入当前角色权限信息
        List<Long> permissionsIds = vo.getPermissionsIds();
        if (!CollectionUtils.isEmpty(permissionsIds)) {
            List<SysRolePermission> rps = permissionsIds.stream().map(permissionId -> {
                SysRolePermission rp = SysRolePermission.builder().roleId(vo.getId()).permissionId(permissionId)
                        .createTime(new Date()).id(idWorker.nextId()).build();
                return rp;
            }).collect(Collectors.toList());
            this.sysRolePermissionMapper.addRolePermissionBatch(rps);
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }


    /**
     * 根据角色id删除角色信息
     * @param roleId
     * @return
     */
    @Override
    public R<String> deleteRoleById(Long roleId) {
        //逻辑删除，更新角色状态
        SysRole role = SysRole.builder().id(roleId).deleted(0).updateTime(new Date()).build();
        int count = sysRoleMapper.updateByPrimaryKeySelective(role);
        if (count!=1) {
            throw new RuntimeException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }

    /**
     * 更新用户的状态信息
     * @param roleId 角色id
     * @param status 状态 1.正常 0：启用
     * @return
     */
    @Override
    public R<String> updateRoleStatus(Long roleId, Integer status) {
        //组装数据
        SysRole role = SysRole.builder().id(roleId).status(status).updateTime(new Date()).build();
        //更新
        int count = this.sysRoleMapper.updateByPrimaryKeySelective(role);
        if (count!=1) {
            throw new RuntimeException(ResponseCode.ERROR.getMessage());
        }
        return R.ok(ResponseCode.SUCCESS.getMessage());
    }


}