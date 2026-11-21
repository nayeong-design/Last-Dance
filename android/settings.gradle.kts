pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Pleos Playground SDK 저장소는 개발자 계정 발급 후 여기에 추가합니다.
    }
}
rootProject.name = "LastDance"
include(":app")
