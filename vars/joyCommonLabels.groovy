/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019 Joyent, Inc.
 */


/**

*/
void call(Map args = [:]) {
    // example: '!platform:true && image_ver:18.4.0 && pkgsrc_arch:x86_64 && pi:20151126T062538Z && jenkins_agent:2'
    if (! args.image_ver) {
        throw new Exception("missing required parameter image_ver");
    }
    args.pi = args.pi ?: '20151126T062538Z';
    args.jenkins_agent = args.jenkins_agent ?: '2';

    String pkgsrc_arch = 'x86_64';
    if (args.image_ver < '18.4.0') {
        pkgsrc_arch = 'multiarch';
    }
    String labels = "!platform:true && image_ver:${args.image_ver} && pkgsrc_arch:${pkgsrc_arch} && pi:${args.pi} && jenkins_agent:${args.jenkins_agent}";
    echo "joyent common labels computed: ${labels}";
    return labels;
}
