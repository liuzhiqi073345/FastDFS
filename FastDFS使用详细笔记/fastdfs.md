####FastDFS简单使用

######CentOS7安装和使用

* 设置IP地址

  * `ip add` 或者 `dhclient`自动获取ip地址，可以`dhclient -r`停掉`dhclient`

  * `ip addr`查看ip地址

  * `vi /etc/sysconfig/network-scripts/ifcfg-ens33 `，修改网络连接信息

    ```shell
    # 修改或者添加其中的项
    BOOTPROUTE=static
    ONBOOT=yes

    IPADDR=ip
    NETMASK=255.255.255.0
    GATEWAY=ip.2
    DNS1=ip.1
    DNS2=8.8.8.8
    ```

######下载FastDFS:`FastDFS_v5.08.tar.gz`

* 地址`https://sourceforge.net/projects/fastdfs/files/`

######安装一个git在centos7系统上，因为需要下载别的插件如`libfastcommon`

* `yum install -y git`

######使用ssh的方式连接git

* 删除电脑生成过的ssh配置`rm -r ~/.ssh/`

* 重新生成一个新的SSH KEY`ssh-keygen -t rsa`，使用默认的生成路径和文件，所以一直点击  Enter  即可

* 设置本机的免登录设置`cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys`

* 获取得到ssh-key的内容并保存`cat ~/.ssh/id_rsa.pub`，因为需要在git上边的 SSH Key，如：

  ```shell
  ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCryAX5FLeCAEiwk7df/cLmsyVeFEd8h3qp4nmC7BBMjrbrvkioU584HmEwDRpcJ1idQF9cRbWzpDHGAsYOB8NHCC6XRKw7ikunG7kIjUTkxq0bhI0MR1BsTyiI/CPnPdpMcWT35hCVreUuNVQq86FqPzUoHZozofkwy8pyKD2DcCeheq0inP2vT8r6EqpkrshAhGzbKMvX1s7GUEwLs1OBqz2dhXXWQBwy3jwNgYfC6MK2ebbdSCobmSCjx/FlQyQFMt+7LOpvqVW+EcUHOE5B6jT5hJD4gn2hmdSwU3ZpsG5GI8nqmfW58Pl0tP7Vtwc9LTyk4sv18zQkxjJL97tb root@localhost.localdomain
  ```

######下载libfastcommon

* 进入`cd /usr/local/src`目录下
* 使用git克隆libfashcommon源码`git clone https://github.com/happyfish100/libfastcommon.git`

######编译libfastcommon

* `cd /usr/local/src/libfastcommon/`
  * `./make.sh`编译，如果出现gcc错误或者not found，需要安装gcc编译器
    * `yum install gcc-c++`
  * `./make.sh install`安装

######为了方便操作创建一些软链接

```shell
ln -svf /usr/include/fastcommon /usr/local/include/fastcommon
ln -svf /usr/include/fastdfs /usr/local/include/fastdfs
ln -svf /usr/lib/libfastcommon.so /usr/local/lib/libfastcommon.so
ln -svf /usr/lib/libfastclient.so /usr/local/lib/libfastclient.so
ln -svf /usr/lib64/libfdfsclient.so /usr/local/lib/libfdfsclient.so
ln -svf /usr/lib64/libfdfsclient.so /usr/lib/libfdfsclient.so
```
######对FastDFS的包进行编译工作（目录要是没有存在就需要创建目录）

* 创建上传文件的目录`mkdir /src/ftp`
* 进入目录`cd /src/ftp`
* 上传`FastDFS_v5.08.tar.gz`包：使用 `rz` 命令或者其他工具
* 解压文件`tar zxvf /src/ftp/FastDFS_v5.08.tar.gz -C /usr/local/src/FastDFS/`
* 进入`cd /usr/local/src/FastDFS/FastDFS`
* 编译`./make.sh`
* 安装`./make.sh install`
* 检查是否安装成功`cd /etc/fdfs`

######配置FastDFS(tracker-跟踪、storage-存储)，storage和tracker两个服务器使用不同的ip地址

