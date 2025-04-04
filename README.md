# Okta API Security Demo

This project demonstrates how to secure a Spring Boot API with Okta OAuth 2.0 / OIDC. It includes examples of public endpoints, authenticated endpoints, and role-based access control.

## Purpose

This simple API application seeks to demonstrate the following:

1. How an API can be secured with Okta and OAuth2 while supporting:

    - User-based flows such as Authorization Code with PKCE or Token Exchange.
    - Machine-to-machine or "daemon" scenarios using Client Credentials.

    By supporting both types of access in the API, we leave it to the authorization server's configuration to decide which flows (or "grant types") are allowed for any
    particular application client that consumes the API.

2. How the API can use Role-Based Access Control (RBAC) for user-based flows by:

    - defining logical roles, which are granted access to certain features or capabilities of the API
    - binding these logical roles to specific Okta groups in the application configuration (e.g., `application.yml` or in the environment)

    By specifying Okta group names in the application configuration rather than code, we can use different group names for different application environments (e.g., local, dev,
    uat, prod), and we can update role/group bindings without having to rebuild the API application.

    See `src/main/resources/application.yml` and `src/main/java/com/example/oktaapi/config/AppAuthoritiesConfig.java`.

3. How endpoint access can be restricted with two different approaches:

    - In the security configuration (see `src/main/java/com/example/oktaapi/config/SecurityConfig.java`)
    - At the controller endpoints themselves (see `src/main/java/com/example/oktaapi/controller/SecuredController.java`)

## Features

- Spring Boot 3.x with Java 21
- Okta OAuth 2.0 / OIDC integration
- JWT validation and role-based authorization
- Public and secured API endpoints
- Dev Container support for consistent development environment

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop) installed
- [Visual Studio Code](https://code.visualstudio.com/) with the [Dev Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)
- An [Okta Developer Account](https://developer.okta.com/signup/)

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/kevtacular/okta-api-demo.git
cd okta-api-demo
```

### 2. Set up Okta

1. Sign in to your Okta Developer Account
2. Go to **Applications** > **Create App Integration**
3. Select **API Service** as the application type and click **Next**
4. Name your application (e.g., "Spring Boot API Demo")
5. Click **Save**
6. Note your **Client ID**
7. Go to **Security** > **API** > **Authorization Servers**
8. Note your Okta domain and authorization server ID (default is "default" unless you've created a custom auth server)

In addition, create two or more Okta groups to associate with the "user" and "admin" logical roles. Assign test users to different groups to support your test scenarios.

### 3. Configure the application

Create a new `src/main/resources/application-local.yml` file with your Okta details. Also, assign the Okta groups that you created to the application's logical roles
("user" and "admin"). For example:

```yaml
okta:
  oauth2:
    issuer: https://dev-99999999.okta.com/oauth2/default
    audience: api://default
    client-id: 0oao3ipkmq0000000000

app:
  roles:
    user:
      - My User Group
      - My Admin Group
    admin:
      - My Admin Group
```

### 4. Open in Dev Container (Optional)

1. Open the project in Visual Studio Code
2. When prompted, click "Reopen in Container" or run the "Dev Containers: Reopen in Container" command
3. Wait for the container to build and initialize

### 5. Run the application

Once inside the Dev Container, run:

```bash
./mvnw spring-boot:run
```

The API will be available at <http://localhost:8080>

## API Endpoints

### Public Endpoints (No Authentication Required)

- `GET /api/public` - Returns a public message
- `GET /api/public/status` - Returns API status information

### Private Endpoints (Authentication Required)

- `GET /api/private` - Returns a private message (requires authenticated user to be in an Okta group associated with the logical "user" role)
- `GET /api/admin` - Returns an admin message (requires authenticated user to be in an Okta group associated with the logical "admin" role)
- `GET /api/token-info` - Returns information about the JWT bearer token received on the request

## Testing the API

### Using curl

1. Get an access token from Okta (you can use the Okta CLI, create a test token in the Okta Developer Console, or create a test token in a tool like Postman).

2. Test public endpoint:

    ```bash
    curl http://localhost:8080/api/public
    ```

3. Test private endpoint:

    ```bash
    curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" http://localhost:8080/api/private
    ```

4. Test admin endpoint:

    ```bash
    curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" http://localhost:8080/api/admin
    ```

5. Get token info:

    ```bash
    curl -H "Authorization: Bearer YOUR_ACCESS_TOKEN" http://localhost:8080/api/token-info
    ```

### Using JUnit tests

JUnit tests are provided in `src/test/java/com/example/oktaapi/controller/SecuredControllerTest.java`. (The app does not need to be running in order to run the unit tests, naturally.)

```bash
./mvnw test
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
