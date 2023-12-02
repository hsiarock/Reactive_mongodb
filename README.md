### Run it
    http://localhost:8080/house
    http://localhost:8080/house-paged?page=1&size=12

# Spring Boot House Web

## 03/20/2023 -
1. spring boot 2.5  JUNIT5 is the default package
2. remove @RunWith(....)
3. Change to use @Before  to @BeforeEach (use @BeforeAll need static...haven't tried it)
4. Dockerfile --> line 4. both ./mvnw and ./mvnw.cmd have error (/bin/sh not found, or @REM not valid)
   So, need to use bash:

   docker build -t <image-name> .   # use image name: "spring-boot-reactive-web-tpd"

   ** still error for "/bin/sh^M" not found ....so, you need to change Dockerfile to 'LF' end of line
   ** click the right-down corner 'CRLF' to change to 'LF' only
   ** THen, it works!
   $ docker build -t <image-name> .

   [+] Building 43.4s (11/11) FINISHED
   => [internal] load build definition from Dockerfile      0.0s
   => => transferring dockerfile: 399B    0.0s
   => [internal] load .dockerignore      0.0s
   => => transferring context: 2B            0.0s
   => [internal] load metadata for docker.io/library/openjdk:15     0.3s
   => [internal] load build context     0.0s
   => => transferring context: 9.84kB   0.0s
   => CACHED [builder 1/4] FROM docker.io/library/openjdk:15@sha256:f6ce9c7e6ce39002d55a4dba83c5a65139112983e5707825e  0.0s
   => [builder 2/4] COPY . /usr/src/reactive-backend    0.0s
   => [builder 3/4] WORKDIR /usr/src/reactive-backend  0.0s
   => [builder 4/4] RUN ./mvnw clean package       42.7s
   => [stage-1 2/3] COPY --from=builder /usr/src/reactive-backend/target/reactive-web-1.0.0-SNAPSHOT.jar /usr/src/rea  0.1s
   => [stage-1 3/3] WORKDIR /usr/src/reactive-backend    0.0s
   => exporting to image      0.1s
   => => exporting layers     0.1s
   => => writing image sha256:469e18180587c2c83af1187be46b23e2e643f411c73c84a700877e3e230f0827    0.0s

   Use 'docker scan' to run Snyk tests against images to find vulnerabilities and learn how to fix them

5. if didn't provide -t <tag-name>, default is 'latest/img-name'. But you cab use tag command to change it

   docker tag <new tag-name> <old tag-name>  hsiarock/spring-boot-reactive-web-tpd

6. to push it to docker repository

   $ docker push hsiarock/spring-boot-reactive-web-tpd
   Using default tag: latest
   The push refers to repository [docker.io/hsiarock/spring-boot-reactive-web-tpd]
   An image does not exist locally with the tag: hsiarock/spring-boot-reactive-web-tpd

   So you need to tag this image. first

   $ docker login -u hsiarock
   Password:
   Login Succeeded

   # give a new image name using tag
   $ docker tag spring-boot-reactive-web-tpd hsiarock/spring-boot-reactive-web-tpd

   # now you can push it again

   $ docker push hsiarock/spring-boot-reactive-web-tpd
   Using default tag: latest
   The push refers to repository [docker.io/hsiarock/spring-boot-reactive-web-tpd]
   5f70bf18a086: Pushed
   e23eb752d894: Pushed
   7eb1709a0468: Mounted from library/openjdk
   7a5aea3b0543: Mounted from library/openjdk
   acf86001822d: Mounted from library/openjdk
   latest: digest: sha256:65d2dbcbd93938ca06eaeff7f8111352e6ceb7fddaf0133002acfd75faf3730a size: 1372

7. now from browser, go to https://hub.docker.com, you should see this new images been pushed to repository

8. run this image using local image - house

   $ docker run -dp 8080:8080 house

   # Dockerfile has EXPOSE, it is an announce, not configure. need to use docker run -p port:map-port

   # to run both mongo + springboot-reactive-web use docker-compose.
   I create a new docker-compose-mongo-spring-reactive.yml file
   THe network name must be specified

   THen, build these two images
```
   $ docker-compose -f docker/docker-compose-mongo-spring-reactive.yml build
   [+] Building 46.9s (11/12)
   => [internal] load build definition from Dockerfile  0.0s
   => => transferring dockerfile: 399B        0.0s
   => [internal] load .dockerignore           0.0s
   => => transferring context: 2B             0.0s
   => [internal] load metadata for docker.io/library/openjdk:15        46.9s
   => [auth] library/openjdk:pull token for registry-1.docker.io       0.0s
   => [internal] load build context                                    0.0s
   => => transferring context: 30.90kB        0.0s
   => CACHED [builder 1/4] FROM docker.io/library/openjdk:15@sha256:f6ce9c7e6ce39002d55a4dba83c5a65139112983e5707825e  0.0s
   => [builder 2/4] COPY . /usr/src/reactive-backend                    0.0s
   => [builder 3/4] WORKDIR /usr/src/reactive-backend                   0.0s
   => [builder 4/4] RUN ./mvnw clean package                            45.2s
   => [stage-1 2/3] COPY --from=builder /usr/src/reactive-backend/target/reactive-web-1.0.0-SNAPSHOT.jar /usr/src/rea  0.0s
   => [stage-1 3/3] WORKDIR /usr/src/reactive-backend                   0.0s
   => exporting to image                                                0.1s
   => => exporting layers                                               0.1s
   => => writing image sha256:2922f64e3d709f5b7d0211a0e06cc38c00d9b8e8a2fabdea9c2a135fe4327db3   0.0s
   => => naming to docker.io/library/spring-boot-reactive-web-tpd
```
Run docker-compose
```
    $ docker-compose -f docker/docker-compose-mongo-spring-reactive.yml up

[+] Running 3/3
- Network docker_network-reactive          Created                                                                  0.0s
- Container docker-spring-boot-reactive-1  Created                                                                  0.0s
- Container docker-mongo-1                 Recreated                                                                0.1s
  Attaching to docker-mongo-1, docker-spring-boot-reactive-1
  docker-mongo-1                 | 2023-03-22T02:03:39.148+0000 I CONTROL  [initandlisten] MongoDB starting : pid=1 port=27017 dbpath=/data/db 64-bit host=mongo
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] db version v3.4.24
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] git version: 865b4f6a96d0f5425e39a18337105f33e8db504d
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] OpenSSL version: OpenSSL 1.0.2g  1 Mar 2016
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] allocator: tcmalloc
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] modules: none
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] build environment:
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten]     distmod: ubuntu1604
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten]     distarch: x86_64
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten]     target_arch: x86_64
  docker-mongo-1                 | 2023-03-22T02:03:39.149+0000 I CONTROL  [initandlisten] options: {}
  docker-mongo-1                 | 2023-03-22T02:03:39.153+0000 I -        [initandlisten] Detected data files in /data/db created by the 'wiredTiger' storage engine, so setting the active storage engine to 'wiredTiger'.
  docker-mongo-1                 | 2023-03-22T02:03:39.153+0000 I STORAGE  [initandlisten]
  docker-mongo-1                 | 2023-03-22T02:03:39.153+0000 I STORAGE  [initandlisten] ** WARNING: Using the XFS filesystem is strongly recommended with the WiredTiger storage engine
  docker-mongo-1                 | 2023-03-22T02:03:39.153+0000 I STORAGE  [initandlisten] **          See http://dochub.mongodb.org/core/prodnotes-filesystem
  docker-mongo-1                 | 2023-03-22T02:03:39.154+0000 I STORAGE  [initandlisten] wiredtiger_open config: create,cache_size=7413M,session_max=20000,eviction=(threads_min=4,threads_max=4),config_base=false,statistics=(fast),log=(enabled=true,archive=true,path=journal,compressor=snappy),file_manager=(close_idle_time=100000),checkpoint=(wait=60,log_size=2GB),statistics_log=(wait=0),verbose=(recovery_progress),
  docker-mongo-1                 | 2023-03-22T02:03:39.673+0000 I STORAGE  [initandlisten] WiredTiger message [1679450619:673975][1:0x7f067014ad40], txn-recover: Main recovery loop: starting at 2/5504
  docker-mongo-1                 | 2023-03-22T02:03:39.738+0000 I STORAGE  [initandlisten] WiredTiger message [1679450619:738205][1:0x7f067014ad40], txn-recover: Recovering log 2 through 3
  docker-spring-boot-reactive-1  |
  docker-spring-boot-reactive-1  |   .   ____          _            __ _ _
  docker-spring-boot-reactive-1  |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
  docker-spring-boot-reactive-1  | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
  docker-spring-boot-reactive-1  |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  docker-spring-boot-reactive-1  |   '  |____| .__|_| |_|_| |_\__, | / / / /
  docker-spring-boot-reactive-1  |  =========|_|==============|___/=/_/_/_/
  docker-spring-boot-reactive-1  |  :: Spring Boot ::                (v2.7.9)
  docker-spring-boot-reactive-1  |
  docker-mongo-1                 | 2023-03-22T02:03:39.795+0000 I STORAGE  [initandlisten] WiredTiger message [1679450619:795773][1:0x7f067014ad40], txn-recover: Recovering log 3 through 3
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:39.813  INFO 1 --- [           main] c.t.reactiveweb.ReactiveWebApplication   : Starting ReactiveWebApplication v1.0.0-SNAPSHOT using Java 15.0.2 on 59b3644c1d41 with PID 1 (/usr/src/reactive-backend/reactive-web-1.0.0-SNAPSHOT.jar started by root in /usr/src/reactive-backend)
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:39.815  INFO 1 --- [           main] c.t.reactiveweb.ReactiveWebApplication   : No active profile set, falling back to 1 default profile: "default"
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten]
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten] ** WARNING: Access control is not enabled for the database.
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten] **          Read and write access to data and configuration is unrestricted.
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten]
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten]
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten] ** WARNING: /sys/kernel/mm/transparent_hugepage/enabled is 'always'.
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten] **        We suggest setting it to 'never'
  docker-mongo-1                 | 2023-03-22T02:03:39.851+0000 I CONTROL  [initandlisten]
  docker-mongo-1                 | 2023-03-22T02:03:39.857+0000 I FTDC     [initandlisten] Initializing full-time diagnostic data capture with directory '/data/db/diagnostic.data'
  docker-mongo-1                 | 2023-03-22T02:03:39.858+0000 I NETWORK  [thread1] waiting for connections on port 27017  
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:40.370  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Reactive MongoDB repositories in DEFAULT mode.
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:40.477  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 104 ms. Found 1 Reactive MongoDB repository interfaces.
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:40.481  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data MongoDB repositories in DEFAULT mode.
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:40.485  INFO 1 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 3 ms. Found 1 MongoDB repository interfaces.
  docker-mongo-1                 | 2023-03-22T02:03:41.074+0000 I NETWORK  [thread1] connection accepted from 172.19.0.2:34986 #1 (1 connection now open)
  docker-mongo-1                 | 2023-03-22T02:03:41.075+0000 I NETWORK  [thread1] connection accepted from 172.19.0.2:34988 #2 (2 connections now open)
  docker-mongo-1                 | 2023-03-22T02:03:41.075+0000 I NETWORK  [thread1] connection accepted from 172.19.0.2:35018 #3 (3 connections now open)
  docker-mongo-1                 | 2023-03-22T02:03:41.075+0000 I NETWORK  [thread1] connection accepted from 172.19.0.2:35034 #4 (4 connections now open)
  docker-mongo-1                 | 2023-03-22T02:03:41.100+0000 I NETWORK  [conn4] received client metadata from 172.19.0.2:35034 conn4: { driver: { name: "mongo-java-driver|reactive-streams|spring-boot", version: "4.6.1" }, os: { type: "Linux", name: "Linux", architecture: "amd64", version: "5.15.90.1-microsoft-standard-WSL2" }, platform: "Java/Oracle Corporation/15.0.2+7-27" }
  docker-mongo-1                 | 2023-03-22T02:03:41.100+0000 I NETWORK  [conn2] received client metadata from 172.19.0.2:34988 conn2: { driver: { name: "mongo-java-driver|reactive-streams|spring-boot", version: "4.6.1" }, os: { type: "Linux", name: "Linux", architecture: "amd64", version: "5.15.90.1-microsoft-standard-WSL2" }, platform: "Java/Oracle Corporation/15.0.2+7-27" }
  docker-mongo-1                 | 2023-03-22T02:03:41.100+0000 I NETWORK  [conn3] received client metadata from 172.19.0.2:35018 conn3: { driver: { name: "mongo-java-driver|sync|spring-boot", version: "4.6.1" }, os: { type: "Linux", name: "Linux", architecture: "amd64", version: "5.15.90.1-microsoft-standard-WSL2" }, platform: "Java/Oracle Corporation/15.0.2+7-27" }
  docker-mongo-1                 | 2023-03-22T02:03:41.100+0000 I NETWORK  [conn1] received client metadata from 172.19.0.2:34986 conn1: { driver: { name: "mongo-java-driver|sync|spring-boot", version: "4.6.1" }, os: { type: "Linux", name: "Linux", architecture: "amd64", version: "5.15.90.1-microsoft-standard-WSL2" }, platform: "Java/Oracle Corporation/15.0.2+7-27" }
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:41.643  INFO 1 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:41.903  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
  docker-spring-boot-reactive-1  | 2023-03-22 02:03:41.918  INFO 1 --- [           main] c.t.reactiveweb.ReactiveWebApplication   : Started ReactiveWebApplication in 2.577 seconds (JVM running for 2.942)
  docker-mongo-1                 | 2023-03-22T02:03:41.964+0000 I NETWORK  [thread1] connection accepted from 172.19.0.2:35048 #5 (5 connections now open)
  docker-mongo-1                 | 2023-03-22T02:03:41.972+0000 I NETWORK  [conn5] received client metadata from 172.19.0.2:35048 conn5: { driver: { name: "mongo-java-driver|reactive-streams|spring-boot", version: "4.6.1" }, os: { type: "Linux", name: "Linux", architecture: "amd64", version: "5.15.90.1-microsoft-standard-WSL2" }, platform: "Java/Oracle Corporation/15.0.2+7-27" }
```

Use mongosh to check data in MongoDB

C:\WinApp\MongoDBAtlasCLI>mongosh-1.6.2-win32-x64\bin\mongosh "mongodb+srv://cluster73852.xtjd8.mongodb.net/myFirstDatabase" --apiVersion 1 --username Cluster73852
Enter password: ************
Current Mongosh Log ID: 63c9e3692e5640a5c5323914
Connecting to:          mongodb+srv://<credentials>@cluster73852.xtjd8.mongodb.net/myFirstDatabase?appName=mongosh+1.6.2

#### MongoServerSelectionError: **connection <monitor> to 35.169.99.70:27017 closed**

/c/WinApp/MongoDBAtlasCLI/mongosh-1.6.2-win32-x64/bin/mongosh "mongodb+srv://cluster73852.xtjd8.mongodb.net/test" --apiVersion 1 --username Cluster73852
Enter password: ********
Current Mongosh Log ID: 642315a21acc4e5679fda344
Connecting to:          mongodb+srv://<credentials>@cluster73852.xtjd8.mongodb.net/test?appName=mongosh+1.6.2
Using MongoDB:          5.0.15 (API Version 1)
Using Mongosh:          1.6.2
For mongosh info see: https://docs.mongodb.com/mongodb-shell/


## 06/14/2023 
  - Specify --username Cluster73852 and AlexGo!2 authentication failed
  - don't use --username and w/o passworkd I can login successfully

C:\WinApp\MongoDBAtlasCLI\mongosh-1.6.2-win32-x64\bin>mongosh.exe "mongodb+srv://cluster73852.xtjd8.mongodb.net/test" --apiVersion 1
Current Mongosh Log ID: 6489e6af7a284cf14fef2ca9
Connecting to:          mongodb+srv://cluster73852.xtjd8.mongodb.net/test?appName=mongosh+1.6.2
Using MongoDB:          6.0.6 (API Version 1)
Using Mongosh:          1.6.2

For mongosh info see: https://docs.mongodb.com/mongodb-shell/


Atlas atlas-zpgx4q-shard-0 [primary] test>

    use   Set current database
    show  'show databases'/'show dbs': Print a list of all available databases.
          'show collections'/'show tables': Print a list of all collections for current database.
          'show profile': Prints system.profile information.
          'show users': Print a list of all users for current database.
          'show roles': Print a list of all roles for current database.
          'show log <type>': log for current connection, if type is not set uses 'global'
          'show logs': Print all logs.

Use Curl to test Docker container's port mapping

curl -H "Accept: text/event-stream" "http://localhost:8080/house-paged?page=0&size=50"

docker-compose.yml can create network.

For Dockerfile, use -

$ docker network create my-net # to create my-net network

    * this is a custom bridge network, all containers ports are exposed in this network)
    * use DNS automatically, use --network to specify custom network container uses
    * use --link to define env variables and other configure 
    *
$ docker create --name my-container-name -- network my-net --publich 8080:80 image:latest

to connect running containers to an existing user-definde network

$ docker network dis/connect my-net my-container-name

'host' network only works in Linux, not in Mac, nor Windows

## 12/01/2023
upgrade to springframe 3.1.2. but got repository default save() not found compiler error
after google, change HouseMongoRepository.java to extend class
 - new - ReactiveMongoRepository<House, String>
 - old - ReactiveSortingRepository<House, String>