pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                bat 'docker build -t max2ba/user-service:latest D:/JavaProjects/user-service'
            }
        }

        stage('Docker Restart') {
            steps {
                bat '''
                cd /d D:\\JavaProjects\\config-server
                docker compose up -d --build user-service
                '''
            }
        }
    }
}