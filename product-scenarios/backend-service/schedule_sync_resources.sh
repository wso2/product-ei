#!/bin/bash

addDate() {
    while IFS= read -r line; do
        echo "$(date) $line"
    done
}

echo '0 2 * * * ./sync_resources.sh >> sync_task.log' | crontab -

