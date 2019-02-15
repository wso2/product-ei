#!/bin/bash

echo "$(echo '* */2 * * * ./clear_invocations.sh >> clear_invocations.log' ; crontab -l)" | crontab -
echo "scheduled task that clears invocations to run every 2 hours"