* 编译tracker的配置文件，复制模板文件进行修改

  * `cp /etc/fdfs/tracker.conf.sample /etc/fdfs/tracker.conf`

  * 创建一个tracker的信息保存目录`mkdir -p /usr/data/fdfs/tracker`

  * 编辑配置文件

    * `vim /etc/fdfs/tracker.conf`

      ```shell
      base_path=/usr/data/fdfs/tracker
      ```

* 编辑storage配置文件

  * `cp /etc/fdfs/storage.conf.sample /etc/fdfs/storage.conf`

  * 创建一个storage信息保存目录`mkdir -p /usr/data/fdfs/storage`

  * 编辑配置文件

    * `vim /etc/fdfs/storage.conf`

      ```shell
      base_path=/usr/data/fdfs/storage
      store_path0=/usr/data/fdfs/storage
      tracker_server=ip:22122
      ```

######启动storage和tracker服务

* 启动tracker服务`/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf`
* 启动storage服务`/usr/bin/fdfs_storaged /etc/fdfs/storage.conf`

######单机版客户端测试(可能遇到防火墙的问题，需要关闭防火墙。storage服务器和tracker服务起的ip地址必须不一样)

* 配置客户端配置文件

  * `cp /etc/fdfs/client.conf.sample /etc/fdfs/client.conf`

  * `mkdir -p /usr/data/fdfs/client`

  * 编辑配置文件`vim /etc/fdfs/client.conf`

    ```shell
    base_path=/usr/data/fdfs/client
    tracker_server=ip:22122
    ```

* 通过客户端查看服务器当前的状态`/usr/bin/fdfs_monitor /etc/fdfs/client.conf`

* 测试过程中越到**no route host**表示tracker服务器的防火墙拦截了，所以需要在tracker服务器上关闭防火墙

  * `systemctl stop firewalld.service`

######上传下载文件进行测试

* 将模板图片kaka.jpg放到客户端服务器的/src/ftp目录下

* 上传图片`/usr/bin/fdfs_test /etc/fdfs/client.conf upload /src/ftp/kaka.jpg`

  ```shell
  # 上传成功之后的提示信息
  This is FastDFS client test program v5.08

  Copyright (C) 2008, Happy Fish / YuQing

  FastDFS may be copied only under the terms of the GNU General
  Public License V3, which may be found in the FastDFS source kit.
  Please visit the FastDFS Home Page http://www.csource.org/
  for more detail.

  [2017-12-19 02:02:14] DEBUG - base_path=/usr/data/fdfs/client, connect_timeout=30, network_timeout=60, tracker_server_count=1, anti_steal_token=0, anti_steal_secret_key length=0, use_connection_pool=0, g_connection_pool_max_idle_time=3600s, use_storage_id=0, storage server id count: 0

  tracker_query_storage_store_list_without_group:
          server 1. group_name=, ip_addr=192.168.17.130, port=23000

  group_name=group1, ip_addr=192.168.17.130, port=23000
  storage_upload_by_filename
  group_name=group1, remote_filename=M00/00/00/wKgRglo4uXaAU573AADwQmFmfec771.jpg
  source ip address: 192.168.17.130
  file timestamp=2017-12-19 02:02:14
  file size=61506
  file crc32=1634106855
  example file url: http://192.168.17.130/group1/M00/00/00/wKgRglo4uXaAU573AADwQmFmfec771.jpg
  storage_upload_slave_by_filename
  group_name=group1, remote_filename=M00/00/00/wKgRglo4uXaAU573AADwQmFmfec771_big.jpg
  source ip address: 192.168.17.130
  file timestamp=2017-12-19 02:02:14
  file size=61506
  file crc32=1634106855
  example file url: http://192.168.17.130/group1/M00/00/00/wKgRglo4uXaAU573AADwQmFmfec771_big.jpg
  ```

* 上传图片成功之后的保存目录为`/usr/data/fdfs/storage/data/00/00`

* 下载文件

  * 进入`cd /src/ftp`目录下
  * `/usr/bin/fdfs_download_file  /etc/fdfs/client.conf group1/M00/00/00/wKgRglo4uXaAU573AADwQmFmfec771_big.jpg`

