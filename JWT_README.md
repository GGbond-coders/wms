# WMS仓储管理系统 - JWT认证使用说明

## JWT认证功能

系统已集成JWT (JSON Web Token) 认证机制，提供安全的API访问控制。

### 认证流程

1. **用户登录**
   - POST `/login`
   - 参数：`username`, `password`
   - 返回：包含JWT token的用户信息

2. **API访问**
   - 在请求头中添加 `Authorization: Bearer {token}`
   - token有效期为24小时

3. **获取当前用户信息**
   - GET `/user/info`
   - 需要在请求头中携带有效的token

### 测试用户

系统已预设以下测试用户：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | ADMIN |
| warehouse | 123456 | WAREHOUSE |
| sales | 123456 | SALES |

### API请求示例

#### 1. 登录获取token
```bash
curl -X POST "http://localhost:8080/login" \
  -d "username=admin&password=123456"
```

响应：
```json
{
  "code": 1,
  "msg": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "role": "ADMIN"
    }
  }
}
```

#### 2. 使用token访问API
```bash
curl -X GET "http://localhost:8080/goods" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### 3. 获取当前用户信息
```bash
curl -X GET "http://localhost:8080/user/info" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### 安全特性

- ✅ JWT token验证
- ✅ 请求拦截器自动验证token
- ✅ 跨域请求支持 (CORS)
- ✅ token过期自动失效
- ✅ 敏感信息加密存储

### 注意事项

- 所有API请求（除登录外）都需要在请求头中携带有效的JWT token
- token格式：`Authorization: Bearer {token}`
- token过期后需要重新登录获取新token
- 建议在前端使用axios拦截器自动添加token到请求头