Spring Cloud Vault GreeterApplication
=====================================
1) Set-up Vault-Token on your local by downloading it from https://www.vaultproject.io/downloads.html

2) Refer to the vault.conf file we have backend as file. For difference between different backends and suitability of their implementation refer. 

	https://www.vaultproject.io/intro/getting-started/secret-backends.html.
	
3) We have https enabled in our vault.conf file.

	Start the server - $ vault server -config vault.conf
	
4) Initialize vault and unseal vault using the keys

	$ vault init
	
	$ vault unseal <key>
	
5) For setting-up you can refer https://spring.io/blog/2016/06/24/managing-secrets-with-vault or for completeness this guide its given below according to this project.

	Setup

	Open a console in the examples root directory and execute the following commands to setup Vault:

	$ src/test/bash/create_certificates.sh # Create SSL certificates
	
	Vault requires some configuration before you can run the examples. (You need to set-up the environment variables in windows that you have in env.sh)
	Replace with the actual root token that you got while initializing vault.
	
	$ source src/test/bash/env.sh 
	The root token is set to 00000000-0000-0000-0000-000000000000 and Vault is running in dev mode.

	You can use Vault now from the console or run the examples. Check out the example-specific readme's for further instructions/requirements.

	Vault runs with SSL enabled so make sure the application runs in the current directory so it can find work/keystore.jks.
	

6) Write data to generic backend 

	$ vault write secret/vaultDemo mykey=Sai ttl=1h
	
7) Read data from generic backend

	$ vault read secret/vaultDemo
	
For more info on generic backend - https://www.vaultproject.io/docs/secrets/generic/index.html

8) Mount postgresql

	$ vault mount postgresql
	
9) Connect to postgresql

	$ vault write postgresql/config/connection connection_url="postgresql://localhost:5432/uaa"

10) Configure lease settings for credentials generated

	$ vault write postgresql/config/lease lease=1h lease_max=24h

11) Create a role that maps to a policy used to generate credentials

	$ vault write postgresql/roles/readonly \
      sql="CREATE ROLE \"{{name}}\" WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}';
      GRANT SELECT ON ALL TABLES IN SCHEMA public TO \"{{name}}\";"
	
12) To test if you are able to generate the credentials you can run the below command from the vault console you can (We do this in our client application)
	
	$ vault read postgresql/creds/readonly
	
	For more info on postgresql refer https://www.vaultproject.io/docs/secrets/postgresql/index.html

13) Create a Policy for authorization

	Create a policy file with file format .hcl like below.
		
		path "sys/*" {
		  policy = "deny"
		}

		path "secret/*" {
		  policy = "write"
		}

		path "auth/approle/*" {
		  policy = "read"
		}

		path "postgresql/creds/*" {
		  policy = "read"
		}
	
	https://www.vaultproject.io/intro/getting-started/acl.html
	
14) Writing a policy
	
	$ vault policy-write <name of the policy> <file name>
	$ vault policies (shows available policies) - available to users with root access
	$ vault policies <policy name> (shows contents of the policy) - available to users with root access
	$ vault auth <token> (to change the token for authentication)
	
15) Enable AppRole authentication

	Create environment vauriable with root token VAULT_TOKEN
	
	$ curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"type":"approle"}' https://127.0.0.1:8200/v1/sys/auth/approle -k
	
16) Create an AppRole with desired set of policies

	$ curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" -d '{"policies":"secret"}' https://127.0.0.1:8200/v1/auth/approle/role/readonly -k
	
17) Fetch Role ID and place it in your application.properties

	$ curl -X GET -H "X-Vault-Token:$VAULT_TOKEN" https://127.0.0.1:8200/v1/auth/approle/role/readonly/role-id -k

	
18) Create new Secret ID under the identifier and place it in your application.properties

	$ curl -X POST -H "X-Vault-Token:$VAULT_TOKEN" https://127.0.0.1:8200/v1/auth/approle/role/readonly/secret-id -k
