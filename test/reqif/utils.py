##########################
# Copyright (C) 2023 Daniel Pindur <pindurdan@gmail.com>, <xpindu01@stud.fit.vutbr.cz>
#
# This program and the accompanying materials are made available under
# the terms of the Eclipse Public License 2.0 which is available at
# https://www.eclipse.org/legal/epl-2.0
#
# SPDX-License-Identifier: EPL-2.0
##########################

import sys

class Log:
    """Logger class"""

    @staticmethod
    def Error(errorMessage, code):
        """Logs error messege and exits the program with specified return code"""
        sys.stderr.write('ERROR: ' + errorMessage + '\n')
        sys.exit(code)

    @staticmethod
    def Warning(message):
        """Prints to stderr"""
        sys.stderr.write('WARNING: ' + message + '\n')

    @staticmethod
    def Info(message):
        """Prints to stdout"""
        sys.stdout.write('INFO: ' + message + '\n')
