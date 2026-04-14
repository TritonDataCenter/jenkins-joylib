/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019 Joyent, Inc.
 * Copyright 2026 Edgecast Cloud LLC.
 */


/**
 * Invokes multiple engbld targets to both build and image, and upload it to an
 * image server.
 *
 * Optional parameters:
 *   dir: directory to run make from (for monorepos that produce multiple
 *        images). When unset, make runs from the current working directory
 *        (typically the workspace root).
 *
 * Example: joyBuildImageAndUpload(dir: 'mysubdir')
 */
void call(Map args = [:]) {
    Set unknownArgs = args.keySet() - ['dir'] as Set;
    if (unknownArgs) {
        error("joyBuildImageAndUpload: unknown parameter(s): " +
              "${unknownArgs}. Valid parameters are: dir.")
    }
    String makeDir = args.dir ?: '.';
    echo "[joyBuildImageAndUpload] building from directory: ${makeDir}"
    withEnv(["MAKE_DIR=${makeDir}"]) {
        sh('''
set -o errexit
set -o pipefail

export ENGBLD_BITS_UPLOAD_IMGAPI=true
make -C "$MAKE_DIR" print-BRANCH print-STAMP all release publish buildimage bits-upload''')
    }
}