* 删除文件`/usr/bin/fdfs_delete_file /etc/fdfs/client.conf  group1/M00/00/00/wKgRglo4uXaAU573AADwQmFmfec771_big.jpg`

######使用java代码实现文件的上传和下载

* 需要fastdfs的客户端包，在pom文件中添加

  ```xml
  <dependency>
    <groupId>com.github.tobato</groupId>
    <artifactId>fastdfs-client</artifactId>
    <version>1.25.3-RELEASE</version>
  </dependency>
  ```

* FastDFS_Client_SDK:`https://github.com/happyfish100/fastdfs-client-java/`，要么使用maven引入，要么把源码包放到代码中去

* 可以配置一些自己需要的参数，具体参考`https://github.com/happyfish100/fastdfs-client-java/`

* 上传文件

  * 穿件一个配置文件在src目录下，配置跟踪服务器的ip地址和端口

    ```xml
    tracker_server=192.168.17.129:22122
    ```

  * 上传文件的代码

    ```java
    // 1、设置一个上传文件的路径；
    File imgFile = new File("C:" + File.separator + "kaka.jpg") ;
    // 取得文件扩展名称
    String fileExtName = imgFile.getName().substring(imgFile.getName().lastIndexOf(".") + 1) ;
    // 2、读取上传的配置文件，此配置文件在CLASSPATH路径下
    ClassPathResource res = new ClassPathResource("fdfs_client.conf") ;
    // 3、初始化FastDFS上传的环境
    ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
    // 4、建立Tracker的客户端连接
    TrackerClient tracker = new TrackerClient() ;
    TrackerServer trackerServer = tracker.getConnection() ;    // 取得服务器连接
    // 5、真正负责数据保存的是Storage
    StorageServer storageServer = null ;
    // 6、进行StoragerClient的创建
    StorageClient client = new StorageClient(trackerServer,storageServer) ;
    // 7、创建文件的元数据对象
    NameValuePair[] metaList = new NameValuePair[3] ;
    metaList[0] = new NameValuePair("fileName",imgFile.getName()) ;
    metaList[1] = new NameValuePair("fileExtName",fileExtName) ;
    metaList[2] = new NameValuePair("fileLength",String.valueOf(imgFile.length())) ;
    // 8、实现文件上传，返回文件的路径名称
    String [] fileId = client.upload_file(imgFile.getPath(), fileExtName, metaList) ;
    System.out.println(Arrays.toString(fileId));
    trackerServer.close();
    ```

* 查看文件信息

  ```java
  // 1、读取上传的配置文件，此配置文件在src路径下
  ClassPathResource res = new ClassPathResource("fdfs_client.conf");
  // 2、初始化FastDFS上传的环境
  ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
  // 3、建立Tracker的客户端连接
  TrackerClient tracker = new TrackerClient();
  // 取得服务器连接
  TrackerServer trackerServer = tracker.getConnection(); 
  // 4、真正负责数据保存的是Storage
  StorageServer storageServer = null;
  // 5、进行StoragerClient的创建
  StorageClient1 client = new StorageClient1(trackerServer, storageServer);
  // 6、取得文件的相关信息
  String fileName = "group1/M00/00/00/wKgRglo4yViAHLKfAADwQmFmfec047.jpg";
  // 取得该文件内容的信息
  FileInfo fi = client.get_file_info1(fileName); 
  System.out.println("得到文件大小：" + fi.getFileSize());
  System.out.println("创建日期：" + fi.getCreateTimestamp());
  System.out.println("真实保存主机：" + fi.getSourceIpAddr());
  System.out.println("CRC32：" + fi.getCrc32());
  trackerServer.close();
  ```

* 删除文件

  ```java
  // 1、读取上传的配置文件，此配置文件在src路径下
  ClassPathResource res = new ClassPathResource("fdfs_client.conf");
  // 2、初始化FastDFS上传的环境
  ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
  // 3、建立Tracker的客户端连接
  TrackerClient tracker = new TrackerClient();
  TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
  // 4、真正负责数据保存的是Storage
  StorageServer storageServer = null;
  // 5、进行StoragerClient的创建
  StorageClient1 client = new StorageClient1(trackerServer, storageServer);
  // 6、取得文件的相关信息
  String fileName = "group1/M00/00/00/wKgRglo4yViAHLKfAADwQmFmfec047.jpg";
  // 7、删除文件，返回值为0则删除成功
  System.out.println(client.delete_file1(fileName));
  trackerServer.close();
  ```

