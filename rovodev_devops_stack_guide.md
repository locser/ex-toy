# DevOps Stack Guide: AWS + Ansible + Packer + Docker + Rancher + K8s

## 🎯 Tổng Quan DevOps Stack

Đây là stack DevOps hiện đại cho enterprise applications, từ development đến production deployment:

```
┌─────────────────────────────────────────────────────────────┐
│                    DEVELOPMENT LAYER                        │
├─────────────────────────────────────────────────────────────┤
│  Docker          │  Docker Compose    │  Local Development  │
│  • Containerization │  • Multi-service │  • Fast iteration │
│  • Consistency   │  • Local testing   │  • Debugging      │
├─────────────────────────────────────────────────────────────┤
│                    BUILD & IMAGE LAYER                      │
├─────────────────────────────────────────────────────────────┤
│  Packer          │  Docker Registry   │  CI/CD Pipeline    │
│  • AMI creation  │  • Image storage   │  • Automated builds│
│  • Base images   │  • Version control │  • Testing        │
├─────────────────────────────────────────────────────────────┤
│                    INFRASTRUCTURE LAYER                     │
├─────────────────────────────────────────────────────────────┤
│  AWS             │  Ansible           │  Terraform        │
│  • Cloud provider│  • Configuration   │  • Infrastructure │
│  • Services      │  • Automation      │  • as Code        │
├─────────────────────────────────────────────────────────────┤
│                    ORCHESTRATION LAYER                      │
├─────────────────────────────────────────────────────────────┤
│  Kubernetes      │  Rancher           │  Helm Charts      │
│  • Container     │  • K8s Management  │  • Package        │
│  • orchestration │  • Multi-cluster   │  • management     │
└─────────────────────────────────────────────────────────────┘
```

## 🐳 Docker - Foundation Layer

### **Hiện tại trong codebase:**
```dockerfile
# Multi-stage build optimization
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Layer caching optimization
COPY pom.xml .
COPY */pom.xml ./
RUN mvn dependency:go-offline -B

# Build application
COPY . .
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -g 1000 appuser && adduser -D -u 1000 -G appuser appuser

# Security best practices
USER appuser
EXPOSE 1122

# Performance tuning
ENV JAVA_OPTS="-server -Xms512m -Xmx1024m -XX:+UseG1GC"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### **Docker Best Practices:**
```dockerfile
# 1. Multi-stage builds
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

FROM node:18-alpine AS runtime
COPY --from=builder /app/node_modules ./node_modules
COPY . .
EXPOSE 3000
CMD ["npm", "start"]

# 2. Security hardening
FROM alpine:3.18
RUN addgroup -g 1001 -S nodejs && adduser -S nodejs -u 1001
USER nodejs

# 3. Health checks
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:3000/health || exit 1

# 4. Build optimization
# Use .dockerignore
# Layer caching
# Minimal base images
```

### **Docker Compose - Local Development:**
```yaml
# Current setup enhanced
version: '3.8'
services:
  app:
    build: 
      context: .
      dockerfile: Dockerfile
      target: development  # Multi-stage target
    ports:
      - "1122:1122"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_URL=jdbc:mysql://mysql:3306/java_demo
    depends_on:
      mysql:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:1122/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    networks:
      - app-network

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: java_demo
    volumes:
      - mysql_data:/var/lib/mysql
      - ./docker/mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

networks:
  app-network:
    driver: bridge

volumes:
  mysql_data:
```

## 📦 Packer - Image Building

### **Purpose:**
- **AMI Creation**: Build custom AWS AMIs
- **Base Images**: Standardized server images
- **Immutable Infrastructure**: Pre-configured images

### **Packer Template Example:**
```json
{
  "variables": {
    "aws_region": "us-west-2",
    "instance_type": "t3.medium",
    "source_ami": "ami-0c02fb55956c7d316"
  },
  "builders": [{
    "type": "amazon-ebs",
    "region": "{{user `aws_region`}}",
    "source_ami": "{{user `source_ami`}}",
    "instance_type": "{{user `instance_type`}}",
    "ssh_username": "ec2-user",
    "ami_name": "toy-app-{{timestamp}}",
    "tags": {
      "Name": "ToyApp-BaseImage",
      "Environment": "production",
      "Application": "toy-exchange"
    }
  }],
  "provisioners": [
    {
      "type": "shell",
      "inline": [
        "sudo yum update -y",
        "sudo yum install -y docker",
        "sudo systemctl enable docker",
        "sudo systemctl start docker",
        "sudo usermod -a -G docker ec2-user"
      ]
    },
    {
      "type": "ansible",
      "playbook_file": "./ansible/base-setup.yml",
      "user": "ec2-user"
    },
    {
      "type": "file",
      "source": "./docker-compose.yml",
      "destination": "/home/ec2-user/docker-compose.yml"
    }
  ]
}
```

### **Build Process:**
```bash
# Build AMI
packer build \
  -var 'aws_region=us-west-2' \
  -var 'instance_type=t3.medium' \
  toy-app-ami.json

