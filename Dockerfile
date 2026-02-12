FROM eclipse-temurin:17-jre-jammy

# === Install Python 3.11 + venv + wget (optional) ===
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        python3.11 \
        python3.11-venv \
        python3.11-distutils \
    && rm -rf /var/lib/apt/lists/* \
    && ln -sf /usr/bin/python3.11 /usr/bin/python \
    && ln -sf /usr/bin/python3.11 /usr/bin/python3

WORKDIR /opt/app

# === Copy JAR ===
ARG JAR_FILE=target/rentstuff_api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# === Copy Python code ===
COPY rentstuff_marketing/ ./autogen/

# === Install PyTorch + diffusers + requirements (NO WGET) ===
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --upgrade pip && \
    /opt/venv/bin/pip install --no-cache-dir \
        torch==2.4.1+cpu torchvision==0.19.1+cpu \
        --index-url https://download.pytorch.org/whl/cpu && \
    /opt/venv/bin/pip install --no-cache-dir diffusers==0.30.3 && \
    /opt/venv/bin/pip install --no-cache-dir transformers && \
    /opt/venv/bin/pip install --no-cache-dir accelerate && \
    /opt/venv/bin/pip install --no-cache-dir ddgs && \
    /opt/venv/bin/pip install --no-cache-dir -r autogen/requirements.txt
        

ENV PATH="/opt/venv/bin:$PATH"
ENV PYTORCH_ENABLE_MPS_FALLBACK=1
ENV CUDA_VISIBLE_DEVICES=""

WORKDIR /opt/app

HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]