* 下载文件

  ```java
  // 1、读取上传的配置文件，此配置文件在src路径下
  ClassPathResource res = new ClassPathResource("fdfs_client.conf");
  // 2、初始化FastDFS上传的环境
  ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
  // 3、建立Tracker的客户端连接
  TrackerClient tracker = new TrackerClient();
  TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
  // 4、真正负责数据保存的是Storage
  StorageServer storageServer = null;
  // 5、进行StoragerClient的创建
  StorageClient1 client = new StorageClient1(trackerServer, storageServer);
  // 6、取得文件的字节
  byte[] b = client.download_file("group1", "M00/00/00/wKgRglo4vYmAInx5AADwQmFmfec182.jpg");
  // 7、保存文件
  IOUtils.write(b, new FileOutputStream("D:/" + UUID.randomUUID().toString() + ".jpg"));
  System.out.println("下载文件成功：" + b);
  ```



#### FastDFS集群使用

| 主机名称      | ip地址           | 分组     |
| --------- | -------------- | ------ |
| tracker01 | 192.168.17.131 |        |
| tracker02 | 192.168.17.132 |        |
| storage11 | 192.168.17.133 | group1 |
| storage12 | 192.168.17.134 | group1 |
| storage21 | 192.168.17.135 | group2 |

###### 配置好一台tracker服务器（其他的进行拷贝和修改）

* 在单机版本环境的前提下进行配置（即已经安装了fastdfs、libfastcommon等插件和工具）

* 上传相关开发包到/src/ftp目录下，开发包为`nginx-1.11.3.tar.gz`、`echo-nginx-module-0.59.tar.gz`、`ngx_cache_purge-2.3.tar.gz`、`nginx-upstream-fair-a18b409.tar.gz`、`pcre-8.36.tar.gz`、`zlib-1.2.8.tar.gz`

* 解压所有的开发包

  ```shell
  tar zxvf /src/ftp/echo-nginx-module-0.59.tar.gz -C /usr/local/src/
  tar zxvf /src/ftp/nginx-1.11.3.tar.gz -C /usr/local/src/
  tar zxvf /src/ftp/nginx-upstream-fair-a18b409.tar.gz -C /usr/local/src/
  tar zxvf /src/ftp/ngx_cache_purge-2.3.tar.gz -C /usr/local/src/
  tar zxvf /src/ftp/pcre-8.36.tar.gz -C /usr/local/src/
  tar xvf /src/ftp/zlib-1.2.8.tar.gz -C /usr/local/src/
  ```

* 进行nginx的编译处理

  * 建立信息目录`mkdir -p /usr/local/nginx/{logs,conf,fastcgi_temp,sbin,client_body_temp,proxy_temp,uwsgi_temp,scgi_temp}`

  * 进入nginx源码目录`cd /usr/local/src/nginx-1.11.3/`

  * 在ngnix源码目录执行以下命令进行配置

    ```shell
    ./configure --prefix=/usr/local/nginx/ \
    --sbin-path=/usr/local/nginx/sbin/ \
    --with-http_ssl_module \
    --conf-path=/usr/local/nginx/conf/nginx.conf \
    --pid-path=/usr/local/nginx/logs/nginx.pid \
    --error-log-path=/usr/local/nginx/logs/error.log \
    --http-log-path=/usr/local/nginx/logs/access.log \
    --http-fastcgi-temp-path=/usr/local/nginx/fastcgi_temp \
    --http-client-body-temp-path=/usr/local/nginx/client_body_temp \
    --http-proxy-temp-path=/usr/local/nginx/proxy_temp \
    --http-uwsgi-temp-path=/usr/local/nginx/uwsgi_temp \
    --http-scgi-temp-path=/usr/local/nginx/scgi_temp \
    --add-module=/usr/local/src/echo-nginx-module-0.59 \
    --add-module=/usr/local/src/gnosek-nginx-upstream-fair-a18b409 \
    --add-module=/usr/local/src/ngx_cache_purge-2.3 \
    --with-zlib=/usr/local/src/zlib-1.2.8 \
    --with-pcre=/usr/local/src/pcre-8.36
    ```

    * 以上命令的执行结果可能出现not found SSL，安装`yum install openssl-devel`，然后在nginx源码目录重新执行上一步的命令

  * 编译安装nginx `make;make install`

