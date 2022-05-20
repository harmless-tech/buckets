package tech.harmless.buckets.security.oauth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import tech.harmless.buckets.security.oauth.github.GitHubAdmins

@Configuration
class OAuth2UserApprovalHandler {

    @Bean
    fun rest(clients: ClientRegistrationRepository, auths: OAuth2AuthorizedClientRepository): WebClient {
        val oauth2 = ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, auths)
        return WebClient.builder().filter(oauth2).build()
    }

    // Only handles GitHub right now.
    @Bean
    fun oauth2UserService(rest: WebClient): OAuth2UserService<OAuth2UserRequest, OAuth2User> {
        val delegate = DefaultOAuth2UserService()

        return OAuth2UserService<OAuth2UserRequest, OAuth2User> { request ->
            val user = delegate.loadUser(request)
            if ("github" != request.clientRegistration.registrationId) {
                throw OAuth2AuthenticationException(
                    OAuth2Error(
                        "access_denied",
                        "This OAuth2 method is not supported.",
                        ""
                    )
                )
            }

            // Handle permanent users
            if (
                GitHubAdmins.ids.any { id -> id == user.attributes["id"] } ||
                GitHubAdmins.usernames.any { u -> u == user.attributes["login"] }
            ) {
                return@OAuth2UserService user
            }

            val client = OAuth2AuthorizedClient(request.clientRegistration, user.name, request.accessToken)
            for (organization in GitHubAdmins.organizations) {
                val url = "https://api.github.com/orgs/$organization/members/${user.attributes["login"]}"
                println(url)
                val inOrg: HttpStatus? = rest
                    .get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .attributes(oauth2AuthorizedClient(client))
                    .exchangeToMono { r -> Mono.just(r.statusCode()) }
                    .block()

                if (inOrg == HttpStatus.NO_CONTENT) {
                    return@OAuth2UserService user
                }
            }

            for (team in GitHubAdmins.teams) {
                val url = "https://api.github.com/orgs/${team.x}/teams/${team.y}/memberships/${user.attributes["login"]}"
                println(url)
                val inTeam: HttpStatus? = rest
                    .get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .attributes(oauth2AuthorizedClient(client))
                    .exchangeToMono { r -> Mono.just(r.statusCode()) }
                    .block()

                if (inTeam == HttpStatus.OK) {
                    return@OAuth2UserService user
                }
            }

            // TODO: Handle db users

            throw OAuth2AuthenticationException(
                OAuth2Error(
                    "invalid_token",
                    "Did not match an approved GitHub username, id, or team.",
                    ""
                )
            )
        }
    }
}
