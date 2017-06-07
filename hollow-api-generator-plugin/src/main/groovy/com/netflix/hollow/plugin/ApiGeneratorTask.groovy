/**
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.plugin

import com.netflix.hollow.api.codegen.HollowAPIGenerator
import com.netflix.hollow.core.write.HollowWriteStateEngine
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper
import org.gradle.api.DefaultTask
import org.gradle.api.InvalidUserDataException
import org.gradle.api.tasks.TaskAction

class ApiGeneratorTask extends DefaultTask {

    final File projectDirFile = project.projectDir
    final String projectDirPath = projectDirFile.absolutePath
    final String relativeJavaSourcesPath = '/src/main/java/'
    final String javaSourcesPath = projectDirPath + relativeJavaSourcesPath
    final String compiledClassesPath = projectDirPath + '/build/classes/main/'

    URLClassLoader urlClassLoader

    @TaskAction
    void generateApi() {
        ApiGeneratorExtension extension = project.extensions.getByType(ApiGeneratorExtension.class)
        validatePluginConfiguration(extension)

        initClassLoader()

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine()
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine)

        Collection<Class<?>> datamodelClasses = extractClasses(extension.packagesToScan)
        for (Class<?> clazz : datamodelClasses) {
            logger.debug('Initialize schema for class {}', clazz.getName())
            mapper.initializeTypeState(clazz)
        }

        HollowAPIGenerator generator =
                new HollowAPIGenerator(
                        extension.apiClassName,
                        extension.apiPackageName,
                        writeEngine
                )

        String apiTargetPath = buildPathToApiTargetFolder(extension.apiPackageName)

        cleanupAndCreateFolders(apiTargetPath)
        generator.generateFiles(apiTargetPath)
    }

    private Collection<Class<?>> extractClasses(List<String> packagesToScan) {
        Set<Class<?>> classes = new HashSet<>()

        for (String packageToScan : packagesToScan) {
            File packageFile = buildPackageFile(packageToScan)

            List<File> allFilesInPackage = findFilesRecursively(packageFile)
            List<String> classNames = []
            for (File file : allFilesInPackage) {
                String filePath = file.absolutePath
                logger.debug('Candidate for schema initialization {}', filePath)
                if (filePath.endsWith('.java') &&
                        !filePath.endsWith('package-info.java') &&
                        !filePath.endsWith('module-info.java')
                ) {
                    String relativePathToFile = filePath - projectDirPath - relativeJavaSourcesPath
                    classNames << convertFolderPathToPackageName(relativePathToFile - '.java')
                }
            }

            for (String fqdn : classNames) {
                try {
                    Class<?> clazz = urlClassLoader.loadClass(fqdn)
                    classes << clazz
                } catch (ClassNotFoundException e) {
                    logger.warn('{} class not found', fqdn)
                }
            }
        }
        return classes
    }

    private List<File> findFilesRecursively(File packageFile) {
        List<File> foundFiles = new ArrayList<>()
        if (packageFile.exists()) {
            for (File file : packageFile.listFiles()) {
                if (file.isDirectory()) {
                    foundFiles.addAll(findFilesRecursively(file))
                } else {
                    foundFiles.add(file)
                }
            }
        }
        return foundFiles
    }

    private File buildPackageFile(String packageName) {
        return new File(javaSourcesPath + convertPackageNameToFolderPath(packageName))
    }

    private String buildPathToApiTargetFolder(String apiPackageName) {
        return javaSourcesPath + convertPackageNameToFolderPath(apiPackageName)
    }

    private String convertPackageNameToFolderPath(String packageName) {
        return packageName.replaceAll('\\.', '/')
    }

    private String convertFolderPathToPackageName(String folderName) {
        return folderName.replaceAll('/', '\\.')
    }

    private void cleanupAndCreateFolders(String generatedApiTarget) {
        File apiCodeFolder = new File(generatedApiTarget)
        apiCodeFolder.mkdirs()
        for (File f : apiCodeFolder.listFiles()) {
            f.delete()
        }
    }

    private void initClassLoader() {
        URL url = new File(compiledClassesPath).toURI().toURL()
        URL[] urls = [url] as URL[]
        urlClassLoader = new URLClassLoader(urls)
    }

    private void validatePluginConfiguration(ApiGeneratorExtension extension) {
        if (extension.apiClassName == null || extension.apiPackageName == null || extension.packagesToScan.isEmpty()) {
            throw new InvalidUserDataException('Specify buildscript as per plugin readme!')
        }
    }
}
