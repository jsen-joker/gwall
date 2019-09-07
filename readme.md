
## 数据库相关
    数据库放在 res文件夹下
    route.json是mongodb中用于支持数据中台的网关路由配置
    gwall.sql 是数据中台对应的mysql数据库，只是保存了登录用户，默认用户名密码 admin / ant.design
    

# 启动
    启动 mysql mongo redis，注意 authentication mysql的用户名和密码配置
    配置好nacos服务器地址
    启动 gwall guard authentication 模块
    启动gront cnpm i & npm run start:no-mock，访问8000端口 admin/ant.design


# gwall

    mongodb
    开放式网关
    必须 核心网关
    
# authentication

    redis + mysql
    网关下的用户认证系统
    网关下有用户需用户认证api的必须有认证系统
    基于redis的sso登录
    
# authorization
    
    redis
    网关下的权限系统
    网关下有权限认证api的必须有权限系统
    
# gront
    数据中台，目前只用于gwall路由管理，后期实现业务级别数据控制
    
# guard
    中台对应的后端

    
# 数据库
    gwall 使用 mongodb 保存路由信息
    使用 redis 实现 sso
    使用 mysql 实现用户认证系统和api权限系统