* 配置tracker配置文件

  * 建立数据目录`mkdir -p /usr/data/fdfs/tracker`

  * 拷贝配置文件`cp /etc/fdfs/tracker.conf.sample /etc/fdfs/tracker.conf`

  * 编辑配置文件`vim /etc/fdfs/tracker.conf`

    ```shell
    base_path=/usr/data/fdfs/tracker
    store_lookup=0 #store_lookup，该值默认是2（即负载均衡策略），现在把它修改为0（即轮询策略，修改成这样方便一会儿我们进行测试，当然，最终还是要改回到2的。如果值为1的话表明要始终向某个group进行上传下载操作，这时下图中的"store_group=group2"才会起作用，如果值是0或2，则"store_group=group2"不起作用）
    ```

* 配置nginx

  * 为tracker主机配置缓存，创建缓存目录`mkdir -p /usr/data/nginx/{cache,tmp}`

  * 修改nginx的配置文件`vim /usr/local/nginx/conf/nginx.conf`

    ```shell
    worker_processes  1;

    events {
        worker_connections  65536;
        use epoll;
    }

    http {
        include       mime.types;
        default_type  application/octet-stream;
        server_names_hash_bucket_size 128;
        client_header_buffer_size 32k;
        large_client_header_buffers 4 32k;
        client_max_body_size 300m;
        proxy_redirect off;
        proxy_set_header Host $http_host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_connect_timeout 90;
        proxy_send_timeout 90;
        proxy_read_timeout 90;
        proxy_buffer_size 16k;
        proxy_buffers 4 64k;
        proxy_busy_buffers_size 128k;
        proxy_temp_file_write_size 128k;
        proxy_cache_path /usr/data/nginx/cache levels=1:2 keys_zone=http-cache:500m max_size=10g inactive=30d;
        proxy_temp_path /usr/data/nginx/tmp;
      #需要注意的地方重点1
        upstream fdfs_group1 {
            server 192.168.17.133:9999 weight=1 max_fails=2 fail_timeout=30s;
            server 192.168.17.134:9999 weight=1 max_fails=2 fail_timeout=30s;
        }
        upstream fdfs_group2 {
            server 192.168.17.135:9999 weight=1 max_fails=2 fail_timeout=30s;
        }

        sendfile        on;

        keepalive_timeout  65;
        
        server {
            listen       80;
            server_name  localhost;

            location /group1/M00 {
                proxy_next_upstream http_502 http_504 error timeout invalid_header;
    proxy_cache http-cache;
                proxy_cache_valid  200 304 12h;
                proxy_cache_key $uri$is_args$args;
                proxy_pass http://fdfs_group1;
                expires 30d;
            }
            location /group2/M00 {
                proxy_next_upstream http_502 http_504 error timeout invalid_header;
    proxy_cache http-cache;
                proxy_cache_valid  200 304 12h;
                proxy_cache_key $uri$is_args$args;
                proxy_pass http://fdfs_group2;
                expires 30d;
            }
            
            location ~ /purge(/.*) {
                allow 127.0.0.1;
                allow 192.168.17.0/24; #需要注意的地方重点2
                deny all;
                proxy_cache_purge http-cache  $1$is_args$args;
            }

            location / {
                root   html;
                index  index.html index.htm;
            }

            error_page   500 502 503 504  /50x.html;
            location = /50x.html {
                root   html;
            }

        }

    }
    ```

###### 配置好一台storage服务器（其他的进行拷贝和修改）

- 在单机版本环境的前提下进行配置（即已经安装了fastdfs、libfastcommon等插件和工具）

