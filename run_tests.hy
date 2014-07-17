#!/usr/local/bin/hy
(import sys)
(import unittest)
(import test_buildscript)

(unittest.main (get sys.modules "test_buildscript"))

; TODO accept command-line arguments to choose between running
; buildscript tests, or regular `lein test`, or `lein test-refresh`

