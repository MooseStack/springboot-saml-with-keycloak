## Keycloak

1. Optional - Use my keycloak (`localhost` CN) cert and key, or generate your own with your domain/SAN:
   - `openssl req -new -newkey rsa:2048 -days 2000 -nodes -x509 -keyout ./keycloak/keycloak.key -out ./keycloak/keycloak.pem -addext 'subjectAltName=IP.1:127.0.0.1,IP.2:127.0.0.2,DNS.1:localhost,DNS.2:localhost.localdomain'`
  
2. `podman build -t keycloak ./keycloak`

3. `podman run -dit --name keycloak --replace -p 8080:8080 -p 8443:8443 localhost/keycloak:latest`

4. Access in web browser via either urls with `admin`/`admin` user and password:
   - https://localhost:8443
   - http://localhost:8080

### Create users in keycloak:

There's an already created `spring-boot-keycloak` realm in Keycloak if you've created keycloak using default existing config [keycloak/config/realm-export.json](keycloak/config/realm-export.json)

1. http://localhost:8080/admin/master/console/#/spring-boot-keycloak/users

- user: `admin`
- password: `admin`

- Docs: [User Creation in keycloak](https://www.keycloak.org/docs/latest/server_admin/index.html#assembly-managing-users_server_administration_guide)


## Java Springboot SAML app

### Optional - Generate SAML signing and verification certs
 
Already done - and configured in Keycloak and [application.yml](app/src/main/resources/application.yml)

if you want to redo it:

1. `openssl req -new -newkey rsa:2048 -days 2000 -nodes -x509 -keyout ./app/src/main/resources/saml-signing-verification.key -out ./app/src/main/resources/saml-signing-verification.pem`

   - This cert and key will be used by the springboot app AND keycloak to verify and sign the SAML response provided from the IDP.
   - The IDP(keycloak/etc) will need to have the .pem file uploaded to it.
     - If keycloak - in UI: `Switch to 'spring-boot-keycloak' Realm -> Clients -> <Click on SAML client name> -> Keys -> >Import Key> -> switch to PEM for Archive format  and upload "saml-signing-verification.pem"`
     - Locations: [app/src/main/resources/saml-signing-verification.pem](app/src/main/resources/saml-signing-verification.pem)



### Start App

1. `cd app`

2. Configure:
   - Application configurations: [application.yml](app/target/classes/application.yml)
     - Ensure keycloak URL's are correct
     - If using the [metadata-idp.xml](app/src/main/resources/metadata/metadata-idp.xml), ensure the certificate and key are correct by checking directly with the IDP(keycloak) provider's metadata.
       - In my case, I'm using [saml-signing-verification.pem](app/src/main/resources/saml-signing-verification.pem) that I uploaded to Keycloak. From there, keycloak converted it to a binary and base64 encoded it to view in the metadata xml.


3. `mvn spring-boot:run` or `mvn clean install -DskipTests && java -jar ./target/*jar`

4. Access app on http://localhost:8081


### Java app endpoints

- [/login](http://localhost:8081/login) - to login
- [/logout](http://localhost:8081/logout) - to log out
- [/access/user](http://localhost:8081/access/user) - tests authorization and authentication
    - allows authorization to this page if authenticated. 
    - The role "ROLE_USER" is automatically mapped to all authenticated user
- [/access/developer](http://localhost:8081/access/developer) - 
    - allows authorization to this page if user is part of the role "developers"
    - roles are typically mapped to a user/group in your IDP (keycloak)
    - [AccessController.java](app/src/main/java/AccessController.java)


### Troubleshooting

`Invalid signature for object [ID_7b686fcb-8f14-4aee-9313-9b45b3750f7e]`
 - This means that there is an issue during the signing or verification of SAML
 - If using metadata file instead of doing an http/https call in [application.yml](app/src/main/resources/application.yml)
   -  Ensure the [metadata-idp.xml](app/src/main/resources/metadata/metadata-idp.xml) has the correct `X509Certificate` and `KeyName`
   -  This certificate information is a binary and base64 encoded version of [saml-signing-verification.pem](app/src/main/resources/saml-signing-verification.pem) that keycloak generates when uploaded to keycloak
   -  You can pull the metadata xml from the IDP metadata, for example: http://localhost:8080/realms/spring-boot-keycloak/protocol/saml/descriptor