# Validate template
packer validate toy-app-ami.json

# Debug build
packer build -debug toy-app-ami.json
```

## 🔧 Ansible - Configuration Management

### **Purpose:**
- **Configuration Management**: Automate server setup
- **Application Deployment**: Deploy applications consistently
- **Infrastructure Automation**: Manage infrastructure as code

### **Ansible Playbook Example:**
```yaml
# ansible/deploy-toy-app.yml
---
- name: Deploy Toy Exchange Application
  hosts: toy_servers
  become: yes
  vars:
    app_name: toy-exchange
    app_version: "{{ lookup('env', 'APP_VERSION') | default('latest') }}"
    docker_image: "your-registry/toy-app:{{ app_version }}"
    
  tasks:
    - name: Install Docker
      yum:
        name: docker
        state: present
      
    - name: Start Docker service
      systemd:
        name: docker
        state: started
        enabled: yes
        
    - name: Create application directory
      file:
        path: /opt/{{ app_name }}
        state: directory
        owner: ec2-user
        group: ec2-user
        
    - name: Copy docker-compose file
      template:
        src: docker-compose.yml.j2
        dest: /opt/{{ app_name }}/docker-compose.yml
        owner: ec2-user
        group: ec2-user
        
    - name: Pull latest Docker images
      docker_image:
        name: "{{ docker_image }}"
        source: pull
        
    - name: Deploy application
      docker_compose:
        project_src: /opt/{{ app_name }}
        state: present
        
    - name: Wait for application to be ready
      uri:
        url: "http://localhost:1122/actuator/health"
        method: GET
        status_code: 200
      retries: 30
      delay: 10
```

### **Inventory Management:**
```ini
# ansible/inventory/production
[toy_servers]
toy-app-1 ansible_host=10.0.1.10 ansible_user=ec2-user
toy-app-2 ansible_host=10.0.1.11 ansible_user=ec2-user
toy-app-3 ansible_host=10.0.1.12 ansible_user=ec2-user

[toy_servers:vars]
ansible_ssh_private_key_file=~/.ssh/toy-app-key.pem
environment=production
app_version=v1.2.3

[database_servers]
toy-db-1 ansible_host=10.0.2.10 ansible_user=ec2-user

[load_balancers]
toy-lb-1 ansible_host=10.0.3.10 ansible_user=ec2-user
```

### **Deployment Commands:**
```bash
# Deploy to production
ansible-playbook -i inventory/production deploy-toy-app.yml

# Deploy specific version
ansible-playbook -i inventory/production deploy-toy-app.yml \
  -e "app_version=v1.2.3"

# Rolling deployment
ansible-playbook -i inventory/production deploy-toy-app.yml \
  --limit toy_servers \
  --serial 1

# Check deployment status
ansible toy_servers -i inventory/production -m uri \
  -a "url=http://localhost:1122/actuator/health"

## ☁️ AWS - Cloud Infrastructure

### **Core Services for Toy Exchange App:**

#### **1. Compute Services:**
```yaml
# EC2 Instances
Production Setup:
  - Application Servers: 3x t3.large (Auto Scaling Group)
  - Database: RDS MySQL 8.0 (Multi-AZ)
  - Load Balancer: Application Load Balancer
  - Cache: ElastiCache Redis cluster

Development Setup:
  - Application: 1x t3.medium
  - Database: RDS MySQL (Single AZ)
  - Cache: Single Redis node
```

