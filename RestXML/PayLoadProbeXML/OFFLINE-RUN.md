The 503 happens because Jersey 2 needs the HK2 injection provider at runtime. Maven could not download it due to network restrictions, so the webapp initializes as UNAVAILABLE and Jetty returns 503.

Fix without Maven/network: drop the required jars into WEB-INF/lib so Jetty loads them directly.

Steps
1) Stop any running Jetty (optional if not running)
   - Close the terminal running mvn jetty:run, or kill the mvn process in Task Manager.

2) Create the lib directory
   - Create folder: RestXML/PayLoadProbeXML/src/main/webapp/WEB-INF/lib

3) Download these jars with your browser and place them into that lib folder exactly
   Jersey HK2 bridge (required):
   - https://repo1.maven.org/maven2/org/glassfish/jersey/inject/jersey-hk2/2.34/jersey-hk2-2.34.jar

   HK2 core (versions aligned to Jersey 2.34):
   - https://repo1.maven.org/maven2/org/glassfish/hk2/hk2-api/2.6.1/hk2-api-2.6.1.jar
   - https://repo1.maven.org/maven2/org/glassfish/hk2/hk2-locator/2.6.1/hk2-locator-2.6.1.jar
   - https://repo1.maven.org/maven2/org/glassfish/hk2/hk2-utils/2.6.1/hk2-utils-2.6.1.jar

   JSR-330 injection API:
   - https://repo1.maven.org/maven2/javax/inject/javax.inject/1/javax.inject-1.jar

   Notes:
   - Other Jersey jars (jersey-server, jersey-common, jersey-container-servlet) appear to be present locally already, because Jetty started the ServletContainer before failing on InjectionManagerFactory.
   - If you still get ClassNotFound after adding the 5 jars above, also add (version 2.34):
     - https://repo1.maven.org/maven2/org/glassfish/jersey/core/jersey-common/2.34/jersey-common-2.34.jar
     - https://repo1.maven.org/maven2/org/glassfish/jersey/core/jersey-server/2.34/jersey-server-2.34.jar
     - https://repo1.maven.org/maven2/org/glassfish/jersey/containers/jersey-container-servlet/2.34/jersey-container-servlet-2.34.jar

4) Start Jetty using the bundled Maven
   From repository root:
     - PowerShell:
       $env:JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-8.0.462.8-hotspot"
       $env:Path="$env:JAVA_HOME\bin;$env:Path"
       .\tools\apache-maven-3.9.6\bin\mvn.cmd -f RestXML\PayLoadProbeXML\pom.xml -DskipTests=true jetty:run

5) Verify endpoints
   - http://localhost:8080/help  (should return JSON, not 503)
   - http://localhost:8080/fetch/openTest  (should return text/xml)
   - POST http://localhost:8080/default  (Content-Type: text/xml) returns a default XML response

Why this works
- Jetty’s webapp classloader includes WEB-INF/lib ahead of the container. By placing the missing Jersey-HK2 and HK2 jars there, Jersey can create the InjectionManagerFactory and the app initializes successfully, eliminating the 503.

Online alternative (when proxy is available)
- Keep the added pom dependency org.glassfish.jersey.inject:jersey-hk2:2.34.
- Configure Maven proxy (settings.xml) so it can download dependencies.
- Then run: .\tools\apache-maven-3.9.6\bin\mvn.cmd -s RestXML\PayLoadProbeXML\maven-settings.xml -f RestXML\PayLoadProbeXML\pom.xml -DskipTests=true jetty:run
