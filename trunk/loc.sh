#!/bin/sh
echo "overall lines of code:"
find src/crm/ -name "*.java" | xargs wc -l | sort -n
echo ""
echo "core system (everything except dtos, domain classes, tests):"
find src/ -name "*.java" | grep -v "client/dto" | grep -v "server/domain" | grep -v "client/test" | grep -v "server/test" | xargs wc -l | sort -n
echo ""
echo "client test code only:"
find src/crm/client/test -name "*.java" | xargs wc -l | tail -n 1 
echo ""
echo "server test code only:"
find src/crm/server/test -name "*.java" | xargs wc -l | tail -n 1
