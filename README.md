# AEM Test Samples
Basic tests that illustrate how to start a test module using aem-testing-clients.
Tests are written according to [Best practices](https://github.com/adobe/aem-testing-clients/wiki/Best-practices).


## How to use
Clone the repository and use maven for running each of the test modules:
```bash
mvn clean verify -Ptest-all
```

A list of system properties can be passed to the maven command to convey info about the AEM instances 
(if different than default localhost:4502 and localhost:4503):
```bash
-Dsling.it.instances=2 
-Dsling.it.instance.url.1=http://author_hostnme:port
-Dsling.it.instance.runmode.1=author 
-Dsling.it.instance.adminUser.1=user 
-Dsling.it.instance.adminPassword.1=password 
-Dsling.it.instance.url.2=http://publish_hostname:port 
-Dsling.it.instance.runmode.2=publish 
-Dsling.it.instance.adminUser.2=user 
-Dsling.it.instance.adminPassword.2=password
```

The build also produces a `jar-with-dependencies` that can be run as a self-contained test module 
(using java directly or a small maven pom with failsafe configured).