#### **2. AWS Architecture for Toy App:**
```
┌─────────────────────────────────────────────────────────────┐
│                        INTERNET                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                 CloudFront CDN                              │
│                 (Static Assets)                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│              Application Load Balancer                      │
│                 (SSL Termination)                           │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                  Auto Scaling Group                         │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   EC2-1     │ │   EC2-2     │ │   EC2-3     │           │
│  │ Toy App     │ │ Toy App     │ │ Toy App     │           │
│  │ Container   │ │ Container   │ │ Container   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                   Data Layer                                │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ RDS MySQL   │ │ ElastiCache │ │     S3      │           │
│  │ (Multi-AZ)  │ │   Redis     │ │ (File Store)│           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
```

#### **3. Terraform for AWS Infrastructure:**
```hcl
# terraform/main.tf
provider "aws" {
  region = var.aws_region
}

# VPC and Networking
resource "aws_vpc" "toy_app_vpc" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  tags = {
    Name        = "toy-app-vpc"
    Environment = var.environment
  }
}

# Public Subnets for Load Balancer
resource "aws_subnet" "public_subnets" {
  count             = length(var.availability_zones)
  vpc_id            = aws_vpc.toy_app_vpc.id
  cidr_block        = "10.0.${count.index + 1}.0/24"
  availability_zone = var.availability_zones[count.index]
  
  map_public_ip_on_launch = true
  
  tags = {
    Name = "toy-app-public-subnet-${count.index + 1}"
    Type = "Public"
  }
}

# Private Subnets for Application
resource "aws_subnet" "private_subnets" {
  count             = length(var.availability_zones)
  vpc_id            = aws_vpc.toy_app_vpc.id
  cidr_block        = "10.0.${count.index + 10}.0/24"
  availability_zone = var.availability_zones[count.index]
  
  tags = {
    Name = "toy-app-private-subnet-${count.index + 1}"
    Type = "Private"
  }
}

# Application Load Balancer
resource "aws_lb" "toy_app_alb" {
  name               = "toy-app-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb_sg.id]
  subnets            = aws_subnet.public_subnets[*].id
  
  enable_deletion_protection = false
  
  tags = {
    Environment = var.environment
  }
}

# Auto Scaling Group
resource "aws_autoscaling_group" "toy_app_asg" {
  name                = "toy-app-asg"
  vpc_zone_identifier = aws_subnet.private_subnets[*].id
  target_group_arns   = [aws_lb_target_group.toy_app_tg.arn]
  health_check_type   = "ELB"
  
  min_size         = 2
  max_size         = 10
  desired_capacity = 3
  
  launch_template {
    id      = aws_launch_template.toy_app_lt.id
    version = "$Latest"
  }
  
  tag {
    key                 = "Name"
    value               = "toy-app-instance"
    propagate_at_launch = true
  }
}

# RDS MySQL Database
resource "aws_db_instance" "toy_app_db" {
  identifier = "toy-app-database"
  
  engine         = "mysql"
  engine_version = "8.0"
  instance_class = "db.t3.medium"
  
  allocated_storage     = 100
  max_allocated_storage = 1000
  storage_type          = "gp2"
  storage_encrypted     = true
  
  db_name  = "java_demo"
  username = "root"
  password = var.db_password
  
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
  db_subnet_group_name   = aws_db_subnet_group.toy_app_db_subnet_group.name
  
  backup_retention_period = 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "sun:04:00-sun:05:00"
  
  multi_az               = true
  publicly_accessible    = false
  
  tags = {
    Name        = "toy-app-database"
    Environment = var.environment
  }
}

# ElastiCache Redis
resource "aws_elasticache_subnet_group" "toy_app_cache_subnet" {
  name       = "toy-app-cache-subnet"
  subnet_ids = aws_subnet.private_subnets[*].id
}

resource "aws_elasticache_replication_group" "toy_app_redis" {
  replication_group_id       = "toy-app-redis"
  description                = "Redis cluster for toy app"
  
  node_type            = "cache.t3.micro"
  port                 = 6379
  parameter_group_name = "default.redis7"
  
  num_cache_clusters = 2
  
  subnet_group_name  = aws_elasticache_subnet_group.toy_app_cache_subnet.name
  security_group_ids = [aws_security_group.redis_sg.id]
  
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true
  
  tags = {
    Name        = "toy-app-redis"
    Environment = var.environment
  }
}
```

