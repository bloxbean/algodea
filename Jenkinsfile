pipeline {
    agent any

    parameters {
        choice(
                choices: ['BUILD_ONLY' , 'RELEASE'],
                description: '',
                name: 'BUILD_TYPE')
    }

    tools {
        //gradle 'Gradle 5.6.2'
        jdk 'jdk-8'
    }

    stages {

        stage('Build') {
            steps {
                 sh  './gradlew clean build'
            }
        }

        stage('Results') {
            steps {
                archiveArtifacts 'build/distributions/*.zip'
            }
        }
    }
}