- 上传相关开发包到/src/ftp目录下，开发包为`nginx-1.11.3.tar.gz`、`echo-nginx-module-0.59.tar.gz`、`ngx_cache_purge-2.3.tar.gz`、`nginx-upstream-fair-a18b409.tar.gz`、`pcre-8.36.tar.gz`、`fastdfs-nginx-module_v1.16.tar.gz`、`zlib-1.2.8.tar.gz`

- 解压开发包

  ```shell
  tar zxvf /src/ftp/echo-nginx-module-0.59.tar.gz -C /usr/local/src
  tar zxvf /src/ftp/fastdfs-nginx-module_v1.16.tar.gz -C /usr/local/src
  tar zxvf /src/ftp/nginx-1.11.3.tar.gz -C /usr/local/src
  tar zxvf /src/ftp/nginx-upstream-fair-a18b409.tar.gz -C /usr/local/src
  tar zxvf /src/ftp/ngx_cache_purge-2.3.tar.gz -C /usr/local/src
  tar zxvf /src/ftp/pcre-8.36.tar.gz -C /usr/local/src
  tar xvf /src/ftp/zlib-1.2.8.tar.gz -C /usr/local/src
  ```

- 进行nginx的编译处理

  - 创建好nginx编译之后的保存目录`mkdir -p /usr/local/nginx/{logs,conf,fastcgi_temp,sbin,client_body_temp,proxy_temp,uwsgi_temp,scgi_temp}`

  - 进入nginx源码目录`cd /usr/local/src/nginx-1.11.3/`

  - 在源码目录下执行以下命令进行处理

    ```shell
    ./configure --prefix=/usr/local/nginx/ \
    --sbin-path=/usr/local/nginx/sbin/ \
    --with-http_ssl_module \
    --conf-path=/usr/local/nginx/conf/nginx.conf \
    --pid-path=/usr/local/nginx/logs/nginx.pid \
    --error-log-path=/usr/local/nginx/logs/error.log \
    --http-log-path=/usr/local/nginx/logs/access.log \
    --http-fastcgi-temp-path=/usr/local/nginx/fastcgi_temp \
    --http-client-body-temp-path=/usr/local/nginx/client_body_temp \
    --http-proxy-temp-path=/usr/local/nginx/proxy_temp \
    --http-uwsgi-temp-path=/usr/local/nginx/uwsgi_temp \
    --http-scgi-temp-path=/usr/local/nginx/scgi_temp \
    --add-module=/usr/local/src/echo-nginx-module-0.59 \
    --add-module=/usr/local/src/gnosek-nginx-upstream-fair-a18b409 \
    --add-module=/usr/local/src/ngx_cache_purge-2.3 \
    --add-module=/usr/local/src/fastdfs-nginx-module/src \
    --with-zlib=/usr/local/src/zlib-1.2.8 \
    --with-pcre=/usr/local/src/pcre-8.36
    ```

  - 编译安装`make;make install`

- 配置storage配置文件

  - 拷贝storage配置文件`cp /etc/fdfs/storage.conf.sample /etc/fdfs/storage.conf`

  - 创建工作目录`mkdir -p /usr/data/fdfs/storage`

  - 编辑配置文件`vim /etc/fdfs/storage.conf`

    ```shell
    group_name=group1
    store_path0=/usr/data/fdfs/storage
    base_path=/usr/data/fdfs/storage
    tracker_server=192.168.17.131:22122
    tracker_server=192.168.17.132:22122
    ```

- 将fastdfs-nginx-module模块中的配置文件拷贝到/etc/fdfs目录中`cp /usr/local/src/fastdfs-nginx-module/src/mod_fastdfs.conf /etc/fdfs/`

- 修改mod_fastdfs.conf文件`vim /etc/fdfs/mod_fastdfs.conf`

  ```shell
  base_path=/usr/data/fdfs/storage
  tracker_server=192.168.17.131:22122
  tracker_server=192.168.17.132:22122
  group_name=group1
  store_path0=/usr/data/fdfs/storage
  url_have_group_name=true

  #一下的部分可以不用修改或者添加
  group_count = 2
  [group1]
  group_name=group1
  storage_server_port=23000
  store_path_count=1
  store_path0=/usr/data/fdfs/storage
  [group2]
  group_name=group2
  storage_server_port=23000
  store_path_count=1
  store_path0=/usr/data/fdfs/storage
  ```

