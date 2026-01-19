# PageHelper 使用指南

## 基本使用

### 分页查询步骤

1. 在查询前调用 `PageHelper.startPage(page, size)`
2. 执行查询（会自动应用分页）
3. 使用 `PageInfo` 获取分页信息

### 代码示例

```java
@Override
@Transactional(readOnly = true)
public PageResult<UserDTO> list(PageRequest pageRequest) {
    // 1. 启动分页
    PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize());
    
    // 2. 构建查询条件
    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(User::getDeleted, 0);
    
    // 3. 执行查询
    List<User> list = userService.list(queryWrapper);
    
    // 4. 获取分页信息
    PageInfo<User> pageInfo = new PageInfo<>(list);
    
    // 5. 转换为 DTO
    List<UserDTO> content = list.stream()
            .map(userConverter::toDTO)
            .collect(Collectors.toList());
    
    // 6. 构建分页结果
    return PageResult.<UserDTO>builder()
            .content(content)
            .total(pageInfo.getTotal())
            .page(pageRequest.getPage())
            .size(pageRequest.getSize())
            .totalPages(pageInfo.getPages())
            .hasPrevious(pageInfo.isHasPreviousPage())
            .hasNext(pageInfo.isHasNextPage())
            .isFirst(pageInfo.isIsFirstPage())
            .isLast(pageInfo.isIsLastPage())
            .build();
}
```

## 注意事项

1. **必须在查询前调用**：`PageHelper.startPage()` 必须在查询方法调用之前
2. **只对第一个查询生效**：分页只对紧跟着的第一个查询生效
3. **线程安全**：PageHelper 使用 ThreadLocal，确保线程安全
4. **自动清理**：查询完成后会自动清理 ThreadLocal

## 配置

### application.yml

```yaml
pagehelper:
  helper-dialect: mysql
  reasonable: true
  support-methods-arguments: true
  params: count=countSql
```
