/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2020 Joyent, Inc.
 */


/**
 * Triggers a joyent-org build of args.repo on args.compBranch if we're on the
 * args.whenBranch of the current repository. If a ${COMPONENTS} environment
 * variable is set, we only build args.repo if it's listed in that variable.
 */
void call(Map args = [:]) {
    if (! args.repo) {
        throw new Exception("missing requires parameter repo");
    }
    if (! args.compBranch) {
        throw new Exception("missing required parameter compBranch");
    }
    if (!args.whenBranch) {
        throw new Exception("missing required parameter whenBranch");
    }

    if (args.whenBranch != env.BRANCH_NAME) {
        echo "Skipping build due to mismatched whenBranch"
        return
    }

    def userIdCause = currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause');
    if (userIdCause == null) {
        echo "Build of " + args.repo + " was not triggered by a user, skipping."
        return
    }

    if (env.COMPONENTS == "" ||
        env.COMPONENTS =~ ".*" + args.repo + ".*")  {
        if (args.isAgentBuild) {
            build(
                job: "joyent-org/" + args.repo + "/" + args.compBranch,
                wait: true,
                params: [
                    [
                        $class: 'BooleanParameterValue',
                        name: 'TRIGGER_AGENTS_INSTALLER_BUILD',
                        value: false,
                    ]
                ])
        } else {
            build(
                job: "joyent-org/" + args.repo + "/" + args.compBranch,
                wait: true)
        }
    } else {
        echo "Skipping build of " + args.repo;
    }
}
