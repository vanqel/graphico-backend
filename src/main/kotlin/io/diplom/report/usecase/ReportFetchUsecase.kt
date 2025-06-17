package io.diplom.report.usecase

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entities.entity
import io.diplom.auth.usecase.UserFetchUsecase
import io.diplom.common.security.configurator.getUser
import io.diplom.common.security.models.User
import io.diplom.config.jpql.JpqlEntityManager
import io.diplom.config.jpql.PaginationInput
import io.diplom.report.dto.out.ReportOutput
import io.diplom.report.model.ReportEntity
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class ReportFetchUsecase(
    private val securityIdentity: SecurityIdentity,
    private val jpqlEntityManager: JpqlEntityManager,
    private val userFetchUsecase: UserFetchUsecase
) {
    companion object {
        val entity = entity(ReportEntity::class)
    }


    @Deprecated("дедлайн был ночь, я такое осуждаю.. стыдно")
    fun findForWork(workId: Long): Uni<List<ReportOutput>> {

        val query = jpql {
            selectDistinct(entity)
                .from(entity)
                .whereAnd(
                    entity.path(ReportEntity::userId)
                        .eq(securityIdentity.getUser().id),

                    entity.path(ReportEntity::workId)
                        .eq(workId)
                )
        }

        return jpqlEntityManager.JpqlQuery()
            .getResultData(query, PaginationInput(0, 20))
            .flatMap(this::wrap)
    }


    fun wrap(e: ReportEntity): Uni<ReportOutput> =
        userFetchUsecase.findById(e.userId!!).map { u ->
            ReportOutput(
                e.id!!,
                u,
                e.workId!!,
                e.text!!
            )
        }


    fun wrap(e: List<ReportEntity>): Uni<List<ReportOutput>> =
        userFetchUsecase.findByIds(e.map { it.userId!! })
            .map { u ->
                val userMap = u.associateBy(User::id)

                e.groupBy(ReportEntity::userId).flatMap { (userId, list) ->
                    val user = userMap.getValue(userId!!)

                    list.map { reportEntity ->
                        ReportOutput(
                            reportEntity.id!!,
                            user,
                            reportEntity.workId!!,
                            reportEntity.text!!
                        )
                    }
                }

            }


}
