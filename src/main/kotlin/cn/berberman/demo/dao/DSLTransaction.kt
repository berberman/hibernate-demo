package cn.berberman.demo.dao

import org.hibernate.Session
import kotlin.reflect.KMutableProperty1


inline fun session(block: SessionScope.() -> Unit) = SessionScope().block()

@Suppress("UNCHECKED_CAST")
class SessionScope {
	fun transaction(block: Session.() -> Unit) {
		val session = DB.instance.sessionFactory.openSession()
		session.beginTransaction()
		session.block()
		session.close()
	}

	fun Session.commit() = transaction.commit()

	inline operator fun <reified T> Session.get(id: Int): T = find(T::class.java, id)
	inline fun <reified T> Session.findAll(): List<T> =
			query { from() }

	inline fun <reified T> Session.query(block: HQLQueryStringBuilder<T>.() -> Unit): List<T> {
		val queryString = HQLQueryStringBuilder<T>(T::class.java).apply { block() }.generate()
		println(queryString)
		return createQuery(queryString).list() as List<T>
	}

}

class HQLQueryStringBuilder<T>(entity: Class<*>) {
	private val entityName: String = entity.simpleName
	private val stringBuilder = StringBuilder()
	fun fromThis() = apply {
		stringBuilder.append("from $entityName ${entityName.toLowerCase()} ")
	}

	infix fun where(map: Map<KMutableProperty1<T, out Any?>, Any>) =
			apply {
				stringBuilder.append("where ")
				fun <K, V> Map<K, V>.forEachIndexed(action: (Int, K, V) -> Unit) {
					var index = 0
					for (item in this) action(index++, item.key, item.value)
				}
				map.forEachIndexed { index, k, v ->
					when (v) {
						!is String -> stringBuilder.append("${entityName.toLowerCase()}.${k.name}=$v ")
						else       -> stringBuilder.append("${entityName.toLowerCase()}.${k.name} like '%$v%' ")
					}
					if (index != map.size - 1) stringBuilder.append("and ")
				}
			}

	fun generate() = stringBuilder.toString()
}
