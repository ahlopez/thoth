rem C:\apache\apache-tomcat-9.0.33\bin\shutdown.bat
call mvn clean
call mvn package
if EXIST C:\apache\apache-tomcat-9.0.33\webapps\evidentia-1.0-SNAPSHOT.war del  C:\apache\apache-tomcat-9.0.33\webapps\evidentia-1.0-SNAPSHOT.war /Q
if EXIST C:\apache\apache-tomcat-9.0.33\webapps\evidentia-1.0-SNAPSHOT rmdir C:\apache\apache-tomcat-9.0.33\webapps\evidentia-1.0-SNAPSHOT /S /Q
copy C:\ahl\des\wk1\thoth\target\evidentia-1.0-SNAPSHOT.war  C:\apache\apache-tomcat-9.0.33\webapps
