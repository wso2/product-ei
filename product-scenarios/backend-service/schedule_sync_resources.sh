#!/bin/bash

addDate() {
    while IFS= read -r line; do
        echo "$(date) $line"
    done
}

echo '*/10 * * * * ./sync_resources.sh >> sync_task.log' | crontab -
echo "scheduled task to run every 10 minutes"

