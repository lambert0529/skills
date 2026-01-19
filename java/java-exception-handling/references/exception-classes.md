# 异常类设计

## 异常类层次结构

### 基础异常类

```java
public class BaseException extends RuntimeException {
    
    private final ErrorCode errorCode;
    private final Object[] args;
    
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public BaseException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = args;
    }
    
    public BaseException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.args = null;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public Object[] getArgs() {
        return args;
    }
}
```

## 业务异常

```java
public class BusinessException extends BaseException {
    
    public BusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```

## 系统异常

```java
public class SystemException extends BaseException {
    
    public SystemException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
```