- 拷贝配置文件

  - http.conf配置文件 `cp /usr/local/src/FastDFS/FastDFS/conf/http.conf /etc/fdfs/`
  - mime.types配置文件:`cp /usr/local/src/FastDFS/FastDFS/conf/mime.types /etc/fdfs/`

- 修改nginx的配置项`vim /usr/local/nginx/conf/nginx.conf`

  - 监听端口设置成9999：

    `server {  listen       9999;    server_name  localhost;`

  - 添加组的关系

    ```shell
    location ~/group[1-3]/M00{
    root /usr/data/fdfs/storage;
    ngx_fastdfs_module;
    }
    ```

- 做一个软连接`ln -s /usr/data/fdfs/storage/data/ /usr/data/fdfs/storage/M00`

- 检测nginx配置是否正确`/usr/local/nginx/sbin/nginx -t`

###### 集群环境测试

* 启动tracker进程`/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf`

* 启动storage进程`/usr/bin/fdfs_storaged /etc/fdfs/storage.conf`

* 启动storage的nginx服务`/usr/local/nginx/sbin/nginx`

* 启动tracker的nginx服务`/usr/local/nginx/sbin/nginx`

* 修改client.conf文件，主要是设置tracker的地址：`vim /etc/fdfs/client.conf`

  * 如果没有client.conf文件就先拷贝一份`cp /etc/fdfs/client.conf.sample /etc/fdfs/client.conf`

    ```shell
    tracker_server=192.168.17.131:22122
    tracker_server=192.168.17.132:22122
    base_path=/usr/data/fdfs/client
    ```

* 检测当前服务状态

  * 如果没有client保存的目录创建一个即可`mkdir -p /usr/data/fdfs/client`
  * `/usr/bin/fdfs_monitor /etc/fdfs/client.conf`
  * 在执行检测的过程中可能出现no route to host,这是由于防火墙阻止了连接，执行命令(在tracker-server机器上执行)`systemctl stop firewalld.service` 

* 上传图片`/usr/bin/fdfs_test /etc/fdfs/client.conf upload /src/ftp/kaka.jpg`

* 浏览器访问图片

  * 直接访问storage服务器`http://192.168.17.133:9999/group1/M00/00/08/wKgRiFo8mO-AaIg6AADwQmFmfec048_big.jpg`
    * 直接访问storage服务器的时候要加上nginx监听的端口9999
  * 访问任意一个tracker服务器`http://192.168.17.131/group1/M00/00/08/wKgRiFo8mO-AaIg6AADwQmFmfec048_big.jpg`
    * 直接访问tracker服务器的时候默认80端口，nginx的nginx.conf中配置了location，其中会映射到storage服务器

#### 防盗链处理

###### 问题说明

* 文件服务器打开之后，说明别人就能够访问你的服务器文件了，这样下去就会出现一个问题，别人的服务器能够直接引用你的图片什么的，同时造成了你的服务其的容量骤增，这种想象必须禁止，所以就叫做盗链防范。

###### 处理方法

* 你生成一个随机的token，这个token在某一段时间内有效，超过指定的时间出现就是错误页面。防盗链的功能可以修改**storage主机的配置**实现功能。

###### 具体操作

* 上传防盗链之后显示的404图片到/src/ftp，将图片移动到`mv /src/ftp/404.jpg /etc/fdfs/`

* 将此图片拷贝到所有的storage服务器上去`scp /etc/fdfs/404.jpg 192.168.17.135:/etc/fdfs`

* 修改所有storage服务器的`vim /etc/fdfs/http.conf`

  ```shell
  http.anti_steal.check_token=true #是否检测token，是否开启防盗链
  http.anti_steal.token_ttl=900 #设置每一个token有效的时间（秒）
  http.anti_steal.secret_key=FastDFS1234567890 #设置生成token密码
  http.anti_steal.token_check_fail=/etc/fdfs/404.jpg #设置token失效之后显示的404图片
  ```

