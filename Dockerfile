FROM eclipse-temurin:17-jre-jammy

# Install Python 3.11 + venv
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        python3.11 \
        python3.11-venv \
        python3-pip \
    && rm -rf /var/lib/apt/lists/* \
    && ln -sf /usr/bin/python3.11 /usr/bin/python \
    && ln -sf /usr/bin/python3.11 /usr/bin/python3

WORKDIR /opt/app

# Copy JAR
ARG JAR_FILE=target/rentstuff_api-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Copy Python code
COPY rentstuff_marketing/ ./autogen/

# Create venv and install ALL dependencies in one go (no duplicates)
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --upgrade pip && \   
    /opt/venv/bin/pip install --no-cache-dir "numpy<2.0"  huggingface_hub==0.22.2 && \    
    /opt/venv/bin/pip install --no-cache-dir \
        torch==2.1.0+cpu torchvision==0.16.0+cpu torchaudio==2.1.0+cpu \
        --index-url https://download.pytorch.org/whl/cpu \
    && /opt/venv/bin/pip install --no-cache-dir diffusers==0.26.3 transformers==4.36.2 accelerate==0.25.0 langchain langchain-openai langchain-community langgraph tweepy pillow python-dotenv ddgs \
        -r autogen/requirements.txt
ENV PATH="/opt/venv/bin:$PATH"
ENV PYTORCH_ENABLE_MPS_FALLBACK=1
ENV CUDA_VISIBLE_DEVICES=""

WORKDIR /opt/app

HEALTHCHECK --interval=30s CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]