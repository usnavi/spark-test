#!/bin/bash

identity="https://identity-internal.api.rackspacecloud.com"
files="https://storage101.ord1.clouddrive.com"
tenantid=842558
container=https://storage101.ord1.clouddrive.com/v1/MossoCloudFS_3029cf39-d8ef-4578-8c90-2abf64f24088/container_1
admin=cloudfeedadmin

echo "get cloudfeedadmin password!"
pw=PUT_PASSWORD_HERE
apikey=PUT_APIKEY_HERE

admin_token=`curl -sX POST $identity/v2.0/tokens -H "Content-type: application/json" -H "Accept: application/json" -d '{ "auth":{ "RAX-KSKEY:apiKeyCredentials":{ "username": "'$admin'", "apiKey":"'$apikey'"} } }' | jsawk 'return this.access.token.id'`

echo "admin: $admin_token"

# needs v1.1 to get admin user for given tenant
user_name=`curl -s --user $admin:$pw $identity/v1.1/mosso/842558 | jsawk 'return this.user.id'`

imp_token=`curl -sH "X-Auth-Token: $admin_token" -d '{"RAX-AUTH:impersonation":{"user":{"username":"'$user_name'"},"expire-in-seconds":10800}}' https://identity-internal.api.rackspacecloud.com/v2.0/RAX-AUTH/impersonation-tokens -H "Content-type: application/json" | jsawk 'return this.access.token.id'`

echo "imp: $imp_token"

# list container
curl -sH "X-Auth-Token: $imp_token" $container

# make container
#curl -vH "X-Auth-Token: $imp_token" -X PUT https://storage101.ord1.clouddrive.com/v1/MossoCloudFS_3029cf39-d8ef-4578-8c90-2abf64f24088/container_2

# make file
#curl -vH "X-Auth-Token: $imp_token" --upload-file pom.xml $container/file2.xml



#user_token=`curl -sX POST https://identity-internal.api.rackspacecloud.com/v2.0/tokens -H "Content-type: application/json" -H "Accept: application/json" -d '{ "auth":{ "RAX-KSKEY:apiKeyCredentials":{ "username": "gregsharek", "apiKey":"fec1a662c92c8f1f64099bde50efb800"} } }' | jsawk 'return this.access.token.id'` 
#echo "user: $user_token"




