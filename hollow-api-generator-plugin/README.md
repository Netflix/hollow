#Hollow-api-generator-plugin

This plugin provides task for generating hollow consumer api, that is 
described [here](http://hollow.how/getting-started/#consumer-api-generation)

## Usage
In order to use, add plugin dependency to your buildscript
```
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath "com.netflix.hollow:hollow-api-generator-plugin:0.1"
    }
}
```
apply it
```
apply plugin: 'hollow-api-generator-plugin'
```
and configure (you have to specify all the three values to work)
```
hollow {
    packagesToScan = ['org.example.data', 'org.example.other.data']
    apiClassName = 'MyApi'
    apiPackageName = 'org.example.consumer.api'
}
```

where:

- `packagesToScan` - packages with your data classes, **scan is recursive**
- `apiClassName` - class name for your [api implementation](https://github.com/Netflix/hollow/blob/master/hollow/src/main/java/com/netflix/hollow/api/custom/HollowAPI.java) 
- `apiPackageName` - target package in your project for api-related sources

launch task:

`gradle generateHollowConsumerApi`
