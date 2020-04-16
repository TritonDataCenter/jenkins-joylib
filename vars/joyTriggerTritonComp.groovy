/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2020 Joyent, Inc.
 */

def AGENTS = [
    "sdc-agents-core",
    "triton-cmon-agent",
    "sdc-cn-agent",
    "sdc-net-agent",
    "sdc-vm-agent",
    "sdc-hagfish-watcher",
    "sdc-smart-login",
    "sdc-amon",
    "sdc-firewaller-agent",
    "sdc-config-agent",
]

/**
 * Triggers a joyent-org build of args.repo on args.compBranch if we're on the
 * args.whenBranch of the current repository. If a ${COMPONENTS} environment
 * variable is set, we only build args.repo if it's listed in that variable.
 */
void call(Map args = [:]) {
    if (!args.repo) {
        throw new Exception(
            "missing requires parameter repo: " + args.toString());
    }
    if (!args.compBranch) {
        throw new Exception(
            "missing required parameter compBranch: " +  args.toString());
    }
    if (!args.whenBranch) {
        throw new Exception(
            "missing required parameter whenBranch: " + args.toString());
    }

    if (args.whenBranch != env.BRANCH_NAME) {
        echo "Skipping build due to mismatched whenBranch";
        return;
    }

    def userIdCause = currentBuild.getBuildCauses(
        'hudson.model.Cause$UserIdCause');
    if (!userIdCause) {
        echo "Build of " + args.repo + " was not triggered by a user, skipping.";
        return;
    }

    /*
     * We only build a given component if no $COMPONENTS parameter was set,
     * or if this repository was included in the value.
     */
    def components = env.COMPONENTS.split(" ");
    if (env.COMPONENTS == "" || args.repo in components)  {
        if (args.repo in AGENTS) {
            /*
             * We don't want an automatic build of sdc-agents-installer for
             * every agent. We only need to build that once.
             */
            build(
                job: "joyent-org/" + args.repo + "/" + args.compBranch,
                wait: true,
                parameters: [
                    [
                        $class: 'BooleanParameterValue',
                        name: 'TRIGGER_AGENTS_INSTALLER_BUILD',
                        value: false,
                    ]
                ]);
        } else {
            build(
                job: "joyent-org/" + args.repo + "/" + args.compBranch,
                wait: true);
        }
    } else if (args.repo == "sdc-agents-installer" &&
                shouldBuildSDCAgentsInstaller(components)) {
        echo "Building sdc-agents-installer since COMPONENTS included an agent";
        build(
            job: "joyent-org/" + args.repo + "/" + args.compBranch,
            wait: true);
    } else {
        echo "Skipping build of " + args.repo;
    }
}

boolean shouldBuildSDCAgentsInstaller(components) {
    boolean includesAgent = false;
    // No components were passed, which means build everything
    if (components.size() == 0) {
        return true;
    }
    // If any agents were in the list of components to be built
    // then we need to build sdc-agents-installer
    for (agent in AGENTS) {
        if (agent in components) {
            includesAgent = true;
        }
    }
    return includesAgent;
}
