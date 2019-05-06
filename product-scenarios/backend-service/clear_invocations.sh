#!/bin/bash

echo "[$(date -u)] Sending request to clear backend invocation history"
CLEAR_RESPONSE=$(curl -X "DELETE" http://ei-backend.scenarios.wso2.org:9090/eiTests/invocationCount/)
echo "[$(date -u)] Response from the backend: $CLEAR_RESPONSE"


