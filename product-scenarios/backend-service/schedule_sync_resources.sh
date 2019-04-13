#!/bin/bash

echo "$(echo '*/10 * * * * ./sync_resources.sh >> sync_task.log' ; crontab -l)" | crontab -
echo "scheduled task to run every 10 minutes"

