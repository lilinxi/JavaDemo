**Mac 中解决中文乱码问题**

在 /etc 新建 my.cnf 文件：sudo vim my.cnf

添加：
[client]
default-character-set=utf8
[mysqld]
character-set-server=utf8

退出：
Esc+qw+ZZ

重启 MySQL

```

```