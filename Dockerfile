FROM clojure

COPY . /usr/src/app
WORKDIR /usr/src/app

RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" para-keats-standalone.jar

EXPOSE 6336
CMD ["java", "-jar", "para-keats-standalone.jar"]