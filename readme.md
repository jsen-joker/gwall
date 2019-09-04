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
    
    
# openapi
    网关下的开发api授权系统
    网关下有开放api的必须有开发api授权系统
    
# oauth2
    第三方登录支持
    
# gwall优点
    关注业务：集成了sso和api权限控制，在开发的时候，开发者只需要关注自己的业务，
    不必管理用户认证和权限认证
    多语言支持：微服务互调只要支持nacos客户端就可以实现微服务互调，典型的
    可以使用java + nodejs + golang进行混合开发，发挥各种语言的优势

    
# 数据库
    gwall 使用 mongodb 保存路由信息
    使用 redis 实现 sso
    使用 mysql 实现用户认证系统和api权限系统