package cn.berberman.demo.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User @JvmOverloads constructor(block: User.() -> Unit = {}) {
	init {
		block()
	}

	@GeneratedValue
	@Id
	var id: Int? = null

	var name: String? = null

	var banana:Array<Any>? =null

	operator fun invoke(block: User.() -> Unit) = apply { block() }
	override fun toString() =
			"User(id=$id, name=$name)"


}