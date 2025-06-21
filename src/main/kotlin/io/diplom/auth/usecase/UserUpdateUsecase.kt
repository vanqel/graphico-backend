package io.diplom.auth.usecase

import io.diplom.auth.dto.inp.UserUpdateInput
import io.diplom.common.security.configurator.getUser
import io.diplom.exception.GeneralException
import io.diplom.outer.images.FileOutput
import io.diplom.outer.images.MinioService
import io.diplom.outer.user.models.UserEntity
import io.diplom.outer.user.models.UserPhotos
import io.diplom.outer.user.repository.UserPhotoRepositoryPanache
import io.diplom.outer.user.repository.UserRepository
import io.diplom.outer.user.repository.UserRepositoryPanache
import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.uni
import io.vertx.ext.web.FileUpload
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class UserUpdateUsecase(
    private val userPhotoRepository: UserPhotoRepositoryPanache,
    private val userRepository: UserRepositoryPanache,
    private val securityIdentity: SecurityIdentity,
    private val fileService: MinioService,
    private val repository: UserRepository

) {
    @WithTransaction
    fun updatePerson(personEntity: UserUpdateInput): Uni<UserEntity> =
        userRepository.findById(personEntity.id)
            .flatMap { pe ->

                val checkUsernameUni = personEntity.username?.let { en ->
                    if (en == pe.username) uni { false }
                    else {
                        repository.checkExistsUsername(en).map {
                            pe.username = en
                            it
                        }
                    }
                } ?: uni { false }

                val checkEmailUni = personEntity.email?.let { en ->
                    if (en == pe.email) uni { false }
                    else {
                        repository.checkExistsEmail(en).map {
                            pe.email = en
                            it
                        }
                    }
                } ?: uni { false }

                Uni.combine().all().unis(checkUsernameUni, checkEmailUni).asTuple().flatMap {

                    if (it.item1) return@flatMap Uni.createFrom()
                        .failure(GeneralException("Логин уже существует"))
                    if (it.item2) return@flatMap Uni.createFrom()
                        .failure(GeneralException("Почта уже существует"))

                    personEntity.password?.let { pe.password = BcryptUtil.bcryptHash(it) }
                    personEntity.name?.let({ pe.name = it })
                    personEntity.about?.let { pe.about = it }
                    personEntity.skills?.let { pe.skills = it }
                    personEntity.directions?.let { pe.directions = it }

                    userRepository.persistAndFlush(pe)
                }
            }


    fun updateAvatarUser(file: FileUpload): Uni<FileOutput> {
        return fileService.addObject(file).call { s ->

//            userRepository.findById(securityIdentity.getUser().id).flatMap {
                val e = UserPhotos(securityIdentity.getUser().id, s.filename)
                userPhotoRepository.persistAndFlush(e)
//            }
        }
    }


}