* 将配置文件拷贝给别的storage服务器`scp /etc/fdfs/http.conf 192.168.17.134:/etc/fdfs/`

* 重启所有的storage服务`/usr/bin/fdfs_storaged /etc/fdfs/storage.conf restart`

* 重启所有的storage的nginx服务`/usr/local/nginx/sbin/nginx -s reload`

* 对404图片的权限进行修改`chmod 777 -R /etc/fdfs/404.jpg`


* 通过java程序生成token，必须保证服务器的时间和客户端电脑的时间是相对应的

  * centos时间同步网络时间

    ```shell
    yum install -y ntpdate
    ntpdate cn.pool.ntp.org
    hwclock --systohc
    ```

  * 同步时间的时候可能差一个小时

    ```shell
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
    ntpdate cn.pool.ntp.org
    hwclock --systohc
    ```

* 客户端java编写

  * 修改src下的 fdfs_client.conf配置文件

    ```shell
    tracker_server = 192.168.17.131:22122
    tracker_server = 192.168.17.132:22122
    http.anti_steal_token = true
    http.secret_key=FastDFS1234567890
    ```

* 编写程序生成token

  ```java
  // 1、读取上传的配置文件，此配置文件在src路径下
  ClassPathResource res = new ClassPathResource("fdfs_client.conf");
  // 2、初始化FastDFS上传的环境
  ClientGlobal.init(res.getClassLoader().getResource("fdfs_client.conf").getPath());
  // 3、建立Tracker的客户端连接
  TrackerClient tracker = new TrackerClient();
  TrackerServer trackerServer = tracker.getConnection(); // 取得服务器连接
  // 定义文件id的时候千万不要加上组名，否则无法访问
  String fileId = "M00/00/08/wKgRiFo8mO-AaIg6AADwQmFmfec048_big.jpg";
  int ts = (int) (System.currentTimeMillis() / 1000); // 取得当前的一个时间标志
  String token = ProtoCommon.getToken(fileId, ts, ClientGlobal.g_secret_key);
  StringBuffer fileUrl = new StringBuffer();
  fileUrl.append("http://");
  fileUrl.append(trackerServer.getInetSocketAddress().getHostString());
  fileUrl.append("/group2/").append(fileId);
  fileUrl.append("?token=").append(token).append("&ts=").append(ts);
  System.out.println(fileUrl);
  ```

  ​

####部分问题

###### 关闭默认的防火墙

* centos7 默认使用的是firewall作为防火墙，关闭`systemctl stop firewalld.service`
* 启用iptables防火墙
  * 安装或者更新iptables服务`yum install iptables-services`
  * 启动iptables `systemctl enable iptables`
  * 打开iptables `systemctl start iptables`

######重启storage和tracker服务

* 启动tracker服务`/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf`
* 启动storage服务`/usr/bin/fdfs_storaged /etc/fdfs/storage.conf`

######配置iptables防火墙策略（这个的话了解一下iptables防火墙再配置吧，先不配置）

* 编辑配置文件`vim /etc/sysconfig/iptables`
* storage服务器，配置文件后边追加`-A INPUT -m state --state NEW -m tcp -p tcp --dport 23000 -j ACCEPT`
* tracker服务器，配置文件后边追加`-A INPUT -m state --state NEW -m tcp -p tcp --dport 23000 -j ACCEPT`
* 重启防火墙`service ipatbles restart`

######新加入的tracker可能出现无法和老tracker进行连接和选举，出现错误

* 原因：新加入的tracker在集群同步时监测storage同步情况存在差异
* 解决办法：
  * 1.升级FastDFS的版本至5.10及以上
  * 2.通过以下操作
    * 先重启所有的storage服务
    * 将之前的tracker   leader服务暂停掉
    * 清除新加入的tracker的/tracker/data/下边的所有*.bat文件
    * 重新启动新加入的tracker服务
    * 直到新加入的tracker成为leader之后，启动老tracker服务
      * 等待1-2分钟，自动选举leader，然后查看日志文件logs
      * 查看所有的tracker和storage服务的日志文件logs，如果出现错误，重启服务可能就好了，根据实际情况进行处理