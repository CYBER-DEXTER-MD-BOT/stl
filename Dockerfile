FROM openjdk:17
WORKDIR /app
COPY . .
RUN javac SongDownloadBot.java
CMD ["java", "SongDownloadBot"]
