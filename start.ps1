$ErrorActionPreference = "Stop"
Set-Location "F:\codex\苍穹外卖"
$env:JAVA_HOME = "F:\codex\苍穹外卖\.tools\jdk-17.0.19+10"
$env.PATH = "$env:JAVA_HOME\bin;$env.PATH"
& "F:\codex\苍穹外卖\.tools\maven\apache-maven-3.9.15\bin\mvn.cmd" spring-boot:run