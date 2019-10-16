/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019 Joyent, Inc.
 */


/**
 
*/
void call(String img_ver = "18.4.1", String pi = '20151126T062538Z', String jenkins_agent = '2') {
    // example: '!platform:true && image_ver:18.4.0 && pkgsrc_arch:x86_64 && pi:20151126T062538Z && jenkins_agent:2'
    String pkgsrc_arch = 'x86_64';
    if (img_ver < '18.4.0') {
        pkgsrc_arch = 'multiarch';
    }
    String labels = "!platform:true && image_ver:${img_ver} && pkgsrc_arch:${pkgsrc_arch} && pi:${pi} && jenkins_agent:${jenkins_agent}";
    echo "joyent common labels: ${labels}";
    return labels;
}
