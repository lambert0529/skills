# MyBatis-Plus QueryWrapper 使用指南

## LambdaQueryWrapper

### 基本查询

```java
// 等值查询
LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(User::getUsername, "zhangsan");

// 模糊查询
queryWrapper.like(User::getUsername, "zhang");

// 范围查询
queryWrapper.between(User::getAge, 18, 65);

// 排序
queryWrapper.orderByDesc(User::getCreateTime);
queryWrapper.orderByAsc(User::getId);

// 组合条件
queryWrapper.eq(User::getDeleted, 0)
            .like(User::getUsername, "zhang")
            .ge(User::getAge, 18)
            .orderByDesc(User::getCreateTime);
```

## 常用方法

### 比较方法

- `eq(column, value)` - 等于
- `ne(column, value)` - 不等于
- `gt(column, value)` - 大于
- `ge(column, value)` - 大于等于
- `lt(column, value)` - 小于
- `le(column, value)` - 小于等于
- `between(column, val1, val2)` - 在范围内
- `notBetween(column, val1, val2)` - 不在范围内

### 模糊查询

- `like(column, value)` - LIKE '%value%'
- `notLike(column, value)` - NOT LIKE '%value%'
- `likeLeft(column, value)` - LIKE '%value'
- `likeRight(column, value)` - LIKE 'value%'

### 空值判断

- `isNull(column)` - IS NULL
- `isNotNull(column)` - IS NOT NULL

### 包含判断

- `in(column, values)` - IN
- `notIn(column, values)` - NOT IN

### 排序

- `orderByAsc(column)` - 升序
- `orderByDesc(column)` - 降序
- `orderBy(boolean condition, boolean isAsc, SFunction<T, ?> column)` - 条件排序

## 链式调用示例

```java
LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
    .eq(User::getDeleted, 0)
    .like(StringUtils.isNotBlank(keyword), User::getUsername, keyword)
    .ge(ageMin != null, User::getAge, ageMin)
    .le(ageMax != null, User::getAge, ageMax)
    .orderByDesc(User::getCreateTime);
```

## 条件判断

使用条件方法，只在条件为 true 时添加条件：

```java
queryWrapper.like(StringUtils.isNotBlank(keyword), User::getUsername, keyword)
            .ge(ageMin != null, User::getAge, ageMin);
```
