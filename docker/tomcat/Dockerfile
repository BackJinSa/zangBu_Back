FROM tomcat:9.0.105-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*

# COPY build/libs/ROOT.war /usr/local/tomcat/webapps/

# 로컬에서 가져온 ROOT.war만 배치 (빌드 컨텍스트에 ROOT.war를 같이 올림)
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
