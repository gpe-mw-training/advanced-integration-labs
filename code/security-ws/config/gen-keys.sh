#!/bin/bash

# Set the values we'll use for the generation
read -p"Server Key Alias?" serverkeyalias
read -p"Server Key Password?" serverkeypassword
read -p"Server Keystore Password?" serverstorepassword
read -p"Server Keystore File Name?" serverkeystorename

read -p"Client Key Alias?" clientkeyalias
read -p"Client Key Password?" clientkeypassword
read -p"Client Keystore Password?" clientstorepassword
read -p"Client Keystore File Name?" clientkeystorename

# Generate the server and client keys
keytool -genkey -alias $serverkeyalias -keyalg RSA -sigalg SHA1withRSA -keypass $serverkeypassword -storepass $serverstorepassword -keystore $serverkeystorename -dname "cn=localhost"
keytool -genkey -alias $clientkeyalias -keyalg RSA -sigalg SHA1withRSA -keypass $clientkeypassword -storepass $clientstorepassword -keystore $clientkeystorename -dname "cn=integration"

# Export the client key and import it to the server keystore
keytool -export -rfc -keystore $clientkeystorename -storepass $clientstorepassword -alias $clientkeyalias -file $clientkeyalias.cer
keytool -import -trustcacerts -keystore $serverkeystorename -storepass $serverstorepassword -alias $clientkeyalias -file $clientkeyalias.cer -noprompt
rm $clientkeyalias.cer

# Export the server key and import it to the client keystore
keytool -export -rfc -keystore $serverkeystorename -storepass $serverstorepassword -alias $serverkeyalias -file $serverkeyalias.cer
keytool -import -trustcacerts -keystore $clientkeystorename -storepass $clientstorepassword -alias $serverkeyalias -file $serverkeyalias.cer -noprompt
rm $serverkeyalias.cer
