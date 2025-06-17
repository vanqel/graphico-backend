package io.diplom.common.security

import io.diplom.common.security.configurator.SecurityConfiguration
import io.diplom.common.security.filters.DeveloperAuthenticationFilter
import io.diplom.common.security.filters.FilterType
import io.diplom.common.security.filters.JwtAuthenticationFilter
import io.diplom.common.security.models.AuthorityName
import io.quarkus.runtime.Startup
import jakarta.enterprise.context.ApplicationScoped

/**
 * Конфигурация безопасности.
 */
@ApplicationScoped
class WebConfig(
    /** Фильтр аторизации разработчика.*/
    val developerAuthenticationFilter: DeveloperAuthenticationFilter,
    /** Фильтр аторизации пользователя.*/
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

    /**
     * Создание конфигурации безопасности.
     */
    @ApplicationScoped
    @Startup
    fun config(): SecurityConfiguration =
        SecurityConfiguration.Builder

            .addFilter(developerAuthenticationFilter, FilterType.DEVELOPER)
            .addFilter(jwtAuthenticationFilter, FilterType.USER)

            .permitAll("/public/*")
            .permitAll("/auth/*")
            .authorized("/user/*")
            .authorized("/cart/*")
            .authorized("/work/*")
            .authorized("/report/*")
            .authorized("/admin-api/*", roles = listOf(AuthorityName.ADMIN.name))
            .anyRequestPermitAll()
            .build()
}