#### **4. AWS CLI Commands:**
```bash
# Deploy infrastructure
terraform init
terraform plan -var-file="production.tfvars"
terraform apply -var-file="production.tfvars"

# Application deployment
aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 123456789012.dkr.ecr.us-west-2.amazonaws.com

# Build and push image
docker build -t toy-app .
docker tag toy-app:latest 123456789012.dkr.ecr.us-west-2.amazonaws.com/toy-app:latest
docker push 123456789012.dkr.ecr.us-west-2.amazonaws.com/toy-app:latest

# Update Auto Scaling Group
aws autoscaling update-auto-scaling-group \
  --auto-scaling-group-name toy-app-asg \
  --desired-capacity 5

# Check application health
aws elbv2 describe-target-health \
  --target-group-arn arn:aws:elasticloadbalancing:us-west-2:123456789012:targetgroup/toy-app-tg/1234567890123456
```

## ⚓ Kubernetes - Container Orchestration

### **Purpose:**
- **Container Orchestration**: Manage containerized applications
- **Auto-scaling**: Scale based on demand
- **Service Discovery**: Internal service communication
- **Rolling Updates**: Zero-downtime deployments

### **K8s Manifests for Toy App:**

#### **1. Namespace and ConfigMap:**
```yaml
# k8s/namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: toy-exchange
  labels:
    name: toy-exchange
    environment: production

---
# k8s/configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: toy-app-config
  namespace: toy-exchange
data:
  SPRING_PROFILES_ACTIVE: "kubernetes"
  DB_HOST: "mysql-service"
  REDIS_HOST: "redis-service"
  KAFKA_BOOTSTRAP_SERVERS: "kafka-service:9092"
  JAVA_OPTS: "-server -Xms512m -Xmx1024m -XX:+UseG1GC"
```

#### **2. Deployment:**
```yaml
# k8s/deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: toy-app
  namespace: toy-exchange
  labels:
    app: toy-app
    version: v1
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0
  selector:
    matchLabels:
      app: toy-app
  template:
    metadata:
      labels:
        app: toy-app
        version: v1
    spec:
      containers:
      - name: toy-app
        image: your-registry/toy-app:latest
        ports:
        - containerPort: 1122
          name: http
        env:
        - name: SPRING_PROFILES_ACTIVE
          valueFrom:
            configMapKeyRef:
              name: toy-app-config
              key: SPRING_PROFILES_ACTIVE
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: toy-app-secrets
              key: db-password
        - name: REDIS_PASSWORD
          valueFrom:
            secretKeyRef:
              name: toy-app-secrets
              key: redis-password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 1122
          initialDelaySeconds: 60
          periodSeconds: 30
          timeoutSeconds: 5
          failureThreshold: 3
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 1122
          initialDelaySeconds: 30
          periodSeconds: 10
          timeoutSeconds: 3
          failureThreshold: 3
        volumeMounts:
        - name: app-logs
          mountPath: /app/logs
      volumes:
      - name: app-logs
        emptyDir: {}
      imagePullSecrets:
      - name: registry-secret
```

#### **3. Service and Ingress:**
```yaml
# k8s/service.yaml
apiVersion: v1
kind: Service
metadata:
  name: toy-app-service
  namespace: toy-exchange
  labels:
    app: toy-app
spec:
  type: ClusterIP
  ports:
  - port: 80
    targetPort: 1122
    protocol: TCP
    name: http
  selector:
    app: toy-app

---
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: toy-app-ingress
  namespace: toy-exchange
  annotations:
    kubernetes.io/ingress.class: "nginx"
    cert-manager.io/cluster-issuer: "letsencrypt-prod"
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  tls:
  - hosts:
    - api.toy-exchange.com
    secretName: toy-app-tls
  rules:
  - host: api.toy-exchange.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: toy-app-service
            port:
              number: 80
```

#### **4. HPA (Horizontal Pod Autoscaler):**
```yaml
# k8s/hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: toy-app-hpa
  namespace: toy-exchange
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: toy-app
  minReplicas: 3
  maxReplicas: 20
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
  - type: Resource
    resource:
      name: memory
      target:
        type: Utilization
        averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
      - type: Percent
        value: 10
        periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 60
      policies:
      - type: Percent
        value: 50
        periodSeconds: 60
```

