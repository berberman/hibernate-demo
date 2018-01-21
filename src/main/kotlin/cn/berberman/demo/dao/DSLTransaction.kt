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
			query { fromThis() }

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

//	infix fun where(map: Map<KMutableProperty1<T, out Any?>, Any>) =
//			apply {
//				stringBuilder.append("where ")
//				fun <K, V> Map<K, V>.forEachIndexed(action: (Int, K, V) -> Unit) {
//					var index = 0
//					for (item in this) action(index++, item.key, item.value)
//				}
//				map.forEachIndexed { index, k, v ->
//					when (v) {
//						!is String -> stringBuilder.append("${entityName.toLowerCase()}.${k.name}=$v ")
//						else       -> stringBuilder.append("${entityName.toLowerCase()}.${k.name} like '%$v%' ")
//					}
//					if (index != map.size - 1) stringBuilder.append("and ")
//				}

	infix fun where(conditions: Conditions<KMutableProperty1<T, out Any?>>) =
			apply {
				stringBuilder.append("where ")

				conditions.forEachIndexed { index, k, v ->
					stringBuilder.append("${entityName.toLowerCase()}.${k.name} $v")
					if (index != conditions.size - 1) stringBuilder.append("and ")
				}
			}

	infix fun select(args: List<KMutableProperty1<T, out Any?>>) {
		TODO("还没想好怎么写")
	}

	fun generate() = stringBuilder.toString()
}


sealed class PotatoExpression {
	override fun toString(): String = when (this) {
		is LikeExpression       -> "like '%$target%' "
		NotExpression           -> "not "
		is InExpression<*>      -> "in(${list.joinToString()}) "
		is BetweenExpression<*> -> "between $a and $b "
		is EqualsExpression     -> "= $string"
	}

	data class LikeExpression(val target: String) : PotatoExpression()
	object NotExpression : PotatoExpression()
	data class InExpression<out T>(val list: List<T>) : PotatoExpression()
	data class BetweenExpression<in T>(val a: Comparable<T>, val b: Comparable<T>) : PotatoExpression()
	data class EqualsExpression(val string: String) : PotatoExpression()


}

class PotatoExpressionBuilder {
	private val holder = StringBuilder()

	private inline fun <T> runReturnBuilder(block: () -> T) = kotlin.run { block();this }


	fun not() = runReturnBuilder { PotatoExpression.NotExpression.let(holder::append) }
	fun <T> between(a: Comparable<T>, b: Comparable<T>) = runReturnBuilder { PotatoExpression.BetweenExpression(a, b).let(holder::append) }
	infix fun <T> `in`(list: List<T>) = runReturnBuilder { PotatoExpression.InExpression(list).let(holder::append) }
	infix fun between(range: IntRange) = runReturnBuilder { PotatoExpression.BetweenExpression(range.first, range.last).let(holder::append) }
	infix fun like(target: String) = runReturnBuilder { PotatoExpression.LikeExpression(target).let(holder::append) }
	infix fun equals(string: String) = runReturnBuilder { PotatoExpression.LikeExpression(string).let(holder::append) }

	override fun toString(): String = holder.toString()

}

fun expression(block: PotatoExpressionBuilder.() -> Unit) = PotatoExpressionBuilder().apply(block)

class Conditions<T> : HashMap<T, PotatoExpressionBuilder>()

//TODO 根据Pair计算Map大小
fun <T> condition(vararg pairs: Pair<T, PotatoExpressionBuilder>) = Conditions<T>().apply { putAll(pairs) }

fun <K, V> Map<K, V>.forEachIndexed(action: (Int, K, V) -> Unit) {
	var index = 0
	for (item in this) action(index++, item.key, item.value)
}