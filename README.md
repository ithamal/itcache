### Maven依赖
```xml
<groupId>io.github.ithamal</groupId>
<artifactId>itcache</artifactId>
<version>1.0.0</version>
```

### 特性：
- 支持多级缓存（通过缓存链实现），
- 支持与Spring Cache集成
- 支持服务扩展： 实体、列表、分页等缓存方式
- 支持增量缓存加载
- 与spring-data-redis框架无缝集成
- 已实现缓存：redis、hutool （建议用hutool作为一级缓存，redis作为二级缓存）

## 示例
### springboot配置
```yaml
itcache:
  enable: true
  spring: true
  templates:
    default:
      prefix: "cache:"
      timeToLiveSeconds: 3600
      preCaches: memory
      implClass: redis
    memory:
      timeToLiveSeconds: 10
      maxElements: 10000
      implClass: hutool
  caches:
    test:
      template: default
      timeToLiveSeconds: 3600
```
已支持实现类（implCalss)：
- redis: redisKV（默认）、redisHash
- hutool: hutoolLRU（默认）、hutoolFIFO、hutoolLFU

未来支持：
- ehcache

### 多级缓存
通过配置属性“preCaches”（前置缓存），以实现缓存链，而达到多级缓存效果。
```yaml
itcache:
  templates:
    default:
      prefix: "cache:"
      timeToLiveSeconds: 3600
      preCaches: memory
      implClass: redis
    memory:
      timeToLiveSeconds: 10
      maxElements: 10000
      implClass: hutool
```
此配置构成缓存链： memory（hutool）-> default(redis)； 

memory为一级缓存设置，default为二级缓存设置。

#### 框架缓存管理
```java
@Resource
private io.github.ithamal.itcache.core.CacheManager itCacheManager;

@Test
public void test() throws Exception {
    Cache cache = itCacheManager.getCache("test2");
    cache.put(1, "张三");
    System.out.println(cache.get(1)); // 张三
    System.out.println(cache.get(2));  // null
}
```

#### 与spring-cache集成
```yaml
itcache:
    spring: true
```
```java
@Resource
private org.springframework.cache.CacheManager cacheManager;

@Test
public void test() throws Exception {
    Cache cache = cacheManager.getCache("test2");
    cache.put(1, "张三");
    System.out.println(cache.get(1)); // 张三
    System.out.println(cache.get(2));  // null
}
```

####  服务扩展：实体服务
重要特性：支持缓存的“增量加载” 
```java
public class UserEntityCacheService extends EntityCacheService<Integer, User> {

    @Override
    protected String getRegion() {
        return "users";
    }

    @Override
    protected String buildKey(Integer key) {
        return String.valueOf(key);
    }

    @Override
    protected Map<Integer, User> loadFromDb(Collection<Integer> keys) {
        HashMap<Integer, User> map = new HashMap<>();
        map.put(1, new User(1, "张三"));
        map.put(2, new User(2, "李四"));
        return map;
    }
}
```

```java
@Test
public void testEntityCache(){
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    UserEntityCacheService userCacheService = new UserEntityCacheService();
    userCacheService.setCacheManager(cacheManager);
    System.out.println(userCacheService.load(1));
    System.out.println(userCacheService.batchLoad(Arrays.asList(1, 2, 3 ,4)));
    System.out.println(userCacheService.batchLoad(Arrays.asList(1, 2, 3 ,4)));
}
```

####  服务扩展：列表服务
```java
public class UserListCacheService extends ListCacheService<Integer, Integer, User> {

    @Override
    protected String getRegion() {
        return "users.list";
    }

    @Override
    protected String buildKey(Integer key) {
        return String.valueOf(key);
    }

    @Override
    protected List<Integer> loadFromDb(Integer key) {
        return Arrays.asList(1, 2, 3,4);
    }

    @Override
    protected EntityCacheService<Integer, User> getEntityCacheService() {
        UserEntityCacheService service = new UserEntityCacheService();
        service.setCacheManager(this.getCacheManager());
        return service;
    }
}
```
```java
@Test
public void testListCache(){
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    UserListCacheService userListCacheService = new UserListCacheService();
    userListCacheService.setCacheManager(cacheManager);
    System.out.println(userListCacheService.loadIfAbsent(1, ()->{
        return Arrays.asList(2);
    }));
    System.out.println(userListCacheService.loadList(1));
    System.out.println(userListCacheService.loadList(2));
}
```

####  服务扩展：分页服务
```java
public class UserPageCacheService extends PageCacheService<QueryDto, Integer, User> {

    @Override
    protected String getRegion() {
        return "users.page";
    }

    @Override
    protected String buildKey(QueryDto key) {
        return key.toString();
    }

    @Override
    protected Page<Integer> loadFromDb(QueryDto key) {
        return Page.<Integer>of(key.getPageNumber(), key.getPageSize()).items(Arrays.asList(1, 2, 3));
    }

    @Override
    protected EntityCacheService<Integer, User> getEntityCacheService() {
        UserEntityCacheService service = new UserEntityCacheService();
        service.setCacheManager(this.getCacheManager());
        return service;
    }
}
```
```java
@Test
public void testPageCache(){
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    UserPageCacheService userPageCacheService = new UserPageCacheService();
    userPageCacheService.setCacheManager(cacheManager);
    QueryDto queryDto = new QueryDto();
    queryDto.setPageNumber(1);
    queryDto.setPageSize(20);
//        userPageCacheService.loadIfAbsent(queryDto, ()->{
//            return Page.<Integer>of(queryDto.getPageNumber(), queryDto.getPageSize()).items(Arrays.asList(1));
//        });
    System.out.println(userPageCacheService.loadPage(queryDto));
}
```
