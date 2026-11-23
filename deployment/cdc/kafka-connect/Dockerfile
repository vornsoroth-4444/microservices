# ============================================================
#  Stage 1 — download plugins using alpine (tiny ~7 MB image)
# ============================================================
FROM alpine:3.19 AS downloader

RUN apk add --no-cache curl tar

WORKDIR /plugins

# ── 1. Debezium Oracle Source Connector (v3.0.0 for Kafka 3.8+) ─
RUN mkdir -p debezium-oracle \
 && curl -fsSL \
    "https://repo1.maven.org/maven2/io/debezium/debezium-connector-oracle/3.0.0.Final/debezium-connector-oracle-3.0.0.Final-plugin.tar.gz" \
    -o /tmp/debezium-oracle.tar.gz \
 && tar -xzf /tmp/debezium-oracle.tar.gz -C debezium-oracle --strip-components=1 \
 && rm -f /tmp/debezium-oracle.tar.gz \
 && curl -fsSL \
    "https://repo1.maven.org/maven2/com/oracle/database/jdbc/ojdbc11/23.3.0.23.09/ojdbc11-23.3.0.23.09.jar" \
    -o debezium-oracle/ojdbc11.jar

# ── 2. Debezium JDBC Sink Connector (v3.0.0 for Kafka 3.8+) ───
RUN mkdir -p debezium-jdbc \
 && curl -fsSL \
    "https://repo1.maven.org/maven2/io/debezium/debezium-connector-jdbc/3.0.0.Final/debezium-connector-jdbc-3.0.0.Final-plugin.tar.gz" \
    -o /tmp/debezium-jdbc.tar.gz \
 && tar -xzf /tmp/debezium-jdbc.tar.gz -C debezium-jdbc --strip-components=1 \
 && rm -f /tmp/debezium-jdbc.tar.gz

# ── 3. PostgreSQL JDBC driver ────────────────────────────────
RUN curl -fsSL \
    "https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.3/postgresql-42.7.3.jar" \
    -o debezium-jdbc/postgresql-42.7.3.jar

# ============================================================
#  Stage 2 — final image using CACHED cp-kafka:latest
# ============================================================
FROM confluentinc/cp-kafka:latest

USER root

# Copy downloaded plugins from downloader stage
COPY --from=downloader /plugins /usr/share/java/kafka-connect-plugins

# Create connect configuration directories
RUN mkdir -p /etc/kafka /kafka/connect

EXPOSE 8083

USER appuser
