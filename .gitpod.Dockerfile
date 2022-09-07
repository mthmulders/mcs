FROM gitpod/workspace-full

USER gitpod

RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 22.1.0.r17-grl && \
    sdk default java 22.1.0.r17-grl"