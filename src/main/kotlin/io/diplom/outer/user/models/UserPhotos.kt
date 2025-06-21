package io.diplom.outer.user.models

import com.fasterxml.jackson.annotation.JsonIgnore
import io.diplom.models.LongEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_photos")
class UserPhotos(

    @Column(name = "uid_id")
    val userId: Long? = null,

    @Column(name = "filename")
    val filename: String? = null

) : LongEntity() {

    @Transient
    var uri: String? = null
}
