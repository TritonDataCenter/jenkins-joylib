/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019 Joyent, Inc.
 */


/**

*/
void call(String channel = 'jenkins') {
    echo "call"
    // addapted from https://github.com/jenkinsci/mattermost-plugin/blob/mattermost-2.7.0/src/main/java/jenkins/plugins/mattermost/ActiveNotifier.java
    def STATUS_MAP = ['SUCCESS': ':white_check_mark:', 'FAILURE': 'no_entry_sign:',
                      'UNSTABLE': ':no_entry_sign:', 'ABORTED': ':warning:',
                      'NOT_BUILT': ':warning:', 'UNSTABLE': ':warning:'];
     String emoji = ':question';
    if (STATUS_MAP.containsKey(currentBuild.currentResult)) {
        emoji = STATUS_MAP[currentBuild.currentResult];
    }

    String branch = env.BRANCH_NAME;
    if (!branch) {
        echo "[joyMattermostNotification] env.BRANCH_NAME=${env.BRANCH_NAME}, trying to guess real branch...";
        branch = sh(returnStdout: true, script: 'git symbolic-ref HEAD | cut -d / -f 3');
        echo "[joyMattermostNotification] guessed ${branch}";
    }

    // wtf
    echo branch;
    if (branch == 'master') {
        echo "==t";
    }
    if (branch == 'master' || false) {
        echo "==tt";
    }
    
    
    if (branch == 'master' || (branch =~ '^release.*').matches()) {
        mattermostSend(
            channel: channel,
            color: "${if (currentBuild.currentResult == 'SUCCESS') 'good' else 'danger'}",
            message: "${emoji} ${env.JOB_NAME} - #${env.BUILD_NUMBER} ${currentBuild.currentResult} after ${currentBuild.durationString.replace(' and counting', '')} (<${currentBuild.absoluteUrl}|Open>)",
            text: "${currentBuild.description}")
    } else {
        echo "[joyMattermostNotification] not in whitelisted branch, suppressing notification"
    }
}
