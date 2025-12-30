dependencies {
    implementation(project(":api"))
    implementation(project(":compatibility"))
    implementation(project(":nms:v1_21_R1"))
}

// 确保 plugin.yml 被正确处理
tasks.processResources {
    expand("version" to project.version)
}