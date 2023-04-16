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