#### **5. K8s Commands:**
```bash
# Deploy application
kubectl apply -f k8s/

# Check deployment status
kubectl get pods -n toy-exchange
kubectl get services -n toy-exchange
kubectl get ingress -n toy-exchange

# Scale deployment
kubectl scale deployment toy-app --replicas=5 -n toy-exchange

# Rolling update
kubectl set image deployment/toy-app toy-app=your-registry/toy-app:v1.2.3 -n toy-exchange

# Check logs
kubectl logs -f deployment/toy-app -n toy-exchange

# Port forward for debugging
kubectl port-forward service/toy-app-service 8080:80 -n toy-exchange
```

## 🐄 Rancher - Kubernetes Management Platform

### **Purpose:**
- **Multi-cluster Management**: Manage multiple K8s clusters
- **User-friendly UI**: Simplified K8s operations
- **RBAC**: Role-based access control
- **Monitoring**: Built-in monitoring and alerting

### **Rancher Setup:**

#### **1. Rancher Installation:**
```bash
# Install Rancher on existing K8s cluster
helm repo add rancher-latest https://releases.rancher.com/server-charts/latest
kubectl create namespace cattle-system

# Install cert-manager
kubectl apply --validate=false -f https://github.com/jetstack/cert-manager/releases/download/v1.5.1/cert-manager.crds.yaml
kubectl create namespace cert-manager
helm repo add jetstack https://charts.jetstack.io
helm install cert-manager jetstack/cert-manager --namespace cert-manager --version v1.5.1

# Install Rancher
helm install rancher rancher-latest/rancher \
  --namespace cattle-system \
  --set hostname=rancher.toy-exchange.com \
  --set ingress.tls.source=letsEncrypt \
  --set letsEncrypt.email=admin@toy-exchange.com
```

#### **2. Rancher Project Structure:**
```yaml
# Rancher Project for Toy Exchange
Project: toy-exchange-production
├── Namespaces:
│   ├── toy-exchange-app      # Application workloads
│   ├── toy-exchange-data     # Database and cache
│   └── toy-exchange-monitor  # Monitoring stack
├── Resource Quotas:
│   ├── CPU: 10 cores
│   ├── Memory: 20Gi
│   └── Storage: 100Gi
└── Network Policies:
    ├── Allow app → database
    ├── Allow app → cache
    └── Deny all other traffic
```

#### **3. Rancher Pipeline (CI/CD):**
```yaml
# .rancher-pipeline.yml
stages:
- name: Build
  steps:
  - runScriptConfig:
      image: maven:3.9.6-eclipse-temurin-21
      shellScript: |
        mvn clean package -DskipTests
        
- name: Docker Build
  steps:
  - publishImageConfig:
      dockerfilePath: ./Dockerfile
      buildContext: .
      tag: toy-app:${CICD_EXECUTION_SEQUENCE}
      registry: your-registry.com
      
- name: Deploy to Staging
  steps:
  - applyYamlConfig:
      path: ./k8s/staging/
      
- name: Deploy to Production
  when:
    branch:
      include: ["master"]
  steps:
  - applyYamlConfig:
      path: ./k8s/production/
```

#### **4. Rancher Monitoring:**
```yaml
# Rancher monitoring configuration
apiVersion: v1
kind: ConfigMap
metadata:
  name: toy-app-monitoring
  namespace: cattle-monitoring-system
data:
  prometheus-rules.yaml: |
    groups:
    - name: toy-app.rules
      rules:
      - alert: ToyAppHighCPU
        expr: rate(container_cpu_usage_seconds_total{pod=~"toy-app-.*"}[5m]) > 0.8
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Toy App high CPU usage"
          description: "Pod {{ $labels.pod }} CPU usage is above 80%"
          
      - alert: ToyAppHighMemory
        expr: container_memory_usage_bytes{pod=~"toy-app-.*"} / container_spec_memory_limit_bytes > 0.9
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "Toy App high memory usage"
          description: "Pod {{ $labels.pod }} memory usage is above 90%"
```

