rem C:\apache\tomcat-9.0.40\bin\shutdown.bat
call mvn clean
call mvn package -Dvaadin.ignoreVersionChecks=true
if EXIST C:\apache\tomcat-9.0.40\webapps\evidentia-1.0-SNAPSHOT.war del  C:\apache\tomcat-9.0.40\webapps\evidentia-1.0-SNAPSHOT.war /Q
if EXIST C:\apache\tomcat-9.0.40\webapps\evidentia-1.0-SNAPSHOT rmdir C:\apache\tomcat-9.0.40\webapps\evidentia-1.0-SNAPSHOT /S /Q
copy C:\ahl\des\gitrepo\thoth\target\evidentia-1.0-SNAPSHOT.war  C:\apache\tomcat-9.0.40\webapps
