# 响应格式示例

## 成功响应（单个对象）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "createTime": "2024-01-01T10:00:00"
  },
  "timestamp": 1704067200000,
  "traceId": "trace-123456"
}
```

## 成功响应（列表）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "username": "zhangsan",
      "email": "zhangsan@example.com"
    },
    {
      "id": 2,
      "username": "lisi",
      "email": "lisi@example.com"
    }
  ],
  "timestamp": 1704067200000
}
```

## 成功响应（分页）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "zhangsan",
        "email": "zhangsan@example.com"
      }
    ],
    "total": 100,
    "page": 1,
    "size": 10,
    "totalPages": 10,
    "hasPrevious": false,
    "hasNext": true,
    "isFirst": true,
    "isLast": false
  },
  "timestamp": 1704067200000
}
```

## 失败响应

```json
{
  "code": 1001,
  "message": "用户不存在",
  "data": null,
  "timestamp": 1704067200000,
  "traceId": "trace-123456"
}
```
