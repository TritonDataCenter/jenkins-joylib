/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2020 Joyent, Inc.
 */


/**
 * The label expressions used on jenkins.joyent.us tend to be quite long.  This
 * method is a helper for the most common case: an agent of a particular pkgsrc
 * version that can build production images, and is not used for the platform.

 * See https://modocs.joyent.us/engdoc/master/jenkins/index.html (internal) for
 * more on labels
*/
void call(Map args = [:]) {
    if (! args.image_ver) {
        throw new Exception("missing required parameter image_ver");
    }
    args.pi = args.pi ?: '20181206T011455Z';
    args.jenkins_agent = args.jenkins_agent ?: '2';

    String pkgsrc_arch = args.pkgsrc_arch;
    if (! pkgsrc_arch) {
        pkgsrc_arch = 'x86_64';
        if (args.image_ver < '18.4.0') {
            pkgsrc_arch = 'multiarch';
        }
    }
    String labels = "!platform:true && image_ver:${args.image_ver} && pkgsrc_arch:${pkgsrc_arch} && pi:${args.pi} && jenkins_agent:${args.jenkins_agent}";
    echo "joyent common labels computed: ${labels}";
    return labels;
}