## 🔄 Complete DevOps Workflow

### **End-to-End Pipeline:**

```
┌─────────────────────────────────────────────────────────────┐
│                    DEVELOPMENT PHASE                        │
├─────────────────────────────────────────────────────────────┤
│  1. Code Development (Local)                                │
│     • Docker Compose for local testing                     │
│     • Hot reload with volume mounts                        │
│                                                             │
│  2. Build & Test                                            │
│     • Maven build in Docker container                      │
│     • Unit tests, integration tests                        │
│     • SonarQube code quality checks                        │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    BUILD PHASE                              │
├─────────────────────────────────────────────────────────────┤
│  3. Image Building                                          │
│     • Packer builds base AMI with dependencies             │
│     • Docker builds application image                      │
│     • Push to ECR/Docker Registry                          │
│                                                             │
│  4. Infrastructure Provisioning                            │
│     • Terraform provisions AWS infrastructure              │
│     • Ansible configures servers                           │
│     • Kubernetes cluster setup                             │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    DEPLOYMENT PHASE                         │
├─────────────────────────────────────────────────────────────┤
│  5. Staging Deployment                                      │
│     • Deploy to staging K8s cluster                        │
│     • Automated testing (E2E, performance)                 │
│     • Manual QA validation                                 │
│                                                             │
│  6. Production Deployment                                   │
│     • Blue-green deployment via Rancher                    │
│     • Health checks and rollback capability               │
│     • Monitoring and alerting activation                   │
└─────────────────────┬───────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────┐
│                    OPERATIONS PHASE                         │
├─────────────────────────────────────────────────────────────┤
│  7. Monitoring & Scaling                                    │
│     • Prometheus metrics collection                        │
│     • Grafana dashboards                                   │
│     • Auto-scaling based on load                           │
│                                                             │
│  8. Maintenance & Updates                                   │
│     • Rolling updates via K8s                              │
│     • Infrastructure updates via Terraform                 │
│     • Security patches via Ansible                         │
└─────────────────────────────────────────────────────────────┘
```

## 🎯 Best Practices Summary

### **Docker:**
- ✅ Multi-stage builds for smaller images
- ✅ Non-root users for security
- ✅ Health checks for reliability
- ✅ Layer caching optimization

### **AWS:**
- ✅ Infrastructure as Code (Terraform)
- ✅ Auto Scaling Groups for resilience
- ✅ Multi-AZ deployments for HA
- ✅ Security groups and IAM roles

### **Ansible:**
- ✅ Idempotent playbooks
- ✅ Inventory management
- ✅ Vault for secrets
- ✅ Rolling deployments

### **Packer:**
- ✅ Immutable infrastructure
- ✅ Consistent base images
- ✅ Automated AMI builds
- ✅ Version control for images

### **Kubernetes:**
- ✅ Resource limits and requests
- ✅ Health checks (liveness/readiness)
- ✅ ConfigMaps and Secrets
- ✅ Horizontal Pod Autoscaling

### **Rancher:**
- ✅ Multi-cluster management
- ✅ RBAC implementation
- ✅ Pipeline automation
- ✅ Centralized monitoring

## 🚀 Getting Started Roadmap

### **Phase 1: Foundation (Weeks 1-2)**
1. **Docker mastery**: Containerize applications
2. **AWS basics**: EC2, VPC, RDS setup
3. **Basic automation**: Simple Ansible playbooks

### **Phase 2: Orchestration (Weeks 3-4)**
1. **Kubernetes fundamentals**: Pods, Services, Deployments
2. **Infrastructure as Code**: Terraform basics
3. **Image building**: Packer for AMI creation

### **Phase 3: Advanced (Weeks 5-6)**
1. **Rancher setup**: Multi-cluster management
2. **CI/CD pipelines**: Automated deployments
3. **Monitoring**: Prometheus + Grafana

### **Phase 4: Production (Weeks 7-8)**
1. **Security hardening**: RBAC, network policies
2. **Performance optimization**: Auto-scaling, caching
3. **Disaster recovery**: Backup and restore procedures

---

**Conclusion**: Đây là stack DevOps enterprise-grade cho modern applications. Mỗi tool có vai trò riêng và work together để tạo ra một pipeline deployment robust, scalable và maintainable.