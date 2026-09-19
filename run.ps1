$env:JAVA_HOME = ".\.tools\jdk-17.0.19+10"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
& ".\.tools\maven\apache-maven-3.9.15\bin\mvn.cmd" spring-boot:run