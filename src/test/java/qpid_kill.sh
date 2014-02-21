#!/bin/bash
kill -9 $(ps ax |grep QPID|grep -v grep| awk '{print $1}')
