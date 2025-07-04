#!/usr/bin/env bash

echo "Creating account..."
ACCOUNT_ID=$(curl -s -X POST http://localhost:8080/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 123,
    "countryCode": "EE",
    "currencies": ["EUR", "USD"]
  }' | jq -r '.accountId')

if [[ -z "$ACCOUNT_ID" || "$ACCOUNT_ID" == "null" ]]; then
  echo "Account creation failed, exiting"
  exit 1
fi

echo "Account created with id ${ACCOUNT_ID}"

TRANSACTION_PAYLOAD=$(cat <<EOF
{
  "accountId": $ACCOUNT_ID,
  "amount": 100,
  "currency": "EUR",
  "direction": "IN",
  "description": "load test"
}
EOF
)

CPUS=$(nproc)

#Create temporary file that holds a lua script for providing request body to wrk
LUA_SCRIPT_PATH="./wrk-post.lua"

cat > "$LUA_SCRIPT_PATH" <<EOF
wrk.method = "POST"
wrk.headers["Content-Type"] = "application/json"
wrk.body = [[$TRANSACTION_PAYLOAD]]
EOF

echo 'Running mock benchmark to warm up the JVM'
docker run --rm --network host -v "$(pwd)":/data williamyeh/wrk -t$(nproc) -c200 -d2s -s /data/wrk-post.lua http://localhost:8080/api/v1/transactions >/dev/null

echo 'Mock benchmark done, running real benchmark now...'

echo 'Running benchmark, it will take 5 seconds'
docker run --rm --network host -v "$(pwd)":/data williamyeh/wrk -t$(nproc) -c200 -d5s -s /data/wrk-post.lua http://localhost:8080/api/v1/transactions

# Clean up
rm "$LUA_SCRIPT_PATH"