pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1' // Updated to your preferred region
        ECR_REPO = 'customer-service-repo' // Your ECR repository name
        AWS_ACCOUNT_ID = '754369775377' // Replace with your AWS account ID
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        DOCKER_IMAGE = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}:${IMAGE_TAG}"
        K8S_CONFIG_REPO = 'https://github.com/yourusername/kubernetes-configs.git' // Replace with your Kubernetes configs repo
        K8S_CLUSTER_NAME = 'customer-service-cluster' // Your EKS cluster name
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/yourusername/customer-service.git' // Replace with your application repository URL
            }
        }

        stage('Build') {
            steps {
                // Build the Maven project and package the application
                sh 'mvn clean package'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    sh """
                        # Log in to AWS ECR (Elastic Container Registry)
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com

                        # Build Docker image
                        docker build -t ${DOCKER_IMAGE} .

                        # Check if the ECR repository exists, if not create it
                        aws ecr describe-repositories --repository-names ${ECR_REPO} || aws ecr create-repository --repository-name ${ECR_REPO}

                        # Push the Docker image to ECR
                        docker push ${DOCKER_IMAGE}
                    """
                }
            }
        }

        stage('Deploy to EKS') {
            steps {
                script {
                    // Clone your Kubernetes manifests repository
                    sh """
                        git clone ${K8S_CONFIG_REPO} kubernetes-configs
                    """

                    // Update the Kubernetes deployment.yaml to use the new image
                    sh """
                        sed -i 's|image: .*|image: ${DOCKER_IMAGE}|g' kubernetes-configs/customer-service/deployment.yaml
                    """

                    // Update kubeconfig to access the EKS cluster and apply Kubernetes manifests
                    sh """
                        aws eks --region ${AWS_REGION} update-kubeconfig --name ${K8S_CLUSTER_NAME}
                        kubectl apply -f kubernetes-configs/customer-service/
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment to EKS was successful!'
        }
        failure {
            echo 'Deployment failed. Please check the logs.'
        }
    }
}
