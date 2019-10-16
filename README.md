## Introduction

This repository contains a small set of reusable parts for Jenkins pipelines
building Joyent components in the form of a shared library.  As of the initial
writing, helpful resources on extending pipelines with shared libraries
included:

 * https://jenkins.io/doc/book/pipeline/shared-libraries
 * https://jenkins.io/blog/2017/10/02/pipeline-templates-with-shared-libraries/
 * https://medium.com/@AndrzejRehmann/private-jenkins-shared-libraries-540abe7a0ab7
 * https://automatingguy.com/2017/12/29/jenkins-pipelines-shared-libraries/

## Brief Usage Example

(The shared library would generally be configured on the Jenkins server first as
described in the upstream "Using Libraries" documentation.)

The `joyCommonLabels` helper if for generating commonly used label expressions.  So

```groovy
pipeline {
    agent {
        label '!platform:true && image_ver:18.4.0 && pkgsrc_arch:x86_64 && pi:20151126T062538Z && jenkins_agent:2'
    }
    // rest of your pipeline here
}
```

Becomes

```groovy
@Library('jenkins-joylib@tag_version') _

pipeline {
    agent {
        label joyCommonLabels(image_ver: '18.4.0')
    }
    // rest of your pipeline here
}
```


## Versioning

Pipelines should almost always point at a specific `x.y.z` *tag* of this library so that
builds remain reproducible.
