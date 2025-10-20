plugins {
    id("java")
}



repositories {
    mavenCentral()
    
}

dependencies {
    compileOnly("net.portswigger.burp.extensions:montoya-api:2025.3")
    compileOnly("com.formdev:flatlaf:3.2.5")
    compileOnly("com.formdev:flatlaf-extras:3.2.5")
    implementation("com.opencsv:opencsv:5.11")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
    implementation("org.freemarker:freemarker:2.3.34")
    implementation("org.jsoup:jsoup:1.20.1")
    implementation("org.apache.poi:poi-ooxml:5.4.1")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.document:2.1.0")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.document.docx:2.1.0")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.converter.docx.xwpf:2.1.0")
    implementation("fr.opensagres.xdocreport:fr.opensagres.xdocreport.template.velocity:2.1.0")
    implementation("org.apache.velocity:velocity-engine-core:2.4.1")
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("commons-validator:commons-validator:1.9.0")

}




tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it)   })
}

tasks.build {
    dependsOn(tasks.jar)
}