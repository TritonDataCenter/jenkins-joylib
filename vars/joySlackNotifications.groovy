/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2021 Joyent, Inc.
 */


/**
 * Notify the jenkins Slack channel, only on master/release branches, and
 * don't neglect colors or emoji.
*/
void call(Map args = [:]) {
    String channel = args.channel ?: 'jenkins';
    String comment = args.comment ?: '';

    // adapted from https://github.com/jenkinsci/slack-plugin/blob/slack-2.48/src/main/java/jenkins/plugins/slack/ActiveNotifier.java
    def STATUS_MAP = ['SUCCESS': ':white_check_mark:', 'FAILURE': ':no_entry_sign:',
                      'UNSTABLE': ':no_entry_sign:', 'ABORTED': ':warning:',
                      'NOT_BUILT': ':warning:', 'UNSTABLE': ':warning:'];
     String emoji = ':question';
    if (STATUS_MAP.containsKey(currentBuild.currentResult)) {
        emoji = STATUS_MAP[currentBuild.currentResult];
    }

    String branch = env.BRANCH_NAME;
    if (!branch) {
        echo "[joySlackNotification] env.BRANCH_NAME=${env.BRANCH_NAME}, trying to guess real branch...";
        branch = sh(returnStdout: true, script: 'git symbolic-ref HEAD | cut -d / -f 3').trim();
        echo "[joySlackNotification] guessed ${branch}";
    }

    String slackText = "";
    if (currentBuild.description) {
        slackText = currentBuild.description;
    } else if (branch) {
        slackText = branch;
    }

    // Callers can include a comment which is useful to differentiate
    // notifications from different pipeline stages, but can be used for any
    // purpose.
    if (comment) {
        slackText += ' ' + comment;
    }

    if (branch == 'master' || branch == 'mantav1' || branch ==~ '^release.*') {
        slackSend(
            channel: channel,
            color: "${if (currentBuild.currentResult == 'SUCCESS') 'good' else 'danger'}",
            message: "${env.JOB_NAME} - #${env.BUILD_NUMBER} ${emoji} ${currentBuild.currentResult} after ${currentBuild.durationString.replace(' and counting', '')} (<${currentBuild.absoluteUrl}|Open>)\n ${slackText}")
    } else {
        echo "[joySlackNotification] not in master, mantav1, or release-YYYYMMDD branch: suppressing notification"
    }
}
