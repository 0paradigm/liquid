FROM openjdk:18-jdk-alpine

ENV DOCKER true
ENV TZ Asia/Shanghai
ENV LIQUID_HOME /opt/liquid

RUN apt update
RUN apt install -y curl
RUN rm -rf /var/lib/apt/lists/*


WORKDIR $LIQUID_HOME

# FIXME
ADD ./target/liquid $LIQUID_HOME

EXPOSE 10025

# TODO: ~dolphinscheduler
CMD [ "/bin/bash", "./bin/start.sh" ]
