package io.diplom.auth.usecase

import io.diplom.auth.dto.inp.UserUpdateInput
import io.diplom.common.security.configurator.getUser
import io.diplom.outer.images.FileOutput
import io.diplom.outer.images.MinioService
import io.diplom.outer.user.models.UserEntity
import io.diplom.outer.user.models.UserPhotos
import io.diplom.outer.user.repository.UserPhotoRepositoryPanache
import io.diplom.outer.user.repository.UserRepositoryPanache
import io.quarkus.elytron.security.common.BcryptUtil
import io.quarkus.hibernate.reactive.panache.common.WithTransaction
import io.quarkus.security.identity.SecurityIdentity
import io.smallrye.mutiny.Uni
import io.vertx.ext.web.FileUpload
import jakarta.enterprise.context.ApplicationScoped

@WithTransaction
@ApplicationScoped
class UserUpdateUsecase(
    private val userRepository: UserRepositoryPanache,
    private val userPhotoRepository: UserPhotoRepositoryPanache,
    private val fileService: MinioService,
    private val securityIdentity: SecurityIdentity
) {
    @WithTransaction
    fun updatePerson(personEntity: UserUpdateInput): Uni<UserEntity> =
        userRepository.findById(personEntity.id)
            .call { pe ->
                personEntity.password?.let { pe.password = BcryptUtil.bcryptHash(it) }
                personEntity.name?.let({ pe.name = it })
                personEntity.about?.let { pe.about = it }
                personEntity.skills?.let { pe.skills = it }
                personEntity.directions?.let { pe.directions = it }
                personEntity.email?.let { pe.email = it }

                userRepository.persistAndFlush(pe)
            }


    fun updateAvatarUser(file: FileUpload): Uni<FileOutput> {
        return fileService.addObject(file).call { s ->
            val e = UserPhotos(securityIdentity.getUser().id, s.filename)
            userPhotoRepository.persistAndFlush(e)
        }
    }


}
