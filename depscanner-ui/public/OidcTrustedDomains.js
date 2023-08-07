/**
 * trustedDomains to allow OIDC access to my depscanner project realm for user authentication
 */
const trustedDomains = {
    default:[
        'http://localhost:8181/realms/depscanner',
        'http://keycloak:8181/realms/depscanner'
    ]
}