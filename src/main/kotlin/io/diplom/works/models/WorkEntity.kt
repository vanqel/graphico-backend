package io.diplom.works.models

import io.diplom.common.security.models.User
import io.diplom.models.LongEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "work")
class WorkEntity(

    @Column(name = "user_id")
    val userId: Long? = null,

    @Column(name = "title")
    val titleName: String? = null,

    @Column(name = "about", columnDefinition = "text")
    val about: String? = null,

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "work_id", referencedColumnName = "id")
    val images: MutableList<WorkPhotoEntity> = mutableListOf(),

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    var status: Status? = null,


    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    val category: Category? = null,


    @Column(name = "price")
    val price: Double? = null,


    @Column(name = "style")
    val style: String? = null


) : LongEntity() {

    @Transient
    var user: User? = null

    enum class Status(val text: String) {
        DONE("Готово"), IN_WORK("В работе"), CANCEL("Отменён")
    }

    enum class Category(val text: String) {
        `3d-models`("3д-модели"),
        `graphic-design`("Графические рисунки"),
        `logos`("Логотипы"),
        `web-design`("Веб-дизайн"),
        `interior-design`("Дизайн интерьера"),
        `animations`("Анимации"),
        `print-design`("Принт-дизайн"),
    }
}
